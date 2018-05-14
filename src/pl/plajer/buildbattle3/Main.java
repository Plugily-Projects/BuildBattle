/*
 *  Village Defense 3 - Protect villagers from hordes of zombies
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

package pl.plajer.buildbattle3;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.buildbattle3.arena.Arena;
import pl.plajer.buildbattle3.arena.ArenaRegistry;
import pl.plajer.buildbattle3.plots.Plot;
import pl.plajer.buildbattle3.commands.MainCommand;
import pl.plajer.buildbattle3.entities.EntityItem;
import pl.plajer.buildbattle3.entities.EntityMenuEvents;
import pl.plajer.buildbattle3.events.GameEvents;
import pl.plajer.buildbattle3.events.JoinEvents;
import pl.plajer.buildbattle3.events.SetupInventoryEvents;
import pl.plajer.buildbattle3.events.SpectatorEvents;
import pl.plajer.buildbattle3.handlers.BungeeManager;
import pl.plajer.buildbattle3.handlers.ConfigurationManager;
import pl.plajer.buildbattle3.handlers.InventoryManager;
import pl.plajer.buildbattle3.handlers.PlaceholderManager;
import pl.plajer.buildbattle3.handlers.SignManager;
import pl.plajer.buildbattle3.buildbattleapi.StatsStorage;
import pl.plajer.buildbattle3.user.UserManager;
import pl.plajer.buildbattle3.items.SpecialItem;
import pl.plajer.buildbattle3.language.LanguageManager;
import pl.plajer.buildbattle3.particles.ParticleHandler;
import pl.plajer.buildbattle3.particles.ParticleMenu;
import pl.plajer.buildbattle3.playerheads.PlayerHeadsMenu;
import pl.plajer.buildbattle3.stats.FileStats;
import pl.plajer.buildbattle3.stats.MySQLDatabase;
import pl.plajer.buildbattle3.user.User;
import pl.plajer.buildbattle3.utils.MessageUtils;
import pl.plajer.buildbattle3.utils.MetricsLite;
import pl.plajer.buildbattle3.utils.UpdateChecker;
import pl.plajer.buildbattle3.utils.Util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tom on 17/08/2015.
 */
public class Main extends JavaPlugin {

    private static Economy econ = null;
    private static Permission perms = null;
    private boolean databaseActivated = false;
    private boolean forceDisable = false;
    private static boolean debug;
    private MySQLDatabase database;
    private FileStats fileStats;
    private BungeeManager bungeeManager;
    private boolean bungeeActivated;
    private InventoryManager inventoryManager;
    private boolean inventoryManagerEnabled;
    private SignManager signManager;
    private String version;
    private List<String> filesToGenerate = Arrays.asList("arenas", "EntityMenu", "particles", "scoreboard", "SpecialItems", "stats", "voteItems", "mysql");

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

    public boolean is1_8_R3() {
        return version.equalsIgnoreCase("v1_8_R3");
    }

    public boolean is1_9_R1() {
        return version.equalsIgnoreCase("v1_9_R1");
    }

