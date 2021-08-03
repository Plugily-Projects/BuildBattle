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

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import plugily.projects.buildbattle.ConfigPreferences;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.api.StatsStorage;
import plugily.projects.buildbattle.api.event.game.BBGameEndEvent;
import plugily.projects.buildbattle.api.event.game.BBGameStartEvent;
import plugily.projects.buildbattle.arena.ArenaManager;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.ArenaState;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.arena.options.ArenaOption;
import plugily.projects.buildbattle.handlers.HolidayManager;
import plugily.projects.buildbattle.handlers.language.LanguageManager;
import plugily.projects.buildbattle.handlers.reward.Reward;
import plugily.projects.buildbattle.menus.options.registry.particles.ParticleRefreshScheduler;
import plugily.projects.buildbattle.menus.themevoter.VoteMenu;
import plugily.projects.buildbattle.menus.themevoter.VotePoll;
import plugily.projects.buildbattle.user.User;
import plugily.projects.buildbattle.utils.Debugger;
import plugily.projects.buildbattle.utils.MessageUtils;
import plugily.projects.commonsbox.minecraft.compat.VersionUtils;
import plugily.projects.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.commonsbox.minecraft.misc.MiscUtils;
import plugily.projects.commonsbox.minecraft.serialization.InventorySerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;

/**
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 */
public class SoloArena extends BaseArena {

  private final Main plugin;

  private final Map<Integer, List<Player>> topList = new HashMap<>();
  private final Queue<Plot> queue = new LinkedList<>();
  private boolean receivedVoteItems;
  private Plot votingPlot;
  private boolean voteTime;
  private boolean themeVoteTime = true;
  private boolean themeTimerSet = false;
  private VoteMenu voteMenu;

  public SoloArena(String id, Main plugin) {
    super(id, plugin);
    this.plugin = plugin;
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

  @NotNull
  public Queue<Plot> getQueue() {
    return queue;
  }

  @Override
  public void run() {
    //idle task
    if(getArenaState() == ArenaState.WAITING_FOR_PLAYERS && getPlayers().isEmpty()) {
      return;
    }

    updateBossBar();

    switch(getArenaState()) {
      case WAITING_FOR_PLAYERS:
        if(getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
          getPlugin().getServer().setWhitelist(false);
        }
        getPlotManager().resetPlotsGradually();

        int minmPlayers = getMinimumPlayers();

        if(getPlayers().size() < minmPlayers) {
          if(getTimer() <= 0) {
            setTimer(getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.LOBBY, this));
            getPlugin().getChatManager().broadcast(this, getPlugin().getChatManager().colorMessage("In-Game.Messages.Lobby-Messages.Waiting-For-Players").replace("%MINPLAYERS%", Integer.toString(minmPlayers)));
            return;
          }
        } else {
          getPlugin().getChatManager().broadcast(this, getPlugin().getChatManager().colorMessage("In-Game.Messages.Lobby-Messages.Enough-Players-To-Start"));
          setArenaState(ArenaState.STARTING);
          if(HolidayManager.getCurrentHoliday() != HolidayManager.HolidayType.NONE) {
            initPoll();
          }
          Bukkit.getPluginManager().callEvent(new BBGameStartEvent(this));
          setTimer(getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.LOBBY, this));
        }
        setTimer(getTimer() - 1);
        break;
      case STARTING:
        int lobbyTimer = getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.LOBBY, this);
        int timer = getTimer();
        float exp = (float) (timer / (double) lobbyTimer);

        if(exp > 1f || exp < 0f) {
          exp = 1f;
        }

        for(Player player : getPlayers()) {
          player.setExp(exp);
          player.setLevel(timer);
        }

        int minPlayers = getMinimumPlayers();

