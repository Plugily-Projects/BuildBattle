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
import plugily.projects.buildbattle.arena.states.guess.InGameState;
import plugily.projects.buildbattle.arena.states.guess.StartingState;
import plugily.projects.buildbattle.old.arena.managers.plots.Plot;
import plugily.projects.buildbattle.old.menus.themevoter.BBTheme;
import plugily.projects.buildbattle.old.menus.themevoter.VoteMenu;
import plugily.projects.buildbattle.old.menus.themevoter.VotePoll;
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.user.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 29.05.2022
 */
public class GuessArena extends BaseArena {

  private final List<Player> whoGuessed = new ArrayList<>();
  private int round = 1;
  private BBTheme currentTheme;
  private Map<Player, Plot> plotList = new HashMap<>();
  private Player winner;
  private Player currentBuilder;
  private Map<Player, Integer> playersPoints = new HashMap<>();
  private List<Integer> removedCharsAt = new ArrayList<>();

  private final Random random = new Random();

  public GuessArena(String id) {
    super(id);
    setArenaType(ArenaType.GUESS_THE_BUILD);
    addGameStateHandler(ArenaState.IN_GAME, new InGameState());
    addGameStateHandler(ArenaState.STARTING, new StartingState());
  }

  /**
   * Initiates voting poll
   */
  public void initPoll() {
    voteMenu = new VoteMenu(this);
    voteMenu.resetPoll();
  }

  @Override
  public void cleanUpArena() {
    winnerPlot = null;
    votingPlot = null;
    topList.clear();
    voteMenu.resetPoll();
    plotList.clear();
    super.cleanUpArena();
  }

  public VotePoll getVotePoll() {
    return voteMenu.getVotePoll();
  }

  public VoteMenu getVoteMenu() {
    return voteMenu;
  }

  public void setVotingPlot(Plot votingPlot) {
    this.votingPlot = votingPlot;
  }

  public Plot getWinnerPlot() {
    return winnerPlot;
  }

  public void setWinnerPlot(Plot winnerPlot) {
    this.winnerPlot = winnerPlot;
  }

  public Map<Integer, List<Player>> getTopList() {
    return topList;
  }

  public Plot getVotingPlot() {
    return votingPlot;
  }

  @NotNull
  public Queue<Plot> getQueue() {
    return queue;
  }


  public void distributePlots() {
    int neededPlots = (getPlayers().size() / getArenaOption("PLOT_MEMBER_SIZE"));
    if(getPlotManager().getPlots().size() < neededPlots) {
      getPlugin().getMessageUtils().errorOccurred();
      getPlugin().getDebugger().sendConsoleMsg("&c[Build Battle] [PLOT WARNING] Not enough plots in arena " + getId() + "! Lacks " + (neededPlots - getPlotManager().getPlots().size()) + " plots");
      getPlugin().getDebugger().sendConsoleMsg("&c[PLOT WARNING] Required " + neededPlots + " but have " + getPlotManager().getPlots().size());
      getPlugin().getDebugger().sendConsoleMsg("&c[PLOT WARNING] Instance was stopped!");
      getPlugin().getArenaManager().stopGame(false, this);
    }
    switch(getArenaType()) {
      case SOLO:
        List<Player> players = new ArrayList<>(getPlayersLeft());
        for(Plot plot : getPlotManager().getPlots()) {
          if(players.isEmpty()) {
            break;
          }

          Player first = players.get(0);
          User user = getPlugin().getUserManager().getUser(first);
          if(user.isSpectator()) {
            continue;
          }

          plot.addMember(first, this, true);

          players.remove(0);
        }
        break;
      case TEAM:
        for(Player player : getPlayers()) {
          // get base with min players
          Plot minPlayers = getPlotManager().getPlots().stream().min(Comparator.comparing(Plot::getMembersSize)).get();
          // add player to min base if he got no base
          Plot playerPlot = getPlotManager().getPlot(player);
          if(playerPlot == null) {
            minPlayers.addMember(player, this, true);
          }
          // fallback
          if(playerPlot == null) {
            getPlotManager().getPlots().get(0).addMember(player, this, true);
          }
        }
        //check if not only one plot got players
        Plot maxPlayers = getPlotManager().getPlots().stream().max(Comparator.comparing(Plot::getMembersSize)).get();
        Plot minPlayers = getPlotManager().getPlots().stream().min(Comparator.comparing(Plot::getMembersSize)).get();
        if(maxPlayers.getMembersSize() == getPlayers().size()) {
          for(int i = 0; i < maxPlayers.getMembersSize() / 2; i++) {
            Player move = maxPlayers.getMembers().get(i);
            minPlayers.addMember(move, this, true);
            maxPlayers.removeMember(move);
          }
        }
    }
    for(Plot plot : getPlotManager().getPlots()) {
      for(Player member : plot.getMembers()) {
        plotList.put(member, plot);
      }
    }
    getPlotManager().teleportToPlots();
  }


  @Override
  public boolean enoughPlayersToContinue() {
    int size = getPlayers().size();
    if(size > getArenaOption("PLOT_MEMBER_SIZE")) {
      return true;
    }
    if(size == getArenaOption("PLOT_MEMBER_SIZE")) {
      return !getPlotManager().getPlot(getPlayersLeft().get(0)).getMembers().containsAll(getPlayers());
    }
    return false;
  }

  @Override
  public void setMinimumPlayers(int amount) {
    if(amount <= getArenaOption("PLOT_MEMBER_SIZE")) {
      getPlugin().getDebugger().debug("Minimum players amount for TEAM game mode arena cannot be less than 3! Setting amount to 3!");
      setArenaOption("MINIMUM_PLAYERS", 3);
      return;
    }
    super.setMinimumPlayers(amount);
  }

  public Map<Player, Plot> getPlotList() {
    return plotList;
  }

  public Plot getPlotFromPlayer(Player player) {
    return plotList.get(player);
  }
}
