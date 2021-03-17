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

package plugily.projects.buildbattle.menus.options.registry.playerheads;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import pl.plajerlair.commonsbox.minecraft.item.ItemUtils;
import pl.plajerlair.commonsbox.minecraft.misc.stuff.ComplementAccessor;
import plugily.projects.buildbattle.ConfigPreferences;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.menus.options.MenuOption;
import plugily.projects.buildbattle.menus.options.OptionsRegistry;
import plugily.projects.buildbattle.utils.Utils;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class PlayerHeadsOption {
  private static final Main plugin = JavaPlugin.getPlugin(Main.class);

  public PlayerHeadsOption(OptionsRegistry registry) {
    registry.registerOption(new MenuOption(10, "PLAYER_HEADS", new ItemBuilder(ItemUtils.PLAYER_HEAD_ITEM.clone())
        .name(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Players-Heads.Item-Name"))
        .lore(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Players-Heads.Item-Lore"))
        .build(), registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Players-Heads.Inventory-Name")) {

      @Override
      public void onClick(InventoryClickEvent e) {
        e.getWhoClicked().closeInventory();
        if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.HEADS_COMMAND)) {
          if(e.getWhoClicked() instanceof Player) {
            ((Player) e.getWhoClicked()).performCommand(plugin.getConfig().getString("Command-Instead-Of-Head-Menu.Command", "heads"));
          }
          return;
        }
        Inventory inventory = ComplementAccessor.getComplement().createInventory(null,
            Utils.serializeInt(registry.getPlayerHeadsRegistry().getCategories().size() + 1),
            registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Players-Heads.Inventory-Name"));
        for(HeadsCategory categoryItem : registry.getPlayerHeadsRegistry().getCategories().keySet()) {
          inventory.addItem(categoryItem.getItemStack());
        }
        inventory.addItem(Utils.getGoBackItem());
        e.getWhoClicked().openInventory(inventory);
      }

      @Override
      public void onTargetClick(InventoryClickEvent e) {
        e.getWhoClicked().closeInventory();
        for(HeadsCategory category : registry.getPlayerHeadsRegistry().getCategories().keySet()) {
          if(!ComplementAccessor.getComplement().getDisplayName(category.getItemStack().getItemMeta())
              .equals(ComplementAccessor.getComplement().getDisplayName(e.getCurrentItem().getItemMeta()))) {
            continue;
          }
          if(e.getWhoClicked().hasPermission(category.getPermission())) {
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
