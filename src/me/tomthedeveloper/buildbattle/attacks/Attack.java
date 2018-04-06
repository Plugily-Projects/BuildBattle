package me.tomthedeveloper.buildbattle.attacks;

import me.tomthedeveloper.buildbattle.GameAPI;
import org.bukkit.Location;

/**
 * Created by Tom on 30/07/2014.
 */
public abstract class Attack {


    public static GameAPI plugin;
    private int ticks;
    private int counter;


    public Attack(int ticks) {
        this.ticks = ticks;
    }

    public static AttackDirection getDirection(Location location) {
        float number = location.getYaw();
        if(number > -20 && number < 20) return AttackDirection.SOUTH;
        if(number < -340) return AttackDirection.SOUTH;
        if(number > 340) return AttackDirection.SOUTH;
        if(number > 160 && number < 200) return AttackDirection.NORTH;
        if(number > -200 && number < -180) return AttackDirection.NORTH;
        if(number > 250 && number < 290) return AttackDirection.EAST;
        if(number > -110 && number < -70) return AttackDirection.EAST;
        if(number > -290 && number < -250) return AttackDirection.WEST;
        if(number > 70 && number < 110) return AttackDirection.WEST;
        return AttackDirection.NORTH_WEST;


    }

    public abstract void run();

    public int getTicks() {
        return ticks;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public enum AttackDirection {
        NORTH, WEST, SOUTH, EAST, NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST
    }
}
