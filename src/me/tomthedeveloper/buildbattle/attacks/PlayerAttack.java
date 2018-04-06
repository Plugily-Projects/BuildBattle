package me.tomthedeveloper.buildbattle.attacks;

import org.bukkit.entity.Player;

/**
 * Created by Tom on 1/08/2014.
 */
public abstract class PlayerAttack extends Attack {

    private Player player;


    public PlayerAttack(int ticks, Player player) {
        super(ticks);
        this.player = player;
    }

    public Player getAttacker() {
        return player;
    }
}
