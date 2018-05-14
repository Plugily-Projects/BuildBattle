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

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.handlers.ConfigurationManager;
import pl.plajer.buildbattle3.user.UserManager;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * @author Plajer
 * <p>
 * Created at 14.05.2018
 */
public class StatsStorage {

    public static Main plugin;

    private static Map sortByValue(Map unsortMap) {
        List list = new LinkedList(unsortMap.entrySet());
        list.sort((o1, o2) -> ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue()));
        Map sortedMap = new LinkedHashMap();
        for(Object aList : list) {
            Map.Entry entry = (Map.Entry) aList;
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    /**
     * Get all UUID's sorted ascending by Statistic Type
     *
     * @param stat Statistic type to get (kills, deaths etc.)
     * @return Map of UUID keys and Integer values sorted in ascending order of requested statistic type
     */
    public static Map<UUID, Integer> getStats(StatisticType stat) {
        Main.debug("Village API getStats(" + stat.getName() + ") run", System.currentTimeMillis());
        if(plugin.isDatabaseActivated())
            return plugin.getMySQLDatabase().getColumn(stat.getName());
        else {
            FileConfiguration config = ConfigurationManager.getConfig("stats");
            Map<UUID, Integer> stats = new TreeMap<>();
            for(String string : config.getKeys(false)) {
                stats.put(UUID.fromString(string), config.getInt(string + "." + stat.getName()));
            }
            return sortByValue(stats);
        }
    }

    /**
     * Get user statistic based on StatisticType
     *
     * @param player        Online player to get data from
     * @param statisticType Statistic type to get (blocks placed, wins etc.)
     * @return int of statistic
     * @see StatisticType
     */
    public static int getUserStats(Player player, StatisticType statisticType) {
        Main.debug("Village API getUserStats(" + player.getName() + ", " + statisticType.getName() + ") run", System.currentTimeMillis());
        return UserManager.getUser(player.getUniqueId()).getInt(statisticType.name);
    }

    /**
     * Available statistics to get.
     */
    public enum StatisticType {
        BLOCKS_PLACED("blocksplaced"), BLOCKS_BROKEN("blocksbroken"), GAMES_PLAYED("gamesplayed"), WINS("wins"), LOSES("loses"), HIGHEST_WIN("highestwin"), PARTICLES_USED("particles");

        String name;

        StatisticType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
