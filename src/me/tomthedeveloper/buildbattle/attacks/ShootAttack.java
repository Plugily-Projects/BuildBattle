package me.tomthedeveloper.buildbattle.attacks;

import org.bukkit.Location;

/**
 * Created by Tom on 1/08/2014.
 */
public abstract class ShootAttack extends Attack {

    protected ShootAttack(int ticks) {
        super(ticks);
    }

    public abstract Location getLocation();
}
