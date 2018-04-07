package me.tomthedeveloper.buildbattle;

import me.tomthedeveloper.buildbattle.handlers.ConfigurationManager;
import me.tomthedeveloper.buildbattle.game.BuildInstance;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Tom on 17/08/2015.
 */
public class ConfigPreferences {

    private static FileConfiguration config = ConfigurationManager.getConfig("config");
    private static HashMap<String, Integer> options = new HashMap<>();
    private static Main main;
    private static List<String> winCommands = new ArrayList<>();
    private static List<String> endGameCommands = new ArrayList<>();
    private static List<String> secondPlaceCommands = new ArrayList<>();
    private static List<String> whitelistedCommands = new ArrayList<>();
    private static List<String> thirdPlaceCommands = new ArrayList<>();

    public ConfigPreferences(Main main) {
        config = main.getConfig();
        ConfigPreferences.main = main;
    }


    public static void loadThemes() {
        for(String theme : config.getStringList("themes")) {
            BuildInstance.addTheme(theme);
        }

    }

    public static void loadWinCommands() {
        winCommands.addAll(config.getStringList("Win-Commands"));

    }

    public static void loadWhitelistedCommands() {
        whitelistedCommands.addAll(config.getStringList("Whitelisted-Commands"));

    }

    public static void loadThirdPlaceCommands() {
        thirdPlaceCommands.addAll(config.getStringList("Third-Place-Commands"));

    }

    public static void loadSecondPlaceCommands() {
        secondPlaceCommands.addAll(config.getStringList("Second-Place-Commands"));

    }

    public static List<String> getSecondPlaceCommands() {
        return secondPlaceCommands;
    }

    public static List<String> getThirdPlaceCommands() {
        return thirdPlaceCommands;
    }

    public static List<String> getWinCommands() {
        return winCommands;
    }

    public static List<String> getEndGameCommands() {
        return endGameCommands;
    }


    public static void loadEndGameCommands() {
        if(!config.contains("End-Game-Commands")) {
            config.set("End-Game-Commands", Arrays.asList("say %PLAYER% has played a game!", "give %PLAYER% 100"));
            saveConfig();
        }
        endGameCommands.addAll(config.getStringList("End-Game-Commands"));
    }

    public static boolean isMobSpawningDisabled() {
        return options.get("Disable-Mob-Spawning-Completely") == 1;
    }

    public static boolean isDynamicSignSystemEnabled() {
        return options.get("Dynamic-Sign-System") == 1;
    }

    public static int getDefaultFloorMaterial() {
        return options.get("Default-Floor-Material");
    }

    public static int getLobbyTimer() {
        return options.get("Lobby-Starting-Time");
    }

    public static void loadBlackList() {
        for(int ID : config.getIntegerList("blacklist")) {
            BuildInstance.addToBlackList(ID);
        }
    }

    public static boolean isBarEnabled() {
        return options.get("bar") == 1;
    }

    public static List<String> getWhitelistedCommands() {
        return whitelistedCommands;
    }

    public static boolean isHidePlayersOutsideGameEnabled() {
        return options.get("Hide-Players-Outside-Game") == 1;
    }

    public static int getAmountFromOneParticle() {
        return options.get("Amount-One-Particle-Effect-Contains");
    }

    public static int getMaxParticles() {
        return options.get("Max-Amount-Particles");
    }


    public static boolean restartOnEnd() {
        return options.get("Bungee-Restart-On-End") == 1;
    }

    public static int getVotingTime() {
        return options.get("Voting-Time-In-Seconds");
    }

    public static boolean isScoreboardDisabled() {
        return options.get("Disable-Scoreboard-Ingame") == 1;
    }

    public static int getBuildTime() {
        return options.get("Build-Time-In-Seconds");
    }

    public static boolean getBungeeShutdown() {
        return options.get("Bungee-Shutdown-On-End") == 1;
    }

    public static int getParticlOffset() {
        return options.get("Particle-Offset");
    }

    public static int getMaxMobs() {
        return options.get("Mobs-Max-Amount-Per-Plot");
    }

    public static boolean isWinCommandsEnabled() {
        return options.get("Win-Commands-Activated") == 1;
    }

    public static boolean isNameUsedInDatabase() {return options.get("Use-Name-Instead-Of-UUID-In-Database") == 1;}

    public static boolean isSecondPlaceCommandsEnabled() {
        return options.get("Second-Place-Commands-Activated") == 1;
    }

    public static boolean isThirdPlaceCommandsEnabled() {
        return options.get("Third-Place-Commands-Activated") == 1;
    }

    public static boolean isEndGameCommandsEnabled() {
        return options.get("End-Game-Commands-Activated") == 1;
    }

