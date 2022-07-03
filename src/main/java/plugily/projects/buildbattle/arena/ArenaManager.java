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

package plugily.projects.buildbattle.arena;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.PluginArenaManager;
import plugily.projects.minigamesbox.classic.user.User;

import java.util.Comparator;

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
  public void additionalPartyJoin(Player player, PluginArena arena, Player partyLeader) {
    BaseArena pluginArena = (BaseArena) plugin.getArenaRegistry().getArena(arena.getId());
    if(pluginArena == null) {
      return;
    }
    Plot partyPlot = pluginArena.getPlotManager().getPlots().get(plugin.getRandom().nextInt(pluginArena.getPlotManager().getPlots().size()));

    if(arena.getPlayers().contains(partyLeader)) {
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
  public void leaveAttempt(@NotNull Player player, @NotNull PluginArena arena) {
    BaseArena pluginArena = (BaseArena) plugin.getArenaRegistry().getArena(arena.getId());
    if(pluginArena == null) {
      return;
    }
    super.leaveAttempt(player, arena);
    User user = plugin.getUserManager().getUser(player);
    user.setStatistic("LOCAL_POINTS", 0);
    user.setStatistic("LOCAL_POINTS_GTB", 0);
    if(arena instanceof BuildArena) {
      Plot plot = pluginArena.getPlotManager().getPlot(player);
      if(plot != null && plot.getMembers().size() <= 1) {
        ((BuildArena) arena).getQueue().remove(plot);
      }
    }
    if(arena instanceof GuessArena) {
      ((GuessArena) arena).getWhoGuessed().remove(player);
      if(player == ((GuessArena) pluginArena).getCurrentBuilder()) {
        ((GuessArena) pluginArena).setCurrentBuilder(null);
        if(arena.getArenaState() == ArenaState.IN_GAME) {
          pluginArena.setTimer(plugin.getConfig().getInt("Time-Manager." + pluginArena.getArenaType().getPrefix() + ".Round-Delay"));
          pluginArena.setArenaInGameStage(BaseArena.ArenaInGameStage.PLOT_VOTING);
        }
      }
    }
  }

}
