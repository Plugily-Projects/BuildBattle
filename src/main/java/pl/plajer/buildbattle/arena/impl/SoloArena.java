/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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
 */

package pl.plajer.buildbattle.arena.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import pl.plajer.buildbattle.ConfigPreferences;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.arena.managers.plots.Plot;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.handlers.language.LanguageManager;
import pl.plajer.buildbattle.menus.themevoter.VoteMenu;
import pl.plajer.buildbattle.menus.themevoter.VotePoll;
import pl.plajer.buildbattle.user.User;
import pl.plajerlair.core.utils.MinigameUtils;

/**
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 */
public class SoloArena extends BaseArena {

  private Map<Integer, List<Player>> topList = new HashMap<>();
  private String theme = "Theme";
  private boolean receivedVoteItems;
  private Queue<Player> queue = new LinkedList<>();
  private Plot votingPlot = null;
  private boolean voteTime;
  private boolean themeVoteTime = true;
  private boolean themeTimerSet = false;
  private int buildTime;
  private VoteMenu voteMenu;

  public SoloArena(String id, Main plugin) {
    super(id, plugin);
  }

  /**
   * Initiates voting poll
   */
  public void initPoll() {
    voteMenu = new VoteMenu(this);
    voteMenu.resetPoll();
  }

  public VotePoll getVotePoll() {
    return voteMenu.getVotePoll();
  }

  public VoteMenu getVoteMenu() {
    return voteMenu;
  }

  /**
   * Is voting time in game?
   *
   * @return true = voting time, false = no
   */
  public boolean isVoting() {
    return voteTime;
  }

  public void setVoting(boolean voting) {
    voteTime = voting;
  }

  public boolean isThemeVoteTime() {
    return themeVoteTime;
  }

  public void setThemeVoteTime(boolean themeVoteTime) {
    this.themeVoteTime = themeVoteTime;
  }

  public int getBuildTime() {
    return buildTime;
  }

  public Queue<Player> getQueue() {
    return queue;
  }

  @Override
  public void run() {

  }

  /**
   * Get current arena theme
   *
   * @return arena theme String
   */
  public String getTheme() {
    return theme;
  }

  public void setTheme(String theme) {
    this.theme = theme;
  }

  @Override
  public void updateBossBar() {
    switch (getArenaState()) {
      case WAITING_FOR_PLAYERS:
        getGameBar().setTitle(ChatManager.colorMessage("Bossbar.Waiting-For-Players"));
        break;
      case STARTING:
        getGameBar().setTitle(ChatManager.colorMessage("Bossbar.Starting-In").replace("%time%", String.valueOf(getTimer())));
        break;
      case IN_GAME:
        if (!isVoting()) {
          getGameBar().setTitle(ChatManager.colorMessage("Bossbar.Time-Left").replace("%time%", String.valueOf(getTimer())));
        } else {
          getGameBar().setTitle(ChatManager.colorMessage("Bossbar.Vote-Time-Left").replace("%time%", String.valueOf(getTimer())));
        }
        break;
    }
  }

