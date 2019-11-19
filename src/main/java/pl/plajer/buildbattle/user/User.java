/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Tigerpanzer_02, Plajer and contributors
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

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.api.event.player.BBPlayerStatisticChangeEvent;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.impl.BaseArena;
import pl.plajer.buildbattle.arena.managers.plots.Plot;

/**
 * Created by Tom on 27/07/2014.
 */
public class User {

  private static Main plugin = JavaPlugin.getPlugin(Main.class);
  private Player player;
  private Map<StatsStorage.StatisticType, Integer> stats = new EnumMap<>(StatsStorage.StatisticType.class);
  private Plot currentPlot;

  public User(Player player) {
    this.player = player;
  }

  public Plot getCurrentPlot() {
    return currentPlot;
  }

  public void setCurrentPlot(Plot currentPlot) {
    this.currentPlot = currentPlot;
  }

  public Player getPlayer() {
    return player;
  }

  public BaseArena getArena() {
    return ArenaRegistry.getArena(player);
  }

  public int getStat(StatsStorage.StatisticType stat) {
    if (!stats.containsKey(stat)) {
      stats.put(stat, 0);
      return 0;
    } else if (stats.get(stat) == null) {
      return 0;
    }

    return stats.get(stat);
  }

  public void setStat(StatsStorage.StatisticType stat, int i) {
    stats.put(stat, i);

    Bukkit.getScheduler().runTask(plugin, () -> {
      BBPlayerStatisticChangeEvent event = new BBPlayerStatisticChangeEvent(getArena(), player, stat, i);
      Bukkit.getPluginManager().callEvent(event);
    });
  }

  public void addStat(StatsStorage.StatisticType stat, int i) {
    stats.put(stat, getStat(stat) + i);

    Bukkit.getScheduler().runTask(plugin, () -> {
      BBPlayerStatisticChangeEvent event = new BBPlayerStatisticChangeEvent(getArena(), player, stat, getStat(stat));
      Bukkit.getPluginManager().callEvent(event);
    });
  }

}
