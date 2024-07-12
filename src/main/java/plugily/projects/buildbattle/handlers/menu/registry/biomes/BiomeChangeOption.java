/*
 *
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2022 Plugily Projects - maintained by Tigerpanzer_02 and contributors
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
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.inventory.common.item.SimpleClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

import java.util.Set;

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
        if(!(humanEntity instanceof Player)) {
          return;
        }
        Player player = (Player) humanEntity;
        BaseArena arena = registry.getPlugin().getArenaRegistry().getArena(player);
        if(arena == null) {
          return;
        }
        Plot plot = arena.getPlotManager().getPlot(player);
        if(plot == null || plot.getCuboid() == null) {
          return;
        }

        Set<BiomeItem> biomes = registry.getBiomesRegistry().getBiomes();

        NormalFastInv gui = new NormalFastInv(registry.getPlugin().getBukkitHelper().serializeInt(biomes.size() + 1), new MessageBuilder("MENU_OPTION_CONTENT_BIOME_INVENTORY").asKey().build());

        for(BiomeItem biomeItem : biomes) {
          gui.addItem(new SimpleClickableItem(biomeItem.getItemStack(), clickEvent -> {
            if(!player.hasPermission(biomeItem.getPermission())) {
              new MessageBuilder("IN_GAME_MESSAGES_PLOT_PERMISSION_BIOME").asKey().player(player).sendPlayer();
              return;
            }
            Biome biome = biomeItem.getBiome().getBiome();
            if(biome != null) {
              for(Block block : plot.getCuboid().blockList()) {
                block.setBiome(biome);
              }
            }

            for(Chunk chunk : plot.getCuboid().chunkList()) {
              if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_15)) {
                chunk.getWorld().refreshChunk(chunk.getX(), chunk.getZ());
                continue;
              }

              for(Player p : registry.getPlugin().getServer().getOnlinePlayers()) {
                if(p.getWorld().equals(chunk.getWorld())) {
                  ChunkManager.sendMapChunk(p, chunk);
                }
              }
            }

            for(Player p : plot.getMembers()) {
              new MessageBuilder("MENU_OPTION_CONTENT_BIOME_CHANGED").asKey().player(p).sendPlayer();
            }
          }));
        }
        registry.getPlugin().getOptionsRegistry().addGoBackItem(gui, gui.getInventory().getSize() - 1);

        gui.open(player);
      }
    });
  }

}
