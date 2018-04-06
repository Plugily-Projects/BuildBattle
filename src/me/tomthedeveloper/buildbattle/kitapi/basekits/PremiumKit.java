package me.tomthedeveloper.buildbattle.kitapi.basekits;

import me.tomthedeveloper.buildbattle.handlers.ChatManager;
import me.tomthedeveloper.buildbattle.utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Tom on 25/07/2014.
 */
public abstract class PremiumKit extends Kit {

    private int pointsNeeded;

    protected PremiumKit() {
    }


    public int getPointsNeeded() {
        return pointsNeeded;
    }


    public void setPointsNeeded(int pointsNeeded) {
        this.pointsNeeded = pointsNeeded;
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(getMaterial());
        setItemNameAndLore(itemStack, getName(), getDescription());
        Util.addLore(itemStack, ChatManager.getFromLanguageConfig("Unlock-This-Kit-In-The-Store", ChatColor.AQUA + "Unlock this in the store!"));
        return itemStack;
    }
}
