/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.arena.impl.BaseArena;
import pl.plajer.buildbattle.handlers.language.LanguageManager;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.utils.MinigameUtils;

/**
 * Created by Tom on 27/07/2014.
 */
public class ChatManager {

  private static String prefix;

  public ChatManager(String prefix) {
    ChatManager.prefix = prefix;
  }

  /**
   * @return game prefix
   */
  public static String getPrefix() {
    return prefix;
  }

  public static void broadcast(BaseArena arena, String message) {
    for (Player p : arena.getPlayers()) {
      p.sendMessage(prefix + message);
    }
  }

  public static String colorMessage(String message) {
    try {
      return ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageMessage(message));
    } catch (NullPointerException e1) {
      Bukkit.getConsoleSender().sendMessage("Game message not found!");
      if (LanguageManager.isDefaultLanguageUsed()) {
        Bukkit.getConsoleSender().sendMessage("Please regenerate your language.yml file! If error still occurs report it to the developer!");
      } else {
        Bukkit.getConsoleSender().sendMessage("Locale message string not found! Please contact developer!");
        new ReportedException(JavaPlugin.getPlugin(Main.class), e1);
      }
      Bukkit.getConsoleSender().sendMessage("Access string: " + message);
      return "ERR_MESSAGE_NOT_FOUND";
    }
  }

  public static String colorRawMessage(String msg) {
    return ChatColor.translateAlternateColorCodes('&', msg);
  }

  public static String formatMessage(BaseArena arena, String message, Player player) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%PLAYER%", player.getName());
    returnString = colorRawMessage(formatPlaceholders(returnString, arena));
    return returnString;
  }

  private static String formatPlaceholders(String message, BaseArena arena) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%TIME%", Integer.toString(arena.getTimer()));
    returnString = StringUtils.replace(returnString, "%FORMATTEDTIME%", MinigameUtils.formatIntoMMSS((arena.getTimer())));
    returnString = StringUtils.replace(returnString, "%PLAYERSIZE%", Integer.toString(arena.getPlayers().size()));
    returnString = StringUtils.replace(returnString, "%MAXPLAYERS%", Integer.toString(arena.getMaximumPlayers()));
    returnString = StringUtils.replace(returnString, "%MINPLAYERS%", Integer.toString(arena.getMinimumPlayers()));
    return returnString;
  }

  public static void broadcastAction(BaseArena arena, Player p, ActionType action) {
    switch (action) {
      case JOIN:
        broadcast(arena, formatMessage(arena, ChatManager.colorMessage("In-Game.Messages.Join"), p));
        break;
      case LEAVE:
        broadcast(arena, formatMessage(arena, ChatManager.colorMessage("In-Game.Messages.Leave"), p));
        break;
    }
  }

  public enum ActionType {
    JOIN, LEAVE
  }

}
