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

package plugily.projects.buildbattle.arena;

import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion.Version;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.dimensional.Cuboid;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;
import plugily.projects.buildbattle.ConfigPreferences;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.arena.impl.GuessTheBuildArena;
import plugily.projects.buildbattle.arena.impl.SoloArena;
import plugily.projects.buildbattle.arena.impl.TeamArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.utils.Debugger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Tom on 27/07/2014.
 */
public class ArenaRegistry {

  private static final List<BaseArena> ARENAS = new ArrayList<>();
  private static final Main plugin = JavaPlugin.getPlugin(Main.class);

  private static int bungeeArena = -999;

  public static List<BaseArena> getArenas() {
    return ARENAS;
  }

  /**
   * Returns arena where the player is
   *
   * @param p target player
   * @return Arena or null if not playing
   */
  @Nullable
  public static BaseArena getArena(Player p) {
    if(p == null || !p.isOnline()) {
      return null;
    }

    for(BaseArena arena : ARENAS) {
      for(Player player : arena.getPlayers()) {
        if(p == player) return arena;
      }
      for(Player player : arena.getSpectators()) {
        if(p == player) return arena;
      }
    }
    return null;
  }

  public static void registerArena(BaseArena arena) {
    Debugger.debug("Registering new game instance, " + arena.getID());
    ARENAS.add(arena);
  }

  public static void unregisterArena(BaseArena arena) {
    Debugger.debug("Unegistering game instance, " + arena.getID());
    ARENAS.remove(arena);
  }

  /**
   * Returns arena based by ID
   *
   * @param id name of arena
   * @return Arena or null if not found
   */
  public static BaseArena getArena(String id) {
    for(BaseArena arena : ARENAS) {
      if(arena.getID().equalsIgnoreCase(id)) {
        return arena;
      }
    }
    return null;
  }

  public static void registerArenas() {
    Debugger.debug("Initial arenas registration");
    ArenaRegistry.getArenas().clear();
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    ConfigurationSection section = config.getConfigurationSection("instances");
    if(section == null) {
      Debugger.debug(Debugger.Level.WARN, "No instances configuration section in arenas.yml, skipping registration process! Was it manually edited?");
      return;
    }
    for(String id : section.getKeys(false)) {
      if(id.equalsIgnoreCase("default")) {
        continue;
      }
      BaseArena arena;
      String s = "instances." + id + ".";

      switch(BaseArena.ArenaType.valueOf(config.getString(s + "gametype", "solo").toUpperCase())) {
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

      if(config.contains(s + "minimumplayers")) {
        arena.setMinimumPlayers(config.getInt(s + "minimumplayers"));
      } else {
        arena.setMinimumPlayers(config.getInt("instances.default.minimumplayers"));
      }
      if(config.contains(s + "maximumplayers")) {
        arena.setMaximumPlayers(config.getInt(s + "maximumplayers"));
      } else {
        arena.setMaximumPlayers(config.getInt("instances.default.maximumplayers"));
      }
      if(config.contains(s + "mapname")) {
        arena.setMapName(config.getString(s + "mapname"));
      } else {
        arena.setMapName(config.getString("instances.default.mapname"));
      }
      if(config.contains(s + "lobbylocation")) {
        arena.setLobbyLocation(LocationSerializer.getLocation(config.getString(s + "lobbylocation")));
      }
      if(config.contains(s + "Endlocation")) {
        arena.setEndLocation(LocationSerializer.getLocation(config.getString(s + "Endlocation")));
      } else {
        if(!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
          System.out.print(id + " doesn't contains an end location!");
          arena.setReady(false);
          ArenaRegistry.registerArena(arena);
          continue;
        }
      }
      if(config.contains(s + "gametype")) {
        arena.setArenaType(BaseArena.ArenaType.valueOf(config.getString(s + "gametype").toUpperCase()));
      } else {
        //assuming that arena is from 3.1.x releases we set arena type to SOLO by default
        arena.setArenaType(BaseArena.ArenaType.SOLO);
      }
      if(config.contains(s + "plots")) {
        if(config.isConfigurationSection(s + "plots")) {
          for(String plotName : config.getConfigurationSection(s + "plots").getKeys(false)) {
            if(config.isSet(s + "plots." + plotName + ".maxpoint") && config.isSet(s + "plots." + plotName + ".minpoint")) {
              Location minPoint = LocationSerializer.getLocation(config.getString(s + "plots." + plotName + ".minpoint"));
              if(minPoint != null && minPoint.getWorld() != null) {
                Biome biome = Version.isCurrentHigher(Version.v1_15_R1) ?
                    minPoint.getWorld().getBiome(minPoint.getBlockX(), minPoint.getBlockY(), minPoint.getBlockZ())
                    : minPoint.getWorld().getBiome(minPoint.getBlockX(), minPoint.getBlockZ());
                Plot buildPlot = new Plot(arena, biome);
                buildPlot.setCuboid(new Cuboid(minPoint, LocationSerializer.getLocation(config.getString(s + "plots." + plotName + ".maxpoint"))));
                buildPlot.fullyResetPlot();
                arena.getPlotManager().addBuildPlot(buildPlot);
              }
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
      if(arena instanceof SoloArena) {
        ((SoloArena) arena).initPoll();
      }
      ArenaRegistry.registerArena(arena);
      arena.start();
    }
    Debugger.debug("Arenas registration completed");
  }

  public static void shuffleBungeeArena() {
    bungeeArena = new Random().nextInt(ARENAS.size());
  }

  public static int getBungeeArena() {
    if(bungeeArena == -999) {
      bungeeArena = new Random().nextInt(ARENAS.size());
    }
    return bungeeArena;
  }
}
