/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.buildbattle.menus.options.registry;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.menus.options.MenuOption;
import pl.plajer.buildbattle.menus.options.OptionsRegistry;
import pl.plajerlair.core.utils.ItemBuilder;
import pl.plajerlair.core.utils.XMaterial;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class FloorChangeOption {

  public FloorChangeOption(OptionsRegistry registry) {
    //todo material change
    registry.registerOption(new MenuOption(15, "FLOOR", new ItemBuilder(XMaterial.OAK_LOG.parseItem())
        .name(ChatManager.colorMessage("Menus.Option-Menu.Items.Floor.Item-Name"))
        .lore(ChatManager.colorMessage("Menus.Option-Menu.Items.Floor.Item-Lore"))
        .build()) {
      @Override
      public void onClick(InventoryClickEvent e) {
        Arena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
        if (arena == null) {
          return;
        }
        if (e.getCursor() == null) {
          return;
        }
        if (!((e.getCursor().getType().isBlock() && e.getCursor().getType().isSolid()) || e.getCursor().getType() == Material.WATER_BUCKET || e.getCursor().getType() == Material.LAVA_BUCKET)) {
          return;
        }
        //todo blacklist
        if (e.getCursor().getType() == null || e.getCursor().getType() == Material.IRON_TRAPDOOR || e.getCursor().getType() == Material.ACACIA_DOOR || e.getCursor().getType() == Material.BIRCH_DOOR || e.getCursor().getType() == Material.JUNGLE_DOOR || e.getCursor().getType() == Material.SPRUCE_DOOR || e.getCursor().getType() == Material.IRON_DOOR || e.getCursor().getType() == Material.CHEST || e.getCursor().getType() == Material.TRAPPED_CHEST || e.getCursor().getType() == Material.LADDER || e.getCursor().getType() == Material.JUNGLE_FENCE_GATE || e.getCursor().getType() == Material.SIGN || e.getCursor().getType() == Material.WALL_SIGN || e.getCursor().getType() == Material.CACTUS || e.getCursor().getType() == Material.ENDER_CHEST
            || e.getCursor().getType() == Material.TNT || e.getCursor().getType() == Material.AIR) {
          e.setCancelled(true);
          return;
        }

        arena.getPlotManager().getPlot((Player) e.getWhoClicked()).changeFloor(e.getCursor().getType(), e.getCursor().getData().getData());
        e.getWhoClicked().sendMessage(ChatManager.colorMessage("Menus.Option-Menu.Items.Floor.Floor-Changed"));
        e.getCursor().setAmount(0);
        e.getCursor().setType(Material.AIR);
        e.getCurrentItem().setType(Material.AIR);
        e.getWhoClicked().closeInventory();
        for (Entity entity : e.getWhoClicked().getNearbyEntities(5, 5, 5)) {
          if (entity.getType() == EntityType.DROPPED_ITEM) {
            entity.remove();
          }
        }
      }
    });
  }

}
