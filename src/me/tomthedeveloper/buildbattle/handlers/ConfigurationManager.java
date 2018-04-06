package me.tomthedeveloper.buildbattle.handlers;


import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author IvanTheBuilder
 */
public class ConfigurationManager {

    public static JavaPlugin plugin;

    public static File getFile(String filename) {
        return new File(plugin.getDataFolder() + File.separator + filename + ".yml");
    }

    public static void Init(JavaPlugin pl) {
        plugin = pl;
    }

    public static FileConfiguration getConfig(String filename) {
        File ConfigFile = new File(plugin.getDataFolder() + File.separator + filename + ".yml");
        if(!ConfigFile.exists()) {
            boolean error = false;
            try {
                plugin.getLogger().info("Creating " + filename + ".yml because it does not exist!");
                ConfigFile.createNewFile();
            } catch(IOException ex) {
                error = true;
                System.out.print("[GameAPI]: Please check the file " + filename + ".yml because it has a format error inside! Check the stacktrace of the error to quickly find out where.");
                System.out.print("[GameAPI]: Shutting down server! Fix this format error and restart it again.");
                Bukkit.getServer().shutdown();
                ex.printStackTrace();

            }
            if(error) {
                System.out.print("BOOO");
            }

            ConfigFile = new File(plugin.getDataFolder(), filename + ".yml");
            YamlConfiguration config = new YamlConfiguration();

            try {
                config.load(ConfigFile);
                //YamlConfiguration config = YamlConfiguration.loadConfiguration(ConfigFile);
            } catch(InvalidConfigurationException ex) {
                ex.printStackTrace();
                System.out.print("[GameAPI]: Please check the file " + filename + ".yml because it has a format error inside! Check the stacktrace of the error to quickly find out where.");
                System.out.print("[GameAPI]: Shutting down server! Fix this format error and restart it again.");
                Bukkit.getServer().shutdown();

            } catch(FileNotFoundException e) {
                e.printStackTrace();
            } catch(IOException e) {
                e.printStackTrace();
            }

            try {
                config.save(ConfigFile);

            } catch(IOException ex) {
                System.out.print("[GameAPI]: Please check the file " + filename + ".yml because it has a format error inside! Check the stacktrace of the error to quickly find out where.");
                System.out.print("[GameAPI]: Shutting down server! Fix this format error and restart it again.");
                Bukkit.getServer().shutdown();
                ex.printStackTrace();
            }


        }
        ConfigFile = new File(plugin.getDataFolder(), filename + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(ConfigFile);
            //YamlConfiguration config = YamlConfiguration.loadConfiguration(ConfigFile);
        } catch(InvalidConfigurationException ex) {
            ex.printStackTrace();
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GameAPI]: Please check the file " + filename + ".yml because it has a format error inside! Check the stacktrace of the above error to quickly find out where.");
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GameAPI]: Shutting down server! Fix this format error and restart it again.");
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GameAPI]: Please ignore following error(s).");
            Bukkit.shutdown();
            return null;

        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return config;
    }


}
