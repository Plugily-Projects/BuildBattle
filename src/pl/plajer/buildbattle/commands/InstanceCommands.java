package pl.plajer.buildbattle.commands;

import pl.plajer.buildbattle.CommandsInterface;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.events.PlayerAddCommandEvent;
import pl.plajer.buildbattle.utils.SetupInventory;
import pl.plajer.buildbattle.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Tom on 7/08/2014.
 */
public class InstanceCommands implements CommandExecutor {

    private Main plugin;
    private CommandsInterface commandsInterface;

    public InstanceCommands(Main plugin, CommandsInterface commandsInterface) {
        this.plugin = plugin;
        this.commandsInterface = commandsInterface;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) return true;
        Player player = (Player) commandSender;
        if(commandsInterface.checkPlayerCommands(player, command, s, args)) return true;
        if(!(player.isOp() || player.hasPermission("minigames.edit"))) return true;
        if(!(command.getLabel().equalsIgnoreCase("BuildBattle") || command.getLabel().equalsIgnoreCase("bb"))) return true;
        if(commandsInterface.checkSpecialCommands(player, command, s, args)) {
            return true;
        }
        if(!(args.length > 1)) return true;

        if(args[0].equalsIgnoreCase("create")) {
            this.createArenaCommand((Player) commandSender, args);
            return true;
        }
        if(args[1].equalsIgnoreCase("addsign")) {
            if(ArenaRegistry.getArena(args[0]) == null) {
                player.sendMessage(ChatColor.RED + "ARENA DOES NOT EXIST!");
                return true;
            } else {
                Location location = player.getTargetBlock(null, 10).getLocation();
                if(location.getBlock().getState() instanceof Sign) {
                    Arena arena = ArenaRegistry.getArena(args[0]);
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
            return true;
        }
        if(args[0].equalsIgnoreCase("tp") && args.length == 3) {
            onTpCommand(player, args[1], args[2]);
            return true;
        }

        if(args[1].equalsIgnoreCase("setup") || args[1].equals("edit")) {
            if(ArenaRegistry.getArena(args[0]) == null) {
                player.sendMessage(ChatColor.RED + "ARENA DOES NOT EXIST!");
                return true;
            }
            new SetupInventory(ArenaRegistry.getArena(args[0])).openInventory(player);
            return true;
        }
        if(!(args.length > 2)) return true;

        if(!plugin.getConfig().contains("instances." + args[0])) {
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


        if(args.length == 3) {
            if(args[2].equalsIgnoreCase("lobbylocation") || args[2].equalsIgnoreCase("lobbyloc")) {
                Util.saveLoc("instances." + args[0] + ".lobbylocation", player.getLocation());
                player.sendMessage("BuildBattle: Lobby location for arena/instance " + args[0] + " set to " + Util.locationToString(player.getLocation()));
            } else if(args[2].equalsIgnoreCase("Startlocation") || args[2].equalsIgnoreCase("Startloc")) {
                Util.saveLoc("instances." + args[0] + ".Startlocation", player.getLocation());
                player.sendMessage("BuildBattle: Start location for arena/instance " + args[0] + " set to " + Util.locationToString(player.getLocation()));
            } else if(args[2].equalsIgnoreCase("Endlocation") || args[2].equalsIgnoreCase("Endloc")) {
                Util.saveLoc("instances." + args[0] + ".Endlocation", player.getLocation());
                player.sendMessage("BuildBattle: End location for arena/instance " + args[0] + " set to " + Util.locationToString(player.getLocation()));
            } else {
                player.sendMessage(ChatColor.RED + "Invalid Command!");
                player.sendMessage(ChatColor.RED + "Usage: /bb <ARENA > set <StartLOCTION | LOBBYLOCATION | EndLOCATION>");
            }
        } else if(args.length == 4) {

            if(args[2].equalsIgnoreCase("MAXPLAYERS") || args[2].equalsIgnoreCase("maximumplayers")) {
                plugin.getConfig().set("instances." + args[0] + ".maximumplayers", Integer.parseInt(args[3]));
                player.sendMessage("BuildBattle: Maximum players for arena/instance " + args[0] + " set to " + Integer.parseInt(args[3]));

            } else if(args[2].equalsIgnoreCase("MINPLAYERS") || args[2].equalsIgnoreCase("minimumplayers")) {
                plugin.getConfig().set("instances." + args[0] + ".minimumplayers", Integer.parseInt(args[3]));
                player.sendMessage("BuildBattle: Minimum players for arena/instance " + args[0] + " set to " + Integer.parseInt(args[3]));
            } else if(args[2].equalsIgnoreCase("MAPNAME") || args[2].equalsIgnoreCase("NAME")) {
                plugin.getConfig().set("instances." + args[0] + ".mapname", args[3]);
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
                plugin.getConfig().set("instances." + args[0] + ".world", args[3]);
                player.sendMessage("BuildBattle: World for arena/instance " + args[0] + " set to " + args[3]);
            } else {
                player.sendMessage(ChatColor.RED + "Invalid Command!");
                player.sendMessage(ChatColor.RED + "Usage: /bb set <MINPLAYERS | MAXPLAYERS | SCHEMATIC> <value>");
            }
        }
        plugin.saveConfig();
        return true;
    }


    private void createArenaCommand(Player player, String[] strings) {


        boolean b = false;
        for(Arena arena : ArenaRegistry.getArenas()) {
            if(arena.getID().equalsIgnoreCase(strings[1])) {
                b = true;
                break;
            }

        }
        if(b) {
            player.sendMessage(ChatColor.DARK_RED + "Arena with that ID already exists!");
            player.sendMessage(ChatColor.DARK_RED + "Usage: bb create <ID>");
            return;
        }
        if(plugin.getConfig().contains("instances." + strings[1])) {
            player.sendMessage(ChatColor.DARK_RED + "Instance/Arena already exists! Use another ID or delete it first!");
        } else {
            createInstanceInConfig(strings[1]);

            player.sendMessage(ChatColor.GREEN + "Instances/Arena successfully created! Restart or reload the server to start the arena!");
            player.sendMessage(ChatColor.BOLD + "--------------- INFORMATION --------------- ");
            player.sendMessage(ChatColor.GREEN + "WORLD: " + ChatColor.RED + strings[1]);
            player.sendMessage(ChatColor.GREEN + "MAX PLAYERS: " + ChatColor.RED + plugin.getConfig().getInt("instances.default.minimumplayers"));
            player.sendMessage(ChatColor.GREEN + "MIN PLAYERS: " + ChatColor.RED + plugin.getConfig().getInt("instances.default.maximumplayers"));
            player.sendMessage(ChatColor.GREEN + "MAP NAME: " + ChatColor.RED + plugin.getConfig().getInt("instances.default.mapname"));
            player.sendMessage(ChatColor.GREEN + "LOBBY LOCATION " + ChatColor.RED + Util.locationToString(Util.getLocation("instances." + strings[1] + ".lobbylocation")));
            player.sendMessage(ChatColor.GREEN + "Start LOCATION " + ChatColor.RED + Util.locationToString(Util.getLocation("instances." + strings[1] + ".Startlocation")));
            player.sendMessage(ChatColor.GREEN + "End LOCATION " + ChatColor.RED + Util.locationToString(Util.getLocation("instances." + strings[1] + ".Endlocation")));
            player.sendMessage(ChatColor.BOLD + "------------------------------------------- ");
            player.sendMessage(ChatColor.RED + "You can edit this game instances in the config!");
        }
    }

    private void createInstanceInConfig(String ID) {
        String path = "instances." + ID + ".";
        Util.saveLoc(path + "lobbylocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
        Util.saveLoc(path + "Startlocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
        Util.saveLoc(path + "Endlocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
        plugin.getConfig().set(path + "minimumplayers", plugin.getConfig().getInt("instances.default.minimumplayers"));
        plugin.getConfig().set(path + "maximumplayers", plugin.getConfig().getInt("instances.default.maximumplayers"));
        plugin.getConfig().set(path + "mapname", plugin.getConfig().getInt("instances.default.mapname"));

        plugin.getConfig().set(path + "world", plugin.getConfig().getString("instances.default.world"));
        plugin.saveConfig();
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
        if(!plugin.getConfig().contains("instances." + ID)) {
            player.sendMessage(ChatColor.RED + "That arena doesn't exists!");
            return true;
        }
        Arena arena = ArenaRegistry.getArena(ID);

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
