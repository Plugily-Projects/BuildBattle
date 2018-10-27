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
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.api.event.player.BBPlayerStatisticChangeEvent;
import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.plots.ArenaPlot;

/**
 * Created by Tom on 27/07/2014.
 */
public class User {

  private static Main plugin = JavaPlugin.getPlugin(Main.class);
  private ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
  private UUID uuid;
  private Map<StatsStorage.StatisticType, Integer> stats = new HashMap<>();
  private ArenaPlot currentPlot;

  public User(UUID uuid) {
    this.uuid = uuid;
  }

  public ArenaPlot getCurrentPlot() {
    return currentPlot;
  }

  public void setCurrentPlot(ArenaPlot currentPlot) {
    this.currentPlot = currentPlot;
  }

  public Player toPlayer() {
    return Bukkit.getServer().getPlayer(uuid);
  }

  public Arena getArena() {
    return ArenaRegistry.getArena(Bukkit.getPlayer(uuid));
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

  public void removeScoreboard() {
    this.toPlayer().setScoreboard(scoreboardManager.getNewScoreboard());
  }

  public void setStat(StatsStorage.StatisticType stat, int i) {
    stats.put(stat, i);

    Bukkit.getScheduler().runTask(plugin, () -> {
      BBPlayerStatisticChangeEvent bbPlayerStatisticChangeEvent = new BBPlayerStatisticChangeEvent(getArena(), toPlayer(), stat, i);
      Bukkit.getPluginManager().callEvent(bbPlayerStatisticChangeEvent);
    });
  }

  public void addStat(StatsStorage.StatisticType stat, int i) {
    stats.put(stat, getStat(stat) + i);

    Bukkit.getScheduler().runTask(plugin, () -> {
      BBPlayerStatisticChangeEvent bbPlayerStatisticChangeEvent = new BBPlayerStatisticChangeEvent(getArena(), toPlayer(), stat, getStat(stat));
      Bukkit.getPluginManager().callEvent(bbPlayerStatisticChangeEvent);
    });
  }

}
