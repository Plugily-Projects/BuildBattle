package pl.plajer.buildbattle.commands;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import pl.plajer.buildbattle.ConfigPreferences;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.User;
import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.ArenaState;
import pl.plajer.buildbattle.events.PlayerAddCommandEvent;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.handlers.UserManager;
import pl.plajer.buildbattle.utils.SetupInventory;
import pl.plajer.buildbattle.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 26.04.2018
 */
public class GameCommands implements CommandExecutor {

    private Main plugin;

    public GameCommands(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("buildbattle").setExecutor(this);
        plugin.getCommand("leave").setExecutor(this);
        plugin.getCommand("stats").setExecutor(this);
        plugin.getCommand("addsigns").setExecutor(this);
    }

    boolean checkSenderIsConsole(CommandSender sender) {
        if(sender instanceof ConsoleCommandSender) {
            //todo make me translatable
            sender.sendMessage("Only player can execute this command!");
            return true;
        }
        return false;
    }

    boolean hasPermission(CommandSender sender, String perm) {
        if(sender.hasPermission(perm)) {
            return true;
        }
        //todo make me translatable
        sender.sendMessage("You don't have permission to this command!");
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("leave")) {
            if(plugin.getConfig().getBoolean("Disable-Leave-Command")) return true;
            if(checkSenderIsConsole(sender)) return true;
            Player player = (Player) sender;
            if(ArenaRegistry.getArena(player) == null) {
                System.out.print(player.getName() + " tried /leave but isn't in an arena!");
                return true;
            }
            if(plugin.isBungeeActivated()) {
                plugin.getBungeeManager().connectToHub(player);
                System.out.print(player.getName() + " is teleported to the Hub Server");
                return true;
            } else {
                ArenaRegistry.getArena(player).teleportToEndLocation(player);
                ArenaRegistry.getArena(player).leaveAttempt(player);
                System.out.print(player.getName() + " has left the arena! He is teleported to the end location.");
                return true;
            }
        }
        if(cmd.getName().equalsIgnoreCase("stats")) {
            Player player = (Player) sender;
            User user = UserManager.getUser(player.getUniqueId());
            player.sendMessage(ChatManager.getSingleMessage("STATS-Above-Line", ChatColor.BOLD + "-----YOUR STATS----- "));
            player.sendMessage(ChatManager.getSingleMessage("STATS-Wins", ChatColor.GREEN + "Wins: " + ChatColor.YELLOW) + user.getInt("wins"));
            player.sendMessage(ChatManager.getSingleMessage("STATS-Loses", ChatColor.GREEN + "Loses: " + ChatColor.YELLOW) + user.getInt("loses"));
            player.sendMessage(ChatManager.getSingleMessage("STATS-Games-Played", ChatColor.GREEN + "Games played: " + ChatColor.YELLOW) + user.getInt("gamesplayed"));
            player.sendMessage(ChatManager.getSingleMessage("STATS-Highest-Win", ChatColor.GREEN + "Highest win (points): " + ChatColor.YELLOW) + user.getInt("highestwin"));
            player.sendMessage(ChatManager.getSingleMessage("STATS-Blocks-Placed", ChatColor.GREEN + "Blocks Placed: " + ChatColor.YELLOW) + user.getInt("blocksplaced"));
            player.sendMessage(ChatManager.getSingleMessage("STATS-Blocks-Broken", ChatColor.GREEN + "Blocks Broken: " + ChatColor.YELLOW) + user.getInt("blocksbroken"));
            player.sendMessage(ChatManager.getSingleMessage("STATS-Particles-Placed", ChatColor.GREEN + "Particles Placed: " + ChatColor.YELLOW) + user.getInt("particles"));
            player.sendMessage(ChatManager.getSingleMessage("STATS-Under-Line", ChatColor.BOLD + "--------------------"));
            return true;
        }
        if(cmd.getName().equalsIgnoreCase("addsigns")) {
            if(checkSenderIsConsole(sender)) return true;
            Player player = (Player) sender;
            Selection selection = plugin.getWorldEditPlugin().getSelection(player);
            int i = plugin.getConfig().getConfigurationSection("signs").getKeys(false).size();
            int counter = 0;
            i = i + 2;
            if(selection == null) {
                player.sendMessage("You have to select a region with 1 or more signs in it with World Edit before clicking on the sign");
                return true;
            }
            if(selection instanceof CuboidSelection) {
                CuboidSelection cuboidSelection = (CuboidSelection) selection;
                Vector min = cuboidSelection.getNativeMinimumPoint();
                Vector max = cuboidSelection.getNativeMaximumPoint();
                for(int x = min.getBlockX(); x <= max.getBlockX(); x = x + 1) {
                    for(int y = min.getBlockY(); y <= max.getBlockY(); y = y + 1) {
                        for(int z = min.getBlockZ(); z <= max.getBlockZ(); z = z + 1) {
                            Location tmpblock = new Location(player.getWorld(), x, y, z);
                            if(tmpblock.getBlock().getState() instanceof Sign && !getSigns().contains(tmpblock.getBlock().getState())) {
                                Util.saveLoc("signs." + i, tmpblock);
                                counter++;
                                i++;
                            }

                        }
                    }
                }

            } else {
                if(selection.getMaximumPoint().getBlock().getState() instanceof Sign && !getSigns().contains(selection.getMaximumPoint().getBlock().getState())) {
                    plugin.getSignManager().registerSign((Sign) selection.getMaximumPoint().getBlock().getState());
                    Util.saveLoc("signs." + i, selection.getMaximumPoint());
                    counter++;
                    i++;
                }
                if(selection.getMinimumPoint().getBlock().getState() instanceof Sign && !getSigns().contains(selection.getMinimumPoint().getBlock().getState())) {
                    plugin.getSignManager().registerSign((Sign) selection.getMinimumPoint().getBlock().getState());
                    Util.saveLoc("signs." + i, selection.getMinimumPoint());
                    counter++;
                    i++;
                }
            }
            plugin.saveConfig();
            player.sendMessage(ChatColor.GREEN + "" + counter + " signs added!");
            return true;
        }
        if(cmd.getName().equalsIgnoreCase("buildbattle")) {
            if(checkSenderIsConsole(sender)) return true;
            Player player = (Player) sender;
            if(args.length == 0) {
                player.sendMessage(ChatColor.GOLD + "----------------{BuildBattle Commands}----------");
                player.sendMessage(ChatColor.AQUA + "/BuildBattle create <ARENAID>: " + ChatColor.GRAY + "Create an arena!");
                player.sendMessage(ChatColor.AQUA + "/BuildBattle <ARENAID> edit: " + ChatColor.GRAY + "Opens the menu to edit the arena!");
                player.sendMessage(ChatColor.AQUA + "/BuildBattle addplot <ARENAID>: " + ChatColor.GRAY + "Adds a plot to the arena");
                player.sendMessage(ChatColor.AQUA + "/BuildBattle forcestart: " + ChatColor.GRAY + "Forcestarts the arena u are in");
                player.sendMessage(ChatColor.AQUA + "/BuildBattle reload: " + ChatColor.GRAY + "Reloads plugin");
                player.sendMessage(ChatColor.GOLD + "-------------------------------------------------");
                return true;
            }
            if(args.length == 2 && args[0].equalsIgnoreCase("join")) {
                Arena arena = ArenaRegistry.getArena(args[1]);
                if(arena == null) {
                    player.sendMessage(ChatManager.getSingleMessage("Arena-Does-Not-Exist", ChatColor.RED + "This arena does not exist!"));
                    return true;
                } else {
                    if(arena.getPlayers().size() >= arena.getMAX_PLAYERS() && !UserManager.getUser(player.getUniqueId()).isPremium()) {
                        player.sendMessage(ChatManager.getSingleMessage("Arena-Is-Full", ChatColor.RED + "This arena does not exist!"));
                        return true;
                    } else if(arena.getGameState() == ArenaState.INGAME) {
                        player.sendMessage(ChatManager.getSingleMessage("Arena-Is-Already-Started", ChatColor.RED + "This arena is already started!"));
                        return true;
                    } else {
                        arena.joinAttempt(player);
                    }
                }
            }
            //todo different permissions
            if(!hasPermission(sender, "minigames.edit")) return true;
            if(args.length == 2 && args[0].equalsIgnoreCase("addplot")) {
                if(ArenaRegistry.getArena(args[1]) == null) {
                    player.sendMessage(ChatColor.RED + "That gameinstance doesn't exist!");
                    return true;
                }
                Selection selection = plugin.getWorldEditPlugin().getSelection(player);
                if(selection instanceof CuboidSelection) {
                    if(plugin.getConfig().contains("instances." + args[1] + ".plots")) {
                        Util.saveLoc("instances." + args[1] + ".plots." + (plugin.getConfig().getConfigurationSection("instances." + args[1] + ".plots").getKeys(false).size() + 1) + ".minpoint", selection.getMinimumPoint());
                        Util.saveLoc("instances." + args[1] + ".plots." + (plugin.getConfig().getConfigurationSection("instances." + args[1] + ".plots").getKeys(false).size()) + ".maxpoint", selection.getMaximumPoint());
                    } else {
                        Util.saveLoc("instances." + args[1] + ".plots.0.minpoint", selection.getMinimumPoint());
                        Util.saveLoc("instances." + args[1] + ".plots.0.maxpoint", selection.getMaximumPoint());
                    }
                    plugin.saveConfig();
                    player.sendMessage(ChatColor.GREEN + "Plot added to instance " + ChatColor.RED + args[1]);
                } else {
                    player.sendMessage(ChatColor.RED + "U don't have the right selection!");
                }
                return true;
            }
            if(args.length == 1 && args[0].equalsIgnoreCase("forcestart")) {
                if(ArenaRegistry.getArena(player) == null) return false;
                Arena invasionInstance = ArenaRegistry.getArena(player);
                if(invasionInstance.getGameState() == ArenaState.WAITING_FOR_PLAYERS) {
                    invasionInstance.setGameState(ArenaState.STARTING);
                    invasionInstance.getChatManager().broadcastMessage("Admin-ForceStart-Game", ChatManager.HIGHLIGHTED + "An admin forcestarted the game!");
                    return true;
                }
                if(invasionInstance.getGameState() == ArenaState.STARTING) {
                    invasionInstance.setTimer(0);
                    invasionInstance.getChatManager().broadcastMessage("Admin-Set-Starting-In-To-0", ChatManager.HIGHLIGHTED + "An admin set waiting time to 0. Game starts now!");
                    return true;
                }
            }
            if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {
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
                return true;
            }
            //fixme xd
            if(!(args.length > 1)) return true;
            if(args[0].equalsIgnoreCase("create")) {
                this.createArenaCommand((Player) sender, args);
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
        return false;
    }

    private void createArenaCommand(Player player, String[] strings) {
        for(Arena arena : ArenaRegistry.getArenas()) {
            if(arena.getID().equalsIgnoreCase(strings[1])) {
                player.sendMessage(ChatColor.DARK_RED + "Arena with that ID already exists!");
                player.sendMessage(ChatColor.DARK_RED + "Usage: bb create <ID>");
                return;
            }
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

    public List<Sign> getSigns() {
        List<Sign> list = new ArrayList<>();
        for(String s : plugin.getConfig().getConfigurationSection("signs").getKeys(false)) {
            s = "signs." + s;
            Location location = Util.getLocation(s);
            if(location.getBlock().getState() instanceof Sign) list.add((Sign) location.getBlock().getState());
        }
        return list;
    }

    private enum LocationType {
        LOBBY, END, START
    }

}
