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

package pl.plajer.buildbattle.utils;

import org.bukkit.block.Biome;

/**
 * @author Plajer
 * <p>
 * Created at 03.02.2019
 */
public enum XBiome {

  SNOWY_TUNDRA("ICE_FLATS"),
  SNOWY_TAIGA_HILLS("TAIGA_COLD_HILLS"),
  MUSHROOM_FIELD_SHORE("MUSHROOM_ISLAND_SHORE"),
  SUNFLOWER_PLAINS("MUTATED_PLAINS"),
  NETHER("HELL"),
  MOUNTAIN_EDGE("SMALLER_EXTREME_HILLS"),
  DEEP_COLD_OCEAN("COLD_DEEP_OCEAN"),
  MODIFIED_JUNGLE("MUTATED_JUNGLE"),
  WOODED_HILLS("FOREST_HILLS"),
  DESERT_LAKES("MUTATED_DESERT"),
  FLOWER_FOREST("MUTATED_FOREST"),
  MODIFIED_GRAVELLY_MOUNTAINS("MUTATED_EXTREME_HILLS_WITH_TREES"),
  SNOWY_TAIGA_MOUNTAINS("MUTATED_TAIGA_COLD"),
  THE_END("SKY"),
  MODIFIED_WOODED_BADLANDS_PLATEAU("MUTATED_MESA_ROCK"),
  ICE_SPIKES("MUTATED_ICE_FLATS"),
  THE_VOID("VOID"),
  TALL_BIRCH_FOREST("MUTATED_BIRCH_FOREST"),
  SWAMP_HILLS("MUTATED_SWAMPLAND"),
  GIANT_TREE_TAIGA_HILLS("REDWOOD_TAIGA_HILLS"),
  SNOWY_TAIGA("TAIGA_COLD"),
  TAIGA_MOUNTAINS("MUTATED_TAIGA"),
  MODIFIED_JUNGLE_EDGE("MUTATED_JUNGLE_EDGE"),
  SMALL_END_ISLANDS("SKY_ISLAND_LOW"),
  WOODED_BADLANDS_PLATEAU("MESA_ROCK"),
  DEEP_LUKEWARM_OCEAN("LUKEWARM_DEEP_OCEAN"),
  SWAMP("SWAMPLAND"),
  MODIFIED_BADLANDS_PLATEAU("MUTATED_MESA_CLEAR_ROCK"),
  END_BARRENS("SKY_ISLAND_BARREN"),
  BEACH("BEACHES"),
  WOODED_MOUNTAINS("EXTREME_HILLS_WITH_TREES"),
  TALL_BIRCH_HILLS("MUTATED_BIRCH_FOREST_HILLS"),
  BADLANDS("MESA"),
  BADLANDS_PLATEAU("MESA_CLEAR_ROCK"),
  GIANT_SPRUCE_TAIGA_HILLS("MUTATED_REDWOOD_TAIGA_HILLS"),
  SHATTERED_SAVANNA("MUTATED_SAVANNA"),
  SAVANNA_PLATEAU("SAVANNA_ROCK"),
  SNOWY_MOUNTAINS("ICE_MOUNTAINS"),
  DARK_FOREST_HILLS("MUTATED_ROOFED_FOREST"),
  GIANT_TREE_TAIGA("REDWOOD_TAIGA"),
  ERODED_BADLANDS("MUTATED_MESA"),
  MUSHROOM_FIELDS("MUSHROOM_ISLAND"),
  END_MIDLANDS("SKY_ISLAND_MEDIUM"),
  DEEP_WARM_OCEAN("WARM_DEEP_OCEAN"),
  DEEP_FROZEN_OCEAN("FROZEN_DEEP_OCEAN"),
  SHATTERED_SAVANNA_PLATEAU("MUTATED_SAVANNA_ROCK"),
  MOUNTAINS("EXTREME_HILLS"),
  END_HIGHLANDS("SKY_ISLAND_HIGH"),
  DARK_FOREST("ROOFED_FOREST"),
  SNOWY_BEACH("COLD_BEACH"),
  GIANT_SPRUCE_TAIGA("MUTATED_REDWOOD_TAIGA"),
  GRAVELLY_MOUNTAINS("MUTATED_EXTREME_HILLS"),
  STONE_SHORE("STONE_BEACH"),
  OCEAN("OCEAN"),
  PLAINS("PLAINS"),
  DESERT("DESERT"),
  FOREST("FOREST"),
  TAIGA("TAIGA"),
  RIVER("RIVER"),
  FROZEN_OCEAN("FROZEN_OCEAN"),
  FROZEN_RIVER("FROZEN_RIVER"),
  DESERT_HILLS("DESERT_HILLS"),
  TAIGA_HILLS("TAIGA_HILLS"),
  JUNGLE("JUNGLE"),
  JUNGLE_HILLS("JUNGLE_HILLS"),
  JUNGLE_EDGE("JUNGLE_EDGE"),
  DEEP_OCEAN("DEEP_OCEAN"),
  BIRCH_FOREST("BIRCH_FOREST"),
  BIRCH_FOREST_HILLS("BIRCH_FOREST_HILLS"),
  SAVANNA("SAVANNA"),
  WARM_OCEAN("WARM_OCEAN"),
  LUKEWARM_OCEAN("LUKEWARM_OCEAN"),
  COLD_OCEAN("COLD_OCEAN");

  private String legacyValue;

  XBiome(String legacyValue) {
    this.legacyValue = legacyValue;
  }

  public static XBiome fromString(String biome) {
    try {
      return XBiome.valueOf(biome);
    } catch (Exception ex) {
      for (XBiome xbiome : XBiome.values()) {
        if (xbiome.getLegacyValue().equalsIgnoreCase(biome)) {
          return xbiome;
        }
      }
    }
    return null;
  }

  public String getLegacyValue() {
    return legacyValue;
  }

  public Biome parseBiome() {
    try {
      return Biome.valueOf(this.toString());
    } catch (IllegalArgumentException ex) {
      return Biome.valueOf(legacyValue);
    }
  }

}
