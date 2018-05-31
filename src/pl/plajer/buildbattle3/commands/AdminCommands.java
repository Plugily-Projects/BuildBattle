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

package pl.plajer.buildbattle3.commands;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import pl.plajer.buildbattle3.ConfigPreferences;
import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.arena.Arena;
import pl.plajer.buildbattle3.arena.ArenaManager;
import pl.plajer.buildbattle3.arena.ArenaRegistry;
import pl.plajer.buildbattle3.arena.ArenaState;
import pl.plajer.buildbattle3.handlers.ChatManager;
import pl.plajer.buildbattle3.handlers.ConfigurationManager;
import pl.plajer.buildbattle3.utils.Util;

import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 03.05.2018
 */
public class AdminCommands extends MainCommand {

    private Main plugin;

    public AdminCommands(Main plugin) {
        this.plugin = plugin;
    }

    public void sendHelp(CommandSender sender) {
        if(!sender.hasPermission("buildbattle.admin")) return;
        sender.sendMessage(ChatColor.AQUA + "  " + ChatColor.BOLD + "BuildBattle " + ChatColor.GRAY + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.RED + " []" + ChatColor.GRAY + " = optional  " + ChatColor.GOLD + "<>" + ChatColor.GRAY + " = required");
        sender.sendMessage(ChatColor.AQUA + "/bb create " + ChatColor.GOLD + "<arena>" + ChatColor.GRAY + ": Create an arena!");
        sender.sendMessage(ChatColor.AQUA + "/bb " + ChatColor.GOLD + "<arena> " + ChatColor.AQUA + "edit" + ChatColor.GRAY + ": Opens the menu to edit the arena!");
        sender.sendMessage(ChatColor.AQUA + "/bba addplot" + ChatColor.GOLD + " <arena>" + ChatColor.GRAY + ": Adds a plot to the arena");
        sender.sendMessage(ChatColor.AQUA + "/bba addnpc" + ChatColor.GRAY + ": Adds new floor change NPC (Citizens required)");
        sender.sendMessage(ChatColor.AQUA + "/bba forcestart" + ChatColor.GRAY + ": Force starts the arena you are in");
        sender.sendMessage(ChatColor.AQUA + "/bba reload" + ChatColor.GRAY + ": Reloads plugin");
    }

    public void addPlot(Player player, String arena) {
        if(!hasPermission(player, "buildbattle.admin.addplot")) return;
        if(ArenaRegistry.getArena(arena) == null) {
            player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
            return;
        }
        Selection selection = plugin.getWorldEditPlugin().getSelection(player);
        if(selection instanceof CuboidSelection) {
            FileConfiguration config = ConfigurationManager.getConfig("arenas");
            if(config.contains("instances." + arena + ".plots")) {
                Util.saveLocation("instances." + arena + ".plots." + (config.getConfigurationSection("instances." + arena + ".plots").getKeys(false).size()) + ".minpoint", selection.getMinimumPoint());
                Util.saveLocation("instances." + arena + ".plots." + (config.getConfigurationSection("instances." + arena + ".plots").getKeys(false).size()) + ".maxpoint", selection.getMaximumPoint());
            } else {
                Util.saveLocation("instances." + arena + ".plots.0.minpoint", selection.getMinimumPoint());
                Util.saveLocation("instances." + arena + ".plots.0.maxpoint", selection.getMaximumPoint());
            }
            player.sendMessage(ChatColor.GREEN + "Plot added to instance " + ChatColor.RED + arena);
        } else {
            player.sendMessage(ChatColor.RED + "You don't have the right selection!");
        }
    }

    public void forceStart(Player player) {
        if(!hasPermission(player, "buildbattle.admin.forcestart")) return;
        Arena arena = ArenaRegistry.getArena(player);
        if(arena == null) return;
        if(arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING) {
            arena.setGameState(ArenaState.STARTING);
            arena.setTimer(0);
            for(Player p : arena.getPlayers()) {
                p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Admin-Messages.Set-Starting-In-To-0"));
            }
        }
    }

