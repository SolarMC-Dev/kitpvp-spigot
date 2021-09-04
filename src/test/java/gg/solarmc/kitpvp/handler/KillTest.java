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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class KillTest {

    private final Player killer;
    private final Player victim;

    private Kill kill;

    public KillTest(@Mock Player killer, @Mock Player victim) {
        this.killer = killer;
        this.victim = victim;
    }

    @BeforeEach
    public void setKill() {
        kill = new Kill(killer, victim, "KillerName", "VictimName");
    }

    @Test
    public void sendSimpleMessage(@Mock Player receiver) {
        Component message = Component.text("msg");
        kill.sendMessageIfNotEmpty(receiver, message);
        kill.postTransactCallback();
        kill.mainThreadCallback();
        verify(receiver).sendMessage((ComponentLike) message); // Overloaded methods are different
        verifyNoMoreInteractions(receiver, killer, victim);
    }

    @Test
    public void sendNestedMessage(@Mock Player receiver) {
        Component message = TextComponent.ofChildren(Component.text("hello"), Component.text(" world"));
        kill.sendMessageIfNotEmpty(receiver, message);
        kill.postTransactCallback();
        kill.mainThreadCallback();
        verify(receiver).sendMessage((ComponentLike) message); // Overloaded methods are different
        verifyNoMoreInteractions(receiver, killer, victim);
    }

    @Test
    public void avoidSendEmptyMessage(@Mock Player receiver) {
        Component message = Component.empty();
        kill.sendMessageIfNotEmpty(receiver, message);
        kill.postTransactCallback();
        kill.mainThreadCallback();
        verifyNoMoreInteractions(receiver, killer, victim);
    }
}
