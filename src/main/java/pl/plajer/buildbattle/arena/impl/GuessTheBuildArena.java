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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import pl.plajer.buildbattle.ConfigPreferences;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.api.event.game.BBGameEndEvent;
import pl.plajer.buildbattle.api.event.game.BBGameStartEvent;
import pl.plajer.buildbattle.arena.ArenaState;
import pl.plajer.buildbattle.arena.ArenaUtils;
import pl.plajer.buildbattle.arena.managers.GuessTheBuildScoreboardManager;
import pl.plajer.buildbattle.arena.managers.plots.Plot;
import pl.plajer.buildbattle.arena.options.ArenaOption;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.menus.themevoter.GTBTheme;
import pl.plajer.buildbattle.user.User;
import pl.plajer.buildbattle.utils.Utils;
import pl.plajerlair.core.utils.ItemBuilder;
import pl.plajerlair.core.utils.MinigameUtils;

/**
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 */
public class GuessTheBuildArena extends BaseArena {

  private int round = 0;
  private GTBTheme currentTheme;
  private boolean themeSet;
  private Player currentBuilder;
  private Map<Player, Integer> playersPoints = new HashMap<>();
  private GuessTheBuildScoreboardManager scoreboardManager;

  public GuessTheBuildArena(String id, Main plugin) {
    super(id, plugin);
    scoreboardManager = new GuessTheBuildScoreboardManager(this);
  }

