package me.tomthedeveloper.buildbattle.menuapi;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Tom on 27/07/2014.
 */
public class MenuListener implements Listener {

    @EventHandler
    public void onClickToSubMenu(InventoryClickEvent event) {
        if(event.getInventory().getHolder() == null) event.setCancelled(true);
        if(!event.getCurrentItem().hasItemMeta()) return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName()) return;
        ItemMeta itemMeta = event.getCurrentItem().getItemMeta();
        if(!(itemMeta.getDisplayName().contains("Open "))) return;


    }
}
