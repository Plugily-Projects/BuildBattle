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

package pl.plajer.buildbattle3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import pl.plajer.buildbattle3.arena.Arena;

/**
 * Created by Tom on 17/08/2015.
 */
public class ConfigPreferences {

  private static FileConfiguration config;
  private static List<String> themes = new ArrayList<>();
  private static Map<String, Integer> options = new HashMap<>();
  private static List<String> winCommands = new ArrayList<>();
  private static List<String> endGameCommands = new ArrayList<>();
  private static List<String> secondPlaceCommands = new ArrayList<>();
  private static List<String> whitelistedCommands = new ArrayList<>();
  private static List<String> thirdPlaceCommands = new ArrayList<>();

  public ConfigPreferences(Main plugin) {
    config = plugin.getConfig();
  }

  public static void loadThemes() {
    themes.addAll(config.getStringList("Game-Themes"));
  }

  public static List<String> getThemes() {
    return themes;
  }

  public static void loadWinCommands() {
    winCommands.addAll(config.getStringList("Win-Commands"));
  }

  public static void loadWhitelistedCommands() {
    whitelistedCommands.addAll(config.getStringList("Whitelisted-Commands"));
  }

  public static void loadThirdPlaceCommands() {
    thirdPlaceCommands.addAll(config.getStringList("Third-Place-Commands"));
  }

  public static void loadSecondPlaceCommands() {
    secondPlaceCommands.addAll(config.getStringList("Second-Place-Commands"));
  }

  public static List<String> getSecondPlaceCommands() {
    return secondPlaceCommands;
  }

  public static List<String> getThirdPlaceCommands() {
    return thirdPlaceCommands;
  }

  public static List<String> getWinCommands() {
    return winCommands;
  }

  public static List<String> getEndGameCommands() {
    return endGameCommands;
  }


  public static void loadEndGameCommands() {
    endGameCommands.addAll(config.getStringList("End-Game-Commands"));
  }

  public static boolean isMobSpawningDisabled() {
    return options.getOrDefault("Disable-Mob-Spawning-Completely", 1) == 1;
  }

  public static Material getDefaultFloorMaterial() {
    return Material.getMaterial(config.getString("Default-Floor-Material-Name", "LOG").toUpperCase());
  }

  public static int getThemeVoteTimer() {
    return options.getOrDefault("Theme-Voting-Time-In-Seconds", 25);
  }

  public static int getLobbyTimer() {
    return options.getOrDefault("Lobby-Starting-Time", 60);
  }

  public static void loadBlackList() {
    for (String item : config.getStringList("Blacklisted-Item-Names")) {
      try {
        Arena.addToBlackList(Material.valueOf(item.toUpperCase()));
      } catch (IllegalArgumentException ex){
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

  public static int getTeamBuildTime() {
    return options.getOrDefault("Team-Build-Time-In-Seconds", 540);
  }

  public static int getBuildTime() {
    return options.getOrDefault("Build-Time-In-Seconds", 480);
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
    return options.getOrDefault("End-Game-Commands-Activated", 1) == 1;
  }

  public static long getParticleRefreshTick() {
    return options.getOrDefault("Particle-Refresh-Per-Tick", 10);
  }

  public static void loadOptions() {
    List<String> loadOptions = new ArrayList<>();
    loadOptions.add("Build-Time-In-Seconds");
    loadOptions.add("Team-Build-Time-In-Seconds");
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
    loadWinCommands();
    loadSecondPlaceCommands();
    loadThirdPlaceCommands();
    loadEndGameCommands();
    loadWhitelistedCommands();
  }

}
