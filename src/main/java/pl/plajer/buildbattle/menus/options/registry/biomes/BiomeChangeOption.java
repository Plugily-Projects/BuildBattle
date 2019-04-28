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

package pl.plajer.buildbattle.menus.options.registry.biomes;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.impl.BaseArena;
import pl.plajer.buildbattle.arena.managers.plots.Plot;
import pl.plajer.buildbattle.menus.options.MenuOption;
import pl.plajer.buildbattle.menus.options.OptionsRegistry;
import pl.plajer.buildbattle.utils.Utils;
import pl.plajerlair.core.utils.ItemBuilder;
import pl.plajerlair.core.utils.MinigameUtils;
import pl.plajerlair.core.utils.XMaterial;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class BiomeChangeOption {

  private Main plugin;

  public BiomeChangeOption(OptionsRegistry registry) {
    this.plugin = registry.getPlugin();
    registry.registerOption(new MenuOption(32, "BIOME", new ItemBuilder(XMaterial.MYCELIUM.parseItem())
        .name(plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Biome.Item-Name"))
        .lore(plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Biome.Item-Lore"))
        .build(), plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Biome.Inventory-Name")) {

      @Override
      public void onClick(InventoryClickEvent e) {
        e.getWhoClicked().closeInventory();

        Inventory biomeInv = Bukkit.createInventory(null, MinigameUtils.serializeInt(registry.getBiomesRegistry().getBiomes().size()),
            plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Biome.Inventory-Name"));
        for (BiomeItem biome : registry.getBiomesRegistry().getBiomes()) {
          biomeInv.addItem(biome.getItemStack());
        }
        e.getWhoClicked().openInventory(biomeInv);
      }

      @Override
      public void onTargetClick(InventoryClickEvent e) {
        BaseArena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
        if (arena == null) {
          return;
        }
        Plot plot = arena.getPlotManager().getPlot((Player) e.getWhoClicked());
          Biome biome = registry.getBiomesRegistry().getByItem(e.getCurrentItem()).getBiome().parseBiome();
          for (Block block : plot.getCuboid().blockList()) {
            block.setBiome(biome);
          }
        try {
          for (Chunk chunk : plot.getCuboid().chunkList()) {
            for (Player p : Bukkit.getOnlinePlayers()) {
              Utils.sendPacket(p, Utils.getNMSClass("PacketPlayOutMapChunk").getConstructor(Utils.getNMSClass("Chunk"), int.class)
                  .newInstance(chunk.getClass().getMethod("getHandle").invoke(chunk), 65535));
            }
          }
        } catch (ReflectiveOperationException ignored) {/*fail silently*/}
          for (Player p : plot.getOwners()) {
            p.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Biome.Biome-Set"));
          }
      }
    });
  }

}
