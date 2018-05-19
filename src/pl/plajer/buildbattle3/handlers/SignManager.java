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

package pl.plajer.buildbattle3.handlers;

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
import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.arena.Arena;
import pl.plajer.buildbattle3.arena.ArenaRegistry;
import pl.plajer.buildbattle3.arena.ArenaState;
import pl.plajer.buildbattle3.language.LanguageManager;
import pl.plajer.buildbattle3.utils.Util;

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
        gameStateToString.put(ArenaState.WAITING_FOR_PLAYERS, ChatManager.colorMessage("Signs.Game-States.Inactive"));
        gameStateToString.put(ArenaState.STARTING, ChatManager.colorMessage("Signs.Game-States.Starting"));
        gameStateToString.put(ArenaState.IN_GAME, ChatManager.colorMessage("Signs.Game-States.In-Game"));
        gameStateToString.put(ArenaState.ENDING, ChatManager.colorMessage("Signs.Game-States.Ending"));
        gameStateToString.put(ArenaState.RESTARTING, ChatManager.colorMessage("Signs.Game-States.Restarting"));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        loadSigns();
        updateSignScheduler();
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        if(!e.getPlayer().hasPermission("buildbattle.admin.sign.create")) return;
        if(e.getLine(0).equalsIgnoreCase("[buildbattle]")) {
            if(e.getLine(1).isEmpty()) {
                e.getPlayer().sendMessage(ChatManager.PREFIX + ChatManager.colorMessage("Signs.Please-Type-Arena-Name"));
                return;
            }
            FileConfiguration config = LanguageManager.getLocaleFile();
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
                                    .replaceAll("%state%", ChatManager.colorRawMessage(gameStateToString.get(ArenaState.WAITING_FOR_PLAYERS))));
                        }
                        if(config.getStringList("Signs.Lines").get(i).contains("%playersize%")) {
                            e.setLine(i, config.getStringList("Signs.Lines").get(i)
                                    .replaceAll("%playersize%", String.valueOf(arena.getPlayers().size()))
                                    .replaceAll("%maxplayers%", String.valueOf(arena.getMaximumPlayers())));
                        }
                    }
                    loadedSigns.put((Sign) e.getBlock().getState(), arena);
                    e.getPlayer().sendMessage(ChatManager.PREFIX + ChatManager.colorMessage("Signs.Sign-Created"));
                    String location = e.getBlock().getWorld().getName() + "," + e.getBlock().getX() + "," + e.getBlock().getY() + "," + e.getBlock().getZ() + ",0.0,0.0";
                    FileConfiguration arenas = ConfigurationManager.getConfig("arenas");
                    List<String> locs = arenas.getStringList("instances." + arena.getID() + ".signs");
                    locs.add(location);
                    arenas.set("instances." + arena.getID() + ".signs", locs);
                    ConfigurationManager.saveConfig(arenas, "arenas");
                    return;
                }
            }
            e.getPlayer().sendMessage(ChatManager.PREFIX + ChatManager.colorMessage("Signs.Arena-Doesnt-Exists"));
        }
    }

    @EventHandler
    public void onSignDestroy(BlockBreakEvent e) {
        if(!e.getPlayer().hasPermission("buildbattle.admin.sign.break")) return;
        if(loadedSigns.get(e.getBlock().getState()) == null) return;
        loadedSigns.remove(e.getBlock().getState());
        String location = e.getBlock().getWorld().getName() + "," + e.getBlock().getX() + ".0" + "," + e.getBlock().getY() + ".0" + "," + e.getBlock().getZ() + ".0" + "," + "0.0,0.0";
        FileConfiguration config = ConfigurationManager.getConfig("arenas");
        for(String arena : config.getConfigurationSection("instances").getKeys(false)) {
            for(String sign : config.getStringList("instances." + arena + ".signs")) {
                if(sign.equals(location)) {
                    List<String> signs = config.getStringList("instances." + arena + ".signs");
                    signs.remove(location);
                    config.set("instances." + arena + ".signs", signs);
                    ConfigurationManager.saveConfig(config, "arenas");
                    e.getPlayer().sendMessage(ChatManager.PREFIX + ChatManager.colorMessage("Signs.Sign-Removed"));
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
                        e.getPlayer().sendMessage(ChatManager.PREFIX + ChatManager.colorMessage("In-Game.Messages.Already-Playing"));
                        return;
                    }
                }
                if(arena.getMaximumPlayers() <= arena.getPlayers().size()) {
                    if((e.getPlayer().hasPermission(PermissionManager.getJoinFullGames()))) {
                        boolean b = false;
                        for(Player player : arena.getPlayers()) {
                            if(!player.hasPermission(PermissionManager.getJoinFullGames())) {
                                if((arena.getGameState() == ArenaState.STARTING || arena.getGameState() == ArenaState.WAITING_FOR_PLAYERS)) {
                                    arena.leaveAttempt(player);
                                    player.sendMessage(ChatManager.PREFIX + ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.You-Were-Kicked-For-Premium-Slot"));
                                    String message = ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Kicked-For-Premium-Slot").replace("%PLAYER%", player.getName());
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
                            e.getPlayer().sendMessage(ChatManager.PREFIX + ChatManager.colorMessage("In-Game.No-Slots-For-Premium"));
                        }
                    } else {
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
        FileConfiguration config = ConfigurationManager.getConfig("arenas");
        for(String path : config.getConfigurationSection("instances").getKeys(false)) {
            if(path.equals("default")) continue;
            for(String sign : config.getStringList("instances." + path + ".signs")) {
                Location loc = Util.getLocation(false, sign);
                if(loc.getBlock().getState() instanceof Sign) {
                    loadedSigns.put((Sign) loc.getBlock().getState(), ArenaRegistry.getArena(path));
                } else {
                    Main.debug("Block at loc " + loc + " for arena " + path + " not a sign", System.currentTimeMillis());
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
                FileConfiguration config = LanguageManager.getLocaleFile();
                s.setLine(0, ChatColor.translateAlternateColorCodes('&', config.getStringList("Signs.Lines").get(0)));
                if(arena.getPlayers().size() == arena.getMaximumPlayers()) {
                    s.setLine(1, ChatColor.translateAlternateColorCodes('&', config.getStringList("Signs.Lines").get(1).replaceAll("%state%", ChatManager.colorMessage("Signs.Game-States.Full-Game"))));
                } else {
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
