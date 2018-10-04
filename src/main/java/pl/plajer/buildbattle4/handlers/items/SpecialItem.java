/*
 * BuildBattle 4 - Ultimate building competition minigame
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.buildbattle4.handlers.items;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle4.Main;
import pl.plajer.buildbattle4.handlers.ChatManager;
import pl.plajer.buildbattle4.utils.Utils;
import pl.plajerlair.core.utils.ConfigUtils;

/**
 * Created by Tom on 5/02/2016.
 */
public class SpecialItem {

  private Material material;
  private Byte data = null;
  private String[] lore;
  private String displayName;
  private int slot;
  private String name;

  private SpecialItem(String name) {
    this.name = name;
  }

  public static void loadAll() {
    new SpecialItem("Leave").load();
  }

  private void load() {
    FileConfiguration config = ConfigUtils.getConfig(JavaPlugin.getPlugin(Main.class), "SpecialItems");
    SpecialItem particleItem = new SpecialItem(name);
    particleItem.setData(config.getInt(name + ".data"));
    particleItem.setMaterial(org.bukkit.Material.getMaterial(config.getInt(name + ".material")));
    particleItem.setLore(config.getStringList(name + ".lore"));
    particleItem.setDisplayName(config.getString(name + ".displayname"));
    particleItem.setSlot(config.getInt(name + ".slot"));
    SpecialItemManager.addEntityItem(name, particleItem);
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

  private void setLore(List<String> lore) {
    this.lore = lore.toArray(new String[0]);
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
    if (data != null) {
      itemStack = new ItemStack(getMaterial(), 1, getData());
    } else {
      itemStack = new ItemStack(getMaterial());

    }
    Utils.setItemNameAndLore(itemStack, ChatManager.colorRawMessage(this.getDisplayName()), lore);
    return itemStack;
  }


}
