package pl.plajer.buildbattle;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import pl.plajer.buildbattle.commands.GameCommands;
import pl.plajer.buildbattle.events.GameEvents;
import pl.plajer.buildbattle.events.JoinEvents;
import pl.plajer.buildbattle.events.SpectatorEvents;
import pl.plajer.buildbattle.handlers.BungeeManager;
import pl.plajer.buildbattle.entities.EntityItem;
import pl.plajer.buildbattle.entities.EntityMenuEvents;
import pl.plajer.buildbattle.events.IngameEvents;
import pl.plajer.buildbattle.events.NormalEvents;
import pl.plajer.buildbattle.arena.ArenaState;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.handlers.ConfigurationManager;
import pl.plajer.buildbattle.handlers.InventoryManager;
import pl.plajer.buildbattle.handlers.SignManager;
import pl.plajer.buildbattle.handlers.UserManager;
import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.items.SpecialItem;
import pl.plajer.buildbattle.particles.ParticleHandler;
import pl.plajer.buildbattle.particles.ParticleMenu;
import pl.plajer.buildbattle.playerheads.PlayerHeadsMenu;
import pl.plajer.buildbattle.stats.BuildBattleStats;
import pl.plajer.buildbattle.stats.FileStats;
import pl.plajer.buildbattle.stats.MySQLDatabase;
import pl.plajer.buildbattle.utils.MetricsLite;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tom on 17/08/2015.
 */
public class Main extends JavaPlugin implements CommandsInterface {

    private static Economy econ = null;
    private static Permission perms = null;
    private boolean databaseActivated = false;
    private MySQLDatabase database;
    private FileStats fileStats;
    private GameAPI gameAPI = new GameAPI();
    private BungeeManager bungeeManager;
    private boolean bungeeActivated;
    private InventoryManager inventoryManager;
    private boolean inventoryManagerEnabled;
    private SignManager signManager;
    private List<String> filesToGenerate = Arrays.asList("EntityMenu", "particles", "scoreboard", "signModification", "SpecialItems", "STATS", "voteItems", "MySQL");

    public static Permission getPerms() {
        return perms;
    }

    public static Economy getEcon() {
        return econ;
    }

    public BungeeManager getBungeeManager() {
        return bungeeManager;
    }

    public boolean isBungeeActivated() {
        return bungeeActivated;
    }

