package me.tomthedeveloper.buildbattle;

import me.tomthedeveloper.buildbattle.utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Created by Tom on 10/08/2015.
 */
public class ChatFormatter {


    public static String formatMessage(String message, int integer) {
        String returnstring = message;
        returnstring = returnstring.replaceAll("%NUMBER%", Integer.toString(integer));
        returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS(integer));

        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
        returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
        returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
        returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
        returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());

        return returnstring;
    }

    public static String formatMessage(String message) {
        String returnstring = message;
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
        returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
        returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
        returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
        returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());

        return returnstring;
    }

}
