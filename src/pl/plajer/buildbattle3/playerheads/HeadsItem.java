/*
 * BuildBattle 3 - Ultimate building competition minigame
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

package pl.plajer.buildbattle3.playerheads;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import pl.plajer.buildbattle3.handlers.ChatManager;
import pl.plajer.buildbattle3.handlers.ConfigurationManager;
import pl.plajer.buildbattle3.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Tom on 26/08/2015.
 */
class HeadsItem {

    private Material material;
    private Byte data = null;
    private String[] lore;
    private String displayName;
    private String permission;
    private boolean enabled = true;
    private Location location;
    private int slot;
    private String owner;
    private String config;
    private int size = 18;
    private String menuName;
    private ItemStack itemStack = null;

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    private String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public FileConfiguration getConfig() {
        return ConfigurationManager.getConfig("playerheadmenu/menus/" + config);
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getConfigName() {
        return config;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setData(Byte data) {
        this.data = data;
    }

    private Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    private byte getData() {
        return data;
    }

    public void setData(Integer data) {
        this.data = data.byteValue();
    }

    private List<String> getLore() {
        List<String> lorelist = new ArrayList<>();
        for(String string : lore) {
            string = ChatManager.colorRawMessage(string);
            lorelist.add(string);
        }
        return lorelist;
    }

    public void setLore(String[] lore) {
        this.lore = lore;
    }

    public void setLore(List<String> lore) {

        this.lore = lore.toArray(new String[lore.size()]);
    }

    private String getDisplayName() {
        return ChatManager.colorRawMessage(displayName);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public ItemStack getItemStack() {
        if(itemStack == null) {
            if(data != null) {
                itemStack = new ItemStack(getMaterial(), 1, getData());
            } else {
                itemStack = new ItemStack(getMaterial());

            }
            if(itemStack.getType() == Material.SKULL_ITEM && itemStack.getData().getData() == SkullType.PLAYER.ordinal()) {
                if(getOwner().matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}")) {
                    itemStack = Util.getPlayerHead(Bukkit.getOfflinePlayer(UUID.fromString(getOwner())));
                } else {
                    itemStack = Util.getPlayerHead(Bukkit.getOfflinePlayer(getOwner()));
                }
            }
            Util.setItemNameAndLore(itemStack, ChatManager.colorRawMessage(this.getDisplayName()), getLore().toArray(new String[getLore().size()]));

            return itemStack;
        } else {
            return itemStack;
        }
    }
}
