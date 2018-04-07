package me.tomthedeveloper.buildbattle.commands;

import me.tomthedeveloper.buildbattle.User;
import me.tomthedeveloper.buildbattle.handlers.ChatManager;
import me.tomthedeveloper.buildbattle.handlers.UserManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Tom on 28/08/2015.
 */
public class StatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) return true;
        if(!(command.getLabel().equalsIgnoreCase("STATS"))) return true;
        Player player = (Player) commandSender;
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
}
