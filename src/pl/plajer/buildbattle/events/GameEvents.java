package pl.plajer.buildbattle.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.arena.ArenaRegistry;

/**
 * @author Plajer
 * <p>
 * Created at 26.04.2018
 */
public class GameEvents implements Listener {

    private Main plugin;

    public GameEvents(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if(ArenaRegistry.getArena(event.getPlayer()) == null) {
            for(Player player : event.getRecipients()) {
                if(ArenaRegistry.getArena(event.getPlayer()) == null) return;
                event.getRecipients().remove(player);

            }
        }
        event.getRecipients().clear();
        event.getRecipients().addAll(ArenaRegistry.getArena(event.getPlayer()).getPlayers());
    }

}
