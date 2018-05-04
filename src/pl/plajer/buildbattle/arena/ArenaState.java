package pl.plajer.buildbattle.arena;

/**
 * Created by Tom on 27/07/2014.
 */
public enum ArenaState {
    WAITING_FOR_PLAYERS, STARTING, IN_GAME, ENDING, RESTARTING;


    public static ArenaState fromString(String s) {
        if(s.contains("RESTARTING")) return RESTARTING;
        if(s.contains("WAITING_FOR")) return WAITING_FOR_PLAYERS;
        if(s.contains("STARTING")) return STARTING;
        if(s.contains("IN_GAME")) return IN_GAME;
        if(s.contains("ENDING")) return ENDING;
        return WAITING_FOR_PLAYERS;
    }
}
