/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and contributors
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

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import pl.plajer.buildbattle.ConfigPreferences;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.arena.ArenaManager;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.impl.BaseArena;
import pl.plajer.buildbattle.user.User;

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
  public void onQuit(PlayerQuitEvent e) {
    BaseArena arena = ArenaRegistry.getArena(e.getPlayer());
    if (arena == null) {
      return;
    }
    if (!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
      ArenaManager.leaveAttempt(e.getPlayer(), arena);
    }
  }

  @EventHandler
  public void onQuitSaveStats(PlayerQuitEvent e) {
    User user = plugin.getUserManager().getUser(e.getPlayer());
      for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
        plugin.getUserManager().saveStatistic(user, stat);
      }
      plugin.getUserManager().removeUser(user);
  }

}
