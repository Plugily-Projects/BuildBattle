package pl.plajer.buildbattle.handlers;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.language.LanguageManager;
import pl.plajer.buildbattle.utils.Util;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Tom on 27/07/2014.
 */
public class ChatManager {

    public static ChatColor NORMAL = ChatColor.GRAY;
    public static ChatColor HIGHLIGHTED = ChatColor.AQUA;
    public static String PREFIX;
    private static FileConfiguration config = null;
    private static HashMap<String, String> messages = new HashMap<>();

    public ChatManager() {
        PREFIX = colorMessage("In-Game.Plugin-Prefix");
        config = ConfigurationManager.getConfig("language");
        loadMessages();
    }

    public static String colorMessage(String msg) {
        return ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageMessage(msg));
    }

    public static String colorRawMessage(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String getFromLanguageConfig(String messageID, String defaultMessage) {
        if(messages.containsKey(messageID)) return messages.get(messageID);
        FileConfiguration configuration = ConfigurationManager.getConfig("language");
        if(!configuration.contains(messageID)) {
            configuration.set(messageID, defaultMessage);
            try {
                configuration.save(ConfigurationManager.getFile("language"));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        registerMessage(messageID, configuration.getString(messageID));
        return configuration.getString(messageID);

    }

    public static String formatMessage(Arena arena, String message, Player player) {
        String returnstring = message;
        returnstring = returnstring.replaceAll("%PLAYER%", player.getName());
        returnstring = returnstring.replaceAll("%TIME%", Integer.toString(arena.getTimer()));
        returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((arena.getTimer())));
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(arena.getPlayers().size()));
        returnstring = returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(arena.getMaximumPlayers()));
        returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(arena.getMinimumPlayers()));
        //todo remember
        returnstring = colorRawMessage(returnstring);
        return returnstring;
    }

    public static String getSingleMessage(String ID, String defualt) {
        if(messages.containsKey(ID)) return messages.get(ID).replaceAll("(&([a-f0-9]))", "\u00A7$2");
        return defualt.replaceAll("(&([a-f0-9]))", "\u00A7$2");

    }

    public static String getSingleMessage(String ID, String defualt, int i) {
        if(messages.containsKey(ID)) return messages.get(ID).replaceAll("(&([a-f0-9]))", "\u00A7$2").replaceAll("%NUMBER%", Integer.toString(i));
        return defualt.replaceAll("(&([a-f0-9]))", "\u00A7$2").replaceAll("%NUMBER%", Integer.toString(i));

    }

    public static String getSingleMessage(String ID, String defualt, OfflinePlayer player, int i) {
        if(messages.containsKey(ID))
            return messages.get(ID).replaceAll("(&([a-f0-9]))", "\u00A7$2").replaceAll("%NUMBER%", Integer.toString(i)).replaceAll("%PLAYER%", player.getName());
        return defualt.replaceAll("(&([a-f0-9]))", "\u00A7$2").replaceAll("%NUMBER%", Integer.toString(i)).replaceAll("%PLAYER%", player.getName());

    }

    public static void registerMessage(String ID, String message) {
        messages.put(ID, message);
    }

    public static String formatMessage(String message, int integer, Arena arena) {
        String returnstring = message;
        returnstring = returnstring.replaceAll("%NUMBER%", Integer.toString(integer));

        returnstring = returnstring.replaceAll("%TIME%", Integer.toString(arena.getTimer()));
        returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((arena.getTimer())));
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(arena.getPlayers().size()));
        returnstring = returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(arena.getMaximumPlayers()));
        returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(arena.getMinimumPlayers()));

        returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
        returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
        returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
        returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
        returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());
        return returnstring;
    }

    public static String formatMessage(String message, Arena arena) {
        String returnstring = message;

        returnstring = returnstring.replaceAll("%TIME%", Integer.toString(arena.getTimer()));
        returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((arena.getTimer())));
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(arena.getPlayers().size()));
        returnstring = returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(arena.getMaximumPlayers()));
        returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(arena.getMinimumPlayers()));
        returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
        returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
        returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
        returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
        returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());
        return returnstring;
    }

    public static void broadcastMessage(String ID, Arena arena) {
        if(messages.containsKey(ID)) {
            String message = formatMessage(messages.get(ID), arena);
            for(Player player : arena.getPlayers()) {
                player.sendMessage(PREFIX + message);
            }
        } else {
            for(Player p : arena.getPlayers()) {
                p.sendMessage(PREFIX + ID);
            }
        }

    }

    public static void broadcastMessage(String ID, String defaultmessage, Arena arena) {
        if(messages.containsKey(ID)) {
            broadcastMessage(ID, arena);
        } else {
            getFromLanguageConfig(ID, defaultmessage);

            broadcastMessage(ID, arena);
        }

    }

    public static void broadcastMessage(String ID, String defaultmessage, OfflinePlayer player, Arena arena) {
        if(messages.containsKey(ID)) {
            broadcastMessage(ID, player, arena);
        } else {
            getFromLanguageConfig(ID, defaultmessage);
            broadcastMessage(ID, player, arena);
        }

    }

    public static void broadcastMessage(String ID, String defaultmessage, int integer, Arena arena) {
        if(messages.containsKey(ID)) {
            broadcastMessage(ID, integer, arena);
        } else {
            getFromLanguageConfig(ID, defaultmessage);
            broadcastMessage(ID, integer, arena);
        }

    }

    //todo wtff
    public static void broadcastJoinMessage(Player p, Arena arena) {
        if(messages.containsKey("JoinMessage")) {
            ChatManager.broadcastMessage("JoinMessage", p, arena);
        } else if(messages.containsKey("Join")) {
            ChatManager.broadcastMessage("Join", p, arena);
        } else {
            ChatManager.broadcastMessage(HIGHLIGHTED + p.getName() + NORMAL + " joined the Game! (" + arena.getPlayers().size() + "/" + arena.getMaximumPlayers() + ")", arena);
        }
    }

    //todo wtf
    public static void broadcastLeaveMessage(Player p, Arena arena) {
        if(messages.containsKey("LeaveMessage")) {
            ChatManager.broadcastMessage("LeaveMessage", p, arena);
        } else if(messages.containsKey("Leave")) {
            ChatManager.broadcastMessage("Leave", p, arena);
        } else {
            ChatManager.broadcastMessage(HIGHLIGHTED + p.getName() + NORMAL + " left the Game! (" + arena.getPlayers().size() + "/" + arena.getMaximumPlayers() + ")", arena);
        }
    }

    public static void broadcastMessage(String messageID, Player player, Arena arena) {
        String message = formatMessage(messages.get(messageID), player, arena);
        for(Player player1 : arena.getPlayers()) {
            player1.sendMessage(PREFIX + message);
        }
    }

    public static void broadcastMessage(String messageID, OfflinePlayer player, Arena arena) {
        String message = formatMessage(messages.get(messageID), player, arena);
        for(Player player1 : arena.getPlayers()) {
            player1.sendMessage(PREFIX + message);
        }
    }

    public static void broadcastMessage(String messageID, int integer, Arena arena) {
        String message = formatMessage(messages.get(messageID), integer, arena);
        for(Player player1 : arena.getPlayers()) {
            player1.sendMessage(PREFIX + message);
        }
    }

    public static String getMessage(String ID, String defaultmessage, OfflinePlayer player, Arena arena) {
        if(Arena.getPlugin().getServer().getPlayer(player.getUniqueId()) != null) {
            if(messages.containsKey(ID)) {
                return getMessage(ID, Arena.getPlugin().getServer().getPlayer(player.getUniqueId()), arena);
            } else {
                ChatManager.getFromLanguageConfig(ID, defaultmessage);
                return getMessage(ID, Arena.getPlugin().getServer().getPlayer(player.getUniqueId()), arena);
            }
        } else {
            if(messages.containsKey(ID)) {
                return getMessage(ID, Arena.getPlugin().getServer().getOfflinePlayer(player.getUniqueId()), arena);
            } else {
                ChatManager.getFromLanguageConfig(ID, defaultmessage);
                return getMessage(ID, Arena.getPlugin().getServer().getOfflinePlayer(player.getUniqueId()), arena);
            }
        }
    }

    public static String formatMessage(String message, Player player, Arena arena) {
        String returnstring = message;
        returnstring = returnstring.replaceAll("%PLAYER%", player.getName());
        returnstring = returnstring.replaceAll("%TIME%", Integer.toString(arena.getTimer()));
        returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((arena.getTimer())));
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(arena.getPlayers().size()));
        returnstring = returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(arena.getMaximumPlayers()));
        returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(arena.getMinimumPlayers()));
        returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
        returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
        returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
        returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
        returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());
        return returnstring;
    }

    public static String formatMessage(String message, OfflinePlayer player, Arena arena) {
        String returnstring = message;
        returnstring = returnstring.replaceAll("%PLAYER%", player.getName());
        returnstring = returnstring.replaceAll("%TIME%", Integer.toString(arena.getTimer()));
        returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((arena.getTimer())));
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(arena.getPlayers().size()));
        returnstring = returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(arena.getMaximumPlayers()));
        returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(arena.getMinimumPlayers()));
        returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
        returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
        returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
        returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
        returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());
        return returnstring;
    }

    public String getMessage(String ID, Player player, Arena arena) {
        return formatMessage(messages.get(ID), player, arena);
    }

    public String getMessage(String ID, OfflinePlayer playername, Arena arena) {
        return formatMessage(messages.get(ID), playername.getName(), arena);
    }

    public String formatMessage(String message, String playername, Arena arena) {
        String returnstring = message;
        returnstring = returnstring.replaceAll("%PLAYER%", playername);
        returnstring = returnstring.replaceAll("%TIME%", Integer.toString(arena.getTimer()));
        returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((arena.getTimer())));
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(arena.getPlayers().size()));
        returnstring = returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(arena.getMaximumPlayers()));
        returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(arena.getMinimumPlayers()));
        returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
        returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
        returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
        returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
        returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());
        return returnstring;
    }

    public void loadMessages() {
        for(String path : config.getKeys(false)) {
            String string = config.getString(path);
            messages.put(path, string);
        }

    }


}
