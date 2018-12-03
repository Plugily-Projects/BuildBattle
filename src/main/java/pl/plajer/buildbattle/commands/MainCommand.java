/*
 * BuildBattle 4 - Ultimate building competition minigame
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.arena.ArenaManager;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.ArenaState;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.handlers.setup.SetupInventory;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.utils.ConfigUtils;
import pl.plajerlair.core.utils.LocationUtils;
import pl.plajerlair.core.utils.StringMatcher;

/**
 * @author Plajer
 * <p>
 * Created at 26.04.2018
 */
public class MainCommand implements CommandExecutor {

  private Main plugin;
  private AdminCommands adminCommands;
  private GameCommands gameCommands;

  public MainCommand() {
  }

  public MainCommand(Main plugin) {
    this.plugin = plugin;
    TabCompletion completion = new TabCompletion();
    plugin.getCommand("buildbattle").setExecutor(this);
    plugin.getCommand("buildbattle").setTabCompleter(completion);
    plugin.getCommand("buildbattleadmin").setExecutor(this);
    plugin.getCommand("buildbattleadmin").setTabCompleter(completion);
    this.adminCommands = new AdminCommands(plugin);
    this.gameCommands = new GameCommands(plugin);
  }

  public AdminCommands getAdminCommands() {
    return adminCommands;
  }

  boolean checkSenderIsConsole(CommandSender sender) {
    if (sender instanceof ConsoleCommandSender) {
      sender.sendMessage(ChatManager.colorMessage("Commands.Only-By-Player"));
      return true;
    }
    return false;
  }

