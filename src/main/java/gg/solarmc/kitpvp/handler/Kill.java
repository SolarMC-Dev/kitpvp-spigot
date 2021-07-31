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

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class Kill implements KillCallback {

    // Careful - these callbacks need not run on the main thread
    private final List<Runnable> anywhereCallbacks = new ArrayList<>();
    private final List<Runnable> syncCallbacks = new ArrayList<>();

    private final Player killer;
    private final Player victim;
    private final String killerName;
    private final String victimName;

    Kill(Player killer, Player victim, String killerName, String victimName) {
        this.killer = killer;
        this.victim = victim;
        this.killerName = killerName;
        this.victimName = victimName;
    }

    Kill(Player killer, Player victim) {
        this(killer, victim, killer.getName(), victim.getName());
    }

    Audience broadcast() {
        return new ForwardingAudience() {
            @Override
            public @NonNull Iterable<? extends Audience> audiences() {
                return Set.of(killer.getServer());
            }
        };
    }

    Player killer() {
        return killer;
    }

    Player victim() {
        return victim;
    }

    String killerName() {
        return killerName;
    }

    String victimName() {
        return victimName;
    }

    /**
     * Adds a general callback. Careful: this need not run on the main thread
     *
     * @param anywhereCallback the callback which may run anywhere
     */
    void addCallback(Runnable anywhereCallback) {
        anywhereCallbacks.add(anywhereCallback);
    }

    void addSyncCallback(Runnable syncCallback) {
        syncCallbacks.add(syncCallback);
    }

    void sendMessageIfNotEmpty(Audience target, ComponentLike message) {
        Component component = message.asComponent();
        if (component instanceof TextComponent tc && tc.content().isEmpty()) {
            return;
        }
        addSyncCallback(() -> target.sendMessage(component));
    }

    @Override
    public void postTransactCallback() {
        anywhereCallbacks.forEach(Runnable::run);
    }

    @Override
    public void mainThreadCallback() {
        syncCallbacks.forEach(Runnable::run);
    }

}
