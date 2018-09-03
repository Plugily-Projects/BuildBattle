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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle3.plajerlair.core.utils.ConfigUtils;
import pl.plajer.buildbattle3.plajerlair.core.utils.InternalUtils;

/**
 * Localization service used for fetching latest locales for minigames
 */
public class LocaleService {

  private JavaPlugin plugin;
  private FileConfiguration localeData;

  //don't create it outside core
  LocaleService(JavaPlugin plugin) {
    this.plugin = plugin;
    try {
      String data = IOUtils.toString(requestLocaleFetch(null), StandardCharsets.UTF_8);
      FileUtils.write(new File(plugin.getDataFolder().getPath() + "/locales/data.yml"), data, StandardCharsets.UTF_8);
      if (!new File(plugin.getDataFolder().getPath() + "/locales/origin_data.yml").exists()) {
        FileUtils.write(new File(plugin.getDataFolder().getPath() + "/locales/origin_data.yml"), data, StandardCharsets.UTF_8);
      }
      this.localeData = ConfigUtils.getConfig(plugin, "/locales/data");
      //-1 cause not including "valid-version" param
      plugin.getLogger().log(Level.INFO, "Fetched latest localizations, " + (localeData.getConfigurationSection("locales").getKeys(false).size() - 1) + " locales were fetched.");
    } catch (IOException ignored) {
      //ignore exceptions
      plugin.getLogger().log(Level.WARNING, "Couldn't access locale fetcher service or there is other problem! You should notify author!");
    }
  }

  private InputStream requestLocaleFetch(String locale) {
    try {
      URL url = new URL("https://plajer.xyz/localeservice/fetch.php");
      HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("User-Agent", "Mozilla/5.0");
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      conn.setDoOutput(true);

      OutputStream os = conn.getOutputStream();
      if (locale == null) {
        os.write(("pass=localeservice&type=" + plugin.getName()).getBytes("UTF-8"));
      } else {
        os.write(("pass=localeservice&type=" + plugin.getName() + "&locale=" + locale).getBytes("UTF-8"));
      }
      os.flush();
      os.close();
      return conn.getInputStream();
    } catch (Exception e) {
      e.printStackTrace();
      return new InputStream() {
        @Override
        public int read() {
          return -1;
        }
      };
    }
  }


  /**
   * Sends a demand request to download latest locale from Plajer-Lair/locale_storage repository
   * Whole repository can be seen here https://github.com/Plajer-Lair/locale_storage
   *
   * @param locale locale prefix to download
   * @return SUCCESS for downloaded locale, FAIL for service fault, LATEST when locale is latest as one in repository
   */
  public DownloadStatus demandLocaleDownload(String locale) {
    //service fault
    if (localeData == null) {
      return DownloadStatus.FAIL;
    }
    if (!new File(plugin.getDataFolder() + "/locales/" + locale + ".properties").exists()) {
      return writeFile(locale);
    }
    if (localeData.getInt("locales." + locale + ".version") > ConfigUtils.getConfig(plugin, "/locales/origin_data").getInt("locales." + locale + ".version", -1)) {
      return writeFile(locale);
    } else {
      return DownloadStatus.LATEST;
    }
  }

  private DownloadStatus writeFile(String locale) {
    try {
      String data = IOUtils.toString(requestLocaleFetch(locale), StandardCharsets.UTF_8);
      FileUtils.write(new File(plugin.getDataFolder().getPath() + "/locales/" + locale + ".properties"), data, StandardCharsets.UTF_8);

      FileConfiguration config = ConfigUtils.getConfig(plugin, "/locales/origin_data");
      config.set("locales." + locale, localeData.getInt("locales." + locale, 0));
      ConfigUtils.saveConfig(plugin, config, "/locales/origin_data");
      return DownloadStatus.SUCCESS;
    } catch (IOException ignored) {
      plugin.getLogger().log(Level.WARNING, "Demanded locale " + locale + " cannot be downloaded! You should notify author!");
      return DownloadStatus.FAIL;
    }
  }

  /**
   * Checks if plugin version allows to update locale
   *
   * @return true if locale can be updated for this version else cannot
   */
  public boolean isValidVersion() {
    //service fault
    if (localeData == null) {
      return false;
    }
    return !checkHigher(plugin.getDescription().getVersion(), localeData.getString("locales.valid-version", "0"));
  }

  private boolean checkHigher(String currentVersion, String newVersion) {
    String current = InternalUtils.toReadable(currentVersion);
    String newVer = InternalUtils.toReadable(newVersion);
    return current.compareTo(newVer) < 0;
  }

  /**
   * Returns requested locale version number
   *
   * @param locale locale prefix
   * @return locale version
   */
  public int getLocaleVersion(String locale) {
    //service fault
    if (localeData == null) {
      return 0;
    }
    return localeData.getInt("locales." + locale + ".version", 69);
  }

  /**
   * Download status enum for locale download demands
   */
  public enum DownloadStatus {
    SUCCESS, FAIL, LATEST
  }

}