        if(getPlayers().size() < minPlayers && !isForceStart()) {
          getPlugin().getChatManager().broadcast(this, getPlugin().getChatManager().colorMessage("In-Game.Messages.Lobby-Messages.Waiting-For-Players").replace("%MINPLAYERS%", Integer.toString(minPlayers)));
          setArenaState(ArenaState.WAITING_FOR_PLAYERS);
          Bukkit.getPluginManager().callEvent(new BBGameStartEvent(this));
          setTimer(lobbyTimer);
          for(Player player : getPlayers()) {
            player.setExp(1);
            player.setLevel(0);
          }
          break;
        }
        if(getTimer() == 0 || isForceStart()) {
          particleRefreshSched = new ParticleRefreshScheduler(getPlugin());
          if(!getPlotManager().isPlotsCleared()) {
            getPlotManager().resetQueuedPlots();
          }
          setArenaState(ArenaState.IN_GAME);
          distributePlots();
          setTimer(getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.THEME_VOTE, this));
          for(Player player : getPlayers()) {
            player.getInventory().clear();
            player.setGameMode(GameMode.CREATIVE);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.getInventory().setItem(8, getPlugin().getOptionsRegistry().getMenuItem());
            //to prevent Multiverse changing gamemode bug
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> player.setGameMode(GameMode.CREATIVE), 40);
            getPlugin().getRewardsHandler().performReward(player, Reward.RewardType.START_GAME, -1);
          }
        }
        if(isForceStart()) {
          setForceStart(false);
        }
        setTimer(getTimer() - 1);
        break;
      case IN_GAME:
        if(getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
          getPlugin().getServer().setWhitelist(getMaximumPlayers() <= getPlayers().size());
        }
        if(themeVoteTime) {
          if(!themeTimerSet) {
            setTimer(getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.THEME_VOTE, this));
            setThemeTimerSet(true);
            for(Player p : getPlayers()) {
              p.openInventory(voteMenu.getInventory());
            }
          }

          getPlayers().forEach(voteMenu::updateInventory);

          if(getTimer() == 0) {
            setThemeVoteTime(false);
            if(getVotePoll() != null) {
              setTheme(getVotePoll().getVotedTheme());
            }
            setTimer(getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.BUILD, this));
            String message = getPlugin().getChatManager().colorMessage("In-Game.Messages.Lobby-Messages.Game-Started");
            for(Player p : getPlayers()) {
              p.closeInventory();
              Plot plot = getPlotManager().getPlot(p);
              if(plot != null) {
                p.teleport(plot.getTeleportLocation());
              }

              p.sendMessage(getPlugin().getChatManager().getPrefix() + message);
            }
          } else {
            setTimer(getTimer() - 1);
          }
          break;
        }
        if(!enoughPlayersToContinue()) {
          getPlugin().getChatManager().broadcast(this, getPlugin().getChatManager().colorMessage("In-Game.Messages.Game-End-Messages.Only-You-Playing"));
          setArenaState(ArenaState.ENDING);
          Bukkit.getPluginManager().callEvent(new BBGameEndEvent(this));
          setTimer(10);
        }

        int timerco = getTimer();

        if((timerco == (4 * 60) || timerco == (3 * 60) || timerco == (5 * 60) || timerco == 30 || timerco == (2 * 60) || timerco == 60 || timerco == 15) && !voteTime) {
          sendBuildLeftTimeMessage();
        }

        if(timerco != 0 && !receivedVoteItems) {
          if(getOption(ArenaOption.IN_PLOT_CHECKER) == 1) {
            setOptionValue(ArenaOption.IN_PLOT_CHECKER, 0);
            for(Player player : getPlayers()) {
              User user = getPlugin().getUserManager().getUser(player);
              Plot buildPlot = user.getCurrentPlot();
              if(buildPlot != null && buildPlot.getCuboid() != null && !buildPlot.getCuboid().isInWithMarge(player.getLocation(), 5)) {
                player.teleport(buildPlot.getTeleportLocation());
                player.sendMessage(getPlugin().getChatManager().getPrefix() + getPlugin().getChatManager().colorMessage("In-Game.Messages.Cant-Fly-Outside-Plot"));
              }
            }
          }
          addOptionValue(ArenaOption.IN_PLOT_CHECKER, 1);
        } else if(timerco == 0 && !receivedVoteItems) {
          for(Player player : getPlayers()) {
            User user = getPlugin().getUserManager().getUser(player);

            if(user.isSpectator()) continue;
            if(!queue.contains(user.getCurrentPlot())) {
              queue.add(user.getCurrentPlot());
            }
            player.getInventory().clear();
            getPlugin().getVoteItems().giveVoteItems(player);
            user.setStat(StatsStorage.StatisticType.LOCAL_POINTS, 3);
          }
          receivedVoteItems = true;
        }
        if(getTimer() == 0 && receivedVoteItems) {
          setVoting(true);
          if(!queue.isEmpty()) {
            voteForNextPlot();
          } else {
            if(votingPlot != null) {
              if(votingPlot.getPoints() == 0) {
                String message = getPlugin().getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Voted-For-Player-Plot").replace("%PLAYER%", votingPlot.getFormattedMembers());
                for(Player player : getPlayers()) {
                  if(getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.ANNOUNCE_PLOTOWNER_LATER)) {
                    for(Player p : getPlayers()) {
                      String owner = getPlugin().getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Plot-Owner-Title");
                      owner = formatWinners(votingPlot, owner);
                      VersionUtils.sendTitle(p, owner, 5, 40, 5);
                      p.sendMessage(getPlugin().getChatManager().getPrefix() + message);
                    }
                  }
                  User user = getPlugin().getUserManager().getUser(player);
                  int points = user.getStat(StatsStorage.StatisticType.LOCAL_POINTS);
                  //no vote made, in this case make it a good vote
                  if(points == 0) {
                    points = 3;
                  }
                  if(!votingPlot.getMembers().contains(player))
                    votingPlot.setPoints(votingPlot.getPoints() + points);
                  user.setStat(StatsStorage.StatisticType.LOCAL_POINTS, 3);
                }
              }
            }
            calculateResults();
            Plot winnerPlot = null;
            for(List<Player> potentialWinners : topList.values()) {
              if(!potentialWinners.isEmpty()) {
                winnerPlot = getPlotManager().getPlot(potentialWinners.get(0));
                break;
              }
            }
            if(winnerPlot == null) {
              getPlugin().getLogger().log(Level.SEVERE, "Fatal error in getting winner plot in game! No plot contain any online player!");
              setArenaState(ArenaState.ENDING);
              Bukkit.getPluginManager().callEvent(new BBGameEndEvent(this));
              setTimer(10);
              break;
            }
            announceResults();

            Location winnerLocation = winnerPlot.getTeleportLocation();
            List<Player> players = getPlayers();
            players.addAll(getSpectators());
            for(Player player : players) {
              player.teleport(winnerLocation);
              String winner = getPlugin().getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Winner-Title");
              winner = formatWinners(winnerPlot, winner);
              VersionUtils.sendTitle(player, winner, 5, 35, 5);
            }
            setArenaState(ArenaState.ENDING);
            Bukkit.getPluginManager().callEvent(new BBGameEndEvent(this));
            setTimer(10);
          }
        }
        setTimer(getTimer() - 1);
        break;
      case ENDING:
        List<Player> players = getPlayers();
        getScoreboardManager().stopAllScoreboards();
        if(getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
          getPlugin().getServer().setWhitelist(false);
        }
        setVoting(false);
        setThemeTimerSet(false);
        if(getPlugin().getConfig().getBoolean("Firework-When-Game-Ends", true)) {
          for(Player player : players) {
            MiscUtils.spawnRandomFirework(player.getLocation());
          }
        }
        if(getTimer() <= 0) {
          for(Player spectator: getSpectators()){
            if(!players.contains(spectator)){
              players.add(spectator)
            }
          }
          for(Player player : players) {
            User user = plugin.getUserManager().getUser(player);
            user.removeScoreboard(this);
            user.setSpectator(false);
            teleportToEndLocation(player);
            doBarAction(BarAction.REMOVE, player);
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
            player.setFlying(false);
            player.setAllowFlight(false);
            player.getInventory().setArmorContents(null);
            player.sendMessage(getPlugin().getChatManager().getPrefix() + getPlugin().getChatManager().colorMessage("Commands.Teleported-To-The-Lobby"));
            user.addStat(StatsStorage.StatisticType.GAMES_PLAYED, 1);
            if(getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
              InventorySerializer.loadInventory(getPlugin(), player);
            }
            //plot might be already deleted by team mate in TEAM game mode
            Plot plot = getPlotManager().getPlot(player);
            if(plot != null) {
              plot.fullyResetPlot();
            }
          }
          giveRewards();
          clearPlayers();
          if(particleRefreshSched != null) {
            particleRefreshSched.task.cancel();
          }
          setArenaState(ArenaState.RESTARTING);
        }
        setTimer(getTimer() - 1);
        break;
      case RESTARTING:
        setTimer(14);
        setVoting(false);
        receivedVoteItems = false;
        setOptionValue(ArenaOption.IN_PLOT_CHECKER, 0);
        setArenaState(ArenaState.WAITING_FOR_PLAYERS);
        topList.clear();
        //clear plot members
        for(Plot plot : getPlotManager().getPlots()) {
          plot.getMembers().clear();
        }
        setThemeTimerSet(false);
        setThemeVoteTime(true);
        voteMenu.resetPoll();
        for(Player spectator : new ArrayList<>(getSpectators())) {
          ArenaManager.leaveAttempt(spectator, this);
        }
        if(getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
          if(ConfigUtils.getConfig(getPlugin(), "bungee").getBoolean("Shutdown-When-Game-Ends")) {
            getPlugin().getServer().shutdown();
          }
          ArenaRegistry.shuffleBungeeArena();
          for(Player player : Bukkit.getOnlinePlayers()) {
            ArenaManager.joinAttempt(player, ArenaRegistry.getArenas().get(ArenaRegistry.getBungeeArena()));
          }
        }
    }
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
        getGameBar().setTitle(getPlugin().getChatManager().colorMessage(voteTime ? "Bossbar.Vote-Time-Left" : "Bossbar.Time-Left")
            .replace("%time%", Integer.toString(getTimer())));
        break;
      default:
        break;
    }
  }

  @Override
  public void distributePlots() {
    //clear plots before distribution to avoid problems
    for(Plot plot : getPlotManager().getPlots()) {
      plot.getMembers().clear();
    }
    List<Player> players = new ArrayList<>(getPlayers());
    for(Plot plot : getPlotManager().getPlots()) {
      if(players.isEmpty()) {
        break;
      }

      Player first = players.get(0);
      User user = plugin.getUserManager().getUser(first);
      if(user.isSpectator()) {
        continue;
      }

      plot.addMember(first, this, true);
      user.setCurrentPlot(plot);

      players.remove(0);
    }
    if(!players.isEmpty()) {
      MessageUtils.errorOccurred();
      Debugger.sendConsoleMsg("&c[Build Battle] [PLOT WARNING] Not enough plots in arena " + getID() + "! Lacks " + players.size() + " plots");
      Debugger.sendConsoleMsg("&c[PLOT WARNING] Required " + getPlayers().size() + " but have " + getPlotManager().getPlots().size());
      Debugger.sendConsoleMsg("&c[PLOT WARNING] Instance was stopped!");
      ArenaManager.stopGame(false, this);
    }
    getPlotManager().teleportToPlots();
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
      String message = getPlugin().getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Voting-For-Player-Plot").replace("%PLAYER%", plot.getFormattedMembers());

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

  public String formatWinners(Plot plot, String string) {
    return string.replace("%player%", plot.getFormattedMembers());
  }

  public void voteForNextPlot() {
    if(votingPlot != null) {
      if(votingPlot.getPoints() == 0) {
        for(Player player : getPlayers()) {
          User user = getPlugin().getUserManager().getUser(player);
          if(!votingPlot.getMembers().contains(player))
            votingPlot.setPoints(votingPlot.getPoints() + user.getStat(StatsStorage.StatisticType.LOCAL_POINTS));
          user.setStat(StatsStorage.StatisticType.LOCAL_POINTS, 3);
          if(!player.getInventory().contains(plugin.getVoteItems().getReportItem())) {
            player.getInventory().setItem(plugin.getVoteItems().getReportVoteItem().getSlot(), plugin.getVoteItems().getReportVoteItem().getItemStack());
            player.updateInventory();
          }
        }
      }
      if(!votingPlot.getMembers().isEmpty() && getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.ANNOUNCE_PLOTOWNER_LATER)) {
        String message = getPlugin().getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Voted-For-Player-Plot").replace("%PLAYER%", votingPlot.getFormattedMembers());
        for(Player p : getPlayers()) {
          String owner = getPlugin().getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Plot-Owner-Title");
          owner = formatWinners(votingPlot, owner);
          VersionUtils.sendTitle(p, owner, 5, 40, 5);
          p.sendMessage(getPlugin().getChatManager().getPrefix() + message);
        }
      }
    }
    voteRoutine();
  }

  /**
   * Get plot where players are voting currently
   *
   * @return Plot object where players are voting
   */
  public Plot getVotingPlot() {
    return votingPlot;
  }

  private void announceResults() {
    List<String> messages = LanguageManager.getLanguageList("In-Game.Messages.Voting-Messages.Summary");
    List<String> formattedSummary = new ArrayList<>();
    for(String summary : messages) {
      String message = getPlugin().getChatManager().colorRawMessage(summary);
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
          List<Player> list = topList.get(i);

          if(list != null && !list.isEmpty()) {
            Plot plot = getPlotManager().getPlot(list.get(0));
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
      formattedSummary.add(message);
    }
    List<Player> players = getPlayers();
    players.addAll(getSpectators());
    players.forEach(player -> formattedSummary.forEach(msg -> MiscUtils.sendCenteredMessage(player, msg)));
    for(Map.Entry<Integer, List<Player>> map : topList.entrySet()) {
      for(Player p : map.getValue()) {
        if(map.getKey() > 3) {
          p.sendMessage(getPlugin().getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Summary-Other-Place").replace("%number%", Integer.toString(map.getKey())));
        }
        User user = getPlugin().getUserManager().getUser(p);
        Plot plot = getPlotManager().getPlot(p);
        if(plot != null) {
          if(plot.getPoints() > user.getStat(StatsStorage.StatisticType.HIGHEST_POINTS)) {
            user.setStat(StatsStorage.StatisticType.HIGHEST_POINTS, plot.getPoints());
          }
          user.addStat(StatsStorage.StatisticType.TOTAL_POINTS_EARNED, plot.getPoints());
        }
        if(map.getKey() != 1) {
          user.addStat(StatsStorage.StatisticType.LOSES, 1);
          continue;
        }
        user.addStat(StatsStorage.StatisticType.WINS, 1);
        if(plot != null && plot.getPoints() > user.getStat(StatsStorage.StatisticType.HIGHEST_WIN)) {
          user.setStat(StatsStorage.StatisticType.HIGHEST_WIN, plot.getPoints());
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

  private void calculateResults() {
    for(int b = 1; b <= getPlayers().size(); b++) {
      topList.put(b, new ArrayList<>());
    }
    for(Plot buildPlot : getPlotManager().getPlots()) {
      long i = buildPlot.getPoints();

      for(Map.Entry<Integer, List<Player>> map : new HashMap<>(topList).entrySet()) {
        Player first = map.getValue().isEmpty() ? null : map.getValue().get(0);

        Plot plot = getPlotManager().getPlot(first);
        if(plot == null) {
          topList.put(map.getKey(), buildPlot.getMembers());
          break;
        }
        if(i > plot.getPoints()) {
          moveScore(map.getKey(), buildPlot.getMembers());
          break;
        }
        if(i == plot.getPoints()) {
          List<Player> winners = topList.getOrDefault(map.getKey(), new ArrayList<>());
          winners.addAll(buildPlot.getMembers());
          topList.put(map.getKey(), winners);
          break;
        }
      }
    }
  }

  private void moveScore(int pos, List<Player> owners) {
    List<Player> after = topList.getOrDefault(pos, new ArrayList<>());
    topList.put(pos, owners);
    if(pos <= getPlayers().size() && !after.isEmpty()) {
      moveScore(pos + 1, after);
    }
  }

  public boolean enoughPlayersToContinue() {
    return getPlayers().size() >= 2;
  }

  @Override
  public void giveRewards() {
    for(int i = 1; i <= topList.size(); i++) {
      List<Player> list = topList.get(i);
      if(list != null) {
        for(Player player : list) {
          getPlugin().getRewardsHandler().performReward(player, Reward.RewardType.PLACE, i);
        }
      }
    }
    getPlugin().getRewardsHandler().performReward(this, Reward.RewardType.END_GAME);
  }

  public boolean isThemeVoteTime() {
    return themeVoteTime;
  }

  public void setThemeVoteTime(boolean themeVoteTime) {
    this.themeVoteTime = themeVoteTime;
  }

  public boolean isThemeTimerSet() {
    return themeTimerSet;
  }

  public void setThemeTimerSet(boolean themeTimerSet) {
    this.themeTimerSet = themeTimerSet;
  }

}
