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

package plugily.projects.buildbattle.handlers.menu.registry;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.handlers.menu.MenuOption;
import plugily.projects.buildbattle.handlers.menu.OptionsRegistry;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class FloorChangeOption {

  public FloorChangeOption(OptionsRegistry registry) {
    //todo material change
    registry.registerOption(new MenuOption(14, "FLOOR", new ItemBuilder(XMaterial.OAK_LOG.parseItem())
        .name(new MessageBuilder("MENU_OPTION_CONTENT_FLOOR_ITEM_NAME").asKey().build())
        .lore(new MessageBuilder("MENU_OPTION_CONTENT_FLOOR_ITEM_LORE").asKey().build())
        .build()) {
      @Override
      public void onClick(InventoryClickEvent e) {
        ItemStack itemStack = e.getCursor();
        if (itemStack == null)
          return;

        Player who = (Player) e.getWhoClicked();
        BaseArena arena = registry.getPlugin().getArenaRegistry().getArena(who);
        if(arena == null) {
          return;
        }

        Material material = itemStack.getType();
        if(material != XMaterial.WATER_BUCKET.parseMaterial() && material != XMaterial.LAVA_BUCKET.parseMaterial()
            && !(material.isBlock() && material.isSolid() && material.isOccluding())) {
          new MessageBuilder("IN_GAME_MESSAGES_PLOT_PERMISSION_FLOOR_ITEM").asKey().player(who).sendPlayer();
          return;
        }

        if(registry.getPlugin().getBlacklistManager().getFloorList().contains(material)) {
          new MessageBuilder("IN_GAME_MESSAGES_PLOT_PERMISSION_FLOOR_ITEM").asKey().player(who).sendPlayer();
          return;
        }

        Plot plot = arena.getPlotManager().getPlot(who);
        if (plot == null)
          return;

        plot.changeFloor(material, XMaterial.matchXMaterial(itemStack).getData());
        new MessageBuilder("MENU_OPTION_CONTENT_FLOOR_CHANGED").asKey().player(who).sendPlayer();

        itemStack.setAmount(0);
        itemStack.setType(Material.AIR);
        e.getCurrentItem().setType(Material.AIR);
        who.closeInventory();

        who.getNearbyEntities(5, 5, 5).stream().filter(entity -> entity.getType() == EntityType.DROPPED_ITEM)
            .forEach(Entity::remove);
      }
    });
  }

}
