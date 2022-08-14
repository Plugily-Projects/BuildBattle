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

package plugily.projects.buildbattle.handlers.menu.registry.biomes;

import org.bukkit.Chunk;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.handlers.menu.MenuOption;
import plugily.projects.buildbattle.handlers.menu.OptionsRegistry;
import plugily.projects.buildbattle.handlers.misc.ChunkManager;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class BiomeChangeOption {

  public BiomeChangeOption(OptionsRegistry registry) {
    registry.registerOption(new MenuOption(32, "BIOME", new ItemBuilder(XMaterial.MYCELIUM.parseItem())
        .name(new MessageBuilder("MENU_OPTION_CONTENT_BIOME_ITEM_NAME").asKey().build())
        .lore(new MessageBuilder("MENU_OPTION_CONTENT_BIOME_ITEM_LORE").asKey().build())
        .build(), new MessageBuilder("MENU_OPTION_CONTENT_BIOME_INVENTORY").asKey().build()) {

      @Override
      public void onClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();

        humanEntity.closeInventory();
        humanEntity.openInventory(registry.getBiomesRegistry().getInventory());
      }

      @Override
      public void onTargetClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();

        if (!(humanEntity instanceof Player))
          return;

        Player player = (Player) humanEntity;
        BaseArena arena = registry.getPlugin().getArenaRegistry().getArena(player);

        if(arena == null) {
          return;
        }

        BiomeItem item = registry.getBiomesRegistry().getByItem(event.getCurrentItem());
        if(item == BiomeItem.INVALID_BIOME) {
          return;
        }

        Plot plot = arena.getPlotManager().getPlot(player);
        if(plot == null || plot.getCuboid() == null)
          return;

        if(!player.hasPermission(item.getPermission())) {
          new MessageBuilder("IN_GAME_MESSAGES_PLOT_PERMISSION_BIOME").asKey().player(player).sendPlayer();
          return;
        }

        Biome biome = item.getBiome().getBiome();

        if(biome != null) {
          for(Block block : plot.getCuboid().blockList()) {
            block.setBiome(biome);
          }
        }

        for(Chunk chunk : plot.getCuboid().chunkList()) {
          for(Player p : registry.getPlugin().getServer().getOnlinePlayers()) {
            if(p.getWorld().equals(chunk.getWorld())) {
              ChunkManager.sendMapChunk(p, chunk);
            }
          }
        }

        for(Player p : plot.getMembers()) {
          new MessageBuilder("MENU_OPTION_CONTENT_BIOME_CHANGED").asKey().player(p).sendPlayer();
        }
      }
    });
  }

}
