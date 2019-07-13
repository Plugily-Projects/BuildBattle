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

package pl.plajer.buildbattle.menus.options.registry;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.impl.BaseArena;
import pl.plajer.buildbattle.menus.options.MenuOption;
import pl.plajer.buildbattle.menus.options.OptionsRegistry;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class FloorChangeOption {

  public FloorChangeOption(OptionsRegistry registry) {
    //todo material change
    registry.registerOption(new MenuOption(15, "FLOOR", new ItemBuilder(XMaterial.OAK_LOG.parseItem())
        .name(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Floor.Item-Name"))
        .lore(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Floor.Item-Lore"))
        .build()) {
      @Override
      public void onClick(InventoryClickEvent e) {
        BaseArena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
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
        if (e.getCursor().getType() == Material.IRON_TRAPDOOR || e.getCursor().getType() == Material.ACACIA_DOOR || e.getCursor().getType() == Material.BIRCH_DOOR
            || e.getCursor().getType() == Material.JUNGLE_DOOR || e.getCursor().getType() == Material.SPRUCE_DOOR || e.getCursor().getType() == Material.IRON_DOOR
            || e.getCursor().getType() == Material.CHEST || e.getCursor().getType() == Material.TRAPPED_CHEST || e.getCursor().getType() == Material.LADDER
            || e.getCursor().getType() == Material.JUNGLE_FENCE_GATE || e.getCursor().getType() == XMaterial.SIGN.parseMaterial() || e.getCursor().getType() == XMaterial.WALL_SIGN.parseMaterial()
            || e.getCursor().getType() == Material.CACTUS || e.getCursor().getType() == Material.ENDER_CHEST || e.getCursor().getType() == Material.TNT || e.getCursor().getType() == Material.AIR) {
          e.setCancelled(true);
          return;
        }
        if (!e.getCursor().getType().isBlock() || !e.getCursor().getType().isOccluding()) {
          e.setCancelled(true);
          return;
        }

        arena.getPlotManager().getPlot((Player) e.getWhoClicked()).changeFloor(e.getCursor().getType(), e.getCursor().getData().getData());
        e.getWhoClicked().sendMessage(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Floor.Floor-Changed"));
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
