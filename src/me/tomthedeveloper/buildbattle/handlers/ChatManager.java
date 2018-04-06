package me.tomthedeveloper.buildbattle.handlers;

import me.tomthedeveloper.buildbattle.GameAPI;
import me.tomthedeveloper.buildbattle.game.GameInstance;
import me.tomthedeveloper.buildbattle.utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Tom on 27/07/2014.
 */
public class ChatManager {

    public static ChatColor PREFIX = ChatColor.GOLD;
    public static ChatColor NORMAL = ChatColor.GRAY;
    public static ChatColor HIGHLIGHTED = ChatColor.AQUA;
    private static FileConfiguration config = null;
    private static HashMap<String, String> messages = new HashMap<String, String>();
    public String GAMENAME = "Firemaster";
    public String prefix;
    private GameInstance gameInstance;

    public ChatManager(GameInstance gameInstance) {
        this.gameInstance = gameInstance;
        config = ConfigurationManager.getConfig("language");

        GAMENAME = getFromLanguageConfig("GAMENAME", gameInstance.getPlugin().getGameName());
        prefix = getFromLanguageConfig("PREFIX", PREFIX + "[" + GAMENAME + "] " + this.NORMAL).replaceAll("(&([a-f0-9]))", "\u00A7$2").replaceAll("&l", ChatColor.BOLD.toString());
        loadMessages();


    }

