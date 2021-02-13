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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
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
public class TimeChangeOption {

  public TimeChangeOption(OptionsRegistry registry) {
    final org.bukkit.inventory.ItemStack clock = XMaterial.CLOCK.parseItem();
    registry.registerOption(new MenuOption(30, "TIME", new ItemBuilder(clock)
        .name(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Time.Item-Name"))
        .lore(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Time.Item-Lore"))
        .build(), registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Time.Inventory-Name")) {

      @Override
      public void onClick(InventoryClickEvent e) {
        e.getWhoClicked().closeInventory();

        Inventory timeInv = Bukkit.createInventory(null, 9, registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Time.Inventory-Name"));
        timeInv.setItem(0, new ItemBuilder(clock)
            .name(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Time.Time-Type.World-Time")).build());
        timeInv.setItem(1, new ItemBuilder(clock)
            .name(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Time.Time-Type.Day")).build());
        timeInv.setItem(2, new ItemBuilder(clock)
            .name(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Time.Time-Type.Noon")).build());
        timeInv.setItem(3, new ItemBuilder(clock)
            .name(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Time.Time-Type.Sunset")).build());
        timeInv.setItem(4, new ItemBuilder(clock)
            .name(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Time.Time-Type.Night")).build());
        timeInv.setItem(5, new ItemBuilder(clock)
            .name(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Time.Time-Type.MidNight")).build());
        timeInv.setItem(6, new ItemBuilder(clock)
            .name(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Time.Time-Type.Sunrise")).build());
        timeInv.addItem(Utils.getGoBackItem());
        e.getWhoClicked().openInventory(timeInv);
      }

      @Override
      public void onTargetClick(InventoryClickEvent e) {
        BaseArena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
        if(arena == null) {
          return;
        }
        Plot plot = arena.getPlotManager().getPlot((Player) e.getWhoClicked());
        plot.setTime(Plot.Time.valueOf(TimeClickPosition.getByPosition(e.getSlot()).toString()));
        for(Player p : plot.getOwners()) {
          p.setPlayerTime(Plot.Time.format(plot.getTime(), p.getWorld().getTime()), false);
          p.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Time.Time-Set"));
        }
      }
    });
  }

  public enum TimeClickPosition {
    WORLD_TIME(0), DAY(1), NOON(2), SUNSET(3), NIGHT(4), MIDNIGHT(5), SUNRISE(6);

    private final int position;

    TimeClickPosition(int position) {
      this.position = position;
    }

    /**
     * Get time by clicked inventory position
     *
     * @param pos clicked position
     * @return clicked time, returns WORLD_TIME if clicked not matching results
     */
    public static TimeClickPosition getByPosition(int pos) {
      for(TimeClickPosition position : values()) {
        if(position.getPosition() == pos) {
          return position;
        }
      }
      return TimeClickPosition.WORLD_TIME;
    }

    public int getPosition() {
      return position;
    }
  }

}
