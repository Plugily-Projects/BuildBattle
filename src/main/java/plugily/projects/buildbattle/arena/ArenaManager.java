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

package plugily.projects.buildbattle.arena;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.api.user.IUser;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.PluginArenaManager;
import plugily.projects.minigamesbox.classic.user.User;

import java.util.List;

/**
 * @author Plajer
 * <p>Created at 13.05.2018
 */
public class ArenaManager extends PluginArenaManager {

  private final Main plugin;

  public ArenaManager(Main plugin) {
    super(plugin);
    this.plugin = plugin;
  }

  @Override
  public void additionalPartyJoin(Player player, IPluginArena arena, Player partyLeader) {
    BaseArena pluginArena = plugin.getArenaRegistry().getArena(arena.getId());
    if(pluginArena == null) {
      return;
    }

    List<Plot> plots = pluginArena.getPlotManager().getPlots();
    Plot partyPlot = plots.get(plots.size() == 1 ? 0 : plugin.getRandom().nextInt(plots.size()));

    if(arena.getPlayersLeft().contains(partyLeader)) {
      Plot partyLeaderPlot = pluginArena.getPlotManager().getPlot(partyLeader);
      if(partyLeaderPlot != null) {
        partyPlot = partyLeaderPlot;
      }
    }
    if(partyPlot != null) {
      partyPlot.addMember(player, pluginArena, false);
    }

  }

  @Override
  public void leaveAttempt(@NotNull Player player, @NotNull IPluginArena arena) {
    BaseArena pluginArena = plugin.getArenaRegistry().getArena(arena.getId());
    if(pluginArena == null) {
      return;
    }
    super.leaveAttempt(player, arena);
    IUser user = plugin.getUserManager().getUser(player);
    user.setStatistic("LOCAL_POINTS", 0);
    user.setStatistic("LOCAL_POINTS_GTB", 0);
    Plot plot = pluginArena.getPlotManager().getPlot(player);
    if(plot == null) {
      return;
    }
    plot.getMembers().remove(player);
    if(arena instanceof BuildArena) {
      if(plot.getMembers().isEmpty()) {
        ((BuildArena) arena).getQueue().remove(plot);
      }
    }
    if(arena instanceof GuessArena) {
      GuessArena guessArena = (GuessArena) pluginArena;
      ((GuessArena) arena).getWhoGuessed().remove(player);
      if(guessArena.getCurrentBuilders().contains(player)) {
        if(plot.getMembers().isEmpty()) {
          if(arena.getArenaState() == IArenaState.IN_GAME) {
            //ToDo message force skipped
            pluginArena.setTimer(plugin.getConfig().getInt("Time-Manager." + pluginArena.getArenaType().getPrefix() + ".Round-Delay"), true);
            pluginArena.setArenaInGameState(BaseArena.ArenaInGameState.PLOT_VOTING);
          }
        }
      }
    }
  }

}
