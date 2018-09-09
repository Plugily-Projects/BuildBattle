/*
 * BuildBattle 3 - Ultimate building competition minigame
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
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

package pl.plajer.buildbattle3.arena;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import me.clip.placeholderapi.PlaceholderAPI;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import pl.plajer.buildbattle3.ConfigPreferences;
import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.VoteItems;
import pl.plajer.buildbattle3.arena.plots.ArenaPlot;
import pl.plajer.buildbattle3.arena.plots.ArenaPlotManager;
import pl.plajer.buildbattle3.buildbattleapi.BBGameChangeStateEvent;
import pl.plajer.buildbattle3.buildbattleapi.BBGameEndEvent;
import pl.plajer.buildbattle3.buildbattleapi.BBGameStartEvent;
import pl.plajer.buildbattle3.handlers.ChatManager;
import pl.plajer.buildbattle3.handlers.language.LanguageManager;
import pl.plajer.buildbattle3.handlers.language.Locale;
import pl.plajer.buildbattle3.menus.OptionsMenu;
import pl.plajer.buildbattle3.menus.themevoter.VoteMenu;
import pl.plajer.buildbattle3.menus.themevoter.VotePoll;
import pl.plajer.buildbattle3.user.User;
import pl.plajer.buildbattle3.user.UserManager;
import pl.plajer.buildbattle3.utils.MessageUtils;
import pl.plajerlair.core.services.ReportedException;
import pl.plajerlair.core.utils.InventoryUtils;
import pl.plajerlair.core.utils.MinigameScoreboard;
import pl.plajerlair.core.utils.MinigameUtils;

/**
 * Created by Tom on 17/08/2015.
 */
public class Arena extends BukkitRunnable {

    public static Main plugin;
    private static List<Material> blacklist = new ArrayList<>();
    private Map<Integer, List<UUID>> topList = new HashMap<>();
    private String theme = "Theme";
    private ArenaPlotManager plotManager;
    private boolean receivedVoteItems;
    private Queue<UUID> queue = new LinkedList<>();
    private int extraCounter;
    private ArenaPlot votingPlot = null;
    private boolean voteTime;
    private boolean themeVoteTime = true;
    private boolean themeTimerSet = false;
    private boolean bossBarEnabled = ConfigPreferences.isBarEnabled();
    private int BUILD_TIME;
    private boolean PLAYERS_OUTSIDE_GAME_ENABLED = ConfigPreferences.isHidePlayersOutsideGameEnabled();
    private int LOBBY_STARTING_TIMER = ConfigPreferences.getLobbyTimer();
    private boolean WIN_COMMANDS_ENABLED = ConfigPreferences.isWinCommandsEnabled();
    private boolean SECOND_PLACE_COMMANDS_ENABLED = ConfigPreferences.isSecondPlaceCommandsEnabled();
    private boolean THIRD_PLACE_COMMANDS_ENABLED = ConfigPreferences.isThirdPlaceCommandsEnabled();
    private boolean END_GAME_COMMANDS_ENABLED = ConfigPreferences.isEndGameCommandsEnabled();
    private ArenaState gameState;
    private int minimumPlayers = 2;
    private int maximumPlayers = 10;
    private String mapName = "";
    private int timer;
    private String ID;
    private boolean ready = true;
    private Location lobbyLoc = null;
    private Location startLoc = null;
    private Location endLoc = null;
    private Set<UUID> players = new HashSet<>();
    private BossBar gameBar;
    private ArenaType arenaType;
    private VoteMenu voteMenu;
    private Map<String, List<String>> scoreboardContents = new HashMap<>();

    public Arena(String ID) {
        gameState = ArenaState.WAITING_FOR_PLAYERS;
        this.ID = ID;
        if(bossBarEnabled) {
            gameBar = Bukkit.createBossBar(ChatManager.colorMessage("Bossbar.Waiting-For-Players"), BarColor.BLUE, BarStyle.SOLID);
        }
        plotManager = new ArenaPlotManager(this);
        voteMenu = new VoteMenu(this);
        voteMenu.resetPoll();

        for(ArenaState state : ArenaState.values()) {
            if(state == ArenaState.RESTARTING) continue;
            List<String> lines;
            if(LanguageManager.getPluginLocale() == Locale.ENGLISH) {
                lines = LanguageManager.getLanguageFile().getStringList("Scoreboard.Content." + state.getFormattedName());
            } else {
                lines = Arrays.asList(ChatManager.colorMessage("Scoreboard.Content." + state.getFormattedName()).split(";"));
            }
            scoreboardContents.put(state.getFormattedName(), lines);
        }
        List<String> lines;
        if(LanguageManager.getPluginLocale() == Locale.ENGLISH) {
            lines = LanguageManager.getLanguageFile().getStringList("Scoreboard.Content.Playing-Teams");
        } else {
            lines = Arrays.asList(ChatManager.colorMessage("Scoreboard.Content.Playing-Teams").split(";"));
        }
        scoreboardContents.put(ArenaState.IN_GAME.getFormattedName() + "-Teams", lines);
    }

