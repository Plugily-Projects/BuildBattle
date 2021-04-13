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

package plugily.projects.buildbattle.user;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import plugily.projects.buildbattle.ConfigPreferences;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.api.StatsStorage;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.user.data.FileStats;
import plugily.projects.buildbattle.user.data.MysqlManager;
import plugily.projects.buildbattle.user.data.UserDatabase;
import plugily.projects.buildbattle.utils.Debugger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 27/07/2014.
 */
public class UserManager {

  private final UserDatabase database;
  private final Main plugin;
  private final List<User> users = new ArrayList<>();

  public UserManager(Main plugin) {
    this.plugin = plugin;
    if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
      database = new MysqlManager(plugin);
    } else {
      database = new FileStats(plugin);
    }
    loadStatsForPlayersOnline();
  }

  private void loadStatsForPlayersOnline() {
    for(Player player : Bukkit.getServer().getOnlinePlayers()) {
      if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
        ArenaRegistry.getArenas().get(ArenaRegistry.getBungeeArena()).teleportToLobby(player);
      }
      loadStatistics(getUser(player));
    }
  }

  public User getUser(Player player) {
    for(User user : users) {
      if(user.getPlayer().equals(player)) {
        return user;
      }
    }
    Debugger.debug("Registering new user with UUID: " + player.getUniqueId() + " (" + player.getName() + ")");
    User user = new User(player);
    users.add(user);
    return user;
  }

  public void saveStatistic(User user, StatsStorage.StatisticType stat) {
    if(stat.isPersistent()) {
      database.saveStatistic(user, stat);
    }
  }

  public void saveAllStatistic(User user) {
    database.saveAllStatistic(user);
  }

  public void loadStatistics(User user) {
    database.loadStatistics(user);
  }

  public void removeUser(User user) {
    users.remove(user);
  }

  public UserDatabase getDatabase() {
    return database;
  }
}
