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

package plugily.projects.buildbattle.arena.managers.plots;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion.Version;
import pl.plajerlair.commonsbox.minecraft.dimensional.Cuboid;
import plugily.projects.buildbattle.api.event.plot.BBPlayerPlotReceiveEvent;
import plugily.projects.buildbattle.arena.ArenaState;
import plugily.projects.buildbattle.arena.impl.BaseArena;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Tom on 17/08/2015.
 */
public class PlotManager {

  private final List<Plot> plots = new ArrayList<>();
  private final List<Plot> plotsToClear = new ArrayList<>();
  private final BaseArena arena;

  public PlotManager(BaseArena arena) {
    this.arena = arena;
  }

  public void addBuildPlot(Plot buildPlot) {
    plots.add(buildPlot);
  }

  public Plot getPlot(Player player) {
    for(Plot buildPlot : plots) {
      if(buildPlot.getOwners().contains(player)) {
        return buildPlot;
      }
    }

    return null;
  }

  public void resetQueuedPlots() {
    plotsToClear.forEach(Plot::fullyResetPlot);
    plotsToClear.clear();
  }

  public boolean isPlotsCleared() {
    return plotsToClear.isEmpty();
  }

  public void resetPlotsGradually() {
    if(isPlotsCleared()) {
      return;
    }

    plotsToClear.get(0).fullyResetPlot();
    plotsToClear.remove(0);
  }

  public void teleportToPlots() {
    for(Plot buildPlot : plots) {
      if(!buildPlot.getOwners().isEmpty()) {
        Cuboid cuboid = buildPlot.getCuboid();
        if(cuboid == null) {
          continue;
        }

        final Location tploc = cuboid.getCenter();
        if(tploc == null) {
          continue;
        }

        if(Version.isCurrentEqualOrLower(Version.v1_13_R2)) { // Async catch in old versions
          Location loc = tploc;
          int m = 0;
          while(loc.getBlock().getType() != Material.AIR) {
            if (m >= 500) {
              break;// Thread never ends on flat map?
            }

            if(arena.getArenaState() == ArenaState.IN_GAME && arena.getTimer() > 30) {
              break;
            }

            loc = loc.add(0, 1, 0);
            //teleporting 1 x and z block away from center cause Y is above plot limit
            if(loc.getY() >= cuboid.getMaxPoint().getY()) {
              loc = cuboid.getCenter().clone().add(1, 0, 1);
            }

            m++; // Preventing server froze on flat map
          }

          for(Player p : buildPlot.getOwners()) {
            p.teleport(cuboid.getCenter());
          }
        } else {
          // Should do this in async thread to do not cause dead for the main thread
          CompletableFuture.supplyAsync(() -> {
            Location loc = tploc;
            while(loc.getBlock().getType() != Material.AIR) {
              if(arena.getArenaState() == ArenaState.IN_GAME && arena.getTimer() > 30) {
                break; // Thread never ends on flat map?
              }

              loc = loc.add(0, 1, 0);
              //teleporting 1 x and z block away from center cause Y is above plot limit
              if(loc.getY() >= cuboid.getMaxPoint().getY()) {
                loc = cuboid.getCenter().clone().add(1, 0, 1);
              }
            }

            return loc;
          }).thenAccept(loc -> {
            for(Player p : buildPlot.getOwners()) {
              p.teleport(cuboid.getCenter());
              //apply creative again to prevent multiverse default gamemode on world switch
              p.setGameMode(GameMode.CREATIVE);
            }
          });
        }
      }
      Bukkit.getPluginManager().callEvent(new BBPlayerPlotReceiveEvent(arena, buildPlot));
    }
  }

  public List<Plot> getPlots() {
    return plots;
  }

}
