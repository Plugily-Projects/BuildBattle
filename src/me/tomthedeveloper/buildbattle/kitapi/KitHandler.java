package me.tomthedeveloper.buildbattle.kitapi;

import me.tomthedeveloper.buildbattle.kitapi.basekits.Kit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 25/07/2014.
 */
public class KitHandler {


    private List<Kit> kits = new ArrayList<Kit>();
    private Kit defaultKit = null;

    public void registerKit(Kit kit) {
        kits.add(kit);
    }

    public Kit getDefaultKit() {
        return defaultKit;
    }

    public void setDefaultKit(Kit defaultKit) {
        this.defaultKit = defaultKit;
    }

    public List<Kit> getKits() {
        return kits;
    }

    public void removeKit(Kit kit) {
        if(kits.contains(kit)) kits.remove(kit);
        else System.out.print("ERROR: Kit isn't existing!");
    }

    public Kit getKit(ItemStack itemStack) {
        Kit returnkit = getDefaultKit();
        for(Kit kit : kits) {
            if(itemStack.getType() == kit.getMaterial()) {
                returnkit = kit;
                break;
            }
        }
        if(returnkit == null) {
            throw new NullPointerException("ERROR: kit doesn't exist!");
        } else {
            return returnkit;
        }
    }


}
