package me.tomthedeveloper.buildbattle.entities;

import me.tomthedeveloper.buildbattle.ChatFormatter;
import me.tomthedeveloper.buildbattle.handlers.ConfigurationManager;
import me.tomthedeveloper.buildbattle.utils.ParticleEffect;
import me.tomthedeveloper.buildbattle.utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tom on 23/08/2015.
 */
public class EntityItem {

    private Material material;
    private Byte data = null;
    private String[] lore;
    private String displayName;
    private ParticleEffect effect;
    private String permission;
    private boolean enabled = true;
    private Location location;
    private int slot;
    private String name;

    private EntityItem(String name) {
        this.name = name;

    }

    public static void loadAll() {
        new EntityItem("Adult").load(ChatColor.GREEN + "Age", new String[]{ChatColor.GREEN + "Adult or baby? Click to change", ChatColor.GRAY + "Selected: " + ChatColor.GREEN + "Adult"}, Material.EGG, 2);
        new EntityItem("Baby").load(ChatColor.GREEN + "Age ", new String[]{ChatColor.GREEN + "Adult or baby? Click to change", ChatColor.GRAY + "Selected: " + ChatColor.RED + "Baby"}, Material.EGG, 2);
        new EntityItem("Move-On").load(ChatColor.GREEN + "Moving", new String[]{ChatColor.GRAY + "Click to disable moving", ChatColor.GRAY + "Selected: " + ChatColor.GREEN + "On"}, Material.ANVIL, 0);
        new EntityItem("Move-Off").load(ChatColor.GREEN + "Moving", new String[]{ChatColor.GRAY + "Click to enabled moving", ChatColor.GRAY + "Selected: " + ChatColor.RED + "Off"}, Material.ANVIL, 0);
        new EntityItem("Close").load(ChatColor.RED + "Close", new String[]{ChatColor.GRAY + "Click to close"}, Material.REDSTONE_BLOCK, 8);
        new EntityItem("Despawn").load(ChatColor.RED + "Destroy entity", new String[]{ChatColor.GRAY + "Click to destroy"}, Material.BEDROCK, 7);
        new EntityItem("Saddle-On").load(ChatColor.GREEN + "Saddled?", new String[]{ChatColor.GRAY + "Click to remove saddle", ChatColor.GRAY + "Selected: " + ChatColor.GREEN + "On"}, Material.SADDLE, 3);
        new EntityItem("Saddle-Off").load(ChatColor.GREEN + "Saddled?", new String[]{ChatColor.GRAY + "Click to enable saddle", ChatColor.GRAY + "Selected: " + ChatColor.RED + "Off"}, Material.SADDLE, 3);
        new EntityItem("Look-At-Me").load(ChatColor.GOLD + "Look", new String[]{ChatColor.GRAY + "Click to let mob look at you"}, Material.COMPASS, 1);
        new EntityItem("Profession-Villager-Selecting").load(ChatColor.GOLD + "Choose Profession", new String[]{ChatColor.GRAY + "Click to choose profession"}, Material.RED_ROSE, 3);
        new EntityItem("Profession.Librarian").load(ChatColor.GOLD + "Librarian", new String[]{ChatColor.GRAY + "Click to choose librarian"}, Material.BOOKSHELF, 1);
        new EntityItem("Profession.Butcher").load(ChatColor.GOLD + "Butcher", new String[]{ChatColor.GRAY + "Click to choose butcher"}, Material.COOKED_BEEF, 2);
        new EntityItem("Profession.Priest").load(ChatColor.GOLD + "Priest", new String[]{ChatColor.GRAY + "Click to choose priest"}, Material.FEATHER, 3);
        new EntityItem("Profession.Blacksmith").load(ChatColor.GOLD + "Blacksmith", new String[]{ChatColor.GRAY + "Click to choose blacksmith"}, Material.IRON_CHESTPLATE, 4);
        new EntityItem("Profession.Farmer").load(ChatColor.GOLD + "Farmer", new String[]{ChatColor.GRAY + "Click to choose farmer"}, Material.WHEAT, 0);


    }

    private void load(String displayName, String[] lore, Material material, int slot) {
        FileConfiguration config = ConfigurationManager.getConfig("EntityMenu");


        if(!config.contains(name)) {
            config.set(name + ".data", 0);
            config.set(name + ".displayname", displayName);
            config.set(name + ".lore", Arrays.asList(lore));
            config.set(name + ".material", material.getId());
            config.set(name + ".slot", slot);
        }
        try {
            config.save(ConfigurationManager.getFile("EntityMenu"));
        } catch(IOException e) {
            e.printStackTrace();
        }
        EntityItem particleItem = new EntityItem(name);
        particleItem.setData(config.getInt(name + ".data"));
        particleItem.setEnabled(config.getBoolean(name + ".enabled"));
        particleItem.setMaterial(org.bukkit.Material.getMaterial(config.getInt(name + ".material")));
        particleItem.setLore(config.getStringList(name + ".lore"));
        particleItem.setDisplayName(config.getString(name + ".displayname"));
        particleItem.setPermission(config.getString(name + ".permission"));
        particleItem.setSlot(config.getInt(name + ".slot"));
        EntityItemManager.addEntityItem(name, particleItem);

    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getPermission() {
        return permission;
    }

    private void setPermission(String permission) {
        this.permission = permission;
    }

    public boolean isEnabled() {
        return enabled;
    }

    private void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setData(Byte data) {
        this.data = data;
    }

    public Material getMaterial() {
        return material;
    }

    private void setMaterial(Material material) {
        this.material = material;
    }

    private byte getData() {
        return data;
    }

    private void setData(Integer data) {
        this.data = data.byteValue();
    }

    public String[] getLore() {
        return lore;
    }

    public void setLore(String[] lore) {
        this.lore = lore;
    }

    private void setLore(List<String> lore) {

        this.lore = lore.toArray(new String[lore.size()]);
    }

    private String getDisplayName() {
        return ChatFormatter.formatMessage(displayName);
    }

    private void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ParticleEffect getEffect() {
        return effect;
    }

    public void setEffect(ParticleEffect effect) {
        this.effect = effect;
    }

    public int getSlot() {
        return slot;
    }

    private void setSlot(int slot) {
        this.slot = slot;
    }

    public ItemStack getItemStack() {
        ItemStack itemStack;
        if(data != null) {
            itemStack = new ItemStack(getMaterial(), 1, getData());
        } else {
            itemStack = new ItemStack(getMaterial());

        }
        Util.setItemNameAndLore(itemStack, ChatFormatter.formatMessage(this.getDisplayName()), lore);
        return itemStack;
    }
}

