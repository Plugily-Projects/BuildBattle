/*
 * BuildBattle 4 - Ultimate building competition minigame
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.buildbattle4.handlers.language;

import com.earth2me.essentials.Essentials;
import com.wasteofplastic.askyblock.ASLocale;
import com.wasteofplastic.askyblock.ASkyBlock;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import pl.plajer.buildbattle4.Main;
import pl.plajer.buildbattle4.handlers.ChatManager;
import pl.plajer.buildbattle4.utils.MessageUtils;
import pl.plajerlair.core.services.ServiceRegistry;
import pl.plajerlair.core.services.locale.Locale;
import pl.plajerlair.core.services.locale.LocaleRegistry;
import pl.plajerlair.core.services.locale.LocaleService;
import pl.plajerlair.core.utils.ConfigUtils;

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
    registerLocales();
    setupLocale();
    LanguageMigrator.configUpdate();
    LanguageMigrator.languageFileUpdate();
    //we will wait until server is loaded, we won't soft depend those plugins
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      if (isDefaultLanguageUsed()) {
        suggestLocale();
      }
    }, 100);
  }

  private static void registerLocales() {
    LocaleRegistry.registerLocale(new pl.plajerlair.core.services.locale.Locale("Chinese (Simplified)", "简体中文", "zh_Hans", "POEditor contributors (Haoting)", Arrays.asList("简体中文", "中文", "chinese", "zh")));
    LocaleRegistry.registerLocale(new pl.plajerlair.core.services.locale.Locale("Czech", "Český", "cs_CZ", "POEditor contributors", Arrays.asList("czech", "cesky", "český", "cs")));
    LocaleRegistry.registerLocale(new pl.plajerlair.core.services.locale.Locale("English", "English", "en_GB", "Plajer", Arrays.asList("default", "english", "en")));
    LocaleRegistry.registerLocale(new pl.plajerlair.core.services.locale.Locale("Estonian", "Eesti", "et_EE", "POEditor contributors (kaimokene)", Arrays.asList("estonian", "eesti", "et")));
    LocaleRegistry.registerLocale(new pl.plajerlair.core.services.locale.Locale("French", "Français", "fr_FR", "POEditor contributors", Arrays.asList("french", "francais", "français", "fr")));
    LocaleRegistry.registerLocale(new pl.plajerlair.core.services.locale.Locale("German", "Deutsch", "de_DE", "Tigerkatze and POEditor contributors", Arrays.asList("deutsch", "german", "de")));
    LocaleRegistry.registerLocale(new pl.plajerlair.core.services.locale.Locale("Hungarian", "Magyar", "hu_HU", "POEditor contributors (montlikadani)", Arrays.asList("hungarian", "magyar", "hu")));
    LocaleRegistry.registerLocale(new pl.plajerlair.core.services.locale.Locale("Indonesian", "Indonesia", "id_ID", "POEditor contributors", Arrays.asList("indonesian", "indonesia", "id")));
    LocaleRegistry.registerLocale(new pl.plajerlair.core.services.locale.Locale("Korean", "한국의", "ko_KR", "POEditor contributors", Arrays.asList("korean", "한국의", "kr")));
    LocaleRegistry.registerLocale(new pl.plajerlair.core.services.locale.Locale("Polish", "Polski", "pl_PL", "Plajer", Arrays.asList("polish", "polski", "pl")));
    LocaleRegistry.registerLocale(new pl.plajerlair.core.services.locale.Locale("Romanian", "Românesc", "ro_RO", "POEditor contributors (Andrei)", Arrays.asList("romanian", "romanesc", "românesc", "ro")));
    LocaleRegistry.registerLocale(new pl.plajerlair.core.services.locale.Locale("Russian", "Pусский", "ru_RU", "POEditor contributors (Mrake)", Arrays.asList("russian", "russkiy", "pусский", "ru")));
    LocaleRegistry.registerLocale(new pl.plajerlair.core.services.locale.Locale("Spanish", "Español", "es_ES", "POEditor contributors", Arrays.asList("spanish", "espanol", "español", "es")));
    LocaleRegistry.registerLocale(new pl.plajerlair.core.services.locale.Locale("Turkish", "Türk", "tr_TR", "POEditor contributors", Arrays.asList("turkish", "turk", "türk", "tr")));
    LocaleRegistry.registerLocale(new pl.plajerlair.core.services.locale.Locale("Vietnamese", "Việt", "vn_VN", "POEditor contributors (HStreamGamer)", Arrays.asList("vietnamese", "viet", "việt", "vn")));
  }

  private static void loadProperties() {
    LocaleService service = ServiceRegistry.getLocaleService(plugin);
    if (service.isValidVersion()) {
      LocaleService.DownloadStatus status = service.demandLocaleDownload(pluginLocale.getPrefix());
      if (status == LocaleService.DownloadStatus.FAIL) {
        pluginLocale = LocaleRegistry.getByName("English");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildBattle] Locale service couldn't download latest locale for plugin! English locale will be used instead!");
        return;
      } else if (status == LocaleService.DownloadStatus.SUCCESS) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildBattle] Downloaded locale " + pluginLocale.getPrefix() + " properly!");
      } else if (status == LocaleService.DownloadStatus.LATEST) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildBattle] Locale " + pluginLocale.getPrefix() + " is latest! Awesome!");
      }
    } else {
      pluginLocale = LocaleRegistry.getByName("English");
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
    for (Locale locale : LocaleRegistry.getRegisteredLocales()) {
      for (String alias : locale.getAliases()) {
        if (alias.equals(localeName)) {
          pluginLocale = locale;
          break;
        }
      }
    }
    if (pluginLocale == null) {
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildBattle] Plugin locale is invalid! Using default one...");
      pluginLocale = LocaleRegistry.getByName("English");
    }
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[BuildBattle] Loaded locale " + pluginLocale.getName() + " (" + pluginLocale.getOriginalName() + " ID: " +
        pluginLocale.getPrefix() + ") by " + pluginLocale.getAuthor());
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
          case "cs-CS":
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
          case "CS":
          case "RO":
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

  public static boolean isDefaultLanguageUsed() {
    return pluginLocale.getName().equals("English");
  }

  public static List<String> getLanguageList(String path) {
    if (isDefaultLanguageUsed()) {
      return ConfigUtils.getConfig(plugin, "language").getStringList(path);
    } else {
      return Arrays.asList(ChatManager.colorMessage(path).split(";"));
    }
  }

  public static String getLanguageMessage(String message) {
    if (isDefaultLanguageUsed()) {
      return ConfigUtils.getConfig(plugin, "language").getString(message, "ERR_MESSAGE_NOT_FOUND");
    }
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

  public static Locale getPluginLocale() {
    return pluginLocale;
  }

}