    public void reloadPlugin(Player player) {
        if(!hasPermission(player, "buildbattle.admin.reload")) return;
        ConfigPreferences.loadOptions();
        ConfigPreferences.loadOptions();
        ConfigPreferences.loadThemes();
        ConfigPreferences.loadBlackList();
        ConfigPreferences.loadWinCommands();
        ConfigPreferences.loadSecondPlaceCommands();
        ConfigPreferences.loadThirdPlaceCommands();
        ConfigPreferences.loadEndGameCommands();
        ConfigPreferences.loadWhitelistedCommands();
        plugin.loadArenas();
        player.sendMessage(ChatColor.GREEN + "Plugin reloaded!");
    }

    public void addSign(Player player, String arenaName) {
        if(!hasPermission(player, "buildbattle.admin.addsign")) return;
        Arena arena = ArenaRegistry.getArena(arenaName);
        if(arena == null) {
            player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
        } else {
            Location loc = player.getTargetBlock(null, 10).getLocation();
            if(loc.getBlock().getState() instanceof Sign) {
                FileConfiguration config = ConfigurationManager.getConfig("arenas");
                List<String> signs = config.getStringList("instances." + arena.getID() + ".signs");
                signs.add(loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch());
                config.set("instances." + arena.getID() + ".signs", signs);
                ConfigurationManager.saveConfig(config, "arenas");
                plugin.getSignManager().getLoadedSigns().put((Sign) loc.getBlock().getState(), arena);
                player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Signs.Sign-Created"));
            } else {
                player.sendMessage(ChatColor.RED + "You have to look at a sign to perform this command!");
            }

        }
    }

    public void stopGame(CommandSender sender) {
        if(checkSenderIsConsole(sender)) return;
        if(!hasPermission(sender, "buildbattle.admin.stopgame")) return;
        Arena a = ArenaRegistry.getArena((Player) sender);
        if(a == null) return;
        ArenaManager.stopGame(false, a);
    }

    public void addNPC(Player player) {
        if(!hasPermission(player, "buildbattle.admin.addnpc")) return;
        if(plugin.getServer().getPluginManager().isPluginEnabled("Citizens")) {
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, ChatManager.colorMessage("In-Game.Floor-Change-NPC-Name"));
            npc.spawn(player.getLocation());
            npc.setProtected(true);
            npc.setName(ChatManager.colorMessage("In-Game.Floor-Change-NPC-Name"));
            player.sendMessage(ChatManager.colorMessage("In-Game.NPC-Created"));
        } else {
            player.sendMessage(ChatManager.colorMessage("In-Game.Install-Citizens"));
        }
    }

    public void printList(CommandSender sender) {
        if(!hasPermission(sender, "buildbattle.admin.list")) return;
        sender.sendMessage(ChatManager.colorMessage("Commands.Admin-Commands.List-Command.Header"));
        int i = 0;
        for(Arena arena : ArenaRegistry.getArenas()) {
            sender.sendMessage(ChatManager.colorMessage("Commands.Admin-Commands.List-Command.Format").replaceAll("%arena%", arena.getID())
                    .replaceAll("%status%", arena.getArenaState().getFormattedName()).replaceAll("%players%", String.valueOf(arena.getPlayers().size()))
                    .replaceAll("%maxplayers%", String.valueOf(arena.getMaximumPlayers())));
            i++;
        }
        if(i == 0) sender.sendMessage(ChatManager.colorMessage("Commands.Admin-Commands.List-Command.No-Arenas"));
    }

    public void deleteArena(CommandSender sender, String arenaString) {
        if(checkSenderIsConsole(sender)) return;
        if(!hasPermission(sender, "buildbattle.admin.delete")) return;
        Arena arena = ArenaRegistry.getArena(arenaString);
        if(arena == null) {
            sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
            return;
        }
        ArenaManager.stopGame(false, arena);
        FileConfiguration config = ConfigurationManager.getConfig("arenas");
        config.set("instances." + arenaString, null);
        ConfigurationManager.saveConfig(config, "arenas");
        ArenaRegistry.unregisterArena(arena);
        sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatColor.RED + "Successfully removed game instance!");
    }

}
