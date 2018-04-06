package me.tomthedeveloper.buildbattle.kitapi;

import me.tomthedeveloper.buildbattle.kitapi.basekits.FreeKit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by Tom on 28/02/2016.
 */
public class DefaultKit extends FreeKit {

    public DefaultKit() {
        setName("");
        setDescription(new String[]{""});
    }


    @Override
    public boolean isUnlockedByPlayer(Player p) {
        return true;
    }

    @Override
    public void giveKitItems(Player player) {

    }

    @Override
    public Material getMaterial() {
        return Material.BEDROCK;
    }

    @Override
    public void reStock(Player player) {

    }
}
