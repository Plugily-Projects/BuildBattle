/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.menus.options.OptionsRegistry;
import pl.plajer.buildbattle.utils.XBiome;
import pl.plajerlair.core.debug.Debugger;
import pl.plajerlair.core.debug.LogLevel;
import pl.plajerlair.core.utils.ConfigUtils;
import pl.plajerlair.core.utils.ItemBuilder;
import pl.plajerlair.core.utils.MinigameUtils;
import pl.plajerlair.core.utils.XMaterial;

/**
 * @author Plajer
 * <p>
 * Created at 03.02.2019
 */
public class BiomesRegistry {

  private Inventory inventory;
  private Set<BiomeItem> biomes = new HashSet<>();
  private Main plugin;

  public BiomesRegistry(OptionsRegistry registry) {
    this.plugin = registry.getPlugin();
    registerBiomes();
    registerInventory();
  }

  private void registerBiomes() {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "biomes");
    Debugger.debug(LogLevel.TASK, "Registering biomes!");
    int i = 0;
    for (String biome : config.getKeys(false)) {
      if (i >= 52) {
        Debugger.debug(LogLevel.WARN, "There are too many biomes to register! Menu can't hold any more!");
        break;
      }
      BiomeItem biomeItem = new BiomeItem(new ItemBuilder(XMaterial.fromString(config
          .getString(biome + ".material-name").toUpperCase()).parseItem())
          .name(ChatManager.colorRawMessage(config.getString(biome + ".displayname")))
          .lore(config.getStringList(biome + ".lore")
              .stream().map((lore) -> lore = ChatManager.colorRawMessage(lore)).collect(Collectors.toList()))
          .build(), config.getString(biome + ".permission"), XBiome.fromString(biome));
      biomes.add(biomeItem);
      i++;
    }
    Debugger.debug(LogLevel.INFO, "Registered in total " + i + " biomes!");
  }

  private void registerInventory() {
    Inventory inv = Bukkit.createInventory(null, MinigameUtils.serializeInt(biomes.size()),
        ChatManager.colorMessage("Menus.Option-Menu.Items.Biome.Inventory-Name"));
    for (BiomeItem item : biomes) {
      inv.addItem(item.getItemStack());
    }
    inventory = inv;
  }

  public Inventory getInventory() {
    return inventory;
  }

  public void setInventory(Inventory inventory) {
    this.inventory = inventory;
  }

  @Nullable
  public BiomeItem getByItem(ItemStack stack) {
    for (BiomeItem item : biomes) {
      if (item.getItemStack().isSimilar(stack)) {
        return item;
      }
    }
    return null;
  }

  public Set<BiomeItem> getBiomes() {
    return biomes;
  }

}
