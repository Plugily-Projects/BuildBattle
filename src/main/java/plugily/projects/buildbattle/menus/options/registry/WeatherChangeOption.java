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

package plugily.projects.buildbattle.menus.options.registry;

import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import pl.plajerlair.commonsbox.minecraft.misc.stuff.ComplementAccessor;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.menus.options.MenuOption;
import plugily.projects.buildbattle.menus.options.OptionsRegistry;
import plugily.projects.buildbattle.utils.Utils;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class WeatherChangeOption {

  public WeatherChangeOption(OptionsRegistry registry) {
    registry.registerOption(new MenuOption(28, "WEATHER", new ItemBuilder(Material.BUCKET)
        .name(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Weather.Item-Name"))
        .lore(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Weather.Item-Lore"))
        .build(), registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Weather.Inventory-Name")) {

      @Override
      public void onClick(InventoryClickEvent e) {
        e.getWhoClicked().closeInventory();

        Inventory weatherInv = ComplementAccessor.getComplement().createInventory(null, 9, registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Weather.Inventory-Name"));
        weatherInv.addItem(new ItemBuilder(Material.BUCKET).name(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Weather.Weather-Type.Clear")).build());
        weatherInv.addItem(new ItemBuilder(Material.BUCKET).name(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Weather.Weather-Type.Downfall")).build());
        weatherInv.addItem(Utils.getGoBackItem());
        e.getWhoClicked().openInventory(weatherInv);
      }

      @Override
      public void onTargetClick(InventoryClickEvent e) {
        if(e.getCurrentItem() == null)
          return;

        BaseArena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
        if(arena == null) {
          return;
        }
        Plot plot = arena.getPlotManager().getPlot((Player) e.getWhoClicked());
        if(ComplementAccessor.getComplement().getDisplayName(e.getCurrentItem().getItemMeta()).equalsIgnoreCase(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Weather.Weather-Type.Downfall"))) {
          plot.setWeatherType(WeatherType.DOWNFALL);
        } else if(ComplementAccessor.getComplement().getDisplayName(e.getCurrentItem().getItemMeta()).equalsIgnoreCase(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Weather.Weather-Type.Clear"))) {
          plot.setWeatherType(WeatherType.CLEAR);
        }
        for(Player p : plot.getOwners()) {
          p.setPlayerWeather(plot.getWeatherType());
          p.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Weather.Weather-Set"));
        }
      }
    });
  }

}
