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

package plugily.projects.buildbattle.menus.options;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import pl.plajerlair.commonsbox.minecraft.item.ItemUtils;
import pl.plajerlair.commonsbox.minecraft.misc.stuff.ComplementAccessor;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.ArenaState;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.utils.Utils;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class OptionsMenuHandler implements Listener {

  private final Main plugin;

  public OptionsMenuHandler(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onOptionsMenuClick(InventoryClickEvent e) {
    if(!(e.getWhoClicked() instanceof Player) || e.getCurrentItem() == null) {
      return;
    }
    if(!ItemUtils.isItemStackNamed(e.getCurrentItem())
        || !ComplementAccessor.getComplement().getTitle(e.getView()).equals(plugin.getChatManager().colorMessage("Menus.Option-Menu.Inventory-Name"))) {
      return;
    }
    BaseArena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
    if(arena == null || arena.getArenaState() != ArenaState.IN_GAME) {
      return;
    }
    for(MenuOption option : plugin.getOptionsRegistry().getRegisteredOptions()) {
      if(!option.getItemStack().isSimilar(e.getCurrentItem())) {
        continue;
      }
      e.setCancelled(true);
      option.onClick(e);
      return;
    }
  }

  @EventHandler
  public void onRegisteredMenuOptionsClick(InventoryClickEvent e) {
    if(!(e.getWhoClicked() instanceof Player) || !ItemUtils.isItemStackNamed(e.getCurrentItem())) {
      return;
    }

    for(MenuOption option : plugin.getOptionsRegistry().getRegisteredOptions()) {
      if(ComplementAccessor.getComplement().getDisplayName(Utils.getGoBackItem().getItemMeta())
          .equalsIgnoreCase(ComplementAccessor.getComplement().getDisplayName(e.getCurrentItem().getItemMeta()))) {
        e.getWhoClicked().openInventory(plugin.getOptionsRegistry().formatInventory());
        return;
      }
      if(ComplementAccessor.getComplement().getTitle(e.getView()).equals(option.getInventoryName())) {
        e.setCancelled(true);
        option.onTargetClick(e);
        return;
      }
    }
  }

}
