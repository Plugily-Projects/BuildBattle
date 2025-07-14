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

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.BuildArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.arena.vote.VoteItems;
import plugily.projects.minigamesbox.api.user.IUser;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.states.PluginInGameState;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.language.TitleBuilder;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

import java.util.ArrayList;
import java.util.List;

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
    switch(pluginArena.getArenaInGameState()) {
      case THEME_VOTING:
        if(arena.getTimer() <= 0 || pluginArena.getTheme() != null) {
          // may consider start to build message...
          pluginArena.setArenaInGameState(BaseArena.ArenaInGameState.BUILD_TIME);

          setArenaTimer(getPlugin().getConfig().getInt("Time-Manager." + pluginArena.getArenaType().getPrefix() + ".In-Game"));

          if(pluginArena.getVotePoll() != null && pluginArena.getTheme() == null) {
            pluginArena.setTheme(pluginArena.getVotePoll().getVotedTheme());
            new MessageBuilder("IN_GAME_MESSAGES_PLOT_GTB_THEME_NAME").asKey().arena(pluginArena).sendArena();
          }

          for(Player player : pluginArena.getPlayersLeft()) {
            player.closeInventory();
            player.setGameMode(GameMode.CREATIVE);
            VersionUtils.setCollidable(player, false);
          }
          // second time after voting as sometimes world switch causing no teleportation!
          pluginArena.getPlotManager().teleportToPlots();
        } else {
          handleThemeVoting(pluginArena);
        }

        break;
      case BUILD_TIME:
        handleBuildTime(pluginArena);
        handleOptionsMenu(pluginArena);
        if(arena.getTimer() <= 0) {
          for(Player player : pluginArena.getPlayersLeft()) {
            IUser user = getPlugin().getUserManager().getUser(player);

            Plot playerPlot = pluginArena.getPlotFromPlayer(player);
            if(!pluginArena.getQueue().contains(playerPlot)) {
              pluginArena.getQueue().add(playerPlot);
            }
            player.getInventory().clear();
            pluginArena.getPlugin().getVoteItems().giveVoteItems(player);
            user.setStatistic("LOCAL_POINTS", 3);
          }

          pluginArena.setArenaInGameState(BaseArena.ArenaInGameState.PLOT_VOTING);
          voteForNextPlot(pluginArena);
        }
        break;
      case PLOT_VOTING:
        handlePlotVoting(pluginArena);
        if(arena.getTimer() <= 0) {
          calculatePlotResults(pluginArena);
          if(pluginArena.getQueue().isEmpty()) {
            pluginArena.calculateWinnerPlot();
            adjustStatistics(pluginArena);

            pluginArena.teleportToWinnerPlot();
            getPlugin().getArenaManager().stopGame(false, arena);
            pluginArena.executeEndRewards();
          } else {
            voteForNextPlot(pluginArena);
          }
        }
        break;
      default:
        break;
    }
    // no players - stop game
    if(!pluginArena.enoughPlayersToContinue()) {
      getPlugin().getArenaManager().stopGame(true, pluginArena);
    }
  }

  private void calculatePlotResults(BuildArena pluginArena) {
    Plot votingPlot = pluginArena.getVotingPlot();

    if(votingPlot == null || votingPlot.getPoints() != 0) {
      return;
    }

    boolean hidePlotOwner = getPlugin().getConfigPreferences().getOption("PLOT_HIDE_OWNER");
    String formattedMembers = "";

    if(hidePlotOwner) {
      formattedMembers = votingPlot.getFormattedMembers();
    }
    List<Plot> plotsVoted = new ArrayList<>();
    for(Player player : pluginArena.getPlayersLeft()) {
      if(hidePlotOwner) {
        new MessageBuilder("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_WAS").asKey().arena(pluginArena).player(player).value(formattedMembers).sendArena();
        new TitleBuilder("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_TITLE").asKey().arena(pluginArena).player(player).value(formattedMembers).sendArena();
      }

      IUser user = getPlugin().getUserManager().getUser(player);
      int points = user.getStatistic("LOCAL_POINTS");

      //no vote made, in this case make it a good vote
      if(points == 0) {
        points = 3;
      }
      Plot plot = pluginArena.getPlotFromPlayer(player);
      if(!votingPlot.getMembers().contains(player)) {
        if(!plotsVoted.contains(plot)) {
          votingPlot.addPoints(points);
          plotsVoted.add(plot);
        }
      }

      user.setStatistic("LOCAL_POINTS", 3);
    }
  }

  private void adjustStatistics(BuildArena pluginArena) {
    for(Plot plot : pluginArena.getPlotManager().getTopPlotsOrder()) {
      plot.getMembers().forEach(player -> {
        IUser user = getPlugin().getUserManager().getUser(player);
        if(plot.getPoints() > user.getStatistic("POINTS_HIGHEST")) {
          user.setStatistic("POINTS_HIGHEST", plot.getPoints());
        }
        user.adjustStatistic("POINTS_TOTAL", plot.getPoints());
        if(plot == pluginArena.getWinnerPlot()) {
          user.adjustStatistic("WINS", 1);
        } else {
          user.adjustStatistic("LOSES", 1);
        }
        getPlugin().getUserManager().addExperience(player, 5);
        if(plot.getPoints() > user.getStatistic("POINTS_HIGHEST_WIN")) {
          user.setStatistic("POINTS_HIGHEST_WIN", plot.getPoints());
        }
      });
    }
  }

  private void handleThemeVoting(BuildArena pluginArena) {
    for(Player player : pluginArena.getPlayersLeft()) {
      pluginArena.getVoteMenu().updateInventory(player);
    }
  }

  private void handleBuildTime(BuildArena pluginArena) {
    int timer = pluginArena.getTimer();
    for(int timers : getPlugin().getConfig().getIntegerList("Time-Manager.Time-Left-Intervals")) {
      if(timers == timer) {
        pluginArena.sendBuildLeftTimeMessage();
        break;
      }
    }
    pluginArena.checkPlayerOutSidePlot();
  }

  private void handleOptionsMenu(BuildArena pluginArena) {
    if(pluginArena.getTimer() % 10 != 0) {
      return;
    }
    for(Player player : pluginArena.getPlayersLeft()) {
      pluginArena.getPlugin().getSpecialItemManager().getSpecialItem("OPTIONS_MENU").setItem(player);
    }
  }

  private void handlePlotVoting(BuildArena pluginArena) {

  }

  public void voteForNextPlot(BuildArena pluginArena) {
    Plot votingPlot = pluginArena.getVotingPlot();

    if(votingPlot != null) {
      if(votingPlot.getPoints() == 0) {
        List<Plot> plotsVoted = new ArrayList<>();
        for(Player player : pluginArena.getPlayersLeft()) {
          IUser user = getPlugin().getUserManager().getUser(player);
          Plot plot = pluginArena.getPlotFromPlayer(player);
          if(!votingPlot.getMembers().contains(player)) {
            if(!plotsVoted.contains(plot)) {
              votingPlot.addPoints(user.getStatistic("LOCAL_POINTS"));
              plotsVoted.add(plot);
            }
          }

          user.setStatistic("LOCAL_POINTS", 3);

          VoteItems voteItems = pluginArena.getPlugin().getVoteItems();

          if(!player.getInventory().contains(voteItems.getReportItem())) {
            player.getInventory().setItem(voteItems.getReportVoteItem().getSlot(), voteItems.getReportVoteItem().getItemStack());
            player.updateInventory();
          }
        }
      }

      if(!votingPlot.getMembers().isEmpty() && getPlugin().getConfigPreferences().getOption("PLOT_HIDE_OWNER")) {
        String formattedMembers = votingPlot.getFormattedMembers();

        new MessageBuilder("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_WAS").asKey().arena(pluginArena).value(formattedMembers).sendArena();
        new TitleBuilder("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_TITLE").asKey().arena(pluginArena).value(formattedMembers).sendArena();
      }
    }

    voteRoutine(pluginArena);
  }

  public void voteRoutine(BuildArena pluginArena) {
    if(pluginArena.getQueue().isEmpty()) {
      return;
    }

    setArenaTimer(getPlugin().getConfig().getInt("Time-Manager." + pluginArena.getArenaType().getPrefix() + ".Voting.Plot"));

    Plot plot = pluginArena.getQueue().poll();

    while(plot == null && !pluginArena.getQueue().isEmpty()) {
      // should not happen anymore... to be removed
      getPlugin().getDebugger().debug("A PLAYER HAS NO PLOT!");
      plot = pluginArena.getQueue().poll();
    }

    if(pluginArena.getQueue().isEmpty() && plot == null) {
      pluginArena.setVotingPlot(null);
      return;
    }

    pluginArena.setVotingPlot(plot);

    if(plot == null) {
      return;
    }

    Location teleportLoc = plot.getTeleportLocation();
    String formattedMembers = plot.getFormattedMembers();
    boolean hidePlotOwner = getPlugin().getConfigPreferences().getOption("PLOT_HIDE_OWNER");

    for(Player player : pluginArena.getPlayersLeft()) {
      VersionUtils.teleport(player, teleportLoc);
      player.setPlayerWeather(plot.getWeatherType());
      player.setPlayerTime(Plot.Time.format(plot.getTime(), player.getWorld().getTime()), false);

      if(hidePlotOwner) {
        new MessageBuilder("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_NEXT").asKey().arena(pluginArena).value("???").player(player).sendPlayer();
      } else {
        new TitleBuilder("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_TITLE").asKey().arena(pluginArena).value(formattedMembers).player(player).sendPlayer();
        new MessageBuilder("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_NEXT").asKey().arena(pluginArena).value(formattedMembers).player(player).sendPlayer();
      }
    }

    for(Player spectator : pluginArena.getSpectators()) {
      VersionUtils.teleport(spectator, teleportLoc);
      spectator.setPlayerWeather(plot.getWeatherType());
      spectator.setPlayerTime(Plot.Time.format(plot.getTime(), spectator.getWorld().getTime()), false);

      new TitleBuilder("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_TITLE").asKey().arena(pluginArena).value(formattedMembers).player(spectator).sendPlayer();
      new MessageBuilder("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_NEXT").asKey().arena(pluginArena).value(formattedMembers).player(spectator).sendPlayer();
    }
  }
}
