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

package plugily.projects.buildbattle.handlers.menu.registry;

import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.handlers.menu.MenuOption;
import plugily.projects.buildbattle.handlers.menu.OptionsRegistry;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.inventory.common.item.SimpleClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

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
      public void onClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        humanEntity.closeInventory();
        if(!(humanEntity instanceof Player)) {
          return;
        }
        Player player = (Player) humanEntity;
        BaseArena arena = registry.getPlugin().getArenaRegistry().getArena(player);

        if(arena == null) {
          return;
        }

        Plot plot = arena.getPlotManager().getPlot(player);
        if(plot == null) {
          return;
        }
        NormalFastInv gui = new NormalFastInv(9, new MessageBuilder("MENU_OPTION_CONTENT_WEATHER_INVENTORY").asKey().build());
        for(WeatherType weatherType : WeatherType.values()) {
          Material material = Material.BUCKET;
          if(weatherType == WeatherType.CLEAR) {
            material = Material.WATER_BUCKET;
          }
          gui.addItem(new SimpleClickableItem(new ItemBuilder(material).name(new MessageBuilder("MENU_OPTION_CONTENT_WEATHER_TYPE_" + weatherType.name()).asKey().build()).build(), clickEvent -> {
            plot.setWeatherType(weatherType);

            for(Player p : plot.getMembers()) {
              p.setPlayerWeather(plot.getWeatherType());
              new MessageBuilder("MENU_OPTION_CONTENT_WEATHER_CHANGED").asKey().value(new MessageBuilder("MENU_OPTION_CONTENT_WEATHER_TYPE_" + weatherType.name()).asKey().build()).player(p).sendPlayer();
            }
          }));
        }
        registry.getPlugin().getOptionsRegistry().addGoBackItem(gui, 8);
        gui.open(player);
      }
    });
  }

}
