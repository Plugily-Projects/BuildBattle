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

package pl.plajer.buildbattle3.menus.playerheads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.handlers.ChatManager;
import pl.plajerlair.core.utils.ConfigUtils;
import pl.plajerlair.core.utils.MinigameUtils;

/**
 * Created by Tom on 26/08/2015.
 */
//todo texture loading
public class PlayerHeadsMenu {

  private static List<HeadsItem> headsItems = new ArrayList<>();
  private static Map<String, List<HeadsItem>> playerheadmenus = new HashMap<>();
  private static Map<String, Inventory> inventories = new HashMap<>();

  public static void loadHeadItems() {
    FileConfiguration config = ConfigUtils.getConfig(Main.getPlugin(Main.class), "playerheadmenu/mainmenu");
    if (!config.contains("animals")) {
      config.set("animals.displayname", "&6" + "Animals");
      config.set("animals.lore", Arrays.asList("Click to open", "animals head menu"));
      config.set("animals.enabled", true);
      config.set("animals.config", "animalheads");
      config.set("animals.permission", "particles.VIP");
      config.set("animals.texture", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjIxNjY4ZWY3Y2I3OWRkOWMyMmNlM2QxZjNmNGNiNmUyNTU5ODkzYjZkZjRhNDY5NTE0ZTY2N2MxNmFhNCJ9fX0=");
      config.set("animals.menuname", "Animal Heads Menu");
    }
    try {
      config.save(ConfigUtils.getFile(JavaPlugin.getPlugin(Main.class), "playerheadmenu/mainmenu"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    for (String str : config.getKeys(false)) {
      HeadsItem headsItem = new HeadsItem();
      headsItem.setEnabled(config.getBoolean(str + ".enabled"));
      headsItem.setLore(config.getStringList(str + ".lore"));
      headsItem.setDisplayName(config.getString(str + ".displayname"));
      headsItem.setPermission(config.getString(str + ".permission"));
      headsItem.setConfig(config.getString(str + ".config"));
      headsItem.setMenuName(config.getString(str + ".menuname"));
      headsItem.setTexture(config.getString(str + ".texture"));
      if (headsItem.isEnabled()) headsItems.add(headsItem);
    }
    for (HeadsItem headsItem : headsItems) {
      config = headsItem.getConfig();
      Inventory inv;
      List<HeadsItem> list = new ArrayList<>();
      if (!config.contains("example")) {
        config.set("example.displayname", "&6" + "Animals");
        config.set("example.texture", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjIxNjY4ZWY3Y2I3OWRkOWMyMmNlM2QxZjNmNGNiNmUyNTU5ODkzYjZkZjRhNDY5NTE0ZTY2N2MxNmFhNCJ9fX0=");
        config.set("example.lore", Collections.singletonList(ChatManager.colorRawMessage("&7Click to select")));
        config.set("example.enabled", true);
        try {
          config.save(ConfigUtils.getFile(JavaPlugin.getPlugin(Main.class), "playerheadmenu/menus/" + headsItem.getConfigName()));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      for (String path : headsItem.getConfig().getKeys(false)) {
        HeadsItem heads = new HeadsItem();
        heads.setEnabled(config.getBoolean(path + ".enabled"));
        heads.setLore(config.getStringList(path + ".lore"));
        heads.setDisplayName(config.getString(path + ".displayname"));
        heads.setPermission(config.getString(path + ".permission"));
        heads.setTexture(config.getString(path + ".texture"));
        if (heads.isEnabled()) list.add(heads);
      }
      playerheadmenus.put(headsItem.getMenuName(), list);
      inv = Bukkit.createInventory(null, MinigameUtils.serializeInt(list.size()), headsItem.getMenuName());
      int i = 0;
      for (HeadsItem item : list) {
        if (item.isEnabled()) {
          inv.setItem(i, item.getItemStack());
          i++;
        }
      }
      inventories.put(headsItem.getMenuName(), inv);
    }
  }

  public static void openMenu(Player player) {
    Inventory inventory = player.getServer().createInventory(player, MinigameUtils.serializeInt(headsItems.size()), ChatManager.colorMessage("Menus.Option-Menu.Players-Heads-Inventory-Name"));
    int i = 0;
    for (HeadsItem headsItem : headsItems) {
      if (headsItem.isEnabled()) {
        inventory.setItem(i, headsItem.getItemStack());
        i++;
      }
    }
    player.openInventory(inventory);
  }

  public static Set<String> getMenuNames() {
    return playerheadmenus.keySet();
  }

  public static void onClickInMainMenu(Player player, ItemStack itemStack) {
    for (HeadsItem headsItem : headsItems) {
      if (headsItem.getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(itemStack.getItemMeta().getDisplayName())) {
        if (!player.hasPermission(headsItem.getPermission())) {
          player.sendMessage(ChatManager.colorMessage("Menus.Option-Menu.Players-Heads-No-Permission"));
          return;
        } else {
          player.openInventory(inventories.get(headsItem.getMenuName()));
                    /*Inventory inventory = player.getServer().createInventory(player, headsItem.getSize(), headsItem.getMenuName());
                    List<HeadsItem> list = playerheadmenus.get(headsItem.getMenuName());
                    for(HeadsItem headsItem1 : list) {
                        if(headsItem.isEnabled()) inventory.setItem(headsItem1.getSlot(), headsItem1.getItemStack());
                    }
                    player.openInventory(inventory);*/
          return;
        }
      }
    }
  }

  public static void onClickInDeeperMenu(Player player, ItemStack itemStack) {
    player.getInventory().addItem(itemStack.clone());
    player.closeInventory();
  }

}
