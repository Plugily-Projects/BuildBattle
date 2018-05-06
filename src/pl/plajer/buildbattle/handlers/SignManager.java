package pl.plajer.buildbattle.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.ArenaState;
import pl.plajer.buildbattle.utils.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Plajer
 * <p>
 * Created at 04.05.2018
 */
public class SignManager implements Listener {

    private Main plugin;
    private Map<Sign, Arena> loadedSigns = new HashMap<>();
    private Map<ArenaState, String> gameStateToString = new HashMap<>();

    public SignManager(Main plugin) {
        this.plugin = plugin;
        FileConfiguration config = ConfigurationManager.getConfig("signModification");
        gameStateToString.put(ArenaState.WAITING_FOR_PLAYERS, config.getString("Signs.Game-States.Inactive"));
        gameStateToString.put(ArenaState.STARTING, config.getString("Signs.Game-States.Starting"));
        gameStateToString.put(ArenaState.IN_GAME, config.getString("Signs.Game-States.In-Game"));
        gameStateToString.put(ArenaState.ENDING, config.getString("Signs.Game-States.Ending"));
        gameStateToString.put(ArenaState.RESTARTING, config.getString("Signs.Game-States.Restarting"));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        loadSigns();
        updateSignScheduler();
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        //todo permission check if(!e.getPlayer().hasPermission("buildbattle.admin.sign.create")) return;
        if(e.getLine(0).equalsIgnoreCase("[buildbattle]")) {
            if(e.getLine(1).isEmpty()) {
                //todo translateee
                e.getPlayer().sendMessage(ChatManager.PREFIX + "Please type arena name!");
                return;
            }
            FileConfiguration config = ConfigurationManager.getConfig("signModification");
            for(Arena arena : ArenaRegistry.getArenas()) {
                if(arena.getID().equalsIgnoreCase(e.getLine(1))) {
                    for(int i = 0; i < config.getStringList("Signs.Lines").size(); i++) {
                        if(i == 1) {
                            //maybe not needed
                            e.setLine(i, ChatColor.translateAlternateColorCodes('&', config.getStringList("Signs.Lines").get(i)
                                    .replaceAll("%mapname%", arena.getMapName())));
                        }
                        if(config.getStringList("Signs.Lines").get(i).contains("%state%")) {
                            e.setLine(i, config.getStringList("Signs.Lines").get(i)
                                    .replaceAll("%state%", ChatManager.colorMessage(gameStateToString.get(ArenaState.WAITING_FOR_PLAYERS))));
                        }
                        if(config.getStringList("Signs.Lines").get(i).contains("%playersize%")) {
                            e.setLine(i, config.getStringList("Signs.Lines").get(i)
                                    .replaceAll("%playersize%", String.valueOf(arena.getPlayers().size()))
                                    .replaceAll("%maxplayers%", String.valueOf(arena.getMaximumPlayers())));
                        }
                    }
                    loadedSigns.put((Sign) e.getBlock().getState(), arena);
                    //todo translateeeeeeeeeee
                    e.getPlayer().sendMessage(ChatManager.PREFIX + ChatManager.colorMessage("&lSign created!"));
                    String location = e.getBlock().getWorld().getName() + "," + e.getBlock().getX() + "," + e.getBlock().getY() + "," + e.getBlock().getZ() + ",0.0,0.0";
                    List<String> locs = plugin.getConfig().getStringList("instances." + arena.getID() + ".signs");
                    locs.add(location);
                    plugin.getConfig().set("instances." + arena.getID() + ".signs", locs);
                    plugin.saveConfig();
                    return;
                }
            }
            //todo translateeeeeeeeeeeeeeeeeee
            e.getPlayer().sendMessage(ChatManager.PREFIX + ChatManager.colorMessage("&lArena doesn't exist!"));
        }
    }

    @EventHandler
    public void onSignDestroy(BlockBreakEvent e) {
        if(!e.getPlayer().hasPermission("buildbattle.admin.sign.break")) return;
        if(loadedSigns.get(e.getBlock().getState()) == null) return;
        loadedSigns.remove(e.getBlock().getState());
        String location = e.getBlock().getWorld().getName() + "," + e.getBlock().getX() + ".0" + "," + e.getBlock().getY() + ".0" + "," + e.getBlock().getZ() + ".0" + "," + "0.0,0.0";
        for(String arena : plugin.getConfig().getConfigurationSection("instances").getKeys(false)) {
            for(String sign : plugin.getConfig().getStringList("instances." + arena + ".signs")) {
                if(sign.equals(location)) {
                    List<String> signs = plugin.getConfig().getStringList("instances." + arena + ".signs");
                    signs.remove(location);
                    plugin.getConfig().set("instances." + arena + ".signs", signs);
                    plugin.saveConfig();
                    //todo translat
                    e.getPlayer().sendMessage(ChatManager.PREFIX + "REMOVEDD");//ChatManager.colorMessage("Signs.Sign-Removed"));
                    return;
                }
            }
        }
        e.getPlayer().sendMessage(ChatManager.PREFIX + "" + ChatColor.RED + "Couldn't remove sign from configuration! Please do this manually!");
    }

