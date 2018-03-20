package me.tomthedeveloper.buildbattle.stats;

import me.tomthedeveloper.buildbattle.ConfigPreferences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class MySQLDatabase {

    private MySQLConnectionManager manager;
    private JavaPlugin plugin;

    public MySQLDatabase(JavaPlugin javaPlugin) {
        this.plugin = javaPlugin;
        this.manager = new MySQLConnectionManager(plugin);
        plugin.getLogger().info("Configuring connection pool...");
        manager.configureConnPool();


        try {
            Connection connection = manager.getConnection();
            if(connection == null) {
                System.out.print("CONNECTION TO DATABASE FAILED!");
                return;
            }

            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS `buildbattlestats` (\n" +
                    "  `UUID` text NOT NULL,\n" +
                    "  `loses` int(11) NOT NULL DEFAULT '0',\n" +
                    "  `wins` int(11) NOT NULL DEFAULT '0',\n" +
                    "  `highestwin` int(11) NOT NULL DEFAULT '0',\n" +
                    "  `gamesplayed` int(11) NOT NULL DEFAULT '0',\n" +
                    "  `blocksbroken` int(11) NOT NULL DEFAULT '0',\n" +
                    "  `blocksplaced` int(11) NOT NULL DEFAULT '0',\n" +
                    "  `particles` int(11) NOT NULL DEFAULT '0'\n" +
                    ");");
            manager.closeConnection(connection);
        } catch(SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        // Table exists
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


    public void addStat(String UUID, String stat) {
        addStat(UUID, stat, 1);
    }

    private void addStat(String UUID, String stat, int amount) {
        if(ConfigPreferences.isNameUsedInDatabase()) {
            UUID = Bukkit.getPlayer(UUID).getName();
        }
        executeUpdate("UPDATE `buildbattlestats` SET " + stat + "=" + stat + "+" + amount + " WHERE UUID='" + UUID + "'");
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
            if(!set.next())
                return 0;
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