  private void voteRoutine() {
    if (!queue.isEmpty()) {
      setTimer(getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.PLOT_VOTE, this));
      Player player = queue.poll();
      while (getPlotManager().getPlot(player) == null && !queue.isEmpty()) {
        System.out.print("A PLAYER HAS NO PLOT!");
        player = queue.poll();
      }
      if (queue.isEmpty() && getPlotManager().getPlot(player) == null) {
        setVotingPlot(null);
      } else {
        // getPlotManager().teleportAllToPlot(plotManager.getPlot(player.getUniqueId()));
        setVotingPlot(getPlotManager().getPlot(player));
        String message = ChatManager.colorMessage("In-Game.Messages.Voting-Messages.Voting-For-Player-Plot").replace("%PLAYER%", player.getName());
        for (Player p : getPlayers()) {
          p.teleport(getVotingPlot().getTeleportLocation());
          p.setPlayerWeather(getVotingPlot().getWeatherType());
          p.setPlayerTime(Plot.Time.format(getVotingPlot().getTime(), p.getWorld().getTime()), false);
          String owner = ChatManager.colorMessage("In-Game.Messages.Voting-Messages.Plot-Owner-Title");
          if (getArenaType() == BaseArena.ArenaType.TEAM) {
            if (getVotingPlot().getOwners().size() == 1) {
              owner = owner.replace("%player%", player.getName());
            } else {
              owner = owner.replace("%player%", getVotingPlot().getOwners().get(0).getName() + " & " + getVotingPlot().getOwners().get(1).getName());
            }
          } else {
            owner = owner.replace("%player%", player.getName());
          }
          p.sendTitle(owner, null, 5, 40, 5);
          p.sendMessage(ChatManager.getPrefix() + message);
        }
      }
    }

  }

  /**
   * Get plot where players are voting currently
   *
   * @return Plot object where players are voting
   */
  public Plot getVotingPlot() {
    return votingPlot;
  }

  private void setVotingPlot(Plot buildPlot) {
    votingPlot = buildPlot;
  }

  private void announceResults() {
    List<String> messages = LanguageManager.getLanguageList("In-Game.Messages.Voting-Messages.Summary");
    List<String> formattedSummary = new ArrayList<>();
    for (String summary : messages) {
      String message = summary;
      message = ChatManager.colorRawMessage(message);
      for (int i = 1; i < 4; i++) {
        String access = "One";
        switch (i) {
          case 1:
            access = "One";
            break;
          case 2:
            access = "Two";
            break;
          case 3:
            access = "Three";
            break;
        }
        if (message.contains("%place_" + access.toLowerCase() + "%")) {
          if (topList.containsKey(i) && topList.get(i) != null && !topList.get(i).isEmpty()) {
            message = StringUtils.replace(message, "%place_" + access.toLowerCase() + "%", ChatManager.colorMessage("In-Game.Messages.Voting-Messages.Place-" + access)
                .replace("%player%", formatWinners(topList.get(i)))
                .replace("%number%", String.valueOf(getPlotManager().getPlot(topList.get(i).get(0)).getPoints())));
          } else {
            message = StringUtils.replace(message, "%place_" + access.toLowerCase() + "%", ChatManager.colorMessage("In-Game.Messages.Voting-Messages.Place-" + access)
                .replace("%player%", "None")
                .replace("%number%", "none"));
          }
        }
      }
      formattedSummary.add(message);
    }
    getPlayers().forEach((player) -> formattedSummary.forEach((msg) -> MinigameUtils.sendCenteredMessage(player, msg)));
    for (Integer rang : topList.keySet()) {
      if (topList.get(rang) != null) {
        for (Player p : topList.get(rang)) {
          if (rang > 3) {
            p.sendMessage(ChatManager.colorMessage("In-Game.Messages.Voting-Messages.Summary-Other-Place").replace("%number%", String.valueOf(rang)));
          }
          User user = getPlugin().getUserManager().getUser(p.getUniqueId());
          if (rang != 1) {
            user.addStat(StatsStorage.StatisticType.LOSES, 1);
            continue;
          }
          Plot plot = getPlotManager().getPlot(p);
          user.addStat(StatsStorage.StatisticType.WINS, 1);
          if (plot.getPoints() > user.getStat(StatsStorage.StatisticType.HIGHEST_WIN)) {
            user.setStat(StatsStorage.StatisticType.HIGHEST_WIN, plot.getPoints());
          }
        }
      }
    }
  }

  private String formatWinners(final List<Player> winners) {
    List<Player> players = new ArrayList<>(winners);
    StringBuilder builder = new StringBuilder(players.get(0).getName());
    if (players.size() == 1) {
      return builder.toString();
    } else {
      players.remove(0);
      for (Player p : players) {
        builder.append(" & ").append(p.getName());
      }
      return builder.toString();
    }
  }

  private void calculateResults() {
    for (int b = 1; b <= getPlayers().size(); b++) {
      topList.put(b, new ArrayList<>());
    }
    for (Plot buildPlot : getPlotManager().getPlots()) {
      long i = buildPlot.getPoints();
      for (int rang : topList.keySet()) {
        if (topList.get(rang) == null || topList.get(rang).isEmpty() || topList.get(rang).get(0) == null || getPlotManager().getPlot(topList.get(rang).get(0)) == null) {
          topList.put(rang, buildPlot.getOwners());
          break;
        }
        if (i > getPlotManager().getPlot(topList.get(rang).get(0)).getPoints()) {
          moveScore(rang, buildPlot.getOwners());
          break;
        }
        if (i == getPlotManager().getPlot(topList.get(rang).get(0)).getPoints()) {
          List<Player> winners = topList.get(rang);
          winners.addAll(buildPlot.getOwners());
          topList.put(rang, winners);
          break;
        }
      }
    }
  }

  private void moveScore(int pos, List<Player> owners) {
    List<Player> after = topList.get(pos);
    topList.put(pos, owners);
    if (!(pos > getPlayers().size()) && after != null) {
      moveScore(pos + 1, after);
    }
  }

}
