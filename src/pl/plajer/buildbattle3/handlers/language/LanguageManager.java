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

package pl.plajer.buildbattle3.handlers.language;

import com.earth2me.essentials.Essentials;
import com.wasteofplastic.askyblock.ASLocale;
import com.wasteofplastic.askyblock.ASkyBlock;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.handlers.ConfigurationManager;
import pl.plajer.buildbattle3.utils.MessageUtils;

/**
 * @author Plajer
 * <p>
 * Created at 13.05.2018
 */
public class LanguageManager {

  private static Main plugin;
  private static Locale pluginLocale;
  private static Properties properties = new Properties();

  public static void init(Main pl) {
    plugin = pl;
    if (!new File(plugin.getDataFolder() + File.separator + "language.yml").exists()) {
      plugin.saveResource("language.yml", false);
    }
    setupLocale();
    LanguageMigrator.configUpdate();
    LanguageMigrator.languageFileUpdate();
    //we will wait until server is loaded, we won't soft depend those plugins
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      if (pluginLocale == Locale.ENGLISH) suggestLocale();
    }, 100);
  }

  private static void loadProperties() {
    if (pluginLocale == Locale.ENGLISH) return;
    try {
      properties.load(new InputStreamReader(plugin.getResource("locales/" + pluginLocale.getPrefix() + ".properties"), Charset.forName("UTF-8")));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void setupLocale() {
    String locale = plugin.getConfig().getString("locale", "default");
    switch (locale.toLowerCase()) {
      case "简体中文":
      case "中文":
      case "chinese":
      case "zh":
        pluginLocale = Locale.CHINESE_SIMPLIFIED;
        break;
      case "default":
      case "english":
      case "en":
        pluginLocale = Locale.ENGLISH;
        break;
      case "german":
      case "deutsch":
      case "de":
        pluginLocale = Locale.GERMAN;
        break;
      case "hungarian":
      case "magyar":
      case "hu":
        pluginLocale = Locale.HUNGARIAN;
        break;
      case "french":
      case "francais":
      case "français":
      case "fr":
        pluginLocale = Locale.FRENCH;
        break;
      case "korean":
      case "한국의":
      case "kr":
        pluginLocale = Locale.KOREAN;
        break;
      case "polish":
      case "polski":
      case "pl":
        pluginLocale = Locale.POLISH;
        break;
      case "spanish":
      case "espanol":
      case "español":
      case "es":
        pluginLocale = Locale.SPANISH;
        break;
      case "vietnamese":
      case "việt":
      case "viet":
      case "vn":
        pluginLocale = Locale.VIETNAMESE;
        break;
      default:
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildBattle] Plugin locale is invalid! Using default one...");
        pluginLocale = Locale.ENGLISH;
        break;
    }
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[BuildBattle] Loaded locale " + pluginLocale.getFormattedName() + " (" + pluginLocale.getPrefix() + ") by " + pluginLocale.getAuthor());
    loadProperties();
  }

  private static void suggestLocale() {
    //we will catch any exceptions in case of api changes
    boolean hasLocale = false;
    String localeName = "";
    try {
      if (plugin.getServer().getPluginManager().isPluginEnabled("ASkyBlock")) {
        ASLocale locale = ASkyBlock.getPlugin().myLocale();
        switch (locale.getLocaleName()) {
          case "pl-PL":
          case "es-ES":
          case "de-DE":
          case "zh-CN":
          case "vn-VN":
          case "hu-HU":
          case "ko-KR":
          case "fr-FR":
            hasLocale = true;
            localeName = locale.getLocaleName();
        }
      }
      if (plugin.getServer().getPluginManager().isPluginEnabled("Essentials")) {
        Essentials ess = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
        java.util.Locale locale = ess.getI18n().getCurrentLocale();
        switch (locale.getCountry()) {
          case "PL":
          case "ES":
          case "DE":
          case "HU":
          case "VN":
          case "ZH":
          case "KR":
          case "FR":
            hasLocale = true;
            localeName = locale.getDisplayName();
        }
      }
    } catch (Exception e) {
      Main.debug("[WARN] Plugin has occured a problem suggesting locale, probably API change.", System.currentTimeMillis());
    }
    if (hasLocale) {
      MessageUtils.info();
      Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[BuildBattle] We've found that you use locale " + localeName + " in other plugins.");
      Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "We recommend you to change plugin's locale to " + localeName + " to have best plugin experience.");
      Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "You can change plugin's locale in config.yml in locale section!");
    }
  }

  //todo do something with me
  public static FileConfiguration getLanguageFile() {
    return ConfigurationManager.getConfig("language");
  }

  public static String getDefaultLanguageMessage(String message) {
    if (ConfigurationManager.getConfig("language").isSet(message)) {
      return ConfigurationManager.getConfig("language").getString(message);
    }
    MessageUtils.errorOccured();
    Bukkit.getConsoleSender().sendMessage("Game message not found!");
    Bukkit.getConsoleSender().sendMessage("Please regenerate your language.yml file! If error still occurs report it to the developer!");
    Bukkit.getConsoleSender().sendMessage("Access string: " + message);
    return "ERR_MESSAGE_NOT_FOUND";
  }

  public static String getLanguageMessage(String message) {
    if (pluginLocale != Locale.ENGLISH) {
      try {
        return properties.getProperty(ChatColor.translateAlternateColorCodes('&', message));
      } catch (NullPointerException ex) {
        MessageUtils.errorOccured();
        Bukkit.getConsoleSender().sendMessage("Game message not found!");
        Bukkit.getConsoleSender().sendMessage("Please regenerate your language.yml file! If error still occurs report it to the developer!");
        Bukkit.getConsoleSender().sendMessage("Access string: " + message);
        return "ERR_MESSAGE_NOT_FOUND";
      }
    }
    return ConfigurationManager.getConfig("language").getString(message);
  }

  public static Locale getPluginLocale() {
    return pluginLocale;
  }

}
