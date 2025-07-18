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
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.arena.states.build.InGameState;
import plugily.projects.buildbattle.arena.states.build.StartingState;
import plugily.projects.buildbattle.handlers.themes.vote.VoteMenu;
import plugily.projects.buildbattle.handlers.themes.vote.VotePoll;
import plugily.projects.minigamesbox.api.arena.IArenaState;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 22.04.2022
 */
public class BuildArena extends BaseArena {
  private final Queue<Plot> queue = new LinkedList<>();
  private Plot votingPlot;
  private VoteMenu voteMenu;

  public BuildArena(String id) {
    super(id);
    setArenaType(ArenaType.SOLO);
    addGameStateHandler(IArenaState.IN_GAME, new InGameState());
    addGameStateHandler(IArenaState.STARTING, new StartingState());
    getPlugin().getDebugger().debug("Init Arena {0} with ArenaType {1}", getId(), getArenaType());
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
    votingPlot = null;
    if(voteMenu != null) {
      voteMenu.resetPoll();
    }
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

  public Plot getVotingPlot() {
    return votingPlot;
  }

  @NotNull
  public Queue<Plot> getQueue() {
    return queue;
  }

  @Override
  public void distributePlots() {
    int neededPlots = getPlayersLeft().size() / getArenaOption("PLOT_MEMBER_SIZE");
    if(getPlotManager().getPlots().size() < neededPlots) {
      getPlugin().getMessageUtils().errorOccurred();
      getPlugin().getDebugger().sendConsoleMsg("&c[Build Battle] [PLOT WARNING] Not enough plots in arena " + getId() + "! Lacks " + (neededPlots - getPlotManager().getPlots().size()) + " plots");
      getPlugin().getDebugger().sendConsoleMsg("&c[PLOT WARNING] Required " + neededPlots + " but have " + getPlotManager().getPlots().size());
      getPlugin().getDebugger().sendConsoleMsg("&c[PLOT WARNING] Instance was stopped!");
      getPlugin().getArenaManager().stopGame(true, this);
    }
    switch(getArenaType()) {
      case SOLO:
        List<Player> players = new ArrayList<>(getPlayersLeft());
        for(Plot plot : getPlotManager().getPlots()) {
          if(players.isEmpty()) {
            break;
          }
          if(!plot.getMembers().isEmpty()) {
            continue;
          }
          if(!getPlugin().getUserManager().getUser(players.get(0)).isSpectator()) {
            plot.addMember(players.remove(0), this, true);
          }
        }
        break;
      case TEAM:
        int plotMemberSize = getArenaOption("PLOT_MEMBER_SIZE");
        int neededDividedPlots = getPlayersLeft().size() / plotMemberSize;
        int currentDividedPlots = (int) getPlotManager().getPlots().stream().filter(plot -> plot.getMembersSize() > 0).count();


        if(neededDividedPlots > currentDividedPlots) {
          List<Plot> fullPlots = getPlotManager().getPlots().stream().filter(plot -> plot.getMembersSize() == plotMemberSize).collect(Collectors.toList());
          List<Player> playersToMove = getPlayersLeft().stream().filter(player -> fullPlots.stream().noneMatch(plot -> plot.getMembers().contains(player))).collect(Collectors.toList());
          if(!playersToMove.isEmpty()) {
              for(int i = 0; i < playersToMove.size(); i++) {
                  Player move = playersToMove.get(i);
                  List<Plot> emptyPlots = getPlotManager().getPlots().stream().filter(plot -> plot.getMembersSize() < plotMemberSize).collect(Collectors.toList());
                  Plot maxEmptyPlot = emptyPlots.stream().max(Comparator.comparing(Plot::getMembersSize)).get();
                  maxEmptyPlot.addMember(move, this, true);
              }
          }
        }
        //check if not only one plot got players
        Plot maxPlayers = getPlotManager().getPlots().stream().max(Comparator.comparing(Plot::getMembersSize)).get();
        Plot minPlayers = getPlotManager().getPlots().stream().min(Comparator.comparing(Plot::getMembersSize)).get();
        if(maxPlayers.getMembersSize() == getPlayersLeft().size()) {
          for(int i = 0; i < maxPlayers.getMembersSize() / 2; i++) {
            Player move = maxPlayers.getMembers().get(i);
            minPlayers.addMember(move, this, true);
            maxPlayers.removeMember(move);
          }
        }
    }
    for(Plot plot : getPlotManager().getPlots()) {
      if(plot.getMembers().isEmpty()) {
        continue;
      }
      for(Player member : plot.getMembers()) {
        getPlotList().put(member, plot);
      }
    }
    getPlotManager().teleportToPlots();
  }


  @Override
  public boolean enoughPlayersToContinue() {
    int size = getPlayersLeft().size();
    int memberSize = getArenaOption("PLOT_MEMBER_SIZE");

    if(size > memberSize) {
      return true;
    }
    if(size == memberSize) {
      return !new HashSet<>(getPlotManager().getPlot(getPlayersLeft().get(0)).getMembers()).containsAll(getPlayersLeft());
    }
    return false;
  }

  @Override
  public void setMinimumPlayers(int amount) {
    if(getArenaType() == ArenaType.TEAM && amount <= getArenaOption("PLOT_MEMBER_SIZE")) {
      getPlugin().getDebugger().debug("Minimum players amount for TEAM game mode arena cannot be less than 3! Setting amount to 3!");
      super.setMinimumPlayers(3);
      return;
    }
    super.setMinimumPlayers(amount);
  }

  protected void setTypeByPlotMembers() {
    setArenaType(getArenaOption("PLOT_MEMBER_SIZE") <= 1 ? ArenaType.SOLO : ArenaType.TEAM);
  }

}
