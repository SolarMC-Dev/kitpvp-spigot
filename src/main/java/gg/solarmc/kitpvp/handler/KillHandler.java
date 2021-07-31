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
import gg.solarmc.kitpvp.config.Violence;
import gg.solarmc.kitpvp.misc.FuturePoster;
import gg.solarmc.loader.DataCenter;
import gg.solarmc.loader.Transaction;
import gg.solarmc.loader.credits.CreditsKey;
import gg.solarmc.loader.credits.DepositResult;
import gg.solarmc.loader.kitpvp.KitPvp;
import gg.solarmc.loader.kitpvp.KitPvpKey;
import gg.solarmc.loader.kitpvp.StatisticResult;
import jakarta.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

public class KillHandler {

    private final DataCenter dataCenter;
    private final FuturePoster futurePoster;
    private final ConfigCenter configCenter;
    private final BountyManager bountyManager;
    private final LevelCalculator levelCalculator;

    @Inject
    public KillHandler(DataCenter dataCenter, FuturePoster futurePoster,
                       ConfigCenter configCenter, BountyManager bountyManager, LevelCalculator levelCalculator) {
        this.dataCenter = dataCenter;
        this.futurePoster = futurePoster;
        this.configCenter = configCenter;
        this.bountyManager = bountyManager;
        this.levelCalculator = levelCalculator;
    }

    private Config config() {
        return configCenter.config();
    }

    private Violence violence() {
        return config().violence();
    }

    public void onKill(Player victim, Player killer, Set<Player> assistants) {
        Objects.requireNonNull(assistants, "assistants");

        futurePoster.postFuture(dataCenter.transact((tx) -> {
            return updateStatisticsAndGrantRewards(tx, victim, killer, assistants);
        }).thenApply((killCallback) -> {
            killCallback.postTransactCallback();
            return killCallback;
        }).thenAcceptSync(KillCallback::mainThreadCallback));
    }

    private KillCallback updateStatisticsAndGrantRewards(
            Transaction tx, Player victim, Player killer, Set<Player> assistants) {
        ViolentKill kill = new ViolentKill(killer, victim);

        KitPvp killerData = killer.getSolarPlayer().getData(KitPvpKey.INSTANCE);
        KitPvp victimData = victim.getSolarPlayer().getData(KitPvpKey.INSTANCE);

        /*
         * Checklist:
         * Update killstreak for killer
         * Update experience for killer
         * Reset killstreak for victim
         * Increment killer kills and victim deaths
         * Reward each assistant and increment their assists
         * Reward the killer directly
         * Reward the killer and the victim with implicit killstreak-based bounties
         */

        // Update killer killstreak
        int killerNewKillstreak = killerData.addKillstreaks(tx, 1).newValue();
        {
            kill.newKillerKillstreakReached(killerNewKillstreak);
        }

        // Update killer experience
        {
            StatisticResult addExperienceResult = killerData.addExperience(tx, violence().experiencePerKill());
            kill.levelUpKiller(addExperienceResult);
        }

        // Reset victim killstreak and send message
        int victimLostKillstreak = victimData.resetCurrentKillstreaks(tx);
        {
            kill.previousKillstreakLost(victimLostKillstreak);
        }

        // Update statistics and add assist rewards
        {
            killerData.addKills(tx, 1);
            victimData.addDeaths(tx, 1);
            BigDecimal rewardPerAssist = violence().killRewards().assistReward();
            for (Player assistant : assistants) {
                assistant.getSolarPlayer().getData(KitPvpKey.INSTANCE).addAssists(tx, 1);
                DepositResult depositResult = assistant.getSolarPlayer().getData(CreditsKey.INSTANCE)
                        .depositBalance(tx, rewardPerAssist);
                kill.assistedKillReward(assistant, rewardPerAssist, depositResult);
            }
        }

        // Main kill reward
        {
            BigDecimal killReward = violence().killRewards().killReward()
                    .findValue(killerNewKillstreak).orElse(BigDecimal.ZERO);
            DepositResult depositResult = killer.getSolarPlayer().getData(CreditsKey.INSTANCE)
                    .depositBalance(tx, killReward);
            kill.mainKillReward(killReward, depositResult);
        }

        Kill bountyKill = bountyManager.claimBounty(tx, victim, killer, victimLostKillstreak);
        return new KillCallback.Combined(kill, bountyKill);
    }

    final class ViolentKill extends Kill {

        ViolentKill(Player killer, Player victim) {
            super(killer, victim);
        }

        private void levelUpKiller(StatisticResult addExperienceResult) {
            int newLevel = levelCalculator.calculateLevel(addExperienceResult.newValue());
            int oldLevel = levelCalculator.calculateLevel(addExperienceResult.oldValue());
            if (oldLevel == newLevel) {
                return;
            }
            addCallback(() -> {
                Component levelUpMessage = config().levels().levelUpMessage()
                        .asComponent()
                        .replaceText((builder) -> {
                            builder.matchLiteral("%OLD_LEVEL%").replacement(levelCalculator.formatLevel(oldLevel));
                        })
                        .replaceText((builder) -> {
                            builder.matchLiteral("%NEW_LEVEL%").replacement(levelCalculator.formatLevel(newLevel));
                        });
                sendMessageIfNotEmpty(killer(), levelUpMessage);
            });
            addSyncCallback(() -> config().levels().levelUpSound().playTo(killer()));
        }

        private void newKillerKillstreakReached(int newKillstreak) {
            addCallback(() -> {
                ComponentLike gainedKillstreakMessage = violence().gainedKillstreakMessage()
                        .replaceText("%NEW_KILLSTREAK%", Integer.toString(newKillstreak));
                sendMessageIfNotEmpty(killer(), gainedKillstreakMessage);
            });
        }

        private void previousKillstreakLost(int victimLostKillstreak) {
            if (victimLostKillstreak == 0) {
                return;
            }
            addCallback(() -> {   
                ComponentLike lostKillstreakMessage = violence().lostKillstreakMessage()
                        .replaceText("%PREVIOUS_KILLSTREAK%", Integer.toString(victimLostKillstreak));
                sendMessageIfNotEmpty(victim(), lostKillstreakMessage);
            });
        }

        private void assistedKillReward(Player assistant,
                                        BigDecimal assistReward, DepositResult depositResult) {
            addCallback(() -> {
                ComponentLike rewardMessage = violence().killRewards().assistedKillMessage()
                        .replaceText("%ASSIST_REWARD%", assistReward.toPlainString())
                        .replaceText("%VICTIM%", victimName())
                        .replaceText("%NEW_BALANCE%", depositResult.newBalance().toPlainString());
                sendMessageIfNotEmpty(assistant, rewardMessage);
            });
        }

        private void mainKillReward(BigDecimal killReward, DepositResult depositResult) {
            addCallback(() -> {
                ComponentLike rewardMessage = violence().killRewards().rewardedKillMessage()
                        .replaceText("%KILL_REWARD%", killReward.toPlainString())
                        .replaceText("%VICTIM%", victimName())
                        .replaceText("%NEW_BALANCE%", depositResult.newBalance().toPlainString());
                sendMessageIfNotEmpty(killer(), rewardMessage);
            });
        }

    }

}
