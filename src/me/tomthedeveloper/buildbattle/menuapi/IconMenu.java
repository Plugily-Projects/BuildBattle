package me.tomthedeveloper.buildbattle.menuapi;

import me.tomthedeveloper.buildbattle.handlers.ConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * Created by Tom on 27/07/2014.
 */
public class IconMenu {


    private Inventory inventory;
    private String name;
    private int size;
    private ConfigurationManager configurationManager;

    public IconMenu(String name, int size) {
        this.name = name;
        this.size = size;
        size = serializeInt(size);

        inventory = Bukkit.createInventory(null, size, name);


    }

    protected static int serializeInt(int i) {
        if((i % 9) == 0) return i;
        else return (int) ((Math.ceil(i / 9) * 9) + 9);
    }

    public Inventory getIventory() {
        return inventory;
    }


    public void open(Player p) {
        p.openInventory(inventory);

    }

    public void close(Player p) {
        p.closeInventory();
    }

    public void addOption(ItemStack itemStack) {
        inventory.addItem(itemStack);
    }

    public void addOption(ItemStack itemStack, int position) {
        if(position > size) throw new IllegalArgumentException("Number of position to big!");
        else inventory.setItem(position, itemStack);
    }

    private ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore) {
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return item;
    }


}
