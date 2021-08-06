/*
 *
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2021 Plugily Projects - maintained by Tigerpanzer_02, 2Wild4You and contributors
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
 *
 */

package plugily.projects.buildbattle.handlers;

import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import plugily.projects.commonsbox.minecraft.compat.ServerVersion;
import plugily.projects.commonsbox.minecraft.misc.MiscUtils;
import plugily.projects.commonsbox.string.StringFormatUtils;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.handlers.language.LanguageManager;

/**
 * Created by Tom on 27/07/2014.
 */
public class ChatManager {

  private final String pluginPrefix;
  private final Main plugin;

  public ChatManager(Main plugin) {
    this.plugin = plugin;
    this.pluginPrefix = colorMessage("In-Game.Plugin-Prefix");
  }

  /**
   * @return game prefix
   */
  public String getPrefix() {
    return pluginPrefix;
  }

  public String colorMessage(String path) {
    return path == null ? "" : colorRawMessage(LanguageManager.getLanguageMessage(path));
  }

  public String colorRawMessage(String msg) {
    if(msg == null) {
      return "";
    }

    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_16_R1) && msg.indexOf('#') >= 0) {
      msg = MiscUtils.matchColorRegex(msg);
    }

    return ChatColor.translateAlternateColorCodes('&', msg);
  }

  public void broadcast(BaseArena arena, String message) {
    if(message != null && !message.isEmpty()) {
      for(Player p : arena.getPlayers()) {
        p.sendMessage(pluginPrefix + message);
      }
    }
  }

  public String formatMessage(BaseArena arena, String message, Player player) {
    String returnString = message;

    returnString = StringUtils.replace(returnString, "%PLAYER%", player.getName());

    returnString = formatPlaceholders(returnString, arena);

    if(plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      returnString = PlaceholderAPI.setPlaceholders(player, returnString);
    }

    return colorRawMessage(formatPlaceholders(returnString, arena));
  }

  private static String formatPlaceholders(String message, BaseArena arena) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%ARENANAME%", arena.getMapName());

    int timer = arena.getTimer();

    returnString = StringUtils.replace(returnString, "%TIME%", Integer.toString(timer));
    returnString = StringUtils.replace(returnString, "%FORMATTEDTIME%", StringFormatUtils.formatIntoMMSS(timer));
    returnString = StringUtils.replace(returnString, "%PLAYERSIZE%", Integer.toString(arena.getPlayers().size()));
    returnString = StringUtils.replace(returnString, "%MAXPLAYERS%", Integer.toString(arena.getMaximumPlayers()));
    returnString = StringUtils.replace(returnString, "%MINPLAYERS%", Integer.toString(arena.getMinimumPlayers()));
    return returnString;
  }

  public void broadcastAction(BaseArena arena, Player player, ActionType action) {
    String path;
    switch(action) {
      case JOIN:
        path = "In-Game.Messages.Join";
        break;
      case LEAVE:
        path = "In-Game.Messages.Leave";
        break;
      default:
        return; //likely won't ever happen
    }
    broadcast(arena, formatMessage(arena, colorMessage(path), player));
  }

  public enum ActionType {
    JOIN, LEAVE
  }

}
