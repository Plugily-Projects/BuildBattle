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

package plugily.projects.buildbattle.handlers.menu.registry.particles;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

import java.util.HashSet;
import java.util.Map.Entry;

/**
 * Created by Tom on 23/08/2015.
 */
public class ParticleRefreshScheduler {

  private BukkitTask task;

  public ParticleRefreshScheduler(PluginMain plugin) {
    int particleRefreshTick = plugin.getConfig().getInt("Particle.Refresh-Rate", 10);

    if (particleRefreshTick < 0) {
      particleRefreshTick = 20;
    }

    int amountParticle = plugin.getConfig().getInt("Particle.Effects", 20);

    if (amountParticle < 0) {
      amountParticle = 20;
    }

    final int particleAmount = amountParticle;

    task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
      for (IPluginArena arena : plugin.getArenaRegistry().getArenas()) {
        if (arena instanceof BaseArena && !arena.getPlayers().isEmpty()) {
          for (Plot buildPlot : ((BaseArena) arena).getPlotManager().getPlots()) {
            if (!buildPlot.getMembers().isEmpty()) {
              for (Entry<Location, String> map : buildPlot.getParticles().entrySet()) {
                VersionUtils.sendParticles(map.getValue(), new HashSet<>(buildPlot.getMembers()), map.getKey(), particleAmount);
              }
            }
          }
        }
      }
    }, 0, particleRefreshTick);
  }

  public void cancelTask() {
    if (task != null) {
      task.cancel();
    }
  }
}
