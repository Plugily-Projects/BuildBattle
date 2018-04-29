package pl.plajer.buildbattle.entities;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by Tom on 31/01/2016.
 */
class EntityItemManager {

    private static HashMap<String, EntityItem> entityItems = new HashMap<>();

    public static void addEntityItem(String name, EntityItem entityItem) {
        entityItems.put(name, entityItem);
    }

    public static EntityItem getEntityItem(String name) {
        if(entityItems.containsKey(name)) return entityItems.get(name);
        return null;
    }

    public static String getRelatedEntityItemName(ItemStack itemStack) {
        for(String key : entityItems.keySet()) {
            EntityItem entityItem = entityItems.get(key);
            if(entityItem.getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(itemStack.getItemMeta().getDisplayName()) && entityItem.getMaterial().equals(itemStack.getType())) {
                return key;
            }
        }
        return null;
    }

}
