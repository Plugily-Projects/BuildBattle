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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.arena.Arena;
import pl.plajer.buildbattle3.arena.ArenaManager;
import pl.plajer.buildbattle3.arena.ArenaRegistry;
import pl.plajer.buildbattle3.buildbattleapi.StatsStorage;
import pl.plajer.buildbattle3.user.User;
import pl.plajer.buildbattle3.user.UserManager;
import pl.plajerlair.core.services.exception.ReportedException;

/**
 * @author Plajer
 * <p>
 * Created at 29.04.2018
 */
public class QuitEvents implements Listener {

  private Main plugin;

  public QuitEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    Arena a = ArenaRegistry.getArena(event.getPlayer());
    if (a == null) return;
    if (!plugin.isBungeeActivated())
      ArenaManager.leaveAttempt(event.getPlayer(), a);
  }

  @EventHandler
  public void onQuitSaveStats(PlayerQuitEvent event) {
    try {
      Arena a = ArenaRegistry.getArena(event.getPlayer());
      if (a != null) {
        ArenaManager.leaveAttempt(event.getPlayer(), a);
      }
      final User user = UserManager.getUser(event.getPlayer().getUniqueId());
      final Player player = event.getPlayer();

      if (plugin.isDatabaseActivated()) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
          for (StatsStorage.StatisticType s : StatsStorage.StatisticType.values()) {
            int i;
            try {
              i = plugin.getMySQLManager().getStat(player.getUniqueId().toString(), s.getName());
            } catch (NullPointerException npe) {
              i = 0;
              System.out.print("COULDN'T GET STATS FROM PLAYER: " + player.getName());
            }

            if (i > user.getInt(s.getName())) {
              plugin.getMySQLManager().setStat(player.getUniqueId().toString(), s.getName(), user.getInt(s.getName()) + i);
            } else {
              plugin.getMySQLManager().setStat(player.getUniqueId().toString(), s.getName(), user.getInt(s.getName()));
            }
          }
        });
      } else {
        for (StatsStorage.StatisticType s : StatsStorage.StatisticType.values()) {
          plugin.getFileStats().saveStat(player, s.getName());
        }
      }
      UserManager.removeUser(event.getPlayer().getUniqueId());
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

}
