/*
 * BuildBattle 4 - Ultimate building competition minigame
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.buildbattle4.database;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.plajer.buildbattle4.Main;
import pl.plajer.buildbattle4.utils.MessageUtils;
import pl.plajerlair.core.database.MySQLDatabase;

/**
 * @author Plajer
 * <p>
 * Created at 28.09.2018
 */
public class MySQLManager {

  private MySQLDatabase database;

  public MySQLManager(Main plugin) {
    database = plugin.getMySQLDatabase();
    Connection conn = database.getManager().getConnection();
    try {
      conn.createStatement().executeUpdate(
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
        conn.createStatement().executeUpdate("ALTER TABLE buildbattlestats ADD supervotes int(11) NOT NULL DEFAULT '0'");
      } catch (MySQLSyntaxErrorException e) {
        if (!e.getMessage().contains("Duplicate column name")) {
          e.printStackTrace();
        }
      }
      try {
        conn.createStatement().executeUpdate("ALTER TABLE buildbattlestats ADD name text NOT NULL");
      } catch (MySQLSyntaxErrorException e) {
        if (!e.getMessage().contains("Duplicate column name")) {
          e.printStackTrace();
        }
      }
      database.getManager().closeConnection(conn);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void insertPlayer(Player player) {
    database.executeUpdate("INSERT INTO `buildbattlestats` (UUID,name,gamesplayed) VALUES ('" + player.getUniqueId().toString() + "','" + player.getName() + "',0)");
  }

  public void setStat(String UUID, String stat, int number) {
    database.executeUpdate("UPDATE `buildbattlestats` SET " + stat + "=" + number + " WHERE UUID='" + UUID + "';");
  }

  public int getStat(String UUID, String stat) {
    ResultSet set = database.executeQuery("SELECT " + stat + " FROM `buildbattlestats` WHERE UUID='" + UUID + "'");
    try {
      if (!set.next()) {
        return 0;
      }
      return (set.getInt(1));
    } catch (SQLException e) {
      e.printStackTrace();
      MessageUtils.errorOccurred();
      Bukkit.getConsoleSender().sendMessage("Cannot get contents from MySQL database!");
      Bukkit.getConsoleSender().sendMessage("Check configuration of mysql.yml file or disable mysql option in config.yml");
      return 0;
    }
  }

  public Map<UUID, Integer> getColumn(String stat) {
    ResultSet set = database.executeQuery("SELECT UUID, " + stat + " FROM buildbattlestats ORDER BY " + stat + " DESC;");
    Map<java.util.UUID, java.lang.Integer> column = new LinkedHashMap<>();
    try {
      while (set.next()) {
        column.put(java.util.UUID.fromString(set.getString("UUID")), set.getInt(stat));
      }
    } catch (SQLException e) {
      e.printStackTrace();
      MessageUtils.errorOccurred();
      Bukkit.getConsoleSender().sendMessage("Cannot get contents from MySQL database!");
      Bukkit.getConsoleSender().sendMessage("Check configuration of mysql.yml file or disable mysql option in config.yml");
    }
    return column;
  }

}
