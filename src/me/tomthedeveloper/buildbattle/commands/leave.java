package me.tomthedeveloper.buildbattle.commands;

import me.tomthedeveloper.buildbattle.GameAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Tom on 5/08/2014.
 */
public class leave implements CommandExecutor {

    private GameAPI plugin;

    public leave(GameAPI plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!command.getLabel().equalsIgnoreCase("leave")) return true;
        if(!(commandSender instanceof Player)) return true;
        Player player = (Player) commandSender;
        if(plugin.getGameInstanceManager().getGameInstance(player) == null) {
            System.out.print(player.getName() + " tried /leave but isn't in an arena!");

            return true;
        }
        if(plugin.isBungeeActivated()) {
            plugin.getPlugin().getBungeeManager().connectToHub(player);
            System.out.print(player.getName() + " is teleported to the Hub Server");
            return true;
        } else {

            plugin.getGameInstanceManager().getGameInstance(player).teleportToEndLocation(player);
            plugin.getGameInstanceManager().getGameInstance(player).leaveAttempt(player);
            System.out.print(player.getName() + " has left the arena! He is teleported to the end location.");
            return true;
        }


    }
}
