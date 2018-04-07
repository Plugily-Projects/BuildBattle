package me.tomthedeveloper.buildbattle.events;

import me.tomthedeveloper.buildbattle.GameAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Created by Tom on 9/08/2014.
 */
public class BuildEvents implements Listener {

    private GameAPI plugin;

    public BuildEvents(GameAPI plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if(plugin.getAllowBuilding()) return;
        if(plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) == null) return;
        event.setCancelled(true);


    }


    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if(plugin.getAllowBuilding()) return;
        if(plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) == null) return;
        event.setCancelled(true);
    }
}
