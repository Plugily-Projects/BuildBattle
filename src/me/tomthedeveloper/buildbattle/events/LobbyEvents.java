package me.tomthedeveloper.buildbattle.events;

import me.tomthedeveloper.buildbattle.GameAPI;
import me.tomthedeveloper.buildbattle.game.GameInstance;
import me.tomthedeveloper.buildbattle.game.GameState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * Created by Tom on 16/06/2015.
 */
public class LobbyEvents implements Listener {


    private GameAPI plugin;

    public LobbyEvents(GameAPI plugin) {
        this.plugin = plugin;
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFoodLose(FoodLevelChangeEvent event) {
        if(event.getEntity().getType() != EntityType.PLAYER) return;
        Player player = (Player) event.getEntity();
        if(plugin.getGameInstanceManager().getGameInstance(player) == null) return;
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(player);
        if(gameInstance.getGameState() == GameState.STARTING || gameInstance.getGameState() == GameState.WAITING_FOR_PLAYERS)
            event.setCancelled(true);
    }
}
