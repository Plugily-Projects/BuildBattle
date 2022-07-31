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

package plugily.projects.buildbattle.arena.states.build;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.BuildArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.states.PluginInGameState;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.language.TitleBuilder;
import plugily.projects.minigamesbox.classic.handlers.reward.RewardsFactory;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author Tigerpanzer_02
 * <p>Created at 28.05.2022
 */
public class InGameState extends PluginInGameState {

  @Override
  public void handleCall(PluginArena arena) {
    super.handleCall(arena);
    BuildArena pluginArena = (BuildArena) getPlugin().getArenaRegistry().getArena(arena.getId());
    if(pluginArena == null) {
      return;
    }
    switch(pluginArena.getArenaInGameStage()) {
      case THEME_VOTING:
        handleThemeVoting(pluginArena);
        if(arena.getTimer() <= 0) {
          setArenaTimer(getPlugin().getConfig().getInt("Time-Manager." + pluginArena.getArenaType().getPrefix() + ".In-Game"));
          if(pluginArena.getVotePoll() != null && !pluginArena.getTheme().equals("Theme")) {
            pluginArena.setTheme(pluginArena.getVotePoll().getVotedTheme());
          }
          for(Player player : pluginArena.getPlayers()) {
            player.closeInventory();
            /*Teleports on Starting Stage already
            Plot plot = getPlotManager().getPlot(p);
            if(plot != null) {
              p.teleport(plot.getTeleportLocation());
            }*/
          }
          // may consider start to build message...
          pluginArena.setArenaInGameStage(BaseArena.ArenaInGameStage.BUILD_TIME);
        }
        break;
      case BUILD_TIME:
        handleBuildTime(pluginArena);
        if(arena.getTimer() <= 0) {
          setArenaTimer(getPlugin().getConfig().getInt("Time-Manager." + pluginArena.getArenaType().getPrefix() + ".Voting.Plot"));
          for(Player player : pluginArena.getPlayersLeft()) {
            User user = getPlugin().getUserManager().getUser(player);

            if(user.isSpectator()) continue;

            if(!pluginArena.getQueue().contains(pluginArena.getPlotFromPlayer(player))) {
              pluginArena.getQueue().add(pluginArena.getPlotFromPlayer(player));
            }
            player.getInventory().clear();
            pluginArena.getPlugin().getVoteItems().giveVoteItems(player);
            user.setStatistic("LOCAL_POINTS", 3);
          }
          pluginArena.setArenaInGameStage(BaseArena.ArenaInGameStage.PLOT_VOTING);
        }
        break;
      case PLOT_VOTING:
        handlePlotVoting(pluginArena);
        if(arena.getTimer() <= 0) {
          calculatePlotResults(pluginArena);
          if(pluginArena.getQueue().isEmpty()) {
            calculateEndResults(pluginArena);
            calculateWinnerPlot(pluginArena);
            announceResults(pluginArena);
            Location winnerLocation = pluginArena.getWinnerPlot().getTeleportLocation();

            for(Player player : pluginArena.getPlayers()) {
              VersionUtils.teleport(player, winnerLocation);
              new TitleBuilder("IN_GAME_MESSAGES_PLOT_VOTING_WINNER").asKey().player(player).value(pluginArena.getWinnerPlot().getFormattedMembers()).sendPlayer();
            }
            givePlaceRewards(pluginArena);
            getPlugin().getArenaManager().stopGame(false, arena);
          } else {
            voteForNextPlot(pluginArena);
          }
        }
        break;
      default:
        break;
    }
    if(arena.getTimer() <= 0) {
      getPlugin().getArenaManager().stopGame(false, arena);
    }
    // no players - stop game
    if(pluginArena.enoughPlayersToContinue()) {
      getPlugin().getArenaManager().stopGame(false, pluginArena);
    }
  }

  private void givePlaceRewards(BuildArena pluginArena) {
    RewardsFactory rewards = getPlugin().getRewardsHandler();
    for(int i = 1; i <= pluginArena.getTopList().size(); i++) {
      List<Player> list = pluginArena.getTopList().get(i);
      if(list != null) {
        for(Player player : list) {
          rewards.performReward(player, pluginArena, getPlugin().getRewardsHandler().getRewardType("PLACE"), i);
        }
      }
    }
  }

  private void calculateWinnerPlot(BuildArena pluginArena) {
    Plot winnerPlot = null;
    for(List<Player> potentialWinners : pluginArena.getTopList().values()) {
      if(!potentialWinners.isEmpty()) {
        winnerPlot = pluginArena.getPlotManager().getPlot(potentialWinners.get(0));
        break;
      }
    }
    if(winnerPlot == null) {
      getPlugin().getLogger().log(Level.SEVERE, "Fatal error in getting winner plot in game! No plot contain any online player!");
    }
    pluginArena.setWinnerPlot(winnerPlot);
  }

