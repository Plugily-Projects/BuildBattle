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

package plugily.projects.buildbattle.handlers.setup;

import plugily.projects.minigamesbox.classic.handlers.setup.categories.PluginLocationCategory;
import plugily.projects.minigamesbox.classic.handlers.setup.items.category.LocationItem;
import plugily.projects.minigamesbox.classic.handlers.setup.items.category.MultiLocationItem;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;


/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.07.2022
 */
public class LocationCategory extends PluginLocationCategory {
  @Override
  public void addItems(NormalFastInv gui) {
    super.addItems(gui);

    LocationItem starting = new LocationItem(getSetupInventory(), new ItemBuilder(XMaterial.EMERALD_BLOCK.parseMaterial()), "Start World", "Set the location to \nvalidate the world with your plots!", "", inventoryClickEvent -> {
      LocationSerializer.saveLoc(getSetupInventory().getPlugin(), getSetupInventory().getConfig(), "arenas", "instances." + getSetupInventory().getArenaKey() + "." + "startlocation", inventoryClickEvent.getWhoClicked().getLocation());
    }, (emptyConsumer) -> {
    }, false, false, false);
    getItemList().add(starting);
    gui.setItem((getInventoryLine() * 9) + 2, starting);
  }

}