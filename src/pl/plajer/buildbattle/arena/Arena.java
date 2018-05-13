package pl.plajer.buildbattle.arena;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.bossbar.BossBarAPI;
import pl.plajer.buildbattle.BuildPlot;
import pl.plajer.buildbattle.ConfigPreferences;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.PlotManager;
import pl.plajer.buildbattle.User;
import pl.plajer.buildbattle.VoteItems;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.handlers.MessageHandler;
import pl.plajer.buildbattle.handlers.UserManager;
import pl.plajer.buildbattle.items.SpecialItem;
import pl.plajer.buildbattle.items.SpecialItemManager;
import pl.plajer.buildbattle.scoreboards.ScoreboardHandler;
import pl.plajer.buildbattle.selfmadeeventsupdateme.BBGameChangeStateEvent;
import pl.plajer.buildbattle.selfmadeeventsupdateme.BBGameEndEvent;
import pl.plajer.buildbattle.selfmadeeventsupdateme.BBGameStartEvent;
import pl.plajer.buildbattle.utils.IngameMenu;
import pl.plajer.buildbattle.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Tom on 17/08/2015.
 */
public class Arena extends BukkitRunnable {

    public static Main plugin;
    private static List<String> themes = new ArrayList<>();
    private static List<Integer> blacklist = new ArrayList<>();
    private ScoreboardHandler scoreboardHandler;
    private Map<Integer, UUID> topList = new HashMap<>();
    private String theme = "Theme";
    private PlotManager plotManager;
    private boolean receivedVoteItems;
    private Queue<UUID> queue = new LinkedList<>();
    private Random random = new Random();
    private int extraCounter;
    private BuildPlot votingPlot = null;
    private boolean voteTime;
    private boolean scoreboardDisabled = ConfigPreferences.isScoreboardDisabled();
    private boolean BAR_ENABLED = ConfigPreferences.isBarEnabled();
    private int BUILD_TIME = ConfigPreferences.getBuildTime();
    private boolean PLAYERS_OUTSIDE_GAME_ENABLED = ConfigPreferences.isHidePlayersOutsideGameEnabled();
    private boolean BUNGEE_SHUTDOWN = ConfigPreferences.getBungeeShutdown();
    private boolean RESTART_ON_END = ConfigPreferences.restartOnEnd();
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
    private Location lobbyLoc = null;
    private Location startLoc = null;
    private Location endLoc = null;
    private Set<UUID> players = new HashSet<>();

    public Arena(String ID) {
        gameState = ArenaState.WAITING_FOR_PLAYERS;
        this.ID = ID;
        plotManager = new PlotManager(this);
        scoreboardHandler = new ScoreboardHandler(this);
    }

    public static void addTheme(String string) {
        themes.add(string);
    }

    public static void addToBlackList(int ID) {
        blacklist.add(ID);
    }

    public static Main getPlugin() {
        return plugin;
    }

    public boolean isVoting() {
        return voteTime;
    }

    private void setVoting(boolean voting) {
        voteTime = voting;
    }

    public PlotManager getPlotManager() {
        return plotManager;
    }

    private void setRandomTheme() {
        setTheme(themes.get(random.nextInt(themes.size() - 1)));
    }

    public void leaveAttempt(Player p) {
        queue.remove(p.getUniqueId());
        User user = UserManager.getUser(p.getUniqueId());
        if(getGameState() == ArenaState.IN_GAME || getGameState() == ArenaState.ENDING) UserManager.getUser(p.getUniqueId()).addInt("gamesplayed", 1);
        this.teleportToEndLocation(p);
        this.removePlayer(p);
        if(!user.isSpectator()) {
            ChatManager.broadcastLeaveMessage(p, this);
        }
        user.setSpectator(false);
        user.removeScoreboard();

        p.setMaxHealth(20.0);
        p.setFoodLevel(20);
        p.setFlying(false);
        p.setAllowFlight(false);
        Util.clearArmor(p);
        p.getInventory().clear();
        for(PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }
        p.setFireTicks(0);
        if(getPlayers().size() == 0) {
            this.setGameState(ArenaState.RESTARTING);
        }
        if(plugin.isInventoryManagerEnabled()) {
            plugin.getInventoryManager().loadInventory(p);
        }
        p.setGameMode(GameMode.SURVIVAL);
        for(Player player : plugin.getServer().getOnlinePlayers()) {
            if(!getPlayers().contains(player)) {
                p.showPlayer(player);
                player.showPlayer(p);
            }
        }
    }

