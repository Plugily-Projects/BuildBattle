package pl.plajer.buildbattle.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.User;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.handlers.UserManager;
import pl.plajer.buildbattle.stats.MySQLDatabase;
import pl.plajer.buildbattle.utils.UpdateChecker;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 10/07/2015.
 */
//TODO update checker
public class JoinEvents implements Listener {

    private Main plugin;

    public JoinEvents(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(plugin.isBungeeActivated()) return;
        for(Player player : plugin.getServer().getOnlinePlayers()) {
            if(ArenaRegistry.getArena(player) == null) continue;
            player.hidePlayer(event.getPlayer());
            event.getPlayer().hidePlayer(player);
        }
    }

    @EventHandler
    public void onJoinCheckVersion(final PlayerJoinEvent event) {
        //we want to be the first :)
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            /*if(event.getPlayer().isOp() && !plugin.isDataEnabled()){
                event.getPlayer().sendMessage(ChatColor.RED + "[BuildBattle] It seems that you've disabled bStats statistics.");
                event.getPlayer().sendMessage(ChatColor.RED + "Please consider enabling it to help us develop our plugins better!");
                event.getPlayer().sendMessage(ChatColor.RED + "Enable it in plugins/bStats/config.yml file");
            }*/
            //todo perm
            if(event.getPlayer().hasPermission("buildbattle.updatenotify")) {
                if(plugin.getConfig().getBoolean("Update-Notifier.Enabled")) {
                    String currentVersion = "v" + Bukkit.getPluginManager().getPlugin("BuildBattle").getDescription().getVersion();
                    String latestVersion;
                    try {
                        UpdateChecker.checkUpdate(currentVersion);
                        latestVersion = UpdateChecker.getLatestVersion();
                        if(latestVersion != null) {
                            latestVersion = "v" + latestVersion;
                            if(latestVersion.contains("b")) {
                                event.getPlayer().sendMessage("");
                                event.getPlayer().sendMessage(ChatColor.BOLD + "BUILD BATTLE UPDATE NOTIFY");
                                event.getPlayer().sendMessage(ChatColor.RED + "BETA version of software is ready for update! Proceed with caution.");
                                event.getPlayer().sendMessage(ChatColor.YELLOW + "Current version: " + ChatColor.RED + currentVersion + ChatColor.YELLOW + " Latest version: " + ChatColor.GREEN + latestVersion);
                            } else {
                                event.getPlayer().sendMessage("");
                                event.getPlayer().sendMessage(ChatColor.BOLD + "BUILD BATTLE UPDATE NOTIFY");
                                event.getPlayer().sendMessage(ChatColor.GREEN + "Software is ready for update! Download it to keep with latest changes and fixes.");
                                event.getPlayer().sendMessage(ChatColor.YELLOW + "Current version: " + ChatColor.RED + currentVersion + ChatColor.YELLOW + " Latest version: " + ChatColor.GREEN + latestVersion);
                            }
                        }
                    } catch(Exception ex) {
                        event.getPlayer().sendMessage(ChatColor.RED + "[BuildBattle] An error occured while checking for update!");
                        event.getPlayer().sendMessage(ChatColor.RED + "Please check internet connection or check for update via WWW site directly!");
                        event.getPlayer().sendMessage(ChatColor.RED + "WWW site https://www.spigotmc.org/resources/minigame-village-defence-1-12-and-1-8-8.41869/");
                    }
                }
            }
        }, 25);
    }

    @EventHandler
    public void onJoinLoadStats(final PlayerJoinEvent event) {
        if(plugin.isBungeeActivated()) ArenaRegistry.getArenas().get(0).teleportToLobby(event.getPlayer());
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
        final String playername = event.getPlayer().getUniqueId().toString();
        final Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            MySQLDatabase database = plugin.getMySQLDatabase();
            ResultSet resultSet = database.executeQuery("SELECT UUID from buildbattlestats WHERE UUID='" + playername + "'");
            try {
                if(!resultSet.next()) {
                    database.insertPlayer(playername);
                    return;
                }
                int gamesplayed;
                int wins;
                int highestwin;
                int loses;
                int blocksPlaced;
                int blocksBroken;
                int particles;
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
            } catch(SQLException e1) {
                System.out.print("CONNECTION FAILED FOR PLAYER " + playername);
                //e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        });
    }

}
