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

package pl.plajer.buildbattle.menus.options.registry.particles;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.plots.Plot;

/**
 * Created by Tom on 23/08/2015.
 */
public class ParticleRefreshScheduler {


  public ParticleRefreshScheduler(Main plugin) {
    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
      for (Arena arena : ArenaRegistry.getArenas()) {
        for (Plot buildPlot : arena.getPlotManager().getPlots()) {
          for (Location location : buildPlot.getParticles().keySet()) {
            if (arena.getPlayers().isEmpty()) {
              continue;
            }
            location.getWorld().spawnParticle(buildPlot.getParticles().get(location), location,
                plugin.getConfig().getInt("Amount-One-Particle-Effect-Contains", 20), 1, 1, 1);
          }
        }
      }
    }, plugin.getConfig().getInt("Particle-Refresh-Per-Tick", 10), plugin.getConfig().getInt("Particle-Refresh-Per-Tick", 10));
  }

}
