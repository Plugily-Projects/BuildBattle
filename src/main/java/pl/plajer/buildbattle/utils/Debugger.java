/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2020 Plugily Projects - maintained by Tigerpanzer_02, 2Wild4You and contributors
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

package pl.plajer.buildbattle.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * @author Plajer
 * <p>
 * Created at 28.04.2019
 */
public class Debugger {

  private static boolean enabled = false;
  private static String prefix = "[Build Battle Debugger]";

  public static void setEnabled(boolean enabled) {
    Debugger.enabled = enabled;
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
        Bukkit.getConsoleSender().sendMessage(prefix + " " + thing);
        break;
      case WARN:
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + prefix + " " + thing);
        break;
      case ERROR:
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + prefix + " " + thing);
        break;
      case WTF:
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + prefix + " [SEVERE]" + thing);
        break;
      case TASK:
        if (!enabled) {
          return;
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + prefix + " Running task '" + thing + "'");
        break;
    }
  }

  public enum Level {
    INFO, WARN, ERROR, WTF, TASK
  }

}
