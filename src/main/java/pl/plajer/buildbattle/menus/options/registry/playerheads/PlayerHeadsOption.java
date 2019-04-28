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

package pl.plajer.buildbattle.menus.options.registry.playerheads;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import pl.plajer.buildbattle.menus.options.MenuOption;
import pl.plajer.buildbattle.menus.options.OptionsRegistry;
import pl.plajer.buildbattle.utils.Utils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class PlayerHeadsOption {

  public PlayerHeadsOption(OptionsRegistry registry) {
    registry.registerOption(new MenuOption(11, "PLAYER_HEADS", new ItemBuilder(Utils.PLAYER_HEAD_ITEM.clone())
        .name(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Players-Heads.Item-Name"))
        .lore(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Players-Heads.Item-Lore"))
        .build(), registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Players-Heads.Inventory-Name")) {

      @Override
      public void onClick(InventoryClickEvent e) {
        e.getWhoClicked().closeInventory();

        Inventory inventory = Bukkit.getServer().createInventory(null,
            Utils.serializeInt(registry.getPlayerHeadsRegistry().getCategories().size()),
            registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Players-Heads.Inventory-Name"));
        for (HeadsCategory categoryItem : registry.getPlayerHeadsRegistry().getCategories().keySet()) {
          inventory.addItem(categoryItem.getItemStack());
        }
        e.getWhoClicked().openInventory(inventory);
      }

      @Override
      public void onTargetClick(InventoryClickEvent e) {
        e.getWhoClicked().closeInventory();
        for (HeadsCategory category : registry.getPlayerHeadsRegistry().getCategories().keySet()) {
          if (!category.getItemStack().isSimilar(e.getCurrentItem())) {
            continue;
          }
          if (e.getWhoClicked().hasPermission(category.getPermission())) {
            e.getWhoClicked().openInventory(category.getInventory());
            return;
          }
          e.getWhoClicked().sendMessage(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Players-Heads.No-Permission"));
          return;
        }
      }
    });
  }

}
