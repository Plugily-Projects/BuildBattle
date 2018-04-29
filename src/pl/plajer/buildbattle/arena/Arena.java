package pl.plajer.buildbattle.arena;

import pl.plajer.buildbattle.BuildPlot;
import pl.plajer.buildbattle.ConfigPreferences;
import pl.plajer.buildbattle.GameAPI;
import pl.plajer.buildbattle.PlotManager;
import pl.plajer.buildbattle.User;
import pl.plajer.buildbattle.VoteItems;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.handlers.ConfigurationManager;
import pl.plajer.buildbattle.handlers.MessageHandler;
import pl.plajer.buildbattle.handlers.UserManager;
import pl.plajer.buildbattle.items.SpecialItem;
import pl.plajer.buildbattle.items.SpecialItemManager;
import pl.plajer.buildbattle.utils.IngameMenu;
import pl.plajer.buildbattle.scoreboards.ScoreboardHandler;
import pl.plajer.buildbattle.selfmadeevents.GameChangeStateEvent;
import pl.plajer.buildbattle.selfmadeevents.GameEndEvent;
import pl.plajer.buildbattle.selfmadeevents.GameStartEvent;
import pl.plajer.buildbattle.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.bossbar.BossBarAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Tom on 17/08/2015.
 */
public class Arena extends BukkitRunnable {

    public static GameAPI plugin;
    private static List<String> themes = new ArrayList<>();
    private static List<Integer> blacklist = new ArrayList<>();
    protected HashMap<ArenaState, String[]> signlines = new HashMap<>();
    protected String[] FULLlines;
    private ScoreboardHandler scoreboardHandler;
    private HashMap<Integer, UUID> toplist = new HashMap<>();
    private String theme = "Theme";
    private PlotManager plotManager;
    private boolean receivedVoteItems;
    private Queue<UUID> queue = new LinkedList<>();
    private Random random = new Random();
    private int extracounter;
    private BuildPlot votingPlot = null;
    private boolean votetime;
    private boolean scoreboardDisabled = ConfigPreferences.isScoreboardDisabled();
    private boolean BAR_ENABLED = ConfigPreferences.isBarEnabled();
    private int BUILDTIME = ConfigPreferences.getBuildTime();
    private boolean PLAYERS_OUTSIDE_GAME_ENABLED = ConfigPreferences.isHidePlayersOutsideGameEnabled();
    private boolean BUNGEE_SHUTDOWN = ConfigPreferences.getBungeeShutdown();
    private boolean RESTART_ON_END = ConfigPreferences.restartOnEnd();
    private int LOBBY_STARTING_TIMER = ConfigPreferences.getLobbyTimer();
    private boolean WIN_COMMANDS_ENABLED = ConfigPreferences.isWinCommandsEnabled();
    private boolean SECOND_PLACE_COMMANDS_ENABLED = ConfigPreferences.isSecondPlaceCommandsEnabled();
    private boolean THIRD_PLACE_COMMANDS_ENABLED = ConfigPreferences.isThirdPlaceCommandsEnabled();
    private boolean END_GAME_COMMANDS_ENABLED = ConfigPreferences.isEndGameCommandsEnabled();
    private HashSet<Location> signs = new HashSet<>();
    private ArenaState gameState;
    private int MIN_PLAYERS = 2;
    private int MAX_PLAYERS = 10;
    private String mapname = "";
    private int timer;
    private String ID;
    private Location lobbyloc = null;
    private Location Startloc = null;
    private Location Endloc = null;
    private HashSet<UUID> players;
    private ChatManager chatManager;

    public Arena(String ID) {
        gameState = ArenaState.WAITING_FOR_PLAYERS;
        chatManager = new ChatManager(this);

        this.ID = ID;
        players = new HashSet<>();
        loadSignLines();
        plotManager = new PlotManager(this);
        scoreboardHandler = new ScoreboardHandler(this);
    }

    public static void addTheme(String string) {
        themes.add(string);
    }

    public static void addToBlackList(int ID) {
        blacklist.add(ID);
    }

    public static GameAPI getPlugin() {
        return plugin;
    }

    public boolean isVoting() {
        return votetime;
    }

