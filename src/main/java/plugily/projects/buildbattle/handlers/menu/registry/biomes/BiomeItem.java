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

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XBiome;

/**
 * @author Plajer
 * <p>
 * Created at 03.02.2019
 */
public class BiomeItem {

  public static final BiomeItem INVALID_BIOME = new BiomeItem(new ItemStack(Material.DIRT), "", XBiome.PLAINS);
  private final ItemStack itemStack;
  private final String permission;
  private final XBiome biome;

  public BiomeItem(ItemStack itemStack, String permission, XBiome biome) {
    this.itemStack = itemStack;
    this.permission = permission;
    this.biome = biome;
  }

  public ItemStack getItemStack() {
    return itemStack;
  }

  public String getPermission() {
    return permission;
  }

  public XBiome getBiome() {
    return biome;
  }
}
