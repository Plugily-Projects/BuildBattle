package me.tomthedeveloper.buildbattle;

import me.TomTheDeveloper.Handlers.ConfigurationManager;
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
        if(config.getKeys(false) == null || config.getKeys(false).size() == 0) {
            setToConfig(ChatColor.RED + "Poop", Material.STAINED_CLAY, 14, 2);
            setToConfig(ChatColor.RED + "Not good", Material.STAINED_CLAY, 6, 3);
            setToConfig(ChatColor.YELLOW + "Ok", Material.STAINED_CLAY, 1, 4);
            setToConfig(ChatColor.GREEN + "Good", Material.STAINED_CLAY, 13, 5);
            setToConfig(ChatColor.GREEN + "Awesome", Material.STAINED_CLAY, 4, 6);
            setToConfig(ChatColor.DARK_PURPLE + "EPIC", Material.STAINED_CLAY, 10, 7);
            saveConfig();
        }
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

    private static void saveConfig() {
        try {
            config.save(ConfigurationManager.getFile("VoteItems"));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static int getPoints(ItemStack itemStack) {
        for(ItemStack voteitem : voteItems.keySet()) {
            if(itemStack.getType() == voteitem.getType()
                    && itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(voteitem.getItemMeta().getDisplayName()))
                return voteItems.get(voteitem);
        }
        return 0;
    }

    private static void setToConfig(String itemname, Material type, Integer data, Integer points) {
        config.set(points.toString() + ".material", type.getId());
        config.set(points.toString() + ".displayname", itemname);
        config.set(points.toString() + ".data", data);
    }


}
