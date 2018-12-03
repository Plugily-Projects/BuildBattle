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
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.arena.plots.ArenaPlot;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajerlair.core.utils.ItemBuilder;
import pl.plajerlair.core.utils.XMaterial;

/**
 * Created by Tom on 18/08/2015.
 */
public class OptionsMenu {

  private static Main plugin = JavaPlugin.getPlugin(Main.class);

  public static ItemStack getMenuItem() {
    return new ItemBuilder(new ItemStack(Material.NETHER_STAR)).name(ChatManager.colorMessage("Menus.Option-Menu.Option-Item")).lore(ChatManager.colorMessage("Menus.Option-Menu.Option-Item-Lore")).build();
  }

  private static Inventory createMenu(ArenaPlot plot) {
    Inventory inv = Bukkit.createInventory(null, 5 * 9, ChatManager.colorMessage("Menus.Option-Menu.Inventory-Name"));

    ItemStack headOption;
    if (plugin.is1_11_R1() || plugin.is1_12_R1()) {
      headOption = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
    } else {
      //todo check
      headOption = XMaterial.PLAYER_HEAD.parseItem();
    }
    inv.setItem(11, new ItemBuilder(headOption)
        .name(ChatManager.colorMessage("Menus.Option-Menu.Items.Players-Heads.Item-Name"))
        .lore(ChatManager.colorMessage("Menus.Option-Menu.Items.Players-Heads.Item-Lore"))
        .build());
    inv.setItem(13, new ItemBuilder(XMaterial.DANDELION.parseItem())
        .name(ChatManager.colorMessage("Menus.Option-Menu.Items.Particle.Item-Name"))
        .lore(ChatManager.colorMessage("Menus.Option-Menu.Items.Particle.Item-Lore"))
        .build());
    inv.setItem(15, new ItemBuilder(new ItemStack(plot.getFloorMaterial(), 1))
        .name(ChatManager.colorMessage("Menus.Option-Menu.Items.Floor.Item-Name"))
        .lore(ChatManager.colorMessage("Menus.Option-Menu.Items.Floor.Item-Lore"))
        .build());
    inv.setItem(28, new ItemBuilder(new ItemStack(Material.BUCKET))
        .name(ChatManager.colorMessage("Menus.Option-Menu.Items.Weather.Item-Name"))
        .lore(ChatManager.colorMessage("Menus.Option-Menu.Items.Weather.Item-Lore"))
        .build());
    inv.setItem(30, new ItemBuilder(XMaterial.CLOCK.parseItem())
        .name(ChatManager.colorMessage("Menus.Option-Menu.Items.Time.Item-Name"))
        .lore(ChatManager.colorMessage("Menus.Option-Menu.Items.Time.Item-Lore"))
        .build());
    inv.setItem(32, new ItemBuilder(XMaterial.MYCELIUM.parseItem())
        .name(ChatManager.colorMessage("Menus.Option-Menu.Items.Biome.Item-Name"))
        .lore(ChatManager.colorMessage("Menus.Option-Menu.Items.Biome.Item-Lore"))
        .build());
    inv.setItem(34, new ItemBuilder(new ItemStack(Material.BARRIER))
        .name(ChatManager.colorMessage("Menus.Option-Menu.Items.Reset.Item-Name"))
        .lore(ChatManager.colorMessage("Menus.Option-Menu.Items.Reset.Item-Lore"))
        .build());
    return inv;
  }

  public static void openMenu(Player player, ArenaPlot buildPlot) {
    player.openInventory(createMenu(buildPlot));
  }

}
