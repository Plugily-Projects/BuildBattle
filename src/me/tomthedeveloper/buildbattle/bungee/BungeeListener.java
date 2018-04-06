package me.tomthedeveloper.buildbattle.bungee;

import me.tomthedeveloper.buildbattle.GameAPI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 * Created by Tom on 31/08/2014.
 */
public class BungeeListener implements PluginMessageListener {


    private GameAPI plugin;

    public BungeeListener(GameAPI plugin) {
        this.plugin = plugin;
        plugin.getPlugin().getServer().getMessenger().registerOutgoingPluginChannel(plugin.getPlugin(), "BungeeCord");
        plugin.getPlugin().getServer().getMessenger().registerIncomingPluginChannel(plugin.getPlugin(), "BungeeCord", this);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if(!channel.equalsIgnoreCase("BungeeCord")) return;

    }


}
