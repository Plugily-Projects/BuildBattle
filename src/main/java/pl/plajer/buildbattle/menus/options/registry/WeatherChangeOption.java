/*
 * BuildBattle 4 - Ultimate building competition minigame
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.buildbattle.menus.options.registry;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.plots.Plot;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.menus.options.MenuOption;
import pl.plajer.buildbattle.menus.options.OptionsRegistry;
import pl.plajerlair.core.utils.ItemBuilder;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class WeatherChangeOption {

  public WeatherChangeOption(OptionsRegistry registry) {
    registry.registerOption(new MenuOption(28, "WEATHER", new ItemBuilder(new ItemStack(Material.BUCKET))
        .name(ChatManager.colorMessage("Menus.Option-Menu.Items.Weather.Item-Name"))
        .lore(ChatManager.colorMessage("Menus.Option-Menu.Items.Weather.Item-Lore"))
        .build(), ChatManager.colorMessage("Menus.Option-Menu.Items.Weather.Inventory-Name")) {

      @Override
      public void onClick(InventoryClickEvent e) {
        e.getWhoClicked().closeInventory();

        Inventory weatherInv = Bukkit.createInventory(null, 9, ChatManager.colorMessage("Menus.Option-Menu.Items.Weather.Inventory-Name"));
        weatherInv.addItem(new ItemBuilder(new ItemStack(Material.BUCKET)).name(ChatManager.colorMessage("Menus.Option-Menu.Items.Weather.Weather-Type.Clear")).build());
        weatherInv.addItem(new ItemBuilder(new ItemStack(Material.BUCKET)).name(ChatManager.colorMessage("Menus.Option-Menu.Items.Weather.Weather-Type.Downfall")).build());
        e.getWhoClicked().openInventory(weatherInv);
      }

      @Override
      public void onTargetClick(InventoryClickEvent e) {
        Arena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
        if (arena == null) {
          return;
        }
        Plot plot = arena.getPlotManager().getPlot((Player) e.getWhoClicked());
        if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatManager.colorMessage("Menus.Option-Menu.Items.Weather.Weather-Type.Downfall"))) {
          plot.setWeatherType(WeatherType.DOWNFALL);
        } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatManager.colorMessage("Menus.Option-Menu.Items.Weather.Weather-Type.Clear"))) {
          plot.setWeatherType(WeatherType.CLEAR);
        }
        for (UUID owner : plot.getOwners()) {
          if (!Bukkit.getPlayer(owner).isOnline()) {
            continue;
          }
          Bukkit.getPlayer(owner).setPlayerWeather(plot.getWeatherType());
          Bukkit.getPlayer(owner).sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Menus.Option-Menu.Items.Weather.Weather-Set"));
        }
      }
    });
  }

}
