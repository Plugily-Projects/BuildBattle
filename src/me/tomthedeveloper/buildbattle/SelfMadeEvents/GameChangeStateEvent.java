package me.tomthedeveloper.buildbattle.SelfMadeEvents;

import me.TomTheDeveloper.Game.GameState;
import me.tomthedeveloper.buildbattle.instance.BuildInstance;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Tom on 1/11/2015.
 */
public class GameChangeStateEvent extends Event {


    private static final HandlerList handlers = new HandlerList();
    private GameState gameState;
    private BuildInstance buildInstance;
    private GameState previous;

    public GameChangeStateEvent(GameState gameState, BuildInstance buildInstance, GameState previous) {
        this.gameState = gameState;
        this.buildInstance = buildInstance;
        this.previous = previous;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GameState getState() {
        return gameState;
    }

    public BuildInstance getBuildInstance() {
        return buildInstance;
    }

    public GameState getPreviousState() {
        return previous;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
