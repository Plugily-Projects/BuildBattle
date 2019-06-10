/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and contributors
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

package pl.plajer.buildbattle.handlers.language;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.utils.Debugger;
import pl.plajer.buildbattle.utils.MessageUtils;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajer.buildbattle.utils.services.ServiceRegistry;
import pl.plajer.buildbattle.utils.services.locale.Locale;
import pl.plajer.buildbattle.utils.services.locale.LocaleRegistry;
import pl.plajer.buildbattle.utils.services.locale.LocaleService;

/**
 * @author Plajer
 * <p>
 * Created at 13.05.2018
 */
public class LanguageManager {

  private static Main plugin;
  private static Locale pluginLocale;
  private static Properties properties = new Properties();
  private static FileConfiguration languageConfig;

  public static void init(Main plugin) {
    LanguageManager.plugin = plugin;
    if (!new File(plugin.getDataFolder() + File.separator + "language.yml").exists()) {
      plugin.saveResource("language.yml", false);
    }
    registerLocales();
    setupLocale();
    new LanguageMigrator(plugin);
    //get file after all migrations are done
    languageConfig = ConfigUtils.getConfig(plugin, "language");
  }

  private static void registerLocales() {
    LocaleRegistry.registerLocale(new Locale("Chinese (Simplified)", "简体中文", "zh_Hans", "POEditor contributors (Haoting)", Arrays.asList("简体中文", "中文", "chinese", "zh")));
    LocaleRegistry.registerLocale(new Locale("Czech", "Český", "cs_CZ", "POEditor contributors", Arrays.asList("czech", "cesky", "český", "cs")));
    LocaleRegistry.registerLocale(new Locale("English", "English", "en_GB", "Plajer", Arrays.asList("default", "english", "en")));
    LocaleRegistry.registerLocale(new Locale("Estonian", "Eesti", "et_EE", "POEditor contributors (kaimokene)", Arrays.asList("estonian", "eesti", "et")));
    LocaleRegistry.registerLocale(new Locale("French", "Français", "fr_FR", "POEditor contributors", Arrays.asList("french", "francais", "français", "fr")));
    LocaleRegistry.registerLocale(new Locale("German", "Deutsch", "de_DE", "Tigerkatze and POEditor contributors", Arrays.asList("deutsch", "german", "de")));
    LocaleRegistry.registerLocale(new Locale("Hungarian", "Magyar", "hu_HU", "POEditor contributors (montlikadani)", Arrays.asList("hungarian", "magyar", "hu")));
    LocaleRegistry.registerLocale(new Locale("Indonesian", "Indonesia", "id_ID", "POEditor contributors", Arrays.asList("indonesian", "indonesia", "id")));
    LocaleRegistry.registerLocale(new Locale("Korean", "한국의", "ko_KR", "POEditor contributors", Arrays.asList("korean", "한국의", "kr")));
    LocaleRegistry.registerLocale(new Locale("Polish", "Polski", "pl_PL", "Plajer", Arrays.asList("polish", "polski", "pl")));
    LocaleRegistry.registerLocale(new Locale("Romanian", "Românesc", "ro_RO", "POEditor contributors (Andrei)", Arrays.asList("romanian", "romanesc", "românesc", "ro")));
    LocaleRegistry.registerLocale(new Locale("Russian", "Pусский", "ru_RU", "POEditor contributors (Mrake)", Arrays.asList("russian", "russkiy", "pусский", "ru")));
    LocaleRegistry.registerLocale(new Locale("Spanish", "Español", "es_ES", "POEditor contributors", Arrays.asList("spanish", "espanol", "español", "es")));
    LocaleRegistry.registerLocale(new Locale("Turkish", "Türk", "tr_TR", "POEditor contributors", Arrays.asList("turkish", "turk", "türk", "tr")));
    LocaleRegistry.registerLocale(new Locale("Vietnamese", "Việt", "vn_VN", "POEditor contributors (HStreamGamer)", Arrays.asList("vietnamese", "viet", "việt", "vn")));
  }

  private static void loadProperties() {
    LocaleService service = ServiceRegistry.getLocaleService(plugin);
    if (service == null) {
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Build Battle] Locales cannot be downloaded because API website is unreachable, locales will be disabled.");
      pluginLocale = LocaleRegistry.getByName("English");
      return;
    }
    if (service.isValidVersion()) {
      LocaleService.DownloadStatus status = service.demandLocaleDownload(pluginLocale);
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
      return;
    }
    /* is beta release */
    if (plugin.getDescription().getVersion().contains("b") || plugin.getDescription().getVersion().contains("pre")) {
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildBattle] Locales aren't supported in beta versions because they're lacking latest translations! Enabling English one...");
      pluginLocale = LocaleRegistry.getByName("English");
      return;
    }
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[BuildBattle] Loaded locale " + pluginLocale.getName() + " (" + pluginLocale.getOriginalName() + " ID: " +
        pluginLocale.getPrefix() + ") by " + pluginLocale.getAuthor());
    loadProperties();
  }

  public static boolean isDefaultLanguageUsed() {
    return pluginLocale.getName().equals("English");
  }

  public static List<String> getLanguageList(String path) {
    if (isDefaultLanguageUsed()) {
      return languageConfig.getStringList(path);
    } else {
      return Arrays.asList(plugin.getChatManager().colorMessage(path).split(";"));
    }
  }

  public static String getLanguageMessage(String message) {
    if (isDefaultLanguageUsed()) {
      //error
      if (languageConfig.getString(message, "ERR_MESSAGE_NOT_FOUND").equals("ERR_MESSAGE_NOT_FOUND")) {
        Debugger.debug(Debugger.Level.WARN, "No message found for string accessor: " + message);
      }
      return languageConfig.getString(message, "ERR_MESSAGE_NOT_FOUND");
    }
    try {
      return properties.getProperty(ChatColor.translateAlternateColorCodes('&', message));
    } catch (NullPointerException ex) {
      MessageUtils.errorOccurred();
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