    private void setVoting(boolean voting) {
        votetime = voting;
    }

    public PlotManager getPlotManager() {
        return plotManager;
    }

    public boolean needsPlayers() {
        if(!ConfigPreferences.isDynamicSignSystemEnabled()) {
            return true;
        } else {
            return getGameState() == ArenaState.STARTING || getGameState() == ArenaState.WAITING_FOR_PLAYERS;
        }
    }

    private void setRandomTheme() {
        setTheme(themes.get(random.nextInt(themes.size() - 1)));
    }

    public void leaveAttempt(Player p) {
        queue.remove(p.getUniqueId());
        User user = UserManager.getUser(p.getUniqueId());
        if(getGameState() == ArenaState.INGAME || getGameState() == ArenaState.ENDING) UserManager.getUser(p.getUniqueId()).addInt("gamesplayed", 1);
        this.teleportToEndLocation(p);
        this.removePlayer(p);
        if(!user.isSpectator()) {
            getChatManager().broadcastLeaveMessage(p);
        }
        user.setSpectator(false);
        user.removeScoreboard();
        // if(plugin.getPlugin().isBarEnabled())
        //BossbarAPI.removeBar(p);

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
       /* if(!plugin.getPlugin().isBungeeActivated()) {
            plugin.getPlugin().getInventoryManager().loadInventory(p);

        } */
        if(plugin.getPlugin().isInventoryManagerEnabled()) {
            plugin.getPlugin().getInventoryManager().loadInventory(p);
        }
        p.setGameMode(GameMode.SURVIVAL);
        for(Player player : plugin.getPlugin().getServer().getOnlinePlayers()) {
            if(!getPlayers().contains(player)) {
                p.showPlayer(player);
                player.showPlayer(p);
            }
        }
    }

    public void run() {
        if(!this.scoreboardDisabled) updateScoreboard();
        updateNewSign();
        if(BAR_ENABLED) {


            updateBar();
        }
        switch(getGameState()) {

            case WAITING_FOR_PLAYERS:
                getPlotManager().resetPlotsGradually();
                if(getPlayers().size() < getMIN_PLAYERS()) {

                    if(getTimer() <= 0) {
                        setTimer(LOBBY_STARTING_TIMER);
                        getChatManager().broadcastMessage("Waiting-For-Players-Message");
                        return;
                    }
                } else {
                    getChatManager().broadcastMessage("Enough-Players-To-Start", "We now have enough players. The game is starting soon!");
                    setGameState(ArenaState.STARTING);
                    Bukkit.getPluginManager().callEvent(new GameStartEvent(this));

                    setTimer(LOBBY_STARTING_TIMER);
                    this.showPlayers();

                }
                setTimer(getTimer() - 1);
                break;

            case STARTING:
                if(getTimer() == 0) {
                    extracounter = 0;
                    if(!getPlotManager().isPlotsCleared()) {
                        getPlotManager().resetQeuedPlots();
                    }
                    setGameState(ArenaState.INGAME);
                    getPlotManager().distributePlots();
                    getPlotManager().teleportToPlots();
                    setTimer(BUILDTIME);
                    for(Player player : getPlayers()) {
                        player.getInventory().clear();
                        player.setGameMode(GameMode.CREATIVE);
                        if(PLAYERS_OUTSIDE_GAME_ENABLED) hidePlayersOutsideTheGame(player);
                        player.getInventory().setItem(8, IngameMenu.getMenuItem());
                    }
                    setRandomTheme();
                    getChatManager().broadcastMessage("The-Game-Has-Started", "The game has started! Start building guys!!");
                }
                setTimer(getTimer() - 1);

                break;
            case INGAME:
                if(getPlayers().size() <= 1) {
                    getChatManager().broadcastMessage("Only-Player-Left", ChatColor.RED + "U are the only player left. U will be teleported to the lobby");
                    setGameState(ArenaState.ENDING);
                    Bukkit.getPluginManager().callEvent(new GameEndEvent(this));
                    setTimer(10);
                }
                if((getTimer() == (4 * 60) || getTimer() == (3 * 60) || getTimer() == 5 * 60 || getTimer() == 30 || getTimer() == 2 * 60 || getTimer() == 60 || getTimer() == 15) && !this.isVoting()) {
                    getChatManager().broadcastMessage("Time-Left-To-Build", ChatManager.PREFIX + "%FORMATTEDTIME% " + ChatManager.NORMAL + "time left to build!", getTimer());
                }
                if(getTimer() != 0 && !receivedVoteItems) {
                    if(extracounter == 1) {
                        extracounter = 0;
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
                    extracounter++;
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
                        BuildPlot winnerPlot = getPlotManager().getPlot(toplist.get(1));

                        for(Player player : getPlayers()) {
                            player.teleport(winnerPlot.getTeleportLocation());
                        }
                        this.setGameState(ArenaState.ENDING);
                        Bukkit.getPluginManager().callEvent(new GameEndEvent(this));

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
                        if(plugin.getPlugin().isInventoryManagerEnabled()) {
                            plugin.getPlugin().getInventoryManager().loadInventory(player);
                        }

                    }

                    clearPlayers();
                    if(plugin.getPlugin().isBungeeActivated()) {
                        for(Player player : plugin.getPlugin().getServer().getOnlinePlayers()) {
                            this.addPlayer(player);
                        }
                    }
                }
                break;
            case RESTARTING:
                setTimer(14);

                setVoting(false);
                receivedVoteItems = false;
                if(ConfigPreferences.isDynamicSignSystemEnabled()) {
                    plugin.getPlugin().getSignManager().addToQueue(this);
                }
                if(plugin.getPlugin().isBungeeActivated() && ConfigPreferences.getBungeeShutdown()) {
                    plugin.getPlugin().getServer().shutdown();
                }
                if(RESTART_ON_END && BUNGEE_SHUTDOWN) {
                    plugin.getPlugin().getServer().dispatchCommand(plugin.getPlugin().getServer().getConsoleSender(), "restart");
                }


                setGameState(ArenaState.WAITING_FOR_PLAYERS);
                toplist.clear();
        }
    }

