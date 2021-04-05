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

package plugily.projects.buildbattle.arena.impl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion;
import pl.plajerlair.commonsbox.minecraft.compat.VersionUtils;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.string.StringFormatUtils;
import plugily.projects.buildbattle.ConfigPreferences;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.api.event.game.BBGameChangeStateEvent;
import plugily.projects.buildbattle.arena.ArenaState;
import plugily.projects.buildbattle.arena.managers.ScoreboardManager;
import plugily.projects.buildbattle.arena.managers.plots.PlotManager;
import plugily.projects.buildbattle.arena.options.ArenaOption;
import plugily.projects.buildbattle.menus.options.registry.particles.ParticleRefreshScheduler;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * @author Plajer
 * <p>
 * Created at 18.01.2019
 */
public class BaseArena extends BukkitRunnable {

  private final List<Player> players = new ArrayList<>();
  private final List<Player> spectators = new ArrayList<>();

  //instead of 2 (lobby, end) location fields we use map with GameLocation enum
  private final Map<GameLocation, Location> gameLocations = new EnumMap<>(GameLocation.class);
  //all arena values that are integers, contains constant and floating values
  private final Map<ArenaOption, Integer> arenaOptions = new EnumMap<>(ArenaOption.class);

  private final Main plugin;
  private final String id;
  private final PlotManager plotManager;
  private final ScoreboardManager scoreboardManager;
  private String mapName = "";
  //todo move?
  private String theme = "Theme";
  private ArenaState arenaState;
  private BossBar gameBar;
  private ArenaType arenaType;
  private boolean forceStart = false;
  private boolean ready = true;

  protected ParticleRefreshScheduler particleRefreshSched;

