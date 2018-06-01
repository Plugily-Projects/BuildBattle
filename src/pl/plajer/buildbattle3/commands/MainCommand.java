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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.arena.Arena;
import pl.plajer.buildbattle3.arena.ArenaManager;
import pl.plajer.buildbattle3.arena.ArenaRegistry;
import pl.plajer.buildbattle3.arena.ArenaState;
import pl.plajer.buildbattle3.handlers.ChatManager;
import pl.plajer.buildbattle3.handlers.ConfigurationManager;
import pl.plajer.buildbattle3.handlers.PermissionManager;
import pl.plajer.buildbattle3.utils.SetupInventory;
import pl.plajer.buildbattle3.utils.StringMatcher;
import pl.plajer.buildbattle3.utils.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 26.04.2018
 */
public class MainCommand implements CommandExecutor {

    private Main plugin;
    private AdminCommands adminCommands;
    private GameCommands gameCommands;

    public MainCommand() {}

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
        if(sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatManager.colorMessage("Commands.Only-By-Player"));
            return true;
        }
        return false;
    }

    boolean hasPermission(CommandSender sender, String perm) {
        if(sender.hasPermission(perm)) {
            return true;
        }
        sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Permission"));
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("buildbattleadmin")) {
            if(args.length == 0) {
                adminCommands.sendHelp(sender);
                return true;
            }
            if(checkSenderIsConsole(sender)) return true;
            Player player = (Player) sender;
            if(args[0].equalsIgnoreCase("addplot")) {
                if(args.length == 2) {
                    adminCommands.addPlot(player, args[1]);
                } else {
                    player.sendMessage(ChatManager.colorMessage("Commands.Invalid-Args"));
                }
                return true;
            } else if(args[0].equalsIgnoreCase("forcestart")) {
                adminCommands.forceStart(player);
                return true;
            } else if(args[0].equalsIgnoreCase("reload")) {
                adminCommands.reloadPlugin(player);
                return true;
            } else if(args[0].equalsIgnoreCase("addnpc")) {
                adminCommands.addNPC(player);
                return true;
            } else if(args[0].equalsIgnoreCase("stop")) {
                adminCommands.stopGame(sender);
                return true;
            } else if(args[0].equalsIgnoreCase("list")) {
                adminCommands.printList(sender);
                return true;
            } else if(args[0].equalsIgnoreCase("delete")) {
                if(args.length == 2) {
                    adminCommands.deleteArena(sender, args[1]);
                } else {
                    player.sendMessage(ChatManager.colorMessage("Commands.Invalid-Args"));
                }
                return true;
            } else if(args[0].equalsIgnoreCase("help")) {
                adminCommands.sendHelp(sender);
                return true;
            }
            adminCommands.sendHelp(sender);
            List<StringMatcher.Match> matches = StringMatcher.match(args[0], Arrays.asList("stop", "list", "forcestart", "reload", "delete"));
            if(!matches.isEmpty()) {
                sender.sendMessage(ChatManager.colorMessage("Commands.Did-You-Mean").replaceAll("%command%", "bba " + matches.get(0).getMatch()));
            }
            return true;
        }
        if(cmd.getName().equalsIgnoreCase("buildbattle")) {
            if(checkSenderIsConsole(sender)) return true;
            Player player = (Player) sender;
            if(args.length == 0) {
                player.sendMessage(ChatColor.GOLD + "----------------{BuildBattle Commands}----------");
                player.sendMessage(ChatColor.AQUA + "/bb stats [player]" + ChatColor.GRAY + ": Shows your stats!");
                player.sendMessage(ChatColor.AQUA + "/bb join <arena>" + ChatColor.GRAY + ": Join arena and play!");
                player.sendMessage(ChatColor.AQUA + "/bb leave" + ChatColor.GRAY + ": Quit arena you're in");
                if(player.hasPermission("buildbattle.admin")) {
                    player.sendMessage(ChatColor.AQUA + "/bba help" + ChatColor.GRAY + ": Admin commands");
                }
                player.sendMessage(ChatColor.GOLD + "-------------------------------------------------");
                return true;
            } else if(args[0].equalsIgnoreCase("stats")) {
                if(checkSenderIsConsole(sender)) return true;
                if(args.length == 1) {
                    gameCommands.showStats((Player) sender);
                } else {
                    if(Bukkit.getPlayer(args[1]) == null) {
                        player.sendMessage(ChatManager.colorMessage("Commands.Player-Not-Found"));
                        return true;
                    }
                    gameCommands.showStatsOther((Player) sender, Bukkit.getPlayer(args[1]));
                }
                return true;
            } else if(args[0].equalsIgnoreCase("leave")) {
                if(checkSenderIsConsole(sender)) return true;
                gameCommands.leaveGame(sender);
                return true;
            } else if(args[0].equalsIgnoreCase("join")) {
                if(args.length == 2) {
                    Arena arena = ArenaRegistry.getArena(args[1]);
                    if(arena == null) {
                        player.sendMessage(ChatManager.colorMessage("Commands.No-Arena-Like-That"));
                    } else {
                        if(arena.getPlayers().size() >= arena.getMaximumPlayers()) {
                            player.sendMessage(ChatManager.colorMessage("Commands.Arena-Is-Full"));
                        } else if(arena.getArenaState() == ArenaState.IN_GAME) {
                            player.sendMessage(ChatManager.colorMessage("Commands.Arena-Started"));
                        } else {
                            ArenaManager.joinAttempt(player, arena);
                        }
                    }
                    return true;
                }
                player.sendMessage(ChatManager.colorMessage("Commands.Invalid-Args"));
                return true;
            } else if(!args[0].equalsIgnoreCase("create") && !(args.length > 1)) {
                List<StringMatcher.Match> matches = StringMatcher.match(args[0], Arrays.asList("stats", "join", "leave"));
                if(!matches.isEmpty()) {
                    sender.sendMessage(ChatManager.colorMessage("Commands.Did-You-Mean").replaceAll("%command%", "bb " + matches.get(0).getMatch()));
                }
                return true;
            }
            if(!hasPermission(sender, PermissionManager.getEditGames())) return true;
            if(!(args.length > 1)) return true;
            if(args[0].equalsIgnoreCase("create")) {
                this.createArenaCommand((Player) sender, args);
                return true;
            }
            if(args[1].equalsIgnoreCase("setup") || args[1].equals("edit")) {
                Arena arena = ArenaRegistry.getArena(args[0]);
                if(arena == null) {
                    player.sendMessage(ChatManager.colorMessage("Commands.No-Arena-Like-That"));
                    return true;
                }
                new SetupInventory(arena).openInventory(player);
                return true;
            }
            if(!(args.length > 2)) return true;

            if(!ConfigurationManager.getConfig("arenas").contains("instances." + args[0])) {
                player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
                player.sendMessage(ChatColor.RED + "Usage: /bb < ARENA ID > set <MINPLAYRS | MAXPLAYERS | MAPNAME | SCHEMATIC | LOBBYLOCATION | EndLOCATION | STARTLOCATION  >  < VALUE>");
                return true;
            }
            if(!(args[1].equalsIgnoreCase("set"))) return true;

            FileConfiguration config = ConfigurationManager.getConfig("arenas");
            if(args.length == 3) {
                if(args[2].equalsIgnoreCase("lobbylocation") || args[2].equalsIgnoreCase("lobbyloc")) {
                    Util.saveLocation("instances." + args[0] + ".lobbylocation", player.getLocation());
                    player.sendMessage("BuildBattle: Lobby location for arena/instance " + args[0] + " set to " + Util.locationToString(player.getLocation()));
                    return true;
                } else if(args[2].equalsIgnoreCase("Endlocation") || args[2].equalsIgnoreCase("Endloc")) {
                    Util.saveLocation("instances." + args[0] + ".Endlocation", player.getLocation());
                    player.sendMessage("BuildBattle: End location for arena/instance " + args[0] + " set to " + Util.locationToString(player.getLocation()));
                    return true;
                } else {
                    player.sendMessage(ChatColor.RED + "Invalid Command!");
                    player.sendMessage(ChatColor.RED + "Usage: /bb <ARENA > set <LOBBYLOCATION | EndLOCATION>");
                }
            } else if(args.length == 4) {
                if(args[2].equalsIgnoreCase("MAXPLAYERS") || args[2].equalsIgnoreCase("maximumplayers")) {
                    config.set("instances." + args[0] + ".maximumplayers", Integer.parseInt(args[3]));
                    player.sendMessage("BuildBattle: Maximum players for arena/instance " + args[0] + " set to " + Integer.parseInt(args[3]));
                } else if(args[2].equalsIgnoreCase("MINPLAYERS") || args[2].equalsIgnoreCase("minimumplayers")) {
                    config.set("instances." + args[0] + ".minimumplayers", Integer.parseInt(args[3]));
                    player.sendMessage("BuildBattle: Minimum players for arena/instance " + args[0] + " set to " + Integer.parseInt(args[3]));
                } else if(args[2].equalsIgnoreCase("MAPNAME") || args[2].equalsIgnoreCase("NAME")) {
                    config.set("instances." + args[0] + ".mapname", args[3]);
                    player.sendMessage("BuildBattle: Map name for arena/instance " + args[0] + " set to " + args[3]);
                } else if(args[2].equalsIgnoreCase("WORLD") || args[2].equalsIgnoreCase("MAP")) {
                    boolean exists = false;
                    for(World world : Bukkit.getWorlds()) {
                        if(world.getName().equalsIgnoreCase(args[3])) exists = true;
                    }
                    if(!exists) {
                        player.sendMessage(ChatColor.RED + "That world doesn't exists!");
                        return true;
                    }
                    config.set("instances." + args[0] + ".world", args[3]);
                    player.sendMessage("BuildBattle: World for arena/instance " + args[0] + " set to " + args[3]);
                } else {
                    player.sendMessage(ChatColor.RED + "Invalid Command!");
                    player.sendMessage(ChatColor.RED + "Usage: /bb set <MINPLAYERS | MAXPLAYERS> <value>");
                }
            }
            ConfigurationManager.saveConfig(config, "arenas");
            return true;
        }
        return false;
    }

    private void createArenaCommand(Player player, String[] args) {
        for(Arena arena : ArenaRegistry.getArenas()) {
            if(arena.getID().equalsIgnoreCase(args[1])) {
                player.sendMessage(ChatColor.DARK_RED + "Arena with that ID already exists!");
                player.sendMessage(ChatColor.DARK_RED + "Usage: bb create <ID>");
                return;
            }
        }
        FileConfiguration config = ConfigurationManager.getConfig("arenas");
        if(config.contains("instances." + args[1])) {
            player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatColor.DARK_RED + "Instance/Arena already exists! Use another ID or delete it first!");
        } else {
            createInstanceInConfig(args[1]);
            player.sendMessage(ChatColor.BOLD + "------------------------------------------");
            player.sendMessage(ChatColor.YELLOW + "      Instance " + args[1] + " created!");
            player.sendMessage("");
            player.sendMessage(ChatColor.GREEN + "Edit this arena via " + ChatColor.GOLD + "/bb " + args[1] + " edit" + ChatColor.GREEN + "!");
            player.sendMessage(ChatColor.BOLD + "------------------------------------------- ");
        }
    }

    private void createInstanceInConfig(String ID) {
        String path = "instances." + ID + ".";
        Util.saveLocation(path + "lobbylocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
        Util.saveLocation(path + "Endlocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
        FileConfiguration config = ConfigurationManager.getConfig("arenas");
        config.set(path + "minimumplayers", config.getInt("instances.default.minimumplayers"));
        config.set(path + "maximumplayers", config.getInt("instances.default.maximumplayers"));
        config.set(path + "mapname", config.getInt("instances.default.mapname"));
        config.set(path + "signs", new ArrayList<>());
        config.createSection(path + "plots");
        config.set(path + "isdone", false);
        config.set(path + "world", config.getString("instances.default.world"));
        ConfigurationManager.saveConfig(config, "arenas");

        Arena arena = new Arena(ID);

        arena.setMinimumPlayers(ConfigurationManager.getConfig("arenas").getInt(path + "minimumplayers"));
        arena.setMaximumPlayers(ConfigurationManager.getConfig("arenas").getInt(path + "maximumplayers"));
        arena.setMapName(ConfigurationManager.getConfig("arenas").getString(path + "mapname"));
        arena.setLobbyLocation(Util.getLocation(false, ConfigurationManager.getConfig("arenas").getString(path + "lobbylocation")));
        arena.setEndLocation(Util.getLocation(false, ConfigurationManager.getConfig("arenas").getString(path + "Endlocation")));
        arena.setReady(false);
        ArenaRegistry.registerArena(arena);

        ArenaRegistry.registerArenas();
    }

}
