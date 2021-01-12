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

package plugily.projects.buildbattle.user.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import pl.plajerlair.commonsbox.database.MysqlDatabase;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.api.StatsStorage;
import plugily.projects.buildbattle.user.User;

/**
 * @author Plajer
 * <p>
 * Created at 28.09.2018
 */
public class MysqlManager implements UserDatabase {

  private Main plugin;
  private MysqlDatabase database;

  public MysqlManager(Main plugin) {
    this.plugin = plugin;
    database = plugin.getMysqlDatabase();
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      try (Connection connection = database.getConnection();
           Statement statement = connection.createStatement()) {
        statement.executeUpdate(
            "CREATE TABLE IF NOT EXISTS `" + getTableName() + "` (\n"
                + "  `UUID` char(36) NOT NULL PRIMARY KEY,\n"
                + "  `name` varchar(32) NOT NULL,\n"
                + "  `loses` int(11) NOT NULL DEFAULT '0',\n"
                + "  `wins` int(11) NOT NULL DEFAULT '0',\n"
                + "  `highestwin` int(11) NOT NULL DEFAULT '0',\n"
                + "  `gamesplayed` int(11) NOT NULL DEFAULT '0',\n"
                + "  `blocksbroken` int(11) NOT NULL DEFAULT '0',\n"
                + "  `blocksplaced` int(11) NOT NULL DEFAULT '0',\n"
                + "  `supervotes` int(11) NOT NULL DEFAULT '0',\n"
                + "  `particles` int(11) NOT NULL DEFAULT '0');");

        //temporary workaround
        try {
          statement.executeUpdate("ALTER TABLE " + getTableName() + " ADD supervotes int(11) NOT NULL DEFAULT '0'");
        } catch (SQLException e) {
          if (!e.getMessage().contains("Duplicate column name")) {
            e.printStackTrace();
          }
        }
        try {
          statement.executeUpdate("ALTER TABLE " + getTableName() + " ADD name text NOT NULL");
        } catch (SQLException e) {
          if (!e.getMessage().contains("Duplicate column name")) {
            e.printStackTrace();
          }
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });
  }

  public MysqlDatabase getDatabase() {
    return database;
  }

  @Override
  public void saveStatistic(User user, StatsStorage.StatisticType stat) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> database.executeUpdate("UPDATE " + getTableName() + " SET " + stat.getName() + "=" + user.getStat(stat) + " WHERE UUID='" + user.getPlayer().getUniqueId().toString() + "';"));
  }

  @Override
  public void saveAllStatistic(User user) {
    StringBuilder update = new StringBuilder(" SET ");
    for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
      if (!stat.isPersistent()) {
        continue;
      }
      if (update.toString().equalsIgnoreCase(" SET ")){
        update.append(stat.getName()).append('=').append(user.getStat(stat));
      }
      update.append(", ").append(stat.getName()).append('=').append(user.getStat(stat));
    }
    String finalUpdate = update.toString();

    Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            database.executeUpdate("UPDATE " + getTableName() + finalUpdate + " WHERE UUID='" + user.getPlayer().getUniqueId().toString() + "';"));
  }

  @Override
  public void loadStatistics(User user) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      String uuid = user.getPlayer().getUniqueId().toString();
      try (Connection connection = database.getConnection();
           Statement statement = connection.createStatement()) {
        ResultSet rs = statement.executeQuery("SELECT * from " + getTableName() + " WHERE UUID='" + uuid + "'");
        if (rs.next()) {
          //player already exists - get the stats
          for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
            if (!stat.isPersistent()) continue;
            int val = rs.getInt(stat.getName());
            user.setStat(stat, val);
          }
        } else {
          //player doesn't exist - make a new record
          statement.executeUpdate("INSERT INTO " + getTableName() + " (UUID,name) VALUES ('" + uuid + "','" + user.getPlayer().getName() + "')");
          for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
            if (!stat.isPersistent()) continue;
            user.setStat(stat, 0);
          }
        }
      } catch (SQLException e) {
        plugin.getLogger().log(Level.WARNING, "Could not connect to MySQL database! Cause: {0} ({1})", new Object[] {e.getSQLState(), e.getErrorCode()});
      }
    });
  }

  public String getTableName() {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "mysql");
    return config.getString("table", "buildbattlestats");
  }

}
