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

package pl.plajer.buildbattle4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import pl.plajer.buildbattle4.arena.Arena;
import pl.plajer.buildbattle4.utils.XMaterial;

/**
 * Created by Tom on 17/08/2015.
 */
public class ConfigPreferences {

  private static FileConfiguration config;
  private static Map<String, List<String>> gameThemes = new HashMap<>();
  private static Map<String, Integer> options = new HashMap<>();
  private static List<String> endGameCommands = new ArrayList<>();
  private static List<String> whitelistedCommands = new ArrayList<>();

  public ConfigPreferences(Main plugin) {
    config = plugin.getConfig();
  }

  private static void loadThemes() {
    gameThemes.put(Arena.ArenaType.SOLO.getPrefix(), config.getStringList("Themes.Classic"));
    gameThemes.put(Arena.ArenaType.TEAM.getPrefix(), config.getStringList("Themes.Teams"));
    gameThemes.put(Arena.ArenaType.GUESS_THE_BUILD.getPrefix() + "_EASY", config.getStringList("Themes.Guess-The-Build.Easy"));
    gameThemes.put(Arena.ArenaType.GUESS_THE_BUILD.getPrefix() + "_MEDIUM", config.getStringList("Themes.Guess-The-Build.Medium"));
    gameThemes.put(Arena.ArenaType.GUESS_THE_BUILD.getPrefix() + "_HARD", config.getStringList("Themes.Guess-The-Build.Hard"));
  }

  public static List<String> getThemes(String accessor) {
    return gameThemes.get(accessor);
  }

  public static boolean isThemeBlacklisted(String theme) {
    for (String s : config.getStringList("Blacklisted-Themes")) {
      if (s.equalsIgnoreCase(theme)) {
        return true;
      }
    }
    return false;
  }

  private static void loadWhitelistedCommands() {
    whitelistedCommands.addAll(config.getStringList("Whitelisted-Commands"));
  }

  public static List<String> getWinCommands(Position pos) {
    return config.getStringList("Win-Commands." + pos.getName());
  }

  public static List<String> getEndGameCommands() {
    return endGameCommands;
  }

  private static void loadEndGameCommands() {
    endGameCommands.addAll(config.getStringList("End-Game-Commands"));
  }

  public static boolean isMobSpawningDisabled() {
    return options.getOrDefault("Disable-Mob-Spawning-Completely", 1) == 1;
  }

  public static Material getDefaultFloorMaterial() {
    return XMaterial.fromString(config.getString("Default-Floor-Material-Name", "LOG").toUpperCase()).parseMaterial();
  }

  public static int getThemeVoteTimer() {
    return options.getOrDefault("Theme-Voting-Time-In-Seconds", 25);
  }

  public static int getLobbyTimer() {
    return options.getOrDefault("Lobby-Starting-Time", 60);
  }

  private static void loadBlackList() {
    for (String item : config.getStringList("Blacklisted-Item-Names")) {
      try {
        Arena.addToBlackList(Material.valueOf(item.toUpperCase()));
      } catch (IllegalArgumentException ex) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildBattle] Invalid black listed item! " + item + " doesn't exist, are you sure it's properly named?");
      }
    }
  }

  public static boolean isBarEnabled() {
    return options.getOrDefault("Boss-Bar-Enabled", 1) == 1;
  }

  public static List<String> getWhitelistedCommands() {
    return whitelistedCommands;
  }

  public static boolean isHidePlayersOutsideGameEnabled() {
    return options.getOrDefault("Hide-Players-Outside-Game", 1) == 1;
  }

  public static int getAmountFromOneParticle() {
    return options.getOrDefault("Amount-One-Particle-Effect-Contains", 20);
  }

  public static int getMaxParticles() {
    return options.getOrDefault("Max-Amount-Particles", 25);
  }

  public static int getVotingTime() {
    return options.getOrDefault("Voting-Time-In-Seconds", 20);
  }

  public static int getBuildTime(Arena arena) {
    return config.getInt("Build-Time." + arena.getArenaType().getPrefix(), 100);
  }

  public static boolean getBungeeShutdown() {
    return options.getOrDefault("Bungee-Shutdown-On-End", 0) == 1;
  }

  public static int getMaxMobs() {
    return options.getOrDefault("Mobs-Max-Amount-Per-Plot", 20);
  }

  public static boolean isWinCommandsEnabled() {
    return options.getOrDefault("Win-Commands-Activated", 0) == 1;
  }

  public static boolean isSecondPlaceCommandsEnabled() {
    return options.getOrDefault("Second-Place-Commands-Activated", 0) == 1;
  }

  public static boolean isThirdPlaceCommandsEnabled() {
    return options.getOrDefault("Third-Place-Commands-Activated", 0) == 1;
  }

  public static boolean isEndGameCommandsEnabled() {
    return options.getOrDefault("End-Game-Commands-Activated", 0) == 1;
  }

  public static long getParticleRefreshTick() {
    return options.getOrDefault("Particle-Refresh-Per-Tick", 10);
  }

  public static void loadOptions() {
    List<String> loadOptions = new ArrayList<>();
    loadOptions.add("Voting-Time-In-Seconds");
    loadOptions.add("Boss-Bar-Enabled");
    loadOptions.add("Fly-Range-Out-Plot");
    loadOptions.add("Default-Floor-Material-Name");
    loadOptions.add("Disable-Mob-Spawning-Completely");
    loadOptions.add("Amount-One-Particle-Effect-Contains");
    loadOptions.add("Max-Amount-Particles");
    loadOptions.add("Particle-Refresh-Per-Tick");
    loadOptions.add("Bungee-Shutdown-On-End");
    loadOptions.add("Win-Commands-Activated");
    loadOptions.add("End-Game-Commands-Activated");
    loadOptions.add("Second-Place-Commands-Activated");
    loadOptions.add("Third-Place-Commands-Activated");
    loadOptions.add("Mobs-Max-Amount-Per-Plot");
    loadOptions.add("Hide-Players-Outside-Game");
    loadOptions.add("Lobby-Starting-Time");
    loadOptions.add("Theme-Voting-Time-In-Seconds");

    for (String option : loadOptions) {
      if (config.contains(option)) {
        if (config.isBoolean(option)) {
          boolean b = config.getBoolean(option);
          if (b) {
            options.put(option, 1);
          } else {
            options.put(option, 0);
          }
        } else {
          options.put(option, config.getInt(option));
        }
      }
    }
    loadThemes();
    loadBlackList();
    loadEndGameCommands();
    loadWhitelistedCommands();
  }

  public enum Position {
    FIRST("First"), SECOND("Second"), THIRD("Third");

    private String name;

    Position(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

}
