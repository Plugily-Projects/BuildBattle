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

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import plugily.projects.buildbattle.ConfigPreferences;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.ArenaManager;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.user.User;

/**
 * @author Plajer
 * <p>
 * Created at 29.04.2018
 */
public class QuitEvents implements Listener {

  private final Main plugin;

  public QuitEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    onQuit(e.getPlayer());
  }

  @EventHandler
  public void onKick(PlayerKickEvent e) {
    onQuit(e.getPlayer());
  }

  private void onQuit(Player player) {
    BaseArena arena = ArenaRegistry.getArena(player);
    if (arena != null && !plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
      ArenaManager.leaveAttempt(player, arena);
    }

    User user = plugin.getUserManager().getUser(player);
    plugin.getUserManager().saveAllStatistic(user);
    plugin.getUserManager().removeUser(user);
  }

}
