package me.tomthedeveloper.buildbattle.events;

import me.tomthedeveloper.buildbattle.GameAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Tom on 11/08/2014.
 */
public class onQuit implements Listener {


    public GameAPI plugin;

    public onQuit(GameAPI plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if(plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) == null) return;
        if(!plugin.isBungeeActivated()) plugin.getGameInstanceManager().getGameInstance(event.getPlayer()).leaveAttempt(event.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        if(plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) == null) return;
        if(!plugin.isBungeeActivated()) plugin.getGameInstanceManager().getGameInstance(event.getPlayer()).leaveAttempt(event.getPlayer());
    }
}
