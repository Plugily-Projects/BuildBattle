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

package plugily.projects.buildbattle.menus.options.registry.particles;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import pl.plajerlair.commonsbox.minecraft.compat.VersionUtils;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;

import java.util.Map.Entry;

/**
 * Created by Tom on 23/08/2015.
 */
public class ParticleRefreshScheduler {

  public BukkitTask task;

  public ParticleRefreshScheduler(Main plugin) {
    if(task != null) {
      task.cancel();
    }

    task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
      for(BaseArena arena : ArenaRegistry.getArenas()) {
        if(!arena.getPlayers().isEmpty()) {
          for(Plot buildPlot : arena.getPlotManager().getPlots()) {
            for(Entry<Location, String> map : buildPlot.getParticles().entrySet()) {
              VersionUtils.sendParticles(map.getValue(), buildPlot.getOwners().get(0), map.getKey(), plugin.getConfig().getInt("Amount-One-Particle-Effect-Contains", 20));
            }
          }
        }
      }
    }, plugin.getConfig().getInt("Particle-Refresh-Per-Tick", 10), plugin.getConfig().getInt("Particle-Refresh-Per-Tick", 10));
  }

}
