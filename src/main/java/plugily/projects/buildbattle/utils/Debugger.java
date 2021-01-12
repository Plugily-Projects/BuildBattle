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

package plugily.projects.buildbattle.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion;
import pl.plajerlair.commonsbox.minecraft.misc.MiscUtils;

/**
 * @author Plajer
 * <p>
 * Created at 28.04.2019
 */
public class Debugger {

  private static boolean enabled = false;
  private static final String prefix = "[Build Battle Debugger]";

  public static void setEnabled(boolean enabled) {
    Debugger.enabled = enabled;
  }

  public static void debug(String thing) {
    debug(Level.INFO, thing);
  }

  /**
   * Prints debug message with selected log level.
   * Messages of level INFO or TASK won't be posted if
   * debugger is enabled, warnings and errors will be.
   *
   * @param level level of debugged message
   * @param thing debugged message
   */
  public static void debug(Level level, String thing) {
    switch (level) {
      case INFO:
        if (!enabled) {
          return;
        }
        sendConsoleMsg(prefix + " " + thing);
        break;
      case WARN:
      case ERROR:
        sendConsoleMsg("&e" + prefix + " " + thing);
        break;
      case WTF:
        sendConsoleMsg("&4" + prefix + " [SEVERE]" + thing);
        break;
      case TASK:
        if (!enabled) {
          return;
        }
        sendConsoleMsg("&e" + prefix + " Running task '" + thing + "'");
        break;
    }
  }

  public static void sendConsoleMsg(String msg) {
    if (ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_16_R1) && msg.contains("#")) {
      msg = MiscUtils.matchColorRegex(msg);
    }

    Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
  }

  public enum Level {
    INFO, WARN, ERROR, WTF, TASK
  }

}
