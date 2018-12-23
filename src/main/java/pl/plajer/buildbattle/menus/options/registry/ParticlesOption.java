/*
 * BuildBattle 4 - Ultimate building competition minigame
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.menus.options.MenuOption;
import pl.plajer.buildbattle.menus.options.OptionsRegistry;
import pl.plajer.buildbattle.menus.particles.ParticleMenu;
import pl.plajer.buildbattle.menus.particles.ParticleRemoveMenu;
import pl.plajerlair.core.utils.ItemBuilder;
import pl.plajerlair.core.utils.XMaterial;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class ParticlesOption {

  public ParticlesOption(OptionsRegistry registry) {
    registry.registerOption(new MenuOption(13, "PARTICLES", new ItemBuilder(XMaterial.DANDELION.parseItem())
        .name(ChatManager.colorMessage("Menus.Option-Menu.Items.Particle.Item-Name"))
        .lore(ChatManager.colorMessage("Menus.Option-Menu.Items.Particle.Item-Lore"))
        .build(), ChatManager.colorMessage("Menus.Option-Menu.Items.Particle.In-Inventory-Item-Name")) {

      @Override
      public void onClick(InventoryClickEvent e) {
        Arena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
        if (arena == null) {
          return;
        }

        if (e.getCurrentItem().getItemMeta().getDisplayName()
            .contains(ChatManager.colorMessage("Menus.Option-Menu.Items.Particle.In-Inventory-Item-Name"))) {
          e.getWhoClicked().closeInventory();
          ParticleRemoveMenu.openMenu((Player) e.getWhoClicked(), arena.getPlotManager().getPlot((Player) e.getWhoClicked()));
          return;
        }
        e.getWhoClicked().closeInventory();
        ParticleMenu.openMenu((Player) e.getWhoClicked());
      }

      @Override
      public void onTargetClick(InventoryClickEvent e) {
        Arena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
        if (arena == null) {
          return;
        }
        ParticleRemoveMenu.onClick((Player) e.getWhoClicked(), e.getInventory(), e.getCurrentItem(),
            arena.getPlotManager().getPlot((Player) e.getWhoClicked()));
      }
    });
  }

}
