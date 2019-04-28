/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and contributors
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

package pl.plajer.buildbattle.arena.vote;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.utils.Debugger;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;

/**
 * Created by Tom on 17/08/2015.
 */
public class VoteItems {

  private static Set<VoteItem> voteItems = new HashSet<>();
  private static ItemStack reportItem = new ItemStack(Material.BEDROCK, 32);
  private static FileConfiguration config = ConfigUtils.getConfig(JavaPlugin.getPlugin(Main.class), "voteItems");

  public VoteItems() {
    updateVoteItemsConfig();
    loadVoteItems();
  }

  private void loadVoteItems() {
    for (String key : config.getKeys(false)) {
      if (!config.isSet(key + ".displayname")) {
        continue;
      }
      ItemStack stack = new ItemBuilder(XMaterial.fromString(config.getString(key + ".material-name").toUpperCase()).parseItem())
          .name(JavaPlugin.getPlugin(Main.class).getChatManager().colorRawMessage(config.getString(key + ".displayname")))
          .build();
      if (config.getBoolean(key + ".report-item-function", false)) {
        reportItem = stack;
        continue;
      }
      voteItems.add(new VoteItem(stack, Integer.parseInt(key), Integer.parseInt(key) + 1));
    }
  }

  private void updateVoteItemsConfig() {
    for (String key : config.getKeys(false)) {
      if (!config.isSet(key + ".displayname")) {
        continue;
      }
      if (config.isSet(key + ".material-name")) {
        continue;
      }
      config.set(key + ".material-name", XMaterial.GREEN_TERRACOTTA.name());
      Debugger.debug(Debugger.Level.WARN, "Found outdated item in votingItems.yml! We've converted it to the newest version!");
    }
    ConfigUtils.saveConfig(JavaPlugin.getPlugin(Main.class), config, "voteItems");
  }

  public void giveVoteItems(Player player) {
    for (VoteItem voteItem : voteItems) {
      player.getInventory().setItem(voteItem.getSlot(), voteItem.getItemStack());
    }
    player.updateInventory();
  }

  /**
   * Get points value of target vote item stack
   *
   * @param itemStack item stack to get points value from
   * @return points
   */
  public int getPoints(ItemStack itemStack) {
    for (VoteItem item : voteItems) {
      if (item.getItemStack().isSimilar(itemStack)) {
        return item.getPoints();
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

  public class VoteItem {

    private ItemStack itemStack;
    private int slot;
    private int points;

    public VoteItem(ItemStack itemStack, int slot, int points) {
      this.itemStack = itemStack;
      this.slot = slot;
      this.points = points;
    }

    public ItemStack getItemStack() {
      return itemStack;
    }

    public int getSlot() {
      return slot;
    }

    public int getPoints() {
      return points;
    }
  }

}
