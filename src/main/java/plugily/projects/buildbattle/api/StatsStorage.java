/*
 *
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2021 Plugily Projects - maintained by Tigerpanzer_02, 2Wild4You and contributors
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
 *
 */

package plugily.projects.buildbattle.api;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.sorter.SortUtils;
import plugily.projects.buildbattle.ConfigPreferences;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.user.data.MysqlManager;
import plugily.projects.buildbattle.utils.Debugger;
import plugily.projects.buildbattle.utils.MessageUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * @author Plajer, TomTheDeveloper
 * @since 2.0.0
 * <p>
 * Class for accessing users statistics.
 */
public class StatsStorage {

  private static final Main plugin = JavaPlugin.getPlugin(Main.class);

  /**
   * Get all UUID's sorted ascending by Statistic Type
   *
   * @param stat Statistic type to get (kills, deaths etc.)
   * @return Map of UUID keys and Integer values sorted in ascending order of requested statistic type
   */
  public static Map<UUID, Integer> getStats(StatisticType stat) {
    if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
      try(Connection connection = plugin.getMysqlDatabase().getConnection();
          Statement statement = connection.createStatement();
          ResultSet set = statement.executeQuery("SELECT UUID, " + stat.getName() + " FROM " + ((MysqlManager) plugin.getUserManager().getDatabase()).getTableName() + " ORDER BY " + stat.getName())) {
        Map<UUID, Integer> column = new LinkedHashMap<>();
        while(set.next()) {
          column.put(UUID.fromString(set.getString("UUID")), set.getInt(stat.getName()));
        }
        return column;
      } catch(SQLException e) {
        e.printStackTrace();
        MessageUtils.errorOccurred();
        Debugger.sendConsoleMsg("Cannot get contents from MySQL database!");
        Debugger.sendConsoleMsg("Check configuration of mysql.yml file or disable mysql option in config.yml");
        return Collections.emptyMap();
      }
    }
    FileConfiguration config = ConfigUtils.getConfig(plugin, "stats");
    Map<UUID, Integer> stats = new TreeMap<>();
    for(String string : config.getKeys(false)) {
      if("data-version".equals(string)) {
        continue;
      }
      stats.put(UUID.fromString(string), config.getInt(string + "." + stat.getName()));
    }
    return SortUtils.sortByValue(stats);
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
    return plugin.getUserManager().getUser(player).getStat(statisticType);
  }

  /**
   * Available statistics to get.
   */
  public enum StatisticType {
    BLOCKS_PLACED("blocksplaced", true), BLOCKS_BROKEN("blocksbroken", true), GAMES_PLAYED("gamesplayed", true), WINS("wins", true),
    LOSES("loses", true), HIGHEST_WIN("highestwin", true), PARTICLES_USED("particles", true), SUPER_VOTES("supervotes", true),
    LOCAL_POINTS("points", false), LOCAL_GUESS_THE_BUILD_POINTS("gtb_points", false);

    private final String name;
    private final boolean persistent;

    StatisticType(String name, boolean persistent) {
      this.name = name;
      this.persistent = persistent;
    }

    public String getName() {
      return name;
    }

    public boolean isPersistent() {
      return persistent;
    }
  }

}
