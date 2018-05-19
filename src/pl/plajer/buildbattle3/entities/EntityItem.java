/*
 *  Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.plajer.buildbattle3.entities;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import pl.plajer.buildbattle3.handlers.ChatManager;
import pl.plajer.buildbattle3.handlers.ConfigurationManager;
import pl.plajer.buildbattle3.utils.Util;

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
    private Particle effect;
    private Location location;
    private int slot;
    private String name;

    private EntityItem(String name) {
        this.name = name;
    }

    public static void loadAll() {
        new EntityItem("Adult").load("§aAge", new String[]{"§aAdult or baby? Click to change", "§7Selected: §aAdult"}, Material.EGG, 2);
        new EntityItem("Baby").load("§aAge ", new String[]{"§aAdult or baby? Click to change", "§7Selected: §cBaby"}, Material.EGG, 2);
        new EntityItem("Move-On").load("§aMoving", new String[]{"§7Click to disable moving", "§7Selected: §aOn"}, Material.ANVIL, 0);
        new EntityItem("Move-Off").load("§aMoving", new String[]{"§7Click to enabled moving", "§7Selected: §cOff"}, Material.ANVIL, 0);
        new EntityItem("Close").load("§cClose", new String[]{"§7Click to close"}, Material.REDSTONE_BLOCK, 8);
        new EntityItem("Despawn").load("§cDestroy entity", new String[]{"§7Click to destroy"}, Material.BEDROCK, 7);
        new EntityItem("Saddle-On").load("§aSaddled?", new String[]{"§7Click to remove saddle", "§7Selected: §aOn"}, Material.SADDLE, 3);
        new EntityItem("Saddle-Off").load("§aSaddled?", new String[]{"§7Click to enable saddle", "§7Selected: §cOff"}, Material.SADDLE, 3);
        new EntityItem("Look-At-Me").load("§6Look", new String[]{"§7Click to let mob look at you"}, Material.COMPASS, 1);
        new EntityItem("Profession-Villager-Selecting").load("§6Choose Profession", new String[]{"§7Click to choose profession"}, Material.RED_ROSE, 3);
        new EntityItem("Profession.Librarian").load("§6Librarian", new String[]{"§7Click to choose librarian"}, Material.BOOKSHELF, 1);
        new EntityItem("Profession.Butcher").load("§6Butcher", new String[]{"§7Click to choose butcher"}, Material.COOKED_BEEF, 2);
        new EntityItem("Profession.Priest").load("§6Priest", new String[]{"§7Click to choose priest"}, Material.FEATHER, 3);
        new EntityItem("Profession.Blacksmith").load("§6Blacksmith", new String[]{"§7Click to choose blacksmith"}, Material.IRON_CHESTPLATE, 4);
        new EntityItem("Profession.Farmer").load("§6Farmer", new String[]{"§7Click to choose farmer"}, Material.WHEAT, 0);

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
        particleItem.setMaterial(org.bukkit.Material.getMaterial(config.getInt(name + ".material")));
        particleItem.setLore(config.getStringList(name + ".lore"));
        particleItem.setDisplayName(config.getString(name + ".displayname"));
        particleItem.setSlot(config.getInt(name + ".slot"));
        EntityItemManager.addEntityItem(name, particleItem);

    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
        return ChatManager.colorRawMessage(displayName);
    }

    private void setDisplayName(String displayName) {
        this.displayName = displayName;
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
        Util.setItemNameAndLore(itemStack, ChatManager.colorRawMessage(this.getDisplayName()), lore);
        return itemStack;
    }
}

