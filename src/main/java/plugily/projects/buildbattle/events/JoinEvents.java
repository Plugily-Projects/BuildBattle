/*
 *
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2021 Plugily Projects - maintained by Tigerpanzer_02, 2Wild4You and contributors
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
 *
 */

package plugily.projects.buildbattle.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import plugily.projects.buildbattle.ConfigPreferences;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.handlers.PermissionManager;
import plugily.projects.buildbattle.utils.UpdateChecker;

/**
 * Created by Tom on 10/07/2015.
 */

public class JoinEvents implements Listener {

  private final Main plugin;

  public JoinEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onLogin(PlayerLoginEvent e) {
    if(!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED) && !plugin.getServer().hasWhitelist()
        || e.getResult() != PlayerLoginEvent.Result.KICK_WHITELIST) {
      return;
    }
    if(e.getPlayer().hasPermission(PermissionManager.getJoinFullGames())) {
      e.setResult(PlayerLoginEvent.Result.ALLOWED);
    }
  }

  @EventHandler
  public void onJoinCheckVersion(PlayerJoinEvent e) {
    //we want to be the first :)
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      if(!e.getPlayer().hasPermission("buildbattle.updatenotify") || !plugin.getConfig().getBoolean("Update-Notifier.Enabled", true)) {
        return;
      }
      UpdateChecker.init(plugin, 44703).requestUpdateCheck().whenComplete((result, exception) -> {
        if(!result.requiresUpdate()) {
          return;
        }
        if(result.getNewestVersion().contains("b")) {
          if(plugin.getConfig().getBoolean("Update-Notifier.Notify-Beta-Versions", true)) {
            e.getPlayer().sendMessage("");
            e.getPlayer().sendMessage(ChatColor.BOLD + "BUILD BATTLE UPDATE NOTIFY");
            e.getPlayer().sendMessage(ChatColor.RED + "BETA version of software is ready for update! Proceed with caution.");
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Current version: " + ChatColor.RED + plugin.getDescription().getVersion() + ChatColor.YELLOW + " Latest version: " + ChatColor.GREEN + result.getNewestVersion());
          }
          return;
        }
        e.getPlayer().sendMessage("");
        e.getPlayer().sendMessage(ChatColor.BOLD + "BUILD BATTLE UPDATE NOTIFY");
        e.getPlayer().sendMessage(ChatColor.GREEN + "Software is ready for update! Download it to keep with latest changes and fixes.");
        e.getPlayer().sendMessage(ChatColor.YELLOW + "Current version: " + ChatColor.RED + plugin.getDescription().getVersion() + ChatColor.YELLOW + " Latest version: " + ChatColor.GREEN + result.getNewestVersion());
      });
    }, 25);
  }

  @EventHandler
  public void onJoinLoadStats(PlayerJoinEvent e) {
    if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED) && !ArenaRegistry.getArenas().isEmpty()) {
      ArenaRegistry.getArenas().get(ArenaRegistry.getBungeeArena()).teleportToLobby(e.getPlayer());
    }
    plugin.getUserManager().loadStatistics(plugin.getUserManager().getUser(e.getPlayer()));
  }

}