  public BaseArena(String id, Main plugin) {
    arenaState = ArenaState.WAITING_FOR_PLAYERS;
    this.plugin = plugin;
    this.id = id == null ? "" : id;
    if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BOSSBAR_ENABLED) && ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
      gameBar = Bukkit.createBossBar(plugin.getChatManager().colorMessage("Bossbar.Waiting-For-Players"), BarColor.BLUE, BarStyle.SOLID);
    }
    plotManager = new PlotManager(this);
    scoreboardManager = new ScoreboardManager(this);
    for(ArenaOption option : ArenaOption.values()) {
      arenaOptions.put(option, option.getDefaultValue());
    }
  }

  /**
   * Checks if arena is validated and ready to play
   *
   * @return true = ready, false = not ready either you must validate it or it's wrongly created
   */
  public boolean isReady() {
    return ready;
  }

  public void setReady(boolean ready) {
    this.ready = ready;
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

  @Override
  public void run() {
  }

  public void start() {
    runTaskTimer(plugin, 20L, 20L);
  }

  /**
   * Executes boss bar action for arena
   *
   * @param action add or remove a player from boss bar
   * @param p      player
   */
  public void doBarAction(@NotNull BarAction action, Player p) {
    if(p == null || gameBar == null || !plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BOSSBAR_ENABLED) || !ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
      return;
    }
    if (action == BarAction.ADD) {
      gameBar.addPlayer(p);
    } else if (action == BarAction.REMOVE) {
      gameBar.removePlayer(p);
    }
  }

  public void updateBossBar() {

  }

  public void distributePlots() {
  }

  public void sendBuildLeftTimeMessage() {
    String message = plugin.getChatManager().colorMessage("In-Game.Messages.Time-Left-To-Build").replace("%FORMATTEDTIME%", StringFormatUtils.formatIntoMMSS(getTimer()));
    String subtitle = plugin.getChatManager().colorMessage("In-Game.Messages.Time-Left-Subtitle").replace("%FORMATTEDTIME%", String.valueOf(getTimer()));
    for(Player p : getPlayers()) {
      VersionUtils.sendActionBar(p, message);
      p.sendMessage(plugin.getChatManager().getPrefix() + message);
      VersionUtils.sendSubTitle(p, subtitle, 5, 30, 5);
    }
  }

  public ScoreboardManager getScoreboardManager() {
    return scoreboardManager;
  }

  public boolean isForceStart() {
    return forceStart;
  }

  public void setForceStart(boolean forceStart) {
    this.forceStart = forceStart;
  }

  /**
   * Get arena ID, ID != map name
   * ID is used to get and manage arenas
   *
   * @return arena ID
   */
  @NotNull
  public String getID() {
    return id;
  }

  /**
   * Lobby location of arena
   *
   * @return lobby loc of arena
   */
  @Nullable
  public Location getLobbyLocation() {
    return gameLocations.get(BaseArena.GameLocation.LOBBY);
  }

  public void setLobbyLocation(Location loc) {
    gameLocations.put(BaseArena.GameLocation.LOBBY, loc);
  }

  /**
   * Min players that are required to start arena
   *
   * @return min players size
   */
  public int getMinimumPlayers() {
    return getOption(ArenaOption.MINIMUM_PLAYERS);
  }

  public void setMinimumPlayers(int amount) {
    setOptionValue(ArenaOption.MINIMUM_PLAYERS, amount);
  }

  /**
   * Get map name, map name != ID
   * Map name is used in signs
   *
   * @return map name String
   */
  @NotNull
  public String getMapName() {
    return mapName;
  }

  public void setMapName(String mapname) {
    this.mapName = mapname == null ? "" : mapname;
  }

  public void addPlayer(Player player) {
    players.add(player);
  }

  public void removePlayer(Player player) {
    players.remove(player);
  }

  public void clearPlayers() {
    players.clear();
  }

  public void addSpectator(Player player) {
    spectators.add(player);
  }

  public void removeSpectator(Player player) {
    spectators.remove(player);
  }

  /**
   * Global timer of arena
   *
   * @return timer of arena
   */
  public int getTimer() {
    return getOption(ArenaOption.TIMER);
  }

  public void setTimer(int timer) {
    setOptionValue(ArenaOption.TIMER, timer);
  }

  /**
   * Max players size arena can hold
   *
   * @return max players size
   */
  public int getMaximumPlayers() {
    return getOption(ArenaOption.MAXIMUM_PLAYERS);
  }

  public void setMaximumPlayers(int amount) {
    setOptionValue(ArenaOption.MAXIMUM_PLAYERS, amount);
  }

  /**
   * Arena state of arena
   *
   * @return arena state
   * @see ArenaState
   */
  @NotNull
  public ArenaState getArenaState() {
    return arenaState;
  }

  /**
   * Changes arena state of arena
   * Calls BBGameChangeStateEvent
   *
   * @param arenaState arena state to change
   * @see BBGameChangeStateEvent
   */
  public void setArenaState(@NotNull ArenaState arenaState) {
    if(this.arenaState != null) {
      plugin.getServer().getPluginManager().callEvent(new BBGameChangeStateEvent(arenaState, this, this.arenaState));
    }

    this.arenaState = arenaState;
    plugin.getSignManager().updateSigns();
  }

  /**
   * Get players in game
   *
   * @return List with players
   */
  @NotNull
  public List<Player> getPlayers() {
    return players;
  }

  public List<Player> getSpectators() {
    return spectators;
  }

  public Main getPlugin() {
    return plugin;
  }

  public void teleportAllToEndLocation() {
    if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED) && ConfigUtils.getConfig(plugin, "bungee").getBoolean("End-Location-Hub", true)) {
      players.forEach(plugin.getBungeeManager()::connectToHub);
      spectators.forEach(plugin.getBungeeManager()::connectToHub);
      return;
    }

    Location location = getEndLocation();
    if(location == null) {
      location = getLobbyLocation();
      System.out.print("EndLocation for arena " + getID() + " isn't intialized!");
    }

    if(location != null) {
      for(Player player : players) {
        player.teleport(location);
      }
      for(Player player : spectators) {
        player.teleport(location);
      }
    }
  }

  public void teleportToLobby(Player player) {
    Location location = getLobbyLocation();
    if(location == null) {
      System.out.print("LobbyLocation isn't intialized for arena " + getID());
      return;
    }

    player.teleport(location);
  }

  public void teleportToEndLocation(Player player) {
    if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED) && ConfigUtils.getConfig(plugin, "bungee").getBoolean("End-Location-Hub", true)) {
      plugin.getBungeeManager().connectToHub(player);
      return;
    }

    Location location = getEndLocation();
    if(location == null) {
      location = getLobbyLocation();
      System.out.print("EndLocation for arena " + getID() + " isn't intialized!");
    }

    if(location != null) {
      player.teleport(location);
    }
  }

  public void giveRewards() {
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

  /**
   * End location of arena
   *
   * @return end loc of arena
   */
  @Nullable
  public Location getEndLocation() {
    return gameLocations.get(GameLocation.END);
  }

  public void setEndLocation(Location endLoc) {
    gameLocations.put(GameLocation.END, endLoc);
  }

  protected BossBar getGameBar() {
    return gameBar;
  }

  public int getOption(ArenaOption option) {
    return arenaOptions.get(option);
  }

  public void setOptionValue(ArenaOption option, int value) {
    arenaOptions.put(option, value);
  }

  public void addOptionValue(ArenaOption option, int value) {
    arenaOptions.put(option, arenaOptions.get(option) + value);
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

  public enum GameLocation {
    LOBBY, END
  }

  public enum BarAction {
    ADD, REMOVE
  }

}