    public static void loadOptions() {
        List<String> loadOptions = new ArrayList<>();
        loadOptions.add("Build-Time-In-Seconds");
        loadOptions.add("Voting-Time-In-Seconds");
        loadOptions.add("bar");
        loadOptions.add("Fly-Range-Out-Plot");
        loadOptions.add("Default-Floor-Material");
        loadOptions.add("Disable-Mob-Spawning-Completely");
        loadOptions.add("Dynamic-Sign-System");
        loadOptions.add("Amount-One-Particle-Effect-Contains");
        loadOptions.add("Max-Amount-Particles");
        loadOptions.add("Particle-Refresh-Per-Tick");
        loadOptions.add("Bungee-Shutdown-On-End");
        loadOptions.add("Bungee-Restart-On-End");
        loadOptions.add("Particle-Offset");
        loadOptions.add("Win-Commands-Activated");
        loadOptions.add("End-Game-Commands-Activated");
        loadOptions.add("Second-Place-Commands-Activated");
        loadOptions.add("Third-Place-Commands-Activated");
        loadOptions.add("Use-Name-Instead-Of-UUID-In-Database");
        loadOptions.add("Mobs-Max-Amount-Per-Plot");
        loadOptions.add("Hide-Players-Outside-Game");
        loadOptions.add("Disable-Scoreboard-Ingame");
        loadOptions.add("Hook-Into-Vault");
        loadOptions.add("Lobby-Starting-Time");

        for(String option : loadOptions) {
            if(config.contains(option)) {
                if(config.isBoolean(option)) {
                    boolean b = config.getBoolean(option);
                    if(b) {
                        options.put(option, 1);
                    } else {
                        options.put(option, 0);
                    }
                } else {
                    options.put(option, config.getInt(option));
                }
            } else {
                if(option.equalsIgnoreCase("Build-Time-In-Seconds")) config.set("Build-Time-In-Seconds", 60 * 8);
                if(option.equals("Voting-Time-In-Seconds")) config.set("Voting-Time-In-Seconds", 20);
                if(option.equals("bar")) config.set("bar", true);
                if(option.equals("Fly-Range-Out-Plot")) config.set("Fly-Range-Out-Plot", 5);
                if(option.equals("Default-Floor-Material")) config.set("Default-Floor-Material", 17);
                if(option.equals("Disable-Mob-Spawning-Completely")) config.set("Disable-Mob-Spawning-Completely", true);
                if(option.equals("Dynamic-Sign-System")) config.set("Dynamic-Sign-System", true);
                if(option.equals("Amount-One-Particle-Effect-Contains")) config.set("Amount-One-Particle-Effect-Contains", 20);
                if(option.equals("Max-Amount-Particles")) config.set("Max-Amount-Particles", 25);
                if(option.equals("Particle-Refresh-Per-Tick")) config.set("Particle-Refresh-Per-Tick", 10);
                if(option.equals("Bungee-Shutdown-On-End")) config.set("Bungee-Shutdown-On-End", false);
                if(option.equals("Particle-Offset")) config.set("Particle-Offset", 1);
                if(option.equals("Win-Commands-Activated")) config.set("Win-Commands-Activated", false);
                if(option.equals("Second-Place-Commands-Activated")) config.set("Second-Place-Commands-Activated", false);
                if(option.equals("Third-Place-Commands-Activated")) config.set("Third-Place-Commands-Activated", false);
                if(option.equals("End-Game-Commands-Activated")) config.set("End-Game-Commands-Activated", true);
                if(option.equals("Bungee-Restart-On-End")) config.set("Bungee-Restart-On-End", false);
                if(option.equals("Use-Name-Instead-Of-UUID-In-Database")) config.set("Use-Name-Instead-Of-UUID-In-Database", false);
                if(option.equals("Mobs-Max-Amount-Per-Plot")) config.set("Mobs-Max-Amount-Per-Plot", 20);
                if(option.equals("Hide-Players-Outside-Game")) config.set("Hide-Players-Outside-Game", true);
                if(option.equals("Disable-Scoreboard-Ingame")) config.set("Disable-Scoreboard-Ingame", false);
                if(option.equals("Hook-Into-Vault")) config.set("Hook-Into-Vault", false);
                if(option.equals("Lobby-Starting-Time")) config.set("Lobby-Starting-Time", 60);
                saveConfig();
            }
            saveConfig();
        }
    }

    public static boolean isVaultEnabled() {
        return options.get("Hook-Into-Vault") == 1;
    }

    public static long getParticleRefreshTick() {
        return options.get("Particle-Refresh-Per-Tick");
    }

    public static int getExtraPlotRange() {
        return options.get("Fly-Range-Out-Plot");
    }

    private static void saveConfig() {
        main.saveConfig();
    }

}
