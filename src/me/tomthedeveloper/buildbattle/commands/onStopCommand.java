package me.tomthedeveloper.buildbattle.commands;

import me.tomthedeveloper.buildbattle.GameAPI;
import me.tomthedeveloper.buildbattle.game.GameInstance;
import me.tomthedeveloper.buildbattle.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Tom on 10/08/2014.
 */
public class onStopCommand implements CommandExecutor {

    private GameAPI plugin;

    public onStopCommand(GameAPI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(command.getLabel().equalsIgnoreCase("smartstop") && commandSender.isOp()) {
            GameAPI.setRestart();
            for(Player player : Bukkit.getServer().getOnlinePlayers()) {
                player.sendMessage(ChatColor.DARK_GREEN + "RESTARTING THE SERVER AFTER ALL THE GAMES ENDED!");
                commandSender.sendMessage(ChatColor.GRAY + "Restarting process started!");

                Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        boolean b = true;
                        for(GameInstance gameInstance : plugin.getGameInstanceManager().getGameInstances()) {

                            if(gameInstance.getGameState() == GameState.INGAME || gameInstance.getGameState() == GameState.STARTING) b = false;
                        }
                        if(b == true) {
                            plugin.getPlugin().getServer().shutdown();
                        }
                        b = true;

                    }
                }, 20L, 1L);
            }
        }
        return true;
    }
}
