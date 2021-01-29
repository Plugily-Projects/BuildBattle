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

package plugily.projects.buildbattle.user.data;

import org.bukkit.configuration.file.FileConfiguration;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.api.StatsStorage;
import plugily.projects.buildbattle.user.User;

/**
 * Created by Tom on 17/06/2015.
 */
public class FileStats implements UserDatabase {

  private final FileConfiguration config;
  private final Main plugin;

  public FileStats(Main plugin) {
    this.plugin = plugin;
    config = ConfigUtils.getConfig(plugin, "stats");
  }

  @Override
  public void saveStatistic(User user, StatsStorage.StatisticType stat) {
    config.set(user.getPlayer().getUniqueId().toString() + "." + stat.getName(), user.getStat(stat));
    ConfigUtils.saveConfig(plugin, config, "stats");
  }

  @Override
  public void saveAllStatistic(User user) {
    for(StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
      if(!stat.isPersistent()) {
        continue;
      }
      config.set(user.getPlayer().getUniqueId().toString() + "." + stat.getName(), user.getStat(stat));
    }
    ConfigUtils.saveConfig(plugin, config, "stats");
  }

  @Override
  public void loadStatistics(User user) {
    for(StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
      user.setStat(stat, config.getInt(user.getPlayer().getUniqueId().toString() + "." + stat.getName(), 0));
    }
  }
}
