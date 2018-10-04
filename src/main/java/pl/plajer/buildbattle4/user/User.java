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

package pl.plajer.buildbattle4.user;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;

import pl.plajer.buildbattle4.Main;
import pl.plajer.buildbattle4.api.event.player.BBPlayerStatisticChangeEvent;
import pl.plajer.buildbattle4.arena.Arena;
import pl.plajer.buildbattle4.arena.ArenaRegistry;
import pl.plajer.buildbattle4.database.FileStats;

/**
 * Created by Tom on 27/07/2014.
 */
public class User {

  private static Main plugin = JavaPlugin.getPlugin(Main.class);
  private ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
  private UUID uuid;
  private Map<String, Integer> stats = new HashMap<>();
  @Deprecated
  private Map<String, Object> objects = new HashMap<>();

  public User(UUID uuid) {
    this.uuid = uuid;
  }

  @Deprecated
  public Object getObject(String s) {
    if (objects.containsKey(s)) return objects.get(s);
    return null;
  }

  @Deprecated
  public void setObject(Object object, String s) {
    objects.put(s, object);
  }

  public Player toPlayer() {
    return Bukkit.getServer().getPlayer(uuid);
  }

  public Arena getArena() {
    return ArenaRegistry.getArena(Bukkit.getPlayer(uuid));
  }

  @Deprecated
  public int getInt(String s) {
    if (!stats.containsKey(s)) {
      stats.put(s, 0);
      return 0;
    } else if (stats.get(s) == null) {
      return 0;
    }

    return stats.get(s);
  }

  public void removeScoreboard() {
    this.toPlayer().setScoreboard(scoreboardManager.getNewScoreboard());
  }

  @Deprecated
  public void setInt(String s, int i) {
    stats.put(s, i);

    Bukkit.getScheduler().runTask(plugin, () -> {
      BBPlayerStatisticChangeEvent bbPlayerStatisticChangeEvent = new BBPlayerStatisticChangeEvent(getArena(), toPlayer(), FileStats.STATISTICS.get(s), i);
      Bukkit.getPluginManager().callEvent(bbPlayerStatisticChangeEvent);
    });
  }

  @Deprecated
  public void addInt(String s, int i) {
    stats.put(s, getInt(s) + i);

    Bukkit.getScheduler().runTask(plugin, () -> {
      BBPlayerStatisticChangeEvent bbPlayerStatisticChangeEvent = new BBPlayerStatisticChangeEvent(getArena(), toPlayer(), FileStats.STATISTICS.get(s), getInt(s));
      Bukkit.getPluginManager().callEvent(bbPlayerStatisticChangeEvent);
    });
  }

}
