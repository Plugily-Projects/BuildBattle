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
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import pl.plajer.buildbattle.ConfigPreferences;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.event.game.BBGameChangeStateEvent;
import pl.plajer.buildbattle.arena.ArenaState;
import pl.plajer.buildbattle.arena.managers.ScoreboardManager;
import pl.plajer.buildbattle.arena.managers.plots.PlotManager;
import pl.plajer.buildbattle.arena.options.ArenaOption;

/**
 * @author Plajer
 * <p>
 * Created at 18.01.2019
 */
public class BaseArena extends BukkitRunnable {

  private Main plugin;
  private String id;
  private String mapName = "";
  //todo move?
  private String theme = "Theme";
  private PlotManager plotManager;
  private ScoreboardManager scoreboardManager;
  private ArenaState arenaState;
  private BossBar gameBar;
  private ArenaType arenaType;
  private boolean forceStart = false;
  private List<Player> players = new ArrayList<>();
  //instead of 2 (lobby, end) location fields we use map with GameLocation enum
  private Map<GameLocation, Location> gameLocations = new HashMap<>();
  //all arena values that are integers, contains constant and floating values
  private Map<ArenaOption, Integer> arenaOptions = new HashMap<>();
  private boolean ready = true;

  public BaseArena(String id, Main plugin) {
    arenaState = ArenaState.WAITING_FOR_PLAYERS;
    this.plugin = plugin;
    this.id = id;
    if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BOSSBAR_ENABLED)) {
      gameBar = Bukkit.createBossBar(plugin.getChatManager().colorMessage("Bossbar.Waiting-For-Players"), BarColor.BLUE, BarStyle.SOLID);
    }
    plotManager = new PlotManager(this);
    scoreboardManager = new ScoreboardManager(this);
    for (ArenaOption option : ArenaOption.values()) {
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

  public BossBar getGameBar() {
    return gameBar;
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
    this.runTaskTimer(plugin, 20L, 20L);
  }

  public void updateBossBar() {
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
  public String getID() {
    return id;
  }

  /**
   * Lobby location of arena
   *
   * @return lobby loc of arena
   */
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
  public String getMapName() {
    return mapName;
  }

  public void setMapName(String mapname) {
    this.mapName = mapname;
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
  public void setArenaState(ArenaState arenaState) {
    if (getArenaState() != null) {
      BBGameChangeStateEvent gameChangeStateEvent = new BBGameChangeStateEvent(arenaState, this, getArenaState());
      plugin.getServer().getPluginManager().callEvent(gameChangeStateEvent);
    }
    this.arenaState = arenaState;
  }

  /**
   * Get players in game
   *
   * @return List with players
   */
  public List<Player> getPlayers() {
    return players;
  }

  public Main getPlugin() {
    return plugin;
  }

  public void teleportAllToEndLocation() {
      if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
        for (Player player : getPlayers()) {
          plugin.getBungeeManager().connectToHub(player);
        }
        return;
      }
      Location location = getEndLocation();

      if (location == null) {
        location = getLobbyLocation();
        System.out.print("EndLocation for arena " + getID() + " isn't intialized!");
      }
      for (Player player : getPlayers()) {
        player.teleport(location);
      }
  }

  public void teleportToLobby(Player player) {
    Location location = getLobbyLocation();
    if (location == null) {
      System.out.print("LobbyLocation isn't intialized for arena " + getID());
    }
    player.teleport(location);
  }

  public void teleportToEndLocation(Player player) {
      if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
        plugin.getBungeeManager().connectToHub(player);
        return;
      }
      Location location = getEndLocation();
      if (location == null) {
        location = getLobbyLocation();
        System.out.print("EndLocation for arena " + getID() + " isn't intialized!");
      }

      player.teleport(location);
  }

  public void giveRewards() {
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

  /**
   * End location of arena
   *
   * @return end loc of arena
   */
  public Location getEndLocation() {
    return gameLocations.get(GameLocation.END);
  }

  public void setEndLocation(Location endLoc) {
    gameLocations.put(GameLocation.END, endLoc);
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

    private String prefix;

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

}
