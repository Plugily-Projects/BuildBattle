package me.tomthedeveloper.buildbattle.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Tom on 15/08/2014.
 */
public class PlayerAddSpawnCommandEvent extends Event {


    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private boolean cancel = false;
    private String argument;
    private String ID;

    public PlayerAddSpawnCommandEvent(Player player, String string, String ID) {
        this.player = player;
        this.argument = string;
        this.ID = ID;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getSpawnName() {
        return argument;
    }

    public String getArenaID() {
        return ID;
    }

    public boolean isCancelled() {
        return this.cancel;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancel = cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
