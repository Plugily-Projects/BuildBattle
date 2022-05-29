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
import plugily.projects.buildbattle.arena.BuildArena;
import plugily.projects.buildbattle.old.ConfigPreferences;
import plugily.projects.buildbattle.old.handlers.HolidayManager;
import plugily.projects.buildbattle.old.handlers.reward.Reward;
import plugily.projects.buildbattle.old.menus.options.registry.particles.ParticleRefreshScheduler;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.states.PluginStartingState;

/**
 * @author Plajer
 *     <p>Created at 03.06.2019
 */
public class StartingState extends PluginStartingState {

  @Override
  public void handleCall(PluginArena arena) {
    super.handleCall(arena);
    BuildArena pluginArena = (BuildArena) getPlugin().getArenaRegistry().getArena(arena.getId());
    if (pluginArena == null) {
      return;
    }
    /*
           //reset local variables to be 100% sure
           //
    */
    if (arena.getTimer() == 0 || arena.isForceStart()) {
      //getPlotManager().resetPlotsGradually();
      if(HolidayManager.getCurrentHoliday() != HolidayManager.HolidayType.NONE) {
        initPoll();
      }
      pluginArena.setParticleRefreshScheduler(new ParticleRefreshScheduler(getPlugin()));
      if(!getPlotManager().isPlotsCleared()) {
        getPlotManager().resetQueuedPlots();
      }
      pluginArena.distributePlots();
      setTimer(getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.THEME_VOTE, this));
      for (Player player : arena.getPlayers()) {
        player.getInventory().setItem(8, getPlugin().getOptionsRegistry().getMenuItem());
        //to prevent Multiverse changing gamemode bug
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> player.setGameMode(GameMode.CREATIVE), 40);

        getPlugin().getRewardsHandler().performReward(player, this, Reward.RewardType.START_GAME, -1);
      }

    }
  }



}
