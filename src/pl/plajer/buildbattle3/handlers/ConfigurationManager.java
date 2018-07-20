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

package pl.plajer.buildbattle3.handlers;


import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import pl.plajer.buildbattle3.Main;

/**
 * @author IvanTheBuilder
 */
public class ConfigurationManager {

  private static Main plugin;

  public ConfigurationManager(Main plugin) {
    ConfigurationManager.plugin = plugin;
  }

  public static File getFile(String filename) {
    return new File(plugin.getDataFolder() + File.separator + filename + ".yml");
  }

  public static FileConfiguration getConfig(String filename) {
    File file = new File(plugin.getDataFolder() + File.separator + filename + ".yml");
    if (!file.exists()) {
      plugin.getLogger().info("Creating " + filename + ".yml because it does not exist!");
      plugin.saveResource(filename + ".yml", true);
    }
    file = new File(plugin.getDataFolder(), filename + ".yml");
    YamlConfiguration config = new YamlConfiguration();
    try {
      config.load(file);
    } catch (InvalidConfigurationException | IOException ex) {
      ex.printStackTrace();
      Bukkit.getConsoleSender().sendMessage("Cannot load file " + filename + ".yml!");
      Bukkit.getConsoleSender().sendMessage("Create blank file " + filename + ".yml or restart the server!");
    }
    return config;
  }

  public static void saveConfig(FileConfiguration config, String name) {
    try {
      config.save(new File(plugin.getDataFolder(), name + ".yml"));
    } catch (IOException e) {
      e.printStackTrace();
      Bukkit.getConsoleSender().sendMessage("Cannot save file " + name + ".yml!");
      Bukkit.getConsoleSender().sendMessage("Create blank file " + name + ".yml or restart the server!");
    }
  }

}
