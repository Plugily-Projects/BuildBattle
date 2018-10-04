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

package pl.plajer.buildbattle4.database;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle4.Main;
import pl.plajer.buildbattle4.buildbattleapi.StatsStorage;
import pl.plajer.buildbattle4.user.User;
import pl.plajer.buildbattle4.user.UserManager;
import pl.plajer.buildbattle4.utils.MessageUtils;
import pl.plajerlair.core.utils.ConfigUtils;

/**
 * Created by Tom on 17/06/2015.
 */
public class FileStats {

  public final static Map<String, StatsStorage.StatisticType> STATISTICS = new HashMap<>();

  static {
    STATISTICS.put("gamesplayed", StatsStorage.StatisticType.GAMES_PLAYED);
    STATISTICS.put("wins", StatsStorage.StatisticType.WINS);
    STATISTICS.put("highestwin", StatsStorage.StatisticType.HIGHEST_WIN);
    STATISTICS.put("loses", StatsStorage.StatisticType.LOSES);
    STATISTICS.put("particles", StatsStorage.StatisticType.PARTICLES_USED);
    STATISTICS.put("blocksbroken", StatsStorage.StatisticType.BLOCKS_BROKEN);
    STATISTICS.put("blocksplaced", StatsStorage.StatisticType.BLOCKS_PLACED);
    STATISTICS.put("supervotes", StatsStorage.StatisticType.SUPER_VOTES);
  }

  private FileConfiguration config;

  public FileStats() {
    config = ConfigUtils.getConfig(JavaPlugin.getPlugin(Main.class), "stats");
  }

  public void saveStat(Player player, String stat) {
    User user = UserManager.getUser(player.getUniqueId());
    config.set(player.getUniqueId().toString() + "." + stat, user.getInt(stat));
    try {
      config.save(ConfigUtils.getFile(JavaPlugin.getPlugin(Main.class), "stats"));
    } catch (IOException e) {
      e.printStackTrace();
      MessageUtils.errorOccured();
      Bukkit.getConsoleSender().sendMessage("Cannot save stats.yml file!");
      Bukkit.getConsoleSender().sendMessage("Restart the server, file COULD BE OVERRIDDEN!");
    }
  }

  public void loadStat(Player player, String stat) {
    User user = UserManager.getUser(player.getUniqueId());
    if (config.contains(player.getUniqueId().toString() + "." + stat))
      user.setInt(stat, config.getInt(player.getUniqueId().toString() + "." + stat));
    else user.setInt(stat, 0);
  }


}
