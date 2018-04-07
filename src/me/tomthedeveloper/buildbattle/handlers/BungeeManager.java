package me.tomthedeveloper.buildbattle.handlers;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.tomthedeveloper.buildbattle.Main;
import me.tomthedeveloper.buildbattle.game.GameInstance;
import me.tomthedeveloper.buildbattle.game.GameState;
import me.tomthedeveloper.buildbattle.handlers.ConfigurationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

/**
 * Created by Tom on 31/08/2014.
 */
public class BungeeManager implements Listener {

    public Main plugin;

    public BungeeManager(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    public void connectToHub(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(getHubServerName());
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    public String getHubServerName() {
        return ConfigurationManager.getConfig("Bungee").getString("Hub");
    }

    private String getMOTD() {
        GameInstance gameInstance = plugin.getGameAPI().getGameInstanceManager().getGameInstances().get(0);
        if(gameInstance.getGameState() == GameState.STARTING && (gameInstance.getTimer() <= 3)) {
            return GameState.INGAME.toString();
        } else {
            return gameInstance.getGameState().toString();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerListPing(ServerListPingEvent event) {
        if(plugin.getGameAPI().getGameInstanceManager() == null) return;
        if(plugin.getGameAPI().getGameInstanceManager().getGameInstances().size() == 0) return;
        if(plugin.getGameAPI().getGameInstanceManager().getGameInstances() == null) {
            System.out.print("NO GAMEINSTANCE FOUND! FIRST CONFIGURE AN ARENA BEFORE ACTIVATING BUNGEEEMODE!");
            return;
        }
        event.setMaxPlayers(plugin.getGameAPI().getGameInstanceManager().getGameInstances().get(0).getMAX_PLAYERS());
        event.setMotd(this.getMOTD());
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(final PlayerJoinEvent event) {
        event.setJoinMessage("");
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> plugin.getGameAPI().getGameInstanceManager().getGameInstances().get(0).joinAttempt(event.getPlayer()), 1L);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");
        if(plugin.getGameAPI().getGameInstanceManager().getGameInstance(event.getPlayer()) != null)
            plugin.getGameAPI().getGameInstanceManager().getGameInstances().get(0).leaveAttempt(event.getPlayer());
    }


}