    @EventHandler
    public void onJoinAttempt(PlayerInteractEvent e) {
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK &&
                e.getClickedBlock().getState() instanceof Sign && loadedSigns.containsKey(e.getClickedBlock().getState())) {

            Arena arena = loadedSigns.get(e.getClickedBlock().getState());
            if(arena != null) {
                for(Arena loopArena : ArenaRegistry.getArenas()) {
                    if(loopArena.getPlayers().contains(e.getPlayer())) {
                        //todo translateeeeeeeeeeeeeeeeeeee
                        e.getPlayer().sendMessage(ChatManager.PREFIX + ChatManager.colorMessage("&lYou're already playing"));
                        return;
                    }
                }
                if(arena.getMaximumPlayers() <= arena.getPlayers().size()) {
                    if((e.getPlayer().hasPermission(PermissionManager.getVip()) || e.getPlayer().hasPermission(PermissionManager.getJoinFullGames()))) {
                        boolean b = false;
                        for(Player player : arena.getPlayers()) {
                            if(!player.hasPermission(PermissionManager.getVip()) || !player.hasPermission(PermissionManager.getJoinFullGames())) {
                                if((arena.getGameState() == ArenaState.STARTING || arena.getGameState() == ArenaState.WAITING_FOR_PLAYERS)) {
                                    arena.leaveAttempt(player);
                                    //todo translateeeeeeeeeeeeeeeeeeeeeeeeeee
                                    player.sendMessage(ChatManager.PREFIX + ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.You-Were-Kicked-For-Premium-Slot"));
                                    String message = ChatManager.formatMessage(arena, ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Kicked-For-Premium-Slot"), player);
                                    for(Player p : arena.getPlayers()) {
                                        p.sendMessage(ChatManager.PREFIX + message);
                                    }
                                    arena.joinAttempt(e.getPlayer());
                                    return;
                                } else {
                                    arena.joinAttempt(e.getPlayer());
                                    return;
                                }
                            }
                        }
                        if(!b) {
                            //todo transltateeraadaxreaercar
                            e.getPlayer().sendMessage(ChatManager.PREFIX + ChatManager.colorMessage("In-Game.No-Slots-For-Premium"));
                        }
                    } else {
                        //todo afsasdh vhagsd
                        e.getPlayer().sendMessage(ChatManager.PREFIX + ChatManager.colorMessage("In-Game.Full-Game-No-Permission"));
                    }
                } else {
                    arena.joinAttempt(e.getPlayer());
                }
            }
        }
    }

    public Map<Sign, Arena> getLoadedSigns() {
        return loadedSigns;
    }

    public void loadSigns() {
        loadedSigns.clear();
        for(String path : plugin.getConfig().getConfigurationSection("instances").getKeys(false)) {
            if(path.equals("default")) continue;
            for(String sign : plugin.getConfig().getStringList("instances." + path + ".signs")) {
                Location loc = Util.getLocation(false, sign);
                if(loc.getBlock().getState() instanceof Sign) {
                    loadedSigns.put((Sign) loc.getBlock().getState(), ArenaRegistry.getArena(path));
                } else {
                    //todo DEBUGGGG Main.debug("Block at loc " + loc + " for arena " + path + " not a sign", System.currentTimeMillis());
                }
            }
        }
    }

    private void updateSignScheduler() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for(Sign s : loadedSigns.keySet()) {
                Arena arena = loadedSigns.get(s);
                ArenaState arenaState;
                if(arena == null) {
                    arenaState = ArenaState.WAITING_FOR_PLAYERS;
                } else {
                    arenaState = arena.getGameState();
                }
                FileConfiguration config = ConfigurationManager.getConfig("signModification");
                s.setLine(0, ChatColor.translateAlternateColorCodes('&', config.getStringList("Signs.Lines").get(0)));
                if(arena.getPlayers().size() == arena.getMaximumPlayers()) {
                    //todo translargvuatetaeate
                    s.setLine(1, ChatColor.translateAlternateColorCodes('&', config.getStringList("Signs.Lines").get(1).replaceAll("%state%", ChatManager.colorMessage("Signs.Game-States.Full-Game"))));
                } else {
                    //todo oooooooooooooooo
                    s.setLine(1, ChatColor.translateAlternateColorCodes('&', config.getStringList("Signs.Lines").get(1).replaceAll("%state%", gameStateToString.get(arenaState))));
                }
                s.setLine(2, ChatColor.translateAlternateColorCodes('&', config.getStringList("Signs.Lines").get(2).replaceAll("%mapname%", arena.getMapName())));
                s.setLine(3, ChatColor.translateAlternateColorCodes('&', config.getStringList("Signs.Lines").get(3)
                        .replaceAll("%maxplayers%", String.valueOf(arena.getMaximumPlayers()))
                        .replaceAll("%playersize%", String.valueOf(arena.getPlayers().size()))));
                s.update();
            }
        }, 10, 10);
    }
}
