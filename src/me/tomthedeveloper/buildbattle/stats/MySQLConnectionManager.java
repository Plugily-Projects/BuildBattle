package me.tomthedeveloper.buildbattle.stats;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import me.TomTheDeveloper.Handlers.ConfigurationManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * User: Ivan
 * Date: 28/09/13
 * Time: 14:39
 * Look me up on bukkit forums!
 * http://forums.bukkit.org/members/ivan.5352/
 */
public class MySQLConnectionManager {

    private BoneCP connectionPool = null;
    private JavaPlugin plugin;
    private int MIN_CONNECTIONS = 5;
    private int MAX_CONNECTIONS = 10;

    public MySQLConnectionManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void configureConnPool() {
        FileConfiguration databaseconfig = ConfigurationManager.getConfig("MySQL");

        try {

            Class.forName("com.mysql.jdbc.Driver"); //also you need the MySQL driver
            plugin.getLogger().info("Creating BoneCP Configuration...");
            BoneCPConfig config = new BoneCPConfig();
            if(!databaseconfig.contains("address")) {
                databaseconfig.set("address", "jdbc:mysql://localhost:3306/<databasename>");
                databaseconfig.set("user", "<user>");
                databaseconfig.set("password", "<password>");
                databaseconfig.set("min-connections", 5);
                databaseconfig.set("max-connections", 10);
                databaseconfig.save(ConfigurationManager.getFile("MySQL"));
                plugin.getServer().shutdown();
                return;
            }

            config.setJdbcUrl(databaseconfig.getString("address"));
            config.setUsername(databaseconfig.getString("user"));
            config.setPassword(databaseconfig.getString("password"));
            config.setMinConnectionsPerPartition(databaseconfig.getInt("min-connections")); //if you say 5 here, there will be 10 connection available
            config.setMaxConnectionsPerPartition(databaseconfig.getInt("max-connections"));
            config.setPartitionCount(2); //2*5 = 10 connection will be available
            //config.setLazyInit(true); //depends on the application usage you should chose lazy or not
            //setting Lazy true means BoneCP won't open any connections before you request a one from it.
            plugin.getLogger().info("Setting up MySQL Connection pool...");
            connectionPool = new BoneCP(config); // setup the connection pool
            plugin.getLogger().info("Connection pool successfully configured. ");
            plugin.getLogger().info("Total connections ==> " + connectionPool.getTotalCreatedConnections());


        } catch(Exception e) {

            e.printStackTrace(); //you should use exception wrapping on real-production code
        }

    }

    public void shutdownConnPool() {

        try {

            plugin.getLogger().info("Shutting down connection pool. Trying to close all connections.");
            if(connectionPool != null) {
                connectionPool.shutdown(); //this method must be called only once when the application stops.
                //you don't need to call it every time when you get a connection from the Connection Pool
                plugin.getLogger().info("Pool successfully shutdown. ");
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {


        Connection conn = null;
        try {
            conn = getConnectionPool().getConnection();
            //will get a thread-safe connection from the BoneCP connection pool.
            //synchronization of the method will be done inside BoneCP source

        } catch(Exception e) {
            e.printStackTrace();
        }
        return conn;

    }

    public void closeStatement(Statement stmt) {
        try {
            if(stmt != null)
                stmt.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void closeResultSet(ResultSet rSet) {
        try {
            if(rSet != null)
                rSet.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void closeConnection(Connection conn) {
        try {
            if(conn != null)
                conn.close(); //release the connection - the name is tricky but connection is not closed it is released
            //and it will stay in pool
        } catch(SQLException e) {
            e.printStackTrace();
        }

    }

    private BoneCP getConnectionPool() {
        return connectionPool;
    }

    public void setConnectionPool(BoneCP connectionPool) {
        connectionPool = connectionPool;
    }

}
