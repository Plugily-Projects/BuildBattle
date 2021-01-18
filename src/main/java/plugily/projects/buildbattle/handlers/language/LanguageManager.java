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

package plugily.projects.buildbattle.handlers.language;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.FileConfiguration;

import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.utils.Debugger;
import plugily.projects.buildbattle.utils.services.ServiceRegistry;
import plugily.projects.buildbattle.utils.services.locale.Locale;
import plugily.projects.buildbattle.utils.services.locale.LocaleRegistry;
import plugily.projects.buildbattle.utils.services.locale.LocaleService;

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
  private static FileConfiguration defaultLanguageConfig;

  private LanguageManager() {
  }

  public static void init(Main plugin) {
    LanguageManager.plugin = plugin;
    if (!new File(plugin.getDataFolder() + File.separator + "language.yml").exists()) {
      plugin.saveResource("language.yml", false);
    }
    //auto update
    plugin.saveResource("locales/language_default.yml", true);

    registerLocales();
    setupLocale();
    new LanguageMigrator(plugin);
    //get file after all migrations are done
    languageConfig = ConfigUtils.getConfig(plugin, "language");
    defaultLanguageConfig = ConfigUtils.getConfig(plugin, "locales/language_default");
  }

  private static void registerLocales() {
    Arrays.asList(new Locale("Basque", "Basque", "eu_EU", "POEditor contributors", Arrays.asList("basque", "eu")),
      new Locale("Belarusian", "Belarus", "be_BY", "POEditor contributors", Arrays.asList("belarusian", "belarus", "by", "be")),
      new Locale("Chinese (Simplified)", "简体中文", "zh_CN", "POEditor contributors", Arrays.asList("简体中文", "中文", "chinese", "chinese_simplified", "cn")),
      new Locale("Chinese (Traditional)", "简体中文", "zh_HK", "POEditor contributors", Arrays.asList("中文(傳統)", "中國傳統", "chinese_traditional", "zh_hk")),
      new Locale("Czech", "Český", "cs_CZ", "POEditor contributors", Arrays.asList("czech", "cesky", "český", "cs")),
      new Locale("Dutch", "Nederlands", "nl_NL", "POEditor contributors", Arrays.asList("dutch", "nederlands", "nl")),
      new Locale("English", "English", "en_GB", "Plajer", Arrays.asList("default", "english", "en")),
      new Locale("Estonian", "Eesti", "et_EE", "POEditor contributors", Arrays.asList("estonian", "eesti", "et")),
      new Locale("French", "Français", "fr_FR", "POEditor contributors", Arrays.asList("french", "francais", "français", "fr")),
      new Locale("German", "Deutsch", "de_DE", "Tigerkatze and POEditor contributors", Arrays.asList("deutsch", "german", "de")),
      new Locale("Hungarian", "Magyar", "hu_HU", "POEditor contributors", Arrays.asList("hungarian", "magyar", "hu")),
      new Locale("Indonesian", "Indonesia", "id_ID", "POEditor contributors", Arrays.asList("indonesian", "indonesia", "id")),
      new Locale("Italian", "Italiano", "it_IT", "POEditor contributors", Arrays.asList("italian", "italiano", "it")),
      new Locale("Japanese", "日本語", "ja_JP", "POEditor contributors", Arrays.asList("日本語", "japanese", "jp", "ja")),
      new Locale("Korean", "한국의", "ko_KR", "POEditor contributors", Arrays.asList("korean", "한국의", "kr")),
      new Locale("Lithuanian", "Lietuvių", "lt_LT", "POEditor contributors", Arrays.asList("lithuanian", "lietuvių", "lietuviu", "lt")),
      new Locale("Polish", "Polski", "pl_PL", "Plajer", Arrays.asList("polish", "polski", "pl")),
      new Locale("Portuguese (BR)", "Português (Brasil)", "pt_BR", "POEditor contributors", Arrays.asList("portuguese br", "português br", "português brasil", "pt_br")),
      new Locale("Romanian", "Românesc", "ro_RO", "POEditor contributors", Arrays.asList("romanian", "romanesc", "românesc", "ro")),
      new Locale("Russian", "Pусский", "ru_RU", "POEditor contributors", Arrays.asList("russian", "russkiy", "pусский", "ru")),
      new Locale("Slovenian", "Slovenščina", "sl_SL", "POEditor contributors", Arrays.asList("slovenian", "slovenščina", "slovenscina", "sl")),
      new Locale("Spanish", "Español", "es_ES", "POEditor contributors", Arrays.asList("spanish", "espanol", "español", "es")),
      new Locale("Turkish", "Türk", "tr_TR", "POEditor contributors", Arrays.asList("turkish", "turk", "türk", "tr")),
      new Locale("Ukrainian", "Ukraine", "uk_UA", "POEditor contributors", Arrays.asList("ukraine", "ua", "uk")),
      new Locale("Vietnamese", "Việt", "vn_VN", "POEditor contributors", Arrays.asList("vietnamese", "viet", "việt", "vn")))
    .forEach(LocaleRegistry::registerLocale);
  }

  private static void loadProperties() {
    LocaleService service = ServiceRegistry.getLocaleService(plugin);
    if (service == null) {
      Debugger.sendConsoleMsg("&c[Build Battle] Locales cannot be downloaded because API website is unreachable, locales will be disabled.");
      pluginLocale = LocaleRegistry.getByName("English");
      return;
    }
    if (service.isValidVersion()) {
      LocaleService.DownloadStatus status = service.demandLocaleDownload(pluginLocale);
      if (status == LocaleService.DownloadStatus.FAIL) {
        pluginLocale = LocaleRegistry.getByName("English");
        Debugger.sendConsoleMsg("&c[BuildBattle] Locale service couldn't download latest locale for plugin! English locale will be used instead!");
        return;
      } else if (status == LocaleService.DownloadStatus.SUCCESS) {
        Debugger.sendConsoleMsg("&c[BuildBattle] Downloaded locale " + pluginLocale.getPrefix() + " properly!");
      } else if (status == LocaleService.DownloadStatus.LATEST) {
        Debugger.sendConsoleMsg("&c[BuildBattle] Locale " + pluginLocale.getPrefix() + " is latest! Awesome!");
      }
    } else {
      pluginLocale = LocaleRegistry.getByName("English");
      Debugger.sendConsoleMsg("&c[BuildBattle] Your plugin version is too old to use latest locale! Please update plugin to access latest updates of locale!");
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
      if (locale.getPrefix().equalsIgnoreCase(localeName)) {
        pluginLocale = locale;
        break;
      }
      for (String alias : locale.getAliases()) {
        if (alias.equals(localeName)) {
          pluginLocale = locale;
          break;
        }
      }
    }
    if (pluginLocale == null) {
      Debugger.sendConsoleMsg("&c[BuildBattle] Plugin locale is invalid! Using default one...");
      pluginLocale = LocaleRegistry.getByName("English");
      return;
    }
    /* is beta release */
    if (plugin.getDescription().getVersion().contains("locales") || plugin.getDescription().getVersion().contains("pre")) {
      Debugger.sendConsoleMsg("&c[BuildBattle] Locales aren't supported in beta versions because they're lacking latest translations! Enabling English one...");
      pluginLocale = LocaleRegistry.getByName("English");
      return;
    }
    Debugger.sendConsoleMsg("&a[BuildBattle] Loaded locale " + pluginLocale.getName() + " (" + pluginLocale.getOriginalName() + " ID: " +
        pluginLocale.getPrefix() + ") by " + pluginLocale.getAuthor());
    loadProperties();
  }

  public static boolean isDefaultLanguageUsed() {
    return "English".equals(pluginLocale.getName());
  }

  public static String getLanguageMessage(String path) {
    if (isDefaultLanguageUsed()) {
      return getString(path);
    }
    String prop = properties.getProperty(path);
    if (prop == null){
      return getString(path);
    }
    if (getString(path).equalsIgnoreCase(defaultLanguageConfig.getString(path, "not found"))){
      return prop;
    }
    return getString(path);
  }

  public static List<String> getLanguageList(String path) {
    if (isDefaultLanguageUsed()) {
      return getStrings(path);
    }
    String prop = properties.getProperty(path);
    if (prop == null) {
      return getStrings(path);
    }
    if (getString(path).equalsIgnoreCase(defaultLanguageConfig.getString(path, "not found"))){
      return Arrays.asList(plugin.getChatManager().colorRawMessage(prop).split(";"));
    }
    return getStrings(path);
  }


  private static List<String> getStrings(String path) {
    FileConfiguration language = languageConfig;
    if (!language.isSet(path)) {
      language = defaultLanguageConfig;
      if (!language.isSet(path)){
          Debugger.sendConsoleMsg("&c[BuildBattle] Game message not found in your locale!");
          Debugger.sendConsoleMsg("&c[BuildBattle] Please regenerate your language.yml file! If error still occurs report it to the developer on discord!");
          Debugger.sendConsoleMsg("&c[BuildBattle] Path: " + path);
          return Collections.singletonList("ERR_MESSAGE_" + path + "_NOT_FOUND");
      }
    }
    List<String> list = language.getStringList(path);
    list = list.stream().map(string -> plugin.getChatManager().colorRawMessage(string)).collect(Collectors.toList());
    return list;
  }


  private static String getString(String path) {
      FileConfiguration language = languageConfig;
      if (!language.isSet(path)) {
          language = defaultLanguageConfig;
          if (!language.isSet(path)){
            Debugger.sendConsoleMsg("&c[BuildBattle] Game message not found in your locale!");
            Debugger.sendConsoleMsg("&c[BuildBattle] Please regenerate your language.yml file! If error still occurs report it to the developer on discord!");
            Debugger.sendConsoleMsg("&c[BuildBattle] Path: " + path);
            return "ERR_MESSAGE_" + path + "_NOT_FOUND";
        }
      }
    return language.getString(path, "not found");
  }

  public static Locale getPluginLocale() {
    return pluginLocale;
  }

  public static void reloadConfig() {
    LanguageManager.languageConfig = ConfigUtils.getConfig(plugin, "language");
  }

}