    public void run() {
        //idle task
        if(getPlayers().size() == 0 && getGameState() == ArenaState.WAITING_FOR_PLAYERS) return;
        if(!this.scoreboardDisabled) updateScoreboard();
        if(BAR_ENABLED) {
            updateBar();
        }
        switch(getGameState()) {
            case WAITING_FOR_PLAYERS:
                getPlotManager().resetPlotsGradually();
                if(getPlayers().size() < getMinimumPlayers()) {
                    if(getTimer() <= 0) {
                        setTimer(LOBBY_STARTING_TIMER);
                        ChatManager.broadcastMessage("Waiting-For-Players-Message", this);
                        return;
                    }
                } else {
                    ChatManager.broadcastMessage("Enough-Players-To-Start", "We now have enough players. The game is starting soon!", this);
                    setGameState(ArenaState.STARTING);
                    Bukkit.getPluginManager().callEvent(new BBGameStartEvent(this));
                    setTimer(LOBBY_STARTING_TIMER);
                    this.showPlayers();
                }
                setTimer(getTimer() - 1);
                break;
            case STARTING:
                if(getTimer() == 0) {
                    extraCounter = 0;
                    if(!getPlotManager().isPlotsCleared()) {
                        getPlotManager().resetQeuedPlots();
                    }
                    setGameState(ArenaState.IN_GAME);
                    getPlotManager().distributePlots();
                    getPlotManager().teleportToPlots();
                    setTimer(BUILD_TIME);
                    for(Player player : getPlayers()) {
                        player.getInventory().clear();
                        player.setGameMode(GameMode.CREATIVE);
                        if(PLAYERS_OUTSIDE_GAME_ENABLED) hidePlayersOutsideTheGame(player);
                        player.getInventory().setItem(8, IngameMenu.getMenuItem());
                    }
                    setRandomTheme();
                    ChatManager.broadcastMessage("The-Game-Has-Started", "The game has started! Start building guys!!", this);
                }
                setTimer(getTimer() - 1);
                break;
            case IN_GAME:
                if(getPlayers().size() <= 1) {
                    ChatManager.broadcastMessage("Only-Player-Left", ChatColor.RED + "U are the only player left. U will be teleported to the lobby", this);
                    setGameState(ArenaState.ENDING);
                    Bukkit.getPluginManager().callEvent(new BBGameEndEvent(this));
                    setTimer(10);
                }
                if((getTimer() == (4 * 60) || getTimer() == (3 * 60) || getTimer() == 5 * 60 || getTimer() == 30 || getTimer() == 2 * 60 || getTimer() == 60 || getTimer() == 15) && !this.isVoting()) {
                    ChatManager.broadcastMessage("Time-Left-To-Build", ChatManager.PREFIX + "%FORMATTEDTIME% " + ChatManager.NORMAL + "time left to build!", getTimer(), this);
                }
                if(getTimer() != 0 && !receivedVoteItems) {
                    if(extraCounter == 1) {
                        extraCounter = 0;
                        for(Player player : getPlayers()) {
                            User user = UserManager.getUser(player.getUniqueId());
                            BuildPlot buildPlot = (BuildPlot) user.getObject("plot");
                            if(buildPlot != null) {
                                if(!buildPlot.isInFlyRange(player)) {
                                    player.teleport(buildPlot.getTeleportLocation());
                                    player.sendMessage(ChatManager.getSingleMessage("Cant-Fly-Out-Of-Plot", ChatColor.RED + "U can't fly so far out!"));
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
                    setTimer(1);
                } else if(getTimer() == 0 && receivedVoteItems) {
                    setVoting(true);
                    if(!queue.isEmpty()) {
                        if(getVotingPlot() != null) {
                            for(Player player : getPlayers()) {
                                getVotingPlot().setPoints(getVotingPlot().getPoints() + UserManager.getUser(player.getUniqueId()).getInt("points"));
                                UserManager.getUser(player.getUniqueId()).setInt("points", 0);
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
                        announceResults();
                        giveRewards();
                        BuildPlot winnerPlot = getPlotManager().getPlot(topList.get(1));

                        for(Player player : getPlayers()) {
                            player.teleport(winnerPlot.getTeleportLocation());
                        }
                        this.setGameState(ArenaState.ENDING);
                        Bukkit.getPluginManager().callEvent(new BBGameEndEvent(this));
                        setTimer(10);
                    }
                }
                setTimer(getTimer() - 1);
                break;
            case ENDING:
                setVoting(false);
                setTimer(getTimer() - 1);
                for(Player player : getPlayers()) {
                    Util.spawnRandomFirework(player.getLocation());
                    showPlayers();
                }
                if(getTimer() == 0) {

                    teleportAllToEndLocation();
                    setGameState(ArenaState.RESTARTING);
                    for(Player player : getPlayers()) {
                        player.getInventory().clear();
                        UserManager.getUser(player.getUniqueId()).removeScoreboard();
                        player.setGameMode(GameMode.SURVIVAL);
                        player.setFlying(false);
                        player.setAllowFlight(false);
                        Util.clearArmor(player);
                        UserManager.getUser(player.getUniqueId()).addInt("gamesplayed", 1);
                        if(plugin.isInventoryManagerEnabled()) {
                            plugin.getInventoryManager().loadInventory(player);
                        }

                    }

                    clearPlayers();
                    if(plugin.isBungeeActivated()) {
                        for(Player player : plugin.getServer().getOnlinePlayers()) {
                            this.addPlayer(player);
                        }
                    }
                }
                break;
            case RESTARTING:
                setTimer(14);

                setVoting(false);
                receivedVoteItems = false;
                if(plugin.isBungeeActivated() && ConfigPreferences.getBungeeShutdown()) {
                    plugin.getServer().shutdown();
                }
                //todo remove me
                if(RESTART_ON_END && BUNGEE_SHUTDOWN) {
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "restart");
                }
                setGameState(ArenaState.WAITING_FOR_PLAYERS);
                topList.clear();
        }
    }

    private void hidePlayersOutsideTheGame(Player player) {
        for(Player players : plugin.getServer().getOnlinePlayers()) {
            if(getPlayers().contains(players)) continue;
            player.hidePlayer(players);
            players.hidePlayer(player);
        }
    }

    private void updateBar() {
        for(Player player : getPlayers()) {
            BossBarAPI.removeBar(player);
            switch(getGameState()) {
                case WAITING_FOR_PLAYERS:
                    BossBarAPI.setMessage(player, ChatManager.getSingleMessage("Waiting-For-Players-Bar-Message", ChatManager.PREFIX + "BuildBattle made by " + ChatManager.HIGHLIGHTED + "TomTheDeveloper"));
                    break;
                case STARTING:
                    BossBarAPI.setMessage(player, ChatManager.getSingleMessage("Starting-Bar-Message", ChatManager.PREFIX + "BuildBattle made by " + ChatManager.HIGHLIGHTED + "TomTheDeveloper"));
                    break;
                case IN_GAME:
                    if(!isVoting()) {
                        BossBarAPI.setMessage(player, ChatManager.formatMessage(ChatManager.getSingleMessage("Time-Left-Bar-Message", ChatManager.formatMessage(ChatManager.PREFIX + "Time left :" + ChatManager.HIGHLIGHTED + " %FORMATTEDTIME%", this)), this));
                    } else {
                        BossBarAPI.setMessage(player, ChatManager.formatMessage(ChatManager.getSingleMessage("Vote-Time-Left-Bar-Message", ChatManager.PREFIX + "Vote Time left :" + ChatManager.HIGHLIGHTED + " %FORMATTEDTIME%"), this));
                    }
                    break;
            }
        }
    }

    private void giveRewards() {
        if(WIN_COMMANDS_ENABLED) {
            for(String string : ConfigPreferences.getWinCommands()) {
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), string.replaceAll("%PLAYER%", plugin.getServer().getOfflinePlayer(topList.get(1)).getName()));
            }
        }
        if(SECOND_PLACE_COMMANDS_ENABLED) {
            if(topList.get(2) != null) {
                for(String string : ConfigPreferences.getSecondPlaceCommands()) {
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), string.replaceAll("%PLAYER%", plugin.getServer().getOfflinePlayer(topList.get(2)).getName()));
                }
            }
        }
        if(THIRD_PLACE_COMMANDS_ENABLED) {
            if(topList.get(3) != null) {
                for(String string : ConfigPreferences.getThirdPlaceCommands()) {
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), string.replaceAll("%PLAYER%", plugin.getServer().getOfflinePlayer(topList.get(3)).getName()));
                }
            }
        }
        if(END_GAME_COMMANDS_ENABLED) {
            for(String string : ConfigPreferences.getEndGameCommands()) {
                for(Player player : getPlayers()) {
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), string.replaceAll("%PLAYER%", player.getName()).replaceAll("%RANG%", Integer.toString(getRang(player))));
                }
            }
        }
    }

    private Integer getRang(Player player) {
        for(int i : topList.keySet()) {
            if(topList.get(i).equals(player.getUniqueId())) {
                return i;
            }
        }
        return 0;
    }

    public void start() {
        this.runTaskTimer(plugin, 20L, 20L);
    }

    private void updateScoreboard() {
        if(getPlayers().size() == 0) return;
        scoreboardHandler.updateScoreboard();
    }

    public List<Integer> getBlacklist() {
        return blacklist;
    }

    public void joinAttempt(Player p) {
        if((getGameState() == ArenaState.IN_GAME || getGameState() == ArenaState.ENDING || getGameState() == ArenaState.RESTARTING)) return;
        if(plugin.isInventoryManagerEnabled()) plugin.getInventoryManager().saveInventoryToFile(p);
        teleportToLobby(p);
        this.addPlayer(p);
        p.setHealth(20.0);
        p.setFoodLevel(20);
        p.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
        p.getInventory().clear();
        showPlayers();
        if(!UserManager.getUser(p.getUniqueId()).isSpectator()) ChatManager.broadcastJoinMessage(p, this);
        p.updateInventory();
        for(Player player : getPlayers()) {
            showPlayer(player);
        }
        if(ConfigPreferences.isHidePlayersOutsideGameEnabled()) {
            for(Player player : plugin.getServer().getOnlinePlayers()) {
                if(!getPlayers().contains(player)) {
                    p.hidePlayer(player);
                    player.hidePlayer(p);
                }
            }
        }
        SpecialItem leaveItem = SpecialItemManager.getSpecialItem("Leave");
        p.getInventory().setItem(leaveItem.getSlot(), leaveItem.getItemStack());
    }


    public long getTimeleft() {
        return getTimer();
    }

    public String getFormattedTimeLeft() {
        return Util.formatIntoMMSS(getTimer());
    }

    public String getTheme() {
        return theme;
    }

    private void setTheme(String theme) {
        this.theme = theme;
    }

    private void voteRoutine() {
        if(!queue.isEmpty()) {
            setTimer(ConfigPreferences.getVotingTime());
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
                for(Player player1 : getPlayers()) {
                    player1.teleport(getVotingPlot().getTeleportLocation());
                }
                //todo checks for future versions
                //if(plugin.is1_8_R3()) {
                for(Player player1 : getPlayers())
                    MessageHandler.sendTitleMessage(player1, ChatManager.getMessage("Plot-Owner-Title-Message", ChatManager.PREFIX + "Plot Owner: " + ChatManager.HIGHLIGHTED + "%PLAYER%", player, this), 5, 20, 5, ChatColor.BLACK);
                //}
                ChatManager.broadcastMessage("Voting-For-Player-Plot", ChatManager.NORMAL + "Voting for " + ChatManager.HIGHLIGHTED + "%PLAYER%" + ChatManager.NORMAL + "'s plot!", player, this);
            }
        }

    }

    public BuildPlot getVotingPlot() {
        return votingPlot;
    }

    private void setVotingPlot(BuildPlot buildPlot) {
        votingPlot = buildPlot;
    }

    private void announceResults() {
        //todo checks for future versions
        //if(plugin.is1_8_R3()) {
        for(Player player : getPlayers()) {
            MessageHandler.sendTitleMessage(player, ChatManager.getMessage("Title-Winner-Message", ChatColor.YELLOW + "WINNER: " + ChatColor.GREEN + "%PLAYER%", plugin.getServer().getOfflinePlayer(topList.get(1)), this), 5, 40, 5, ChatColor.BLACK);
        }
        //}
        for(Player player : getPlayers()) {
            player.sendMessage(ChatManager.getSingleMessage("Winner-Announcement-Header-Line", ChatColor.GREEN + "=============================="));
            player.sendMessage(ChatManager.getSingleMessage("Empty-Message", " "));
            player.sendMessage(ChatManager.getSingleMessage("Winner-Announcement-Number-One", ChatColor.YELLOW + "1. " + ChatColor.DARK_GREEN + "%PLAYER%" + ChatColor.GREEN + "- %NUMBER%", plugin.getServer().getOfflinePlayer(topList.get(1)), getPlotManager().getPlot(topList.get(1)).getPoints()));
            if(topList.containsKey(2) && topList.get(2) != null) {
                if(getPlotManager().getPlot(topList.get(1)).getPoints() == getPlotManager().getPlot(topList.get(2)).getPoints()) {
                    player.sendMessage(ChatManager.getSingleMessage("Winner-Announcement-Number-One", ChatColor.YELLOW + "1. " + ChatColor.DARK_GREEN + "%PLAYER%" + ChatColor.GREEN + "- %NUMBER%", plugin.getServer().getOfflinePlayer(topList.get(2)), getPlotManager().getPlot(topList.get(2)).getPoints()));
                } else {
                    player.sendMessage(ChatManager.getSingleMessage("Winner-Announcement-Number-Two", ChatColor.YELLOW + "2. " + ChatColor.DARK_GREEN + "%PLAYER%" + ChatColor.GREEN + "- %NUMBER%", plugin.getServer().getOfflinePlayer(topList.get(2)), getPlotManager().getPlot(topList.get(2)).getPoints()));
                }
            }
            if(topList.containsKey(3) && topList.get(3) != null) {
                if(getPlotManager().getPlot(topList.get(1)).getPoints() == getPlotManager().getPlot(topList.get(3)).getPoints()) {
                    player.sendMessage(ChatManager.getSingleMessage("Winner-Announcement-Number-One", ChatColor.YELLOW + "1. " + ChatColor.DARK_GREEN + "%PLAYER%" + ChatColor.GREEN + "- %NUMBER%", plugin.getServer().getOfflinePlayer(topList.get(3)), getPlotManager().getPlot(topList.get(3)).getPoints()));
                } else if(getPlotManager().getPlot(topList.get(2)).getPoints() == getPlotManager().getPlot(topList.get(3)).getPoints()) {
                    player.sendMessage(ChatManager.getSingleMessage("Winner-Announcement-Number-Two", ChatColor.YELLOW + "2. " + ChatColor.DARK_GREEN + "%PLAYER%" + ChatColor.GREEN + "- %NUMBER%", plugin.getServer().getOfflinePlayer(topList.get(3)), getPlotManager().getPlot(topList.get(3)).getPoints()));
                } else {
                    player.sendMessage(ChatManager.getSingleMessage("Winner-Announcement-Number-Three", ChatColor.YELLOW + "3. " + ChatColor.DARK_GREEN + "%PLAYER%" + ChatColor.GREEN + "- %NUMBER%", plugin.getServer().getOfflinePlayer(topList.get(3)), getPlotManager().getPlot(topList.get(3)).getPoints()));
                }
            }
            player.sendMessage(ChatManager.getSingleMessage("Empty-Message", " "));
            player.sendMessage(ChatManager.getSingleMessage("Winner-Announcement-Footer-Line", ChatColor.GREEN + "=============================="));
        }
        for(Integer rang : topList.keySet()) {
            if(topList.get(rang) != null) {
                if(plugin.getServer().getPlayer(topList.get(rang)) != null) {
                    plugin.getServer().getPlayer(topList.get(rang)).sendMessage(ChatManager.getSingleMessage("You-Became-xth", ChatColor.GREEN + "You became " + ChatColor.DARK_GREEN + "%NUMBER%" + ChatColor.GREEN + "th", rang));
                    if(rang == 1) {
                        UserManager.getUser(plugin.getServer().getPlayer(topList.get(rang)).getUniqueId()).addInt("wins", 1);
                        if(getPlotManager().getPlot(topList.get(rang)).getPoints() > UserManager.getUser(topList.get(rang)).getInt("highestwin")) {
                            UserManager.getUser(plugin.getServer().getPlayer(topList.get(rang)).getUniqueId()).setInt("highestwin", getPlotManager().getPlot(topList.get(rang)).getPoints());
                        }
                    } else {
                        UserManager.getUser(plugin.getServer().getPlayer(topList.get(rang)).getUniqueId()).addInt("loses", 1);
                    }
                }
            }
        }
    }

    private void calculateResults() {
        for(int b = 1; b <= 10; b++) {
            topList.put(b, null);
        }
        for(BuildPlot buildPlot : getPlotManager().getPlots()) {
            long i = buildPlot.getPoints();
            Iterator it = topList.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                Integer rang = (Integer) pair.getKey();
                if(topList.get(rang) == null || getPlotManager().getPlot(topList.get(rang)) == null) {
                    topList.put(rang, buildPlot.getOwner());
                    break;
                }
                if(i > getPlotManager().getPlot(topList.get(rang)).getPoints()) {
                    insertScore(rang, buildPlot.getOwner());
                    break;
                }
            }
        }
    }

    private void insertScore(int rang, UUID uuid) {
        UUID after = topList.get(rang);
        topList.put(rang, uuid);
        if(!(rang > 10) && after != null) insertScore(rang + 1, after);
    }

    public String getID() {
        return ID;
    }

    public int getMinimumPlayers() {
        return minimumPlayers;
    }

    public void setMinimumPlayers(int minimumPlayers) {
        this.minimumPlayers = minimumPlayers;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapname) {
        this.mapName = mapname;
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        if(player == null) return;
        if(player.getUniqueId() == null) return;
        players.remove(player.getUniqueId());
    }

    public void clearPlayers() {
        players.clear();
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }
    
    public int getMaximumPlayers() {
        return maximumPlayers;
    }

    public void setMaximumPlayers(int maximumPlayers) {
        this.maximumPlayers = maximumPlayers;
    }

    public ArenaState getGameState() {
        return gameState;
    }

    public void setGameState(ArenaState gameState) {
        if(getGameState() != null) {
            BBGameChangeStateEvent gameChangeStateEvent = new BBGameChangeStateEvent(gameState, this, getGameState());
            plugin.getServer().getPluginManager().callEvent(gameChangeStateEvent);
        }
        this.gameState = gameState;
    }

    public void showPlayers() {
        for(Player player : getPlayers()) {
            for(Player p : getPlayers()) {
                player.showPlayer(p);
                p.showPlayer(player);
            }
        }
    }

    public HashSet<Player> getPlayers() {
        HashSet<Player> list = new HashSet<>();
        for(UUID uuid : players) {
            list.add(Bukkit.getPlayer(uuid));
        }

        return list;
    }

    public void hidePlayer(Player p) {
        for(Player player : getPlayers()) {
            player.hidePlayer(p);
        }
    }

    public void showPlayer(Player p) {
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

    public Location getLobbyLocation() {
        return lobbyLoc;
    }

    public void setLobbyLocation(Location loc) {
        this.lobbyLoc = loc;
    }

    public Location getStartLocation() {
        return startLoc;
    }


    public void setStartLocation(Location location) {
        startLoc = location;
    }

    public void teleportToStartLocation(Player player) {
        if(startLoc != null) player.teleport(startLoc);
        else System.out.print("Startlocation for arena " + getID() + " isn't intialized!");
    }

    public void teleportAllToEndLocation() {
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
    }

    public void teleportToEndLocation(Player player) {
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
    }

    public Location getEndLocation() {
        return endLoc;
    }

    public void setEndLocation(Location endLoc) {
        this.endLoc = endLoc;
    }

}
