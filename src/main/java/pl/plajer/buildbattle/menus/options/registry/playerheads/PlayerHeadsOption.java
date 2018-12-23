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

package pl.plajer.buildbattle.menus.options.registry.playerheads;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.menus.options.MenuOption;
import pl.plajer.buildbattle.menus.options.OptionsRegistry;
import pl.plajerlair.core.utils.ItemBuilder;
import pl.plajerlair.core.utils.MinigameUtils;
import pl.plajerlair.core.utils.XMaterial;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class PlayerHeadsOption {

  //todo test
  public PlayerHeadsOption(OptionsRegistry registry) {
    ItemStack headOption;
    if (registry.getPlugin().is1_11_R1() || registry.getPlugin().is1_12_R1()) {
      headOption = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
    } else {
      headOption = XMaterial.PLAYER_HEAD.parseItem();
    }

    registry.registerOption(new MenuOption(11, "PLAYER_HEADS", new ItemBuilder(headOption)
        .name(ChatManager.colorMessage("Menus.Option-Menu.Items.Players-Heads.Item-Name"))
        .lore(ChatManager.colorMessage("Menus.Option-Menu.Items.Players-Heads.Item-Lore"))
        .build(), ChatManager.colorMessage("Menus.Option-Menu.Items.Player-Heads.Players-Heads.Inventory-Name")) {

      @Override
      public void onClick(InventoryClickEvent e) {
        e.getWhoClicked().closeInventory();

        Inventory inventory = Bukkit.getServer().createInventory(null,
            MinigameUtils.serializeInt(registry.getPlayerHeadsRegistry().getCategories().size()),
            ChatManager.colorMessage("Menus.Option-Menu.Items.Players-Heads.Inventory-Name"));
        for (HeadsCategory categoryItem : registry.getPlayerHeadsRegistry().getCategories().keySet()) {
          inventory.addItem(categoryItem.getItemStack());
        }
        e.getWhoClicked().openInventory(inventory);
      }

      @Override
      public void onTargetClick(InventoryClickEvent e) {
        for (HeadsCategory category : registry.getPlayerHeadsRegistry().getCategories().keySet()) {
          if (!category.getItemStack().isSimilar(e.getCurrentItem())) {
            continue;
          }
          if (e.getWhoClicked().hasPermission(category.getPermission())) {
            e.getWhoClicked().openInventory(category.getInventory());
            return;
          }
          e.getWhoClicked().sendMessage(ChatManager.colorMessage("Menus.Option-Menu.Items.Players-Heads.No-Permission"));
          return;
        }
      }
    });
  }

}
