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
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.utils.MessageUtils;
import pl.plajerlair.core.services.LocaleService;
import pl.plajerlair.core.services.ServiceRegistry;
import pl.plajerlair.core.utils.ConfigUtils;

/**
 * @author Plajer
 * <p>
 * Created at 13.05.2018
 */
public class LanguageManager {

  private static Main plugin;
  private static Locale pluginLocale = null;
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
    LocaleService service = ServiceRegistry.getLocaleService(plugin);
    if (service.isValidVersion()) {
      LocaleService.DownloadStatus status = service.demandLocaleDownload(pluginLocale.getPrefix());
      if (status == LocaleService.DownloadStatus.FAIL) {
        pluginLocale = Locale.ENGLISH;
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildBattle] Locale service couldn't download latest locale for plugin! English locale will be used instead!");
        return;
      } else if (status == LocaleService.DownloadStatus.SUCCESS) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildBattle] Downloaded locale " + pluginLocale.getPrefix() + " properly!");
      } else if (status == LocaleService.DownloadStatus.LATEST) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildBattle] Locale " + pluginLocale.getPrefix() + " is latest! Awesome!");
      }
    } else {
      pluginLocale = Locale.ENGLISH;
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildBattle] Your plugin version is too old to use latest locale! Please update plugin to access latest updates of locale!");
      return;
    }
    try {
      properties.load(new FileReader(new File(plugin.getDataFolder() + "/locales/" + pluginLocale.getPrefix() + ".properties")));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void setupLocale() {
    String localeName = plugin.getConfig().getString("locale", "default").toLowerCase();
    for (Locale locale : Locale.values()) {
      for (String alias : locale.getAliases()) {
        if (alias.equals(localeName)) {
          pluginLocale = locale;
          break;
        }
      }
    }
    if (pluginLocale == null) {
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildBattle] Plugin locale is invalid! Using default one...");
      pluginLocale = Locale.ENGLISH;
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
          case "tr-TR":
          case "ru-RU":
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
          case "ID":
          case "TR":
          case "RU":
          case "ET":
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
    return ConfigUtils.getConfig(plugin, "language");
  }

  public static String getDefaultLanguageMessage(String message) {
    if (ConfigUtils.getConfig(plugin, "language").isSet(message)) {
      return ConfigUtils.getConfig(plugin, "language").getString(message);
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
    return ConfigUtils.getConfig(plugin, "language").getString(message);
  }

  public static Locale getPluginLocale() {
    return pluginLocale;
  }

}
