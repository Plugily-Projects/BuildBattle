/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.buildbattle.user;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.plajer.buildbattle.ConfigPreferences;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.user.data.FileStats;
import pl.plajer.buildbattle.user.data.MySQLManager;
import pl.plajerlair.core.debug.Debugger;
import pl.plajerlair.core.debug.LogLevel;

/**
 * Created by Tom on 27/07/2014.
 */
@Deprecated //switch uuids to players
public class UserManager {

  private FileStats fileStats;
  private MySQLManager mySQLManager;
  private Main plugin;
  private HashMap<UUID, User> users = new HashMap<>();

  public UserManager(Main plugin) {
    this.plugin = plugin;
    if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
      mySQLManager = new MySQLManager(plugin);
    } else {
      fileStats = new FileStats(plugin);
    }
    loadStatsForPlayersOnline();
  }

  private void loadStatsForPlayersOnline() {
    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
      if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
        ArenaRegistry.getArenas().get(0).teleportToLobby(player);
      }
      User user = getUser(player.getUniqueId());
      for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
        loadStatistic(user, stat);
      }
    }
  }

  @Deprecated
  public User getUser(UUID uuid) {
    if (users.containsKey(uuid)) {
      return users.get(uuid);
    } else {
      Debugger.debug(LogLevel.INFO, "Registering new user with UUID: " + uuid);
      users.put(uuid, new User(uuid));
      return users.get(uuid);
    }
  }

  /**
   * Saves player statistic into yaml or MySQL storage based on user choice
   *
   * @param user user to retrieve statistic from
   * @param stat stat to save to storage
   */
  public void saveStatistic(User user, StatsStorage.StatisticType stat) {
    if (!stat.isPersistent()) {
      return;
    }
    Player player = user.toPlayer().getPlayer();
    if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
        mySQLManager.saveStat(user, player, stat);
      });
      return;
    }
    fileStats.saveStat(user, stat);
  }

  /**
   * Loads player statistic from yaml or MySQL storage based on user choice
   *
   * @param user user to load statistic for
   * @param stat type of stat to load from storage
   */
  public void loadStatistic(User user, StatsStorage.StatisticType stat) {
    if (!stat.isPersistent()) {
      return;
    }
    if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> user.setStat(stat, mySQLManager.getStat(user, stat)));
      return;
    }
    fileStats.loadStat(user, stat);
  }

  @Deprecated
  public void removeUser(UUID uuid) {
    users.remove(uuid);
  }

}
