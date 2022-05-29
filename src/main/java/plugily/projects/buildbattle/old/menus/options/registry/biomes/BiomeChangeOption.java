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

package plugily.projects.buildbattle.old.menus.options.registry.biomes;

import org.bukkit.Chunk;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import plugily.projects.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.commonsbox.minecraft.item.ItemBuilder;
import plugily.projects.buildbattle.old.arena.ArenaRegistry;
import plugily.projects.buildbattle.old.arena.managers.plots.Plot;
import plugily.projects.buildbattle.old.menus.options.MenuOption;
import plugily.projects.buildbattle.old.menus.options.OptionsRegistry;
import plugily.projects.buildbattle.old.utils.Utils;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class BiomeChangeOption {

  public BiomeChangeOption(OptionsRegistry registry) {
    registry.registerOption(new MenuOption(32, "BIOME", new ItemBuilder(XMaterial.MYCELIUM.parseItem())
        .name(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Biome.Item-Name"))
        .lore(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Biome.Item-Lore"))
        .build(), registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Biome.Inventory-Name")) {

      @Override
      public void onClick(InventoryClickEvent e) {
        e.getWhoClicked().closeInventory();
        e.getWhoClicked().openInventory(registry.getBiomesRegistry().getInventory());
      }

      @Override
      public void onTargetClick(InventoryClickEvent e) {
        Player who = (Player) e.getWhoClicked();
        BaseArena arena = ArenaRegistry.getArena(who);
        if(arena == null) {
          return;
        }
        BiomeItem item = registry.getBiomesRegistry().getByItem(e.getCurrentItem());
        if(item == BiomeItem.INVALID_BIOME) {
          return;
        }
        if(!who.hasPermission(item.getPermission())) {
          who.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("In-Game.No-Permission-For-Biome"));
          return;
        }

        Plot plot = arena.getPlotManager().getPlot(who);
        if (plot == null)
          return;

        if (plot.getCuboid() != null) {
          Biome biome = item.getBiome().getBiome();

          if(biome != null) {
            for(Block block : plot.getCuboid().blockList()) {
              block.setBiome(biome);
            }
          }

          for(Chunk chunk : plot.getCuboid().chunkList()) {
            for(Player p : registry.getPlugin().getServer().getOnlinePlayers()) {
              if(p.getWorld().equals(chunk.getWorld())) {
                Utils.sendMapChunk(p, chunk);
              }
            }
          }
        }

        for(Player p : plot.getMembers()) {
          p.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Biome.Biome-Set"));
        }
      }
    });
  }

}
