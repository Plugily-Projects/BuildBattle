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

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import plugily.projects.buildbattle.api.event.guess.PlayerThemeGuessEvent;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.arena.states.guess.InGameState;
import plugily.projects.buildbattle.arena.states.guess.StartingState;
import plugily.projects.buildbattle.handlers.themes.BBTheme;
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 29.05.2022
 */
public class GuessArena extends BaseArena {

  private final List<Player> whoGuessed = new ArrayList<>();
  private int round = 1;
  private BBTheme currentTheme = null;
  private Player winner;
  private Player currentBuilder;

  private Plot buildPlot = null;
  //ToDo consider guess together with plot members
  private Map<Player, Integer> playersPoints = new HashMap<>();
  private List<Integer> removedCharsAt = new ArrayList<>();

  private final int plotMemberSize = getArenaOption("PLOT_MEMBER_SIZE");

  private List<Plot> playedPlots = new ArrayList<>();

  private List<String> playedThemes = new ArrayList<>();

  public GuessArena(String id) {
    super(id);
    setArenaType(ArenaType.GUESS_THE_BUILD);
    addGameStateHandler(ArenaState.IN_GAME, new InGameState());
    addGameStateHandler(ArenaState.STARTING, new StartingState());
    getPlugin().getDebugger().debug("Init Arena {0} with ArenaType {1}", getId(), getArenaType());
  }

  @Override
  public void cleanUpArena() {
    currentBuilder = null;
    currentTheme = null;
    round = 1;
    whoGuessed.clear();
    playersPoints.clear();
    removedCharsAt.clear();
    winner = null;
    playedPlots.clear();
    playedThemes.clear();
    super.cleanUpArena();
  }

