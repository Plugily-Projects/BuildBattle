package me.tomthedeveloper.buildbattle.attacks;

import org.bukkit.entity.Player;

/**
 * Created by Tom on 1/08/2014.
 */
public abstract class PlayerShootAttack extends ShootAttack {

    private Player player;


    public PlayerShootAttack(int ticks, Player player) {
        super(ticks);
        this.player = player;
    }

    public Player getAttacker() {
        return player;
    }


}
