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

package plugily.projects.buildbattle.menus.options.registry.banner;

import org.bukkit.DyeColor;
import org.bukkit.block.banner.PatternType;

/**
 * @author Plajer
 * <p>
 * Created at 16.07.2019
 */
public class BannerPattern {

  private DyeColor dyeColor;
  private PatternType patternType;

  public BannerPattern(DyeColor dyeColor, PatternType patternType) {
    this.dyeColor = dyeColor;
    this.patternType = patternType;
  }

  public DyeColor getDyeColor() {
    return dyeColor;
  }

  public PatternType getPatternType() {
    return patternType;
  }
}
