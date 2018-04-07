package me.tomthedeveloper.buildbattle.selfmadeevents;

import me.tomthedeveloper.buildbattle.instance.BuildInstance;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Tom on 1/11/2015.
 */
public class GameEndEvent extends Event {


    private static final HandlerList handlers = new HandlerList();
    private BuildInstance buildInstance;

    public GameEndEvent(BuildInstance buildInstance) {
        this.buildInstance = buildInstance;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public BuildInstance getBuildInstance() {
        return buildInstance;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
