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

package pl.plajer.buildbattle.menus.playerheads;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.utils.Utils;
import pl.plajerlair.core.utils.ConfigUtils;

/**
 * Created by Tom on 26/08/2015.
 */
public class HeadsItem {

  private String displayName;
  private String[] lore;
  private String permission;
  private String menuName;
  private String texture;
  private boolean enabled;
  private ItemStack itemStack = null;
  private String config;

  public String getMenuName() {
    return menuName;
  }

  public void setMenuName(String menuName) {
    this.menuName = menuName;
  }

  public FileConfiguration getConfig() {
    return ConfigUtils.getConfig(JavaPlugin.getPlugin(Main.class), "heads/menus/" + config);
  }

  public void setConfig(String config) {
    this.config = config;
  }

  public String getConfigName() {
    return config;
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

  public void setTexture(String texture) {
    this.texture = texture;
  }

  private List<String> getLore() {
    List<String> lorelist = new ArrayList<>();
    for (String string : lore) {
      string = ChatManager.colorRawMessage(string);
      lorelist.add(string);
    }
    return lorelist;
  }

  public void setLore(List<String> lore) {
    this.lore = lore.toArray(new String[0]);
  }

  private String getDisplayName() {
    return ChatManager.colorRawMessage(displayName);
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public ItemStack getItemStack() {
    if (itemStack == null) {
      itemStack = Utils.getSkull(texture);
      Utils.setItemNameAndLore(itemStack, ChatManager.colorRawMessage(this.getDisplayName()), getLore().toArray(new String[0]));
      return itemStack;
    } else {
      return itemStack;
    }
  }
}