  @Override
  public void run() {
    switch (getArenaState()) {
      case WAITING_FOR_PLAYERS:
        if (getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
          getPlugin().getServer().setWhitelist(false);
        }
        getPlotManager().resetPlotsGradually();
        if (getPlayers().size() < getMinimumPlayers()) {
          if (getTimer() <= 0) {
            setTimer(getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.LOBBY, this));
            ChatManager.broadcast(this, ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Waiting-For-Players").replace("%MINPLAYERS%", String.valueOf(getMinimumPlayers())));
            return;
          }
        } else {
          ChatManager.broadcast(this, ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Enough-Players-To-Start"));
          setArenaState(ArenaState.STARTING);
          Bukkit.getPluginManager().callEvent(new BBGameStartEvent(this));
          setTimer(getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.LOBBY, this));
          ArenaUtils.showPlayers(this);
        }
        setTimer(getTimer() - 1);
        break;
      case STARTING:
        for (Player player : getPlayers()) {
          player.setExp((float) (getTimer() / getPlugin().getConfig().getDouble("Lobby-Starting-Time", 60)));
          player.setLevel(getTimer());
        }
        if (getPlayers().size() < getMinimumPlayers()) {
          ChatManager.broadcast(this, ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Waiting-For-Players").replace("%MINPLAYERS%", String.valueOf(getMinimumPlayers())));
          setArenaState(ArenaState.WAITING_FOR_PLAYERS);
          Bukkit.getPluginManager().callEvent(new BBGameStartEvent(this));
          setTimer(getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.LOBBY, this));
          for (Player player : getPlayers()) {
            player.setExp(1);
            player.setLevel(0);
          }
          break;
        }
        if (getTimer() == 0) {
          if (!getPlotManager().isPlotsCleared()) {
            getPlotManager().resetQueuedPlots();
          }
          setArenaState(ArenaState.IN_GAME);
          getPlotManager().distributePlots();
          getPlotManager().teleportToPlots();
          setTimer(getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.THEME_VOTE, this));
          for (Player player : getPlayers()) {
            player.getInventory().clear();
            player.setGameMode(GameMode.CREATIVE);
            player.setAllowFlight(true);
            player.setFlying(true);
            ArenaUtils.hidePlayersOutsideTheGame(this, player);
            player.getInventory().setItem(8, getPlugin().getOptionsRegistry().getMenuItem());
            //to prevent Multiverse chaning gamemode bug
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> player.setGameMode(GameMode.CREATIVE), 20);
          }
          break;
        }
        setTimer(getTimer() - 1);
        break;
      case IN_GAME:
        for (Player p : getPlayers()) {
          if (!playersPoints.containsKey(p)) {
            playersPoints.put(p, 0);
          }
          //todo ineffective?
          playersPoints = Utils.sortByValue(playersPoints);
        }
        if (getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
          if (getMaximumPlayers() <= getPlayers().size()) {
            getPlugin().getServer().setWhitelist(true);
          } else {
            getPlugin().getServer().setWhitelist(false);
          }
        }
        //todo deprecate the themes selector
        if (currentBuilder == null) {
          currentBuilder = getPlayers().get(0);
          Random r = new Random();

          Inventory inv = Bukkit.createInventory(null, 27, ChatManager.colorMessage("Menus.Guess-The-Build-Theme-Selector.Inventory-Name"));
          inv.setItem(11, new ItemBuilder(new ItemStack(Material.PAPER)).name(ChatManager.colorMessage("Menus.Guess-The-Build-Theme-Selector.Theme-Item-Name")
              .replace("%theme%", getPlugin().getConfigPreferences().getThemes("Guess-The-Build_EASY")
                  .get(r.nextInt(getPlugin().getConfigPreferences().getThemes("Guess-The-Build_EASY").size()))))
              .lore(ChatManager.colorMessage("Menus.Guess-The-Build-Theme-Selector.Theme-Item-Lore")
                  .replace("%difficulty%", ChatManager.colorMessage("Menus.Guess-The-Build-Theme-Selector.Difficulties.Easy"))
                  .replace("%points%", String.valueOf(1)).split(";")).build());
          inv.setItem(13, new ItemBuilder(new ItemStack(Material.PAPER)).name(ChatManager.colorMessage("Menus.Guess-The-Build-Theme-Selector.Theme-Item-Name")
              .replace("%theme%", getPlugin().getConfigPreferences().getThemes("Guess-The-Build_MEDIUM")
                  .get(r.nextInt(getPlugin().getConfigPreferences().getThemes("Guess-The-Build_MEDIUM").size()))))
              .lore(ChatManager.colorMessage("Menus.Guess-The-Build-Theme-Selector.Theme-Item-Lore")
                  .replace("%difficulty%", ChatManager.colorMessage("Menus.Guess-The-Build-Theme-Selector.Difficulties.Medium"))
                  .replace("%points%", String.valueOf(2)).split(";")).build());
          inv.setItem(15, new ItemBuilder(new ItemStack(Material.PAPER)).name(ChatManager.colorMessage("Menus.Guess-The-Build-Theme-Selector.Theme-Item-Name")
              .replace("%theme%", getPlugin().getConfigPreferences().getThemes("Guess-The-Build_HARD")
                  .get(r.nextInt(getPlugin().getConfigPreferences().getThemes("Guess-The-Build_HARD").size()))))
              .lore(ChatManager.colorMessage("Menus.Guess-The-Build-Theme-Selector.Theme-Item-Lore")
                  .replace("%difficulty%", ChatManager.colorMessage("Menus.Guess-The-Build-Theme-Selector.Difficulties.Hard"))
                  .replace("%points%", String.valueOf(3)).split(";")).build());
          currentBuilder.openInventory(inv);
          break;
        } else {
          if (!isThemeSet()) {
            if (getTimer() <= 0) {
              Random r = new Random();
              String type = "EASY";
              switch (r.nextInt(2)) {
                case 0:
                  break;
                case 1:
                  type = "MEDIUM";
                  break;
                case 2:
                  type = "HARD";
                  break;
              }
              GTBTheme theme = new GTBTheme(getPlugin().getConfigPreferences().getThemes("Guess-The-Build_" + type)
                  .get(r.nextInt(getPlugin().getConfigPreferences().getThemes("Guess-The-Build_" + type).size())), GTBTheme.Difficulty.valueOf(type));
              setCurrentTheme(theme);
              setThemeSet(true);
              currentBuilder.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatManager.colorMessage("In-Game.Guess-The-Build.Theme-Is-Name")
                  .replace("%THEME%", theme.getTheme())));
            }
            setTimer(getTimer() - 1);
            break;
          }
        }
        if (isThemeVoteTime()) {
          //todo should be removed? -->
          if (!isThemeTimerSet()) {
            setTimer(getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.THEME_VOTE, this));
            setThemeTimerSet(true);
          }
          for (Player p : getPlayers()) {
            voteMenu.updateInventory(p);
          }
          // <--
          if (getTimer() == 0) {
            setThemeVoteTime(false);
            String votedTheme = voteMenu.getVotePoll().getVotedTheme();
            setTheme(votedTheme);
            setTimer(getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.BUILD, this));
            String message = ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Game-Started");
            for (Player p : getPlayers()) {
              p.closeInventory();
              p.teleport(getPlotManager().getPlot(p).getTeleportLocation());
              p.sendMessage(ChatManager.getPrefix() + message);
            }
            break;
          } else {
            setTimer(getTimer() - 1);
            break;
          }
        }
        if (getPlayers().size() < 2) {
          ChatManager.broadcast(this, ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Only-You-Playing"));
          setArenaState(ArenaState.ENDING);
          Bukkit.getPluginManager().callEvent(new BBGameEndEvent(this));
          setTimer(10);
        }
        if ((getTimer() == (4 * 60) || getTimer() == (3 * 60) || getTimer() == 5 * 60 || getTimer() == 30 || getTimer() == 2 * 60 || getTimer() == 60 || getTimer() == 15)) {
          String message = ChatManager.colorMessage("In-Game.Messages.Time-Left-To-Build").replace("%FORMATTEDTIME%", MinigameUtils.formatIntoMMSS(getTimer()));
          String subtitle = ChatManager.colorMessage("In-Game.Messages.Time-Left-Subtitle").replace("%FORMATTEDTIME%", String.valueOf(getTimer()));
          for (Player p : getPlayers()) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
            p.sendMessage(ChatManager.getPrefix() + message);
            p.sendTitle(null, subtitle, 5, 30, 5);
          }
        }
        if (getTimer() != 0) {
          if (getOption(ArenaOption.IN_PLOT_CHECKER) == 1) {
            setOptionValue(ArenaOption.IN_PLOT_CHECKER, 0);
            for (Player player : getPlayers()) {
              User user = getPlugin().getUserManager().getUser(player.getUniqueId());
              Plot buildPlot = user.getCurrentPlot();
              if (buildPlot != null && !buildPlot.getCuboid().isInWithMarge(player.getLocation(), 5)) {
                player.teleport(buildPlot.getTeleportLocation());
                player.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("In-Game.Messages.Cant-Fly-Outside-Plot"));
              }
            }
          }
          addOptionValue(ArenaOption.IN_PLOT_CHECKER, 1);
        }
        if (getTimer() == 0) {
        }
        if (getTimer() == 0 && receivedVoteItems) {
          setVoting(true);
          if (!queue.isEmpty()) {
            if (getVotingPlot() != null) {
              for (Player player : getPlayers()) {
                getVotingPlot().setPoints(getVotingPlot().getPoints() + getPlugin().getUserManager().getUser(player.getUniqueId()).getStat(StatsStorage.StatisticType.LOCAL_POINTS));
                getPlugin().getUserManager().getUser(player.getUniqueId()).setStat(StatsStorage.StatisticType.LOCAL_POINTS, 0);
              }
            }
            voteRoutine();
          } else {
            if (getVotingPlot() != null) {
              for (Player player : getPlayers()) {
                getVotingPlot().setPoints(getVotingPlot().getPoints() + getPlugin().getUserManager().getUser(player.getUniqueId()).getStat(StatsStorage.StatisticType.LOCAL_POINTS));
                getPlugin().getUserManager().getUser(player.getUniqueId()).setStat(StatsStorage.StatisticType.LOCAL_POINTS, 0);
              }
            }
            calculateResults();
            Plot winnerPlot = getPlotManager().getPlot(topList.get(1).get(0));
            announceResults();
            for (Player player : getPlayers()) {
              player.teleport(winnerPlot.getTeleportLocation());
              String winner = ChatManager.colorMessage("In-Game.Messages.Voting-Messages.Winner-Title");
              if (getArenaType() == ArenaType.TEAM) {
                if (winnerPlot.getOwners().size() == 1) {
                  winner = winner.replace("%player%", Bukkit.getOfflinePlayer(topList.get(1).get(0)).getName());
                } else {
                  winner = winner.replace("%player%", Bukkit.getOfflinePlayer(topList.get(1).get(0)).getName() + " & " + Bukkit.getOfflinePlayer(topList.get(1).get(1)).getName());
                }
              } else {
                winner = winner.replace("%player%", Bukkit.getOfflinePlayer(topList.get(1).get(0)).getName());
              }
              player.sendTitle(winner, null, 5, 35, 5);
            }
            setArenaState(ArenaState.ENDING);
            Bukkit.getPluginManager().callEvent(new BBGameEndEvent(this));
            setTimer(10);
          }
        }
        setTimer(getTimer() - 1);
        break;
      case ENDING:
        break;
      case RESTARTING:
        setOptionValue(ArenaOption.IN_PLOT_CHECKER, 0);
        break;
    }
  }

  @Override
  public void updateBossBar() {

  }

  public Player getCurrentBuilder() {
    return currentBuilder;
  }

  public GTBTheme getCurrentGTBTheme() {
    return currentTheme;
  }

  public void setCurrentTheme(GTBTheme currentTheme) {
    this.currentTheme = currentTheme;
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
