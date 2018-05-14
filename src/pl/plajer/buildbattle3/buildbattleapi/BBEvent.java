/*
 *  Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.plajer.buildbattle3.buildbattleapi;

import org.bukkit.event.Event;
import pl.plajer.buildbattle3.arena.Arena;

/**
 * Represents BuildBattle game related events.
 */
public abstract class BBEvent extends Event {

    protected Arena arena;

    public BBEvent(Arena eventArena) {
        arena = eventArena;
    }

    /**
     * Returns event arena
     *
     * @return event arena
     */
    public Arena getArena() {
        return arena;
    }
}
