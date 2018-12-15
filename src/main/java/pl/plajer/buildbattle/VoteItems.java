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

package pl.plajer.buildbattle;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajerlair.core.utils.ConfigUtils;
import pl.plajerlair.core.utils.XMaterial;

/**
 * Created by Tom on 17/08/2015.
 */
public class VoteItems {

  private static Map<ItemStack, Integer> voteItems = new HashMap<>();
  private static ItemStack reportItem;
  private static FileConfiguration config = ConfigUtils.getConfig(JavaPlugin.getPlugin(Main.class), "voteItems");

  public VoteItems() {
    loadVoteItemsFromConfig();
  }

  private static void loadVoteItemsFromConfig() {
    for (String s : config.getKeys(false)) {
      if (config.contains(s + ".displayname")) {
        if (!config.isSet(s + ".material-name")) {
          config.set(s + ".material-name", XMaterial.GREEN_TERRACOTTA.name());
          Main.debug(Main.LogLevel.WARN, "Found outdated item in votingItems.yml! We've converted it to the newest version!");
        }
        ConfigUtils.saveConfig(JavaPlugin.getPlugin(Main.class), config, "voteItems");
        ItemStack item = XMaterial.fromString(config.getString(s + ".material-name").toUpperCase()).parseItem();
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatManager.colorRawMessage(config.getString(s + ".displayname")));
        item.setItemMeta(itemMeta);

        //set to something random for now
        reportItem = new ItemStack(Material.BEDROCK, 32);
        if (config.isSet(s + ".report-item-function") && config.getBoolean(s + ".report-item-function", true)) {
          reportItem = item;
          continue;
        }
        voteItems.put(item, Integer.parseInt(s));
      }
    }
  }

  public void giveVoteItems(Player player) {
    for (ItemStack itemStack : voteItems.keySet()) {
      player.getInventory().setItem(voteItems.get(itemStack), itemStack);
    }
    player.updateInventory();
  }

  public int getPoints(ItemStack itemStack) {
    for (ItemStack voteItem : voteItems.keySet()) {
      if (itemStack.getType() == voteItem.getType() && itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(voteItem.getItemMeta().getDisplayName())) {
        return voteItems.get(voteItem) + 1;
      }
    }
    return 1;
  }

  /**
   * @return itemStack that represents report building function
   */
  public ItemStack getReportItem() {
    return reportItem;
  }
}
