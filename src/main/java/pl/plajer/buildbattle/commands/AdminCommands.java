/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.buildbattle.commands;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.arena.ArenaManager;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.ArenaState;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.user.User;
import pl.plajer.buildbattle.utils.CuboidSelector;
import pl.plajerlair.core.utils.ConfigUtils;
import pl.plajerlair.core.utils.LocationUtils;

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
    command.add(new CommandData("/bba removeplot " + gold + "<arena> <plot ID>", "/bba removeplot <arena> <id>",
            gray + "Remove game plot from the arena\n" + gold + "Permission: " + gray + "buildbattle.admin.removeplot"));
    command.add(new CommandData("/bba plotwand", "/bba plotwand",
            gray + "Get plot wand to create plots\n" + gold + "Permission: " + gray + "buildbattle.admin.plotwand"));
    command.add(new CommandData("/bba addnpc", "/bba addnpc",
            gray + "Add new NPC to the game plots\n" + gold + "Permission: " + gray + "buildbattle.admin.addnpc\n" + gold + "" + ChatColor.BOLD + "Requires Citizen plugin!"));
    command.add(new CommandData("/bba settheme " + gold + "<theme>", "/bba settheme <theme>",
            gray + "Set new arena theme\n" + gold + "Permission: " + gray + "buildbattle.admin.settheme\n" + gold + "You can set arena theme only when it started\n" + gold + "and only for 20 seconds after start!"));
    command.add(new CommandData("/bba addvotes " + gold + "<player> <amount>", "/bba addvotes <player> <amount>",
            gray + "Add super votes to target player\n" + gold + "Permission: " + gray + "buildbattle.admin.supervotes.add"));
    command.add(new CommandData("/bba setvotes " + gold + "<player> <amount>", "/bba setvotes <player> <amount>",
            gray + "Set super votes of target player\n" + gold + "Permission: " + gray + "buildbattle.admin.supervotes.set"));
    command.add(new CommandData("/bba list", "/bba list",
            gray + "Shows list with all loaded arenas\n" + gold + "Permission: " + gray + "buildbattle.admin.list"));
    command.add(new CommandData("/bba stop", "/bba stop",
            gray + "Stops the arena you're in\n" + gray + "" + ChatColor.BOLD + "You must be in target arena!\n" + gold + "Permission: " + gray + "buildbattle.admin.stop"));
    command.add(new CommandData("/bba forcestart", "/bba forcestart",
            gray + "Force starts arena you're in\n" + gold + "Permission: " + gray + "buildbattle.admin.forcestart"));
    command.add(new CommandData("/bba reload", "/bba reload", gray + "Reload all game arenas\n" + gray + "" + ChatColor.BOLD +
            "They will be stopped!\n" + gold + "Permission: " + gray + "buildbattle.admin.reload"));
    command.add(new CommandData(ChatColor.STRIKETHROUGH + "/bba addsign " + ChatColor.GOLD + "<arena>", "/bba addsign <arena>",
            gray + "Set sign you look at as a target arena sign\n" + gold + "Permission: " + gray + "buildbattle.admin.sign.create (for creating signs manually)\n" +
                    gold + "Permission: " + gray + "buildbattle.admin.sign.break (for breaking arena signs)\n" + ChatColor.BOLD + "" + ChatColor.RED + "Currently unused, use Setup menu instead"));
    command.add(new CommandData("/bba delete " + ChatColor.GOLD + "<arena>", "/bba delete <arena>",
            gray + "Deletes specified arena\n" + gold + "Permission: " + gray + "buildbattle.admin.delete"));
  }

  private Main plugin;

  public AdminCommands(Main plugin) {
    this.plugin = plugin;
  }

  public void sendHelp(CommandSender sender) {
    if (!sender.hasPermission("buildbattle.admin")) return;
    if (checkSenderIsConsole(sender)) return;
    //fix for missing spigot() methods for CommandSender
    Player player = (Player) sender;
    player.sendMessage(ChatColor.GREEN + "  " + ChatColor.BOLD + "BuildBattle " + ChatColor.GRAY + plugin.getDescription().getVersion());
    player.sendMessage(ChatColor.RED + " []" + ChatColor.GRAY + " = optional  " + ChatColor.GOLD + "<>" + ChatColor.GRAY + " = required");
    player.sendMessage(ChatColor.GRAY + "Hover command to see more, click command to suggest it.");
    for (CommandData data : command) {
      TextComponent component = new TextComponent(data.getText());
      component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, data.getCommand()));
      component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(data.getDescription()).create()));
      player.spigot().sendMessage(component);
    }
  }

  public void addPlot(Player player, String arena) {
    if (!hasPermission(player, "buildbattle.admin.addplot")) return;
    if (ArenaRegistry.getArena(arena) == null) {
      player.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
      return;
    }
    CuboidSelector.Selection selection = plugin.getCuboidSelector().getSelection(player);
    if (selection == null || selection.getFirstPos() == null || selection.getSecondPos() == null) {
      player.sendMessage(ChatManager.colorRawMessage(ChatManager.getPrefix() + "&cPlease select both corners before adding a plot!"));
      return;
    }
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    int id = 0;
    if (config.contains("instances." + arena + ".plots")) {
      id = config.getConfigurationSection("instances." + arena + ".plots").getKeys(false).size();
    }
    LocationUtils.saveLoc(plugin, config, "arenas", "instances." + arena + ".plots." + id + ".minpoint", selection.getFirstPos());
    LocationUtils.saveLoc(plugin, config, "arenas", "instances." + arena + ".plots." + id + ".maxpoint", selection.getSecondPos());
    player.sendMessage(ChatManager.getPrefix() + ChatManager.colorRawMessage("&aPlot with ID &e" + id + "&a added to arena instance &e" + arena));
    plugin.getCuboidSelector().removeSelection(player);
  }

  public void removePlot(Player player, String arena, String plotID) {
    if (!hasPermission(player, "buildbattle.admin.removeplot")) return;
    if (ArenaRegistry.getArena(arena) == null) {
      player.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
      return;
    }
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    if (config.contains("instances." + arena + ".plots." + plotID)) {
      config.set("instances." + arena + ".plots." + plotID, null);
      ConfigUtils.saveConfig(plugin, config, "arenas");
      player.sendMessage(ChatManager.getPrefix() + ChatManager.colorRawMessage("&aPlot with ID &e" + plotID + "&a removed from arena &e" + arena));
    } else {
      player.sendMessage(ChatManager.getPrefix() + ChatManager.colorRawMessage("&cPlot with that ID doesn't exist!"));
    }
  }

  public void forceStart(Player player) {
    if (!hasPermission(player, "buildbattle.admin.forcestart")) return;
    Arena arena = ArenaRegistry.getArena(player);
    if (arena == null) return;
    if (arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING) {
      arena.setGameState(ArenaState.STARTING);
      arena.setForceStart(true);
      arena.setTimer(0);
      ChatManager.broadcast(arena, ChatManager.colorMessage("In-Game.Messages.Admin-Messages.Set-Starting-In-To-0"));
    }
  }

  public void forceStartWithTheme(Player player, String theme) {
    Arena arena = ArenaRegistry.getArena(player);
    if (arena == null) return;
    forceStart(player);
    arena.setThemeVoteTime(false);
    arena.setTheme(theme);
  }

  public void reloadPlugin(CommandSender sender) {
    if (!hasPermission(sender, "buildbattle.admin.reload")) {
      return;
    }
    plugin.getConfigPreferences().loadOptions();
    ArenaRegistry.registerArenas();
    sender.sendMessage(ChatColor.GREEN + "Plugin reloaded!");
  }

  public void addSign(Player player, String arenaName) {
    if (!hasPermission(player, "buildbattle.admin.addsign")) return;
    Arena arena = ArenaRegistry.getArena(arenaName);
    if (arena == null) {
      player.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
    } else {
      Location loc = player.getTargetBlock((Set<Material>) null, 10).getLocation();
      if (loc.getBlock().getState() instanceof Sign) {
        FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
        List<String> signs = config.getStringList("instances." + arena.getID() + ".signs");
        signs.add(loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch());
        config.set("instances." + arena.getID() + ".signs", signs);
        ConfigUtils.saveConfig(plugin, config, "arenas");
        plugin.getSignManager().getLoadedSigns().put((Sign) loc.getBlock().getState(), arena);
        player.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Signs.Sign-Created"));
      } else {
        player.sendMessage(ChatColor.RED + "You have to look at a sign to perform this command!");
      }

    }
  }

  public void stopGame(CommandSender sender) {
    if (checkSenderIsConsole(sender)) return;
    if (!hasPermission(sender, "buildbattle.admin.stopgame")) return;
    Arena a = ArenaRegistry.getArena((Player) sender);
    if (a == null) return;
    ArenaManager.stopGame(false, a);
  }

  public void addNPC(Player player) {
    if (!hasPermission(player, "buildbattle.admin.addnpc")) return;
    if (plugin.getServer().getPluginManager().isPluginEnabled("Citizens")) {
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
    if (!hasPermission(sender, "buildbattle.admin.list")) return;
    sender.sendMessage(ChatManager.colorMessage("Commands.Admin-Commands.List-Command.Header"));
    int i = 0;
    for (Arena arena : ArenaRegistry.getArenas()) {
      sender.sendMessage(ChatManager.colorMessage("Commands.Admin-Commands.List-Command.Format").replace("%arena%", arena.getID())
              .replace("%status%", arena.getArenaState().getFormattedName()).replace("%players%", String.valueOf(arena.getPlayers().size()))
              .replace("%maxplayers%", String.valueOf(arena.getMaximumPlayers())));
      i++;
    }
    if (i == 0) {
      sender.sendMessage(ChatManager.colorMessage("Commands.Admin-Commands.List-Command.No-Arenas"));
      sender.sendMessage(ChatManager.colorRawMessage("&e&lTIP: &7You can get free maps with configs at our wiki! Just head to https://wiki.plajer.xyz/minecraft/buildbattle/free_maps.php"));
    }
  }

  public void deleteArena(CommandSender sender, String arenaString) {
    if (checkSenderIsConsole(sender)) return;
    if (!hasPermission(sender, "buildbattle.admin.delete")) return;
    Arena arena = ArenaRegistry.getArena(arenaString);
    if (arena == null) {
      sender.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
      return;
    }
    ArenaManager.stopGame(false, arena);
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    config.set("instances." + arenaString, null);
    ConfigUtils.saveConfig(plugin, config, "arenas");
    ArenaRegistry.unregisterArena(arena);
    sender.sendMessage(ChatManager.getPrefix() + ChatColor.RED + "Successfully removed game instance!");
  }

  public void setArenaTheme(CommandSender sender, String theme) {
    if (checkSenderIsConsole(sender)) return;
    if (!hasPermission(sender, "buildbattle.admin.settheme")) return;
    Arena arena = ArenaRegistry.getArena((Player) sender);
    if (arena == null) {
      sender.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Commands.No-Playing"));
      return;
    }
    if (arena.getArenaState() == ArenaState.IN_GAME && (arena.getBuildTime() - arena.getTimer()) <= 20) {
      if (plugin.getConfigPreferences().isThemeBlacklisted(theme.toLowerCase())) {
        sender.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Commands.Admin-Commands.Theme-Blacklisted"));
        return;
      }
      arena.setTheme(theme);
      ChatManager.broadcast(arena, ChatManager.colorMessage("In-Game.Messages.Admin-Messages.Changed-Theme").replace("%THEME%", theme));
    } else {
      if (arena.getArenaState() == ArenaState.STARTING) {
        sender.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Commands.Wait-For-Start"));
      } else {
        sender.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Commands.Arena-Started"));
      }
    }
  }

  public void setSuperVotes(CommandSender sender, String who, String superVotes) {
    if (!hasPermission(sender, "buildbattle.admin.supervotes.set")) return;
    if (!NumberUtils.isNumber(superVotes)) {
      sender.sendMessage(ChatManager.colorRawMessage("&cArgument isn't a number!"));
      return;
    }
    if (Bukkit.getPlayer(who) == null || !Bukkit.getPlayer(who).isOnline()) {
      sender.sendMessage(ChatManager.colorMessage("Commands.Player-Not-Found"));
      return;
    }
    User user = plugin.getUserManager().getUser(Bukkit.getPlayer(who).getUniqueId());
    user.setStat(StatsStorage.StatisticType.SUPER_VOTES, Integer.parseInt(superVotes));
    sender.sendMessage(ChatManager.getPrefix() + ChatManager.colorRawMessage("&aSuper votes set."));
  }

  public void addSuperVotes(CommandSender sender, String who, String superVotes) {
    if (!hasPermission(sender, "buildbattle.admin.supervotes.add")) return;
    if (!NumberUtils.isNumber(superVotes)) {
      sender.sendMessage(ChatManager.colorRawMessage("&cArgument isn't a number!"));
      return;
    }
    if (Bukkit.getPlayer(who) == null || !Bukkit.getPlayer(who).isOnline()) {
      sender.sendMessage(ChatManager.colorMessage("Commands.Player-Not-Found"));
      return;
    }
    User user = plugin.getUserManager().getUser(Bukkit.getPlayer(who).getUniqueId());
    user.addStat(StatsStorage.StatisticType.SUPER_VOTES, Integer.parseInt(superVotes));
    sender.sendMessage(ChatManager.getPrefix() + ChatManager.colorRawMessage("&aSuper votes added."));
  }

}
