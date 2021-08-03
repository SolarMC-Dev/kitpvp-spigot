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

import gg.solarmc.kitpvp.config.Config;
import gg.solarmc.kitpvp.config.ConfigCenter;
import gg.solarmc.loader.DataCenter;
import gg.solarmc.loader.Transaction;
import gg.solarmc.loader.kitpvp.KitPvpKey;
import jakarta.inject.Inject;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.math.BigDecimal;

public class BountyManager {

    private final DataCenter dataCenter;
    private final ConfigCenter configCenter;
    private final BankAccess bankAccess;

    @Inject
    public BountyManager(DataCenter dataCenter, ConfigCenter configCenter, BankAccess bankAccess) {
        this.dataCenter = dataCenter;
        this.configCenter = configCenter;
        this.bankAccess = bankAccess;
    }

    private Config config() {
        return configCenter.config();
    }

    private Config.Bounties bounties() {
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
    public CentralisedFuture<?> placeBounty(Player target, Player malefactor, int amount) {

        record BountyPlacement(boolean success, BigDecimal availableFunds, int newBounty) { }

        return dataCenter.transact((tx) -> {
            BankAccess.WithdrawResult withdrawResult = bankAccess.withdrawBalance(tx, malefactor, BigDecimal.valueOf(amount));
            if (!withdrawResult.isSuccessful()) {
                return new BountyPlacement(false, withdrawResult.newBalance(), 0 /* doesn't matter */);
            }
            int newBounty = target.getSolarPlayer().getData(KitPvpKey.INSTANCE).addBounty(tx, amount);
            return new BountyPlacement(true, withdrawResult.newBalance(), newBounty);
        }).thenAcceptSync((bountyPlacement) -> {
            if (!bountyPlacement.success()) {
                malefactor.sendMessage(bounties().notEnoughFunds()
                        .replaceText("%BOUNTY_ADDED%", Integer.toString(amount))
                        .replaceText("%BOUNTY_TARGET%", target.getName())
                        .replaceText("%AVAILABLE_FUNDS%", bountyPlacement.availableFunds().toPlainString()));
                return;
            }
            malefactor.sendMessage(bounties().placedBounty()
                    .replaceText("%BOUNTY_ADDED%", Integer.toString(amount))
                    .replaceText("%BOUNTY_TARGET%", target.getName())
                    .replaceText("%BOUNTY_NEW%", Integer.toString(bountyPlacement.newBounty())));
            malefactor.getServer().sendMessage(bounties().placedBountyBroadcast()
                    .replaceText("%BOUNTY_ADDED%", Integer.toString(amount))
                    .replaceText("%BOUNTY_TARGET%", target.getName())
                    .replaceText("%BOUNTY_NEW%", Integer.toString(bountyPlacement.newBounty()))
                    .replaceText("%BOUNTY_MALEFACTOR%", malefactor.getName()));
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
        Grant explicit and implicit bounties
         */

        BigDecimal explicitBounty = BigDecimal.valueOf(
                victim.getSolarPlayer().getData(KitPvpKey.INSTANCE).resetBounty(tx));
        BigDecimal implicitBounty = bounties().implicitBounties().killstreakImplicitBounties()
                .findValue(victimLostKillstreak).orElse(BigDecimal.ZERO);

        BigDecimal totalBounty = explicitBounty.add(implicitBounty);
        if (!totalBounty.equals(BigDecimal.ZERO)) {
            bankAccess.depositBalance(tx, killer, totalBounty);
            kill.bountyReward(totalBounty, explicitBounty, implicitBounty);

            if (!implicitBounty.equals(BigDecimal.ZERO) &&
                    bounties().implicitBounties().victimReceivesImplicitBounty()) {
                bankAccess.depositBalance(tx, victim, implicitBounty);
                kill.implicitBountyReward(implicitBounty, victimLostKillstreak);
            }
        }
        return kill;
    }

    final class BountyKill extends Kill {

        BountyKill(Player killer, Player victim) {
            super(killer, victim);
        }

        void bountyReward(BigDecimal totalBounty, BigDecimal explicitBounty, BigDecimal implicitBounty) {
            addCallback(() -> {
                Component claimedBounty = bounties().claimedBounty()
                        .replaceText("%BOUNTY_VALUE%", totalBounty.toPlainString())
                        .replaceText("%BOUNTY_VALUE_EXPLICIT%", explicitBounty.toPlainString())
                        .replaceText("%BOUNTY_VALUE_IMPLICIT%", implicitBounty.toPlainString())
                        .replaceText("%BOUNTY_TARGET%", victimName())
                        .asComponent();
                sendMessageIfNotEmpty(killer(), claimedBounty);

                Component claimedBountyBroadcast = bounties().claimedBountyBroadcast()
                        .replaceText("%BOUNTY_VALUE%", totalBounty.toPlainString())
                        .replaceText("%BOUNTY_VALUE_EXPLICIT%", explicitBounty.toPlainString())
                        .replaceText("%BOUNTY_VALUE_IMPLICIT%", implicitBounty.toPlainString())
                        .replaceText("%BOUNTY_TARGET%", victimName())
                        .replaceText("%BOUNTY_CLAIMANT%", killerName())
                        .asComponent();
                sendMessageIfNotEmpty(broadcast(), claimedBountyBroadcast);
            });
        }

        void implicitBountyReward(BigDecimal implicitBounty, int victimLostKillstreak) {
            addCallback(() -> {
                if (bounties().implicitBounties().victimReceivesImplicitBounty()) {
                    sendMessageIfNotEmpty(victim(), bounties().implicitBounties()
                            .implicitBountyMessageVictim()
                            .replaceText("%IMPLICIT_BOUNTY%", implicitBounty.toPlainString())
                            .replaceText("%KILLER%", killerName())
                            .replaceText("%VICTIM_KILLSTREAK%", Integer.toString(victimLostKillstreak)));
                }
                sendMessageIfNotEmpty(killer(), bounties().implicitBounties()
                        .implicitBountyMessageKiller()
                        .replaceText("%IMPLICIT_BOUNTY%", implicitBounty.toPlainString())
                        .replaceText("%VICTIM%", victimName())
                        .replaceText("%VICTIM_KILLSTREAK%", Integer.toString(victimLostKillstreak)));
            });
        }
    }
}
