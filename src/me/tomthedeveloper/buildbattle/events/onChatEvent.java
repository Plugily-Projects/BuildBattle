package me.tomthedeveloper.buildbattle.events;

import me.tomthedeveloper.buildbattle.GameAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collection;

/**
 * Created by Tom on 13/08/2014.
 */
public class onChatEvent implements Listener {

    private GameAPI plugin;

    public onChatEvent(GameAPI plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if(plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) == null) {
            for(Player player : event.getRecipients()) {
                if(plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) == null) return;
                event.getRecipients().remove(player);

            }
        }

        event.getRecipients().clear();
        event.getRecipients().addAll(plugin.getGameInstanceManager().getGameInstance(event.getPlayer()).getPlayers());

    }
}
