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

package pl.plajer.buildbattle3.arena;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 27/07/2014.
 */
public class ArenaRegistry {

    private static List<Arena> arenas = new ArrayList<>();

    public static List<Arena> getArenas() {
        return arenas;
    }

    @Nullable
    public static Arena getArena(Player p) {
        if(p == null) return null;
        if(!p.isOnline()) return null;

        for(Arena arena : arenas) {
            for(Player player : arena.getPlayers()) {
                if(player.getUniqueId() == p.getUniqueId()) {
                    return arena;
                }
            }
        }
        return null;
    }

    public static void registerArena(Arena arena) {
        arenas.add(arena);
    }

    public static Arena getArena(String ID) {
        for(Arena arena : arenas) {
            if(arena.getID().equalsIgnoreCase(ID)) {
                return arena;
            }
        }
        return null;
    }

}
