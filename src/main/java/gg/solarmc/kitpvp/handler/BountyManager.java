/*
 * kitpvp
 * Copyright © 2021 SolarMC Developers
 *
 * kitpvp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * kitpvp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with kitpvp. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Affero General Public License.
 */

package gg.solarmc.kitpvp.handler;

import gg.solarmc.kitpvp.config.Bounties;
import gg.solarmc.kitpvp.config.Config;
import gg.solarmc.kitpvp.config.ConfigCenter;
import gg.solarmc.kitpvp.config.RangedLookupTable;
import gg.solarmc.kitpvp.handler.banking.Bank;
import gg.solarmc.kitpvp.handler.banking.BankAccess;
import gg.solarmc.kitpvp.misc.Formatter;
import gg.solarmc.loader.DataCenter;
import gg.solarmc.loader.Transaction;
import gg.solarmc.loader.kitpvp.BountyAmount;
import gg.solarmc.loader.kitpvp.BountyCurrency;
import gg.solarmc.loader.kitpvp.KitPvpKey;
import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.math.BigDecimal;

public class BountyManager {

    private final DataCenter dataCenter;
    private final ConfigCenter configCenter;
    private final Formatter formatter;
    private final BankAccess bankAccess;

    @Inject
    public BountyManager(DataCenter dataCenter, ConfigCenter configCenter,
                         Formatter formatter, BankAccess bankAccess) {
        this.dataCenter = dataCenter;
        this.configCenter = configCenter;
        this.formatter = formatter;
        this.bankAccess = bankAccess;
    }

    private Config config() {
        return configCenter.config();
    }

    private Bounties bounties() {
        return config().bounties();
    }

    /**
     * Attempts to place a bounty
     *
     * @param target the target of the bounty
     * @param malefactor the one placing the bounty
     * @param amount the bounty amount
     * @return a future of the placement
     */
    public CentralisedFuture<?> placeBounty(Player target, Player malefactor, BountyAmount amount) {

        record BountyPlacement(boolean success, BountyAmount availableFunds, BountyAmount newBounty) { }

        BountyCurrency currency = amount.currency();
        Bank bank = bankAccess.findBank(currency);

        String targetName = target.getName();
        String malefactorName = malefactor.getName();

        return dataCenter.transact((tx) -> {
            Bank.WithdrawResult withdrawResult = bank.withdrawBalance(tx, malefactor, amount.value());
            BountyAmount availableFunds = currency.createAmount(withdrawResult.newBalance());
            if (!withdrawResult.isSuccessful()) {
                return new BountyPlacement(false, availableFunds, null /* doesn't matter */);
            }
            BountyAmount newBounty = target.getSolarPlayer().getData(KitPvpKey.INSTANCE).addBounty(tx, amount);
            return new BountyPlacement(true, availableFunds, newBounty);

        }).thenAccept((bountyPlacement) -> {
            // Careful, not on the main thread
            CharSequence formattedCurrency = formatter.formatCurrency(currency);
            if (!bountyPlacement.success()) {
                malefactor.sendMessage(bounties().notEnoughFunds()
                        .replaceText("%BOUNTY_CURRENCY%", formattedCurrency)
                        .replaceText("%BOUNTY_TARGET%", targetName)
                        .asComponent()
                        .replaceText(formatter.formatBounty("%AVAILABLE_FUNDS%", bountyPlacement.availableFunds()))
                        .replaceText(formatter.formatBounty("%BOUNTY_ADDED%", amount))
                );
                return;
            }
            malefactor.sendMessage(bounties().placedBounty()
                    .replaceText("%BOUNTY_CURRENCY%", formattedCurrency)
                    .replaceText("%BOUNTY_TARGET%", targetName)
                    .asComponent()
                    .replaceText(formatter.formatBounty("%BOUNTY_ADDED%", amount))
                    .replaceText(formatter.formatBounty("%BOUNTY_NEW%", bountyPlacement.newBounty()))
            );
            malefactor.getServer().sendMessage(bounties().placedBountyBroadcast()
                    .replaceText("%BOUNTY_CURRENCY%", formattedCurrency)
                    .replaceText("%BOUNTY_TARGET%", targetName)
                    .replaceText("%BOUNTY_MALEFACTOR%", malefactorName)
                    .asComponent()
                    .replaceText(formatter.formatBounty("%BOUNTY_ADDED%", amount))
                    .replaceText(formatter.formatBounty("%BOUNTY_NEW%", bountyPlacement.newBounty()))
            );
        });
    }

