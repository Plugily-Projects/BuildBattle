/*
 * BuildBattle 4 - Ultimate building competition minigame
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.buildbattle4.menus.particles;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import pl.plajer.buildbattle4.ConfigPreferences;
import pl.plajer.buildbattle4.Main;
import pl.plajer.buildbattle4.arena.Arena;
import pl.plajer.buildbattle4.arena.ArenaRegistry;
import pl.plajer.buildbattle4.arena.plots.ArenaPlot;

/**
 * Created by Tom on 23/08/2015.
 */
public class ParticleHandler extends BukkitRunnable {

  private static int amount = ConfigPreferences.getAmountFromOneParticle();
  private Main plugin;
  private long tick;

  public ParticleHandler(Main main) {
    plugin = main;
  }

  public void start() {
    tick = ConfigPreferences.getParticleRefreshTick();
    this.runTaskTimer(plugin, tick, tick);
  }

  @Override
  public void run() {
    for (Arena arena : ArenaRegistry.getArenas()) {
      for (ArenaPlot buildPlot : arena.getPlotManager().getPlots()) {
        for (Location location : buildPlot.getParticles().keySet()) {
          if (!arena.getPlayers().isEmpty())
            location.getWorld().spawnParticle(buildPlot.getParticles().get(location), location, amount, 1, 1, 1);
        }
      }
    }
  }
}
