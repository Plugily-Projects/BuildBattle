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

package pl.plajer.buildbattle3.plajerlair.core.services;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;

/**
 * Reporter service
 */
public class ReporterService {

  private String plugin;
  private String pluginVersion;
  private String serverVersion;
  private String error;

  //don't create it outside core
  ReporterService(String plugin, String pluginVersion, String serverVersion, String error) {
    this.plugin = plugin;
    this.pluginVersion = pluginVersion;
    this.serverVersion = serverVersion;
    this.error = error;
  }

  public void reportException() {
    try {
      URL url = new URL("https://plajer.xyz/errorservice/report.php");
      HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("User-Agent", "Mozilla/5.0");
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      conn.setDoOutput(true);

      OutputStream os = conn.getOutputStream();
      os.write(("pass=servicereporter&type=" + plugin + "&pluginversion=" + pluginVersion + "&serverversion=" + serverVersion + "&error=" + error).getBytes("UTF-8"));
      os.flush();
      os.close();

      Bukkit.getConsoleSender().sendMessage("[Reporter service] Error reported! Code: " + conn.getResponseCode() + " (" + conn.getResponseMessage() + ")");
    } catch (IOException ignored) {/*cannot connect or there is a problem*/}
  }

}
