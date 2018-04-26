package me.tomthedeveloper.buildbattle.selfmadeevents;

import me.tomthedeveloper.buildbattle.game.GameState;
import me.tomthedeveloper.buildbattle.arena.Arena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Tom on 1/11/2015.
 */
public class GameChangeStateEvent extends Event {


    private static final HandlerList handlers = new HandlerList();
    private GameState gameState;
    private Arena buildInstance;
    private GameState previous;

    public GameChangeStateEvent(GameState gameState, Arena buildInstance, GameState previous) {
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

    public Arena getBuildInstance() {
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