    /**
     * Adds item which can not be used in game
     *
     * @param mat Material to blacklist
     */
    public static void addToBlackList(Material mat) {
        blacklist.add(mat);
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

    public VotePoll getVotePoll() {
        return voteMenu.getVotePoll();
    }

    /**
     * Is voting time in game?
     *
     * @return true = voting time, false = no
     */
    public boolean isVoting() {
        return voteTime;
    }

    void setVoting(boolean voting) {
        voteTime = voting;
    }

    public boolean isThemeVoteTime() {
        return themeVoteTime;
    }

    public void setThemeVoteTime(boolean themeVoteTime) {
        this.themeVoteTime = themeVoteTime;
    }

    public ArenaPlotManager getPlotManager() {
        return plotManager;
    }

    public BossBar getGameBar() {
        return gameBar;
    }

    public int getBuildTime() {
        return BUILD_TIME;
    }

    Queue<UUID> getQueue() {
        return queue;
    }

    public ArenaType getArenaType() {
        return arenaType;
    }

    public void setArenaType(ArenaType arenaType) {
        this.arenaType = arenaType;
        switch(arenaType) {
            case SOLO:
                BUILD_TIME = ConfigPreferences.getBuildTime();
                break;
            case TEAM:
                BUILD_TIME = ConfigPreferences.getTeamBuildTime();
                break;
        }
    }

    public void run() {
        try {
            //idle task
            if(getPlayers().size() == 0 && getArenaState() == ArenaState.WAITING_FOR_PLAYERS) return;
            updateScoreboard();
            if(bossBarEnabled) {
                updateBossBar();
            }
            switch(getArenaState()) {
                case WAITING_FOR_PLAYERS:
                    if(plugin.isBungeeActivated())
                        plugin.getServer().setWhitelist(false);
                    getPlotManager().resetPlotsGradually();
                    if(getPlayers().size() < getMinimumPlayers()) {
                        if(getTimer() <= 0) {
                            setTimer(LOBBY_STARTING_TIMER);
                            String message = ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Waiting-For-Players").replace("%MINPLAYERS%", String.valueOf(getMinimumPlayers()));
                            for(Player p : getPlayers()) {
                                p.sendMessage(ChatManager.PLUGIN_PREFIX + message);
                            }
                            return;
                        }
                    } else {
                        String message = ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Enough-Players-To-Start");
                        for(Player p : getPlayers()) {
                            p.sendMessage(ChatManager.PLUGIN_PREFIX + message);
                        }
                        setGameState(ArenaState.STARTING);
                        Bukkit.getPluginManager().callEvent(new BBGameStartEvent(this));
                        setTimer(LOBBY_STARTING_TIMER);
                        this.showPlayers();
                    }
                    setTimer(getTimer() - 1);
                    break;
                case STARTING:
                    if(getPlayers().size() < getMinimumPlayers()) {
                        String message = ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Waiting-For-Players").replace("%MINPLAYERS%", String.valueOf(getMinimumPlayers()));
                        for(Player p : getPlayers()) {
                            p.sendMessage(ChatManager.PLUGIN_PREFIX + message);
                        }
                        setGameState(ArenaState.WAITING_FOR_PLAYERS);
                        Bukkit.getPluginManager().callEvent(new BBGameStartEvent(this));
                        setTimer(LOBBY_STARTING_TIMER);
                        break;
                    }
                    if(getTimer() == 0) {
                        extraCounter = 0;
                        if(!getPlotManager().isPlotsCleared()) {
                            getPlotManager().resetQeuedPlots();
                        }
                        setGameState(ArenaState.IN_GAME);
                        getPlotManager().distributePlots();
                        getPlotManager().teleportToPlots();
                        setTimer(ConfigPreferences.getThemeVoteTimer());
                        for(Player player : getPlayers()) {
                            player.getInventory().clear();
                            player.setGameMode(GameMode.CREATIVE);
                            player.setAllowFlight(true);
                            player.setFlying(true);
                            if(PLAYERS_OUTSIDE_GAME_ENABLED) hidePlayersOutsideTheGame(player);
                            player.getInventory().setItem(8, OptionsMenu.getMenuItem());
                            //to prevent Multiverse chaning gamemode bug
                            Bukkit.getScheduler().runTaskLater(plugin, () -> player.setGameMode(GameMode.CREATIVE), 20);
                        }
                        break;
                    }
                    setTimer(getTimer() - 1);
                    break;
                case IN_GAME:
                    if(plugin.isBungeeActivated()) {
                        if(getMaximumPlayers() <= getPlayers().size()) {
                            plugin.getServer().setWhitelist(true);
                        } else {
                            plugin.getServer().setWhitelist(false);
                        }
                    }
                    if(isThemeVoteTime()) {
                        if(!themeTimerSet) {
                            setTimer(ConfigPreferences.getThemeVoteTimer());
                            themeTimerSet = true;
                        }
                        for(Player p : getPlayers()) {
                            voteMenu.updateInventory(p);
                        }
                        if(getTimer() == 0) {
                            setThemeVoteTime(false);
                            String votedTheme = voteMenu.getVotePoll().getVotedTheme();
                            setTheme(votedTheme);
                            if(arenaType == ArenaType.SOLO) {
                                setTimer(ConfigPreferences.getBuildTime());
                            } else {
                                setTimer(ConfigPreferences.getTeamBuildTime());
                            }
                            String message = ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Game-Started");
                            for(Player p : getPlayers()) {
                                p.closeInventory();
                                p.teleport(getPlotManager().getPlot(p).getTeleportLocation());
                                p.sendMessage(ChatManager.PLUGIN_PREFIX + message);
                            }
                            break;
                        } else {
                            setTimer(getTimer() - 1);
                            break;
                        }
                    }
                    if(getPlayers().size() <= 2) {
                        if((getPlayers().size() == 1 && arenaType == ArenaType.SOLO) || (getPlayers().size() == 2 && arenaType == ArenaType.TEAM
                                && getPlotManager().getPlot(((Player) getPlayers().toArray()[0]).getUniqueId()).getOwners().contains(((Player) getPlayers().toArray()[1]).getUniqueId()))) {
                            String message = ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Only-You-Playing");
                            for(Player p : getPlayers()) {
                                p.sendMessage(ChatManager.PLUGIN_PREFIX + message);
                            }
                            setGameState(ArenaState.ENDING);
                            Bukkit.getPluginManager().callEvent(new BBGameEndEvent(this));
                            setTimer(10);
                        }
                    }
                    if((getTimer() == (4 * 60) || getTimer() == (3 * 60) || getTimer() == 5 * 60 || getTimer() == 30 || getTimer() == 2 * 60 || getTimer() == 60 || getTimer() == 15) && !this.isVoting()) {
                        String message = ChatManager.colorMessage("In-Game.Messages.Time-Left-To-Build").replace("%FORMATTEDTIME%", MinigameUtils.formatIntoMMSS(getTimer()));
                        String subtitle = ChatManager.colorMessage("In-Game.Messages.Time-Left-Subtitle").replace("%FORMATTEDTIME%", String.valueOf(getTimer()));
                        for(Player p : getPlayers()) {
                            p.sendMessage(ChatManager.PLUGIN_PREFIX + message);
                            p.sendTitle(null, subtitle, 5, 30, 5);
                        }
                    }
                    if(getTimer() != 0 && !receivedVoteItems) {
                        if(extraCounter == 1) {
                            extraCounter = 0;
                            for(Player player : getPlayers()) {
                                User user = UserManager.getUser(player.getUniqueId());
                                ArenaPlot buildPlot = (ArenaPlot) user.getObject("plot");
                                if(buildPlot != null) {
                                    if(!buildPlot.getCuboid().isInWithMarge(player.getLocation(), 5)) {
                                        player.teleport(buildPlot.getTeleportLocation());
                                        player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Cant-Fly-Outside-Plot"));
                                    }
                                }
                            }
                        }
                        extraCounter++;
                    } else if(getTimer() == 0 && !receivedVoteItems) {
                        for(Player player : getPlayers()) {
                            queue.add(player.getUniqueId());
                        }
                        for(Player player : getPlayers()) {
                            player.getInventory().clear();
                            VoteItems.giveVoteItems(player);
                        }
                        receivedVoteItems = true;
                    }
                    if(getTimer() == 0 && receivedVoteItems) {
                        setVoting(true);
                        if(!queue.isEmpty()) {
                            if(getVotingPlot() != null) {
                                for(Player player : getPlayers()) {
                                    getVotingPlot().setPoints(getVotingPlot().getPoints() + UserManager.getUser(player.getUniqueId()).getInt("points"));
                                    UserManager.getUser(player.getUniqueId()).setInt("points", 0);
                                }
                            }
                            if(arenaType == ArenaType.TEAM) {
                                for(ArenaPlot p : getPlotManager().getPlots()) {
                                    if(p.getOwners() != null && p.getOwners().size() == 2) {
                                        //removing second owner to not vote for same plot twice
                                        queue.remove(p.getOwners().get(1));
                                    }
                                }
                            }
                            voteRoutine();
                        } else {
                            if(getVotingPlot() != null) {
                                for(Player player : getPlayers()) {
                                    getVotingPlot().setPoints(getVotingPlot().getPoints() + UserManager.getUser(player.getUniqueId()).getInt("points"));
                                    UserManager.getUser(player.getUniqueId()).setInt("points", 0);
                                }
                            }
                            calculateResults();
                            ArenaPlot winnerPlot = getPlotManager().getPlot(topList.get(1).get(0));
                            announceResults();

                            for(Player player : getPlayers()) {
                                player.teleport(winnerPlot.getTeleportLocation());
                                String winner = ChatManager.colorMessage("In-Game.Messages.Voting-Messages.Winner-Title");
                                if(getArenaType() == ArenaType.TEAM) {
                                    if(winnerPlot.getOwners().size() == 1) {
                                        winner = winner.replace("%player%", Bukkit.getOfflinePlayer(topList.get(1).get(0)).getName());
                                    } else {
                                        winner = winner.replace("%player%", Bukkit.getOfflinePlayer(topList.get(1).get(0)).getName() + " & " + Bukkit.getOfflinePlayer(topList.get(1).get(1)).getName());
                                    }
                                } else {
                                    winner = winner.replace("%player%", Bukkit.getOfflinePlayer(topList.get(1).get(0)).getName());
                                }
                                player.sendTitle(winner, null, 5, 35, 5);
                            }
                            this.setGameState(ArenaState.ENDING);
                            Bukkit.getPluginManager().callEvent(new BBGameEndEvent(this));
                            setTimer(10);
                        }
                    }
                    setTimer(getTimer() - 1);
                    break;
                case ENDING:
                    if(plugin.isBungeeActivated())
                        plugin.getServer().setWhitelist(false);
                    setVoting(false);
                    themeTimerSet = false;
                    for(Player player : getPlayers()) {
                        MinigameUtils.spawnRandomFirework(player.getLocation());
                        showPlayers();
                    }
                    if(getTimer() <= 0) {
                        teleportAllToEndLocation();
                        for(Player player : getPlayers()) {
                            if(bossBarEnabled) {
                                gameBar.removePlayer(player);
                            }
                            player.getInventory().clear();
                            UserManager.getUser(player.getUniqueId()).removeScoreboard();
                            player.setGameMode(GameMode.SURVIVAL);
                            player.setFlying(false);
                            player.setAllowFlight(false);
                            player.getInventory().setArmorContents(null);
                            player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Teleported-To-The-Lobby"));
                            UserManager.getUser(player.getUniqueId()).addInt("gamesplayed", 1);
                            if(plugin.isInventoryManagerEnabled()) {
                                InventoryUtils.loadInventory(plugin, player);
                            }
                            //plot might be already deleted by team mate in TEAM game mode
                            if(plotManager.getPlot(player) != null) {
                                plotManager.getPlot(player).fullyResetPlot();
                            }
                        }
                        giveRewards();
                        clearPlayers();
                        setGameState(ArenaState.RESTARTING);
                        if(plugin.isBungeeActivated()) {
                            for(Player player : plugin.getServer().getOnlinePlayers()) {
                                this.addPlayer(player);
                            }
                        }
                    }
                    setTimer(getTimer() - 1);
                    break;
                case RESTARTING:
                    setTimer(14);
                    setVoting(false);
                    receivedVoteItems = false;
                    if(plugin.isBungeeActivated() && ConfigPreferences.getBungeeShutdown()) {
                        plugin.getServer().shutdown();
                    }
                    setGameState(ArenaState.WAITING_FOR_PLAYERS);
                    topList.clear();
                    themeTimerSet = false;
                    setThemeVoteTime(true);
                    voteMenu.resetPoll();
            }
        } catch(Exception ex) {
            new ReportedException(plugin, ex);
        }
    }

    private void hidePlayersOutsideTheGame(Player player) {
        for(Player players : plugin.getServer().getOnlinePlayers()) {
            if(getPlayers().contains(players)) continue;
            player.hidePlayer(players);
            players.hidePlayer(player);
        }
    }

    private void updateBossBar() {
        switch(getArenaState()) {
            case WAITING_FOR_PLAYERS:
                gameBar.setTitle(ChatManager.colorMessage("Bossbar.Waiting-For-Players"));
                break;
            case STARTING:
                gameBar.setTitle(ChatManager.colorMessage("Bossbar.Starting-In").replace("%time%", String.valueOf(getTimer())));
                break;
            case IN_GAME:
                if(!isVoting()) {
                    gameBar.setTitle(ChatManager.colorMessage("Bossbar.Time-Left").replace("%time%", String.valueOf(getTimer())));
                } else {
                    gameBar.setTitle(ChatManager.colorMessage("Bossbar.Vote-Time-Left").replace("%time%", String.valueOf(getTimer())));
                }
                break;
        }
    }

    private void giveRewards() {
        if(WIN_COMMANDS_ENABLED) {
            if(topList.get(1) != null) {
                for(String string : ConfigPreferences.getWinCommands()) {
                    for(UUID u : topList.get(1))
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), string.replace("%PLAYER%", plugin.getServer().getOfflinePlayer(u).getName()));
                }
            }
        }
        if(SECOND_PLACE_COMMANDS_ENABLED) {
            if(topList.get(2) != null) {
                for(String string : ConfigPreferences.getSecondPlaceCommands()) {
                    for(UUID u : topList.get(2))
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), string.replace("%PLAYER%", plugin.getServer().getOfflinePlayer(u).getName()));
                }
            }
        }
        if(THIRD_PLACE_COMMANDS_ENABLED) {
            if(topList.get(3) != null) {
                for(String string : ConfigPreferences.getThirdPlaceCommands()) {
                    for(UUID u : topList.get(3))
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), string.replace("%PLAYER%", plugin.getServer().getOfflinePlayer(u).getName()));
                }
            }
        }
        if(END_GAME_COMMANDS_ENABLED) {
            for(String string : ConfigPreferences.getEndGameCommands()) {
                for(Player player : getPlayers()) {
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), string.replace("%PLAYER%", player.getName()).replace("%RANG%", Integer.toString(getRang(player))));
                }
            }
        }
    }

    private Integer getRang(Player player) {
        for(int i : topList.keySet()) {
            if(topList.get(i).contains(player.getUniqueId())) {
                return i;
            }
        }
        return 0;
    }

    public void start() {
        this.runTaskTimer(plugin, 20L, 20L);
    }

    private void updateScoreboard() {
        if(getPlayers().size() == 0 || getArenaState() == ArenaState.RESTARTING) return;
        MinigameScoreboard scoreboard;
        for(Player p : getPlayers()) {
            if(p == null) continue;
            scoreboard = new MinigameScoreboard("PL_BB3", "BB_CR", ChatManager.colorMessage("Scoreboard.Title"));
            List<String> lines = scoreboardContents.get(getArenaState().getFormattedName());
            if(getArenaType() == ArenaType.TEAM && getArenaState() == ArenaState.IN_GAME) {
                lines = scoreboardContents.get(getArenaState().getFormattedName() + "-Teams");
            }
            for(String line : lines) {
                scoreboard.addRow(formatScoreboardLine(line, p));
            }
            scoreboard.finish();
            scoreboard.display(p);
        }
    }

    private String formatScoreboardLine(String string, Player player) {
        String returnString = string;
        returnString = StringUtils.replace(returnString, "%PLAYERS%", Integer.toString(getPlayers().size()));
        returnString = StringUtils.replace(returnString, "%PLAYER%", player.getName());
        if(isThemeVoteTime()) {
            returnString = StringUtils.replace(returnString, "%THEME%", ChatManager.colorMessage("In-Game.No-Theme-Yet"));
        } else {
            returnString = StringUtils.replace(returnString, "%THEME%", getTheme());
        }
        returnString = StringUtils.replace(returnString, "%MIN_PLAYERS%", Integer.toString(getMinimumPlayers()));
        returnString = StringUtils.replace(returnString, "%MAX_PLAYERS%", Integer.toString(getMaximumPlayers()));
        returnString = StringUtils.replace(returnString, "%TIMER%", Integer.toString(getTimer()));
        returnString = StringUtils.replace(returnString, "%TIME_LEFT%", Long.toString(getTimeLeft()));
        returnString = StringUtils.replace(returnString, "%FORMATTED_TIME_LEFT%", MinigameUtils.formatIntoMMSS(getTimer()));
        returnString = StringUtils.replace(returnString, "%ARENA_ID%", getID());
        returnString = StringUtils.replace(returnString, "%MAPNAME%", getMapName());
        if(!isThemeVoteTime()) {
            if(getArenaType() == ArenaType.TEAM && getPlotManager().getPlot(player) != null) {
                if(getPlotManager().getPlot(player).getOwners().size() == 2) {
                    if(getPlotManager().getPlot(player).getOwners().get(0).equals(player.getUniqueId())) {
                        returnString = StringUtils.replace(returnString, "%TEAMMATE%", Bukkit.getOfflinePlayer(getPlotManager().getPlot(player).getOwners().get(1)).getName());
                    } else {
                        returnString = StringUtils.replace(returnString, "%TEAMMATE%", Bukkit.getOfflinePlayer(getPlotManager().getPlot(player).getOwners().get(0)).getName());
                    }
                } else {
                    returnString = StringUtils.replace(returnString, "%TEAMMATE%", ChatManager.colorMessage("In-Game.Nobody"));
                }
            }
        } else {
            returnString = StringUtils.replace(returnString, "%TEAMMATE%", ChatManager.colorMessage("In-Game.Nobody"));
        }
        if(plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            PlaceholderAPI.setPlaceholders(player, returnString);
        }
        returnString = ChatManager.colorRawMessage(returnString);
        return returnString;
    }

    /**
     * List of blacklisted materials that cannot be used in game
     *
     * @return blacklisted materials
     */
    public List<Material> getBlacklistedBlocks() {
        return blacklist;
    }

    /**
     * Get arena's building time left
     *
     * @return building time left
     */
    public long getTimeLeft() {
        return getTimer();
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

    private void voteRoutine() {
        if(!queue.isEmpty()) {
            setTimer(ConfigPreferences.getVotingTime());
            OfflinePlayer player = plugin.getServer().getOfflinePlayer(queue.poll());
            while(getPlotManager().getPlot(player.getUniqueId()) == null && !queue.isEmpty()) {
                System.out.print("A PLAYER HAS NO PLOT!");
                player = plugin.getServer().getPlayer(queue.poll());
            }
            if(queue.isEmpty() && getPlotManager().getPlot(player.getUniqueId()) == null) {
                setVotingPlot(null);
            } else {
                // getPlotManager().teleportAllToPlot(plotManager.getPlot(player.getUniqueId()));
                setVotingPlot(plotManager.getPlot(player.getUniqueId()));
                String message = ChatManager.colorMessage("In-Game.Messages.Voting-Messages.Voting-For-Player-Plot").replace("%PLAYER%", player.getName());
                for(Player p : getPlayers()) {
                    p.teleport(getVotingPlot().getTeleportLocation());
                    p.setPlayerWeather(getVotingPlot().getWeatherType());
                    String owner = ChatManager.colorMessage("In-Game.Messages.Voting-Messages.Plot-Owner-Title");
                    if(getArenaType() == ArenaType.TEAM) {
                        if(getVotingPlot().getOwners().size() == 1) {
                            owner = owner.replace("%player%", player.getName());
                        } else {
                            owner = owner.replace("%player%", Bukkit.getOfflinePlayer(getVotingPlot().getOwners().get(0)).getName() + " & " + Bukkit.getOfflinePlayer(getVotingPlot().getOwners().get(1)).getName());
                        }
                    } else {
                        owner = owner.replace("%player%", player.getName());
                    }
                    p.sendTitle(owner, null, 5, 40, 5);
                    p.sendMessage(ChatManager.PLUGIN_PREFIX + message);
                }
            }
        }

    }

    /**
     * Get plot where players are voting currently
     *
     * @return Plot object where players are voting
     */
    public ArenaPlot getVotingPlot() {
        return votingPlot;
    }

    private void setVotingPlot(ArenaPlot buildPlot) {
        votingPlot = buildPlot;
    }

    //fixme wtf
    private void announceResults() {
        List<String> messages;
        if(LanguageManager.getPluginLocale() == Locale.ENGLISH) {
            messages = LanguageManager.getLanguageFile().getStringList("In-Game.Messages.Voting-Messages.Summary-Message");
        } else {
            messages = Arrays.asList(ChatManager.colorMessage("In-Game.Messages.Voting-Messages.Summary-Message").split(";"));
        }
        List<String> formattedSummary = new ArrayList<>();
        for(String summary : messages) {
            String message = summary;
            message = ChatManager.colorRawMessage(message);
            for(int i = 1; i < 4; i++) {
                String access = "One";
                switch(i) {
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
                if(message.contains("%place_" + access.toLowerCase() + "%")) {
                    if(topList.containsKey(i) && topList.get(i) != null && !topList.get(i).isEmpty()) {
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
        getPlayers().forEach((player) -> formattedSummary.forEach((msg) -> MessageUtils.sendCenteredMessage(player, msg)));
        for(Integer rang : topList.keySet()) {
            if(topList.get(rang) != null) {
                for(UUID u : topList.get(rang)) {
                    Player p = plugin.getServer().getPlayer(u);
                    if(p != null) {
                        if(rang > 3) {
                            p.sendMessage(ChatManager.colorMessage("In-Game.Messages.Voting-Messages.Summary-Other-Place").replace("%number%", String.valueOf(rang)));
                        }
                        if(rang == 1) {
                            UserManager.getUser(p.getUniqueId()).addInt("wins", 1);
                            if(getPlotManager().getPlot(u).getPoints() > UserManager.getUser(u).getInt("highestwin")) {
                                UserManager.getUser(p.getUniqueId()).setInt("highestwin", getPlotManager().getPlot(u).getPoints());
                            }
                        } else {
                            UserManager.getUser(p.getUniqueId()).addInt("loses", 1);
                        }
                    }
                }
            }
        }
    }

    private String formatWinners(final List<UUID> winners) {
        List<UUID> uuids = new ArrayList<>(winners);
        StringBuilder builder = new StringBuilder(plugin.getServer().getOfflinePlayer(uuids.get(0)).getName());
        if(uuids.size() == 1) {
            return builder.toString();
        } else {
            uuids.remove(0);
            for(UUID uuid : uuids) {
                builder.append(" & ").append(plugin.getServer().getOfflinePlayer(uuid).getName());
            }
            return builder.toString();
        }
    }

    private void calculateResults() {
        for(int b = 1; b <= getPlayers().size(); b++) {
            topList.put(b, new ArrayList<>());
        }
        for(ArenaPlot buildPlot : getPlotManager().getPlots()) {
            long i = buildPlot.getPoints();
            for(int rang : topList.keySet()) {
                if(topList.get(rang) == null || topList.get(rang).isEmpty() || topList.get(rang).get(0) == null || getPlotManager().getPlot(topList.get(rang).get(0)) == null) {
                    topList.put(rang, buildPlot.getOwners());
                    break;
                }
                if(i > getPlotManager().getPlot(topList.get(rang).get(0)).getPoints()) {
                    moveScore(rang, buildPlot.getOwners());
                    break;
                }
                if(i == getPlotManager().getPlot(topList.get(rang).get(0)).getPoints()) {
                    List<UUID> winners = topList.get(rang);
                    winners.addAll(buildPlot.getOwners());
                    topList.put(rang, winners);
                    break;
                }
            }
        }
    }

    private void moveScore(int pos, List<UUID> uuids) {
        List<UUID> after = topList.get(pos);
        topList.put(pos, uuids);
        if(!(pos > getPlayers().size()) && after != null) moveScore(pos + 1, after);
    }

    /**
     * Get arena ID, ID != map name
     * ID is used to get and manage arenas
     *
     * @return arena ID
     */
    public String getID() {
        return ID;
    }

    /**
     * Min players that are required to start arena
     *
     * @return min players size
     */
    public int getMinimumPlayers() {
        return minimumPlayers;
    }

    public void setMinimumPlayers(int minimumPlayers) {
        this.minimumPlayers = minimumPlayers;
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

    void addPlayer(Player player) {
        players.add(player.getUniqueId());
    }

    void removePlayer(Player player) {
        if(player == null) return;
        if(player.getUniqueId() == null) return;
        players.remove(player.getUniqueId());
    }

    private void clearPlayers() {
        players.clear();
    }

    /**
     * Global timer of arena
     *
     * @return timer of arena
     */
    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    /**
     * Max players size arena can hold
     *
     * @return max players size
     */
    public int getMaximumPlayers() {
        return maximumPlayers;
    }

    public void setMaximumPlayers(int maximumPlayers) {
        this.maximumPlayers = maximumPlayers;
    }

    /**
     * Arena state of arena
     *
     * @return arena state
     * @see ArenaState
     */
    public ArenaState getArenaState() {
        return gameState;
    }

    /**
     * Changes arena state of arena
     * Calls BBGameChangeStateEvent
     *
     * @param gameState arena state to change
     * @see BBGameChangeStateEvent
     */
    public void setGameState(ArenaState gameState) {
        if(getArenaState() != null) {
            BBGameChangeStateEvent gameChangeStateEvent = new BBGameChangeStateEvent(gameState, this, getArenaState());
            plugin.getServer().getPluginManager().callEvent(gameChangeStateEvent);
        }
        this.gameState = gameState;
    }

    void showPlayers() {
        for(Player player : getPlayers()) {
            for(Player p : getPlayers()) {
                player.showPlayer(p);
                p.showPlayer(player);
            }
        }
    }

    /**
     * Get players in game
     *
     * @return HashSet with players
     */
    public HashSet<Player> getPlayers() {
        HashSet<Player> list = new HashSet<>();
        for(UUID uuid : players) {
            list.add(Bukkit.getPlayer(uuid));
        }

        return list;
    }

    void showPlayer(Player p) {
        for(Player player : getPlayers()) {
            player.showPlayer(p);
        }
    }

    public void teleportToLobby(Player player) {
        Location location = getLobbyLocation();
        if(location == null) {
            System.out.print("LobbyLocation isn't intialized for arena " + getID());
        }
        player.teleport(location);
    }

    /**
     * Lobby location of arena
     *
     * @return lobby loc of arena
     */
    public Location getLobbyLocation() {
        return lobbyLoc;
    }

    public void setLobbyLocation(Location loc) {
        this.lobbyLoc = loc;
    }

    /**
     * Start location of arena
     *
     * @return start loc of arena
     */
    public Location getStartLocation() {
        return startLoc;
    }

    private void teleportAllToEndLocation() {
        try {
            if(plugin.isBungeeActivated()) {
                for(Player player : getPlayers()) {
                    plugin.getBungeeManager().connectToHub(player);
                }
                return;
            }
            Location location = getEndLocation();

            if(location == null) {
                location = getLobbyLocation();
                System.out.print("EndLocation for arena " + getID() + " isn't intialized!");
            }
            for(Player player : getPlayers()) {
                player.teleport(location);
            }
        } catch(Exception ex) {
            new ReportedException(plugin, ex);
        }
    }

    public void teleportToEndLocation(Player player) {
        try {
            if(plugin.isBungeeActivated()) {
                plugin.getBungeeManager().connectToHub(player);
                return;
            }
            Location location = getEndLocation();
            if(location == null) {
                location = getLobbyLocation();
                System.out.print("EndLocation for arena " + getID() + " isn't intialized!");
            }

            player.teleport(location);
        } catch(Exception ex) {
            new ReportedException(plugin, ex);
        }
    }

    /**
     * End location of arena
     *
     * @return end loc of arena
     */
    public Location getEndLocation() {
        return endLoc;
    }

    public void setEndLocation(Location endLoc) {
        this.endLoc = endLoc;
    }

    public enum ArenaType {
        SOLO, TEAM
    }

}