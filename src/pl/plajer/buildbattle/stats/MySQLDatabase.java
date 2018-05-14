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

package pl.plajer.buildbattle.stats;

import org.bukkit.Bukkit;
import pl.plajer.buildbattle.ConfigPreferences;
import pl.plajer.buildbattle.Main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class MySQLDatabase {

    private MySQLConnectionManager manager;
    private Main plugin;

    public MySQLDatabase(Main plugin) {
        this.plugin = plugin;
        this.manager = new MySQLConnectionManager(this.plugin);
        this.plugin.getLogger().info("Configuring connection pool...");
        manager.configureConnPool();
        try {
            Connection connection = manager.getConnection();
            if(connection == null) {
                System.out.print("CONNECTION TO DATABASE FAILED!");
                return;
            }

            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS `buildbattlestats` (\n" + "  `UUID` text NOT NULL,\n" + "  `loses` int(11) NOT NULL DEFAULT '0',\n" + "  `wins` int(11) NOT NULL DEFAULT '0',\n" + "  `highestwin` int(11) NOT NULL DEFAULT '0',\n" + "  `gamesplayed` int(11) NOT NULL DEFAULT '0',\n" + "  `blocksbroken` int(11) NOT NULL DEFAULT '0',\n" + "  `blocksplaced` int(11) NOT NULL DEFAULT '0',\n" + "  `particles` int(11) NOT NULL DEFAULT '0'\n" + ");");
            manager.closeConnection(connection);
        } catch(SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    private void executeUpdate(String query) {
        try {
            Connection connection = manager.getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            manager.closeConnection(connection);
        } catch(SQLException e) {
            plugin.getLogger().warning("Failed to execute update: " + query);
        }

    }

    public ResultSet executeQuery(String query) {
        try {
            Connection connection = manager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            manager.closeConnection(connection);
            return rs;
        } catch(SQLException exception) {
            exception.printStackTrace();
            plugin.getLogger().warning("Failed to execute request: " + query);
            return null;
        }
    }

    public void insertPlayer(String UUID) {
        if(ConfigPreferences.isNameUsedInDatabase()) {
            UUID = Bukkit.getOfflinePlayer(java.util.UUID.fromString(UUID)).getName();
        }
        executeUpdate("INSERT INTO `buildbattlestats` (UUID,gamesplayed) VALUES ('" + UUID + "',0)");
    }

    public void closeDatabase() {
        manager.shutdownConnPool();
    }

    public void setStat(String UUID, String stat, int number) {
        if(ConfigPreferences.isNameUsedInDatabase()) {
            UUID = Bukkit.getOfflinePlayer(java.util.UUID.fromString(UUID)).getName();
        }
        executeUpdate("UPDATE `buildbattlestats` SET " + stat + "=" + number + " WHERE UUID='" + UUID + "';");
    }

    public int getStat(String UUID, String stat) {
        if(ConfigPreferences.isNameUsedInDatabase()) {
            UUID = Bukkit.getOfflinePlayer(java.util.UUID.fromString(UUID)).getName();
        }
        ResultSet set = executeQuery("SELECT " + stat + " FROM `buildbattlestats` WHERE UUID='" + UUID + "'");
        try {
            if(!set.next()) return 0;
            return (set.getInt(1));
        } catch(SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return 0;
        }
    }

    public Map<UUID, Integer> getColumn(String stat) {
        ResultSet set = executeQuery("SELECT UUID, " + stat + " FROM buildbattlestats ORDER BY " + stat + " DESC;");
        Map<java.util.UUID, java.lang.Integer> column = new LinkedHashMap<>();
        try {
            while(set.next()) {
                column.put(java.util.UUID.fromString(set.getString("UUID")), set.getInt(stat));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return column;
    }


}