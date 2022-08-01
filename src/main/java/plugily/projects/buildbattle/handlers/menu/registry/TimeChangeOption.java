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

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.handlers.menu.MenuOption;
import plugily.projects.buildbattle.handlers.menu.OptionsRegistry;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;

import static plugily.projects.buildbattle.handlers.menu.registry.TimeChangeOption.TimeClickPosition.getByPosition;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class TimeChangeOption {

  public TimeChangeOption(OptionsRegistry registry) {
    final ItemStack clock = XMaterial.CLOCK.parseItem();
    registry.registerOption(new MenuOption(30, "TIME", new ItemBuilder(clock)
        .name(new MessageBuilder("MENU_OPTION_CONTENT_TIME_ITEM_NAME").asKey().build())
        .lore(new MessageBuilder("MENU_OPTION_CONTENT_TIME_ITEM_LORE").asKey().build())
        .build(), new MessageBuilder("MENU_OPTION_CONTENT_TIME_INVENTORY").asKey().build()) {

      @Override
      public void onClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();

        humanEntity.closeInventory();

        Inventory timeInv = ComplementAccessor.getComplement().createInventory(null, 9, new MessageBuilder("MENU_OPTION_CONTENT_TIME_INVENTORY").asKey().build());
        timeInv.setItem(0, new ItemBuilder(clock)
            .name(new MessageBuilder("MENU_OPTION_CONTENT_TIME_TYPE_WORLD").asKey().build()).build());
        timeInv.setItem(1, new ItemBuilder(clock)
            .name(new MessageBuilder("MENU_OPTION_CONTENT_TIME_TYPE_DAY").asKey().build()).build());
        timeInv.setItem(2, new ItemBuilder(clock)
            .name(new MessageBuilder("MENU_OPTION_CONTENT_TIME_TYPE_NOON").asKey().build()).build());
        timeInv.setItem(3, new ItemBuilder(clock)
            .name(new MessageBuilder("MENU_OPTION_CONTENT_TIME_TYPE_SUNSET").asKey().build()).build());
        timeInv.setItem(4, new ItemBuilder(clock)
            .name(new MessageBuilder("MENU_OPTION_CONTENT_TIME_TYPE_NIGHT").asKey().build()).build());
        timeInv.setItem(5, new ItemBuilder(clock)
            .name(new MessageBuilder("MENU_OPTION_CONTENT_TIME_TYPE_MIDNIGHT").asKey().build()).build());
        timeInv.setItem(6, new ItemBuilder(clock)
            .name(new MessageBuilder("MENU_OPTION_CONTENT_TIME_TYPE_SUNRISE").asKey().build()).build());
        timeInv.addItem(registry.getGoBackItem());
        humanEntity.openInventory(timeInv);
      }

      @Override
      public void onTargetClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();

        if (!(humanEntity instanceof Player))
          return;

        Player player = (Player) humanEntity;

        BaseArena arena = registry.getPlugin().getArenaRegistry().getArena(player);
        if(arena == null) {
          return;
        }

        Plot plot = arena.getPlotManager().getPlot(player);
        if(plot == null)
          return;

        plot.setTime(Plot.Time.valueOf(getByPosition(event.getSlot()).toString()));

        for(Player p : plot.getMembers()) {
          p.setPlayerTime(Plot.Time.format(plot.getTime(), p.getWorld().getTime()), false);
          new MessageBuilder("MENU_OPTION_CONTENT_TIME_CHANGED").asKey().player(p).sendPlayer();
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
      return WORLD_TIME;
    }

    public int getPosition() {
      return position;
    }
  }

}
