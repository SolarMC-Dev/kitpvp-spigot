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

package gg.solarmc.kitpvp.listeners;

import gg.solarmc.kitpvp.config.Config;
import gg.solarmc.kitpvp.config.ConfigCenter;
import gg.solarmc.kitpvp.config.Violence;
import gg.solarmc.kitpvp.handler.KillHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KillListenerTest {

    private final ConfigCenter configCenter;
    private final KillHandler killHandler;

    private KillListener killListener;

    public KillListenerTest(@Mock ConfigCenter configCenter, @Mock KillHandler killHandler) {
        this.configCenter = configCenter;
        this.killHandler = killHandler;
    }

    @BeforeEach
    public void setKillListener(@Mock Config config, @Mock Violence violence) {
        killListener = new KillListener(configCenter, killHandler);
        when(configCenter.config()).thenReturn(config);
        when(config.violence()).thenReturn(violence);
        when(violence.damageExpireMinutes()).thenReturn(1);
        killListener.start();
    }

    private void damage(Player victim, Player attacker, double damage) {
        //noinspection deprecation
        killListener.onDamage(
                new EntityDamageByEntityEvent(attacker, victim, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage));
    }

    private void damageAndKill(Player victim, Player attacker, double damage) {
        damage(victim, attacker, damage);
        killListener.onDeath(new PlayerDeathEvent(victim, List.of(), 0, ""));
    }

    @Test
    public void killWithSomeAssistants(@Mock Player victim, @Mock Player attacker,
                                       @Mock Player assistant1, @Mock Player assistant2) {
        damage(victim, assistant1, 2D);
        damage(victim, assistant2, 2D);
        damageAndKill(victim, attacker, 1D);
        verify(killHandler).onKill(victim, attacker, Set.of(assistant1, assistant2));
    }

}