    /**
     * Claims any available bounty
     *
     * @param tx the transaction
     * @param victim the dead player
     * @param killer the killer
     * @param victimLostKillstreak the victim's killstreak which was lost
     * @return a future of the bounty claim
     */
    Kill claimBounty(Transaction tx, Player victim, Player killer, int victimLostKillstreak) {
        BountyKill kill = new BountyKill(killer, victim);

        /*
        Grant explicit and implicit bounties for all currencies
         */

        for (BountyCurrency currency : BountyCurrency.values()) {
            Bank bank = bankAccess.findBank(currency);
            BountyAmount explicitBounty = victim.getSolarPlayer().getData(KitPvpKey.INSTANCE)
                    .resetBounty(tx, currency);
            BountyAmount implicitBounty;
            {
                RangedLookupTable<BigDecimal> lookupTable = bounties()
                        .implicitBounties().killstreakImplicitBountiesPerCurrency().get(currency);
                if (lookupTable == null) {
                    implicitBounty = currency.zero();
                } else {
                    implicitBounty = currency.createAmount(
                            lookupTable.findValue(victimLostKillstreak).orElse(BigDecimal.ZERO));
                }
            }
            BountyAmount totalBounty = explicitBounty.add(implicitBounty);
            if (totalBounty.isNonZero()) {
                bank.depositBalance(tx, killer, totalBounty.value());
                kill.bountyReward(totalBounty, explicitBounty, implicitBounty);

                if (implicitBounty.isNonZero() &&
                        bounties().implicitBounties().victimReceivesImplicitBounty()) {
                    bank.depositBalance(tx, victim, implicitBounty.value());
                    kill.implicitBountyReward(implicitBounty, victimLostKillstreak);
                }
            }
        }
        return kill;
    }

    final class BountyKill extends Kill {

        BountyKill(Player killer, Player victim) {
            super(killer, victim);
        }

        void bountyReward(BountyAmount totalBounty, BountyAmount explicitBounty, BountyAmount implicitBounty) {
            addCallback(() -> {
                CharSequence formattedCurrency = formatter.formatCurrency(implicitBounty.currency());
                sendMessageIfNotEmpty(killer(), bounties().claimedBounty()
                        .replaceText("%BOUNTY_CURRENCY%", formattedCurrency)
                        .replaceText("%BOUNTY_TARGET%", victimName())
                        .asComponent()
                        .replaceText(formatter.formatBounty("%BOUNTY_VALUE%", totalBounty))
                        .replaceText(formatter.formatBounty("%BOUNTY_VALUE_EXPLICIT%", explicitBounty))
                        .replaceText(formatter.formatBounty("%BOUNTY_VALUE_IMPLICIT%", implicitBounty))
                );
                sendMessageIfNotEmpty(broadcast(), bounties().claimedBountyBroadcast()
                        .replaceText("%BOUNTY_CURRENCY%", formattedCurrency)
                        .replaceText("%BOUNTY_TARGET%", victimName())
                        .replaceText("%BOUNTY_CLAIMANT%", killerName())
                        .asComponent()
                        .replaceText(formatter.formatBounty("%BOUNTY_VALUE%", totalBounty))
                        .replaceText(formatter.formatBounty("%BOUNTY_VALUE_EXPLICIT%", explicitBounty))
                        .replaceText(formatter.formatBounty("%BOUNTY_VALUE_IMPLICIT%", implicitBounty)));
            });
        }

        void implicitBountyReward(BountyAmount implicitBounty, int victimLostKillstreak) {
            addCallback(() -> {
                CharSequence formattedCurrency = formatter.formatCurrency(implicitBounty.currency());
                if (bounties().implicitBounties().victimReceivesImplicitBounty()) {
                    sendMessageIfNotEmpty(victim(), bounties().implicitBounties()
                            .implicitBountyMessageVictim()
                            .replaceText("%BOUNTY_CURRENCY%", formattedCurrency)
                            .replaceText("%KILLER%", killerName())
                            .replaceText("%VICTIM_KILLSTREAK%", Integer.toString(victimLostKillstreak))
                            .asComponent()
                            .replaceText(formatter.formatBounty("%IMPLICIT_BOUNTY%", implicitBounty))
                    );
                }
                sendMessageIfNotEmpty(killer(), bounties().implicitBounties()
                        .implicitBountyMessageKiller()
                        .replaceText("%BOUNTY_CURRENCY%", formattedCurrency)
                        .replaceText("%VICTIM%", victimName())
                        .replaceText("%VICTIM_KILLSTREAK%", Integer.toString(victimLostKillstreak))
                        .asComponent()
                        .replaceText(formatter.formatBounty("%IMPLICIT_BOUNTY%", implicitBounty))
                );
            });
        }
    }
}
