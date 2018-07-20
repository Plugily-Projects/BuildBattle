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
    return options.get("Disable-Mob-Spawning-Completely") == 1;
  }

  public static int getDefaultFloorMaterial() {
    return options.get("Default-Floor-Material");
  }

  public static int getThemeVoteTimer() {
    return options.get("Theme-Voting-Time-In-Seconds");
  }

  public static int getLobbyTimer() {
    return options.get("Lobby-Starting-Time");
  }

  public static void loadBlackList() {
    for (int ID : config.getIntegerList("Blacklisted-Items")) {
      Arena.addToBlackList(ID);
    }
  }

  public static boolean isBarEnabled() {
    return options.get("Boss-Bar-Enabled") == 1;
  }

  public static List<String> getWhitelistedCommands() {
    return whitelistedCommands;
  }

  public static boolean isHidePlayersOutsideGameEnabled() {
    return options.get("Hide-Players-Outside-Game") == 1;
  }

  public static int getAmountFromOneParticle() {
    return options.get("Amount-One-Particle-Effect-Contains");
  }

  public static int getMaxParticles() {
    return options.get("Max-Amount-Particles");
  }

  public static int getVotingTime() {
    return options.get("Voting-Time-In-Seconds");
  }

  public static boolean isScoreboardDisabled() {
    return options.get("Disable-Scoreboard-Ingame") == 1;
  }

  public static int getTeamBuildTime() {
    return options.get("Team-Build-Time-In-Seconds");
  }

  public static int getBuildTime() {
    return options.get("Build-Time-In-Seconds");
  }

  public static boolean getBungeeShutdown() {
    return options.get("Bungee-Shutdown-On-End") == 1;
  }

  public static int getParticlOffset() {
    return options.get("Particle-Offset");
  }

  public static int getMaxMobs() {
    return options.get("Mobs-Max-Amount-Per-Plot");
  }

  public static boolean isWinCommandsEnabled() {
    return options.get("Win-Commands-Activated") == 1;
  }

  public static boolean isNameUsedInDatabase() {
    return options.get("Use-Name-Instead-Of-UUID-In-Database") == 1;
  }

  public static boolean isSecondPlaceCommandsEnabled() {
    return options.get("Second-Place-Commands-Activated") == 1;
  }

  public static boolean isThirdPlaceCommandsEnabled() {
    return options.get("Third-Place-Commands-Activated") == 1;
  }

  public static boolean isEndGameCommandsEnabled() {
    return options.get("End-Game-Commands-Activated") == 1;
  }

  public static void loadOptions() {
    List<String> loadOptions = new ArrayList<>();
    loadOptions.add("Build-Time-In-Seconds");
    loadOptions.add("Team-Build-Time-In-Seconds");
    loadOptions.add("Voting-Time-In-Seconds");
    loadOptions.add("Boss-Bar-Enabled");
    loadOptions.add("Fly-Range-Out-Plot");
    loadOptions.add("Default-Floor-Material");
    loadOptions.add("Disable-Mob-Spawning-Completely");
    loadOptions.add("Amount-One-Particle-Effect-Contains");
    loadOptions.add("Max-Amount-Particles");
    loadOptions.add("Particle-Refresh-Per-Tick");
    loadOptions.add("Bungee-Shutdown-On-End");
    loadOptions.add("Particle-Offset");
    loadOptions.add("Win-Commands-Activated");
    loadOptions.add("End-Game-Commands-Activated");
    loadOptions.add("Second-Place-Commands-Activated");
    loadOptions.add("Third-Place-Commands-Activated");
    loadOptions.add("Use-Name-Instead-Of-UUID-In-Database");
    loadOptions.add("Mobs-Max-Amount-Per-Plot");
    loadOptions.add("Hide-Players-Outside-Game");
    loadOptions.add("Disable-Scoreboard-Ingame");
    loadOptions.add("Hook-Into-Vault");
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
  }

  public static boolean isVaultEnabled() {
    return options.get("Hook-Into-Vault") == 1;
  }

  public static long getParticleRefreshTick() {
    return options.get("Particle-Refresh-Per-Tick");
  }

}
