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

package pl.plajer.buildbattle3;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import pl.plajer.buildbattle3.handlers.ChatManager;
import pl.plajer.buildbattle3.handlers.ConfigurationManager;

/**
 * Created by Tom on 17/08/2015.
 */
public class VoteItems {

  private static Map<ItemStack, Integer> voteItems = new HashMap<>();
  private static FileConfiguration config = ConfigurationManager.getConfig("voteItems");

  public static void giveVoteItems(Player player) {
    for (ItemStack itemStack : voteItems.keySet()) {
      player.getInventory().setItem(voteItems.get(itemStack), itemStack);
    }
    player.updateInventory();
  }


  public static void loadVoteItemsFromConfig() {
    for (String s : config.getKeys(false)) {
      if (StringUtils.isNumeric(s) && config.contains(s + ".material") && config.contains(s + ".data") && config.contains(s + ".displayname")) {
        ItemStack item = new ItemStack(config.getInt(s + ".material"), 1, (byte) config.getInt(s + ".data"));
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatManager.colorRawMessage(config.getString(s + ".displayname")));
        item.setItemMeta(itemMeta);
        voteItems.put(item, Integer.parseInt(s));
      }
    }
  }

  public static int getPoints(ItemStack itemStack) {
    for (ItemStack voteItem : voteItems.keySet()) {
      if (itemStack.getType() == voteItem.getType() && itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(voteItem.getItemMeta().getDisplayName()))
        return voteItems.get(voteItem) + 1;
    }
    return 1;
  }

}
