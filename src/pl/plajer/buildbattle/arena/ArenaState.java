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

package pl.plajer.buildbattle.arena;

/**
 * Created by Tom on 27/07/2014.
 */
public enum ArenaState {
    WAITING_FOR_PLAYERS, STARTING, IN_GAME, ENDING, RESTARTING;

    public static ArenaState fromString(String s) {
        if(s.contains("RESTARTING")) return RESTARTING;
        if(s.contains("WAITING_FOR")) return WAITING_FOR_PLAYERS;
        if(s.contains("STARTING")) return STARTING;
        if(s.contains("IN_GAME")) return IN_GAME;
        if(s.contains("ENDING")) return ENDING;
        return WAITING_FOR_PLAYERS;
    }
}
