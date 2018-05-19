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

package pl.plajer.buildbattle3.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.arena.ArenaRegistry;
import pl.plajer.buildbattle3.stats.MySQLDatabase;
import pl.plajer.buildbattle3.user.User;
import pl.plajer.buildbattle3.user.UserManager;
import pl.plajer.buildbattle3.utils.UpdateChecker;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 10/07/2015.
 */

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
        Player p = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if(p.isOp() && !plugin.isDataEnabled()) {
                p.sendMessage("§c[BuildBattle] It seems that you've disabled bStats statistics.");
                p.sendMessage("§cPlease consider enabling it to help us develop our plugins better!");
                p.sendMessage("§cEnable it in plugins/bStats/config.yml file");
            }
            if(p.hasPermission("buildbattle.updatenotify")) {
                if(plugin.getConfig().getBoolean("Update-Notifier.Enabled")) {
                    String currentVersion = "v" + Bukkit.getPluginManager().getPlugin("BuildBattle").getDescription().getVersion();
                    String latestVersion;
                    try {
                        UpdateChecker.checkUpdate(currentVersion);
                        latestVersion = UpdateChecker.getLatestVersion();
                        if(latestVersion != null) {
                            latestVersion = "v" + latestVersion;
                            if(latestVersion.contains("b")) {
                                p.sendMessage("");
                                p.sendMessage("§lBUILD BATTLE UPDATE NOTIFY");
                                p.sendMessage("§cBETA version of software is ready for update! Proceed with caution.");
                                p.sendMessage("§eCurrent version:§c " + currentVersion + "§e Latest version:§a " + latestVersion);
                            } else {
                                p.sendMessage("");
                                p.sendMessage("§lBUILD BATTLE UPDATE NOTIFY");
                                p.sendMessage("§aSoftware is ready for update! Download it to keep with latest changes and fixes.");
                                p.sendMessage("§eCurrent version:§c " + currentVersion + "§e Latest version:§a " + latestVersion);
                            }
                        }
                    } catch(Exception ex) {
                        p.sendMessage("§c[BuildBattle] An error occured while checking for update!");
                        p.sendMessage("§cPlease check internet connection or check for update via WWW site directly!");
                        p.sendMessage("§cWWW site https://www.spigotmc.org/resources/minigame-village-defence-1-12-and-1-8-8.41869/");
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
            }
        });
    }

}
