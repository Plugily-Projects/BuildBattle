package me.tomthedeveloper.buildbattle.selfmadeevents;

import me.tomthedeveloper.buildbattle.arena.Arena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Tom on 1/11/2015.
 */
public class GameStartEvent extends Event {


    private static final HandlerList handlers = new HandlerList();
    private Arena buildInstance;


    public GameStartEvent(Arena buildInstance) {
        this.buildInstance = buildInstance;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Arena getBuildInstance() {
        return buildInstance;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
