/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.impl.BaseArena;
import pl.plajer.buildbattle.arena.managers.plots.Plot;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.menus.options.MenuOption;
import pl.plajer.buildbattle.menus.options.OptionsRegistry;
import pl.plajerlair.core.utils.ItemBuilder;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class PlotResetOption {

  public PlotResetOption(OptionsRegistry registry) {
    registry.registerOption(new MenuOption(34, "RESET", new ItemBuilder(new ItemStack(Material.BARRIER))
        .name(ChatManager.colorMessage("Menus.Option-Menu.Items.Reset.Item-Name"))
        .lore(ChatManager.colorMessage("Menus.Option-Menu.Items.Reset.Item-Lore"))
        .build()) {
      @Override
      public void onClick(InventoryClickEvent e) {
        e.getWhoClicked().closeInventory();
        BaseArena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
        if (arena == null) {
          return;
        }
        Plot plot = arena.getPlotManager().getPlot((Player) e.getWhoClicked());
        plot.resetPlot();
        e.getWhoClicked().sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Menus.Option-Menu.Items.Reset.Plot-Reset"));
      }
    });
  }

}
