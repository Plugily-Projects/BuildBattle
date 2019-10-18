/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Tigerpanzer_02, Plajer and contributors
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

package pl.plajer.buildbattle.user.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.user.User;
import pl.plajerlair.commonsbox.database.MysqlDatabase;

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
            "CREATE TABLE IF NOT EXISTS `buildbattlestats` (\n"
                + "  `UUID` text NOT NULL,\n"
                + "  `name` text NOT NULL,\n"
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
          statement.executeUpdate("ALTER TABLE buildbattlestats ADD supervotes int(11) NOT NULL DEFAULT '0'");
        } catch (SQLException e) {
          if (!e.getMessage().contains("Duplicate column name")) {
            e.printStackTrace();
          }
        }
        try {
          statement.executeUpdate("ALTER TABLE buildbattlestats ADD name text NOT NULL");
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
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> database.executeUpdate("UPDATE `buildbattlestats` SET " + stat.getName() + "=" + user.getStat(stat) + " WHERE UUID='" + user.getPlayer().getUniqueId().toString() + "';"));
  }

  @Override
  public void loadStatistic(User user, StatsStorage.StatisticType stat) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      try (Connection connection = database.getConnection();
           Statement statement = connection.createStatement()) {
        if (!statement.executeQuery("SELECT UUID from buildbattlestats WHERE UUID='" + user.getPlayer().getUniqueId().toString() + "'").next()) {
          insertPlayer(user.getPlayer());
        }

        try (ResultSet set = statement.executeQuery("SELECT " + stat.getName() + " FROM `buildbattlestats` WHERE UUID='" + user.getPlayer().getUniqueId().toString() + "'")) {
          if (!set.next()) {
            user.setStat(stat, 0);
            return;
          }
          user.setStat(stat, set.getInt(1));
        }
      } catch (SQLException e) {
        plugin.getLogger().log(Level.WARNING, "Could not connect to MySQL database! Cause: {0} ({1})", new Object[] {e.getSQLState(), e.getErrorCode()});
        user.setStat(stat, 0);
      }
    });
  }

  private void insertPlayer(Player player) {
    database.executeUpdate("INSERT INTO `buildbattlestats` (UUID,name,gamesplayed) VALUES ('" + player.getUniqueId().toString() + "','" + player.getName() + "',0)");
  }

}
