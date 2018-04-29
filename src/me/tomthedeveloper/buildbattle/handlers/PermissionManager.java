package me.tomthedeveloper.buildbattle.handlers;

/**
 * Created by Tom on 14/08/2014.
 */
public class PermissionManager {

    private static String joinFullGames = "minigames.fullgames";
    private static String VIP = "minigames.VIP";
    private static String MVP = "minigames.MVP";
    private static String ELITE = "minigames.ELITE";
    private static String editGames = "minigames.edit";

    public static String getJoinFullGames() {
        return joinFullGames;
    }

    public static String getVIP() {
        return VIP;
    }

    public static String getMVP() {
        return MVP;
    }

    public static String getELITE() {
        return ELITE;
    }

    public static String getEditGames() {
        return editGames;
    }
}
