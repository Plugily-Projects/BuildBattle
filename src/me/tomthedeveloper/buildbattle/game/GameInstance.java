package me.tomthedeveloper.buildbattle.game;

import me.tomthedeveloper.buildbattle.User;
import me.tomthedeveloper.buildbattle.GameAPI;
import me.tomthedeveloper.buildbattle.handlers.ChatManager;
import me.tomthedeveloper.buildbattle.handlers.ConfigurationManager;
import me.tomthedeveloper.buildbattle.handlers.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

//import me.confuser.barapi.BarAPI;

/**
 * Created by Tom on 27/07/2014.
 */
public abstract class GameInstance extends BukkitRunnable {

    public static GameAPI plugin;
    protected HashMap<GameState, String[]> signlines = new HashMap<GameState, String[]>();
    protected String[] FULLlines;
    private HashSet<Location> signs = new HashSet<Location>();
    private GameState gameState;
    private int MIN_PLAYERS = 2;
    private int MAX_PLAYERS = 10;
    private String mapname = "";
    private int timer;
    private String schematicName;
    private String ID = null;
    private InstanceType type;

    private Location lobbyloc = null;
    private Location Startloc = null;
    private Location Endloc = null;

    private HashSet<UUID> players;

    private ChatManager chatManager;

    private UUID firstPlace = null;
    private UUID secondPlace = null;
    private UUID thirdPlace = null;

    protected GameInstance(String ID) {
        gameState = GameState.WAITING_FOR_PLAYERS;
        chatManager = new ChatManager(this);

        this.ID = ID;
        players = new HashSet<UUID>();
        loadSignLines();

    }

    public static GameAPI getPlugin() {
        return plugin;
    }

    public InstanceType getType() {
        return type;
    }

