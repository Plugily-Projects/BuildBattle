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

package pl.plajer.buildbattle.menus.options;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.ArenaState;
import pl.plajer.buildbattle.arena.impl.BaseArena;
import pl.plajer.buildbattle.utils.Utils;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class OptionsMenuHandler implements Listener {

  private Main plugin;

  public OptionsMenuHandler(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onOptionsMenuClick(InventoryClickEvent e) {
      if (!(e.getWhoClicked() instanceof Player) || e.getInventory() == null || e.getCurrentItem() == null) {
        return;
      }
    if (!Utils.isNamed(e.getCurrentItem()) || !e.getView().getTitle().equals(plugin.getChatManager().colorMessage("Menus.Option-Menu.Inventory-Name"))) {
        return;
      }
      BaseArena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
      if (arena == null || arena.getArenaState() != ArenaState.IN_GAME) {
        return;
      }
      for (MenuOption option : plugin.getOptionsRegistry().getRegisteredOptions()) {
        if (!option.getItemStack().isSimilar(e.getCurrentItem())) {
          continue;
        }
        e.setCancelled(true);
        option.onClick(e);
        return;
      }
  }

  @EventHandler
  public void onRegisteredMenuOptionsClick(InventoryClickEvent e) {
    if (!(e.getWhoClicked() instanceof Player) || e.getCurrentItem() == null
        || !e.getCurrentItem().hasItemMeta() || !e.getCurrentItem().getItemMeta().hasDisplayName()) {
        return;
      }
      for (MenuOption option : plugin.getOptionsRegistry().getRegisteredOptions()) {
        if (!option.isInventoryEnabled()) {
          continue;
        }
        if (option.getInventoryName().equals(e.getView().getTitle())) {
          e.setCancelled(true);
          option.onTargetClick(e);
          return;
        }
      }
  }

}
