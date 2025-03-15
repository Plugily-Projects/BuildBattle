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

package plugily.projects.buildbattle.arena.states.build;

import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.BuildArena;
import plugily.projects.buildbattle.handlers.menu.registry.particles.ParticleRefreshScheduler;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.states.PluginStartingState;

/**
 * @author Plajer
 * <p>Created at 03.06.2019
 */
public class StartingState extends PluginStartingState {

  @Override
  public void handleCall(PluginArena arena) {
    super.handleCall(arena);
    BuildArena pluginArena = (BuildArena) getPlugin().getArenaRegistry().getArena(arena.getId());
    if(pluginArena == null) {
      return;
    }
    if(arena.getTimer() == 0 || arena.isForceStart()) {

      pluginArena.setParticleRefreshScheduler(new ParticleRefreshScheduler(getPlugin()));

      if(!pluginArena.getPlotManager().isPlotsCleared()) pluginArena.getPlotManager().resetQueuedPlots();

      pluginArena.distributePlots();
      setArenaTimer(getPlugin().getConfig().getInt("Time-Manager." + pluginArena.getArenaType().getPrefix() + ".Voting.Theme"));

      pluginArena.setArenaInGameState(BaseArena.ArenaInGameState.THEME_VOTING);
    }
  }
}
