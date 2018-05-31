/*
 *  Village Defense 3 - Protect villagers from hordes of zombies
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

package pl.plajer.buildbattle3.language;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.handlers.ConfigurationManager;
import pl.plajer.buildbattle3.utils.MessageUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 31.05.2018
 */
public class LanguageMigrator {

    public static final int LANGUAGE_FILE_VERSION = 0;
    private static Main plugin = JavaPlugin.getPlugin(Main.class);
    private static List<String> migratable = Arrays.asList("bungee", "config", "language", "MySQL");

    public static void migrateToNewFormat() {
        MessageUtils.gonnaMigrate();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Build Battle 3 is migrating all files to the new file format...");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Don't worry! Old files will be renamed not overridden!");
        for(String file : migratable) {
            if(ConfigurationManager.getFile(file).exists()) {
                ConfigurationManager.getFile(file).renameTo(new File(plugin.getDataFolder(), "BB2_" + file + ".yml"));
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Renamed file " + file + ".yml");
            }
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Done! Enabling BB2...");
    }

}
