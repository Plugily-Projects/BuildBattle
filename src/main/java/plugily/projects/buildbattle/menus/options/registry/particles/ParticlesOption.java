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

package plugily.projects.buildbattle.menus.options.registry.particles;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import plugily.projects.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.commonsbox.minecraft.item.ItemBuilder;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.menus.options.MenuOption;
import plugily.projects.buildbattle.menus.options.OptionsRegistry;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class ParticlesOption {


  public ParticlesOption(OptionsRegistry registry) {
    registry.registerOption(new MenuOption(12, "PARTICLES", new ItemBuilder(XMaterial.DANDELION.parseItem())
        .name(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Particle.Item-Name"))
        .lore(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Particle.Item-Lore"))
        .build(), registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Particle.Inventory-Name")) {

      @Override
      public void onClick(InventoryClickEvent event) {
        event.getWhoClicked().closeInventory();
        BaseArena arena = ArenaRegistry.getArena((Player) event.getWhoClicked());
        if(arena == null) {
          return;
        }
        registry.getPlugin().getOptionsRegistry().getParticleRegistry().getParticles().show(event.getWhoClicked());
      }
    });
  }

}
