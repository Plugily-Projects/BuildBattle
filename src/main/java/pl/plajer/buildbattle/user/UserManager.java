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

package pl.plajer.buildbattle.user;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.user.data.FileStats;
import pl.plajer.buildbattle.user.data.MySQLManager;

/**
 * Created by Tom on 27/07/2014.
 */
public class UserManager {

  private FileStats fileStats;
  private MySQLManager mySQLManager;
  private Main plugin;
  private HashMap<UUID, User> users = new HashMap<>();

  public UserManager(Main plugin) {
    this.plugin = plugin;
    if (plugin.isDatabaseActivated()) {
      mySQLManager = new MySQLManager(plugin);
    } else {
      fileStats = new FileStats();
    }
  }

  public User getUser(UUID uuid) {
    if (users.containsKey(uuid)) {
      return users.get(uuid);
    } else {
      Main.debug(Main.LogLevel.INFO, "Registering new user with UUID: " + uuid);
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
    if (plugin.isDatabaseActivated()) {
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> mySQLManager.saveStat(user, stat));
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
    if (plugin.isDatabaseActivated()) {
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> user.setStat(stat, mySQLManager.getStat(user, stat)));
      return;
    }
    fileStats.loadStat(user, stat);
  }

  public void removeUser(UUID uuid) {
    users.remove(uuid);
  }
}
