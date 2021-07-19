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
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import plugily.projects.buildbattle.ConfigPreferences;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.arena.impl.GuessTheBuildArena;
import plugily.projects.buildbattle.arena.impl.SoloArena;
import plugily.projects.buildbattle.arena.impl.TeamArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.utils.Debugger;
import plugily.projects.commonsbox.minecraft.compat.ServerVersion.Version;
import plugily.projects.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.commonsbox.minecraft.dimensional.Cuboid;
import plugily.projects.commonsbox.minecraft.serialization.LocationSerializer;

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
    if(p == null) {
      return null;
    }

    for(BaseArena arena : ARENAS) {
      for(Player player : arena.getPlayers()) {
        if(p.getUniqueId().equals(player.getUniqueId())) return arena;
      }
      for(Player player : arena.getSpectators()) {
        if(p.getUniqueId().equals(player.getUniqueId())) return arena;
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

  public static int getArenaPlayersOnline() {
    int players = 0;
    for(BaseArena arena : ARENAS) {
      players += arena.getPlayers().size();
    }
    return players;
  }

  public static void registerArenas() {
    Debugger.debug("Initial arenas registration");
    ArenaRegistry.getArenas().clear();

    ConfigurationSection section = ConfigUtils.getConfig(plugin, "arenas").getConfigurationSection("instances");
    if(section == null) {
      Debugger.debug(Debugger.Level.WARN, "No instances configuration section in arenas.yml, skipping registration process! Was it manually edited?");
      return;
    }
    for(String id : section.getKeys(false)) {
      if(id.equalsIgnoreCase("default")) {
        continue;
      }

      BaseArena.ArenaType arenaType;
      try {
        arenaType = BaseArena.ArenaType.valueOf(section.getString(id + ".gametype", "solo").toUpperCase());
      } catch(IllegalArgumentException e) {
        arenaType = BaseArena.ArenaType.SOLO;
      }

      BaseArena arena;
      switch(arenaType) {
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

      if(section.contains(id + ".minimumplayers")) {
        arena.setMinimumPlayers(section.getInt(id + ".minimumplayers"));
      } else {
        arena.setMinimumPlayers(section.getInt("instances.default.minimumplayers"));
      }
      if(section.contains(id + ".maximumplayers")) {
        arena.setMaximumPlayers(section.getInt(id + ".maximumplayers"));
      } else {
        arena.setMaximumPlayers(section.getInt("instances.default.maximumplayers"));
      }
      if(section.contains(id + ".mapname")) {
        arena.setMapName(section.getString(id + ".mapname"));
      } else {
        arena.setMapName(section.getString("instances.default.mapname"));
      }

      Location lobbyLoc = LocationSerializer.getLocation(section.getString(id + ".lobbylocation"));
      if(lobbyLoc == null || lobbyLoc.getWorld() == null) {
        arena.setReady(false);
        continue;
      }

      arena.setLobbyLocation(lobbyLoc);
      if(section.contains(id + ".Endlocation")) {
        arena.setEndLocation(LocationSerializer.getLocation(section.getString(id + ".Endlocation")));
      } else if(!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
        System.out.print(id + " doesn't contains an end location!");
        arena.setReady(false);
        ArenaRegistry.registerArena(arena);
        continue;
      }

      arena.setArenaType(arenaType);

      arena.setPlotSize(section.getInt(id + ".plotSize", 2));

      if(section.contains(id + ".plots")) {
        if(section.isConfigurationSection(id + ".plots")) {
          for(String plotName : section.getConfigurationSection(id + ".plots").getKeys(false)) {
            if(section.isSet(id + ".plots." + plotName + ".maxpoint")) {
              Location minPoint = LocationSerializer.getLocation(section.getString(id + ".plots." + plotName + ".minpoint"));

              if(minPoint != null) {
                org.bukkit.World minWorld = minPoint.getWorld();

                if(minWorld != null) {
                  Biome biome = Version.isCurrentHigher(Version.v1_15_R1) ?
                      minWorld.getBiome(minPoint.getBlockX(), minPoint.getBlockY(), minPoint.getBlockZ())
                      : minWorld.getBiome(minPoint.getBlockX(), minPoint.getBlockZ());

                  Plot buildPlot = new Plot(arena, biome);

                  buildPlot.setCuboid(new Cuboid(minPoint, LocationSerializer.getLocation(section.getString(id + ".plots." + plotName + ".maxpoint"))));
                  buildPlot.fullyResetPlot();

                  arena.getPlotManager().addBuildPlot(buildPlot);
                }
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
      arena.setReady(section.getBoolean(id + ".isdone"));
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
