package me.tomthedeveloper.buildbattle.handlers;

import me.tomthedeveloper.buildbattle.attacks.Attack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 30/07/2014.
 */
public class AttackManager {

    private List<Attack> attacks = new ArrayList<Attack>();
    private List<Attack> removeattacks = new ArrayList<Attack>();


    public void registerAttack(Attack attack) {
        attacks.add(attack);
    }

    public void unregisterAttack(Attack attack) {
        removeattacks.add(attack);
    }

    public void unregisterAttacksForReal() {
        for(Attack attack : removeattacks) {
            if(attacks.contains(attack)) attacks.remove(attack);


        }
        removeattacks.clear();
    }

    public List<Attack> getAttacks() {
        return attacks;
    }
}
