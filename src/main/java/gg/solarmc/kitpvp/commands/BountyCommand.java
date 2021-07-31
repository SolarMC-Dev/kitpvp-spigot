package gg.solarmc.kitpvp.commands;

import gg.solarmc.kitpvp.config.Config;
import gg.solarmc.kitpvp.config.ConfigCenter;
import gg.solarmc.kitpvp.handler.BountyManager;
import gg.solarmc.kitpvp.misc.FuturePoster;
import gg.solarmc.loader.DataCenter;
import gg.solarmc.loader.kitpvp.KitPvpKey;
import jakarta.inject.Inject;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public class BountyCommand extends BaseCommand {

    private final DataCenter dataCenter;
    private final FuturePoster futurePoster;
    private final BountyManager bountyManager;
    private final Server server;

    @Inject
    public BountyCommand(ConfigCenter configCenter, DataCenter dataCenter,
                         FuturePoster futurePoster, BountyManager bountyManager, Server server) {
        super("bounty", configCenter);
        this.dataCenter = dataCenter;
        this.futurePoster = futurePoster;
        this.bountyManager = bountyManager;
        this.server = server;
    }

    @Override
    public void permission(StringBuilder builder) {
        builder.append("bounty");
    }

    private Config.Bounties.Commands commands() {
        return config().bounties().commands();
    }

    @Override
    public void execute(CommandSender sender, CommandIterator command) {
        if (!command.hasNext()) {
            sender.sendMessage(commands().usage());
            return;
        }
        boolean view;
        switch (command.next().toLowerCase(Locale.ROOT)) {
            case "add" -> view = false;
            case "view" -> view = true;
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
            sender.sendMessage(commands().playerNotFound().replaceText("%ARGUMENT%", playerArgument));
            return;
        }
        String targetName = target.getName();
        if (view) {
            futurePoster.postFuture(dataCenter.transact((tx) -> {
                return target.getSolarPlayer().getData(KitPvpKey.INSTANCE).getBounty(tx);
            }).thenAccept((bounty) -> {
                if (bounty == 0) {
                    sender.sendMessage(commands().viewMessageNoBounty().replaceText("%TARGET%", targetName));
                    return;
                }
                sender.sendMessage(commands().viewMessage()
                        .replaceText("%TARGET%", targetName)
                        .replaceText("%BOUNTY_VALUE%", Integer.toString(bounty)));
            }));
        } else {
            if (!command.hasNext()) {
                sender.sendMessage(commands().usageAdd());
                return;
            }
            String amountArgument = command.next();
            int amount;
            try {
                amount = Integer.parseInt(amountArgument);
            } catch (NumberFormatException ex) {
                sender.sendMessage(commands().addNotANumber().replaceText("%ARGUMENT%", amountArgument));
                return;
            }
            if (!(sender instanceof Player seeker)) {
                sender.sendMessage(Component.text("You are not a player"));
                return;
            }
            futurePoster.postFuture(bountyManager.placeBounty(target, seeker, amount));
        }
    }

}
