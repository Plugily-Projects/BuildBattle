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

package plugily.projects.buildbattle.handlers.menu.registry.playerheads;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import plugily.projects.buildbattle.handlers.menu.MenuOption;
import plugily.projects.buildbattle.handlers.menu.OptionsRegistry;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemUtils;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class PlayerHeadsOption {

  public PlayerHeadsOption(OptionsRegistry registry) {
    registry.registerOption(new MenuOption(10, "PLAYER_HEADS", new ItemBuilder(ItemUtils.PLAYER_HEAD_ITEM.clone())
        .name(new MessageBuilder("MENU_OPTION_CONTENT_HEADS_ITEM_NAME").asKey().build())
        .lore(new MessageBuilder("MENU_OPTION_CONTENT_HEADS_ITEM_LORE").asKey().build())
        .build(), new MessageBuilder("MENU_OPTION_CONTENT_HEADS_INVENTORY").asKey().build()) {

      @Override
      public void onClick(InventoryClickEvent e) {
        org.bukkit.entity.HumanEntity who = e.getWhoClicked();

        who.closeInventory();
        if(registry.getPlugin().getConfigPreferences().getOption("HEAD_MENU_CUSTOM")) {
          if(who instanceof Player) {
            ((Player) who).performCommand(registry.getPlugin().getConfig().getString("Head-Menu.Command", "heads"));
          }
          return;
        }
        Inventory inventory = ComplementAccessor.getComplement().createInventory(null,
            registry.getPlugin().getBukkitHelper().serializeInt(registry.getPlayerHeadsRegistry().getCategories().size() + 1),
            new MessageBuilder("MENU_OPTION_CONTENT_HEADS_INVENTORY").asKey().build());
        for(HeadsCategory categoryItem : registry.getPlayerHeadsRegistry().getCategories().keySet()) {
          inventory.addItem(categoryItem.getItemStack());
        }
        inventory.addItem(registry.getGoBackItem());
        who.openInventory(inventory);
      }

      @Override
      public void onTargetClick(InventoryClickEvent e) {
        org.bukkit.entity.HumanEntity who = e.getWhoClicked();

        who.closeInventory();
        for(HeadsCategory category : registry.getPlayerHeadsRegistry().getCategories().keySet()) {
          if(!ComplementAccessor.getComplement().getDisplayName(category.getItemStack().getItemMeta())
              .equals(ComplementAccessor.getComplement().getDisplayName(e.getCurrentItem().getItemMeta()))) {
            continue;
          }
          if(who.hasPermission(category.getPermission())) {
            who.openInventory(category.getInventory());
            return;
          }
          new MessageBuilder("IN_GAME_MESSAGES_PLOT_PERMISSION_HEAD").asKey().player((Player) who).sendPlayer();
          return;
        }
      }
    });
  }

}
