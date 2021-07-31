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

package gg.solarmc.kitpvp.config;

import net.kyori.adventure.sound.Sound;
import space.arim.api.jsonchat.adventure.util.ComponentText;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

import java.math.BigDecimal;
import java.util.Optional;

public interface Violence {

    @ConfKey("damage-expire-minutes")
    @ConfComments({"After this time has passed, a player who has damaged another ",
            "will no longer be considered an assistant if the damaged player dies."})
    @ConfDefault.DefaultInteger(10)
    int damageExpireMinutes();

    @ConfKey("kill-rewards")
    @SubSection
    KillRewards killRewards();

    interface KillRewards {

        @ConfKey("kill-reward-table")
        @ConfComments({"Reward per kill based on the player's killstreak",
                "Given the player's killstreak, it is matched to the highest number on the left.",
                "Then, the corresponding number on the right is used as the kill reward."})
        @ConfDefault.DefaultMap({
                "0", "50",
                "50", "60",
                "100", "70",
                "150", "80",
                "200", "90",
                "250", "100"})
        RangedLookupTableBigDecimal killReward();

        @ConfKey("rewarded-kill-message")
        @ConfComments({"The message when a killer receives a reward. Set to empty to disable. Variables:",
                "%KILL_REWARD% - the amount rewarded",
                "%VICTIM% - who was killed",
                "%NEW_BALANCE% - the balance the killer now has"})
        @ConfDefault.DefaultString("You received %KILL_REWARD% for finishing off %VICTIM%. You now have %NEW_BALANCE%")
        ComponentText rewardedKillMessage();

        @ConfKey("kill-sound")
        @ConfComments("The sound when a player kills another. Set to empty ({}) to disable")
        @ConfDefault.DefaultObject("defaultKillSound")
        PossibleSound killSound();

        static PossibleSound defaultKillSound() {
            return () -> Optional.of(Sound.sound(
                    org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.Source.PLAYER, 1F, 1F));
        }

        @ConfKey("assist-reward")
        @ConfComments("Reward per assist")
        @ConfDefault.DefaultLong(20)
        BigDecimal assistReward();

        @ConfKey("rewarded-assist-message")
        @ConfComments({"The message when an assistant receives a reward. Set to empty to disable. Variables:",
                "%ASSIST_REWARD% - the amount rewarded",
                "%VICTIM% - who was killed",
                "%NEW_BALANCE% - the balance the assistant now has"})
        @ConfDefault.DefaultString("You received %ASSIST_REWARD% for helping to finish off %VICTIM%. You now have %NEW_BALANCE%")
        ComponentText assistedKillMessage();

    }

    @ConfKey("experience-per-kill")
    @ConfComments("Raw experience points gained per kill")
    @ConfDefault.DefaultInteger(1)
    int experiencePerKill();

    @ConfKey("gained-killstreak-message")
    @ConfComments({"The message when a player's killstreak increases. Set to empty to disable. Variables:",
            "%NEW_KILLSTREAK% - the new killstreak"})
    @ConfDefault.DefaultString("You gained a killstreak of %NEW_KILLSTREAK%.")
    ComponentText gainedKillstreakMessage();

    @ConfKey("lost-killstreak-message")
    @ConfComments({"The message when a player, by dying, has lost their killstreak.",
            "Set to empty to disable. Variables:",
            "%PREVIOUS_KILLSTREAK% - the killstreak they had"})
    @ConfDefault.DefaultString("You lost your killstreak of %PREVIOUS_KILLSTREAK% by dying.")
    ComponentText lostKillstreakMessage();

}
