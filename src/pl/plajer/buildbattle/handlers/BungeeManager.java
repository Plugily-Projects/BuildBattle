package pl.plajer.buildbattle.handlers;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.arena.ArenaState;
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
        return ConfigurationManager.getConfig("bungee").getString("Hub");
    }

    private String getMOTD() {
        Arena arena = plugin.getGameAPI().getGameInstanceManager().getArenas().get(0);
        if(arena.getGameState() == ArenaState.STARTING && (arena.getTimer() <= 3)) {
            return ArenaState.INGAME.toString();
        } else {
            return arena.getGameState().toString();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerListPing(ServerListPingEvent event) {
        if(plugin.getGameAPI().getGameInstanceManager() == null) return;
        if(plugin.getGameAPI().getGameInstanceManager().getArenas().size() == 0) return;
        if(plugin.getGameAPI().getGameInstanceManager().getArenas() == null) {
            System.out.print("NO GAMEINSTANCE FOUND! FIRST CONFIGURE AN ARENA BEFORE ACTIVATING BUNGEEEMODE!");
            return;
        }
        event.setMaxPlayers(plugin.getGameAPI().getGameInstanceManager().getArenas().get(0).getMAX_PLAYERS());
        event.setMotd(this.getMOTD());
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(final PlayerJoinEvent event) {
        event.setJoinMessage("");
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> plugin.getGameAPI().getGameInstanceManager().getArenas().get(0).joinAttempt(event.getPlayer()), 1L);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");
        if(plugin.getGameAPI().getGameInstanceManager().getArena(event.getPlayer()) != null)
            plugin.getGameAPI().getGameInstanceManager().getArenas().get(0).leaveAttempt(event.getPlayer());
    }


}
