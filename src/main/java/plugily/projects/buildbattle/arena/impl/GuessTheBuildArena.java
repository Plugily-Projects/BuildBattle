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
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion;
import pl.plajerlair.commonsbox.minecraft.compat.VersionUtils;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import pl.plajerlair.commonsbox.minecraft.misc.MiscUtils;
import pl.plajerlair.commonsbox.minecraft.misc.stuff.ComplementAccessor;
import pl.plajerlair.commonsbox.minecraft.serialization.InventorySerializer;
import plugily.projects.buildbattle.ConfigPreferences;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.api.StatsStorage;
import plugily.projects.buildbattle.api.event.game.BBGameEndEvent;
import plugily.projects.buildbattle.api.event.game.BBGameStartEvent;
import plugily.projects.buildbattle.arena.ArenaManager;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.ArenaState;
import plugily.projects.buildbattle.arena.managers.GuessTheBuildScoreboardManager;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.arena.options.ArenaOption;
import plugily.projects.buildbattle.handlers.reward.Reward;
import plugily.projects.buildbattle.menus.options.registry.particles.ParticleRefreshScheduler;
import plugily.projects.buildbattle.menus.themevoter.BBTheme;
import plugily.projects.buildbattle.utils.Debugger;
import plugily.projects.buildbattle.utils.MessageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 */
public class GuessTheBuildArena extends BaseArena {

  private final List<Player> whoGuessed = new ArrayList<>();
  private final GuessTheBuildScoreboardManager scoreboardManager;
  private int round = 1;
  private BBTheme currentTheme;
  private boolean themeSet;
  private boolean nextRoundCooldown = false;
  private Player currentBuilder;
  private Map<Player, Integer> playersPoints = new HashMap<>();

  public GuessTheBuildArena(String id, Main plugin) {
    super(id, plugin);
    scoreboardManager = new GuessTheBuildScoreboardManager(this);
  }

