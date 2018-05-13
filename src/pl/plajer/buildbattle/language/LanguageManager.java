package pl.plajer.buildbattle.language;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.handlers.ConfigurationManager;

/**
 * @author Plajer
 * <p>
 * Created at 13.05.2018
 */
public class LanguageManager {

    private static Main plugin;
    private static VDLocale pluginLocale;

    public static void init(Main pl) {
        plugin = pl;
        ConfigurationManager.getConfig("new_language");
        // setupLocale();
    }

    private static void setupLocale() {
        plugin.saveResource("language_de.yml", true);
        plugin.saveResource("language_pl.yml", true);
        String locale = plugin.getConfig().getString("locale");
        if(locale.equalsIgnoreCase("default") || locale.equalsIgnoreCase("english")) {
            pluginLocale = VDLocale.DEFAULT;
        } else if(locale.equalsIgnoreCase("de") || locale.equalsIgnoreCase("deutsch")) {
            pluginLocale = VDLocale.DEUTSCH;
            if(!ConfigurationManager.getConfig("language_de").get("File-Version-Do-Not-Edit").equals(ConfigurationManager.getConfig("language_de").get("Language-Version"))) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Locale DEUTSCH is outdated! Not every message will be in german.");
            }
            if(!LanguageManager.getDefaultLanguageMessage("File-Version-Do-Not-Edit").equals(LanguageManager.getLanguageMessage("File-Version-Do-Not-Edit"))) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Locale DEUTSCH is invalid! Using DEFAULT locale instead...");
                pluginLocale = VDLocale.DEFAULT;
            }
        } else if(locale.equalsIgnoreCase("pl") || locale.equalsIgnoreCase("polski")) {
            pluginLocale = VDLocale.POLSKI;
            if(!ConfigurationManager.getConfig("language_pl").get("File-Version-Do-Not-Edit").equals(ConfigurationManager.getConfig("language_pl").get("Language-Version"))) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Locale POLSKI is outdated! Not every message will be in polish.");
            }
            if(!LanguageManager.getDefaultLanguageMessage("File-Version-Do-Not-Edit").equals(LanguageManager.getLanguageMessage("File-Version-Do-Not-Edit"))) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Locale POLSKI is invalid! Using DEFAULT locale instead...");
                pluginLocale = VDLocale.DEFAULT;
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Village Defense] Plugin locale is invalid! Using default one...");
            pluginLocale = VDLocale.DEFAULT;
        }
    }

    public static String getDefaultLanguageMessage(String message) {
        if(ConfigurationManager.getConfig("new_language").isSet(message)) {
            return ConfigurationManager.getConfig("new_language").getString(message);
        }
        return "NULL_MESSAGE";
    }

    public static String getLanguageMessage(String message) {
        return getDefaultLanguageMessage(message);
        /*switch(pluginLocale) {
            case DEFAULT:
                if(ConfigurationManager.getConfig("language").isSet(message)) {
                    return ConfigurationManager.getConfig("language").getString(message);
                }
                return null;
            case DEUTSCH:
                if(ConfigurationManager.getConfig("language_de").isSet(message)) {
                    return ConfigurationManager.getConfig("language_de").getString(message);
                }
                return null;
            case POLSKI:
                if(ConfigurationManager.getConfig("language_pl").isSet(message)) {
                    return ConfigurationManager.getConfig("language_pl").getString(message);
                }
                return null;
            default:
                if(ConfigurationManager.getConfig("language").isSet(message)) {
                    return ConfigurationManager.getConfig("language").getString(message);
                }
                return null;
        }*/
    }

    private enum VDLocale {
        DEFAULT, DEUTSCH, POLSKI
    }

}
