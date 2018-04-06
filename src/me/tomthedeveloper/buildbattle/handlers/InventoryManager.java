package me.tomthedeveloper.buildbattle.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Created by Tom on 15/02/2015.
 */
public class InventoryManager {

    private JavaPlugin plugin;


    public InventoryManager(JavaPlugin gameAPI) {
        plugin = gameAPI;
    }


    public boolean saveInventoryToFile(Player player) {
        String UUID = player.getUniqueId().toString();
        PlayerInventory inventory = player.getInventory();
        File path = new File(plugin.getDataFolder() + File.separator + "inventorys");
        if(inventory == null || path == null || UUID == null) return false;
        try {
            File invFile = new File(plugin.getDataFolder() + File.separator + "inventorys" + File.separator, UUID + ".invsave");
            if(!path.exists()) path.mkdir();
            if(invFile.exists()) invFile.delete();
            FileConfiguration invConfig = YamlConfiguration.loadConfiguration(invFile);

            invConfig.set("Exp", player.getExpToLevel());
            invConfig.set("Size", inventory.getSize());
            invConfig.set("Max stack size", inventory.getMaxStackSize());
            if(inventory.getHolder() instanceof Player) invConfig.set("Holder", ((Player) inventory.getHolder()).getName());

            ItemStack[] invContents = inventory.getContents();
            for(int i = 0; i < invContents.length; i++) {
                ItemStack itemInInv = invContents[i];
                if(itemInInv != null) if(itemInInv.getType() != Material.AIR.AIR) invConfig.set("Slot " + i, itemInInv);
            }

            ItemStack[] armorContents = inventory.getArmorContents();
            for(int b = 0; b < armorContents.length; b++) {
                ItemStack itemStack = armorContents[b];
                if(itemStack != null) if(itemStack.getType() != Material.AIR) invConfig.set("Armor " + b, itemStack);
            }

            invConfig.save(invFile);
            return true;
        } catch(Exception ex) {
            System.out.print("NOT WORKING!");
            return false;
        }
    }

    public Inventory getInventoryFromFile(String UUID) {
        File file = new File(plugin.getDataFolder() + File.separator + "inventorys" + File.separator + UUID + ".invsave");
        if(file == null) return null;
        if(!file.exists() || file.isDirectory() || !file.getAbsolutePath().endsWith(".invsave")) return null;
        try {
            FileConfiguration invConfig = YamlConfiguration.loadConfiguration(file);
            Inventory inventory = null;
            Integer invTitle = invConfig.getInt("Exp");
            int invSize = invConfig.getInt("Size", 36);
            int invMaxStackSize = invConfig.getInt("Max stack size", 64);
            InventoryHolder invHolder = null;
            if(invConfig.contains("Holder")) invHolder = Bukkit.getPlayer(invConfig.getString("Holder"));
            inventory = Bukkit.getServer().createInventory(invHolder, InventoryType.PLAYER, Integer.toString(invTitle));
            inventory.setMaxStackSize(invMaxStackSize);
            try {
                ItemStack[] invContents = new ItemStack[invSize];
                for(int i = 0; i < invSize; i++) {
                    if(invConfig.contains("Slot " + i)) invContents[i] = invConfig.getItemStack("Slot " + i);
                    else invContents[i] = new ItemStack(Material.AIR);
                }
                inventory.setContents(invContents);

            } catch(Exception ex) {
                System.out.print("Armor getting not working?");
            }
            file.delete();
            return inventory;
        } catch(Exception ex) {

            ex.printStackTrace();
            return null;
        }
    }

    public void loadInventory(Player player) {


        File file = new File(plugin.getDataFolder() + File.separator + "inventorys" + File.separator + player.getUniqueId().toString() + ".invsave");
        if(file == null) return;
        if(!file.exists() || file.isDirectory() || !file.getAbsolutePath().endsWith(".invsave")) return;
        try {
            FileConfiguration invConfig = YamlConfiguration.loadConfiguration(file);


            try {
                ItemStack[] armor = new ItemStack[player.getInventory().getArmorContents().length];
                for(int i = 0; i < player.getInventory().getArmorContents().length; i++) {
                    if(invConfig.contains("Armor " + i)) armor[i] = invConfig.getItemStack("Armor " + i);
                    else armor[i] = new ItemStack(Material.AIR);
                }
                player.getInventory().setArmorContents(armor);

            } catch(Exception ex) {
            }

        } catch(Exception ex) {

        }
        Inventory inventory = this.getInventoryFromFile(player.getUniqueId().toString());

        for(Integer i = 0; i < inventory.getContents().length; i++) {
            if(inventory.getItem(i) != null) player.getInventory().setItem(i, inventory.getItem(i));

        }

        player.updateInventory();
        player.setLevel(Integer.valueOf(inventory.getTitle()));


    }
}
