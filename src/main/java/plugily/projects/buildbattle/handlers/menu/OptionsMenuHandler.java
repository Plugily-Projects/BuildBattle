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

package plugily.projects.buildbattle.handlers.menu;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemUtils;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class OptionsMenuHandler implements Listener {

  private final Main plugin;

  public OptionsMenuHandler(Main plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onOptionsMenuClick(InventoryClickEvent event) {
    HumanEntity humanEntity = event.getWhoClicked();

    if(!(humanEntity instanceof Player)) {
      return;
    }

    BaseArena arena = plugin.getArenaRegistry().getArena((Player) humanEntity);
    if(arena == null || arena.getArenaState() != ArenaState.IN_GAME) {
      return;
    }

    ItemStack currentItem = event.getCurrentItem();

    if(!ItemUtils.isItemStackNamed(currentItem)
        || !ComplementAccessor.getComplement().getTitle(event.getView()).equals(new MessageBuilder("MENU_OPTION_INVENTORY").asKey().build())) {
      return;
    }

    for(MenuOption option : plugin.getOptionsRegistry().getRegisteredOptions()) {
      if(!option.getItemStack().isSimilar(currentItem)) {
        continue;
      }
      event.setCancelled(true);
      option.onClick(event);
      return;
    }
  }

  @EventHandler
  public void onRegisteredMenuOptionsClick(InventoryClickEvent event) {
    HumanEntity human = event.getWhoClicked();

    if(!(human instanceof Player)) {
      return;
    }

    ItemStack currentItem = event.getCurrentItem();

    if (!ItemUtils.isItemStackNamed(currentItem))
      return;

    if(ComplementAccessor.getComplement().getDisplayName(plugin.getOptionsRegistry().getGoBackItem().getItemMeta())
        .equalsIgnoreCase(ComplementAccessor.getComplement().getDisplayName(currentItem.getItemMeta()))) {
      event.setCancelled(true);
      human.closeInventory();
      human.openInventory(plugin.getOptionsRegistry().formatInventory());
      return;
    }

    String viewTitle = ComplementAccessor.getComplement().getTitle(event.getView());

    for(MenuOption option : plugin.getOptionsRegistry().getRegisteredOptions()) {
      if(viewTitle.equals(option.getInventoryName())) {
        event.setCancelled(true);
        option.onTargetClick(event);
        return;
      }
    }
  }

}
