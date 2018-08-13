/*
 * BuildBattle 3 - Ultimate building competition minigame
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.arena.ArenaRegistry;
import pl.plajer.buildbattle3.stats.MySQLDatabase;
import pl.plajer.buildbattle3.user.User;
import pl.plajer.buildbattle3.user.UserManager;
import pl.plajerlair.core.services.ReportedException;
import pl.plajerlair.core.utils.UpdateChecker;

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
    if (plugin.isBungeeActivated()) return;
    for (Player player : plugin.getServer().getOnlinePlayers()) {
      if (ArenaRegistry.getArena(player) == null) continue;
      player.hidePlayer(event.getPlayer());
      event.getPlayer().hidePlayer(player);
    }
  }

  @EventHandler
  public void onJoinCheckVersion(final PlayerJoinEvent event) {
    try {
      //we want to be the first :)
      Bukkit.getScheduler().runTaskLater(plugin, () -> {
        if (event.getPlayer().isOp() && !plugin.isDataEnabled()) {
          event.getPlayer().sendMessage(ChatColor.RED + "[BuildBattle] It seems that you've disabled bStats statistics.");
          event.getPlayer().sendMessage(ChatColor.RED + "Please consider enabling it to help us develop our plugins better!");
          event.getPlayer().sendMessage(ChatColor.RED + "Enable it in plugins/bStats/config.yml file");
        }
        if (event.getPlayer().hasPermission("buildbattle.updatenotify")) {
          if (plugin.getConfig().getBoolean("Update-Notifier.Enabled")) {
            String currentVersion = "v" + Bukkit.getPluginManager().getPlugin("BuildBattle").getDescription().getVersion();
            String latestVersion;
            try {
              UpdateChecker.checkUpdate(plugin, currentVersion, 44703);
              latestVersion = UpdateChecker.getLatestVersion();
              if (latestVersion != null) {
                latestVersion = "v" + latestVersion;
                if (latestVersion.contains("b")) {
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
            } catch (Exception ex) {
              event.getPlayer().sendMessage(ChatColor.RED + "[BuildBattle] An error occured while checking for update!");
              event.getPlayer().sendMessage(ChatColor.RED + "Please check internet connection or check for update via WWW site directly!");
              event.getPlayer().sendMessage(ChatColor.RED + "WWW site https://www.spigotmc.org/resources/minigame-village-defence-1-12-and-1-8-8.41869/");
            }
          }
        }
      }, 25);
    } catch (Exception ex){
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onJoinLoadStats(final PlayerJoinEvent event) {
    try {
      if (plugin.isBungeeActivated()) ArenaRegistry.getArenas().get(0).teleportToLobby(event.getPlayer());
      if (!plugin.isDatabaseActivated()) {
        List<String> temp = new ArrayList<>();
        temp.add("gamesplayed");
        temp.add("wins");
        temp.add("loses");
        temp.add("highestwin");
        temp.add("blocksplaced");
        temp.add("blocksbroken");
        temp.add("particles");
        temp.add("supervotes");
        for (String s : temp) {
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
          if (!resultSet.next()) {
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
          int supervotes;
          gamesplayed = database.getStat(player.getUniqueId().toString(), "gamesplayed");
          wins = database.getStat(player.getUniqueId().toString(), "wins");
          loses = database.getStat(player.getUniqueId().toString(), "loses");
          highestwin = database.getStat(player.getUniqueId().toString(), "highestwin");
          blocksPlaced = database.getStat(player.getUniqueId().toString(), "blocksplaced");
          blocksBroken = database.getStat(player.getUniqueId().toString(), "blocksbroken");
          particles = database.getStat(player.getUniqueId().toString(), "particles");
          supervotes = database.getStat(player.getUniqueId().toString(), "supervotes");
          User user1 = UserManager.getUser(player.getUniqueId());

          user1.setInt("gamesplayed", gamesplayed);
          user1.setInt("wins", wins);
          user1.setInt("highestwin", highestwin);
          user1.setInt("loses", loses);
          user1.setInt("blocksplaced", blocksPlaced);
          user1.setInt("blocksbroken", blocksBroken);
          user1.setInt("particles", particles);
          user1.setInt("supervotes", supervotes);
        } catch (SQLException e1) {
          System.out.print("CONNECTION FAILED FOR PLAYER " + playername);
        }
      });
    } catch (Exception ex){
      new ReportedException(plugin, ex);
    }
  }

}
