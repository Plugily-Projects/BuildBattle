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

package pl.plajer.buildbattle4.menus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import pl.plajer.buildbattle4.arena.plots.ArenaPlot;
import pl.plajer.buildbattle4.handlers.ChatManager;
import pl.plajerlair.core.utils.ItemBuilder;

/**
 * Created by Tom on 18/08/2015.
 */
public class OptionsMenu {

  public static ItemStack getMenuItem() {
    return new ItemBuilder(new ItemStack(Material.NETHER_STAR)).name(ChatManager.colorMessage("Menus.Option-Menu.Option-Item")).lore(ChatManager.colorMessage("Menus.Option-Menu.Option-Item-Lore")).build();
  }

  private static Inventory createMenu(ArenaPlot plot) {
    Inventory inv = Bukkit.createInventory(null, 5 * 9, ChatManager.colorMessage("Menus.Option-Menu.Inventory-Name"));

    ItemStack headOption = new ItemStack(Material.SKULL_ITEM, 1);
    headOption.setTypeId((byte) SkullType.PLAYER.ordinal());
    ItemMeta headMeta = headOption.getItemMeta();
    headMeta.setDisplayName(ChatManager.colorMessage("Menus.Option-Menu.Players-Heads-Option"));
    headMeta.setLore(Collections.singletonList(ChatManager.colorMessage("Menus.Option-Menu.Players-Heads-Option-Lore")));
    headOption.setItemMeta(headMeta);
    inv.setItem(11, headOption);

    ItemStack particleOption = new ItemStack(Material.YELLOW_FLOWER, 1);
    ItemMeta particleMeta = particleOption.getItemMeta();
    particleMeta.setDisplayName(ChatManager.colorMessage("Menus.Option-Menu.Particle-Option"));
    particleMeta.setLore(Collections.singletonList(ChatManager.colorMessage("Menus.Option-Menu.Particle-Option-Lore")));
    particleOption.setItemMeta(particleMeta);
    inv.setItem(13, particleOption);

    ItemStack floorOption = new ItemStack(plot.getFloorMaterial(), 1);
    ItemMeta floorMeta = floorOption.getItemMeta();
    floorMeta.setDisplayName(ChatManager.colorMessage("Menus.Option-Menu.Floor-Option"));
    List<String> lore = new ArrayList<>();
    lore.add(ChatManager.colorMessage("Menus.Option-Menu.Floor-Option-Lore"));
    floorMeta.setLore(lore);
    floorOption.setItemMeta(floorMeta);
    inv.setItem(15, floorOption);

    ItemStack weatherOption = new ItemStack(Material.BUCKET, 1);
    ItemMeta weatherMeta = weatherOption.getItemMeta();
    weatherMeta.setDisplayName(ChatManager.colorMessage("Menus.Option-Menu.Weather-Option"));
    weatherMeta.setLore(Collections.singletonList(ChatManager.colorMessage("Menus.Option-Menu.Weather-Option-Lore")));
    weatherOption.setItemMeta(weatherMeta);
    inv.setItem(30, weatherOption);

    ItemStack resetOption = new ItemStack(Material.BARRIER, 1);
    ItemMeta resetMeta = resetOption.getItemMeta();
    resetMeta.setDisplayName(ChatManager.colorMessage("Menus.Option-Menu.Reset-Option"));
    resetMeta.setLore(Collections.singletonList(ChatManager.colorMessage("Menus.Option-Menu.Reset-Option-Lore")));
    resetOption.setItemMeta(resetMeta);
    inv.setItem(32, resetOption);
    return inv;
  }

  public static void openMenu(Player player, ArenaPlot buildPlot) {
    player.openInventory(createMenu(buildPlot));
  }

}
