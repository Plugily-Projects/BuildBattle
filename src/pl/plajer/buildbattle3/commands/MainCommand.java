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
import pl.plajer.buildbattle3.arena.ArenaRegistry;
import pl.plajer.buildbattle3.arena.ArenaState;
import pl.plajer.buildbattle3.events.PlayerAddCommandEvent;
import pl.plajer.buildbattle3.handlers.ChatManager;
import pl.plajer.buildbattle3.handlers.ConfigurationManager;
import pl.plajer.buildbattle3.user.UserManager;
import pl.plajer.buildbattle3.utils.SetupInventory;
import pl.plajer.buildbattle3.utils.Util;

import java.util.ArrayList;

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
        plugin.getCommand("buildbattle").setExecutor(this);
        //todo /bba command
        this.adminCommands = new AdminCommands(plugin);
        this.gameCommands = new GameCommands(plugin);
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
        sender.sendMessage(ChatManager.PREFIX + ChatManager.colorMessage("Commands.No-Permission"));
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("buildbattle")) {
            if(checkSenderIsConsole(sender)) return true;
            Player player = (Player) sender;
            if(args.length == 0) {
                player.sendMessage(ChatColor.GOLD + "----------------{BuildBattle Commands}----------");
                player.sendMessage(ChatColor.AQUA + "/bb stats: " + ChatColor.GRAY + "Shows your stats!");
                player.sendMessage(ChatColor.AQUA + "/bb join <arena>: " + ChatColor.GRAY + "Join arena and play!");
                player.sendMessage(ChatColor.AQUA + "/bb leave: " + ChatColor.GRAY + "Quit arena you're in");
                //todo perm
                if(player.hasPermission("buildbattle.admin")) {
                    player.sendMessage(ChatColor.AQUA + "/bb create <ARENAID>: " + ChatColor.GRAY + "Create an arena!");
                    player.sendMessage(ChatColor.AQUA + "/bb <ARENAID> edit: " + ChatColor.GRAY + "Opens the menu to edit the arena!");
                    player.sendMessage(ChatColor.AQUA + "/bb addplot <ARENAID>: " + ChatColor.GRAY + "Adds a plot to the arena");
                    player.sendMessage(ChatColor.AQUA + "/bb forcestart: " + ChatColor.GRAY + "Forcestarts the arena u are in");
                    player.sendMessage(ChatColor.AQUA + "/bb reload: " + ChatColor.GRAY + "Reloads plugin");
                }
                player.sendMessage(ChatColor.GOLD + "-------------------------------------------------");
                return true;
            }
            if(args[0].equalsIgnoreCase("stats")) {
                if(checkSenderIsConsole(sender)) return true;
                gameCommands.showStats((Player) sender);
            }
            if(args[0].equalsIgnoreCase("leave")) {
                if(checkSenderIsConsole(sender)) return true;
                gameCommands.leaveGame((Player) sender);
            }
            if(args.length == 2 && args[0].equalsIgnoreCase("join")) {
                Arena arena = ArenaRegistry.getArena(args[1]);
                if(arena == null) {
                    player.sendMessage(ChatManager.colorMessage("Commands.No-Arena-Like-That"));
                    return true;
                } else {
                    if(arena.getPlayers().size() >= arena.getMaximumPlayers() && !UserManager.getUser(player.getUniqueId()).isPremium()) {
                        player.sendMessage(ChatManager.colorMessage("Commands.Arena-Is-Full"));
                        return true;
                    } else if(arena.getGameState() == ArenaState.IN_GAME) {
                        player.sendMessage(ChatManager.colorMessage("Commands.Arena-Started"));
                        return true;
                    } else {
                        arena.joinAttempt(player);
                    }
                }
            }
            //todo different permissions
            if(!hasPermission(sender, "minigames.edit")) return true;
            if(args.length == 2 && args[0].equalsIgnoreCase("addplot")) {
                adminCommands.addPlot(player, args[1]);
                return true;
            }
            if(args.length == 1 && args[0].equalsIgnoreCase("forcestart")) {
                adminCommands.forceStart(player);
            }
            if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                adminCommands.reloadPlugin(player);
                return true;
            }
            //fixme xd
            if(!(args.length > 1)) return true;
            if(args[0].equalsIgnoreCase("create")) {
                this.createArenaCommand((Player) sender, args);
                return true;
            }
            if(args[1].equalsIgnoreCase("addsign")) {
                adminCommands.addSign(player, args[0]);
                return true;
            }
            if(args[0].equalsIgnoreCase("tp") && args.length == 3) {
                onTpCommand(player, args[1], args[2]);
                return true;
            }
            if(args[1].equalsIgnoreCase("setup") || args[1].equals("edit")) {
                Arena arena = ArenaRegistry.getArena(args[0]);
                if(arena == null) {
                    player.sendMessage(ChatColor.RED + "ARENA DOES NOT EXIST!");
                    return true;
                }
                new SetupInventory(arena).openInventory(player);
                return true;
            }
            if(!(args.length > 2)) return true;

            if(!ConfigurationManager.getConfig("arenas").contains("instances." + args[0])) {
                player.sendMessage(ChatColor.RED + "Arena doesn't exists!");
                player.sendMessage(ChatColor.RED + "Usage: /bb < ARENA ID > set <MINPLAYRS | MAXPLAYERS | MAPNAME | SCHEMATIC | LOBBYLOCATION | EndLOCATION | STARTLOCATION  >  < VALUE>");
                return true;
            }
            if(args[1].equalsIgnoreCase("add")) {
                PlayerAddCommandEvent event = new PlayerAddCommandEvent(player, args, args[0]);
                plugin.getServer().getPluginManager().callEvent(event);
                plugin.saveConfig();
                return true;
            }
            if(!(args[1].equalsIgnoreCase("set"))) return true;

            FileConfiguration config = ConfigurationManager.getConfig("arenas");
            if(args.length == 3) {
                if(args[2].equalsIgnoreCase("lobbylocation") || args[2].equalsIgnoreCase("lobbyloc")) {
                    Util.saveLocation("instances." + args[0] + ".lobbylocation", player.getLocation());
                    player.sendMessage("BuildBattle: Lobby location for arena/instance " + args[0] + " set to " + Util.locationToString(player.getLocation()));
                } else if(args[2].equalsIgnoreCase("Startlocation") || args[2].equalsIgnoreCase("Startloc")) {
                    Util.saveLocation("instances." + args[0] + ".Startlocation", player.getLocation());
                    player.sendMessage("BuildBattle: Start location for arena/instance " + args[0] + " set to " + Util.locationToString(player.getLocation()));
                } else if(args[2].equalsIgnoreCase("Endlocation") || args[2].equalsIgnoreCase("Endloc")) {
                    Util.saveLocation("instances." + args[0] + ".Endlocation", player.getLocation());
                    player.sendMessage("BuildBattle: End location for arena/instance " + args[0] + " set to " + Util.locationToString(player.getLocation()));
                } else {
                    player.sendMessage(ChatColor.RED + "Invalid Command!");
                    player.sendMessage(ChatColor.RED + "Usage: /bb <ARENA > set <StartLOCTION | LOBBYLOCATION | EndLOCATION>");
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
            player.sendMessage(ChatColor.DARK_RED + "Instance/Arena already exists! Use another ID or delete it first!");
        } else {
            createInstanceInConfig(args[1]);

            player.sendMessage(ChatColor.GREEN + "Instances/Arena successfully created! Restart or reload the server to start the arena!");
            player.sendMessage(ChatColor.BOLD + "--------------- INFORMATION --------------- ");
            player.sendMessage(ChatColor.GREEN + "WORLD: " + ChatColor.RED + args[1]);
            player.sendMessage(ChatColor.GREEN + "MAX PLAYERS: " + ChatColor.RED + config.getInt("instances.default.minimumplayers"));
            player.sendMessage(ChatColor.GREEN + "MIN PLAYERS: " + ChatColor.RED + config.getInt("instances.default.maximumplayers"));
            player.sendMessage(ChatColor.GREEN + "MAP NAME: " + ChatColor.RED + config.getInt("instances.default.mapname"));
            player.sendMessage(ChatColor.GREEN + "LOBBY LOCATION " + ChatColor.RED + Util.locationToString(Util.getLocation(false, config.getString("instances." + args[1] + ".lobbylocation"))));
            player.sendMessage(ChatColor.GREEN + "Start LOCATION " + ChatColor.RED + Util.locationToString(Util.getLocation(false, config.getString("instances." + args[1] + ".Startlocation"))));
            player.sendMessage(ChatColor.GREEN + "End LOCATION " + ChatColor.RED + Util.locationToString(Util.getLocation(false, config.getString("instances." + args[1] + ".Endlocation"))));
            player.sendMessage(ChatColor.BOLD + "------------------------------------------- ");
            player.sendMessage(ChatColor.RED + "You can edit this game instances in the config!");
        }
    }

    private void createInstanceInConfig(String ID) {
        String path = "instances." + ID + ".";
        Util.saveLocation(path + "lobbylocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
        Util.saveLocation(path + "Startlocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
        Util.saveLocation(path + "Endlocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
        FileConfiguration config = ConfigurationManager.getConfig("arenas");
        config.set(path + "minimumplayers", config.getInt("instances.default.minimumplayers"));
        config.set(path + "maximumplayers", config.getInt("instances.default.maximumplayers"));
        config.set(path + "mapname", config.getInt("instances.default.mapname"));
        config.set(path + "signs", new ArrayList<>());

        config.set(path + "world", config.getString("instances.default.world"));
        ConfigurationManager.saveConfig(config, "arenas");
        plugin.loadInstances();
    }

    private boolean onTpCommand(Player player, String ID, String str) {
        LocationType type = null;
        if(str.equalsIgnoreCase("Endlocation") || str.equalsIgnoreCase("end")) type = LocationType.END;
        if(str.equalsIgnoreCase("lobby") || str.equalsIgnoreCase("lobbylocation")) type = LocationType.LOBBY;
        if(str.equalsIgnoreCase("Startlocation") || str.equalsIgnoreCase("start")) type = LocationType.START;
        if(type == null) {
            player.sendMessage(ChatColor.RED + "Usage: /bb tp <ARENA> <START|END|LOBBY>");
            return true;
        }
        Arena arena = ArenaRegistry.getArena(ID);
        if(!ConfigurationManager.getConfig("arenas").contains("instances." + ID) || arena == null) {
            player.sendMessage(ChatColor.RED + "That arena doesn't exists!");
            return true;
        }
        switch(type) {
            case LOBBY:
                if(arena.getLobbyLocation() == null) {
                    player.sendMessage(ChatColor.RED + "Lobby location isn't set for this arena!");
                    return true;
                }
                arena.teleportToLobby(player);
                player.sendMessage(ChatColor.GRAY + "Teleported to LOBBY location from arena" + ID);
                break;
            case START:
                if(arena.getLobbyLocation() == null) {
                    player.sendMessage(ChatColor.RED + "Start location isn't set for this arena!");
                    return true;
                }
                arena.teleportToStartLocation(player);
                player.sendMessage(ChatColor.GRAY + "Teleported to START location from arena" + ID);
                break;
            case END:
                if(arena.getLobbyLocation() == null) {
                    player.sendMessage(ChatColor.RED + "End location isn't set for this arena!");
                    return true;
                }
                arena.teleportToEndLocation(player);
                player.sendMessage(ChatColor.GRAY + "Teleported to END location from arena" + ID);
                break;
        }
        return true;
    }

    private enum LocationType {
        LOBBY, END, START
    }

}
