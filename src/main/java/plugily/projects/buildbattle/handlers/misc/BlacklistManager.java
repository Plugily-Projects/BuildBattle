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

package plugily.projects.buildbattle.handlers.misc;

import org.bukkit.Material;
import plugily.projects.buildbattle.Main;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 19.06.2022
 */
public class BlacklistManager {

  private final Main plugin;
  private final List<Material> itemList = new ArrayList<>();
  private final List<Material> floorList = new ArrayList<>();

  public BlacklistManager(Main plugin) {
    this.plugin = plugin;
    loadBlackList();
  }

  public List<Material> getItemList() {
    return Collections.unmodifiableList(itemList);
  }

  public List<Material> getFloorList() {
    return Collections.unmodifiableList(floorList);
  }

  private void loadBlackList() {
    for(String item : plugin.getConfig().getStringList("Items.BlacklistManager")) {
      Optional<XMaterial> opt = XMaterial.matchXMaterial(item.toUpperCase());
      if(!opt.isPresent()) {
        plugin.getDebugger().sendConsoleMsg("&c[Build Battle] Invalid black listed item! " + item + " doesn't exist, are you sure it's properly named?");
        continue;
      }
      itemList.add(opt.get().parseMaterial());
    }

    for(String item : plugin.getConfig().getStringList("Floor.BlacklistManager")) {
      Optional<XMaterial> opt = XMaterial.matchXMaterial(item.toUpperCase());
      if(!opt.isPresent()) {
        plugin.getDebugger().sendConsoleMsg("&c[Build Battle] Invalid black listed item! " + item + " doesn't exist, are you sure it's properly named?");
        continue;
      }
      floorList.add(opt.get().parseMaterial());
    }
  }

}
