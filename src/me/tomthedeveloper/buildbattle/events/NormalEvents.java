package me.tomthedeveloper.buildbattle.events;

import me.TomTheDeveloper.GameAPI;
import me.TomTheDeveloper.Handlers.UserManager;
import me.TomTheDeveloper.User;
import me.tomthedeveloper.buildbattle.Main;
import me.tomthedeveloper.buildbattle.stats.MySQLDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 28/08/2015.
 */
public class NormalEvents implements Listener {

    private Main plugin;
    private GameAPI gameAPI;

    public NormalEvents(Main plugin) {
        this.plugin = plugin;
        gameAPI = plugin.getGameAPI();
    }


    @EventHandler
    public void onQuitSaveStats(PlayerQuitEvent event) {
        if(gameAPI.getGameInstanceManager().getGameInstance(event.getPlayer()) != null) {
            gameAPI.getGameInstanceManager().getGameInstance(event.getPlayer()).leaveAttempt(event.getPlayer());
        }
        final User user = UserManager.getUser(event.getPlayer().getUniqueId());
        final Player player = event.getPlayer();

        if(plugin.isDatabaseActivated()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                List<String> temp = new ArrayList<>();
                temp.add("gamesplayed");
                temp.add("wins");
                temp.add("loses");
                temp.add("highestwin");
                temp.add("blocksplaced");
                temp.add("blocksbroken");
                temp.add("particles");
                for(final String s : temp) {
                    int i;
                    try {
                        i = plugin.getMySQLDatabase().getStat(player.getUniqueId().toString(), s);
                    } catch(NullPointerException npe) {
                        i = 0;
                        System.out.print("COULDN'T GET STATS FROM PLAYER: " + player.getName());
                    }

                    if(i > user.getInt(s)) {
                        plugin.getMySQLDatabase().setStat(player.getUniqueId().toString(), s, user.getInt(s) + i);
                    } else {
                        plugin.getMySQLDatabase().setStat(player.getUniqueId().toString(), s, user.getInt(s));
                    }
                }
            });
            UserManager.removeUser(event.getPlayer().getUniqueId());
        } else {
            List<String> temp = new ArrayList<>();
            temp.add("gamesplayed");
            temp.add("wins");
            temp.add("loses");
            temp.add("highestwin");
            temp.add("blocksplaced");
            temp.add("blocksbroken");
            temp.add("particles");
            for(final String s : temp) {
                plugin.getFileStats().saveStat(player, s);
            }
        }


    }


    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        if(gameAPI.isBungeeActivated())
            gameAPI.getGameInstanceManager().getGameInstances().get(0).teleportToLobby(event.getPlayer());
        if(!plugin.isDatabaseActivated()) {
            List<String> temp = new ArrayList<>();
            temp.add("gamesplayed");
            temp.add("wins");
            temp.add("loses");
            temp.add("highestwin");
            temp.add("blocksplaced");
            temp.add("blocksbroken");
            temp.add("particles");
            for(String s : temp) {
                plugin.getFileStats().loadStat(event.getPlayer(), s);
            }
            return;
        }
        User user = UserManager.getUser(event.getPlayer().getUniqueId());
        final String playername = event.getPlayer().getUniqueId().toString();
        final Player player = event.getPlayer();
/*        if (plugin.getMyDatabase().getSingle(new BasicDBObject().append("UUID", event.getPlayer().getUniqueId().toString())) == null) {
            plugin.getMyDatabase().insertDocument(new String[]{"UUID", "gamesplayed", "kills", "deaths", "highestwave", "exp", "level", "orbs"},
                    new Object[]{event.getPlayer().getUniqueId().toString(), 0, 0, 0, 0, 0, 0, 0});
        }

        List<String> temp = new ArrayList<String>();
        temp.add("gamesplayed");
        temp.add("kills");
        temp.add("deaths");
        temp.add("highestwave");
        temp.add("exp");
        temp.add("level");
        temp.add("orbs");
        for (String s : temp) {
            user.setInt(s, (Integer) plugin.getMyDatabase().getSingle(new BasicDBObject("UUID", event.getPlayer().getUniqueId().toString())).get(s));
        } */

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            boolean b = false;
            MySQLDatabase database = plugin.getMySQLDatabase();
            ResultSet resultSet = database.executeQuery("SELECT UUID from buildbattlestats WHERE UUID='" + playername + "'");
            try {
                if(!resultSet.next()) {
                    database.insertPlayer(playername);
                    b = true;
                    return;
                }

                int gamesplayed = 0;
                int wins = 0;
                int highestwin = 0;
                int loses = 0;
                int blocksPlaced = 0;
                int blocksBroken = 0;
                int particles = 0;
                gamesplayed = database.getStat(player.getUniqueId().toString(), "gamesplayed");
                wins = database.getStat(player.getUniqueId().toString(), "wins");
                loses = database.getStat(player.getUniqueId().toString(), "loses");
                highestwin = database.getStat(player.getUniqueId().toString(), "highestwin");
                blocksPlaced = database.getStat(player.getUniqueId().toString(), "blocksplaced");
                blocksBroken = database.getStat(player.getUniqueId().toString(), "blocksbroken");
                particles = database.getStat(player.getUniqueId().toString(), "particles");
                User user1 = UserManager.getUser(player.getUniqueId());

                user1.setInt("gamesplayed", gamesplayed);
                user1.setInt("wins", wins);
                user1.setInt("highestwin", highestwin);
                user1.setInt("loses", loses);
                user1.setInt("blocksplaced", blocksPlaced);
                user1.setInt("blocksbroken", blocksBroken);
                user1.setInt("particles", particles);
                b = true;
            } catch(SQLException e1) {
                System.out.print("CONNECTION FAILED FOR PLAYER " + playername);
                //e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            if(b = false) {
                try {
                    if(!resultSet.next()) {
                        database.insertPlayer(playername);
                        b = true;
                        return;
                    }

                    int gamesplayed = 0;
                    int wins = 0;
                    int highestwin = 0;
                    int loses = 0;
                    int blocksPlaced = 0;
                    int blocksBroken = 0;
                    int particles = 0;
                    gamesplayed = database.getStat(player.getUniqueId().toString(), "gamesplayed");
                    wins = database.getStat(player.getUniqueId().toString(), "wins");
                    loses = database.getStat(player.getUniqueId().toString(), "loses");
                    highestwin = database.getStat(player.getUniqueId().toString(), "highestwin");
                    blocksPlaced = database.getStat(player.getUniqueId().toString(), "blocksplaced");
                    blocksBroken = database.getStat(player.getUniqueId().toString(), "blocksbroken");
                    particles = database.getStat(player.getUniqueId().toString(), "particles");
                    User user1 = UserManager.getUser(player.getUniqueId());

                    user1.setInt("gamesplayed", gamesplayed);
                    user1.setInt("wins", wins);
                    user1.setInt("highestwin", highestwin);
                    user1.setInt("loses", loses);
                    user1.setInt("blocksplaced", blocksPlaced);
                    user1.setInt("blocksbroken", blocksBroken);
                    user1.setInt("particles", particles);
                    b = true;
                } catch(SQLException e1) {
                    System.out.print("CONNECTION FAILED TWICE FOR PLAYER " + playername);
                    //e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });

    }
}
