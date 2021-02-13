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

import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import plugily.projects.buildbattle.api.StatsStorage;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
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
      public void onClick(InventoryClickEvent e) {
        e.getWhoClicked().closeInventory();
        BaseArena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
        if(arena == null) {
          return;
        }
        e.getWhoClicked().openInventory(registry.getParticleRegistry().getPage1());
      }

      @Override
      public void onTargetClick(InventoryClickEvent e) {
        BaseArena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
        if(arena == null || e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()) {
          return;
        }
        if(e.getCurrentItem().getItemMeta().getDisplayName()
            .contains("§7-->")) {
          e.setCancelled(false);
          e.getWhoClicked().closeInventory();
          e.getWhoClicked().openInventory(registry.getParticleRegistry().getPage2());
          return;
        }
        if(e.getCurrentItem().getItemMeta().getDisplayName()
            .contains(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Particle.In-Inventory-Item-Name"))) {
          e.setCancelled(false);
          e.getWhoClicked().closeInventory();
          ParticleRemoveMenu.openMenu((Player) e.getWhoClicked(), arena.getPlotManager().getPlot((Player) e.getWhoClicked()));
          return;
        }
        for(ParticleItem particleItem : registry.getParticleRegistry().getRegisteredParticles()) {
          // Only check for the display name for items in gui, because some of item meta is changed
          if(!e.getCurrentItem().getItemMeta().getDisplayName().contains(particleItem.getItemStack().getItemMeta().getDisplayName())) {
            continue;
          }
          Plot plot = arena.getPlotManager().getPlot((Player) e.getWhoClicked());
          if(!e.getWhoClicked().hasPermission(particleItem.getPermission())) {
            e.getWhoClicked().sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("In-Game.No-Permission-For-Particle"));
            continue;
          }
          if(plot.getParticles().size() >= registry.getPlugin().getConfig().getInt("Max-Amount-Particles", 25)) {
            e.getWhoClicked().sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("In-Game.Max-Particles-Limit-Reached"));
            return;
          }
          plot.getParticles().put(e.getWhoClicked().getLocation(), particleItem.getEffect());
          registry.getPlugin().getUserManager().getUser((Player) e.getWhoClicked())
              .addStat(StatsStorage.StatisticType.PARTICLES_USED, 1);
          e.getWhoClicked().sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("In-Game.Particle-Added"));
        }
      }
    });
  }

}
