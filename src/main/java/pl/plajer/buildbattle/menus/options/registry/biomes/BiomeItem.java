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

import org.bukkit.inventory.ItemStack;

import pl.plajer.buildbattle.utils.XBiome;

/**
 * @author Plajer
 * <p>
 * Created at 03.02.2019
 */
public class BiomeItem {

  private ItemStack itemStack;
  private String permission;
  private XBiome biome;

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
