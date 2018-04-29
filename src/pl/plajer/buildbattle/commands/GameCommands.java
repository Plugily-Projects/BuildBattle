package pl.plajer.buildbattle.commands;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.User;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.handlers.UserManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * @author Plajer
 * <p>
 * Created at 26.04.2018
 */
public class GameCommands implements CommandExecutor {

    private Main plugin;

    public GameCommands(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("leave").setExecutor(this);
        plugin.getCommand("stats").setExecutor(this);
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
            if(plugin.getGameAPI().getGameInstanceManager().getArena(player) == null) {
                System.out.print(player.getName() + " tried /leave but isn't in an arena!");
                return true;
            }
            if(plugin.isBungeeActivated()) {
                plugin.getBungeeManager().connectToHub(player);
                System.out.print(player.getName() + " is teleported to the Hub Server");
                return true;
            } else {
                plugin.getGameAPI().getGameInstanceManager().getArena(player).teleportToEndLocation(player);
                plugin.getGameAPI().getGameInstanceManager().getArena(player).leaveAttempt(player);
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
        return false;
    }
}