    public ChatManager() {

        GAMENAME = getFromLanguageConfig("GAMENAME", gameInstance.getPlugin().getGameName());
        prefix = getFromLanguageConfig("PREFIX", PREFIX + "[" + GAMENAME + "] " + this.NORMAL).replaceAll("(&([a-f0-9]))", "\u00A7$2");
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

    public static String getSingleMessage(String ID, String defualt) {
        if(messages.containsKey(ID)) return messages.get(ID).replaceAll("(&([a-f0-9]))", "\u00A7$2");
        return defualt.replaceAll("(&([a-f0-9]))", "\u00A7$2");

    }

    public static String getSingleMessage(String ID, String defualt, int i) {
        if(messages.containsKey(ID)) return messages.get(ID).replaceAll("(&([a-f0-9]))", "\u00A7$2").replaceAll("%NUMBER%", Integer.toString(i));
        return defualt.replaceAll("(&([a-f0-9]))", "\u00A7$2").replaceAll("%NUMBER%", Integer.toString(i));

    }

    public static String getSingleMessage(String ID, String defualt, Player player, int i) {
        if(messages.containsKey(ID))
            return messages.get(ID).replaceAll("(&([a-f0-9]))", "\u00A7$2").replaceAll("%NUMBER%", Integer.toString(i)).replaceAll("%PLAYER%", player.getDisplayName());
        return defualt.replaceAll("(&([a-f0-9]))", "\u00A7$2").replaceAll("%NUMBER%", Integer.toString(i)).replaceAll("%PLAYER%", player.getDisplayName());

    }

    public static String getSingleMessage(String ID, String defualt, OfflinePlayer player, int i) {
        if(messages.containsKey(ID))
            return messages.get(ID).replaceAll("(&([a-f0-9]))", "\u00A7$2").replaceAll("%NUMBER%", Integer.toString(i)).replaceAll("%PLAYER%", player.getName());
        return defualt.replaceAll("(&([a-f0-9]))", "\u00A7$2").replaceAll("%NUMBER%", Integer.toString(i)).replaceAll("%PLAYER%", player.getName());

    }

    public static void registerMessage(String ID, String message) {
        messages.put(ID, message);
    }

    public void broadcastMessage(String ID) {
        if(messages.containsKey(ID)) {
            String message = formatMessage(messages.get(ID));
            for(Player player : gameInstance.getPlayers()) {
                player.sendMessage(getPrefix(gameInstance.getPlugin()) + message);
            }
        } else {
            for(Player p : gameInstance.getPlayers()) {
                p.sendMessage(getPrefix(gameInstance.getPlugin()) + ID);
            }
        }

    }

    public String getPrefix(GameAPI plugin) {
        //return PREFIX + "[" + GAMENAME + "] " + NORMAL;
        return prefix;
    }

    public void broadcastMessage(String ID, String defaultmessage) {
        if(messages.containsKey(ID)) {
            broadcastMessage(ID);
        } else {
            getFromLanguageConfig(ID, defaultmessage);

            broadcastMessage(ID);
        }

    }

    public void broadcastMessage(String ID, String defaultmessage, OfflinePlayer player) {
        if(messages.containsKey(ID)) {
            broadcastMessage(ID, player);
        } else {
            getFromLanguageConfig(ID, defaultmessage);

            broadcastMessage(ID, player);
        }

    }

    public void broadcastMessage(String ID, String defaultmessage, int integer) {
        if(messages.containsKey(ID)) {
            broadcastMessage(ID, integer);
        } else {
            getFromLanguageConfig(ID, defaultmessage);

            broadcastMessage(ID, integer);
        }

    }

    public void broadcastJoinMessage(Player p) {
        if(messages.containsKey("JoinMessage")) {
            gameInstance.getChatManager().broadcastMessage("JoinMessage", p);
        } else if(messages.containsKey("Join")) {
            gameInstance.getChatManager().broadcastMessage("Join", p);
        } else {
            gameInstance.getChatManager().broadcastMessage(this.HIGHLIGHTED + p.getName() + this.NORMAL + " joined the Game! (" + gameInstance.getPlayers().size() + "/" + gameInstance.getMAX_PLAYERS() + ")");
        }
    }

    public void broadcastLeaveMessage(Player p) {
        if(messages.containsKey("LeaveMessage")) {
            gameInstance.getChatManager().broadcastMessage("LeaveMessage", p);
        } else if(messages.containsKey("Leave")) {
            gameInstance.getChatManager().broadcastMessage("Leave", p);
        } else {
            gameInstance.getChatManager().broadcastMessage(this.HIGHLIGHTED + p.getName() + this.NORMAL + " left the Game! (" + gameInstance.getPlayers().size() + "/" + gameInstance.getMAX_PLAYERS() + ")");
        }
    }

    public void broadcastMessage(String messageID, Player player) {
        String message = formatMessage(messages.get(messageID), player);
        for(Player player1 : gameInstance.getPlayers()) {
            player1.sendMessage(getPrefix(gameInstance.getPlugin()) + message);
        }
    }

    public void broadcastMessage(String messageID, OfflinePlayer player) {
        String message = formatMessage(messages.get(messageID), player);
        for(Player player1 : gameInstance.getPlayers()) {
            player1.sendMessage(getPrefix(gameInstance.getPlugin()) + message);
        }
    }

    public void broadcastMessage(String messageID, int integer) {
        String message = formatMessage(messages.get(messageID), integer);
        for(Player player1 : gameInstance.getPlayers()) {
            player1.sendMessage(getPrefix(gameInstance.getPlugin()) + message);
        }
    }

    private String formatMessage(String message, int integer) {
        String returnstring = message;
        returnstring = returnstring.replaceAll("%NUMBER%", Integer.toString(integer));

        returnstring = returnstring.replaceAll("%TIME%", Integer.toString(gameInstance.getTimer()));
        returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((gameInstance.getTimer())));
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(gameInstance.getPlayers().size()));
        returnstring = returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(gameInstance.getMAX_PLAYERS()));
        returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(gameInstance.getMIN_PLAYERS()));

        returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
        returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
        returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
        returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
        returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());
        return returnstring;
    }

    public String getMessage(String ID) {

        String message = messages.get(ID);
        return formatMessage(message);
    }

    public String getMessage(String ID, String defaultmessage) {
        if(messages.containsKey(ID)) {
            return getMessage(ID);
        } else {
            ChatManager.getFromLanguageConfig(ID, defaultmessage);
            return getMessage(ID);
        }
    }

    public String getMessage(String ID, String defaultmessage, Player player) {
        if(messages.containsKey(ID)) {
            return getMessage(ID, player);
        } else {
            ChatManager.getFromLanguageConfig(ID, defaultmessage);
            return getMessage(ID, player);
        }
    }

    public String getMessage(String ID, String defaultmessage, OfflinePlayer player) {
        if(gameInstance.plugin.getPlugin().getServer().getPlayer(player.getUniqueId()) != null) {

            if(messages.containsKey(ID)) {
                return getMessage(ID, gameInstance.plugin.getPlugin().getServer().getPlayer(player.getUniqueId()));
            } else {
                ChatManager.getFromLanguageConfig(ID, defaultmessage);
                return getMessage(ID, gameInstance.plugin.getPlugin().getServer().getPlayer(player.getUniqueId()));
            }
        } else {
            if(messages.containsKey(ID)) {
                return getMessage(ID, gameInstance.plugin.getPlugin().getServer().getOfflinePlayer(player.getUniqueId()));
            } else {
                ChatManager.getFromLanguageConfig(ID, defaultmessage);
                return getMessage(ID, gameInstance.plugin.getPlugin().getServer().getOfflinePlayer(player.getUniqueId()));
            }
        }
    }

    public String getMessage(String ID, Player player) {
        return formatMessage(messages.get(ID), player);
    }

    public String getMessage(String ID, OfflinePlayer playername) {
        return formatMessage(messages.get(ID), playername.getName());
    }

    private String formatMessage(String message) {
        String returnstring = message;

        returnstring = returnstring.replaceAll("%TIME%", Integer.toString(gameInstance.getTimer()));
        returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((gameInstance.getTimer())));
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(gameInstance.getPlayers().size()));
        returnstring = returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(gameInstance.getMAX_PLAYERS()));
        returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(gameInstance.getMIN_PLAYERS()));
        returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
        returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
        returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
        returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
        returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());
        return returnstring;
    }

    public String formatMessage(String message, Player player) {
        String returnstring = message;
        returnstring = returnstring.replaceAll("%PLAYER%", player.getName());
        returnstring = returnstring.replaceAll("%TIME%", Integer.toString(gameInstance.getTimer()));
        returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((gameInstance.getTimer())));
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(gameInstance.getPlayers().size()));
        returnstring = returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(gameInstance.getMAX_PLAYERS()));
        returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(gameInstance.getMIN_PLAYERS()));
        returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
        returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
        returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
        returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
        returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());
        return returnstring;
    }

    public String formatMessage(String message, OfflinePlayer player) {
        String returnstring = message;
        returnstring = returnstring.replaceAll("%PLAYER%", player.getName());
        returnstring = returnstring.replaceAll("%TIME%", Integer.toString(gameInstance.getTimer()));
        returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((gameInstance.getTimer())));
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(gameInstance.getPlayers().size()));
        returnstring = returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(gameInstance.getMAX_PLAYERS()));
        returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(gameInstance.getMIN_PLAYERS()));
        returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
        returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
        returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
        returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
        returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());
        return returnstring;
    }

    public String formatMessage(String message, String playername) {
        String returnstring = message;
        returnstring = returnstring.replaceAll("%PLAYER%", playername);
        returnstring = returnstring.replaceAll("%TIME%", Integer.toString(gameInstance.getTimer()));
        returnstring = returnstring.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((gameInstance.getTimer())));
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        returnstring = returnstring.replaceAll("%PLAYERSIZE%", Integer.toString(gameInstance.getPlayers().size()));
        returnstring = returnstring.replaceAll("%MAXPLAYERS%", Integer.toString(gameInstance.getMAX_PLAYERS()));
        returnstring = returnstring.replaceAll("%MINPLAYERS%", Integer.toString(gameInstance.getMIN_PLAYERS()));
        returnstring = returnstring.replaceAll("&l", ChatColor.BOLD.toString());
        returnstring = returnstring.replaceAll("&n", ChatColor.UNDERLINE.toString());
        returnstring = returnstring.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
        returnstring = returnstring.replaceAll("&r", ChatColor.RESET.toString());
        returnstring = returnstring.replaceAll("&k", ChatColor.MAGIC.toString());
        return returnstring;
    }

    public void loadMessages() {
        for(String path : config.getKeys(false)) {
            ;
            String string = config.getString(path);
            messages.put(path, string);
        }

    }


}
