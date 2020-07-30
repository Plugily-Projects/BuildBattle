/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2020 Plugily Projects - maintained by Tigerpanzer_02, 2Wild4You and contributors
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

package plugily.projects.buildbattle.menus.options.registry.playerheads;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.menus.options.OptionsRegistry;
import plugily.projects.buildbattle.utils.Utils;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class PlayerHeadsRegistry {

  private Main plugin;
  private Map<HeadsCategory, Inventory> categories = new HashMap<>();

  public PlayerHeadsRegistry(OptionsRegistry registry) {
    this.plugin = registry.getPlugin();
    registerCategories();
  }

  private void registerCategories() {
    FileConfiguration config = ConfigUtils.getConfig(JavaPlugin.getPlugin(Main.class), "heads/mainmenu");
    for (String str : config.getKeys(false)) {
      if (!config.getBoolean(str + ".enabled", true)) {
        continue;
      }
      HeadsCategory category = new HeadsCategory(str);

      category.setItemStack(new ItemBuilder(Utils.getSkull(config.getString(str + ".texture")))
          .name(plugin.getChatManager().colorRawMessage(config.getString(str + ".displayname")))
          .lore(config.getStringList(str + ".lore").stream()
              .map(lore -> lore = plugin.getChatManager().colorRawMessage(lore)).collect(Collectors.toList()))
          .build());
      category.setPermission(config.getString(str + ".permission"));

      Set<ItemStack> playerHeads = new HashSet<>();
      FileConfiguration categoryConfig = ConfigUtils.getConfig(JavaPlugin.getPlugin(Main.class), "heads/menus/" +
          config.getString(str + ".config"));
      for (String path : categoryConfig.getKeys(false)) {
        if (!categoryConfig.getBoolean(path + ".enabled", true)) {
          continue;
        }
        ItemStack stack = Utils.getSkull(categoryConfig.getString(path + ".texture"));
        Utils.setItemNameAndLore(stack, plugin.getChatManager().colorRawMessage(categoryConfig.getString(path + ".displayname")),
            categoryConfig.getStringList(path + ".lore").stream()
                .map(lore -> lore = plugin.getChatManager().colorRawMessage(lore)).collect(Collectors.toList()));
        playerHeads.add(stack);
      }
      Inventory inv = Bukkit.createInventory(null, Utils.serializeInt(playerHeads.size() + 1),
          plugin.getChatManager().colorRawMessage(config.getString(str + ".menuname")));
      for (ItemStack item : playerHeads) {
        inv.addItem(item);
      }
      inv.addItem(Utils.getGoBackItem());
      category.setInventory(inv);
      categories.put(category, inv);
    }
  }

  public boolean isHeadsMenu(Inventory inventory) {
    for (Inventory inv : categories.values()) {
      if (inventory.equals(inv)) {
        return true;
      }
    }
    return false;
  }

  public Map<HeadsCategory, Inventory> getCategories() {
    return categories;
  }

}
