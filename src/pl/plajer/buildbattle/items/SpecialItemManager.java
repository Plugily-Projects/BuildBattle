package pl.plajer.buildbattle.items;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by Tom on 5/02/2016.
 */
public class SpecialItemManager {


    private static HashMap<String, SpecialItem> specialItems = new HashMap<>();


    public static void addEntityItem(String name, SpecialItem entityItem) {
        specialItems.put(name, entityItem);
    }

    public static SpecialItem getSpecialItem(String name) {
        if(specialItems.containsKey(name)) return specialItems.get(name);
        return null;
    }

    public static String getRelatedSpecialItem(ItemStack itemStack) {
        for(String key : specialItems.keySet()) {
            SpecialItem entityItem = specialItems.get(key);
            if(entityItem.getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(itemStack.getItemMeta().getDisplayName()) && entityItem.getMaterial().equals(itemStack.getType())) {
                return key;
            }
        }
        return null;
    }
}
