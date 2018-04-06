package me.tomthedeveloper.buildbattle.permissions;

/**
 * Created by Tom on 14/08/2014.
 */
public class PermStrings {


    private static String joinFullGames = "minigames.fullgames";
    private static String VIP = "minigames.VIP";
    private static String MVP = "minigames.MVP";
    private static String ELITE = "minigames.ELITE";
    private static String editGames = "minigames.edit";
    private static String doubleJump = "minigames.doublejump";

    public static String getJoinFullGames() {
        return joinFullGames;
    }

    public static void setJoinFullGames(String joinFullGames) {
        PermStrings.joinFullGames = joinFullGames;
    }

    public static String getDoubleJump() {
        return doubleJump;
    }

    public static void setDoubleJump(String doubleJump) {
        PermStrings.doubleJump = doubleJump;
    }

    public static String getVIP() {
        return VIP;
    }

    public static void setVIP(String VIP) {
        PermStrings.VIP = VIP;
    }

    public static String getMVP() {
        return MVP;
    }

    public static void setMVP(String MVP) {
        PermStrings.MVP = MVP;
    }

    public static String getELITE() {
        return ELITE;
    }

    public static void setELITE(String ELITE) {
        PermStrings.ELITE = ELITE;
    }

    public static String getEditGames() {
        return editGames;
    }

    public static void setEditGames(String editGames) {
        PermStrings.editGames = editGames;
    }
}
