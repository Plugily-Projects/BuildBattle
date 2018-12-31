/*
 * BuildBattle - Ultimate building competition minigame
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

package pl.plajer.buildbattle.menus.options.registry.particles;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.plots.Plot;
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
public class ParticlesOption {


  public ParticlesOption(OptionsRegistry registry) {
    registry.registerOption(new MenuOption(13, "PARTICLES", new ItemBuilder(XMaterial.DANDELION.parseItem())
        .name(ChatManager.colorMessage("Menus.Option-Menu.Items.Particle.Item-Name"))
        .lore(ChatManager.colorMessage("Menus.Option-Menu.Items.Particle.Item-Lore"))
        .build(), ChatManager.colorMessage("Menus.Option-Menu.Items.Particle.Inventory-Name")) {

      @Override
      public void onClick(InventoryClickEvent e) {
        e.getWhoClicked().closeInventory();
        Arena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
        if (arena == null) {
          return;
        }

        if (e.getCurrentItem().getItemMeta().getDisplayName()
            .contains(ChatManager.colorMessage("Menus.Option-Menu.Items.Particle.In-Inventory-Item-Name"))) {
          ParticleRemoveMenu.openMenu((Player) e.getWhoClicked(), arena.getPlotManager().getPlot((Player) e.getWhoClicked()));
          return;
        }
        e.getWhoClicked().openInventory(registry.getParticleRegistry().getInventory());
      }

      @Override
      public void onTargetClick(InventoryClickEvent e) {
        Arena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
        if (arena == null) {
          return;
        }

        if (e.getCurrentItem().getItemMeta().getDisplayName()
            .contains(ChatManager.colorMessage("Menus.Option-Menu.Items.Particle.In-Inventory-Item-Name"))) {
          e.setCancelled(false);
          e.getWhoClicked().closeInventory();
          ParticleRemoveMenu.openMenu((Player) e.getWhoClicked(), arena.getPlotManager().getPlot((Player) e.getWhoClicked()));
          return;
        }
        for (ParticleItem particleItem : registry.getParticleRegistry().getRegisteredParticles()) {
          if (!e.getCurrentItem().isSimilar(particleItem.getItemStack())) {
            continue;
          }
          Plot plot = arena.getPlotManager().getPlot((Player) e.getWhoClicked());
          if (!e.getWhoClicked().hasPermission(particleItem.getPermission())) {
            e.getWhoClicked().sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("In-Game.No-Permission-For-Particle"));
            continue;
          }
          if (plot.getParticles().size() >= registry.getPlugin().getConfig().getInt("Max-Amount-Particles", 25)) {
            e.getWhoClicked().sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("In-Game.Max-Particles-Limit-Reached"));
            return;
          }
          plot.addParticle(e.getWhoClicked().getLocation(), particleItem.getEffect());
          registry.getPlugin().getUserManager().getUser(e.getWhoClicked().getUniqueId())
              .addStat(StatsStorage.StatisticType.PARTICLES_USED, 1);
          e.getWhoClicked().sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("In-Game.Particle-Added"));
        }
      }
    });
  }

}
