package gg.solarmc.kitpvp.commands;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import gg.solarmc.command.CommandIterator;
import gg.solarmc.kitpvp.config.Bounties;
import gg.solarmc.kitpvp.config.ConfigCenter;
import gg.solarmc.kitpvp.handler.BountyManager;
import gg.solarmc.kitpvp.misc.Formatter;
import gg.solarmc.kitpvp.misc.FuturePoster;
import gg.solarmc.loader.DataCenter;
import gg.solarmc.loader.kitpvp.Bounty;
import gg.solarmc.loader.kitpvp.BountyCurrency;
import gg.solarmc.loader.kitpvp.BountyListOrder;
import gg.solarmc.loader.kitpvp.BountyPage;
import gg.solarmc.loader.kitpvp.KitPvpKey;
import jakarta.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import space.arim.omnibus.util.UUIDUtil;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BountyCommand extends BaseCommand {

    private final DataCenter dataCenter;
    private final FuturePoster futurePoster;
    private final BountyManager bountyManager;
    private final Formatter formatter;
    private final Server server;

    private final Cache<UUID, BountyPage> bountyPages = Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(5L)).build();

    @Inject
    public BountyCommand(ConfigCenter configCenter, DataCenter dataCenter,
                         FuturePoster futurePoster, BountyManager bountyManager,
                         Formatter formatter, Server server) {
        super("bounty", configCenter);
        this.dataCenter = dataCenter;
        this.futurePoster = futurePoster;
        this.bountyManager = bountyManager;
        this.formatter = formatter;
        this.server = server;
    }

    @Override
    public void permission(StringBuilder builder) {
        builder.append("bounty");
    }

    private Bounties bounties() {
        return config().bounties();
    }

    private Bounties.Commands commands() {
        return bounties().commands();
    }

    @Override
    public void execute(CommandSender sender, CommandIterator command) {
        if (!command.hasNext()) {
            sender.sendMessage(commands().usage());
            return;
        }
        Execution execution = new Execution(sender, command);
        boolean view;
        switch (command.next().toLowerCase(Locale.ROOT)) {
        case "add" -> view = false;
        case "view" -> view = true;
        case "list" -> {
            execution.showBountyList();
            return;
        }
        default -> {
            sender.sendMessage(commands().usage());
            return;
        }
        }
        if (!command.hasNext()) {
            sender.sendMessage((view) ? commands().usageView() : commands().usageAdd());
            return;
        }
        String playerArgument = command.next();
        Player target = server.getPlayer(playerArgument);
        if (target == null) {
            sender.sendMessage(config().messages().playerNotFound().replaceText("%ARGUMENT%", playerArgument));
            return;
        }
        if (view) {
            execution.showView(target);
        } else {
            execution.placeBounty(target);
        }
    }

    private final class Execution {

        private final CommandSender sender;
        private final CommandIterator command;

        private Execution(CommandSender sender, CommandIterator command) {
            this.sender = sender;
            this.command = command;
        }

        Optional<BountyCurrency> parseCurrency(String currencyArg) {
            BountyCurrency currency = null;
            for (BountyCurrency possibleCurrency : BountyCurrency.values()) {
                var acceptedArgs = bounties().currencyDisplay().currencyArgument().get(possibleCurrency);
                if (acceptedArgs == null) {
                    continue;
                }
                if (acceptedArgs.acceptedArguments().contains(currencyArg)) {
                    currency = possibleCurrency;
                    break;
                }
            }
            return Optional.ofNullable(currency);
        }

        private void showView(Player target) {
            String targetName = target.getName();
            futurePoster.postFuture(dataCenter.transact((tx) -> {
                Map<BountyCurrency, BigDecimal> bounties = new EnumMap<>(BountyCurrency.class);
                for (BountyCurrency currency : BountyCurrency.values()) {
                    bounties.put(currency,
                            target.getSolarPlayer().getData(KitPvpKey.INSTANCE).getBounty(tx, currency).value());
                }
                return bounties;
            }).thenAccept((bounties) -> {
                sender.sendMessage(commands().viewMessage()
                        .replaceText("%TARGET%", targetName)
                        .asComponent()
                        .replaceText(formatter.formatBounties("BOUNTY_VALUE", bounties)));
            }));
        }

        private void placeBounty(Player target) {
            BountyCurrency currency;
            {
                if (!command.hasNext()) {
                    sender.sendMessage(commands().usageAdd());
                    return;
                }
                String currencyArg = command.next();
                Optional<BountyCurrency> optCurrency = parseCurrency(currencyArg);
                if (optCurrency.isEmpty()) {
                    sender.sendMessage(commands().notACurrency().replaceText("%ARGUMENT%", currencyArg));
                    return;
                }
                currency = optCurrency.get();
            }
            if (!command.hasNext()) {
                sender.sendMessage(commands().usageAdd());
                return;
            }
            String amountArgument = command.next();
            BigDecimal amount;
            try {
                amount = BigDecimal.valueOf(Double.parseDouble(amountArgument));
            } catch (NumberFormatException ex) {
                sender.sendMessage(commands().addNotANumber().replaceText("%ARGUMENT%", amountArgument));
                return;
            }
            if (!(sender instanceof Player malefactor)) {
                sender.sendMessage(Component.text("You are not a player"));
                return;
            }
            futurePoster.postFuture(
                    bountyManager.placeBounty(target, malefactor, currency.createAmount(amount)));
        }

        private CentralisedFuture<Optional<BountyPage>> fetchFirstBountyPage() {
            int perPage = commands().bountiesPerPage();
            BountyListOrder.Built listOrder = BountyListOrder
                    .countPerPage(perPage)
                    .includeCurrencies(List.of(BountyCurrency.values()))
                    .build();
            return dataCenter.transact(
                    (tx) -> dataCenter.getDataManager(KitPvpKey.INSTANCE).listBounties(tx, listOrder));
        }

        private void showBountyList() {
            CentralisedFuture<Optional<BountyPage>> futurePageToShow;

            findExistingPage:
            if (command.hasNext()) {
                String pageCode = command.next();
                UUID previousPageId;
                try {
                    previousPageId = UUIDUtil.fromShortString(pageCode);
                } catch (IllegalArgumentException ignored) {
                    futurePageToShow = fetchFirstBountyPage();
                    break findExistingPage;
                }
                BountyPage previousPage = bountyPages.getIfPresent(previousPageId);
                if (previousPage == null) {
                    sender.sendMessage(commands().bountyListInvalidPagecode()
                            .replaceText("%ARGUMENT%", pageCode));
                    return;
                }
                futurePageToShow = dataCenter.transact(previousPage::nextPage);
            } else {
                futurePageToShow = fetchFirstBountyPage();
            }
            futurePoster.postFuture(futurePageToShow.thenAccept((optPageToShow) -> {
                if (optPageToShow.isEmpty()) {
                    sender.sendMessage(commands().bountyListNoPages());
                    return;
                }
                BountyPage pageToShow = optPageToShow.get();
                UUID pageId = UUID.randomUUID();
                var textBuilder = Component.text();
                for (Bounty bounty : pageToShow.itemsOnPage()) {
                    textBuilder.append(commands().bountyListBounty()
                            .replaceText("%BOUNTY_TARGET%", bounty.target())
                            .asComponent()
                            .replaceText(formatter.formatBounties("BOUNTY_VALUE", bounty.allAmounts())));
                    textBuilder.append(Component.newline());
                }
                ComponentLike nextPage;
                if (pageToShow.itemsOnPage().size() == commands().bountiesPerPage()) {
                    // It is likely that there is another page
                    nextPage = commands().bountyListNextPage()
                            .replaceText("%NEXT_PAGE_CODE%", UUIDUtil.toShortString(pageId));
                } else {
                    nextPage = Component.empty();
                }
                sender.sendMessage(commands().bountyListPage()
                        .asComponent()
                        .replaceText((config) -> {
                            config.matchLiteral("%BOUNTIES_ON_PAGE%").replacement(textBuilder);
                        }).replaceText((config) -> {
                            config.matchLiteral("%NEXT_PAGE%").replacement(nextPage);
                        }));
                bountyPages.put(pageId, pageToShow);
            }));
        }
    }

}