    public void setType(InstanceType type) {
        this.type = type;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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
        if(players.contains(player.getUniqueId())) players.remove(player.getUniqueId());
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

    public String getSchematicName() {
        return schematicName;
    }

    public void setSchematicName(String schematicName) {
        this.schematicName = schematicName;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public abstract boolean needsPlayers();

    public int getMAX_PLAYERS() {
        return MAX_PLAYERS;
    }

    public void setMAX_PLAYERS(int MAX_PLAYERS) {
        this.MAX_PLAYERS = MAX_PLAYERS;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
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
        /*ChatColor DARK_GREEN = ChatColor.DARK_GREEN;
        ChatColor DARK_PURPLE = ChatColor.DARK_PURPLE;
        ChatColor GRAY = ChatColor.GRAY;
        ChatColor DARK_RED = ChatColor.DARK_RED;
        switch (getGameState()){
            case WAITING_FOR_PLAYERS:
                if(getPlayers().size() < getMAX_PLAYERS())
                    sign.setLine(0, DARK_GREEN + "[Join]");
                else
                    sign.setLine(0, DARK_RED + "[Full]");
                sign.setLine(1, mapname );
                sign.setLine(2, getID());
                sign.setLine(3, DARK_PURPLE + "[" + getPlayers().size() + "/" + getMAX_PLAYERS() + "]");
                break;
            case STARTING:
                    if(getPlayers().size() < getMAX_PLAYERS())
                        sign.setLine(0, DARK_GREEN + "[Join]");
                    else
                        sign.setLine(0, DARK_RED + "[Full]");
                sign.setLine(1, mapname );
                sign.setLine(2,  getID());
                sign.setLine(3, DARK_PURPLE + "[" + getPlayers().size() + "/" + getMAX_PLAYERS() + "]");
                sign.update(true);
                break;
            case INGAME:
                sign.setLine(0, DARK_RED + "[Ingame]");
                sign.setLine(1, mapname );
                sign.setLine(2, getID());
                sign.setLine(3, DARK_PURPLE + "[" + getPlayers().size() + "/" + getMAX_PLAYERS() + "]");
                break;
            case ENDING:
                sign.setLine(1, DARK_RED + "ENDING");
                break;
            case RESTARTING:
                sign.setLine(1, DARK_RED + "RESTARTING!");
                break;
            case PHASE_1:
                break;
            case PHASE_2:
                break;
            case PHASE_3:
                break;
            case PHASE_4:
                break;
            case PHASE_5:
                break;
            case PHASE_6:
                break;
            default:
                break;
        }
        sign.update(true); */

        String[] strings = signlines.get(getGameState());
        if(getGameState() == GameState.STARTING || getGameState() == GameState.WAITING_FOR_PLAYERS) {
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

    private String[] formatText(String[] string) {
        String[] returnstring = string;
        returnstring[0] = formatText(string[0]);
        returnstring[1] = formatText(string[1]);
        returnstring[2] = formatText(string[2]);
        returnstring[3] = formatText(string[3]);
        return returnstring;

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

    public void joinAttempt(Player p) {
        if((getGameState() == GameState.INGAME || (getGameState() == GameState.STARTING && getTimer() <= 3) || getGameState() == GameState.ENDING)) {
            this.teleportToStartLocation(p);

            p.getInventory().clear();

            this.addPlayer(p);
            p.setHealth(20.0);
            p.setFoodLevel(20);
            p.setGameMode(GameMode.SURVIVAL);
            p.setAllowFlight(true);
            p.setFlying(true);
            User user = UserManager.getUser(p.getUniqueId());
            user.setSpectator(true);
            this.hidePlayer(p);
            if(plugin.getPlugin().isInventoryManagerEnabled()) {
                plugin.getPlugin().getInventoryManager().saveInventoryToFile(p);
            }


            for(Player spectator : plugin.getGameInstanceManager().getGameInstances().get(0).getPlayers()) {
                if(UserManager.getUser(spectator.getUniqueId()).isSpectator()) {
                    p.hidePlayer(spectator);
                    spectator.hidePlayer(p);
                }
            }
            return;
        }
        teleportToLobby(p);
        this.addPlayer(p);
        p.setHealth(20.0);
        p.setFoodLevel(20);
        p.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
        p.getInventory().clear();
        p.setGameMode(GameMode.SURVIVAL);
        p.setFlying(false);
        p.setAllowFlight(false);
        showPlayers();
        if(!UserManager.getUser(p.getUniqueId()).isSpectator()) getChatManager().broadcastJoinMessage(p);
        p.updateInventory();
        for(Player player : getPlayers()) {
            showPlayer(player);
        }
        showPlayers();
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
        HashSet<Player> list = new HashSet<Player>();
        for(UUID uuid : players) {
            list.add((Player) Bukkit.getPlayer(uuid));
        }

        return list;
    }

    public void leaveAttempt(Player p) {

        User user = UserManager.getUser(p.getUniqueId());

        this.teleportToEndLocation(p);
        this.removePlayer(p);
        if(!user.isSpectator()) {
            getChatManager().broadcastLeaveMessage(p);
        }
        user.setSpectator(false);
        user.removeScoreboard();
        // if(plugin.isBarEnabled())
        //BossbarAPI.removeBar(p);

        p.setMaxHealth(20.0);
        p.setFoodLevel(20);
        p.setFlying(false);
        p.setAllowFlight(false);
        p.getInventory().clear();
        for(PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }
        p.setFireTicks(0);
        if(getPlayers().size() == 0) {
            this.setGameState(GameState.RESTARTING);
        }
       /* if(!plugin.isBungeeActivated()) {
            plugin.getInventoryManager().loadInventory(p);

        } */
        if(plugin.getPlugin().isInventoryManagerEnabled()) {
            plugin.getPlugin().getInventoryManager().loadInventory(p);
        }
        p.setGameMode(GameMode.SURVIVAL);


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

    public void teleportAllPlayersToLobby() {
        if(lobbyloc != null) {
            for(Player p : getPlayers()) {
                p.teleport(lobbyloc);
            }
        } else {
            System.out.print("LobbyLocation for arena " + getID() + " isn't intialized!");
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

    public void teleportAllToStartLocation() {
        for(Player player : getPlayers()) {
            if(Startloc != null) player.teleport(Startloc);
            else System.out.print("Startlocation for arena " + getID() + " isn't intialized!");
        }
    }

    public void teleportAllToEndLocation() {
        if(plugin.isBungeeActivated()) {
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
        if(plugin.isBungeeActivated()) {
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

    public void removeAllPlayers() {
        players.clear();
    }


    public void loadSignLines() {
        FileConfiguration config = ConfigurationManager.getConfig("signModification");
        if(!config.contains("signs.format.WAITING_FOR_PLAYERS")) {
            config.set("signs.format.WAITING_FOR_PLAYERS.lines.1", ChatColor.DARK_GREEN + "[Join]");
            config.set("signs.format.WAITING_FOR_PLAYERS.lines.2", "%MAPNAME%");
            config.set("signs.format.WAITING_FOR_PLAYERS.lines.3", "%ARENA%");
            config.set("signs.format.WAITING_FOR_PLAYERS.lines.4", ChatColor.DARK_PURPLE + "[%PLAYERSIZE%/%MAXPLAYERS%]");
            try {
                config.save(ConfigurationManager.getFile("signModification"));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        if(!config.contains("signs.format.STARTING")) {
            config.set("signs.format.STARTING.lines.1", ChatColor.DARK_GREEN + "[Starting]");
            config.set("signs.format.STARTING.lines.2", "%MAPNAME%");
            config.set("signs.format.STARTING.lines.3", "%ARENA%");
            config.set("signs.format.STARTING.lines.4", ChatColor.DARK_PURPLE + "[%PLAYERSIZE%/%MAXPLAYERS%]");
            try {
                config.save(ConfigurationManager.getFile("signModification"));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        if(!config.contains("signs.format.FULL")) {
            config.set("signs.format.FULL.lines.1", ChatColor.DARK_RED + "[Full]");
            config.set("signs.format.FULL.lines.2", "%MAPNAME%");
            config.set("signs.format.FULL.lines.3", "%ARENA%");
            config.set("signs.format.FULL.lines.4", ChatColor.DARK_PURPLE + "[%PLAYERSIZE%/%MAXPLAYERS%]");
            try {
                config.save(ConfigurationManager.getFile("signModification"));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        if(!config.contains("signs.format.INGAME")) {
            config.set("signs.format.INGAME.lines.1", ChatColor.RED + "[Ingame]");
            config.set("signs.format.INGAME.lines.2", "%MAPNAME%");
            config.set("signs.format.INGAME.lines.3", "%ARENA%");
            config.set("signs.format.INGAME.lines.4", ChatColor.DARK_PURPLE + "[%PLAYERSIZE%/%MAXPLAYERS%]");
            try {
                config.save(ConfigurationManager.getFile("signModification"));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        if(!config.contains("signs.format.ENDING")) {
            config.set("signs.format.ENDING.lines.1", ChatColor.RED + "----------");
            config.set("signs.format.ENDING.lines.2", ChatColor.RED + "--ENDING--");
            config.set("signs.format.ENDING.lines.3", ChatColor.RED + "----------");
            config.set("signs.format.ENDING.lines.4", ChatColor.RED + "----------");
            try {
                config.save(ConfigurationManager.getFile("signModification"));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        if(!config.contains("signs.format.RESTARTING")) {
            config.set("signs.format.RESTARTING.lines.1", ChatColor.RED + "----------");
            config.set("signs.format.RESTARTING.lines.2", ChatColor.RED + "RESTARTING");
            config.set("signs.format.RESTARTING.lines.3", ChatColor.RED + "----------");
            config.set("signs.format.RESTARTING.lines.4", ChatColor.RED + "----------");
            try {
                config.save(ConfigurationManager.getFile("signModification"));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        for(String s : config.getConfigurationSection("signs.format").getKeys(false)) {
            if(s.equalsIgnoreCase("WaitingForNewGame") || s.equalsIgnoreCase("FULL")) continue;

            String path = "signs.format." + s + ".";

            signlines.put(GameState.fromString(s), new String[]{

                    config.getString(path + "lines.1"), config.getString(path + "lines.2"), config.getString(path + "lines.3"), config.getString(path + "lines.4")});

            FULLlines = new String[]{config.getString(path + "lines.1"), config.getString(path + "lines.2"), config.getString(path + "lines.3"), config.getString(path + "lines.4")};

        }


    }


    public Player getFirstPlace() {
        if(firstPlace == null) return null;
        return Bukkit.getPlayer(firstPlace);
    }

    public void setFirstPlace(Player player) {
        firstPlace = player.getUniqueId();
    }

    public Player getSecondPlace() {
        if(secondPlace == null) return null;
        return Bukkit.getPlayer(secondPlace);
    }

    public void setSecondPlace(Player player) {
        secondPlace = player.getUniqueId();
    }

    public Player getThirdPlace() {
        if(thirdPlace == null) return null;
        return Bukkit.getPlayer(thirdPlace);
    }

    public void setThirdPlace(Player player) {
        thirdPlace = player.getUniqueId();
    }

    public void resetPlaces() {
        firstPlace = null;
        secondPlace = null;
        thirdPlace = null;
    }


}



