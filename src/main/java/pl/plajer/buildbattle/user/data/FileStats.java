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

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.user.User;
import pl.plajer.buildbattle.utils.MessageUtils;
import pl.plajerlair.core.utils.ConfigUtils;

/**
 * Created by Tom on 17/06/2015.
 */
public class FileStats {

  private FileConfiguration config;

  public FileStats() {
    config = ConfigUtils.getConfig(JavaPlugin.getPlugin(Main.class), "stats");
  }

  public void saveStat(User user, StatsStorage.StatisticType stat) {
    if (!stat.isPersistent()) {
      return;
    }
    config.set(user.toPlayer().getUniqueId().toString() + "." + stat.getName(), user.getStat(stat));
    try {
      config.save(ConfigUtils.getFile(JavaPlugin.getPlugin(Main.class), "stats"));
    } catch (IOException e) {
      e.printStackTrace();
      MessageUtils.errorOccurred();
      Bukkit.getConsoleSender().sendMessage("Cannot save stats.yml file!");
      Bukkit.getConsoleSender().sendMessage("Restart the server, file COULD BE OVERRIDDEN!");
    }
  }

  public void loadStat(User user, StatsStorage.StatisticType stat) {
    user.setStat(stat, config.getInt(user.toPlayer().getUniqueId().toString() + "." + stat.getName(), 0));
  }


}
