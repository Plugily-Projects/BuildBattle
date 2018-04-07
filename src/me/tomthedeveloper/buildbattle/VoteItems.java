package me.tomthedeveloper.buildbattle;

import me.tomthedeveloper.buildbattle.handlers.ConfigurationManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Tom on 17/08/2015.
 */
public class VoteItems {

    private static HashMap<ItemStack, Integer> voteItems = new HashMap<>();
    private static FileConfiguration config = ConfigurationManager.getConfig("VoteItems");

    public static void giveVoteItems(Player player) {
        for(ItemStack itemStack : voteItems.keySet()) {
            player.getInventory().setItem(voteItems.get(itemStack), itemStack);
        }
        player.updateInventory();
    }


    public static void loadVoteItemsFromConfig() {
        for(String s : config.getKeys(false)) {
            if(StringUtils.isNumeric(s) && config.contains(s + ".material") && config.contains(s + ".data") && config.contains(s + ".displayname")) {
                ItemStack item = new ItemStack(config.getInt(s + ".material"), 1, (byte) config.getInt(s + ".data"));
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(ChatFormatter.formatMessage(config.getString(s + ".displayname")));
                item.setItemMeta(itemMeta);
                voteItems.put(item, Integer.parseInt(s));

            }
        }
    }

    public static int getPoints(ItemStack itemStack) {
        for(ItemStack voteitem : voteItems.keySet()) {
            if(itemStack.getType() == voteitem.getType() && itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(voteitem.getItemMeta().getDisplayName()))
                return voteItems.get(voteitem);
        }
        return 0;
    }

}
