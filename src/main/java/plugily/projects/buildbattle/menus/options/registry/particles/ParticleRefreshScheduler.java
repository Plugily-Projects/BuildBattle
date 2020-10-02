/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2020 Plugily Projects - maintained by Tigerpanzer_02, 2Wild4You and contributors
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

package plugily.projects.buildbattle.menus.options.registry.particles;

import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;

import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.utils.Utils;

/**
 * Created by Tom on 23/08/2015.
 */
public class ParticleRefreshScheduler {

  public ParticleRefreshScheduler(Main plugin) {
    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
      for (BaseArena arena : ArenaRegistry.getArenas()) {
        for (Plot buildPlot : arena.getPlotManager().getPlots()) {
          for (Entry<Location, Particle> map : buildPlot.getParticles().entrySet()) {
            if (arena.getPlayers().isEmpty()) {
              continue;
            }

            Utils.spawnParticle(map.getValue(), map.getKey(), plugin.getConfig().getInt("Amount-One-Particle-Effect-Contains", 20),
                    1, 1, 1, 1);
          }
        }
      }
    }, plugin.getConfig().getInt("Particle-Refresh-Per-Tick", 10), plugin.getConfig().getInt("Particle-Refresh-Per-Tick", 10));
  }

}
