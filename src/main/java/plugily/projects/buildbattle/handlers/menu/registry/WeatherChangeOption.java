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

package plugily.projects.buildbattle.handlers.menu.registry;

import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.handlers.menu.MenuOption;
import plugily.projects.buildbattle.handlers.menu.OptionsRegistry;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class WeatherChangeOption {

  public WeatherChangeOption(OptionsRegistry registry) {
    registry.registerOption(new MenuOption(28, "WEATHER", new ItemBuilder(Material.BUCKET)
        .name(new MessageBuilder("MENU_OPTION_CONTENT_WEATHER_ITEM_NAME").asKey().build())
        .lore(new MessageBuilder("MENU_OPTION_CONTENT_WEATHER_ITEM_LORE").asKey().build())
        .build(), new MessageBuilder("MENU_OPTION_CONTENT_WEATHER_INVENTORY").asKey().build()) {

      @Override
      public void onClick(InventoryClickEvent e) {
        org.bukkit.entity.HumanEntity who = e.getWhoClicked();
        who.closeInventory();

        Inventory weatherInv = ComplementAccessor.getComplement().createInventory(null, 9, new MessageBuilder("MENU_OPTION_CONTENT_WEATHER_INVENTORY").asKey().build());
        weatherInv.addItem(new ItemBuilder(Material.BUCKET).name(new MessageBuilder("MENU_OPTION_CONTENT_WEATHER_TYPE_CLEAR").asKey().build()).build());
        weatherInv.addItem(new ItemBuilder(Material.WATER_BUCKET).name(new MessageBuilder("MENU_OPTION_CONTENT_WEATHER_TYPE_DOWNFALL").asKey().build()).build());
        weatherInv.addItem(registry.getGoBackItem());
        who.openInventory(weatherInv);
      }

      @Override
      public void onTargetClick(InventoryClickEvent e) {
        org.bukkit.inventory.ItemStack item = e.getCurrentItem();
        if(item == null)
          return;

        Player who = (Player) e.getWhoClicked();

        BaseArena arena = registry.getPlugin().getArenaRegistry().getArena(who);
        if(arena == null) {
          return;
        }

        Plot plot = arena.getPlotManager().getPlot(who);
        if(plot == null) {
          return;
        }
        if(ComplementAccessor.getComplement().getDisplayName(item.getItemMeta()).equalsIgnoreCase(new MessageBuilder("MENU_OPTION_CONTENT_WEATHER_TYPE_DOWNFALL").asKey().build())) {
          plot.setWeatherType(WeatherType.DOWNFALL);
        } else if(ComplementAccessor.getComplement().getDisplayName(item.getItemMeta()).equalsIgnoreCase(new MessageBuilder("MENU_OPTION_CONTENT_WEATHER_TYPE_CLEAR").asKey().build())) {
          plot.setWeatherType(WeatherType.CLEAR);
        }

        for(Player p : plot.getMembers()) {
          p.setPlayerWeather(plot.getWeatherType());
          new MessageBuilder("MENU_OPTION_CONTENT_WEATHER_TYPE_DOWNFALL").asKey().player(p).sendPlayer();
        }
      }
    });
  }

}
