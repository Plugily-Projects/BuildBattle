package pl.plajer.buildbattle.playerheads;

import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.handlers.ConfigurationManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Tom on 26/08/2015.
 */
public class PlayerHeadsMenu {

    private static List<HeadsItem> headsItems = new ArrayList<>();
    private static HashMap<String, List<HeadsItem>> playerheadmenus = new HashMap<>();

    public static void loadHeadItems() {
        FileConfiguration config = ConfigurationManager.getConfig("playerheadmenu/mainmenu");
        if(!config.contains("animals")) {
            config.set("animals.data", SkullType.PLAYER.ordinal());
            config.set("animals.displayname", "&6" + "Animals");
            config.set("animals.lore", Arrays.asList("Click to open", "animals head menu"));
            config.set("animals.material", Material.SKULL_ITEM.getId());
            config.set("animals.enabled", true);
            config.set("animals.config", "animalheads");
            config.set("animals.permission", "particles.VIP");
            config.set("animals.slot", 7);
            config.set("animals.owner", "MHF_Pig");
            config.set("animals.inventorysize", 3 * 9);
            config.set("animals.menuname", "Animal Heads Menu");
        }
        try {
            config.save(ConfigurationManager.getFile("playerheadmenu/mainmenu"));
        } catch(IOException e) {
            e.printStackTrace();
        }
        for(String str : config.getKeys(false)) {
            HeadsItem headsItem = new HeadsItem();
            headsItem.setData(config.getInt(str + ".data"));
            headsItem.setEnabled(config.getBoolean(str + ".enabled"));
            headsItem.setMaterial(org.bukkit.Material.getMaterial(config.getInt(str + ".material")));
            headsItem.setLore(config.getStringList(str + ".lore"));
            headsItem.setDisplayName(config.getString(str + ".displayname"));
            headsItem.setPermission(config.getString(str + ".permission"));
            headsItem.setOwner(config.getString(str + ".owner"));
            headsItem.setSlot(config.getInt(str + ".slot"));
            headsItem.setConfig(config.getString(str + ".config"));
            headsItem.setSize(config.getInt(str + ".inventorysize"));
            headsItem.setMenuName(config.getString(str + ".menuname"));
            if(headsItem.isEnabled()) headsItems.add(headsItem);
        }
        for(HeadsItem headsItem : headsItems) {
            config = headsItem.getConfig();
            List<HeadsItem> list = new ArrayList<>();
            if(!config.contains("example")) {
                config.set("example.data", SkullType.PLAYER.ordinal());
                config.set("example.displayname", "&6" + "Animals");
                config.set("example.owner", "MHF_Pig");
                config.set("example.lore", Collections.singletonList(ChatManager.formatMessage("&7Click to select")));
                config.set("example.material", Material.SKULL_ITEM.getId());
                config.set("example.enabled", true);
                config.set("example.slot", 7);
                try {
                    config.save(ConfigurationManager.getFile("playerheadmenu/menus/" + headsItem.getConfigName()));
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
            for(String path : headsItem.getConfig().getKeys(false)) {
                HeadsItem heads = new HeadsItem();
                heads.setData(config.getInt(path + ".data"));
                heads.setEnabled(config.getBoolean(path + ".enabled"));
                heads.setMaterial(org.bukkit.Material.getMaterial(config.getInt(path + ".material")));
                heads.setLore(config.getStringList(path + ".lore"));
                heads.setDisplayName(config.getString(path + ".displayname"));
                heads.setPermission(config.getString(path + ".permission"));
                heads.setOwner(config.getString(path + ".owner"));
                heads.setSlot(config.getInt(path + ".slot"));
                if(heads.isEnabled()) list.add(heads);
            }
            playerheadmenus.put(headsItem.getMenuName(), list);
        }
    }

    public static void openMenu(Player player) {
        Inventory inventory = player.getServer().createInventory(player, 3 * 9, ChatManager.getSingleMessage("Player-Head-Main-Inventory-Name", "Player Head Menu"));
        for(HeadsItem headsItem : headsItems) {
            if(headsItem.isEnabled()) inventory.setItem(headsItem.getSlot(), headsItem.getItemStack());
        }
        player.openInventory(inventory);
    }

    public static Set<String> getMenuNames() {
        return playerheadmenus.keySet();
    }

    public static void onClickInMainMenu(Player player, ItemStack itemStack) {
        for(HeadsItem headsItem : headsItems) {
            if(headsItem.getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(itemStack.getItemMeta().getDisplayName())) {
                if(!player.hasPermission(headsItem.getPermission())) {
                    player.sendMessage(ChatManager.getSingleMessage("No-Permission", ChatColor.RED + "U don't have permission for this!"));
                    return;
                } else {
                    Inventory inventory = player.getServer().createInventory(player, headsItem.getSize(), headsItem.getMenuName());
                    List<HeadsItem> list = playerheadmenus.get(headsItem.getMenuName());
                    for(HeadsItem headsItem1 : list) {
                        if(headsItem.isEnabled()) inventory.setItem(headsItem1.getSlot(), headsItem1.getItemStack());
                    }
                    player.openInventory(inventory);
                    return;
                }
            }
        }
    }

    public static void onClickInDeeperMenu(Player player, ItemStack itemStack, String menuname) {
        player.getInventory().addItem(itemStack.clone());
        player.closeInventory();
    }

}
