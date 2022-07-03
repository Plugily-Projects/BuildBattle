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
import plugily.projects.minigamesbox.classic.utils.hologram.ArmorStandHologram;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;

import java.util.ArrayList;
import java.util.List;

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
    String gameType = ConfigUtils.getConfig(plugin, "arenas").getString("instances." + id + "gametype", "classic");
    switch(gameType.toLowerCase()) {
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
    boolean checks = super.additionalValidatorChecks(section, arena, id);
    if(!checks) return false;

    if(!section.getBoolean(id + ".isdone")) {
      plugin.getDebugger().sendConsoleMsg(new MessageBuilder("VALIDATOR_INVALID_ARENA_CONFIGURATION").asKey().value("NOT VALIDATED").arena(arena).build());
      return false;
    }
    if(arena instanceof BuildArena && !section.contains(id + ".plotmembersize")) {
      plugin.getDebugger().sendConsoleMsg(new MessageBuilder("VALIDATOR_INVALID_ARENA_CONFIGURATION").asKey().value("PLOT MEMBER SIZE MISSING!").arena(arena).build());
      return false;
    }
    arena.setArenaOption("PLOT_MEMBER_SIZE", arena instanceof BuildArena ? section.getInt(id + ".plotmembersize", 1) : 1);

    ConfigurationSection plotSection = section.getConfigurationSection(id + ".plots");
    if(plotSection == null) {
      plugin.getDebugger().sendConsoleMsg(new MessageBuilder("VALIDATOR_INVALID_ARENA_CONFIGURATION").asKey().value("PLOTS SETUP MISSING!").arena(arena).build());
      return false;
    }
    for(String plotName : plotSection.getKeys(false)) {
      String minPointString = plotSection.getString(plotName + ".minpoint");
      String maxPointString = plotSection.getString(plotName + ".maxpoint");

      if(minPointString != null && maxPointString != null) {
        Location minPoint = LocationSerializer.getLocation(minPointString);
        Location maxPoint = LocationSerializer.getLocation(maxPointString);

        if(minPoint != null && maxPoint != null) {
          World minWorld = minPoint.getWorld();

          if(minWorld != null) {
            Biome biome = ServerVersion.Version.isCurrentHigher(ServerVersion.Version.v1_15_R1) ?
                minWorld.getBiome(minPoint.getBlockX(), minPoint.getBlockY(), minPoint.getBlockZ())
                : minWorld.getBiome(minPoint.getBlockX(), minPoint.getBlockZ());

            Plot buildPlot = new Plot((BaseArena) arena, biome);

            buildPlot.setCuboid(new Cuboid(minPoint, maxPoint));
            buildPlot.fullyResetPlot();

            ((BaseArena) arena).getPlotManager().addBuildPlot(buildPlot);
          }
        }
      } else {
        plugin.getDebugger().sendConsoleMsg(new MessageBuilder("VALIDATOR_INVALID_ARENA_CONFIGURATION").asKey().value("PLOT NOT CONFIGURED!").arena(arena).build());
        return false;
      }
    }
    if(arena instanceof BuildArena) {
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
    List<BaseArena> baseArenas = new ArrayList<>();
    for(PluginArena pluginArena : super.getArenas()) {
      if(pluginArena instanceof BaseArena) {
        baseArenas.add((BaseArena) pluginArena);
      }
    }
    return baseArenas;
  }
}
