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

package plugily.projects.buildbattle.handlers.setup;

import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.handlers.setup.categories.PluginSpecificCategory;
import plugily.projects.minigamesbox.classic.handlers.setup.items.category.CountItem;
import plugily.projects.minigamesbox.classic.handlers.setup.items.category.MultiLocationItem;
import plugily.projects.minigamesbox.classic.handlers.setup.items.category.MultiLocationSelectorItem;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;


/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.07.2022
 */
public class SpecificCategory extends PluginSpecificCategory {
  @Override
  public void addItems(NormalFastInv gui) {
    super.addItems(gui);
    MultiLocationSelectorItem gamePlot = new MultiLocationSelectorItem(getSetupInventory(), new ItemBuilder(XMaterial.DIAMOND.parseMaterial()), "Game Plot", "Select one plot with our built-in \n selector (select minimum and maximum \n plot opposite selections with built-in wand) \n INCLUDE the plot floor!", "plots", 2);
    gui.setItem(1, gamePlot);
    getItemList().add(gamePlot);

    MultiLocationItem floorNPC = new MultiLocationItem(getSetupInventory(), new ItemBuilder(XMaterial.VILLAGER_SPAWN_EGG.parseMaterial()), "Floor Changer NPC", "Add floor changer NPC to your plot.\nRequires Citizens plugin! Runs addnpc command", "floornpc", 0, event -> {
      ((Player) event.getWhoClicked()).performCommand("bba addnpc");
    }, interactEvent -> {
    });
    gui.setItem(2, floorNPC);
    getItemList().add(floorNPC);

    CountItem plotSize = new CountItem(getSetupInventory(), new ItemBuilder(XMaterial.CAKE.parseMaterial()), "Plot Member Size", "Choose how many players can play on one plot", "plotmembersize");
    gui.setItem(3, plotSize);
    getItemList().add(plotSize);
  }

}