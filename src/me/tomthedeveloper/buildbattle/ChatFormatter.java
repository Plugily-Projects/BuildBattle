package me.tomthedeveloper.buildbattle;

import me.TomTheDeveloper.Utils.Util;
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

    public static String formatMessage(String string, Player player) {
        String returnstring = string;
        returnstring = returnstring.replaceAll("%PLAYER%", player.getName());
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
        returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
        returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
        returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
        returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());

        return returnstring;
    }

    public static String formatMessage(String string, Player player, int number) {
        String returnstring = string;
        returnstring = returnstring.replaceAll("%PLAYER%", player.getName());
        returnstring = returnstring.replaceAll("%NUMBER%", Integer.toString(number));
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS(number));
        returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
        returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
        returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
        returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
        returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());

        return returnstring;
    }

    public static String formatChatMessage(String string, OfflinePlayer player, String prefix, String suffix) {
        String returnstring = string;
        returnstring = returnstring.replaceAll("%PREFIX%", prefix);
        returnstring = returnstring.replaceAll("%SUFFIX%", suffix);
        returnstring = returnstring.replaceAll("%PLAYER%", player.getName());
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
        returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
        returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
        returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
        returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());


        return returnstring;
    }
}
