package me.tomthedeveloper.buildbattle.kitapi.basekits;

import me.tomthedeveloper.buildbattle.handlers.ChatManager;
import me.tomthedeveloper.buildbattle.handlers.ConfigurationManager;
import me.tomthedeveloper.buildbattle.utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

/**
 * Created by Tom on 14/08/2014.
 */
public abstract class LevelKit extends Kit {


    int level;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        FileConfiguration config = ConfigurationManager.getConfig("Kits");
        String name = getClass().getName().substring(getClass().getName().indexOf("K"));
        if(!config.contains(name)) {
            config.set("Required-Level." + name, level);
            try {
                config.save(ConfigurationManager.getFile("Kits"));
            } catch(IOException e) {
                e.printStackTrace();
            }
        } else {
            this.level = config.getInt("Required-Level." + name);
        }
    }

    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(getMaterial());
        setItemNameAndLore(itemStack, getName(), getDescription());
        Util.addLore(itemStack, ChatManager.getSingleMessage("Unlocks-at-level", ChatColor.GREEN + "Unlocks at level %NUMBER% ", getLevel()));
        return itemStack;
    }
}
