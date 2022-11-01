/*
 *
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2022 Plugily Projects - maintained by Tigerpanzer_02 and contributors
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
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.PluginArenaRegistry;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.dimensional.Cuboid;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Tom on 27/07/2014.
 */
public class ArenaRegistry extends PluginArenaRegistry {

  private final Main plugin;

  public ArenaRegistry(Main plugin) {
    super(plugin);
    this.plugin = plugin;
  }


  @Override
  public PluginArena getNewArena(String id) {
    switch(ConfigUtils.getConfig(plugin, "arenas").getString("instances." + id + ".gametype", "classic").toLowerCase(Locale.ENGLISH)) {
      case "gtb":
      case "guessthebuild":
      case "guess_the_build":
        return new GuessArena(id);
      case "classic":
      case "solo":
      case "team":
      default:
        return new BuildArena(id);
    }
  }

  @Override
  public boolean additionalValidatorChecks(ConfigurationSection section, PluginArena arena, String id) {
    if(!super.additionalValidatorChecks(section, arena, id)) return false;

    boolean isBuildArena = arena instanceof BuildArena;
    boolean isGuessArena = arena instanceof GuessArena;

    if(isBuildArena) {
      int plotMemberSize = section.getInt(id + ".plotmembersize", 0);

      // Member size can not be less than 1
      if(plotMemberSize < 1) {
        plugin.getDebugger().sendConsoleMsg(new MessageBuilder("VALIDATOR_INVALID_ARENA_CONFIGURATION").asKey().value("INVALID PLOT MEMBER SIZE!").arena(arena).build());
        return false;
      }

      arena.setArenaOption("PLOT_MEMBER_SIZE", plotMemberSize);
      ((BuildArena) arena).setTypeByPlotMembers();
    } else {
      arena.setArenaOption("PLOT_MEMBER_SIZE", 1);
    }

    ConfigurationSection plotSection = section.getConfigurationSection(id + ".plots");
    if(plotSection == null) {
      plugin.getDebugger().sendConsoleMsg(new MessageBuilder("VALIDATOR_INVALID_ARENA_CONFIGURATION").asKey().value("PLOTS SETUP MISSING!").arena(arena).build());
      return false;
    }

    BaseArena baseArena = (BaseArena) arena;

    for(String plotName : plotSection.getKeys(false)) {
      String minPointString = plotSection.getString(plotName + ".1", null);
      String maxPointString = plotSection.getString(plotName + ".2", null);

      if(minPointString != null && maxPointString != null) {
        Location minPoint = LocationSerializer.getLocation(minPointString);
        Location maxPoint = LocationSerializer.getLocation(maxPointString);

        if(minPoint != null && maxPoint != null) {
          World minWorld = minPoint.getWorld();

          if(minWorld != null) {
            Biome biome = ServerVersion.Version.isCurrentHigher(ServerVersion.Version.v1_15_R1) ?
                minWorld.getBiome(minPoint.getBlockX(), minPoint.getBlockY(), minPoint.getBlockZ())
                : minWorld.getBiome(minPoint.getBlockX(), minPoint.getBlockZ());

            Plot buildPlot = new Plot(baseArena, biome);

            buildPlot.setCuboid(new Cuboid(minPoint, maxPoint));
            buildPlot.fullyResetPlot();

            if(isGuessArena) {
              int plotMemberSize = arena.getArenaOption("PLOT_MEMBER_SIZE");
              int maxPlayers = arena.getMaximumPlayers();
              int plotAmount = maxPlayers / plotMemberSize;
              for(int i = 0; i < plotAmount; i++) {
                baseArena.getPlotManager().addBuildPlot(buildPlot);
              }
              break;
            } else {
              baseArena.getPlotManager().addBuildPlot(buildPlot);
            }
          }
        }
      } else {
        plugin.getDebugger().sendConsoleMsg(new MessageBuilder("VALIDATOR_INVALID_ARENA_CONFIGURATION").asKey().value("PLOT NOT CONFIGURED!").arena(arena).build());
        return false;
      }
    }
    if(isBuildArena) {
      ((BuildArena) arena).initPoll();
    }
    return true;
  }

  @Override
  public @Nullable BaseArena getArena(Player player) {
    PluginArena pluginArena = super.getArena(player);
    if(pluginArena instanceof BaseArena) {
      return (BaseArena) pluginArena;
    }
    return null;
  }

  @Override
  public @Nullable BaseArena getArena(String id) {
    PluginArena pluginArena = super.getArena(id);
    if(pluginArena instanceof BaseArena) {
      return (BaseArena) pluginArena;
    }
    return null;
  }

  public @NotNull List<BaseArena> getPluginArenas() {
    List<BaseArena> baseArenas = new ArrayList<>(super.getArenas().size());
    for(PluginArena pluginArena : super.getArenas()) {
      if(pluginArena instanceof BaseArena) {
        baseArenas.add((BaseArena) pluginArena);
      }
    }
    return baseArenas;
  }
}
