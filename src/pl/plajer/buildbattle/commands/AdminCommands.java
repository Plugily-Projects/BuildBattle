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

import java.util.List;

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
            ChatManager.broadcastMessage("Admin-ForceStart-Game", ChatManager.HIGHLIGHTED + "An admin forcestarted the game!", arena);
        } else if(arena.getGameState() == ArenaState.STARTING) {
            arena.setTimer(0);
            ChatManager.broadcastMessage("Admin-Set-Starting-In-To-0", ChatManager.HIGHLIGHTED + "An admin set waiting time to 0. Game starts now!", arena);
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
            Location loc = player.getTargetBlock(null, 10).getLocation();
            if(loc.getBlock().getState() instanceof Sign) {
                List<String> signs = plugin.getConfig().getStringList("instances." + arena.getID() + ".signs");
                signs.add(loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch());
                plugin.getConfig().set("instances." + arena.getID() + ".signs", signs);
                plugin.saveConfig();
                plugin.getSignManager().getLoadedSigns().put((Sign) loc.getBlock().getState(), arena);
                player.sendMessage(ChatColor.GREEN + "SIGN ADDED!");
            } else {
                player.sendMessage(ChatColor.RED + "You have to look at a sign to perform this command!");
            }

        }
    }

}