  private void calculatePlotResults(BuildArena pluginArena) {
    if(pluginArena.getVotingPlot().getPoints() == 0) {
      for(Player player : pluginArena.getPlayersLeft()) {
        if(getPlugin().getConfigPreferences().getOption("HIDE_PLOT_OWNER")) {
          new MessageBuilder("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_WAS").asKey().arena(pluginArena).player(player).value(pluginArena.getVotingPlot().getFormattedMembers()).sendArena();
          new TitleBuilder("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_TITLE").asKey().arena(pluginArena).player(player).value(pluginArena.getVotingPlot().getFormattedMembers()).sendArena();
        }
        User user = getPlugin().getUserManager().getUser(player);
        int points = user.getStatistic("LOCAL_POINTS");
        //no vote made, in this case make it a good vote
        if(points == 0) {
          points = 3;
        }
        if(!pluginArena.getVotingPlot().getMembers().contains(player))
          pluginArena.getVotingPlot().setPoints(pluginArena.getVotingPlot().getPoints() + points);
        user.setStatistic("LOCAL_POINTS", 3);
      }
    }
  }

  private void calculateEndResults(BuildArena pluginArena) {
    for(int b = 1; b <= pluginArena.getPlayersLeft().size(); b++) {
      pluginArena.getTopList().put(b, new ArrayList<>());
    }
    for(Plot buildPlot : pluginArena.getPlotManager().getPlots()) {
      for(Map.Entry<Integer, List<Player>> map : new HashMap<>(pluginArena.getTopList()).entrySet()) {
        Player first = map.getValue().isEmpty() ? null : map.getValue().get(0);

        Plot plot = pluginArena.getPlotManager().getPlot(first);
        if(plot == null) {
          pluginArena.getTopList().put(map.getKey(), buildPlot.getMembers());
          break;
        }
        if(buildPlot.getPoints() > plot.getPoints()) {
          moveScore(pluginArena, map.getKey(), buildPlot.getMembers());
          break;
        }
        if(buildPlot.getPoints() == plot.getPoints()) {
          List<Player> winners = pluginArena.getTopList().getOrDefault(map.getKey(), new ArrayList<>());
          winners.addAll(buildPlot.getMembers());
          pluginArena.getTopList().put(map.getKey(), winners);
          break;
        }
      }
    }
  }

  private void moveScore(BuildArena pluginArena, int pos, List<Player> owners) {
    List<Player> after = pluginArena.getTopList().getOrDefault(pos, new ArrayList<>());
    pluginArena.getTopList().put(pos, owners);
    if(pos <= pluginArena.getPlayersLeft().size() && !after.isEmpty()) {
      moveScore(pluginArena, pos + 1, after);
    }
  }

  private void announceResults(BuildArena pluginArena) {
    for(Map.Entry<Integer, List<Player>> map : pluginArena.getTopList().entrySet()) {
      for(Player p : map.getValue()) {
        User user = getPlugin().getUserManager().getUser(p);
        Plot plot = pluginArena.getPlotManager().getPlot(p);
        if(plot != null) {
          if(plot.getPoints() > user.getStatistic("HIGHEST_POINTS")) {
            user.setStatistic("HIGHEST_POINTS", plot.getPoints());
          }
          user.adjustStatistic("TOTAL_POINTS_EARNED", plot.getPoints());
        }
        if(map.getKey() != 1) {
          user.adjustStatistic("LOSES", 1);
          continue;
        }
        user.adjustStatistic("WINS", 1);
        getPlugin().getUserManager().addExperience(p, 5);
        if(plot != null && plot.getPoints() > user.getStatistic("HIGHEST_WIN")) {
          user.setStatistic("HIGHEST_WIN", plot.getPoints());
        }
      }
    }
  }

  private String formatWinners(final List<Player> winners) {
    List<Player> players = new ArrayList<>(winners);
    StringBuilder builder = new StringBuilder(players.get(0).getName());
    if(players.size() == 1) {
      return builder.toString();
    }

    players.remove(0);

    for(Player p : players) {
      builder.append(" & ").append(p.getName());
    }

    return builder.toString();
  }

  private void handleThemeVoting(BuildArena pluginArena) {
    for(Player player : pluginArena.getPlayers()) {
      pluginArena.getVoteMenu().updateInventory(player);
    }

  }

  private void handleBuildTime(BuildArena pluginArena) {
    for(int timers : getPlugin().getConfig().getIntegerList("Time-Manager.Time-Left-Intervals")) {
      if(timers == pluginArena.getTimer()) {
        pluginArena.sendBuildLeftTimeMessage();
        break;
      }
    }
    checkPlayerOutSidePlot(pluginArena);
  }

  private void handlePlotVoting(BuildArena pluginArena) {

  }

