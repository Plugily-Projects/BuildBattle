/*
 * BuildBattle 3 - Ultimate building competition minigame
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
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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

import java.util.LinkedList;
import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 03.05.2018
 */
public class AdminCommands extends MainCommand {

    private static List<CommandData> command = new LinkedList<>();

    static {
        ChatColor gray = ChatColor.GRAY;
        ChatColor gold = ChatColor.GOLD;
        command.add(new CommandData("/bb create " + gold + "<arena>", "/bb create <arena>",
                gray + "Create new arena\n" + gold + "Permission: " + gray + "buildbattle.admin.create"));
        command.add(new CommandData("/bb " + gold + "<arena>" + ChatColor.WHITE + " edit", "/bb <arena> edit",
                gray + "Edit existing arena\n" + gold + "Permission: " + gray + "buildbattle.admin.edit"));
        command.add(new CommandData("/bba addplot " + gold + "<arena>", "/bba addplot <arena>",
                gray + "Add new game plot to the arena\n" + gold + "Permission: " + gray + "buildbattle.admin.addplot"));
        command.add(new CommandData("/bba addnpc", "/bba addnpc",
                gray + "Add new NPC to the game plots\n" + gold + "Permission: " + gray + "buildbattle.admin.addnpc\n" + gold + "" + ChatColor.BOLD + "Requires Citizen plugin!"));
        command.add(new CommandData("/bba settheme " + gold + "<theme>", "/bba settheme <theme>",
                gray + "Set new arena theme\n" + gold + "Permission: " + gray + "buildbattle.admin.settheme\n" + gold + "You can set arena theme only when it started\n" + gold  + "and only for 20 seconds after start!"));
        command.add(new CommandData("/bba list", "/bba list",
                gray + "Shows list with all loaded arenas\n" + gold + "Permission: " + gray + "buildbattle.admin.list"));
        command.add(new CommandData("/bba stop", "/bba stop",
                gray + "Stops the arena you're in\n" + gray + "" + ChatColor.BOLD + "You must be in target arena!\n" + gold + "Permission: " + gray + "buildbattle.admin.stop"));
        command.add(new CommandData("/bba forcestart", "/bba forcestart",
                gray + "Force starts arena you're in\n" + gold + "Permission: " + gray + "buildbattle.admin.forcestart"));
        command.add(new CommandData("/bba reload", "/bba reload", gray + "Reload all game arenas\n" + gray + "" + ChatColor.BOLD +
                "They will be stopped!\n" + gold + "Permission: " + gray + "buildbattle.admin.reload"));
        command.add(new CommandData(ChatColor.STRIKETHROUGH + "/bba addsign " + ChatColor.GOLD + "<arena>", "/bba addsign <arena>",
                gray + "Set sign you look at as a target arena sign\n" + gold + "Permission: " + gray + "buildbattle.admin.addsign\n" +
                        gold + "Permission: " + gray + "buildbattle.admin.sign.create (for creating signs manually)\n" + gold + "Permission: " +
                        gray + "buildbattle.admin.sign.break (for breaking arena signs)\n" + ChatColor.BOLD + "" + ChatColor.RED + "Currently unused, use Setup menu instead"));
        command.add(new CommandData("/bba delete " + ChatColor.GOLD + "<arena>", "/bba delete <arena>",
                gray + "Deletes specified arena\n" + gold + "Permission: " + gray + "buildbattle.admin.delete"));
    }

    private Main plugin;

    public AdminCommands(Main plugin) {
        this.plugin = plugin;
    }

    public void sendHelp(CommandSender sender) {
        if(!sender.hasPermission("buildbattle.admin")) return;
        sender.sendMessage(ChatColor.GREEN + "  " + ChatColor.BOLD + "BuildBattle " + ChatColor.GRAY + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.RED + " []" + ChatColor.GRAY + " = optional  " + ChatColor.GOLD + "<>" + ChatColor.GRAY + " = required");
        sender.sendMessage(ChatColor.GRAY + "Hover command to see more, click command to suggest it.");
        for(CommandData data : command) {
            TextComponent component = new TextComponent(data.getText());
            component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, data.getCommand()));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(data.getDescription()).create()));
            sender.spigot().sendMessage(component);
        }
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
        ArenaRegistry.registerArenas();
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
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, ChatManager.colorMessage("In-Game.NPC.Floor-Change-NPC-Name"));
            npc.spawn(player.getLocation());
            npc.setProtected(true);
            npc.setName(ChatManager.colorMessage("In-Game.NPC.Floor-Change-NPC-Name"));
            player.sendMessage(ChatManager.colorMessage("In-Game.NPC.NPC-Created"));
        } else {
            player.sendMessage(ChatManager.colorMessage("In-Game.NPC.Install-Citizens"));
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

    public void setArenaTheme(CommandSender sender, String theme) {
        if(checkSenderIsConsole(sender)) return;
        if(!hasPermission(sender, "buildbattle.admin.settheme")) return;
        Arena arena = ArenaRegistry.getArena((Player) sender);
        if(arena == null) {
            sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Playing"));
            return;
        }
        if(arena.getArenaState() == ArenaState.IN_GAME && (arena.getBuildTime() - arena.getTimer()) <= 20) {
            arena.setTheme(theme);
            for(Player p : arena.getPlayers()) {
                p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Admin-Messages.Changed-Theme").replace("%THEME%", theme));
            }
        } else {
            if(arena.getArenaState() == ArenaState.STARTING) {
                sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Wait-For-Start"));
            } else {
                sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Arena-Started"));
            }
        }
    }

}
