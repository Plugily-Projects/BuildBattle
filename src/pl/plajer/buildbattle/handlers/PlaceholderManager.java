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

package pl.plajer.buildbattle.handlers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import pl.plajer.buildbattle.stats.BuildBattleStats;

/**
 * @author Plajer
 * <p>
 * Created at 06.05.2018
 */
public class PlaceholderManager extends PlaceholderExpansion {

    public boolean persist() {
        return true;
    }

    public String getIdentifier() {
        return "buildbattle";
    }

    public String getPlugin() {
        return null;
    }

    public String getAuthor() {
        return "Plajer";
    }

    public String getVersion() {
        return "1.0.0";
    }

    //todo rework me
    public String onPlaceholderRequest(Player player, String id) {
        if(player == null) return null;
        switch(id) {
            case "blocks_broken":
                return String.valueOf(BuildBattleStats.BLOCKS_BROKEN.getStat(player));
            case "blocks_placed":
                return String.valueOf(BuildBattleStats.BLOCKS_PLACED.getStat(player));
            case "games_played":
                return String.valueOf(BuildBattleStats.GAMES_PLAYED.getStat(player));
            case "wins":
                return String.valueOf(BuildBattleStats.WINS.getStat(player));
            case "loses":
                return String.valueOf(BuildBattleStats.LOSES.getStat(player));
            case "highest_win":
                return String.valueOf(BuildBattleStats.HIGHEST_WIN.getStat(player));
            case "particles_used":
                return String.valueOf(BuildBattleStats.PARTICLES_USED.getStat(player));
        }
        return null;
    }
}