  boolean hasPermission(CommandSender sender, String perm) {
    if (sender.hasPermission(perm)) {
      return true;
    }
    sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Permission"));
    return false;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    try {
      if (cmd.getName().equalsIgnoreCase("buildbattleadmin")) {
        if (args.length == 0) {
          adminCommands.sendHelp(sender);
          return true;
        }
        if (args[0].equalsIgnoreCase("addplot")) {
          if (checkSenderIsConsole(sender)) {
            return true;
          }
          Player player = (Player) sender;
          if (args.length == 2) {
            adminCommands.addPlot(player, args[1]);
          } else {
            player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Invalid-Args"));
          }
          return true;
        } else if (args[0].equalsIgnoreCase("removeplot")) {
          if (checkSenderIsConsole(sender)) {
            return true;
          }
          Player player = (Player) sender;
          if (args.length == 3) {
            adminCommands.removePlot(player, args[1], args[2]);
          } else {
            player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Invalid-Args"));
          }
          return true;
        } else if (args[0].equalsIgnoreCase("plotwand")) {
          if (checkSenderIsConsole(sender)) {
            return true;
          }
          if (!hasPermission(sender, "buildbattle.admin.plotwand")) {
            return true;
          }
          plugin.getCuboidSelector().giveSelectorWand((Player) sender);
          return true;
        } else if (args[0].equalsIgnoreCase("forcestart")) {
          if (checkSenderIsConsole(sender)) {
            return true;
          }
          Player player = (Player) sender;
          if (args.length == 2) {
            adminCommands.forceStartWithTheme(player, args[1]);
          } else {
            adminCommands.forceStart(player);
          }
          return true;
        } else if (args[0].equalsIgnoreCase("reload")) {
          adminCommands.reloadPlugin(sender);
          return true;
        } else if (args[0].equalsIgnoreCase("addnpc")) {
          if (checkSenderIsConsole(sender)) {
            return true;
          }
          Player player = (Player) sender;
          adminCommands.addNPC(player);
          return true;
        } else if (args[0].equalsIgnoreCase("stop")) {
          if (checkSenderIsConsole(sender)) {
            return true;
          }
          adminCommands.stopGame(sender);
          return true;
        } else if (args[0].equalsIgnoreCase("list")) {
          adminCommands.printList(sender);
          return true;
        } else if (args[0].equalsIgnoreCase("delete")) {
          if (checkSenderIsConsole(sender)) {
            return true;
          }
          if (args.length == 2) {
            adminCommands.deleteArena(sender, args[1]);
          } else {
            sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Invalid-Args"));
          }
          return true;
        } else if (args[0].equalsIgnoreCase("settheme")) {
          if (checkSenderIsConsole(sender)) {
            return true;
          }
          if (args.length == 2) {
            adminCommands.setArenaTheme(sender, args[1]);
          } else {
            sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Invalid-Args"));
          }
          return true;
        } else if (args[0].equalsIgnoreCase("addvotes")) {
          if (args.length == 3) {
            adminCommands.addSuperVotes(sender, args[1], args[2]);
          } else {
            sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Invalid-Args"));
          }
          return true;
        } else if (args[0].equalsIgnoreCase("setvotes")) {
          if (args.length == 3) {
            adminCommands.setSuperVotes(sender, args[1], args[2]);
          } else {
            sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Invalid-Args"));
          }
          return true;
        } else if (args[0].equalsIgnoreCase("help")) {
          adminCommands.sendHelp(sender);
          return true;
        }
        adminCommands.sendHelp(sender);
        List<StringMatcher.Match> matches = StringMatcher.match(args[0], Arrays.asList("addplot", "stop", "list", "forcestart", "reload", "delete", "settheme"));
        if (!matches.isEmpty()) {
          sender.sendMessage(ChatManager.colorMessage("Commands.Did-You-Mean").replace("%command%", "bba " + matches.get(0).getMatch()));
        }
        return true;
      }
      if (cmd.getName().equalsIgnoreCase("buildbattle")) {
        if (checkSenderIsConsole(sender)) {
          return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
          sender.sendMessage(ChatManager.colorMessage("Commands.Main-Command.Header"));
          sender.sendMessage(ChatManager.colorMessage("Commands.Main-Command.Description"));
          if (sender.hasPermission("buildbattle.admin")) {
            sender.sendMessage(ChatManager.colorMessage("Commands.Main-Command.Admin-Bonus-Description"));
          }
          sender.sendMessage(ChatManager.colorMessage("Commands.Main-Command.Footer"));
          return true;
        }
        if (args.length > 1) {
          if (args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("create")) {
            if (checkSenderIsConsole(sender)) {
              return true;
            }
            if (!hasPermission(sender, "buildbattle.admin.create")) {
              return true;
            }
            performSetup(sender, args);
            return true;
          }
        }
        if (args[0].equalsIgnoreCase("stats")) {
          if (checkSenderIsConsole(sender)) {
            return true;
          }
          if (args.length == 1) {
            gameCommands.showStats((Player) sender);
          } else {
            if (Bukkit.getPlayer(args[1]) == null) {
              player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Player-Not-Found"));
              return true;
            }
            gameCommands.showStatsOther((Player) sender, Bukkit.getPlayer(args[1]));
          }
          return true;
        } else if (args[0].equalsIgnoreCase("top")) {
          if (args.length == 2) {
            gameCommands.sendTopStatistics(sender, args[1]);
          } else {
            sender.sendMessage(ChatManager.colorMessage("Commands.Statistics.Type-Name"));
          }
          return true;
        } else if (args[0].equalsIgnoreCase("leave")) {
          if (checkSenderIsConsole(sender)) {
            return true;
          }
          gameCommands.leaveGame(sender);
          return true;
        } else if (args[0].equalsIgnoreCase("join")) {
          if (args.length == 2) {
            if (ArenaRegistry.getArena(player) != null) {
              player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Already-Playing"));
              return true;
            }
            Arena arena = ArenaRegistry.getArena(args[1]);
            if (arena == null) {
              player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
            } else {
              if (arena.getPlayers().size() >= arena.getMaximumPlayers()) {
                player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Arena-Is-Full"));
              } else if (arena.getArenaState() == ArenaState.IN_GAME) {
                player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Arena-Started"));
              } else {
                ArenaManager.joinAttempt(player, arena);
              }
            }
            return true;
          }
          player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Invalid-Args"));
          return true;
        } else if (args[0].equalsIgnoreCase("randomjoin")) {
          if (ArenaRegistry.getArena(player) != null) {
            player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Already-Playing"));
            return true;
          }
          if (!plugin.isBungeeActivated()) {
            if (args.length == 2) {
              switch (args[1].toLowerCase()) {
                case "solo":
                case "team":
                  Arena.ArenaType type = Arena.ArenaType.valueOf(args[1].toUpperCase());
                  for (Arena arena : ArenaRegistry.getArenas()) {
                    if (arena.getArenaType() == type) {
                      if (arena.getArenaState() == ArenaState.STARTING || arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS) {
                        ArenaManager.joinAttempt(player, arena);
                        return true;
                      }
                    }
                  }
                  player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Free-Arenas"));
                  return true;
                default:
                  player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Invalid-Args"));
                  return true;
              }
            } else {
              player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Invalid-Args"));
              return true;
            }
          }
        } else if (!args[0].equalsIgnoreCase("create") && !(args.length > 1)) {
          List<StringMatcher.Match> matches = StringMatcher.match(args[0], Arrays.asList("stats", "join", "leave"));
          if (!matches.isEmpty()) {
            sender.sendMessage(ChatManager.colorMessage("Commands.Did-You-Mean").replace("%command%", "bb " + matches.get(0).getMatch()));
          }
        }
        return true;
      }
      return false;
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
      return false;
    }
  }

