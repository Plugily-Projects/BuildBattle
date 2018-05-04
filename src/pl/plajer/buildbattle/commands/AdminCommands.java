package pl.plajer.buildbattle.commands;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import pl.plajer.buildbattle.ConfigPreferences;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.ArenaState;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.utils.Util;

/**
 * @author Plajer
 * <p>
 * Created at 03.05.2018
 */
public class AdminCommands extends GameCommands {

    private Main plugin;

    public AdminCommands(Main plugin) {
        this.plugin = plugin;
    }

    public void addPlot(Player player, String arena) {
        if(ArenaRegistry.getArena(arena) == null) {
            player.sendMessage(ChatColor.RED + "This arena doesn't exist!");
            return;
        }
        Selection selection = plugin.getWorldEditPlugin().getSelection(player);
        if(selection instanceof CuboidSelection) {
            if(plugin.getConfig().contains("instances." + arena + ".plots")) {
                Util.saveLoc("instances." + arena + ".plots." + (plugin.getConfig().getConfigurationSection("instances." + arena + ".plots").getKeys(false).size() + 1) + ".minpoint", selection.getMinimumPoint());
                Util.saveLoc("instances." + arena + ".plots." + (plugin.getConfig().getConfigurationSection("instances." + arena + ".plots").getKeys(false).size()) + ".maxpoint", selection.getMaximumPoint());
            } else {
                Util.saveLoc("instances." + arena + ".plots.0.minpoint", selection.getMinimumPoint());
                Util.saveLoc("instances." + arena + ".plots.0.maxpoint", selection.getMaximumPoint());
            }
            plugin.saveConfig();
            player.sendMessage(ChatColor.GREEN + "Plot added to instance " + ChatColor.RED + arena);
        } else {
            player.sendMessage(ChatColor.RED + "You don't have the right selection!");
        }
    }

    public void forceStart(Player player) {
        Arena arena = ArenaRegistry.getArena(player);
        if(arena == null) return;
        if(arena.getGameState() == ArenaState.WAITING_FOR_PLAYERS) {
            arena.setGameState(ArenaState.STARTING);
            arena.getChatManager().broadcastMessage("Admin-ForceStart-Game", ChatManager.HIGHLIGHTED + "An admin forcestarted the game!");
        } else if(arena.getGameState() == ArenaState.STARTING) {
            arena.setTimer(0);
            arena.getChatManager().broadcastMessage("Admin-Set-Starting-In-To-0", ChatManager.HIGHLIGHTED + "An admin set waiting time to 0. Game starts now!");
        }
    }

    public void reloadPlugin(Player player) {
        ConfigPreferences.loadOptions();
        ConfigPreferences.loadOptions();
        ConfigPreferences.loadThemes();
        ConfigPreferences.loadBlackList();
        ConfigPreferences.loadWinCommands();
        ConfigPreferences.loadSecondPlaceCommands();
        ConfigPreferences.loadThirdPlaceCommands();
        ConfigPreferences.loadEndGameCommands();
        ConfigPreferences.loadWhitelistedCommands();
        plugin.loadInstances();
        player.sendMessage(ChatColor.GREEN + "Plugin reloaded!");
    }

    public void addSign(Player player, String arenaName) {
        Arena arena = ArenaRegistry.getArena(arenaName);
        if(arena == null) {
            player.sendMessage(ChatColor.RED + "Arena doesn't exist!");
        } else {
            Location location = player.getTargetBlock(null, 10).getLocation();
            if(location.getBlock().getState() instanceof Sign) {
                int keys = 0;
                if(plugin.getConfig().contains("signs." + arena.getID())) {
                    keys = plugin.getConfig().getConfigurationSection("signs." + arena.getID()).getKeys(false).size();
                }
                Util.saveLoc("newsigns." + arena.getID() + "." + (keys + 1), player.getTargetBlock(null, 10).getLocation());
                player.sendMessage(ChatColor.GREEN + "SIGN ADDED!");
                arena.addSign(player.getTargetBlock(null, 10).getLocation());
            } else {
                player.sendMessage(ChatColor.RED + "You have to look at a sign to perform this command!");
            }

        }
    }

}
