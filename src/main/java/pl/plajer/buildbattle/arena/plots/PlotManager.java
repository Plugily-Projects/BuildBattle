/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.buildbattle.arena.plots;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.event.plot.BBPlayerPlotReceiveEvent;
import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.arena.ArenaManager;
import pl.plajer.buildbattle.utils.MessageUtils;
import pl.plajerlair.core.services.exception.ReportedException;

/**
 * Created by Tom on 17/08/2015.
 */
public class PlotManager {

  private Main plugin = JavaPlugin.getPlugin(Main.class);
  private List<Plot> plots = new ArrayList<>();
  private List<Plot> plotsToClear = new ArrayList<>();
  private Arena arena;

  public PlotManager(Arena arena) {
    this.arena = arena;
  }

  public void addBuildPlot(Plot buildPlot) {
    plots.add(buildPlot);
  }

  public void distributePlots() {
    try {
      List<Player> players = new ArrayList<>(arena.getPlayers());
      int times = arena.getArenaType() == Arena.ArenaType.SOLO ? 1 : 2;
      for (int i = 0; i < times; i++) {
        for (Plot plot : plots) {
          if (players.isEmpty()) break;
          if (plot.getOwners() != null) {
            if (arena.getArenaType() == Arena.ArenaType.SOLO || arena.getPlayers().size() == 2 /* in case of 2 min players set for team mode*/) {
              if (plot.getOwners().size() == 0) {
                plot.addOwner(players.get(0).getUniqueId());
                plugin.getUserManager().getUser(players.get(0).getUniqueId()).setCurrentPlot(plot);

                players.remove(0);
              }
            } else if (arena.getArenaType() == Arena.ArenaType.TEAM) {
              if (plot.getOwners().size() < 2) {
                plot.addOwner(players.get(0).getUniqueId());
                plugin.getUserManager().getUser(players.get(0).getUniqueId()).setCurrentPlot(plot);

                players.remove(0);
              }
            }
          }
        }
      }
      if (!players.isEmpty()) {
        MessageUtils.errorOccurred();
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildBattle] [PLOT WARNING] Not enough plots in arena " + arena.getID() + "!");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[PLOT WARNING] Required " + (arena.getArenaType() == Arena.ArenaType.TEAM ? Math.ceil((double) arena.getPlayers().size() / 2) : arena.getPlayers().size()) + " but have " + plots.size());
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[PLOT WARNING] Instance was stopped!");
        ArenaManager.stopGame(false, arena);
      }
    } catch (Exception ex) {
      new ReportedException(JavaPlugin.getPlugin(Main.class), ex);
    }
  }

  public Plot getPlot(Player player) {
    for (Plot buildPlot : plots) {
      if (buildPlot.getOwners() != null && !buildPlot.getOwners().isEmpty()) {
        if (buildPlot.getOwners().contains(player.getUniqueId())) return buildPlot;
      }
    }
    return null;
  }

  public Plot getPlot(UUID uuid) {
    for (Plot buildPlot : plots) {
      if (buildPlot.getOwners() != null && !buildPlot.getOwners().isEmpty()) {
        if (buildPlot.getOwners().contains(uuid)) return buildPlot;
      }
    }
    return null;
  }

  public void resetQueuedPlots() {
    for (Plot buildPlot : plotsToClear) {
      buildPlot.fullyResetPlot();
    }
    plotsToClear.clear();
  }

  public boolean isPlotsCleared() {
    return plotsToClear.isEmpty();
  }

  public void resetPlotsGradually() {
    if (plotsToClear.isEmpty()) return;

    plotsToClear.get(0).fullyResetPlot();
    plotsToClear.remove(0);
  }

  public void teleportToPlots() {
    try {
      for (Plot buildPlot : plots) {
        if (buildPlot.getOwners() != null && !buildPlot.getOwners().isEmpty()) {
          Location tploc = buildPlot.getCuboid().getCenter();
          while (tploc.getBlock().getType() != Material.AIR) {
            tploc = tploc.add(0, 1, 0);
            //teleporting 1 x and z block away from center cause Y is above plot limit
            if (tploc.getY() >= buildPlot.getCuboid().getMaxPoint().getY()) {
              tploc = buildPlot.getCuboid().getCenter().clone().add(1, 0, 1);
            }
          }
          for (UUID u : buildPlot.getOwners()) {
            Player player = Bukkit.getServer().getPlayer(u);
            if (player != null) {
              player.teleport(buildPlot.getCuboid().getCenter());
            }
          }
        }
        BBPlayerPlotReceiveEvent event = new BBPlayerPlotReceiveEvent(arena, buildPlot);
        Bukkit.getPluginManager().callEvent(event);
      }
    } catch (Exception ex) {
      new ReportedException(JavaPlugin.getPlugin(Main.class), ex);
    }
  }

  public List<Plot> getPlots() {
    return plots;
  }

}
