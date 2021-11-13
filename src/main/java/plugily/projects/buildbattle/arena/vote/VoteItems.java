/*
 *
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2021 Plugily Projects - maintained by Tigerpanzer_02, 2Wild4You and contributors
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
 *
 */

package plugily.projects.buildbattle.arena.vote;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import plugily.projects.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.commonsbox.minecraft.compat.xseries.XSound;
import plugily.projects.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.commonsbox.minecraft.item.ItemBuilder;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.utils.Debugger;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Tom on 17/08/2015.
 */
public class VoteItems {

  private final Set<VoteItem> voteItems = new HashSet<>();
  private final FileConfiguration config;
  private final Main plugin;

  private ItemStack reportItem = new ItemStack(Material.BEDROCK, 32);
  private VoteItem reportVoteItem = new VoteItem(new ItemStack(Material.BEDROCK, 32), 8, 8 + 1, XSound.ENTITY_ARROW_HIT.parseSound());

  public VoteItems(Main plugin) {
    this.plugin = plugin;
    config = ConfigUtils.getConfig(plugin, "voteItems");

    updateVoteItemsConfig();
    loadVoteItems();
  }

  private void loadVoteItems() {
    for(String key : config.getKeys(false)) {
      String displayName = config.getString(key + ".displayname", null);

      if(displayName == null) {
        continue;
      }

      ItemStack stack = new ItemBuilder(XMaterial.matchXMaterial(config.getString(key + ".material-name", "BEDROCK")
          .toUpperCase(java.util.Locale.ENGLISH)).orElse(XMaterial.BEDROCK).parseItem())
          .name(plugin.getChatManager().colorRawMessage(displayName))
          .build();

      if(config.getBoolean(key + ".report-item-function", false)) {
        reportItem = stack;
      }

      Sound sound = null;
      try {
        sound = Sound.valueOf(config.getString(key + ".sound", ""));
      } catch(IllegalArgumentException ignored) {
      }

      int s;
      try {
        s = Integer.parseInt(key);
      } catch (NumberFormatException e) {
        continue;
      }

      voteItems.add(reportVoteItem = new VoteItem(stack, s, s + 1, sound));
    }
  }

  private void updateVoteItemsConfig() {
    for(String key : config.getKeys(false)) {
      if(!config.isSet(key + ".displayname") || config.isSet(key + ".material-name")) {
        continue;
      }
      config.set(key + ".material-name", XMaterial.GREEN_TERRACOTTA.name());
      Debugger.debug(Debugger.Level.WARN, "Found outdated item in votingItems.yml! We've converted it to the newest version!");
    }
    ConfigUtils.saveConfig(plugin, config, "voteItems");
  }

  public void giveVoteItems(Player player) {
    for(VoteItem voteItem : voteItems) {
      player.getInventory().setItem(voteItem.slot, voteItem.itemStack);
    }
    player.updateInventory();
  }

  public void playVoteSound(Player player, ItemStack itemStack) {
    for(VoteItem item : voteItems) {
      if(item.itemStack.isSimilar(itemStack)) {
        if(item.sound == null) {
          return;
        }
        player.playSound(player.getLocation(), item.sound, 1, 1);
      }
    }
  }

  /**
   * Get points value of target vote item stack
   *
   * @param itemStack item stack to get points value from
   * @return points
   */
  public int getPoints(ItemStack itemStack) {
    for(VoteItem item : voteItems) {
      if(item.itemStack.isSimilar(itemStack)) {
        return item.points;
      }
    }
    return 1;
  }

  public int getPointsAndPlayVoteSound(Player player, ItemStack itemStack) {
    for(VoteItem item : voteItems) {
      if(item.itemStack.isSimilar(itemStack)) {
        if(item.sound == null) {
          return item.points;
        }

        player.playSound(player.getLocation(), item.sound, 1, 1);
        return item.points;
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

  /**
   * @return itemStack that represents report building function
   */
  public VoteItem getReportVoteItem() {
    return reportVoteItem;
  }

  public static class VoteItem {

    private final ItemStack itemStack;
    private final int slot;
    private final int points;
    private final Sound sound;

    public VoteItem(ItemStack itemStack, int slot, int points, Sound sound) {
      this.itemStack = itemStack;
      this.slot = slot;
      this.points = points;
      this.sound = sound;
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

    @Nullable
    public Sound getSound() {
      return sound;
    }
  }

}
