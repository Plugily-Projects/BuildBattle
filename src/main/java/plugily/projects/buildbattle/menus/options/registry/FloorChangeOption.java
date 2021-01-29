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

package plugily.projects.buildbattle.menus.options.registry;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.menus.options.MenuOption;
import plugily.projects.buildbattle.menus.options.OptionsRegistry;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class FloorChangeOption {

  public FloorChangeOption(OptionsRegistry registry) {
    //todo material change
    registry.registerOption(new MenuOption(14, "FLOOR", new ItemBuilder(XMaterial.OAK_LOG.parseItem())
        .name(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Floor.Item-Name"))
        .lore(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Floor.Item-Lore"))
        .build()) {
      @Override
      public void onClick(InventoryClickEvent e) {
        BaseArena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
        ItemStack itemStack = e.getCursor();
        if(arena == null || itemStack == null) {
          return;
        }
        Material material = itemStack.getType();
        if(material != XMaterial.WATER_BUCKET.parseMaterial() && material != XMaterial.LAVA_BUCKET.parseMaterial()
            && !(material.isBlock() && material.isSolid() && material.isOccluding())) {
          return;
        }
        if(registry.getPlugin().getConfigPreferences().getFloorBlacklist().contains(material)) {
          return;
        }
        byte materialData = XMaterial.matchXMaterial(itemStack).getData();
        arena.getPlotManager().getPlot((Player) e.getWhoClicked()).changeFloor(material, materialData);
        e.getWhoClicked().sendMessage(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Floor.Floor-Changed"));
        itemStack.setAmount(0);
        itemStack.setType(Material.AIR);
        e.getCurrentItem().setType(Material.AIR);
        e.getWhoClicked().closeInventory();
        e.getWhoClicked().getNearbyEntities(5, 5, 5).stream().filter(entity -> entity.getType() == EntityType.DROPPED_ITEM)
            .forEach(Entity::remove);
      }
    });
  }

}
