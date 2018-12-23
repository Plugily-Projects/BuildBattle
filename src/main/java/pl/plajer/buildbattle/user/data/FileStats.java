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

package pl.plajer.buildbattle.user.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.user.User;
import pl.plajerlair.core.utils.ConfigUtils;

/**
 * Created by Tom on 17/06/2015.
 */
public class FileStats {

  private FileConfiguration config;
  private Main plugin;

  public FileStats(Main plugin) {
    this.plugin = plugin;
    config = ConfigUtils.getConfig(JavaPlugin.getPlugin(Main.class), "stats");
  }

  public void saveStat(User user, StatsStorage.StatisticType stat) {
    config.set(user.toPlayer().getUniqueId().toString() + "." + stat.getName(), user.getStat(stat));
    ConfigUtils.saveConfig(plugin, config, "stats");
  }

  public void loadStat(User user, StatsStorage.StatisticType stat) {
    user.setStat(stat, config.getInt(user.toPlayer().getUniqueId().toString() + "." + stat.getName(), 0));
  }


}
