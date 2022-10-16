/*
 *
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2022 Plugily Projects - maintained by Tigerpanzer_02 and contributors
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
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.EventExecutor;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.BaseArena.ArenaInGameState;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public final class OptionsMenuHandler {

  public OptionsMenuHandler(Main plugin) {
    plugin.getServer().getPluginManager().registerEvent(InventoryClickEvent.class, new Listener() {
    }, EventPriority.NORMAL, new EventExecutor() {

      @Override
      public void execute(Listener listener, Event e) {
        InventoryClickEvent event = (InventoryClickEvent) e;
        HumanEntity humanEntity = event.getWhoClicked();

        if(!(humanEntity instanceof Player)) {
          return;
        }

        BaseArena arena = plugin.getArenaRegistry().getArena((Player) humanEntity);
        if(arena == null || arena.getArenaInGameState() != ArenaInGameState.BUILD_TIME) {
          return;
        }

        ItemStack currentItem = event.getCurrentItem();

        if(plugin.getOptionsRegistry().getGoBackItem().isSimilar(currentItem)) {
          event.setCancelled(true);
          humanEntity.closeInventory();
          humanEntity.openInventory(plugin.getOptionsRegistry().formatInventory());
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

        if(event.getInventory() != plugin.getOptionsRegistry().formatInventory()) {
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
    }, plugin);
  }
}
