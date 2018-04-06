package me.tomthedeveloper.buildbattle.utils;

import org.bukkit.Bukkit;

/**
 * Created by TomVerschueren on 10/09/2017.
 */
public class VersionChecker {

    private static final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];


    public static boolean is1_8_R3() {
        if(version.equalsIgnoreCase("v1_8_R3")) return true;
        return false;
    }

    public static boolean is1_12_R1() {
        if(version.equalsIgnoreCase("v1_12_R1")) return true;
        return false;
    }

    public static boolean is1_7_R4() {
        if(version.equalsIgnoreCase("v1_7_R4")) return true;
        return false;
    }

    public static boolean is1_9_R1() {
        if(version.equalsIgnoreCase("v1_9_R1")) return true;
        return false;
    }


}