  @Override
  public void run() {
    //idle task
    if(getPlayers().isEmpty() && getArenaState() == ArenaState.WAITING_FOR_PLAYERS) {
      return;
    }
    if(getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.BOSSBAR_ENABLED)) {
      updateBossBar();
    }
    switch(getArenaState()) {
      case WAITING_FOR_PLAYERS:
        if(getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
          getPlugin().getServer().setWhitelist(false);
        }
        getPlotManager().resetPlotsGradually();
        if(getPlayers().size() < getMinimumPlayers()) {
          if(getTimer() <= 0) {
            setTimer(getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.LOBBY, this));
            getPlugin().getChatManager().broadcast(this, getPlugin().getChatManager().colorMessage("In-Game.Messages.Lobby-Messages.Waiting-For-Players").replace("%MINPLAYERS%", Integer.toString(getMinimumPlayers())));
            return;
          }
        } else {
          getPlugin().getChatManager().broadcast(this, getPlugin().getChatManager().colorMessage("In-Game.Messages.Lobby-Messages.Enough-Players-To-Start"));
          setArenaState(ArenaState.STARTING);
          Bukkit.getPluginManager().callEvent(new BBGameStartEvent(this));
          setTimer(getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.LOBBY, this));
        }
        setTimer(getTimer() - 1);
        break;
      case STARTING:
        for(Player player : getPlayers()) {
          float exp = (float) (getTimer() / (double) getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.LOBBY, this));
          player.setExp((exp > 1f || exp < 0f) ? 1f : exp);
          player.setLevel(getTimer());
        }
        if(getPlayers().size() < getMinimumPlayers()) {
          getPlugin().getChatManager().broadcast(this, getPlugin().getChatManager().colorMessage("In-Game.Messages.Lobby-Messages.Waiting-For-Players").replace("%MINPLAYERS%", Integer.toString(getMinimumPlayers())));
          setArenaState(ArenaState.WAITING_FOR_PLAYERS);
          Bukkit.getPluginManager().callEvent(new BBGameStartEvent(this));
          setTimer(getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.LOBBY, this));
          for(Player player : getPlayers()) {
            player.setExp(1);
            player.setLevel(0);
          }
          break;
        }
        if(getTimer() == 0) {
          particleRefreshSched = new ParticleRefreshScheduler(getPlugin());
          if(!getPlotManager().isPlotsCleared()) {
            getPlotManager().resetQueuedPlots();
          }
          setArenaState(ArenaState.IN_GAME);
          for(Player player : getPlayers()) {
            playersPoints.put(player, 0);
          }
          distributePlots();
          getPlotManager().teleportToPlots();
          setTimer(getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.DELAYED_TASK, this));
          for(Player player : getPlayers()) {
            player.getInventory().clear();
            player.setAllowFlight(true);
            player.setFlying(true);
            player.getInventory().setItem(8, getPlugin().getOptionsRegistry().getMenuItem());
            //to prevent Multiverse changing gamemode bug
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> player.setGameMode(GameMode.SPECTATOR), 20);
          }
          Plot plot = getPlotManager().getPlot(getPlayers().get(round - 1));
          org.bukkit.Location plotLoc = plot == null ? null : plot.getTeleportLocation();
          if(plotLoc != null) {
            for(Player p : getPlayers()) {
              p.teleport(plotLoc);
              getPlugin().getRewardsHandler().performReward(p, Reward.RewardType.START_GAME, -1);
            }
          }
          nextRoundCooldown = true;
          Bukkit.getScheduler().runTaskLater(getPlugin(), () -> nextRoundCooldown = false, 20 * getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.DELAYED_TASK, this));
          if (plot != null) {
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> plot.getOwners().get(0).setGameMode(GameMode.CREATIVE), 20);
          }
          break;
        }
        setTimer(getTimer() - 1);
        break;
      case IN_GAME:
        if(getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
          getPlugin().getServer().setWhitelist(getMaximumPlayers() <= getPlayers().size());
        }
        if(currentBuilder == null && !nextRoundCooldown) {
          currentBuilder = getPlayers().get(round - 1);
          openThemeSelectionInventoryToCurrentBuilder();
          setTimer(getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.THEME_SELECTION, this));
          break;
        }
        if(!themeSet && getTimer() <= 0 && currentBuilder != null) {
          Random r = new Random();
          String type = "EASY";
          switch(r.nextInt(2 + 1)) {
            case 1:
              type = "MEDIUM";
              break;
            case 2:
              type = "HARD";
              break;
            default:
              break;
          }
          List<String> themes = getPlugin().getConfigPreferences().getThemes(BaseArena.ArenaType.GUESS_THE_BUILD.getPrefix() + "_" + type);
          if (!themes.isEmpty()) {
            BBTheme theme = new BBTheme(themes.get(r.nextInt(themes.size())), BBTheme.Difficulty.valueOf(type));
            setCurrentTheme(theme);
            setThemeSet(true);
            VersionUtils.sendActionBar(currentBuilder, getPlugin().getChatManager().colorMessage("In-Game.Guess-The-Build.Theme-Is-Name")
                .replace("%THEME%", theme.getTheme()));
          }
          currentBuilder.closeInventory();

          String roundMessage = getPlugin().getChatManager().colorMessage("In-Game.Guess-The-Build.Current-Round")
              .replace("%ROUND%", Integer.toString(round))
              .replace("%MAXPLAYERS%", Integer.toString(getPlayers().size()));
          for(Player p : getPlayers()) {
            VersionUtils.sendTitle(p, getPlugin().getChatManager().colorMessage("In-Game.Guess-The-Build.Start-Guessing-Title"), 5, 25, 5);
            p.sendMessage(roundMessage);
          }
          setTimer(getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.BUILD, this));
          break;
        }
        if(getTimer() <= 90 && currentTheme != null) {
          if(getTimer() == 90) {
            getPlugin().getChatManager().broadcast(this, getPlugin().getChatManager().colorMessage("In-Game.Guess-The-Build.Theme-Is-Long")
                .replace("%NUM%", Integer.toString(currentTheme.getTheme().length())));
          }
          for(Player player : getPlayers()) {
            if(currentBuilder == player) {
              continue;
            }
            if(whoGuessed.contains(player)) {
              VersionUtils.sendActionBar(player, currentTheme.getTheme());
            }
            StringBuilder actionbar = new StringBuilder();
            for(int i = 0; i < currentTheme.getTheme().length(); i++) {
              if(Character.isWhitespace(currentTheme.getTheme().charAt(i))) {
                actionbar.append("  ");
                continue;
              }
              if((getTimer() <= 75 && i == 0) || (getTimer() <= 40 && i == currentTheme.getTheme().length() - 1) || (getTimer() <= 20 && i == 2) || (getTimer() <= 10 && i == 5)) {
                actionbar.append(currentTheme.getTheme().charAt(i)).append(' ');
                continue;
              }
              actionbar.append("_ ");
            }
            VersionUtils.sendActionBar(player, actionbar.toString());
          }
        }
        if(getTimer() <= 0 && themeSet) {
          getPlugin().getChatManager().broadcast(this, getPlugin().getChatManager().colorMessage("In-Game.Guess-The-Build.Theme-Was-Name").replace("%THEME%", currentTheme.getTheme()));
          for(Player p : getPlayers()) {
            VersionUtils.sendTitles(p, getPlugin().getChatManager().colorMessage("In-Game.Guess-The-Build.Theme-Was-Title"), getPlugin().getChatManager().colorMessage("In-Game.Guess-The-Build.Theme-Was-Subtitle")
                .replace("%THEME%", currentTheme.getTheme()), 5, 25, 5);
          }
          currentBuilder = null;
          setThemeSet(false);
          setCurrentTheme(null);
          whoGuessed.clear();
          round++;
          if(round > getPlayers().size()) {
            setTimer(15);
            setArenaState(ArenaState.ENDING);
            Bukkit.getPluginManager().callEvent(new BBGameEndEvent(this));
            break;
          }

          setTimer(getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.DELAYED_TASK, this));
          nextRoundCooldown = true;
          Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            nextRoundCooldown = false;
            currentBuilder = getPlayers().get(round - 1);
            openThemeSelectionInventoryToCurrentBuilder();
            Plot plot = getPlotManager().getPlot(getPlayers().get(round - 1));
            for(Player p : getPlayers()) {
              if (plot != null) {
                org.bukkit.Location plotLoc = plot.getTeleportLocation();
                if(plotLoc != null) {
                  p.teleport(plotLoc);
                }
                p.setPlayerWeather(plot.getWeatherType());
                p.setPlayerTime(Plot.Time.format(plot.getTime(), p.getWorld().getTime()), false);
              }

              p.setGameMode(GameMode.SPECTATOR);
            }
            if (plot != null) {
              plot.getOwners().get(0).setGameMode(GameMode.CREATIVE);
            }
            setTimer(getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.THEME_SELECTION, this));
            if(getArenaState() != ArenaState.IN_GAME || themeSet) {
              return;
            }
            for(Player player : getPlayers()) {
              if(currentBuilder != player) {
                VersionUtils.sendSubTitle(player, getPlugin().getChatManager().colorMessage("In-Game.Guess-The-Build.Theme-Being-Selected"), 5, 25, 5);
              }
            }
          }, 20 * getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.DELAYED_TASK, this));
          //todo next round info and game state?
          break;
        }
        if(getPlayers().size() < 2) {
          getPlugin().getChatManager().broadcast(this, getPlugin().getChatManager().colorMessage("In-Game.Messages.Game-End-Messages.Only-You-Playing"));
          setArenaState(ArenaState.ENDING);
          Bukkit.getPluginManager().callEvent(new BBGameEndEvent(this));
          setTimer(15);
        }
        if(themeSet && (getTimer() == (4 * 60) || getTimer() == (3 * 60) || getTimer() == 5 * 60 || getTimer() == 30 || getTimer() == 2 * 60 || getTimer() == 60 || getTimer() == 15)) {
          sendBuildLeftTimeMessage();
        }
        //if player leaves during round force next round
        if(currentBuilder != null && !currentBuilder.isOnline()) {
          setTimer(1);
        }
        if(getTimer() != 0 && currentBuilder != null) {
          if(getOption(ArenaOption.IN_PLOT_CHECKER) == 1) {
            setOptionValue(ArenaOption.IN_PLOT_CHECKER, 0);
            for(Player player : getPlayers()) {
              Plot buildPlot = getPlugin().getUserManager().getUser(currentBuilder).getCurrentPlot();
              org.bukkit.Location plotLoc = buildPlot == null ? null : buildPlot.getTeleportLocation();
              if(plotLoc != null && !buildPlot.getCuboid().isInWithMarge(player.getLocation(), 5)) {
                player.teleport(plotLoc);
                player.sendMessage(getPlugin().getChatManager().getPrefix() + getPlugin().getChatManager().colorMessage("In-Game.Messages.Cant-Fly-Outside-Plot"));
              }
            }
          }
          addOptionValue(ArenaOption.IN_PLOT_CHECKER, 1);
        }
        setTimer(getTimer() - 1);
        break;
      case ENDING:
        if(getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
          getPlugin().getServer().setWhitelist(false);
        }
        if(getPlugin().getConfig().getBoolean("Firework-When-Game-Ends", true)) {
          for(Player player : getPlayers()) {
            MiscUtils.spawnRandomFirework(player.getLocation());
          }
        }
        if(getTimer() <= 0) {
          scoreboardManager.stopAllScoreboards();
          teleportAllToEndLocation();
          for(Player player : getPlayers()) {
            if(getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.BOSSBAR_ENABLED) && ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
              getGameBar().removePlayer(player);
            }
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
            player.setFlying(false);
            player.setAllowFlight(false);
            player.getInventory().setArmorContents(null);
            player.sendMessage(getPlugin().getChatManager().getPrefix() + getPlugin().getChatManager().colorMessage("Commands.Teleported-To-The-Lobby"));
            getPlugin().getUserManager().getUser(player).addStat(StatsStorage.StatisticType.GAMES_PLAYED, 1);
            if(getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
              InventorySerializer.loadInventory(getPlugin(), player);
            }
            //fast solution
            if(getPlotManager().getPlot(player) != null)
              getPlotManager().getPlot(player).fullyResetPlot();
          }

          // do it in the main thread to prevent async catch from bukkit
          Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), this::giveRewards);

          clearPlayers();
          if(particleRefreshSched != null) {
            particleRefreshSched.task.cancel();
          }
          setArenaState(ArenaState.RESTARTING);
          if(getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
            getPlugin().getServer().getOnlinePlayers().forEach(this::addPlayer);
          }
        }
        setTimer(getTimer() - 1);
        break;
      case RESTARTING:
        setOptionValue(ArenaOption.IN_PLOT_CHECKER, 0);
        whoGuessed.clear();
        playersPoints.clear();
        round = 1;
        clearPlayers();
        nextRoundCooldown = false;
        setTimer(14);
        setArenaState(ArenaState.WAITING_FOR_PLAYERS);
        currentBuilder = null;
        setThemeSet(false);
        setCurrentTheme(null);
        if(getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
          if(ConfigUtils.getConfig(getPlugin(), "bungee").getBoolean("Shutdown-When-Game-Ends")) {
            getPlugin().getServer().shutdown();
          }
          ArenaRegistry.shuffleBungeeArena();
          for(Player player : Bukkit.getOnlinePlayers()) {
            ArenaManager.joinAttempt(player, ArenaRegistry.getArenas().get(ArenaRegistry.getBungeeArena()));
          }
        }
        break;
    }
  }

  private void openThemeSelectionInventoryToCurrentBuilder() {
    if(currentBuilder == null)
      return;

    Random r = new Random();

    List<String> themes = getPlugin().getConfigPreferences().getThemes(BaseArena.ArenaType.GUESS_THE_BUILD.getPrefix() + "_EASY");
    String itemLore = getPlugin().getChatManager().colorMessage("Menus.Guess-The-Build-Theme-Selector.Theme-Item-Lore");
    String themeItemName = getPlugin().getChatManager().colorMessage("Menus.Guess-The-Build-Theme-Selector.Theme-Item-Name");

    Inventory inv = ComplementAccessor.getComplement().createInventory(null, 27, getPlugin().getChatManager().colorMessage("Menus.Guess-The-Build-Theme-Selector.Inventory-Name"));
    inv.setItem(11, new ItemBuilder(Material.PAPER).name(themeItemName
        .replace("%theme%", !themes.isEmpty() ? themes.get(r.nextInt(themes.size())) : ""))
        .lore(itemLore
            .replace("%difficulty%", getPlugin().getChatManager().colorMessage("Menus.Guess-The-Build-Theme-Selector.Difficulties.Easy"))
            .replace("%points%", Integer.toString(1)).split(";")).build());

    themes = getPlugin().getConfigPreferences().getThemes(BaseArena.ArenaType.GUESS_THE_BUILD.getPrefix() + "_MEDIUM");

    inv.setItem(13, new ItemBuilder(Material.PAPER).name(themeItemName
        .replace("%theme%", !themes.isEmpty() ? themes.get(r.nextInt(themes.size())) : ""))
        .lore(itemLore
            .replace("%difficulty%", getPlugin().getChatManager().colorMessage("Menus.Guess-The-Build-Theme-Selector.Difficulties.Medium"))
            .replace("%points%", Integer.toString(2)).split(";")).build());

    themes = getPlugin().getConfigPreferences().getThemes(BaseArena.ArenaType.GUESS_THE_BUILD.getPrefix() + "_HARD");

    inv.setItem(15, new ItemBuilder(Material.PAPER).name(themeItemName
        .replace("%theme%", !themes.isEmpty() ? themes.get(r.nextInt(themes.size())) : ""))
        .lore(itemLore
            .replace("%difficulty%", getPlugin().getChatManager().colorMessage("Menus.Guess-The-Build-Theme-Selector.Difficulties.Hard"))
            .replace("%points%", Integer.toString(3)).split(";")).build());
    currentBuilder.openInventory(inv);
  }

  public void recalculateLeaderboard() {
    playersPoints = playersPoints.entrySet()
        .stream()
        .sorted((Map.Entry.<Player, Integer>comparingByValue().reversed()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }

  @Override
  public void giveRewards() {
    List<Player> list = new ArrayList<>(playersPoints.keySet());
    for(int i = 0; i <= list.size(); i++) {
      if(list.size() - 1 < i) {
        continue;
      }
      getPlugin().getRewardsHandler().performReward(list.get(i), Reward.RewardType.PLACE, i + 1);
    }
    getPlugin().getRewardsHandler().performReward(this, Reward.RewardType.END_GAME);
  }

  @Override
  public void updateBossBar() {
    if(getGameBar() == null) {
      return;
    }
    switch(getArenaState()) {
      case WAITING_FOR_PLAYERS:
        getGameBar().setTitle(getPlugin().getChatManager().colorMessage("Bossbar.Waiting-For-Players"));
        break;
      case STARTING:
        getGameBar().setTitle(getPlugin().getChatManager().colorMessage("Bossbar.Starting-In").replace("%time%", Integer.toString(getTimer())));
        break;
      case IN_GAME:
        getGameBar().setTitle(getPlugin().getChatManager().colorMessage("Bossbar.Time-Left").replace("%time%", Integer.toString(getTimer())));
        break;
      default:
        break;
    }
  }

  @Override
  public void distributePlots() {
    //clear plots before distribution to avoid problems
    for(Plot plot : getPlotManager().getPlots()) {
      plot.getOwners().clear();
    }
    List<Player> players = new ArrayList<>(getPlayers());
    for(Plot plot : getPlotManager().getPlots()) {
      if(players.isEmpty()) {
        break;
      }
      Player first = players.get(0);
      plot.addOwner(first);
      getPlugin().getUserManager().getUser(first).setCurrentPlot(plot);
      players.remove(0);
    }
    if(!players.isEmpty()) {
      MessageUtils.errorOccurred();
      Debugger.sendConsoleMsg("&c[BuildBattle] [PLOT WARNING] Not enough plots in arena " + getID() + "!");
      Debugger.sendConsoleMsg("&c[PLOT WARNING] Required " + getPlayers().size() + " but have " + getPlotManager().getPlots().size());
      Debugger.sendConsoleMsg("&c[PLOT WARNING] Instance was stopped!");
      ArenaManager.stopGame(false, this);
    }
  }

  public int getRound() {
    return round;
  }

  @Nullable
  public Player getCurrentBuilder() {
    return currentBuilder;
  }

  public BBTheme getCurrentTheme() {
    return currentTheme;
  }

  public void setCurrentTheme(BBTheme currentTheme) {
    this.currentTheme = currentTheme;
  }

  @NotNull
  public List<Player> getWhoGuessed() {
    return whoGuessed;
  }

  public void addWhoGuessed(Player player) {
    whoGuessed.add(player);
    getPlugin().getRewardsHandler().performReward(player, Reward.RewardType.GTB_GUESS, -1);
    //decrease game time by guessed theme
    if(getTimer() >= 15) {
      setTimer(getTimer() - getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.TIME_SHORTENER, this));
    }
    //-1 because builder can´t guess
    if(whoGuessed.size() >= getPlayers().size() - 1) {
      setTimer(getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.ALL_GUESSED, this));
      for(Player players : getPlayers()) {
        players.sendMessage(getPlugin().getChatManager().getPrefix() + getPlugin().getChatManager().colorMessage("In-Game.Guess-The-Build.Theme-Guessed"));
        getPlugin().getRewardsHandler().performReward(players, Reward.RewardType.GTB_ALL_GUESSED, -1);
      }
    }
  }

  @Override
  public GuessTheBuildScoreboardManager getScoreboardManager() {
    return scoreboardManager;
  }

  public boolean isThemeSet() {
    return themeSet;
  }

  public void setThemeSet(boolean themeSet) {
    this.themeSet = themeSet;
  }

  public Map<Player, Integer> getPlayersPoints() {
    return playersPoints;
  }
}
