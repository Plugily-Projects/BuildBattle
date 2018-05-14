/*
 *  Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

    public static String PREFIX;

    public ChatManager() {
        PREFIX = colorMessage("In-Game.Plugin-Prefix");
    }

    public static String colorMessage(String msg) {
        return ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageMessage(msg));
    }

    public static String colorRawMessage(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String formatMessage(Arena arena, String message, Player player) {
        String returnString = message;
        returnString = returnString.replaceAll("%PLAYER%", player.getName());
        returnString = returnString.replaceAll("%TIME%", Integer.toString(arena.getTimer()));
        returnString = returnString.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((arena.getTimer())));
        returnString = returnString.replaceAll("%PLAYERSIZE%", Integer.toString(arena.getPlayers().size()));
        returnString = returnString.replaceAll("%MAXPLAYERS%", Integer.toString(arena.getMaximumPlayers()));
        returnString = returnString.replaceAll("%MINPLAYERS%", Integer.toString(arena.getMinimumPlayers()));
        //todo remember
        returnString = colorRawMessage(returnString);
        return returnString;
    }

    public static String formatMessage(String message, Arena arena) {
        String returnString = message;
        returnString = returnString.replaceAll("%TIME%", Integer.toString(arena.getTimer()));
        returnString = returnString.replaceAll("%FORMATTEDTIME%", Util.formatIntoMMSS((arena.getTimer())));
        returnString = returnString.replaceAll("%PLAYERSIZE%", Integer.toString(arena.getPlayers().size()));
        returnString = returnString.replaceAll("%MAXPLAYERS%", Integer.toString(arena.getMaximumPlayers()));
        returnString = returnString.replaceAll("%MINPLAYERS%", Integer.toString(arena.getMinimumPlayers()));
        returnString = colorRawMessage(returnString);
        return returnString;
    }

    public static void broadcastAction(Arena arena, Player p, ActionType action) {
        switch(action){
            case JOIN:
                String joinMsg = ChatManager.formatMessage(ChatManager.colorMessage("In-Game.Messages.Join"), arena);
                for(Player player : arena.getPlayers()){
                    player.sendMessage(joinMsg);
                }
                break;
            case LEAVE:
                String leaveMsg = ChatManager.formatMessage(ChatManager.colorMessage("In-Game.Messages.Leave"), arena);
                for(Player player : arena.getPlayers()){
                    player.sendMessage(leaveMsg);
                }
                break;
        }
    }

    public enum ActionType {
        JOIN, LEAVE
    }

}
