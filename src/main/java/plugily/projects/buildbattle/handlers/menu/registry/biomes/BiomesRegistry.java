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

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.handlers.menu.OptionsRegistry;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XBiome;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;

import java.util.HashSet;
import java.util.Set;

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
  }

  private void registerBiomes() {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "biomes");
    plugin.getDebugger().debug("Registering biomes!");
    int i = 0;
    for(String biome : config.getKeys(false)) {
      if(i >= 52) {
        plugin.getDebugger().debug("There are too many biomes to register! Menu can't hold any more!");
        break;
      }
      java.util.List<String> lore = config.getStringList(biome + ".lore");
      lore.replaceAll(line -> new MessageBuilder(line).build());
      BiomeItem biomeItem = new BiomeItem(new ItemBuilder(XMaterial.matchXMaterial(config
          .getString(biome + ".material-name", "bedrock").toUpperCase()).orElse(XMaterial.BEDROCK).parseItem())
          .name(new MessageBuilder(config.getString(biome + ".displayname")).build())
          .lore(lore)
          .build(), config.getString(biome + ".permission"), XBiome.of(biome).orElse(XBiome.BADLANDS));
      biomes.add(biomeItem);
      i++;
    }
    plugin.getDebugger().debug("Registered in total " + i + " biomes!");
  }

  public Set<BiomeItem> getBiomes() {
    return biomes;
  }

}
