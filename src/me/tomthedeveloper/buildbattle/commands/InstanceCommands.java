package me.tomthedeveloper.buildbattle.commands;

import me.tomthedeveloper.buildbattle.CommandsInterface;
import me.tomthedeveloper.buildbattle.GameAPI;
import me.tomthedeveloper.buildbattle.events.PlayerAddCommandEvent;
import me.tomthedeveloper.buildbattle.events.PlayerAddSpawnCommandEvent;
import me.tomthedeveloper.buildbattle.game.GameInstance;
import me.tomthedeveloper.buildbattle.setup.SetupInventory;
import me.tomthedeveloper.buildbattle.utils.Util;
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

    private GameAPI plugin;
    private CommandsInterface commandsInterface;

    public InstanceCommands(GameAPI plugin, CommandsInterface commandsInterface) {
        this.plugin = plugin;
        this.commandsInterface = commandsInterface;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) return true;
        Player player = (Player) commandSender;
        if(commandsInterface.checkPlayerCommands(player, command, s, strings)) return true;
        if(!(player.isOp() || player.hasPermission("minigames.edit"))) return true;
        if(!(command.getLabel().equalsIgnoreCase(plugin.getGameName()) || command.getLabel().equalsIgnoreCase(plugin.getAbreviation()))) return true;
        if(commandsInterface.checkSpecialCommands(player, command, s, strings)) {
            return true;
        }
        if(!(strings.length > 1)) return true;

        if(strings[0].equalsIgnoreCase("create")) {
            this.createArenaCommand((Player) commandSender, strings);
            return true;
        }
        if(strings[1].equalsIgnoreCase("addsign")) {
            if(plugin.getGameInstanceManager().getGameInstance(strings[0]) == null) {
                player.sendMessage(ChatColor.RED + "ARENA DOES NOT EXIST!");
                return true;
            } else {
                Location location = player.getTargetBlock(null, 10).getLocation();
                if(location.getBlock().getState() instanceof Sign) {
                    GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(strings[0]);
                    int keys = 0;
                    if(plugin.getPlugin().getConfig().contains("signs." + gameInstance.getID())) {
                        keys = plugin.getPlugin().getConfig().getConfigurationSection("signs." + gameInstance.getID()).getKeys(false).size();
                    }
                    plugin.saveLoc("newsigns." + gameInstance.getID() + "." + (keys + 1), player.getTargetBlock(null, 10).getLocation());
                    player.sendMessage(ChatColor.GREEN + "SIGN ADDED!");
                    gameInstance.addSign(player.getTargetBlock(null, 10).getLocation());
                } else {
                    player.sendMessage(ChatColor.RED + "You have to look at a sign to perform this command!");
                }

            }
            return true;
        }
        if(strings[0].equalsIgnoreCase("tp") && strings.length == 3) {
            if(strings.length != 3) {
                player.sendMessage(ChatColor.RED + "Usage: /" + plugin.getGameName() + "tp <ARENA>");
                return true;
            }
            onTpCommand(player, strings[1], strings[2]);
            return true;
        }

        if(strings[1].equalsIgnoreCase("setup") || strings[1].equals("edit")) {
            if(plugin.getGameInstanceManager().getGameInstance(strings[0]) == null) {
                player.sendMessage(ChatColor.RED + "ARENA DOES NOT EXIST!");
                return true;
            }
            new SetupInventory(plugin.getGameInstanceManager().getGameInstance(strings[0])).openInventory(player);
            return true;
        }
        if(!(strings.length > 2)) return true;

        if(!plugin.getPlugin().getConfig().contains("instances." + strings[0])) {
            player.sendMessage(ChatColor.RED + "Arena doesn't exists!");
            player.sendMessage(ChatColor.RED + "Usage: /" + plugin.getGameName() + " < ARENA ID > set <MINPLAYRS | MAXPLAYERS | MAPNAME | SCHEMATIC | LOBBYLOCATION | EndLOCATION | STARTLOCATION  >  < VALUE>");
            return true;
        }


        if(strings[1].equalsIgnoreCase("addspawn")) {
            PlayerAddSpawnCommandEvent event = new PlayerAddSpawnCommandEvent(player, strings[2], strings[0]);
            plugin.getPlugin().getServer().getPluginManager().callEvent(event);
            plugin.getPlugin().saveConfig();
            return true;
        }
        if(strings[1].equalsIgnoreCase("add")) {
            PlayerAddCommandEvent event = new PlayerAddCommandEvent(player, strings, strings[0]);
            plugin.getPlugin().getServer().getPluginManager().callEvent(event);
            plugin.getPlugin().saveConfig();
            return true;
        }
        if(!(strings[1].equalsIgnoreCase("set"))) return true;


        if(strings.length == 3) {
            if(strings[2].equalsIgnoreCase("lobbylocation") || strings[2].equalsIgnoreCase("lobbyloc")) {
                plugin.saveLoc("instances." + strings[0] + ".lobbylocation", player.getLocation());
                player.sendMessage(plugin.getGameName() + ": Lobby location for arena/instance " + strings[0] + " set to " + Util.locationToString(player.getLocation()));
            } else if(strings[2].equalsIgnoreCase("Startlocation") || strings[2].equalsIgnoreCase("Startloc")) {
                plugin.saveLoc("instances." + strings[0] + ".Startlocation", player.getLocation());
                player.sendMessage(plugin.getGameName() + " Start location for arena/instance " + strings[0] + " set to " + Util.locationToString(player.getLocation()));
            } else if(strings[2].equalsIgnoreCase("Endlocation") || strings[2].equalsIgnoreCase("Endloc")) {
                plugin.saveLoc("instances." + strings[0] + ".Endlocation", player.getLocation());
                player.sendMessage(plugin.getGameName() + " End location for arena/instance " + strings[0] + " set to " + Util.locationToString(player.getLocation()));
            } else {
                player.sendMessage(ChatColor.RED + "Invalid Command!");
                player.sendMessage(ChatColor.RED + "Usage: /" + plugin.getGameName() + " <ARENA > set <StartLOCTION | LOBBYLOCATION | EndLOCATION>");
            }
        } else if(strings.length == 4) {

            if(strings[2].equalsIgnoreCase("MAXPLAYERS") || strings[2].equalsIgnoreCase("maximumplayers")) {
                plugin.getPlugin().getConfig().set("instances." + strings[0] + ".maximumplayers", Integer.parseInt(strings[3]));
                player.sendMessage(plugin.getGameName() + " Maximum players for arena/instance " + strings[0] + " set to " + Integer.parseInt(strings[3]));

            } else if(strings[2].equalsIgnoreCase("MINPLAYERS") || strings[2].equalsIgnoreCase("minimumplayers")) {
                plugin.getPlugin().getConfig().set("instances." + strings[0] + ".minimumplayers", Integer.parseInt(strings[3]));
                player.sendMessage(plugin.getGameName() + " Minimum players for arena/instance " + strings[0] + " set to " + Integer.parseInt(strings[3]));
            } else if(strings[2].equalsIgnoreCase("MAPNAME") || strings[2].equalsIgnoreCase("NAME")) {
                plugin.getPlugin().getConfig().set("instances." + strings[0] + ".mapname", strings[3]);
                player.sendMessage(plugin.getGameName() + " Map name for arena/instance " + strings[0] + " set to " + strings[3]);
            } else if(strings[2].equalsIgnoreCase("WORLD") || strings[2].equalsIgnoreCase("MAP")) {
                boolean exists = false;
                for(World world : Bukkit.getWorlds()) {
                    if(world.getName().equalsIgnoreCase(strings[3])) exists = true;
                }
                if(!exists) {
                    player.sendMessage(ChatColor.RED + "That world doesn't exists!");
                    return true;
                }
                plugin.getPlugin().getConfig().set("instances." + strings[0] + ".world", strings[3]);
                player.sendMessage(plugin.getGameName() + " World for arena/instance " + strings[0] + " set to " + strings[3]);
            } else if(strings[2].equalsIgnoreCase("schematic")) {
                if(plugin.needsMapRestore()) {
                    String filename = strings[3];
                    if(filename.contains(".schematic")) {
                        player.sendMessage(ChatColor.RED + "Don't put .schematic behind the name! ");
                        player.sendMessage(ChatColor.RED + "Usage: /" + plugin.getGameName() + " set schematic <filename (without .schematic)>");
                        return true;
                    } else {
                        plugin.getPlugin().getConfig().set("instances." + strings[0] + ".schematic", strings[3]);
                        player.sendMessage(plugin.getGameName() + ": Schematic file for arena/instance " + strings[0] + " set to " + strings[3]);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "This game doesn't need a schematic file!");
                    return true;
                }


            } else {
                player.sendMessage(ChatColor.RED + "Invalid Command!");
                player.sendMessage(ChatColor.RED + "Usage: /" + plugin.getGameName() + " set <MINPLAYERS | MAXPLAYERS | SCHEMATIC> <value>");
            }
        }
        plugin.getPlugin().saveConfig();
        return true;
    }


    private void createArenaCommand(Player player, String[] strings) {


        boolean b = false;
        for(GameInstance gameInstance : plugin.getGameInstanceManager().getGameInstances()) {
            if(gameInstance.getID().equalsIgnoreCase(strings[1])) {
                b = true;
                break;
            }

        }
        if(b) {
            player.sendMessage(ChatColor.DARK_RED + "Arena with that ID already exists!");
            player.sendMessage(ChatColor.DARK_RED + "Usage: " + plugin.getGameName() + " create <ID>");
            return;
        }
        if(plugin.getPlugin().getConfig().contains("instances." + strings[1])) {
            player.sendMessage(ChatColor.DARK_RED + "Instance/Arena already exists! Use another ID or delete it first!");
        } else {
            createInstanceInConfig(strings[1]);

            player.sendMessage(ChatColor.GREEN + "Instances/Arena successfully created! Restart or reload the server to start the arena!");
            player.sendMessage(ChatColor.BOLD + "--------------- INFORMATION --------------- ");
            player.sendMessage(ChatColor.GREEN + "WORLD: " + ChatColor.RED + strings[1]);
            player.sendMessage(ChatColor.GREEN + "MAX PLAYERS: " + ChatColor.RED + plugin.getPlugin().getConfig().getInt("instances.default.minimumplayers"));
            player.sendMessage(ChatColor.GREEN + "MIN PLAYERS: " + ChatColor.RED + plugin.getPlugin().getConfig().getInt("instances.default.maximumplayers"));
            player.sendMessage(ChatColor.GREEN + "MAP NAME: " + ChatColor.RED + plugin.getPlugin().getConfig().getInt("instances.default.mapname"));
            player.sendMessage(ChatColor.GREEN + "LOBBY LOCATION " + ChatColor.RED + Util.locationToString(plugin.getLocation("instances." + strings[1] + ".lobbylocation")));
            player.sendMessage(ChatColor.GREEN + "Start LOCATION " + ChatColor.RED + Util.locationToString(plugin.getLocation("instances." + strings[1] + ".Startlocation")));
            player.sendMessage(ChatColor.GREEN + "End LOCATION " + ChatColor.RED + Util.locationToString(plugin.getLocation("instances." + strings[1] + ".Endlocation")));
            player.sendMessage(ChatColor.BOLD + "------------------------------------------- ");
            player.sendMessage(ChatColor.RED + "You can edit this game instances in the config!");
        }
    }

    private void createInstanceInConfig(String ID) {
        String path = "instances." + ID + ".";
        plugin.saveLoc(path + "lobbylocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
        plugin.saveLoc(path + "Startlocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
        plugin.saveLoc(path + "Endlocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
        plugin.getPlugin().getConfig().set(path + "minimumplayers", plugin.getPlugin().getConfig().getInt("instances.default.minimumplayers"));
        plugin.getPlugin().getConfig().set(path + "maximumplayers", plugin.getPlugin().getConfig().getInt("instances.default.maximumplayers"));
        plugin.getPlugin().getConfig().set(path + "mapname", plugin.getPlugin().getConfig().getInt("instances.default.mapname"));

        plugin.getPlugin().getConfig().set(path + "world", plugin.getPlugin().getConfig().getString("instances.default.world"));
        if(plugin.needsMapRestore()) plugin.getPlugin().getConfig().set(path + "schematic", "Name of schematic wihout .schematic!");
        plugin.getPlugin().saveConfig();
        plugin.getPlugin().loadInstances();
    }

    private boolean onTpCommand(Player player, String ID, String str) {


        LocationType type = null;
        if(str.equalsIgnoreCase("Endlocation") || str.equalsIgnoreCase("end")) type = LocationType.END;
        if(str.equalsIgnoreCase("lobby") || str.equalsIgnoreCase("lobbylocation")) type = LocationType.LOBBY;
        if(str.equalsIgnoreCase("Startlocation") || str.equalsIgnoreCase("start")) type = LocationType.START;
        if(type == null) {
            player.sendMessage(ChatColor.RED + "Usage: /" + plugin.getGameName() + " tp <ARENA> <START|END|LOBBY>");
            return true;
        }
        if(!plugin.getPlugin().getConfig().contains("instances." + ID)) {
            player.sendMessage(ChatColor.RED + "That arena doesn't exists!");
            return true;
        }
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(ID);

        switch(type) {
            case LOBBY:
                if(gameInstance.getLobbyLocation() == null) {
                    player.sendMessage(ChatColor.RED + "Lobby location isn't set for this arena!");
                    return true;
                }
                gameInstance.teleportToLobby(player);
                player.sendMessage(ChatColor.GRAY + "Teleported to LOBBY location from arena" + ID);
                break;
            case START:
                if(gameInstance.getLobbyLocation() == null) {
                    player.sendMessage(ChatColor.RED + "Start location isn't set for this arena!");
                    return true;
                }
                gameInstance.teleportToStartLocation(player);
                player.sendMessage(ChatColor.GRAY + "Teleported to START location from arena" + ID);
                break;
            case END:
                if(gameInstance.getLobbyLocation() == null) {
                    player.sendMessage(ChatColor.RED + "End location isn't set for this arena!");
                    return true;
                }
                gameInstance.teleportToEndLocation(player);
                player.sendMessage(ChatColor.GRAY + "Teleported to END location from arena" + ID);
                break;
        }
        return true;
    }


    private enum LocationType {
        LOBBY, END, START;
    }

}