  public void voteForNextPlot(BuildArena pluginArena) {
    if(pluginArena.getVotingPlot() != null) {
      if(pluginArena.getVotingPlot().getPoints() == 0) {
        for(Player player : pluginArena.getPlayersLeft()) {
          User user = getPlugin().getUserManager().getUser(player);
          if(!pluginArena.getVotingPlot().getMembers().contains(player))
            pluginArena.getVotingPlot().setPoints(pluginArena.getVotingPlot().getPoints() + user.getStatistic("LOCAL_POINTS"));
          user.setStatistic("LOCAL_POINTS", 3);
          if(!player.getInventory().contains(pluginArena.getPlugin().getVoteItems().getReportItem())) {
            player.getInventory().setItem(pluginArena.getPlugin().getVoteItems().getReportVoteItem().getSlot(), pluginArena.getPlugin().getVoteItems().getReportVoteItem().getItemStack());
            player.updateInventory();
          }
        }
      }
      if(!pluginArena.getVotingPlot().getMembers().isEmpty() && getPlugin().getConfigPreferences().getOption("HIDE_PLOT_OWNER")) {
        new MessageBuilder("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_WAS").asKey().arena(pluginArena).value(pluginArena.getVotingPlot().getFormattedMembers()).sendArena();
        new TitleBuilder("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_TITLE").asKey().arena(pluginArena).value(pluginArena.getVotingPlot().getFormattedMembers()).sendArena();
      }
    }
    voteRoutine(pluginArena);
  }

  public void voteRoutine(BuildArena pluginArena) {
    if(!pluginArena.getQueue().isEmpty()) {
      setArenaTimer(getPlugin().getConfig().getInt("Time-Manager." + pluginArena.getArenaType().getPrefix() + ".Voting.Plot"));
      Plot plot = pluginArena.getQueue().poll();
      while(plot == null && !pluginArena.getQueue().isEmpty()) {
        // should not happen anymore... to be removed
        System.out.print("A PLAYER HAS NO PLOT!");
        plot = pluginArena.getQueue().poll();
      }
      if(pluginArena.getQueue().isEmpty() && plot == null) {
        pluginArena.setVotingPlot(null);
        return;
      }

      // getPlotManager().teleportAllToPlot(plotManager.getPlot(player.getUniqueId()));
      pluginArena.setVotingPlot(plot);


      Location teleportLoc = pluginArena.getVotingPlot().getTeleportLocation();

      for(Player player : pluginArena.getPlayers()) {
        VersionUtils.teleport(player, teleportLoc);
        player.setPlayerWeather(pluginArena.getVotingPlot().getWeatherType());
        player.setPlayerTime(Plot.Time.format(pluginArena.getVotingPlot().getTime(), player.getWorld().getTime()), false);
        if(getPlugin().getConfigPreferences().getOption("HIDE_PLOT_OWNER")) {
          new MessageBuilder("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_NEXT").asKey().arena(pluginArena).value("???").player(player).sendPlayer();
        } else {
          new TitleBuilder("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_TITLE").asKey().arena(pluginArena).value(pluginArena.getVotingPlot().getFormattedMembers()).player(player).sendPlayer();
          new MessageBuilder("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_NEXT").asKey().arena(pluginArena).value(pluginArena.getVotingPlot().getFormattedMembers()).player(player).sendPlayer();
        }
      }

      for(Player spectator : pluginArena.getSpectators()) {
        VersionUtils.teleport(spectator, teleportLoc);
        spectator.setPlayerWeather(pluginArena.getVotingPlot().getWeatherType());
        spectator.setPlayerTime(Plot.Time.format(pluginArena.getVotingPlot().getTime(), spectator.getWorld().getTime()), false);
        new TitleBuilder("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_TITLE").asKey().arena(pluginArena).value(pluginArena.getVotingPlot().getFormattedMembers()).player(spectator).sendPlayer();
        new MessageBuilder("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_NEXT").asKey().arena(pluginArena).value(pluginArena.getVotingPlot().getFormattedMembers()).player(spectator).sendPlayer();
      }
    }
  }

  private void checkPlayerOutSidePlot(BuildArena pluginArena) {
    if(pluginArena.getArenaOption("IN_PLOT_CHECKER") >= 3) {
      pluginArena.setArenaOption("IN_PLOT_CHECKER", 0);
      for(Player player : pluginArena.getPlayersLeft()) {
        User user = getPlugin().getUserManager().getUser(player);
        Plot buildPlot = pluginArena.getPlotFromPlayer(player);
        if(buildPlot != null && buildPlot.getCuboid() != null && !buildPlot.getCuboid().isInWithMarge(player.getLocation(), 5)) {
          VersionUtils.teleport(player, buildPlot.getTeleportLocation());
          new MessageBuilder("IN_GAME_MESSAGES_PLOT_PERMISSION_OUTSIDE").asKey().arena(pluginArena).player(player).sendPlayer();
        }
      }
    }
    pluginArena.changeArenaOptionBy("IN_PLOT_CHECKER", 1);
  }
}