    public SignManager getSignManager() {
        return signManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public boolean isInventoryManagerEnabled() {
        return inventoryManagerEnabled;
    }

    public void setupMessageConfig() {
        ChatManager.getFromLanguageConfig("Waiting-For-Players-Message", "Waiting for players... We need at least " + ChatManager.HIGHLIGHTED + "%MINPLAYERS%" + ChatManager.NORMAL + " players to start.");
        ChatManager.getFromLanguageConfig("Enough-Players-To-Start", "We now have enough players. The game is starting soon!");
        ChatManager.getFromLanguageConfig("The-Game-Has-Started", "The game has started! Start building guys!!");
        ChatManager.getFromLanguageConfig("Time-Left-To-Build", ChatManager.PREFIX + "%FORMATTEDTIME% " + ChatManager.NORMAL + "time left to build!");
        ChatManager.getFromLanguageConfig("Cant-Fly-Out-Of-Plot", ChatColor.RED + "U can't fly so far out!");
        ChatManager.getFromLanguageConfig("Voted", ChatColor.GREEN + "Voted succesfully!");
        ChatManager.getFromLanguageConfig("Voting-For-Player-Plot", ChatManager.NORMAL + "Voting for " + ChatManager.HIGHLIGHTED + "%PLAYER%" + ChatManager.NORMAL + "'s plot!");
        ChatManager.getFromLanguageConfig("Winner-Announcement-Footer-Line", ChatColor.GREEN + "==============================");
        ChatManager.getFromLanguageConfig("Waiting-For-Players-Bar-Message", ChatManager.PREFIX + "BuildBattle made by " + ChatManager.HIGHLIGHTED + "TomTheDeveloper");
        ChatManager.getFromLanguageConfig("Starting-Bar-Message", ChatManager.PREFIX + "BuildBattle made by " + ChatManager.HIGHLIGHTED + "TomTheDeveloper");
        ChatManager.getFromLanguageConfig("Time-Left-Bar-Message", ChatManager.PREFIX + "Time left :" + ChatManager.HIGHLIGHTED + " %FORMATTEDTIME%");
        ChatManager.getFromLanguageConfig("Vote-Time-Left-Bar-Message", ChatManager.PREFIX + "Vote Time left :" + ChatManager.HIGHLIGHTED + " %FORMATTEDTIME%");
        ChatManager.getFromLanguageConfig("Floor-Option-Name", ChatColor.GREEN + "Floor Material");
        ChatManager.getFromLanguageConfig("Floor-Changed", ChatColor.GREEN + "Floor changed!");
        ChatManager.getFromLanguageConfig("Ingame-Menu-Name", "Options Menu");
        ChatManager.getFromLanguageConfig("Options-Menu-Item", ChatColor.GREEN + "Options");
        ChatManager.getFromLanguageConfig("Options-Lore", ChatColor.GRAY + "Right click to open");
        ChatManager.getFromLanguageConfig("Drag-And-Drop-Item-Here", "Drag and drop an item here");
        ChatManager.getFromLanguageConfig("To-Change-Floor", "to change the floor");
        ChatManager.getFromLanguageConfig("Join", ChatManager.HIGHLIGHTED + "%PLAYER%" + ChatColor.GRAY + " joined the game (%PLAYERSIZE%/%MAXPLAYERS%)!");
        ChatManager.getFromLanguageConfig("Leave", ChatManager.HIGHLIGHTED + "%PLAYER% " + ChatColor.GRAY + "left the game (%PLAYERSIZE%/%MAXPLAYERS%)!");
        ChatManager.getFromLanguageConfig("Death", ChatManager.HIGHLIGHTED + "%PLAYER% " + ChatColor.GRAY + "died!");
        ChatManager.getFromLanguageConfig("Seconds-Left-Until-Game-Starts", "The game starts in " + ChatManager.HIGHLIGHTED + "%TIME%" + ChatColor.GRAY + " seconds!");
        ChatManager.getFromLanguageConfig("Waiting-For-Players", "Waiting for players... We need at least " + ChatManager.HIGHLIGHTED + "%MINPLAYERS%" + ChatColor.GRAY + " players to start.");
        ChatManager.getFromLanguageConfig("Enough-Players-To-Start", "We now have enough players. The game is starting soon!");
        ChatManager.getFromLanguageConfig("Teleport-To-EndLocation-In-X-Seconds", "You will be teleported to the lobby in " + ChatManager.HIGHLIGHTED + "%TIME%" + ChatColor.GRAY + " seconds");
        ChatManager.getFromLanguageConfig("Cant-Vote-On-Own-Plot", ChatColor.RED + "U can't vote on your own plot!!");
        ChatManager.getFromLanguageConfig("You-Became-xth", ChatColor.GREEN + "You became " + ChatColor.DARK_GREEN + "%NUMBER%" + ChatColor.GREEN + "th");
        ChatManager.getFromLanguageConfig("Kicked-Game-Already-Started", ChatManager.HIGHLIGHTED + "Kicked! Game has already started!");
        ChatManager.getFromLanguageConfig("Particle-Option-Name", ChatColor.GREEN + "Particles");
        ChatManager.getFromLanguageConfig("Coming-Soon", ChatColor.RED + "Coming Soon");
        ChatManager.getFromLanguageConfig("Heads-Option-Name", ChatColor.GREEN + "Heads");
        ChatManager.getFromLanguageConfig("Only-Command-Ingame-Is-Leave", ChatColor.RED + "You have to leave the game first to perform commands. The only command that works is /leave!");
        ChatManager.getFromLanguageConfig("Particle-Menu-Name", "Particle Menu");
        ChatManager.getFromLanguageConfig("No-Permission-For-This-Particle", ChatColor.RED + " No permission for this particle!");
        ChatManager.getFromLanguageConfig("Reached-Max-Amount-Of-Particles", ChatColor.RED + "Reached max amount of particles!");
        ChatManager.getFromLanguageConfig("Particle-Succesfully-Added", ChatColor.GREEN + "Particle succesfully added!");
        ChatManager.getFromLanguageConfig("Particle-Remove-Menu-Name", "Remove Particles");
        ChatManager.getFromLanguageConfig("Location", "Location: ");
        ChatManager.getFromLanguageConfig("Particle-Removed", ChatColor.GREEN + "Particle Removed!");
        ChatManager.getFromLanguageConfig("Remove-Particle-Item-Lore", "Right click to open menu!");
        ChatManager.getFromLanguageConfig("Particle-Option-Lore", ChatColor.GRAY + "Click to open menu");
        ChatManager.getFromLanguageConfig("Player-Head-Main-Inventory-Name", "Player Head Menu");
        ChatManager.getFromLanguageConfig("Starting-In-Title-Screen", ChatColor.GREEN + "Starting in " + ChatColor.RED + "%NUMBER");
        ChatManager.getFromLanguageConfig("STATS-AboveLine", ChatColor.BOLD + "-----YOUR STATS----- ");
        ChatManager.getFromLanguageConfig("STATS-Wins", ChatColor.GREEN + "Wins: " + ChatColor.YELLOW);
        ChatManager.getFromLanguageConfig("STATS-Loses", ChatColor.GREEN + "Loses: " + ChatColor.YELLOW);
        ChatManager.getFromLanguageConfig("STATS-Games-Played", ChatColor.GREEN + "Games played: " + ChatColor.YELLOW);
        ChatManager.getFromLanguageConfig("STATS-Highest-Win", ChatColor.GREEN + "Highest win: " + ChatColor.YELLOW);
        ChatManager.getFromLanguageConfig("STATS-Blocks-Placed", ChatColor.GREEN + "Blocks Placed: " + ChatColor.YELLOW);
        ChatManager.getFromLanguageConfig("STATS-Blocks-Broken", ChatColor.GREEN + "Blocks Broken: " + ChatColor.YELLOW);
        ChatManager.getFromLanguageConfig("STATS-Particles-Placed", ChatColor.GREEN + "Particles Placed: " + ChatColor.YELLOW);
        ChatManager.getFromLanguageConfig("STATS-UnderLinen", ChatColor.BOLD + "--------------------");
        ChatManager.getFromLanguageConfig("Heads-Option-Lore", ChatColor.GRAY + "Open for heads menu!");
        ChatManager.getFromLanguageConfig("Arena-Does-Not-Exist", ChatColor.RED + "This arena does not exist!");
        ChatManager.getFromLanguageConfig("Arena-Is-Full", ChatColor.RED + "This arena does not exist!");
        ChatManager.getFromLanguageConfig("Arena-Is-Already-Started", ChatColor.RED + "This arena is already started!");
    }

    @Override
    public void onEnable() {
        new ConfigurationManager(this);
        gameAPI.onSetup(this, this);
        initializeClasses();
        bungeeManager = new BungeeManager(this);
        inventoryManager = new InventoryManager(this);
        inventoryManagerEnabled = getConfig().getBoolean("InventoryManager");
        for(String s : filesToGenerate) {
            ConfigurationManager.getConfig(s);
        }
        new ConfigPreferences(this);
        ConfigPreferences.loadOptions();
        ConfigPreferences.loadOptions();
        ConfigPreferences.loadThemes();
        ConfigPreferences.loadBlackList();
        ConfigPreferences.loadWinCommands();
        ConfigPreferences.loadSecondPlaceCommands();
        ConfigPreferences.loadThirdPlaceCommands();
        ConfigPreferences.loadEndGameCommands();
        ConfigPreferences.loadWhitelistedCommands();
        setupMessageConfig();
        ParticleMenu.loadFromConfig();
        PlayerHeadsMenu.loadHeadItems();
        loadInstances();
        SpecialItem.loadAll();
        VoteItems.loadVoteItemsFromConfig();
        this.getServer().getPluginManager().registerEvents(new IngameEvents(this), this);
        EntityItem.loadAll();
        this.getServer().getPluginManager().registerEvents(new EntityMenuEvents(this), this);
        ParticleHandler particleHandler = new ParticleHandler(this);
        particleHandler.start();
        if(!this.getConfig().contains("DatabaseActivated")) this.getConfig().set("DatabaseActivated", false);
        this.saveConfig();
        databaseActivated = this.getConfig().getBoolean("DatabaseActivated");
        if(databaseActivated) this.database = new MySQLDatabase(this);
        else {
            fileStats = new FileStats();
        }
        this.getServer().getPluginManager().registerEvents(new NormalEvents(this), this);
        loadStatsForPlayersOnline();
        BuildBattleStats.plugin = this;
        if(ConfigPreferences.isVaultEnabled()) {
            if(setupEconomy()) System.out.print("NO ECONOMY RELATED TO VAULT FOUND!");
        }
    }

    @Override
    public void onDisable() {
        for(final Player player : getServer().getOnlinePlayers()) {
            if(gameAPI.getGameInstanceManager().getArena(player) != null) {
                gameAPI.getGameInstanceManager().getArena(player).leaveAttempt(player);
            }
            final User user = UserManager.getUser(player.getUniqueId());
            for(final String s : BuildBattleStats.STATISTICS) {
                if(this.isDatabaseActivated()) {
                    int i;
                    try {
                        i = getMySQLDatabase().getStat(player.getUniqueId().toString(), s);
                    } catch(NullPointerException npe) {
                        i = 0;
                        System.out.print("COULDN'T GET STATS FROM PLAYER: " + player.getName());
                    }
                    if(i > user.getInt(s)) {
                        getMySQLDatabase().setStat(player.getUniqueId().toString(), s, user.getInt(s) + i);
                    } else {
                        getMySQLDatabase().setStat(player.getUniqueId().toString(), s, user.getInt(s));
                    }
                } else {
                    getFileStats().saveStat(player, s);
                }
            }
            UserManager.removeUser(player.getUniqueId());
        }
        getMySQLDatabase().closeDatabase();
    }

    private void initializeClasses() {
        User.plugin = this;
        signManager = new SignManager(this);
        bungeeActivated = getConfig().getBoolean("BungeeActivated");
        new GameEvents(this);
        new GameCommands(this);
        new SpectatorEvents(gameAPI);
        new MetricsLite(this);
        new JoinEvents(gameAPI);
    }

    public boolean isDatabaseActivated() {
        return databaseActivated;
    }

    public FileStats getFileStats() {
        return fileStats;
    }

    private boolean setupEconomy() {
        if(getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if(rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public boolean checkPlayerCommands(Player player, Command command, String s, String[] strings) {
        if(strings.length == 2 && strings[0].equalsIgnoreCase("join")) {
            Arena arena = gameAPI.getGameInstanceManager().getArena(strings[1]);
            if(arena == null) {
                player.sendMessage(ChatManager.getSingleMessage("Arena-Does-Not-Exist", ChatColor.RED + "This arena does not exist!"));
                return true;
            } else {
                if(arena.getPlayers().size() >= arena.getMAX_PLAYERS() && !UserManager.getUser(player.getUniqueId()).isPremium()) {
                    player.sendMessage(ChatManager.getSingleMessage("Arena-Is-Full", ChatColor.RED + "This arena does not exist!"));
                    return true;
                } else if(arena.getGameState() == ArenaState.INGAME) {
                    player.sendMessage(ChatManager.getSingleMessage("Arena-Is-Already-Started", ChatColor.RED + "This arena is already started!"));
                    return true;
                } else {
                    arena.joinAttempt(player);
                }
            }
        }
        return false;
    }


    public boolean checkSpecialCommands(Player player, Command command, String s, String[] strings) {
        if(strings.length == 0) {
            player.sendMessage(ChatColor.GOLD + "----------------{BuildBattle Commands}----------");
            player.sendMessage(ChatColor.AQUA + "/BuildBattle create <ARENAID>: " + ChatColor.GRAY + "Create an arena!");
            player.sendMessage(ChatColor.AQUA + "/BuildBattle <ARENAID> edit: " + ChatColor.GRAY + "Opens the menu to edit the arena!");
            player.sendMessage(ChatColor.AQUA + "/BuildBattle addplot <ARENAID>: " + ChatColor.GRAY + "Adds a plot to the arena");
            player.sendMessage(ChatColor.AQUA + "/BuildBattle forcestart: " + ChatColor.GRAY + "Forcestarts the arena u are in");
            player.sendMessage(ChatColor.AQUA + "/BuildBattle reload: " + ChatColor.GRAY + "Reloads plugin");
            player.sendMessage(ChatColor.GOLD + "-------------------------------------------------");
            return true;
        }
        if(strings.length == 2 && strings[0].equalsIgnoreCase("addplot")) {
            if(gameAPI.getGameInstanceManager().getArena(strings[1]) == null) {
                player.sendMessage(ChatColor.RED + "That gameinstance doesn't exist!");
                return true;
            }
            Selection selection = gameAPI.getWorldEditPlugin().getSelection(player);
            if(selection instanceof CuboidSelection) {
                if(getConfig().contains("instances." + strings[1] + ".plots")) {
                    gameAPI.saveLoc("instances." + strings[1] + ".plots." + (getConfig().getConfigurationSection("instances." + strings[1] + ".plots").getKeys(false).size() + 1) + ".minpoint", selection.getMinimumPoint());
                    gameAPI.saveLoc("instances." + strings[1] + ".plots." + (getConfig().getConfigurationSection("instances." + strings[1] + ".plots").getKeys(false).size()) + ".maxpoint", selection.getMaximumPoint());
                } else {
                    gameAPI.saveLoc("instances." + strings[1] + ".plots.0.minpoint", selection.getMinimumPoint());
                    gameAPI.saveLoc("instances." + strings[1] + ".plots.0.maxpoint", selection.getMaximumPoint());
                }
                this.saveConfig();
                player.sendMessage(ChatColor.GREEN + "Plot added to instance " + ChatColor.RED + strings[1]);
            } else {
                player.sendMessage(ChatColor.RED + "U don't have the right selection!");
            }
            return true;
        }
        if(strings.length == 1 && strings[0].equalsIgnoreCase("forcestart")) {
            if(gameAPI.getGameInstanceManager().getArena(player) == null) return false;
            Arena invasionInstance = (Arena) gameAPI.getGameInstanceManager().getArena(player);
            if(invasionInstance.getGameState() == ArenaState.WAITING_FOR_PLAYERS) {
                invasionInstance.setGameState(ArenaState.STARTING);
                invasionInstance.getChatManager().broadcastMessage("Admin-ForceStart-Game", ChatManager.HIGHLIGHTED + "An admin forcestarted the game!");
                return true;
            }
            if(invasionInstance.getGameState() == ArenaState.STARTING) {
                invasionInstance.setTimer(0);
                invasionInstance.getChatManager().broadcastMessage("Admin-Set-Starting-In-To-0", ChatManager.HIGHLIGHTED + "An admin set waiting time to 0. Game starts now!");
                return true;
            }
        }
        if(strings.length == 1 && strings[0].equalsIgnoreCase("reload")) {
            ConfigPreferences.loadOptions();
            ConfigPreferences.loadOptions();
            ConfigPreferences.loadThemes();
            ConfigPreferences.loadBlackList();
            ConfigPreferences.loadWinCommands();
            ConfigPreferences.loadSecondPlaceCommands();
            ConfigPreferences.loadThirdPlaceCommands();
            ConfigPreferences.loadEndGameCommands();
            ConfigPreferences.loadWhitelistedCommands();
            this.loadInstances();
            player.sendMessage(ChatColor.GREEN + "Plugin reloaded!");
        }
        return false;
    }

    public MySQLDatabase getMySQLDatabase() {
        return database;
    }

    public GameAPI getGameAPI() {
        return gameAPI;
    }

    public void loadInstances() {
        this.saveConfig();
        if(gameAPI.getGameInstanceManager().getArenas() != null) {
            if(gameAPI.getGameInstanceManager().getArenas().size() > 0) {
                for(Arena arena : gameAPI.getGameInstanceManager().getArenas()) {
                    signManager.removeSign(arena);
                }
            }
        }
        gameAPI.getGameInstanceManager().getArenas().clear();
        for(String ID : this.getConfig().getConfigurationSection("instances").getKeys(false)) {
            Arena earthMasterInstance;
            String s = "instances." + ID + ".";
            if(s.contains("default")) continue;


            earthMasterInstance = new Arena(ID);


            if(getConfig().contains(s + "minimumplayers")) earthMasterInstance.setMIN_PLAYERS(getConfig().getInt(s + "minimumplayers"));
            else earthMasterInstance.setMIN_PLAYERS(getConfig().getInt("instances.default.minimumplayers"));
            if(getConfig().contains(s + "maximumplayers")) earthMasterInstance.setMAX_PLAYERS(getConfig().getInt(s + "maximumplayers"));
            else earthMasterInstance.setMAX_PLAYERS(getConfig().getInt("instances.default.maximumplayers"));
            if(getConfig().contains(s + "mapname")) earthMasterInstance.setMapName(getConfig().getString(s + "mapname"));
            else earthMasterInstance.setMapName(getConfig().getString("instances.default.mapname"));
            if(getConfig().contains(s + "lobbylocation")) earthMasterInstance.setLobbyLocation(gameAPI.getLocation(s + "lobbylocation"));
            if(getConfig().contains(s + "Startlocation")) earthMasterInstance.setStartLocation(gameAPI.getLocation(s + "Startlocation"));
            else {
                System.out.print(ID + " doesn't contains an start location!");
                gameAPI.getGameInstanceManager().registerArena(earthMasterInstance);
                continue;
            }
            if(getConfig().contains(s + "Endlocation")) earthMasterInstance.setEndLocation(gameAPI.getLocation(s + "Endlocation"));
            else {
                if(!bungeeActivated) {
                    System.out.print(ID + " doesn't contains an end location!");
                    gameAPI.getGameInstanceManager().registerArena(earthMasterInstance);
                    continue;
                }
            }
            if(getConfig().contains(s + "plots")) {
                for(String plotname : getConfig().getConfigurationSection(s + "plots").getKeys(false)) {
                    BuildPlot buildPlot = new BuildPlot();
                    buildPlot.setMAXPOINT(gameAPI.getLocation(s + "plots." + plotname + ".maxpoint"));
                    buildPlot.setMINPOINT(gameAPI.getLocation(s + "plots." + plotname + ".minpoint"));
                    buildPlot.reset();
                    earthMasterInstance.getPlotManager().addBuildPlot(buildPlot);
                }

            } else {
                System.out.print("Instance doesn't contains plots!");
            }
            if(getConfig().contains("newsigns." + earthMasterInstance.getID())) {
                for(String key : getConfig().getConfigurationSection("newsigns." + earthMasterInstance.getID()).getKeys(false)) {
                    if(gameAPI.getLocation("newsigns." + earthMasterInstance.getID() + "." + key).getBlock().getState() instanceof Sign) {
                        earthMasterInstance.addSign(gameAPI.getLocation("newsigns." + earthMasterInstance.getID() + "." + key));
                    } else {
                        Location location = gameAPI.getLocation("newsigns." + earthMasterInstance.getID() + "." + key);
                        System.out.println("SIGN ON LOCATION " + location.getX() + ", " + location.getY() + ", " + location.getZ() + "iIN WORLD " + location.getWorld().getName() + "ISN'T A SIGN!");
                    }
                }
            }

            gameAPI.getGameInstanceManager().registerArena(earthMasterInstance);
            earthMasterInstance.start();


        }
    }


    private void loadStatsForPlayersOnline() {
        for(final Player player : getServer().getOnlinePlayers()) {
            if(bungeeActivated) gameAPI.getGameInstanceManager().getArenas().get(0).teleportToLobby(player);
            if(!this.isDatabaseActivated()) {
                for(String s : BuildBattleStats.STATISTICS) {
                    this.getFileStats().loadStat(player, s);
                }
                return;
            }
            Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
                final String playername = player.getUniqueId().toString();

                @Override
                public void run() {
                    MySQLDatabase database = getMySQLDatabase();
                    ResultSet resultSet = database.executeQuery("SELECT UUID from buildbattlestats WHERE UUID='" + playername + "'");
                    try {
                        if(!resultSet.next()) {
                            database.insertPlayer(playername);
                        }

                        int gamesplayed;
                        int wins;
                        int highestwin;
                        int loses;
                        int blocksPlaced;
                        int blocksBroken;
                        int particles;
                        gamesplayed = database.getStat(player.getUniqueId().toString(), "gamesplayed");
                        wins = database.getStat(player.getUniqueId().toString(), "wins");
                        loses = database.getStat(player.getUniqueId().toString(), "loses");
                        highestwin = database.getStat(player.getUniqueId().toString(), "highestwin");
                        blocksPlaced = database.getStat(player.getUniqueId().toString(), "blocksplaced");
                        blocksBroken = database.getStat(player.getUniqueId().toString(), "blocksbroken");
                        particles = database.getStat(player.getUniqueId().toString(), "particles");
                        User user = UserManager.getUser(player.getUniqueId());

                        user.setInt("gamesplayed", gamesplayed);
                        user.setInt("wins", wins);
                        user.setInt("highestwin", highestwin);
                        user.setInt("loses", loses);
                        user.setInt("blocksplaced", blocksPlaced);
                        user.setInt("blocksbroken", blocksBroken);
                        user.setInt("particles", particles);
                    } catch(SQLException e1) {
                        System.out.print("CONNECTION FAILED FOR PLAYER " + player.getName());
                        //e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            });
        }
    }
}
