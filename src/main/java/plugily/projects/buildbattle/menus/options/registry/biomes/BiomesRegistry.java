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

package plugily.projects.buildbattle.menus.options.registry.biomes;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import pl.plajerlair.commonsbox.minecraft.compat.xseries.XBiome;
import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import pl.plajerlair.commonsbox.minecraft.misc.stuff.ComplementAccessor;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.menus.options.OptionsRegistry;
import plugily.projects.buildbattle.utils.Debugger;
import plugily.projects.buildbattle.utils.Utils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Plajer
 * <p>
 * Created at 03.02.2019
 */
public class BiomesRegistry {

  private Inventory inventory;
  private final Set<BiomeItem> biomes = new HashSet<>();
  private final Main plugin;

  public BiomesRegistry(OptionsRegistry registry) {
    this.plugin = registry.getPlugin();
    registerBiomes();
    registerInventory();
  }

  private void registerBiomes() {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "biomes");
    Debugger.debug(Debugger.Level.TASK, "Registering biomes!");
    int i = 0;
    for(String biome : config.getKeys(false)) {
      if(i >= 52) {
        Debugger.debug(Debugger.Level.WARN, "There are too many biomes to register! Menu can't hold any more!");
        break;
      }
      BiomeItem biomeItem = new BiomeItem(new ItemBuilder(XMaterial.matchXMaterial(config
          .getString(biome + ".material-name", "bedrock").toUpperCase()).orElse(XMaterial.BEDROCK).parseItem())
          .name(plugin.getChatManager().colorRawMessage(config.getString(biome + ".displayname")))
          .lore(config.getStringList(biome + ".lore")
              .stream().map(lore -> lore = plugin.getChatManager().colorRawMessage(lore)).collect(Collectors.toList()))
          .build(), config.getString(biome + ".permission"), XBiome.matchXBiome(biome).orElse(XBiome.BADLANDS));
      biomes.add(biomeItem);
      i++;
    }
    Debugger.debug("Registered in total " + i + " biomes!");
  }

  private void registerInventory() {
    Inventory inv = ComplementAccessor.getComplement().createInventory(null, Utils.serializeInt(biomes.size() + 1),
        plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Biome.Inventory-Name"));

    biomes.stream().map(BiomeItem::getItemStack).forEach(inv::addItem);

    inv.addItem(Utils.getGoBackItem());
    inventory = inv;
  }

  public Inventory getInventory() {
    return inventory;
  }

  public void setInventory(Inventory inventory) {
    this.inventory = inventory;
  }

  public BiomeItem getByItem(ItemStack stack) {
    for(BiomeItem item : biomes) {
      if(item.getItemStack().isSimilar(stack)) {
        return item;
      }
    }
    return BiomeItem.INVALID_BIOME;
  }

  public Set<BiomeItem> getBiomes() {
    return biomes;
  }

}
