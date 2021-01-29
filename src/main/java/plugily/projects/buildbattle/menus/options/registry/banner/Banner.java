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
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 16.07.2019
 */
public class Banner {

  private final List<BannerPattern> patterns = new LinkedList<>();
  private DyeColor color = DyeColor.WHITE;

  public void setBaseColor(DyeColor color) {
    this.color = color;
  }

  public void addPattern(BannerPattern pattern) {
    patterns.add(pattern);
  }

  public void replaceLastPattern(BannerPattern pattern) {
    patterns.remove(patterns.size() - 1);
    patterns.add(pattern);
  }

  public BannerPattern getLastPattern() {
    if(patterns.isEmpty()) {
      return new BannerPattern(DyeColor.BLACK, PatternType.BASE);
    }
    return patterns.get(patterns.size() - 1);
  }

  @SuppressWarnings("deprecation")
  public ItemStack buildBanner() {
    ItemStack item = XMaterial.WHITE_BANNER.parseItem();
    BannerMeta meta = (BannerMeta) item.getItemMeta();
    if(item instanceof org.bukkit.block.Banner) {
      ((org.bukkit.block.Banner) item).setBaseColor(this.color);
    } else {
      meta.setBaseColor(this.color);
    }
    for(BannerPattern pattern : patterns) {
      meta.addPattern(new Pattern(pattern.getDyeColor(), pattern.getPatternType()));
    }
    item.setItemMeta(meta);
    return item;
  }

  public DyeColor getColor() {
    return color;
  }
}
