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

package pl.plajer.buildbattle.selfmadeeventsupdateme;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.plajer.buildbattle.arena.Arena;

/**
 * Created by Tom on 1/11/2015.
 */
public class BBGameEndEvent extends Event {


    private static final HandlerList handlers = new HandlerList();
    private Arena buildInstance;

    public BBGameEndEvent(Arena buildInstance) {
        this.buildInstance = buildInstance;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Arena getBuildInstance() {
        return buildInstance;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
