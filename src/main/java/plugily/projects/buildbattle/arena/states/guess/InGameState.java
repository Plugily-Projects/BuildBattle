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

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.BuildArena;
import plugily.projects.buildbattle.old.ConfigPreferences;
import plugily.projects.buildbattle.old.arena.managers.plots.Plot;
import plugily.projects.buildbattle.old.handlers.language.LanguageManager;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.states.PluginInGameState;
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
          if(pluginArena.getVotePoll() != null) {
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
            getPlugin().getVoteItems().giveVoteItems(player);
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
              player.teleport(winnerLocation);
              String winner = getPlugin().getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Winner-Title");
              winner = formatWinners(pluginArena.getWinnerPlot(), winner);
              VersionUtils.sendTitle(player, winner, 5, 35, 5);
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
    if(arena.getTimer() == 30 || arena.getTimer() == 60 || arena.getTimer() == 120) {
      new TitleBuilder("IN_GAME_MESSAGES_ARENA_TIME_LEFT").asKey().arena(pluginArena).sendArena();
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
      String message = getPlugin().getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Voted-For-Player-Plot").replace("%PLAYER%", pluginArena.getVotingPlot().getFormattedMembers());
      for(Player player : pluginArena.getPlayersLeft()) {
        if(getPlugin().getConfigPreferences().getOption("HIDE_PLOT_OWNER")) {
          for(Player p : pluginArena.getPlayersLeft()) {
            String owner = getPlugin().getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Plot-Owner-Title");
            owner = formatWinners(pluginArena.getVotingPlot(), owner);
            VersionUtils.sendTitle(p, owner, 5, 40, 5);
            p.sendMessage(getPlugin().getChatManager().getPrefix() + message);
          }
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
    List<String> formattedSummary = LanguageManager.getLanguageList("In-Game.Messages.Voting-Messages.Summary");

    for(int b = 0; b < formattedSummary.size(); b++) {
      String message = getPlugin().getChatManager().colorRawMessage(formattedSummary.get(b));
      for(int i = 1; i < 4; i++) {
        String access = "One";
        switch(i) {
          case 2:
            access = "Two";
            break;
          case 3:
            access = "Three";
            break;
          default:
            break;
        }

        String accessLower = access.toLowerCase();

        if(message.contains("%place_" + accessLower + "%")) {
          List<Player> list = pluginArena.getTopList().get(i);

          if(list != null && !list.isEmpty()) {
            Plot plot = pluginArena.getPlotManager().getPlot(list.get(0));
            message = StringUtils.replace(message, "%place_" + accessLower + "%", getPlugin().getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Place-" + access)
                .replace("%player%", formatWinners(list))
                .replace("%number%", plot == null ? "" : Integer.toString(plot.getPoints())));
          } else {
            message = StringUtils.replace(message, "%place_" + accessLower + "%", getPlugin().getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Place-" + access)
                .replace("%player%", "None")
                .replace("%number%", "none"));
          }
        }
      }
      formattedSummary.set(b, message);
    }
    pluginArena.getPlayers().forEach(player -> formattedSummary.forEach(msg -> MiscUtils.sendCenteredMessage(player, msg)));
    for(Map.Entry<Integer, List<Player>> map : pluginArena.getTopList().entrySet()) {
      for(Player p : map.getValue()) {
        if(map.getKey() > 3) {
          p.sendMessage(getPlugin().getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Summary-Other-Place").replace("%number%", Integer.toString(map.getKey())));
        }
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

  public String formatWinners(Plot plot, String string) {
    return string.replace("%player%", plot.getFormattedMembers());
  }

  private void handleThemeVoting(BuildArena pluginArena) {
    for(Player player : pluginArena.getPlayers()) {
      player.openInventory(pluginArena.getVoteMenu().getInventory());
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
          if(!player.getInventory().contains(getPlugin().getVoteItems().getReportItem())) {
            player.getInventory().setItem(getPlugin().getVoteItems().getReportVoteItem().getSlot(), getPlugin().getVoteItems().getReportVoteItem().getItemStack());
            player.updateInventory();
          }
        }
      }
      if(!pluginArena.getVotingPlot().getMembers().isEmpty() && getPlugin().getConfigPreferences().getOption("HIDE_PLOT_OWNER")) {
        String message = getPlugin().getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Voted-For-Player-Plot").replace("%PLAYER%", pluginArena.getVotingPlot().getFormattedMembers());
        for(Player p : pluginArena.getPlayersLeft()) {
          String owner = getPlugin().getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Plot-Owner-Title");
          owner = formatWinners(pluginArena.getVotingPlot(), owner);
          VersionUtils.sendTitle(p, owner, 5, 40, 5);
          p.sendMessage(getPlugin().getChatManager().getPrefix() + message);
        }
      }
    }
    voteRoutine();
  }

  public void voteRoutine() {
    if(!queue.isEmpty()) {
      setTimer(getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.PLOT_VOTE, this));
      Plot plot = queue.poll();
      while(plot == null && !queue.isEmpty()) {
        // should not happen anymore... to be removed
        System.out.print("A PLAYER HAS NO PLOT!");
        plot = queue.poll();
      }
      if(queue.isEmpty() && plot == null) {
        votingPlot = null;
        return;
      }

      // getPlotManager().teleportAllToPlot(plotManager.getPlot(player.getUniqueId()));
      votingPlot = plot;
      String message = getPlugin().getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Voting-For-Player-Plot").replace("%PLAYER%", votingPlot.getFormattedMembers());

      Location teleportLoc = votingPlot.getTeleportLocation();

      for(Player p : getPlayers()) {
        p.teleport(teleportLoc);
        p.setPlayerWeather(votingPlot.getWeatherType());
        p.setPlayerTime(Plot.Time.format(votingPlot.getTime(), p.getWorld().getTime()), false);
        if(getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.ANNOUNCE_PLOTOWNER_LATER)) {
          p.sendMessage(getPlugin().getChatManager().getPrefix() + getPlugin().getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Vote-For-Next-Plot"));
        } else {
          String owner = getPlugin().getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Plot-Owner-Title");
          owner = formatWinners(votingPlot, owner);
          VersionUtils.sendTitle(p, owner, 5, 40, 5);
          p.sendMessage(getPlugin().getChatManager().getPrefix() + message);
        }
      }

      for(Player spectator : getSpectators()) {
        spectator.teleport(teleportLoc);
        spectator.setPlayerWeather(votingPlot.getWeatherType());
        spectator.setPlayerTime(Plot.Time.format(votingPlot.getTime(), spectator.getWorld().getTime()), false);
        String owner = getPlugin().getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Plot-Owner-Title");
        owner = formatWinners(votingPlot, owner);
        VersionUtils.sendTitle(spectator, owner, 5, 40, 5);
        spectator.sendMessage(getPlugin().getChatManager().getPrefix() + message);
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
          player.teleport(buildPlot.getTeleportLocation());
          player.sendMessage(getPlugin().getChatManager().getPrefix() + getPlugin().getChatManager().colorMessage("In-Game.Messages.Cant-Fly-Outside-Plot"));
        }
      }
    }
    pluginArena.changeArenaOptionBy("IN_PLOT_CHECKER", 1);
  }
}
