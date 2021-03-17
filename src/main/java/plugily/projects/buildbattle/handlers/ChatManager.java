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

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion;
import pl.plajerlair.commonsbox.minecraft.misc.MiscUtils;
import pl.plajerlair.commonsbox.string.StringFormatUtils;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.handlers.language.LanguageManager;

/**
 * Created by Tom on 27/07/2014.
 */
public class ChatManager {

  private final String prefix;

  public ChatManager(String prefix) {
    this.prefix = colorRawMessage(prefix);
  }

  private static String formatPlaceholders(String message, BaseArena arena) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%ARENANAME%", arena.getMapName());
    returnString = StringUtils.replace(returnString, "%TIME%", Integer.toString(arena.getTimer()));
    returnString = StringUtils.replace(returnString, "%FORMATTEDTIME%", StringFormatUtils.formatIntoMMSS((arena.getTimer())));
    returnString = StringUtils.replace(returnString, "%PLAYERSIZE%", Integer.toString(arena.getPlayers().size()));
    returnString = StringUtils.replace(returnString, "%MAXPLAYERS%", Integer.toString(arena.getMaximumPlayers()));
    returnString = StringUtils.replace(returnString, "%MINPLAYERS%", Integer.toString(arena.getMinimumPlayers()));
    return returnString;
  }

  /**
   * @return game prefix
   */
  public String getPrefix() {
    return prefix;
  }

  public void broadcast(BaseArena arena, String message) {
    if(message.isEmpty()) {
      return;
    }

    for(Player p : arena.getPlayers()) {
      p.sendMessage(prefix + message);
    }
  }

  public String colorMessage(String message) {
    return message == null ? "" : colorRawMessage(LanguageManager.getLanguageMessage(message));
  }

  public String colorRawMessage(String msg) {
    if(msg == null) {
      return "";
    }

    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_16_R1) && msg.contains("#")) {
      msg = MiscUtils.matchColorRegex(msg);
    }

    return ChatColor.translateAlternateColorCodes('&', msg);
  }

  public String formatMessage(BaseArena arena, String message, Player player) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%PLAYER%", player.getName());
    returnString = colorRawMessage(formatPlaceholders(returnString, arena));
    return returnString;
  }

  public void broadcastAction(BaseArena arena, Player p, ActionType action) {
    switch(action) {
      case JOIN:
        broadcast(arena, formatMessage(arena, colorMessage("In-Game.Messages.Join"), p));
        break;
      case LEAVE:
        broadcast(arena, formatMessage(arena, colorMessage("In-Game.Messages.Leave"), p));
        break;
      default:
        break;
    }
  }

  public enum ActionType {
    JOIN, LEAVE
  }

}