    public static void debug(String thing, long millis) {
        long elapsed = System.currentTimeMillis() - millis;
        if(debug) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Village Debugger] Running task '" + thing + "'");
        }
        if(elapsed > 15) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Debugger] Slow server response, games may be affected.");
        }
    }

    @Override
    public void onEnable() {
        version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        try {
            Class.forName("org.spigotmc.SpigotConfig");
        } catch(Exception e) {
            MessageUtils.thisVersionIsNotSupported();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Your server software is not supported by Build Battle!");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "We support only Spigot and Spigot forks only! Shutting off...");
            forceDisable = true;
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        //todo migrations
        debug = getConfig().getBoolean("Debug");
        debug("Main setup start", System.currentTimeMillis());
        new ConfigurationManager(this);
        LanguageManager.init(this);
        initializeClasses();
        bungeeManager = new BungeeManager(this);
        inventoryManager = new InventoryManager(this);
        inventoryManagerEnabled = getConfig().getBoolean("InventoryManager");
        for(String s : filesToGenerate) {
            ConfigurationManager.getConfig(s);
        }
        if(getConfig().getBoolean("BungeeActivated")) {
            getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        }
        ConfigPreferences.loadOptions();
        ConfigPreferences.loadOptions();
        ConfigPreferences.loadThemes();
        ConfigPreferences.loadBlackList();
        ConfigPreferences.loadWinCommands();
        ConfigPreferences.loadSecondPlaceCommands();
        ConfigPreferences.loadThirdPlaceCommands();
        ConfigPreferences.loadEndGameCommands();
        ConfigPreferences.loadWhitelistedCommands();
        saveResource("language.yml", false);
        ParticleMenu.loadFromConfig();
        PlayerHeadsMenu.loadHeadItems();
        loadInstances();
        //load signs after arenas
        signManager = new SignManager(this);
        SpecialItem.loadAll();
        VoteItems.loadVoteItemsFromConfig();
        EntityItem.loadAll();
        new EntityMenuEvents(this);
        ParticleHandler particleHandler = new ParticleHandler(this);
        particleHandler.start();
        this.saveConfig();
        databaseActivated = this.getConfig().getBoolean("DatabaseActivated");
        if(databaseActivated) this.database = new MySQLDatabase(this);
        else {
            fileStats = new FileStats();
        }
        loadStatsForPlayersOnline();
        if(ConfigPreferences.isVaultEnabled()) {
            if(setupEconomy()) System.out.print("NO ECONOMY RELATED TO VAULT FOUND!");
        }
    }

    private void checkUpdate() {
        String currentVersion = "v" + Bukkit.getPluginManager().getPlugin("BuildBattle").getDescription().getVersion();
        if(getConfig().getBoolean("Update-Notifier.Enabled")) {
            try {
                UpdateChecker.checkUpdate(currentVersion);
                String latestVersion = UpdateChecker.getLatestVersion();
                if(latestVersion != null) {
                    latestVersion = "v" + latestVersion;
                    if(latestVersion.contains("b")) {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildBattle] Your software is ready for update! However it's a BETA VERSION. Proceed with caution.");
                        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildBattle] Current version %old%, latest version %new%".replaceAll("%old%", currentVersion).replaceAll("%new%", latestVersion));
                    } else {
                        //MessageUtils.updateIsHere();
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Your Build Battle plugin is outdated! Download it to keep with latest changes and fixes.");
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Disable this option in config.yml if you wish.");
                        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Current version: " + ChatColor.RED + currentVersion + ChatColor.YELLOW + " Latest version: " + ChatColor.GREEN + latestVersion);
                    }
                }
            } catch(Exception ex) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildBattle] An error occured while checking for update!");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Please check internet connection or check for update via WWW site directly!");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "WWW site https://www.spigotmc.org/resources/buildbattle-1-8.44703/");
            }
        }
    }

    @Override
    public void onDisable() {
        if(forceDisable) return;
        for(final Player player : getServer().getOnlinePlayers()) {
            Arena arena = ArenaRegistry.getArena(player);
            if(arena != null) {
                arena.leaveAttempt(player);
            }
            final User user = UserManager.getUser(player.getUniqueId());
            for(StatsStorage.StatisticType s : StatsStorage.StatisticType.values()) {
                if(this.isDatabaseActivated()) {
                    int i;
                    try {
                        i = getMySQLDatabase().getStat(player.getUniqueId().toString(), s.getName());
                    } catch(NullPointerException npe) {
                        i = 0;
                        System.out.print("COULDN'T GET STATS FROM PLAYER: " + player.getName());
                    }
                    if(i > user.getInt(s.getName())) {
                        getMySQLDatabase().setStat(player.getUniqueId().toString(), s.getName(), user.getInt(s.getName()) + i);
                    } else {
                        getMySQLDatabase().setStat(player.getUniqueId().toString(), s.getName(), user.getInt(s.getName()));
                    }
                } else {
                    getFileStats().saveStat(player, s.getName());
                }
            }
            UserManager.removeUser(player.getUniqueId());
        }
        if(databaseActivated) getMySQLDatabase().closeDatabase();
    }

    private void initializeClasses() {
        Arena.plugin = this;
        User.plugin = this;
        new SetupInventoryEvents(this);
        bungeeActivated = getConfig().getBoolean("BungeeActivated");
        new GameEvents(this);
        new MainCommand(this);
        new SpectatorEvents(this);
        new MetricsLite(this);
        new JoinEvents(this);
        new GameEvents(this);
        StatsStorage.plugin = this;
        if(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderManager().register();
        }
        new ConfigPreferences(this);
        checkUpdate();
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

    public MySQLDatabase getMySQLDatabase() {
        return database;
    }

    public void loadInstances() {
        this.saveConfig();
        ArenaRegistry.getArenas().clear();
        FileConfiguration config = ConfigurationManager.getConfig("arenas");
        for(String ID : config.getConfigurationSection("instances").getKeys(false)) {
            Arena arena;
            String s = "instances." + ID + ".";
            if(s.contains("default")) continue;

            arena = new Arena(ID);

            if(config.contains(s + "minimumplayers")) arena.setMinimumPlayers(config.getInt(s + "minimumplayers"));
            else arena.setMinimumPlayers(config.getInt("instances.default.minimumplayers"));
            if(config.contains(s + "maximumplayers")) arena.setMaximumPlayers(config.getInt(s + "maximumplayers"));
            else arena.setMaximumPlayers(config.getInt("instances.default.maximumplayers"));
            if(config.contains(s + "mapname")) arena.setMapName(config.getString(s + "mapname"));
            else arena.setMapName(config.getString("instances.default.mapname"));
            if(config.contains(s + "lobbylocation")) arena.setLobbyLocation(Util.getLocation(false, config.getString(s + "lobbylocation")));
            if(config.contains(s + "Startlocation")) arena.setStartLocation(Util.getLocation(false, config.getString(s + "Startlocation")));
            else {
                System.out.print(ID + " doesn't contains an start location!");
                ArenaRegistry.registerArena(arena);
                continue;
            }
            if(config.contains(s + "Endlocation")) arena.setEndLocation(Util.getLocation(false, config.getString(s + "Endlocation")));
            else {
                if(!bungeeActivated) {
                    System.out.print(ID + " doesn't contains an end location!");
                    ArenaRegistry.registerArena(arena);
                    continue;
                }
            }
            if(config.contains(s + "plots")) {
                for(String plotName : config.getConfigurationSection(s + "plots").getKeys(false)) {
                    Plot buildPlot = new Plot();
                    buildPlot.setMaxPoint(Util.getLocation(false, config.getString(s + "plots." + plotName + ".maxpoint")));
                    buildPlot.setMinPoint(Util.getLocation(false, config.getString(s + "plots." + plotName + ".minpoint")));
                    buildPlot.reset();
                    arena.getPlotManager().addBuildPlot(buildPlot);
                }
            } else {
                System.out.print("Instance doesn't contains plots!");
            }
            ArenaRegistry.registerArena(arena);
            arena.start();
        }
    }


    private void loadStatsForPlayersOnline() {
        for(final Player player : getServer().getOnlinePlayers()) {
            if(bungeeActivated) ArenaRegistry.getArenas().get(0).teleportToLobby(player);
            if(!this.isDatabaseActivated()) {
                for(StatsStorage.StatisticType s : StatsStorage.StatisticType.values()) {
                    this.getFileStats().loadStat(player, s.getName());
                }
                return;
            }
            Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
                final String playerName = player.getUniqueId().toString();

                @Override
                public void run() {
                    MySQLDatabase database = getMySQLDatabase();
                    ResultSet resultSet = database.executeQuery("SELECT UUID from buildbattlestats WHERE UUID='" + playerName + "'");
                    try {
                        if(!resultSet.next()) {
                            database.insertPlayer(playerName);
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

    public WorldEditPlugin getWorldEditPlugin() {
        Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if(p instanceof WorldEditPlugin) return (WorldEditPlugin) p;
        return null;
    }

}
