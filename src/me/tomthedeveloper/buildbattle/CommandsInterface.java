package me.tomthedeveloper.buildbattle;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 * Created by Tom on 20/02/2016.
 */
public interface CommandsInterface {

    boolean checkPlayerCommands(Player player, Command command, String s, String[] strings);

    boolean checkSpecialCommands(Player player, Command command, String s, String[] strings);
}
