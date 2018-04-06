package me.tomthedeveloper.buildbattle.utils;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;

/**
 * Created by Tom on 31/07/2014.
 */
public class Rollback {


    public static void unloadMap(String mapname) {
        if(Bukkit.getServer().unloadWorld(Bukkit.getServer().getWorld(mapname), false)) {
            System.out.println("Successfully unloaded " + mapname);
        } else {
            System.out.println("COULD NOT UNLOAD " + mapname);
        }
    }

    //Loading maps (MUST BE CALLED AFTER UNLOAD MAPS TO FINISH THE ROLLBACK PROCESS)
    public static void loadMap(String mapname) {
        Bukkit.getServer().createWorld(new WorldCreator(mapname));
    }

    //Maprollback method, because were too lazy to type 2 lines
    public static void rollback(String mapname) {
        unloadMap(mapname);
        loadMap(mapname);
    }
}
