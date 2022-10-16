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

package plugily.projects.buildbattle.arena.managers;

import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.minigamesbox.classic.arena.managers.PluginMapRestorerManager;

public class MapRestorerManager extends PluginMapRestorerManager {

  public final BaseArena arena;

  public MapRestorerManager(BaseArena arena) {
    super(arena);
    this.arena = arena;
  }

  @Override
  public void fullyRestoreArena() {
    arena.cleanUpArena();
    clearPlots();
    cancelParticleRefresh();
    arena.setArenaOption("IN_PLOT_CHECKER", 0);
    arena.setArenaInGameState(BaseArena.ArenaInGameState.NONE);
    super.fullyRestoreArena();
  }

  private void clearPlots() {
    for(Plot plot : arena.getPlotManager().getPlots()) {
      plot.fullyResetPlot();
      plot.getMembers().clear();
    }
  }

  private void cancelParticleRefresh() {
    if(arena.getParticleRefreshScheduler() != null) {
      arena.getParticleRefreshScheduler().cancelTask();
    }
  }
}
