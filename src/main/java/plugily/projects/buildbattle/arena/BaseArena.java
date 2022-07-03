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
import plugily.projects.buildbattle.arena.managers.MapRestorerManager;
import plugily.projects.buildbattle.arena.managers.ScoreboardManager;
import plugily.projects.buildbattle.arena.managers.plots.PlotManager;
import plugily.projects.buildbattle.handlers.menu.registry.particles.ParticleRefreshScheduler;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.language.TitleBuilder;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

import java.util.ArrayList;
import java.util.List;

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
  private String theme = "Theme";
  protected ParticleRefreshScheduler particleRefreshScheduler;
  private ArenaType arenaType;

  private ArenaInGameStage arenaInGameStage;

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

  public enum ArenaInGameStage {
    THEME_VOTING("Theme-Voting"), BUILD_TIME("Build-Time"), PLOT_VOTING("Plot-Voting");

    private final String prefix;

    ArenaInGameStage(String prefix) {
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
    //make sure to have no NPE
    return theme == null ? "Theme" : theme;
  }

  public void setTheme(String theme) {
    this.theme = theme == null ? "Theme" : theme;
  }

  public void sendBuildLeftTimeMessage() {
    new TitleBuilder("IN_GAME_MESSAGES_PLOT_TIME_LEFT_TITLE").asKey().arena(this).sendArena();
    String message = new MessageBuilder("IN_GAME_MESSAGES_PLOT_TIME_LEFT_CHAT").asKey().arena(this).build();
    for(Player p : getPlayers()) {
      VersionUtils.sendActionBar(p, message);
      p.sendMessage(message);
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

  public ArenaInGameStage getArenaInGameStage() {
    return arenaInGameStage;
  }

  public void setArenaInGameStage(ArenaInGameStage arenaInGameStage) {
    this.arenaInGameStage = arenaInGameStage;
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

}