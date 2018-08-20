/*
 * BuildBattle 3 - Ultimate building competition minigame
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
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

package pl.plajer.buildbattle3.user;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;

import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.arena.Arena;
import pl.plajer.buildbattle3.arena.ArenaRegistry;
import pl.plajer.buildbattle3.buildbattleapi.BBPlayerStatisticChangeEvent;
import pl.plajer.buildbattle3.stats.FileStats;

/**
 * Created by Tom on 27/07/2014.
 */
public class User {

  private static Main plugin = JavaPlugin.getPlugin(Main.class);
  private ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
  private UUID uuid;
  private Map<String, Integer> ints = new HashMap<>();
  private Map<String, Object> objects = new HashMap<>();

  public User(UUID uuid) {
    this.uuid = uuid;
  }

  public Object getObject(String s) {
    if (objects.containsKey(s)) return objects.get(s);
    return null;
  }

  public void setObject(Object object, String s) {
    objects.put(s, object);
  }

  public Player toPlayer() {
    return Bukkit.getServer().getPlayer(uuid);
  }

  public Arena getArena() {
    return ArenaRegistry.getArena(Bukkit.getPlayer(uuid));
  }

  public int getInt(String s) {
    if (!ints.containsKey(s)) {
      ints.put(s, 0);
      return 0;
    } else if (ints.get(s) == null) {
      return 0;
    }

    return ints.get(s);
  }

  public void removeScoreboard() {
    this.toPlayer().setScoreboard(scoreboardManager.getNewScoreboard());
  }

  public void setInt(String s, int i) {
    ints.put(s, i);

    Bukkit.getScheduler().runTask(plugin, () -> {
      BBPlayerStatisticChangeEvent bbPlayerStatisticChangeEvent = new BBPlayerStatisticChangeEvent(getArena(), toPlayer(), FileStats.STATISTICS.get(s), i);
      Bukkit.getPluginManager().callEvent(bbPlayerStatisticChangeEvent);
    });
  }

  public void addInt(String s, int i) {
    ints.put(s, getInt(s) + i);

    Bukkit.getScheduler().runTask(plugin, () -> {
      BBPlayerStatisticChangeEvent bbPlayerStatisticChangeEvent = new BBPlayerStatisticChangeEvent(getArena(), toPlayer(), FileStats.STATISTICS.get(s), getInt(s));
      Bukkit.getPluginManager().callEvent(bbPlayerStatisticChangeEvent);
    });
  }

}
