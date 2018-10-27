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

package pl.plajer.buildbattle.events;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.database.MySQLManager;
import pl.plajer.buildbattle.user.User;
import pl.plajer.buildbattle.user.UserManager;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.services.update.UpdateChecker;

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
    if (plugin.isBungeeActivated()) {
      return;
    }
    for (Player player : plugin.getServer().getOnlinePlayers()) {
      if (ArenaRegistry.getArena(player) == null) {
        continue;
      }
      player.hidePlayer(event.getPlayer());
      event.getPlayer().hidePlayer(player);
    }
  }

  @EventHandler
  public void onJoinCheckVersion(final PlayerJoinEvent event) {
    try {
      //we want to be the first :)
      Bukkit.getScheduler().runTaskLater(plugin, () -> {
        if (event.getPlayer().hasPermission("buildbattle.updatenotify")) {
          if (plugin.getConfig().getBoolean("Update-Notifier.Enabled", true)) {
            UpdateChecker.init(plugin, 44703).requestUpdateCheck().whenComplete((result, exception) -> {
              if (result.requiresUpdate()) {
                if (result.getNewestVersion().contains("b")) {
                  if (plugin.getConfig().getBoolean("Update-Notifier.Notify-Beta-Versions", true)) {
                    event.getPlayer().sendMessage("");
                    event.getPlayer().sendMessage(ChatColor.BOLD + "BUILD BATTLE UPDATE NOTIFY");
                    event.getPlayer().sendMessage(ChatColor.RED + "BETA version of software is ready for update! Proceed with caution.");
                    event.getPlayer().sendMessage(ChatColor.YELLOW + "Current version: " + ChatColor.RED + plugin.getDescription().getVersion() + ChatColor.YELLOW + " Latest version: " + ChatColor.GREEN + result.getNewestVersion());
                  }
                  return;
                }
                event.getPlayer().sendMessage("");
                event.getPlayer().sendMessage(ChatColor.BOLD + "BUILD BATTLE UPDATE NOTIFY");
                event.getPlayer().sendMessage(ChatColor.GREEN + "Software is ready for update! Download it to keep with latest changes and fixes.");
                event.getPlayer().sendMessage(ChatColor.YELLOW + "Current version: " + ChatColor.RED + plugin.getDescription().getVersion() + ChatColor.YELLOW + " Latest version: " + ChatColor.GREEN + result.getNewestVersion());
              }
            });
          }
        }
      }, 25);
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onJoinLoadStats(final PlayerJoinEvent event) {
    try {
      if (plugin.isBungeeActivated() && ArenaRegistry.getArenas().size() >= 1) {
        ArenaRegistry.getArenas().get(0).teleportToLobby(event.getPlayer());
      }
      if (!plugin.isDatabaseActivated()) {
        for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
          plugin.getFileStats().loadStat(event.getPlayer(), stat);
        }
        return;
      }
      final Player player = event.getPlayer();

      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
        MySQLManager database = plugin.getMySQLManager();
        ResultSet resultSet = plugin.getMySQLDatabase().executeQuery("SELECT UUID from buildbattlestats WHERE UUID='" + player.getUniqueId().toString() + "'");
        try {
          if (!resultSet.next()) {
            database.insertPlayer(player);
            return;
          }
          User user = UserManager.getUser(player.getUniqueId());
          for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
            user.setStat(stat, plugin.getMySQLManager().getStat(player, stat));
          }
        } catch (SQLException e1) {
          System.out.print("CONNECTION FAILED FOR PLAYER " + player.getName());
        }
      });
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

}
