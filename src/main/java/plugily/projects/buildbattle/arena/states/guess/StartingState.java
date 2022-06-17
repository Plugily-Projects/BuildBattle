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

package plugily.projects.buildbattle.arena.states.guess;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.BuildArena;
import plugily.projects.buildbattle.arena.GuessArena;
import plugily.projects.buildbattle.old.ConfigPreferences;
import plugily.projects.buildbattle.old.arena.managers.plots.Plot;
import plugily.projects.buildbattle.old.handlers.HolidayManager;
import plugily.projects.buildbattle.old.handlers.reward.Reward;
import plugily.projects.buildbattle.old.menus.options.registry.particles.ParticleRefreshScheduler;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.states.PluginStartingState;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

/**
 * @author Plajer
 *     <p>Created at 03.06.2019
 */
public class StartingState extends PluginStartingState {

  @Override
  public void handleCall(PluginArena arena) {
    super.handleCall(arena);
    GuessArena pluginArena = (GuessArena) getPlugin().getArenaRegistry().getArena(arena.getId());
    if (pluginArena == null) {
      return;
    }
    if (arena.getTimer() == 0 || arena.isForceStart()) {
      //getPlotManager().resetPlotsGradually();
      if(HolidayManager.getCurrentHoliday() != HolidayManager.HolidayType.NONE) {
        initPoll();
      }
      pluginArena.setParticleRefreshScheduler(new ParticleRefreshScheduler(getPlugin()));
      if(!pluginArena.getPlotManager().isPlotsCleared()) {
        pluginArena.getPlotManager().resetQueuedPlots();
      }
      //todo may removeable?
      pluginArena.distributePlots();
      pluginArena.getPlotManager().teleportToPlots();

      setArenaTimer(getPlugin().getConfig().getInt("Time-Manager." + pluginArena.getArenaType().getPrefix() + ".Voting.Theme"));
      pluginArena.setArenaInGameStage(BaseArena.ArenaInGameStage.THEME_VOTING);
    }
  }



}
