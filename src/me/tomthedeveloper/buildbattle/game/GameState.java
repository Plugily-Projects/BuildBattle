package me.tomthedeveloper.buildbattle.game;

/**
 * Created by Tom on 27/07/2014.
 */
public enum GameState {
    WAITING_FOR_PLAYERS, STARTING, INGAME, ENDING, RESTARTING, PHASE_1, PHASE_2, PHASE_3, PHASE_4, PHASE_5, PHASE_6;


    public static GameState fromString(String s) {
        if(s.contains("RESTARTING")) return RESTARTING;
        if(s.contains("WAITING_FOR")) return WAITING_FOR_PLAYERS;
        if(s.contains("STARTING")) return STARTING;
        if(s.contains("INGAME")) return INGAME;
        if(s.contains("ENDING")) return ENDING;
        if(s.contains("PHASE") && s.contains("1")) return PHASE_1;
        if(s.contains("PHASE") && s.contains("2")) return PHASE_2;
        if(s.contains("PHASE") && s.contains("3")) return PHASE_3;
        if(s.contains("PHASE") && s.contains("4")) return PHASE_4;
        if(s.contains("PHASE") && s.contains("5")) return PHASE_5;
        if(s.contains("PHASE") && s.contains("6")) return PHASE_6;
        return WAITING_FOR_PLAYERS;
    }
}
