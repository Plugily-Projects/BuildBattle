package me.tomthedeveloper.buildbattle.events;

import me.tomthedeveloper.buildbattle.GameAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Tom on 10/07/2015.
 */
//TODO update checker
public class JoinEvents implements Listener {

    private GameAPI plugin;

    public JoinEvents(GameAPI plugin) {
        this.plugin = plugin;
        plugin.getPlugin().getServer().getPluginManager().registerEvents(this, plugin.getPlugin());
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(plugin.getPlugin().isBungeeActivated()) return;
        for(Player player : plugin.getPlugin().getServer().getOnlinePlayers()) {
            if(plugin.getGameInstanceManager().getGameInstance(player) == null) continue;
            player.hidePlayer(event.getPlayer());
            event.getPlayer().hidePlayer(player);
        }
    }
}
