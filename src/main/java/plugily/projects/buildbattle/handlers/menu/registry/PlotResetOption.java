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
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.handlers.menu.MenuOption;
import plugily.projects.buildbattle.handlers.menu.OptionsRegistry;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class PlotResetOption {

  public PlotResetOption(OptionsRegistry registry) {
    registry.registerOption(new MenuOption(34, "RESET", new ItemBuilder(Material.BARRIER)
        .name(new MessageBuilder("MENU_OPTION_CONTENT_RESET_ITEM_NAME").asKey().build())
        .lore(new MessageBuilder("MENU_OPTION_CONTENT_RESET_ITEM_LORE").asKey().build())
        .build()) {
      @Override
      public void onClick(InventoryClickEvent e) {
        HumanEntity humanEntity = e.getWhoClicked();

        if (!(humanEntity instanceof Player))
          return;

        Player player = (Player) humanEntity;

        player.closeInventory();

        BaseArena arena = registry.getPlugin().getArenaRegistry().getArena(player);
        if(arena == null) {
          return;
        }

        Plot plot = arena.getPlotManager().getPlot(player);

        if (plot != null) {
          plot.resetPlot();
          new MessageBuilder("MENU_OPTION_CONTENT_RESET_ITEM_LORE").asKey().player(player).sendPlayer();
        }
      }
    });
  }

}
