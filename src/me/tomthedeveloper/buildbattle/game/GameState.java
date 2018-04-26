package me.tomthedeveloper.buildbattle.game;

/**
 * Created by Tom on 27/07/2014.
 */
public enum GameState {
    WAITING_FOR_PLAYERS, STARTING, INGAME, ENDING, RESTARTING;


    public static GameState fromString(String s) {
        if(s.contains("RESTARTING")) return RESTARTING;
        if(s.contains("WAITING_FOR")) return WAITING_FOR_PLAYERS;
        if(s.contains("STARTING")) return STARTING;
        if(s.contains("INGAME")) return INGAME;
        if(s.contains("ENDING")) return ENDING;
        return WAITING_FOR_PLAYERS;
    }
}
