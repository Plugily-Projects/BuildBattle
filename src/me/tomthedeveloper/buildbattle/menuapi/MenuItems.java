package me.tomthedeveloper.buildbattle.menuapi;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * Created by Tom on 27/07/2014.
 */
public class MenuItems {


    public static ItemStack getGoBackItem() {
        ItemStack itemStack = new ItemStack(Material.PAPER);
        setItemNameAndLore(itemStack, ChatColor.RED + "Previous Menu", "This opens the previous menu!");
        return itemStack;
    }

    public static ItemStack getCloseItem() {
        ItemStack itemStack = new ItemStack(Material.REDSTONE);
        setItemNameAndLore(itemStack, ChatColor.RED + "Close Menu", "This closes the menu");
        return itemStack;
    }


    private static ItemStack setItemNameAndLore(ItemStack item, String name, String... lore) {
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return item;
    }

}
