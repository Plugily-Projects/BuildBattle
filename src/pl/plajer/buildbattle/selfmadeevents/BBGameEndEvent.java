package pl.plajer.buildbattle.selfmadeevents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.plajer.buildbattle.arena.Arena;

/**
 * Created by Tom on 1/11/2015.
 */
public class BBGameEndEvent extends Event {


    private static final HandlerList handlers = new HandlerList();
    private Arena buildInstance;

    public BBGameEndEvent(Arena buildInstance) {
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
