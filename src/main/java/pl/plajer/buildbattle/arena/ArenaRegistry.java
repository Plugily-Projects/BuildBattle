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

package pl.plajer.buildbattle.arena;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle.ConfigPreferences;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.arena.impl.BaseArena;
import pl.plajer.buildbattle.arena.impl.GuessTheBuildArena;
import pl.plajer.buildbattle.arena.impl.SoloArena;
import pl.plajer.buildbattle.arena.impl.TeamArena;
import pl.plajer.buildbattle.arena.managers.plots.Plot;
import pl.plajer.buildbattle.utils.Debugger;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.dimensional.Cuboid;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;

/**
 * Created by Tom on 27/07/2014.
 */
public class ArenaRegistry {

  private static List<BaseArena> arenas = new ArrayList<>();
  private static Main plugin = JavaPlugin.getPlugin(Main.class);

  private static int bungeeArena = -999;

  public static List<BaseArena> getArenas() {
    return arenas;
  }

  /**
   * Returns arena where the player is
   *
   * @param p target player
   * @return Arena or null if not playing
   */
  @Nullable
  public static BaseArena getArena(Player p) {
    if (p == null) {
      return null;
    }
    if (!p.isOnline()) {
      return null;
    }

    for (BaseArena arena : arenas) {
      for (Player player : arena.getPlayers()) {
        if (player.equals(p)) {
          return arena;
        }
      }
    }
    return null;
  }

  public static void registerArena(BaseArena arena) {
    Debugger.debug(Debugger.Level.INFO, "Registering new game instance, " + arena.getID());
    arenas.add(arena);
  }

  public static void unregisterArena(BaseArena arena) {
    Debugger.debug(Debugger.Level.INFO, "Unegistering game instance, " + arena.getID());
    arenas.remove(arena);
  }

  /**
   * Returns arena based by ID
   *
   * @param id name of arena
   * @return Arena or null if not found
   */
  public static BaseArena getArena(String id) {
    for (BaseArena arena : arenas) {
      if (arena.getID().equalsIgnoreCase(id)) {
        return arena;
      }
    }
    return null;
  }

  public static void registerArenas() {
    Debugger.debug(Debugger.Level.INFO, "Initial arenas registration");
    ArenaRegistry.getArenas().clear();
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    ConfigurationSection section = config.getConfigurationSection("instances");
    if (section == null) {
      Debugger.debug(Debugger.Level.WARN, "No instances configuration section in arenas.yml, skipping registration process! Was it manually edited?");
      return;
    }
    for (String id : section.getKeys(false)) {
      BaseArena arena;
      String s = "instances." + id + ".";
      if (s.contains("default")) {
        continue;
      }

      if (!config.contains(s + "gametype")) {
        arena = new SoloArena(id, plugin);
      } else {
        switch (BaseArena.ArenaType.valueOf(config.getString(s + "gametype").toUpperCase())) {
          case TEAM:
            arena = new TeamArena(id, plugin);
            break;
          case GUESS_THE_BUILD:
            arena = new GuessTheBuildArena(id, plugin);
            break;
          case SOLO:
          default:
            arena = new SoloArena(id, plugin);
            break;
        }
      }

      if (config.contains(s + "minimumplayers")) {
        arena.setMinimumPlayers(config.getInt(s + "minimumplayers"));
      } else {
        arena.setMinimumPlayers(config.getInt("instances.default.minimumplayers"));
      }
      if (config.contains(s + "maximumplayers")) {
        arena.setMaximumPlayers(config.getInt(s + "maximumplayers"));
      } else {
        arena.setMaximumPlayers(config.getInt("instances.default.maximumplayers"));
      }
      if (config.contains(s + "mapname")) {
        arena.setMapName(config.getString(s + "mapname"));
      } else {
        arena.setMapName(config.getString("instances.default.mapname"));
      }
      if (config.contains(s + "lobbylocation")) {
        arena.setLobbyLocation(LocationSerializer.getLocation(config.getString(s + "lobbylocation")));
      }
      if (config.contains(s + "Endlocation")) {
        arena.setEndLocation(LocationSerializer.getLocation(config.getString(s + "Endlocation")));
      } else {
        if (!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
          System.out.print(id + " doesn't contains an end location!");
          arena.setReady(false);
          ArenaRegistry.registerArena(arena);
          continue;
        }
      }
      if (config.contains(s + "gametype")) {
        arena.setArenaType(BaseArena.ArenaType.valueOf(config.getString(s + "gametype").toUpperCase()));
      } else {
        //assuming that arena is from 3.1.x releases we set arena type to SOLO by default
        arena.setArenaType(BaseArena.ArenaType.SOLO);
      }
      if (config.contains(s + "plots")) {
        if (config.isConfigurationSection(s + "plots")) {
          for (String plotName : config.getConfigurationSection(s + "plots").getKeys(false)) {
            if (config.isSet(s + "plots." + plotName + ".maxpoint") && config.isSet(s + "plots." + plotName + ".minpoint")) {
              Location minPoint = LocationSerializer.getLocation(config.getString(s + "plots." + plotName + ".minpoint"));
              Plot buildPlot = new Plot(arena, minPoint.getWorld().getBiome(minPoint.getBlockX(), minPoint.getBlockZ()));
              buildPlot.setCuboid(new Cuboid(minPoint, LocationSerializer.getLocation(config.getString(s + "plots." + plotName + ".maxpoint"))));
              buildPlot.fullyResetPlot();
              arena.getPlotManager().addBuildPlot(buildPlot);
            } else {
              System.out.println("Non configured plot instances found for arena " + id);
              arena.setReady(false);
            }
          }
        } else {
          System.out.println("Non configured plots in arena " + id);
          arena.setReady(false);
        }
      } else {
        System.out.print("Instance " + id + " doesn't contains plots!");
        arena.setReady(false);
      }
      arena.setReady(config.getBoolean("instances." + id + ".isdone"));
      if (arena instanceof SoloArena) {
        ((SoloArena) arena).initPoll();
      }
      ArenaRegistry.registerArena(arena);
      arena.start();
    }
    Debugger.debug(Debugger.Level.INFO, "Arenas registration completed");
  }

  public static void shuffleBungeeArena() {
    bungeeArena = new Random().nextInt(arenas.size());
  }

  public static int getBungeeArena() {
    if (bungeeArena == -999) {
      bungeeArena = new Random().nextInt(arenas.size());
    }
    return bungeeArena;
  }
}
