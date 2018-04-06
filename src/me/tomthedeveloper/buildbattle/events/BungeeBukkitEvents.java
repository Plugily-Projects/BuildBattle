package me.tomthedeveloper.buildbattle.events;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.tomthedeveloper.buildbattle.GameAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Tom on 31/08/2014.
 */
public class BungeeBukkitEvents implements Listener {


    private GameAPI plugin;


    @EventHandler
    public void onJoinEvent(PlayerJoinEvent event) {
        if(plugin.getGameInstanceManager().getGameInstances().get(0).needsPlayers()) {
            plugin.getGameInstanceManager().getGameInstances().get(0).joinAttempt(event.getPlayer());
        } else {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF("Argument");
        }
    }

}