  private void performSetup(CommandSender sender, String[] args) {
    if (!(args.length > 1)) {
      return;
    }
    if (args[0].equalsIgnoreCase("create")) {
      if (!hasPermission(sender, "buildbattle.admin.create")) {
        return;
      }
      this.createArenaCommand((Player) sender, args);
      return;
    }
    if (args[1].equals("edit")) {
      Arena arena = ArenaRegistry.getArena(args[0]);
      if (arena == null) {
        sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
        return;
      }
      sendProTip((Player) sender);
      new SetupInventory(arena).openInventory((Player) sender);
    }
  }

  private void createArenaCommand(Player player, String[] args) {
    for (Arena arena : ArenaRegistry.getArenas()) {
      if (arena.getID().equalsIgnoreCase(args[1])) {
        player.sendMessage(ChatColor.DARK_RED + "Arena with that ID already exists!");
        player.sendMessage(ChatColor.DARK_RED + "Usage: bb create <ID>");
        return;
      }
    }
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    if (config.contains("instances." + args[1])) {
      player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatColor.DARK_RED + "Instance/Arena already exists! Use another ID or delete it first!");
    } else {
      createInstanceInConfig(args[1]);
      player.sendMessage(ChatColor.BOLD + "------------------------------------------");
      player.sendMessage(ChatColor.YELLOW + "      Instance " + args[1] + " created!");
      player.sendMessage("");
      player.sendMessage(ChatColor.GREEN + "Edit this arena via " + ChatColor.GOLD + "/bb " + args[1] + " edit" + ChatColor.GREEN + "!");
      player.sendMessage(ChatColor.GOLD + "Don't know where to start? Check out tutorial video:");
      player.sendMessage(ChatColor.GOLD + SetupInventory.VIDEO_LINK);
      player.sendMessage(ChatColor.BOLD + "------------------------------------------- ");
      sendProTip(player);
    }
  }

  private void createInstanceInConfig(String ID) {
    String path = "instances." + ID + ".";
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    LocationUtils.saveLoc(plugin, config, "arenas", path + "lobbylocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
    LocationUtils.saveLoc(plugin, config, "arenas", path + "Endlocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
    config.set(path + "minimumplayers", config.getInt("instances.default.minimumplayers"));
    config.set(path + "maximumplayers", config.getInt("instances.default.maximumplayers"));
    config.set(path + "mapname", ID);
    config.set(path + "signs", new ArrayList<>());
    config.createSection(path + "plots");
    config.set(path + "gametype", "SOLO");
    config.set(path + "isdone", false);
    config.set(path + "world", config.getString("instances.default.world"));
    ConfigUtils.saveConfig(plugin, config, "arenas");

    Arena arena = new Arena(ID, plugin);

    arena.setMinimumPlayers(ConfigUtils.getConfig(plugin, "arenas").getInt(path + "minimumplayers"));
    arena.setMaximumPlayers(ConfigUtils.getConfig(plugin, "arenas").getInt(path + "maximumplayers"));
    arena.setMapName(ConfigUtils.getConfig(plugin, "arenas").getString(path + "mapname"));
    arena.setLobbyLocation(LocationUtils.getLocation(ConfigUtils.getConfig(plugin, "arenas").getString(path + "lobbylocation")));
    arena.setEndLocation(LocationUtils.getLocation(ConfigUtils.getConfig(plugin, "arenas").getString(path + "Endlocation")));
    arena.setArenaType(Arena.ArenaType.valueOf(ConfigUtils.getConfig(plugin, "arenas").getString(path + "gametype").toUpperCase()));
    arena.setReady(false);
    arena.initPoll();
    ArenaRegistry.registerArena(arena);

    ArenaRegistry.registerArenas();
  }

  private void sendProTip(Player p) {
    int rand = new Random().nextInt(3 + 1);
    switch (rand) {
      case 0:
        p.sendMessage(ChatManager.colorRawMessage("&e&lTIP: &7We are open source! You can always help us by contributing! Check https://github.com/Plajer-Lair/BuildBattle"));
        break;
      case 1:
        p.sendMessage(ChatManager.colorRawMessage("&e&lTIP: &7Help us translating plugin to your language here: https://translate.plajer.xyz"));
        break;
      case 2:
        p.sendMessage(ChatManager.colorRawMessage("&e&lTIP: &7Download some free maps! Get them here: https://wiki.plajer.xyz/minecraft/buildbattle/free_maps.php"));
        break;
      case 3:
        p.sendMessage(ChatManager.colorRawMessage("&e&lTIP: &7You can use PlaceholderAPI placeholders from our plugin! Check: https://wiki.plajer.xyz/minecraft/buildbattle/papi_placeholders.php"));
        break;
    }
  }

}
