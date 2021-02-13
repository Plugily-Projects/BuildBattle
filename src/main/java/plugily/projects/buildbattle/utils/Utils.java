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

package plugily.projects.buildbattle.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import plugily.projects.buildbattle.Main;

import java.util.List;

/**
 * Created by Tom on 29/07/2014.
 */
public class Utils {

  private Utils() {
  }

  /**
   * Serialize int to use it in Inventories size
   * ex. you have 38 kits and it will serialize it to 45 (9*5)
   * because it is valid inventory size
   * next ex. you have 55 items and it will serialize it to 63 (9*7) not 54 because it's too less
   *
   * @param i integer to serialize
   * @return serialized number
   */
  public static int serializeInt(Integer i) {
    if(i == 0) return 9; //The function bellow doesn't work if i == 0, so return 9 in case that happens.
    return (i % 9) == 0 ? i : (i + 9 - 1) / 9 * 9;
  }

  public static ItemStack getGoBackItem() {
    final Main plugin = JavaPlugin.getPlugin(Main.class);

    return new ItemBuilder(XMaterial.STONE_BUTTON.parseItem())
        .name(plugin.getChatManager().colorMessage("Menus.Option-Menu.Go-Back-Button.Item-Name"))
        .lore(plugin.getChatManager().colorMessage("Menus.Option-Menu.Go-Back-Button.Item-Lore")).build();
  }

  public static ItemStack setItemNameAndLore(ItemStack item, String name, List<String> lore) {
    ItemMeta im = item.getItemMeta();
    im.setDisplayName(name);
    im.setLore(lore);
    item.setItemMeta(im);
    return item;
  }

}