    private void hidePlayersOutsideTheGame(Player player) {
        for(Player players : plugin.getPlugin().getServer().getOnlinePlayers()) {
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
                case INGAME:
                    if(!isVoting()) {
                        BossBarAPI.setMessage(player, ChatManager.formatMessage(ChatManager.getSingleMessage("Time-Left-Bar-Message", ChatManager.formatMessage(ChatManager.PREFIX + "Time left :" + ChatManager.HIGHLIGHTED + " %FORMATTEDTIME%")), getTimer()));
                    } else {
                        BossBarAPI.setMessage(player, ChatManager.formatMessage(ChatManager.getSingleMessage("Vote-Time-Left-Bar-Message", ChatManager.PREFIX + "Vote Time left :" + ChatManager.HIGHLIGHTED + " %FORMATTEDTIME%"), getTimer()));

                    }
                    break;
            }


        }
    }

    private void giveRewards() {
        if(WIN_COMMANDS_ENABLED) {
            for(String string : ConfigPreferences.getWinCommands()) {
                plugin.getPlugin().getServer().dispatchCommand(plugin.getPlugin().getServer().getConsoleSender(), string.replaceAll("%PLAYER%", plugin.getPlugin().getServer().getOfflinePlayer(toplist.get(1)).getName()));
            }
        }
        if(SECOND_PLACE_COMMANDS_ENABLED) {
            if(toplist.get(2) != null) {
                for(String string : ConfigPreferences.getSecondPlaceCommands()) {
                    plugin.getPlugin().getServer().dispatchCommand(plugin.getPlugin().getServer().getConsoleSender(), string.replaceAll("%PLAYER%", plugin.getPlugin().getServer().getOfflinePlayer(toplist.get(2)).getName()));
                }
            }
        }
        if(THIRD_PLACE_COMMANDS_ENABLED) {
            if(toplist.get(3) != null) {
                for(String string : ConfigPreferences.getThirdPlaceCommands()) {
                    plugin.getPlugin().getServer().dispatchCommand(plugin.getPlugin().getServer().getConsoleSender(), string.replaceAll("%PLAYER%", plugin.getPlugin().getServer().getOfflinePlayer(toplist.get(3)).getName()));
                }
            }
        }
        if(END_GAME_COMMANDS_ENABLED) {
            for(String string : ConfigPreferences.getEndGameCommands()) {
                for(Player player : getPlayers()) {
                    plugin.getPlugin().getServer().dispatchCommand(plugin.getPlugin().getServer().getConsoleSender(), string.replaceAll("%PLAYER%", player.getName()).replaceAll("%RANG%", Integer.toString(getRang(player))));
                }
            }
        }
    }

    private Integer getRang(Player player) {
        for(int i : toplist.keySet()) {
            if(toplist.get(i).equals(player.getUniqueId())) {
                return i;
            }
        }
        return 0;
    }

    public void start() {
        this.runTaskTimer(plugin.getPlugin(), 20L, 20L);
        System.out.print(getID() + " STARTED!");
        plugin.getPlugin().getSignManager().addToQueue(this);
    }

    private void updateScoreboard() {
        if(getPlayers().size() == 0) return;
        scoreboardHandler.updateScoreboard();
        /*
        for (Player p : getPlayers()) {

            User user = UserManager.getUser(p.getUniqueId());

            user.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            if (user.getScoreboard().getObjective("waiting") == null) {
                user.getScoreboard().registerNewObjective("waiting", "dummy");
                user.getScoreboard().registerNewObjective("starting", "dummy");
                user.getScoreboard().registerNewObjective("ingame", "dummy");

            }
            switch (getGameState()) {
                case WAITING_FOR_PLAYERS:
                    Objective waitingobj = user.getScoreboard().getObjective("waiting");
                    waitingobj.setDisplayName(getChatManager().getMessage("Scoreboard-Header", ChatManager.PREFIX + "Main"));
                    waitingobj.setDisplaySlot(DisplaySlot.SIDEBAR);

                    Score playerscore1 = waitingobj.getScore(getChatManager().getMessage("Scoreboard-Players", ChatManager.NORMAL + "Players: "));
                    playerscore1.setScore(getPlayers().size());
                    Score minplayerscore1 = waitingobj.getScore(getChatManager().getMessage("Scoreboard-MinPlayers-Message", ChatManager.NORMAL + "Min Players: "));
                    minplayerscore1.setScore(getMIN_PLAYERS());

                    break;
                case STARTING:
                    Objective startingobj = user.getScoreboard().getObjective("starting");
                    startingobj.setDisplayName(getChatManager().getMessage("Scoreboard-Header", ChatManager.PREFIX + "Build Battle"));
                    startingobj.setDisplaySlot(DisplaySlot.SIDEBAR);

                    Score timerscore = startingobj.getScore(getChatManager().getMessage("Scoreboard-Starting-In", ChatManager.NORMAL + "Starting in: "));
                    timerscore.setScore(getTimer());

                    Score playerscore = startingobj.getScore(getChatManager().getMessage("Scoreboard-Players", ChatManager.NORMAL + "Players: "));
                    playerscore.setScore(getPlayers().size());
                    Score minplayerscore = startingobj.getScore(getChatManager().getMessage("Scoreboard-MinPlayers-Message", ChatManager.NORMAL + "Min Players: "));
                    minplayerscore.setScore(getMIN_PLAYERS());

                    break;
                case INGAME:
                    user.getScoreboard().getObjective("ingame").unregister();
                    user.getScoreboard().registerNewObjective("ingame", "dummy");
                    Objective ingameobj = user.getScoreboard().getObjective("ingame");
                    ingameobj.setDisplayName(getChatManager().getMessage("Scoreboard-Header", ChatManager.PREFIX + "Freeze Tag"));
                    ingameobj.setDisplaySlot(DisplaySlot.SIDEBAR);
                    Score timeleft = ingameobj.getScore(getChatManager().getMessage("SCOREBOARD-Time-Left", ChatColor.RED + "" + ChatColor.BOLD + "Time Left: "));
                    timeleft.setScore(9);
                    Score timeleftscore = ingameobj.getScore(ChatColor.WHITE + getFormattedTimeLeft());
                    timeleftscore.setScore(8);
                    Score empty = ingameobj.getScore(" ");
                    empty.setScore(6);
                    Score theme = ingameobj.getScore(getChatManager().getMessage("SCOREBOARD-Theme", ChatColor.GREEN + "Theme"));
                    theme.setScore(5);
                    Score themescore = ingameobj.getScore(ChatColor.WHITE + getTheme());
                    themescore.setScore(4);


                    break;

                case ENDING:
                    break;
                case RESTARTING:

                    break;
                default:
                    setGameState(GameState.WAITING_FOR_PLAYERS);
            }
            user.setScoreboard(user.getScoreboard());


        }
        */

    }

    public List<Integer> getBlacklist() {
        return blacklist;
    }

    public void joinAttempt(Player p) {
        if((getGameState() == ArenaState.INGAME || getGameState() == ArenaState.ENDING || getGameState() == ArenaState.RESTARTING)) return;
        if(plugin.getPlugin().isInventoryManagerEnabled()) plugin.getPlugin().getInventoryManager().saveInventoryToFile(p);
        teleportToLobby(p);
        this.addPlayer(p);
        p.setHealth(20.0);
        p.setFoodLevel(20);
        p.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
        p.getInventory().clear();
        showPlayers();
        if(!UserManager.getUser(p.getUniqueId()).isSpectator()) getChatManager().broadcastJoinMessage(p);
        p.updateInventory();
        for(Player player : getPlayers()) {
            showPlayer(player);
        }
        if(ConfigPreferences.isHidePlayersOutsideGameEnabled()) {
            for(Player player : plugin.getPlugin().getServer().getOnlinePlayers()) {
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
            OfflinePlayer player = plugin.getPlugin().getServer().getOfflinePlayer(queue.poll());
            //while(player.isOnline() && !queue.isEmpty()){
            //   player = plugin.getPlugin().getServer().getPlayer(queue.poll());

            //}

            while(getPlotManager().getPlot(player.getUniqueId()) == null && !queue.isEmpty()) {
                System.out.print("A PLAYER HAS NO PLOT!");
                player = plugin.getPlugin().getServer().getPlayer(queue.poll());
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
                    MessageHandler.sendTitleMessage(player1, getChatManager().getMessage("Plot-Owner-Title-Message", ChatManager.PREFIX + "Plot Owner: " + ChatManager.HIGHLIGHTED + "%PLAYER%", player));
                //}
                getChatManager().broadcastMessage("Voting-For-Player-Plot", ChatManager.NORMAL + "Voting for " + ChatManager.HIGHLIGHTED + "%PLAYER%" + ChatManager.NORMAL + "'s plot!", player);
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
            MessageHandler.sendTitleMessage(player, getChatManager().getMessage("Title-Winner-Message", ChatColor.YELLOW + "WINNER: " + ChatColor.GREEN + "%PLAYER%", plugin.getPlugin().getServer().getOfflinePlayer(toplist.get(1))));
        }
        //}
        for(Player player : getPlayers()) {
            player.sendMessage(ChatManager.getSingleMessage("Winner-Announcement-Header-Line", ChatColor.GREEN + "=============================="));
            player.sendMessage(ChatManager.getSingleMessage("Empty-Message", " "));
            player.sendMessage(ChatManager.getSingleMessage("Winner-Announcement-Number-One", ChatColor.YELLOW + "1. " + ChatColor.DARK_GREEN + "%PLAYER%" + ChatColor.GREEN + "- %NUMBER%", plugin.getPlugin().getServer().getOfflinePlayer(toplist.get(1)), getPlotManager().getPlot(toplist.get(1)).getPoints()));
            if(toplist.containsKey(2) && toplist.get(2) != null) {
                if(getPlotManager().getPlot(toplist.get(1)).getPoints() == getPlotManager().getPlot(toplist.get(2)).getPoints()) {
                    player.sendMessage(ChatManager.getSingleMessage("Winner-Announcement-Number-One", ChatColor.YELLOW + "1. " + ChatColor.DARK_GREEN + "%PLAYER%" + ChatColor.GREEN + "- %NUMBER%", plugin.getPlugin().getServer().getOfflinePlayer(toplist.get(2)), getPlotManager().getPlot(toplist.get(2)).getPoints()));
                } else {
                    player.sendMessage(ChatManager.getSingleMessage("Winner-Announcement-Number-Two", ChatColor.YELLOW + "2. " + ChatColor.DARK_GREEN + "%PLAYER%" + ChatColor.GREEN + "- %NUMBER%", plugin.getPlugin().getServer().getOfflinePlayer(toplist.get(2)), getPlotManager().getPlot(toplist.get(2)).getPoints()));
                }
            }
            if(toplist.containsKey(3) && toplist.get(3) != null) {
                if(getPlotManager().getPlot(toplist.get(1)).getPoints() == getPlotManager().getPlot(toplist.get(3)).getPoints()) {
                    player.sendMessage(ChatManager.getSingleMessage("Winner-Announcement-Number-One", ChatColor.YELLOW + "1. " + ChatColor.DARK_GREEN + "%PLAYER%" + ChatColor.GREEN + "- %NUMBER%", plugin.getPlugin().getServer().getOfflinePlayer(toplist.get(3)), getPlotManager().getPlot(toplist.get(3)).getPoints()));
                } else if(getPlotManager().getPlot(toplist.get(2)).getPoints() == getPlotManager().getPlot(toplist.get(3)).getPoints()) {
                    player.sendMessage(ChatManager.getSingleMessage("Winner-Announcement-Number-Two", ChatColor.YELLOW + "2. " + ChatColor.DARK_GREEN + "%PLAYER%" + ChatColor.GREEN + "- %NUMBER%", plugin.getPlugin().getServer().getOfflinePlayer(toplist.get(3)), getPlotManager().getPlot(toplist.get(3)).getPoints()));
                } else {
                    player.sendMessage(ChatManager.getSingleMessage("Winner-Announcement-Number-Three", ChatColor.YELLOW + "3. " + ChatColor.DARK_GREEN + "%PLAYER%" + ChatColor.GREEN + "- %NUMBER%", plugin.getPlugin().getServer().getOfflinePlayer(toplist.get(3)), getPlotManager().getPlot(toplist.get(3)).getPoints()));
                }
            }
            player.sendMessage(ChatManager.getSingleMessage("Empty-Message", " "));
            player.sendMessage(ChatManager.getSingleMessage("Winner-Announcement-Footer-Line", ChatColor.GREEN + "=============================="));
        }
        for(Integer rang : toplist.keySet()) {
            if(toplist.get(rang) != null) {
                if(plugin.getPlugin().getServer().getPlayer(toplist.get(rang)) != null) {
                    plugin.getPlugin().getServer().getPlayer(toplist.get(rang)).sendMessage(ChatManager.getSingleMessage("You-Became-xth", ChatColor.GREEN + "You became " + ChatColor.DARK_GREEN + "%NUMBER%" + ChatColor.GREEN + "th", rang));
                    if(rang == 1) {
                        UserManager.getUser(plugin.getPlugin().getServer().getPlayer(toplist.get(rang)).getUniqueId()).addInt("wins", 1);
                        if(getPlotManager().getPlot(toplist.get(rang)).getPoints() > UserManager.getUser(toplist.get(rang)).getInt("highestwin")) {
                            UserManager.getUser(plugin.getPlugin().getServer().getPlayer(toplist.get(rang)).getUniqueId()).setInt("highestwin", getPlotManager().getPlot(toplist.get(rang)).getPoints());
                        }
                    } else {
                        UserManager.getUser(plugin.getPlugin().getServer().getPlayer(toplist.get(rang)).getUniqueId()).addInt("loses", 1);
                    }
                }
            }
        }
    }

    private void calculateResults() {
        for(int b = 1; b <= 10; b++) {
            toplist.put(b, null);
        }
        for(BuildPlot buildPlot : getPlotManager().getPlots()) {
            long i = buildPlot.getPoints();
            Iterator it = toplist.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                Integer rang = (Integer) pair.getKey();
                if(toplist.get(rang) == null || getPlotManager().getPlot(toplist.get(rang)) == null) {
                    toplist.put(rang, buildPlot.getOwner());
                    break;
                }
                if(i > getPlotManager().getPlot(toplist.get(rang)).getPoints()) {
                    insertScore(rang, buildPlot.getOwner());
                    break;
                }
            }
        }
    }

    private void insertScore(int rang, UUID uuid) {
        UUID after = toplist.get(rang);
        toplist.put(rang, uuid);
        if(!(rang > 10) && after != null) insertScore(rang + 1, after);
    }

    public String getID() {
        return ID;
    }

    public int getMIN_PLAYERS() {
        return MIN_PLAYERS;
    }

    public void setMIN_PLAYERS(int MIN_PLAYERS) {
        this.MIN_PLAYERS = MIN_PLAYERS;
    }

    public String getMapName() {
        return mapname;
    }

    public void setMapName(String mapname) {
        this.mapname = mapname;
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

    public void addSign(Location location) {
        signs.add(location);
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public int getMAX_PLAYERS() {
        return MAX_PLAYERS;
    }

    public void setMAX_PLAYERS(int MAX_PLAYERS) {
        this.MAX_PLAYERS = MAX_PLAYERS;
    }

    public ArenaState getGameState() {
        return gameState;
    }

    public void setGameState(ArenaState gameState) {
        if(getGameState() != null) {
            GameChangeStateEvent gameChangeStateEvent = new GameChangeStateEvent(gameState, this, getGameState());
            plugin.getPlugin().getServer().getPluginManager().callEvent(gameChangeStateEvent);
        }
        this.gameState = gameState;
    }

    public void updateNewSign() {
        if(signs.size() > 0) {
            for(Location location : signs) {
                updateSign((Sign) location.getBlock().getState());
            }
        }
    }

    public HashSet<Location> getSigns() {
        return signs;
    }

    public void updateSign(Sign sign) {
        String[] strings = signlines.get(getGameState());
        if(getGameState() == ArenaState.STARTING || getGameState() == ArenaState.WAITING_FOR_PLAYERS) {
            if(getPlayers().size() >= MAX_PLAYERS) strings = FULLlines;
        }

        int i = 0;
        sign = (Sign) sign.getLocation().clone().getBlock().getState();
        for(String string : strings) {
            sign.setLine(i, formatText(string));

            i++;
        }
        sign.update(true);
        sign.update();
    }


    private String formatText(String s) {
        String returnstring = s;
        returnstring = returnstring.replaceAll("%ARENA%", getID());
        returnstring = returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(getPlayers().size()));
        returnstring = returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(this.MAX_PLAYERS));
        returnstring = returnstring.replaceAll("%MAPNAME%", getMapName());
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        return returnstring;

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
        return lobbyloc;
    }

    public void setLobbyLocation(Location loc) {
        this.lobbyloc = loc;
    }

    public Location getStartLocation() {
        return Startloc;
    }


    public void setStartLocation(Location location) {
        Startloc = location;
    }

    public void teleportToStartLocation(Player player) {
        if(Startloc != null) player.teleport(Startloc);
        else System.out.print("Startlocation for arena " + getID() + " isn't intialized!");
    }

    public void teleportAllToEndLocation() {
        if(plugin.getPlugin().isBungeeActivated()) {
            for(Player player : getPlayers()) {
                plugin.getPlugin().getBungeeManager().connectToHub(player);
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
        if(plugin.getPlugin().isBungeeActivated()) {
            plugin.getPlugin().getBungeeManager().connectToHub(player);
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
        return Endloc;
    }

    public void setEndLocation(Location Endloc) {
        this.Endloc = Endloc;
    }


    public void loadSignLines() {
        FileConfiguration config = ConfigurationManager.getConfig("signModification");
        for(String s : config.getConfigurationSection("signs.format").getKeys(false)) {
            if(s.equalsIgnoreCase("WaitingForNewGame") || s.equalsIgnoreCase("FULL")) continue;
            String path = "signs.format." + s + ".";
            signlines.put(ArenaState.fromString(s), new String[]{
                    config.getString(path + "lines.1"), config.getString(path + "lines.2"), config.getString(path + "lines.3"), config.getString(path + "lines.4")});
            FULLlines = new String[]{config.getString(path + "lines.1"), config.getString(path + "lines.2"), config.getString(path + "lines.3"), config.getString(path + "lines.4")};

        }
    }

}
