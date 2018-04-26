package me.tomthedeveloper.buildbattle.events;

import me.tomthedeveloper.buildbattle.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Plajer
 * <p>
 * Created at 26.04.2018
 */
public class GameEvents implements Listener {

    private Main plugin;

    public GameEvents(Main plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if(plugin.getGameAPI().getGameInstanceManager().getGameInstance(event.getPlayer()) == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if(plugin.getGameAPI().getGameInstanceManager().getGameInstance(event.getPlayer()) == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if(plugin.getGameAPI().getGameInstanceManager().getGameInstance(event.getPlayer()) == null) {
            for(Player player : event.getRecipients()) {
                if(plugin.getGameAPI().getGameInstanceManager().getGameInstance(event.getPlayer()) == null) return;
                event.getRecipients().remove(player);

            }
        }
        event.getRecipients().clear();
        event.getRecipients().addAll(plugin.getGameAPI().getGameInstanceManager().getGameInstance(event.getPlayer()).getPlayers());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if(plugin.getGameAPI().getGameInstanceManager().getGameInstance(event.getPlayer()) == null) return;
        if(!plugin.isBungeeActivated()) plugin.getGameAPI().getGameInstanceManager().getGameInstance(event.getPlayer()).leaveAttempt(event.getPlayer());
    }

}
