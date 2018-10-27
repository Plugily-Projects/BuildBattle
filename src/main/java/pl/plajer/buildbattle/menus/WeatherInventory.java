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

package pl.plajer.buildbattle.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import pl.plajer.buildbattle.handlers.ChatManager;

/**
 * @author Plajer
 * <p>
 * Created at 20.07.2018
 */
public class WeatherInventory {

  public static void openWeatherInventory(Player p) {
    Inventory inv = Bukkit.createInventory(null, 9, ChatManager.colorMessage("Menus.Option-Menu.Weather-Inventory-Name"));

    ItemStack clear = new ItemStack(Material.BUCKET, 1);
    ItemMeta clearMeta = clear.getItemMeta();
    clearMeta.setDisplayName(ChatManager.colorMessage("Menus.Option-Menu.Weather-Clear"));
    clear.setItemMeta(clearMeta);
    inv.addItem(clear);

    ItemStack downfall = new ItemStack(Material.WATER_BUCKET, 1);
    ItemMeta fallMeta = downfall.getItemMeta();
    fallMeta.setDisplayName(ChatManager.colorMessage("Menus.Option-Menu.Weather-Downfall"));
    downfall.setItemMeta(fallMeta);
    inv.addItem(downfall);

    p.openInventory(inv);
  }

}
