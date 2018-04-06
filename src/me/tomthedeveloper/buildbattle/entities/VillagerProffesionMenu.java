package me.tomthedeveloper.buildbattle.entities;

import me.tomthedeveloper.buildbattle.handlers.ChatManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Tom on 5/02/2016.
 */
class VillagerProffesionMenu {


    public static Inventory getMenu(Entity entity) {
        Inventory inventory = Bukkit.createInventory(null, 9, ChatManager.getSingleMessage("Villager-Profession-Menu", "Choose villager profession"));
        Set<String> professions = new HashSet<>();
        professions.add("Blacksmith");
        professions.add("Librarian");
        professions.add("Farmer");
        professions.add("Butcher");
        professions.add("Priest");

        for(String string : professions) {
            EntityItem entityItem = EntityItemManager.getEntityItem("Profession." + string);
            inventory.setItem(entityItem.getSlot(), entityItem.getItemStack());
        }

        return inventory;

    }
}
