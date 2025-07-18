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

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.managers.MapRestorerManager;
import plugily.projects.buildbattle.arena.managers.ScoreboardManager;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.arena.managers.plots.PlotManager;
import plugily.projects.buildbattle.handlers.menu.registry.particles.ParticleRefreshScheduler;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.language.TitleBuilder;
import plugily.projects.minigamesbox.classic.handlers.reward.RewardType;
import plugily.projects.minigamesbox.classic.handlers.reward.RewardsFactory;
import plugily.projects.minigamesbox.classic.utils.actionbar.ActionBar;
import plugily.projects.minigamesbox.classic.utils.items.HandlerItem;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 17.12.2021
 */
public class BaseArena extends PluginArena {

  private static Main plugin;
  private final List<Player> spectators = new ArrayList<>();
  private MapRestorerManager mapRestorerManager;
  private final PlotManager plotManager;
  private String theme = null;
  protected ParticleRefreshScheduler particleRefreshScheduler;
  private ArenaType arenaType;

  private ArenaInGameState arenaInGameState = ArenaInGameState.NONE;

  private Map<Player, Plot> plotList = new HashMap<>();

  private Plot winnerPlot;

  public BaseArena(String id) {
    super(id);
    setPluginValues();
    setScoreboardManager(new ScoreboardManager(this));
    mapRestorerManager = new MapRestorerManager(this);
    plotManager = new PlotManager(this);
    setMapRestorerManager(mapRestorerManager);
  }

  public static void init(Main plugin) {
    BaseArena.plugin = plugin;
  }

  @Override
  public Main getPlugin() {
    return plugin;
  }


  @Override
  public MapRestorerManager getMapRestorerManager() {
    return mapRestorerManager;
  }


  private void setPluginValues() {
  }


  public void addSpectatorPlayer(Player player) {
    spectators.add(player);
  }

  public void removeSpectatorPlayer(Player player) {
    spectators.remove(player);
  }

  public boolean isSpectatorPlayer(Player player) {
    return spectators.contains(player);
  }

  public void cleanUpArena() {
    spectators.clear();
    plotList.clear();
    setTheme(null);
    winnerPlot = null;
  }

  public List<Player> getSpectators() {
    return spectators;
  }

  public enum ArenaType {
    SOLO("Classic"), TEAM("Teams"), GUESS_THE_BUILD("Guess-The-Build");

    private final String prefix;

    ArenaType(String prefix) {
      this.prefix = prefix;
    }

    public String getPrefix() {
      return prefix;
    }
  }

  public enum ArenaInGameState {
    THEME_VOTING("Theme-Voting"), BUILD_TIME("Build-Time"), PLOT_VOTING("Plot-Voting"), NONE("NONE");

    private final String prefix;

    ArenaInGameState(String prefix) {
      this.prefix = prefix;
    }

    public String getPrefix() {
      return prefix;
    }
  }

  /**
   * Get current arena theme
   *
   * @return arena theme String or "Theme" as default
   */
  @NotNull
  public String getTheme() {
    return theme;
  }

  public void setTheme(String arenaTheme) {
    this.theme = arenaTheme;
  }

  public void sendBuildLeftTimeMessage() {
    new TitleBuilder("IN_GAME_MESSAGES_PLOT_TIME_LEFT_TITLE").asKey().arena(this).sendArena();
    MessageBuilder message = new MessageBuilder("IN_GAME_MESSAGES_PLOT_TIME_LEFT_CHAT").asKey().arena(this);
    message.sendArena();
    getPlayers().forEach(player ->
        getPlugin().getActionBarManager().addActionBar(player, new ActionBar(message,
            ActionBar.ActionBarType.DISPLAY)));
  }

  public final void checkPlayerOutSidePlot() {
    if(plugin.getConfigPreferences().getOption("PLOT_MOVE_OUTSIDE")) {
      return;
    }

    if(getArenaOption("IN_PLOT_CHECKER") >= 3) {
      setArenaOption("IN_PLOT_CHECKER", 0);

      for(Player player : getPlayersLeft()) {
        Plot buildPlot = null;

        if(this instanceof BuildArena) {
          buildPlot = getPlotFromPlayer(player);
        } else if(this instanceof GuessArena) {
          buildPlot = ((GuessArena) this).getBuildPlot();
          player.setPlayerWeather(buildPlot.getWeatherType());
          player.setPlayerTime(Plot.Time.format(buildPlot.getTime(), player.getWorld().getTime()), false);
        }

        if(buildPlot != null && buildPlot.getCuboid() != null && !buildPlot.getCuboid().isInWithMarge(player.getLocation(), 5)) {
          VersionUtils.teleport(player, buildPlot.getTeleportLocation());
          new MessageBuilder("IN_GAME_MESSAGES_PLOT_PERMISSION_OUTSIDE").asKey().arena(this).player(player).sendPlayer();
        }
      }
    }

    changeArenaOptionBy("IN_PLOT_CHECKER", 1);
  }

  public void calculateWinnerPlot() {
    winnerPlot = plotManager.getTopPlotsOrder().get(0);
  }

  public void executeEndRewards() {
    RewardsFactory rewards = plugin.getRewardsHandler();
    RewardType placeRewardType = rewards.getRewardType("PLACE");

    List<Plot> plotsRanking = plotManager.getTopPlotsOrder();

    plotsRanking.forEach(plot -> plot.getMembers().forEach(player -> rewards.performReward(player, this, placeRewardType, plotsRanking.indexOf(plot) + 1)));
    plugin.getRewardsHandler().performReward(this, plugin.getRewardsHandler().getRewardType("END_GAME"));
  }

  public void teleportToWinnerPlot() {
    Location winnerLocation = winnerPlot.getTeleportLocation();
    String formattedMembers = winnerPlot.getFormattedMembers();
    for(Player player : getPlayers()) {
      VersionUtils.teleport(player, winnerLocation);
      new TitleBuilder("IN_GAME_MESSAGES_PLOT_VOTING_WINNER").asKey().player(player).value(formattedMembers).sendPlayer();
    }
  }

  public PlotManager getPlotManager() {
    return plotManager;
  }

  public ArenaType getArenaType() {
    return arenaType;
  }

  public void setArenaType(ArenaType arenaType) {
    this.arenaType = arenaType;
  }

  public ArenaInGameState getArenaInGameState() {
    return arenaInGameState;
  }

  public void setArenaInGameState(ArenaInGameState arenaInGameState) {
    this.arenaInGameState = arenaInGameState;
  }

  public ParticleRefreshScheduler getParticleRefreshScheduler() {
    return particleRefreshScheduler;
  }

  public void setParticleRefreshScheduler(ParticleRefreshScheduler particleRefreshScheduler) {
    this.particleRefreshScheduler = particleRefreshScheduler;
  }

  public boolean enoughPlayersToContinue() {
    return true;
  }

  public void distributePlots() {
  }

  public Map<Player, Plot> getPlotList() {
    return plotList;
  }

  public Plot getPlotFromPlayer(Player player) {
    return plotList.get(player);
  }

  public Plot getWinnerPlot() {
    return winnerPlot;
  }

  public void setWinnerPlot(Plot winnerPlot) {
    this.winnerPlot = winnerPlot;
  }

}