  @Override
  public void distributePlots() {
    int neededPlots = getPlayers().size() / plotMemberSize;
    if(getPlotManager().getPlots().size() < neededPlots) {
      getPlugin().getMessageUtils().errorOccurred();
      getPlugin().getDebugger().sendConsoleMsg("&c[Build Battle] [PLOT WARNING] Not enough plots in arena " + getId() + "! Lacks " + (neededPlots - getPlotManager().getPlots().size()) + " plots");
      getPlugin().getDebugger().sendConsoleMsg("&c[PLOT WARNING] Required " + neededPlots + " but have " + getPlotManager().getPlots().size());
      getPlugin().getDebugger().sendConsoleMsg("&c[PLOT WARNING] Instance was stopped!");
      getPlugin().getArenaManager().stopGame(true, this);
    }
    List<Player> players = new ArrayList<>(getPlayersLeft());
    for(Plot plot : getPlotManager().getPlots()) {
      if(players.isEmpty()) {
        break;
      }
      if(!getPlugin().getUserManager().getUser(players.get(0)).isSpectator()) {
        plot.addMember(players.remove(0), this, true);
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
    int size = getPlayers().size();

    if(size > plotMemberSize) {
      return true;
    }
    if(size == plotMemberSize) {
      return !new HashSet<>(getPlotManager().getPlot(getPlayersLeft().get(0)).getMembers()).containsAll(getPlayers());
    }
    return false;
  }

  public void setNextPlot() {
    List<Plot> plots = new ArrayList<>(getPlotList().values());
    plots.removeAll(playedPlots);
    if(plots.isEmpty()) {
      plots = new ArrayList<>(getPlotList().values());
      playedPlots.clear();
    }
    Plot nextPlot = plots.get(getPlugin().getRandom().nextInt(plots.size()));
    playedPlots.add(nextPlot);
    buildPlot = nextPlot;
    getPlugin().getDebugger().debug("Arena {0} Next BuilderTeam is {1}", getId(), nextPlot.getFormattedMembers());
    teleportPlayersToPlot(nextPlot);
  }

  public void resetBuildPlot() {
    buildPlot.resetPlot();
    getRemovedCharsAt().clear();
    setBuildPlot(null);
    setCurrentTheme(null);
    getWhoGuessed().clear();
  }

  public void teleportPlayersToPlot(Plot plot) {
    Location plotLoc = plot.getTeleportLocation();
    for(Player player : getPlayers()) {
      VersionUtils.teleport(player, plotLoc);
      player.setPlayerWeather(plot.getWeatherType());
      player.setPlayerTime(Plot.Time.format(plot.getTime(), player.getWorld().getTime()), false);
      VersionUtils.setCollidable(player, false);
      player.setGameMode(GameMode.ADVENTURE);
      player.setAllowFlight(true);
      player.setFlying(true);
      player.getInventory().clear();
      player.updateInventory();
    }
  }

  @Override
  public void setMinimumPlayers(int amount) {
    if(amount <= plotMemberSize) {
      getPlugin().getDebugger().debug("Minimum players amount for TEAM game mode arena cannot be less than 3! Setting amount to 3!");
      setArenaOption("MINIMUM_PLAYERS", 3);
      return;
    }
    super.setMinimumPlayers(amount);
  }

  public void recalculateLeaderboard() {
    playersPoints = playersPoints.entrySet()
        .stream()
        .sorted((Map.Entry.<Player, Integer>comparingByValue().reversed()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }

  public void addWhoGuessed(Player player) {
    whoGuessed.add(player);
    getPlugin().getRewardsHandler().performReward(player, this, getPlugin().getRewardsHandler().getRewardType("GUESS"), -1);

    int timer = getTimer();

    //decrease game time by guessed theme
    if(timer >= 15) {
      setTimer(timer - getPlugin().getConfig().getInt("Time-Manager." + getArenaType().getPrefix() + ".Guess"));
    }

    //-1 because builder can´t guess
    if(whoGuessed.size() >= getPlayers().size() - 1) {
      setTimer(getPlugin().getConfig().getInt("Time-Manager." + getArenaType().getPrefix() + ".Round-Delay"));
      setArenaInGameState(ArenaInGameState.PLOT_VOTING);

      new MessageBuilder("IN_GAME_MESSAGES_PLOT_GTB_THEME_GUESSED").asKey().arena(this).sendArena();

      getPlugin().getRewardsHandler().performReward(this, getPlugin().getRewardsHandler().getRewardType("GUESS_ALL"));
    }
  }

  public void broadcastPlayerGuessed(Player player) {
    new MessageBuilder("IN_GAME_MESSAGES_PLOT_GTB_THEME_GUESS_GUESSED").asKey().arena(this).player(player).sendArena();

    new MessageBuilder("IN_GAME_MESSAGES_PLOT_GTB_THEME_GUESS_POINTS").asKey().arena(this).player(player).sendPlayer();

    int bonusAmount = getPlugin().getConfig().getInt("Guessing-Points." + (whoGuessed.size() + 1), 0);

    playersPoints.put(player, playersPoints.getOrDefault(player, 0) + currentTheme.getDifficulty().getPointsReward() + bonusAmount);

    playersPoints.put(currentBuilder, playersPoints.getOrDefault(currentBuilder, 0) + getPlugin().getConfig().getInt("Guessing-Points.Builder", 1));

    getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
      addWhoGuessed(player);
      Bukkit.getPluginManager().callEvent(new PlayerThemeGuessEvent(this, currentTheme));
      recalculateLeaderboard();
    });
  }

  public BBTheme getCurrentBBTheme() {
    return currentTheme;
  }

  public boolean isCurrentThemeSet() {
    return currentTheme != null;
  }

  public List<Player> getCurrentBuilders() {
    if(buildPlot == null) {
      return new ArrayList<>();
    }
    return buildPlot.getMembers();
  }

  public Plot getBuildPlot() {
    return buildPlot;
  }

  public void setBuildPlot(Plot buildPlot) {
    this.buildPlot = buildPlot;
  }


  public List<String> getPlayedThemes() {
    return playedThemes;
  }

  public int getRound() {
    return round;
  }

  public void setCurrentTheme(BBTheme currentTheme) {
    getPlugin().getDebugger().debug("Arena {0} set Theme to {1} ({2})", getId(), currentTheme.getTheme(), currentTheme.getDifficulty());
    this.currentTheme = currentTheme;
  }

  public List<Player> getWhoGuessed() {
    return whoGuessed;
  }

  public Player getWinner() {
    return winner;
  }

  public void setWinner(Player winner) {
    this.winner = winner;
  }

  public List<Integer> getRemovedCharsAt() {
    return removedCharsAt;
  }

  public Map<Player, Integer> getPlayersPoints() {
    return playersPoints;
  }

}
