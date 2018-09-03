/*
 * BuildBattle 3 - Ultimate building competition minigame
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

package pl.plajer.buildbattle3.plajerlair.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Check updates of plugin
 */
public class UpdateChecker {

  private static String latestVersion;

  private static boolean checkHigher(String currentVersion, String newVersion) {
    String current = InternalUtils.toReadable(currentVersion);
    String newVer = InternalUtils.toReadable(newVersion);
    return current.compareTo(newVer) < 0;
  }

  /**
   * Check current version of plugin at SpigotMC website
   * if version is beta (contains "b") plugin will check config of requesting plugin to check if
   * "Update-Notifier.Notify-Beta-Versions" is true
   *
   * @param plugin         requesting plugin
   * @param currentVersion current plugin version from plugin.yml
   * @param resourceID     spigotmc resource ID to check
   * @return true if there is update false otherwise
   * @see #getLatestVersion() when return is true to get it
   */
  public static boolean checkUpdate(JavaPlugin plugin, String currentVersion, int resourceID) {
    String version = getVersion(resourceID);
    if (version.contains("b")) {
      if (!plugin.getConfig().getBoolean("Update-Notifier.Notify-Beta-Versions", true)) {
        return false;
      }
    }
    if (checkHigher(currentVersion, version)) {
      latestVersion = version;
      return true;
    }
    return false;
  }

  /**
   * Get latest version of plugin from spigotmc
   *
   * @return latest version from spigotmc, return null when version is same (latest)
   */
  public static String getLatestVersion() {
    return latestVersion;
  }

  private static String getVersion(int ver) {
    String version = null;
    try {
      HttpURLConnection con = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=" + ver).openConnection();
      con.setDoOutput(true);
      con.setRequestMethod("POST");
      con.getOutputStream().write(("resource=" + ver).getBytes("UTF-8"));
      version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
    } catch (IOException ignored) {
    }
    return version;
  }

}
