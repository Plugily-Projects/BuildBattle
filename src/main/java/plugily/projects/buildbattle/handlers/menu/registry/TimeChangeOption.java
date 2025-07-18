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

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.handlers.menu.MenuOption;
import plugily.projects.buildbattle.handlers.menu.OptionsRegistry;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.inventory.common.item.SimpleClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

import static plugily.projects.buildbattle.handlers.menu.registry.TimeChangeOption.TimeClickPosition.getByPosition;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class TimeChangeOption {

  public TimeChangeOption(OptionsRegistry registry) {
    registry.registerOption(new MenuOption(30, "TIME", new ItemBuilder(XMaterial.CLOCK.parseItem())
        .name(new MessageBuilder("MENU_OPTION_CONTENT_TIME_ITEM_NAME").asKey().build())
        .lore(new MessageBuilder("MENU_OPTION_CONTENT_TIME_ITEM_LORE").asKey().build())
        .build(), new MessageBuilder("MENU_OPTION_CONTENT_TIME_INVENTORY").asKey().build()) {

      @Override
      public void onClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        humanEntity.closeInventory();
        if(!(humanEntity instanceof Player))
          return;

        Player player = (Player) humanEntity;
        NormalFastInv gui = new NormalFastInv(9, new MessageBuilder("MENU_OPTION_CONTENT_TIME_INVENTORY").asKey().build());

        BaseArena arena = registry.getPlugin().getArenaRegistry().getArena(player);
        if(arena == null) {
          return;
        }
        Plot plot = arena.getPlotManager().getPlot(player);
        if(plot == null) {
          return;
        }

        //gui.addItem(new ItemBuilder(XMaterial.CLOCK.parseItem()).name(new MessageBuilder("MENU_OPTION_CONTENT_TIME_TYPE_WORLD").asKey().build()).lore(plot.getTime().name()).build());

        addClockItem(gui, plot, "MENU_OPTION_CONTENT_TIME_TYPE_WORLD");
        addClockItem(gui, plot, "MENU_OPTION_CONTENT_TIME_TYPE_DAY");
        addClockItem(gui, plot, "MENU_OPTION_CONTENT_TIME_TYPE_NOON");
        addClockItem(gui, plot, "MENU_OPTION_CONTENT_TIME_TYPE_SUNSET");
        addClockItem(gui, plot, "MENU_OPTION_CONTENT_TIME_TYPE_NIGHT");
        addClockItem(gui, plot, "MENU_OPTION_CONTENT_TIME_TYPE_MIDNIGHT");
        addClockItem(gui, plot, "MENU_OPTION_CONTENT_TIME_TYPE_SUNRISE");
        registry.getPlugin().getOptionsRegistry().addGoBackItem(gui, 8);
        gui.open(player);
      }

      private void addClockItem(NormalFastInv gui, Plot plot, String messageKey) {
        gui.addItem(new SimpleClickableItem(new ItemBuilder(XMaterial.CLOCK.parseItem()).name(new MessageBuilder(messageKey).asKey().build()).build(), event -> {
          Plot.Time time = Plot.Time.valueOf(getByPosition(event.getSlot()).toString());
          plot.setTime(time);

          for(Player p : plot.getMembers()) {
            p.setPlayerTime(Plot.Time.format(time, p.getWorld().getTime()), false);
            new MessageBuilder("MENU_OPTION_CONTENT_TIME_CHANGED").asKey().player(p).value(new MessageBuilder(messageKey).asKey().build()).sendPlayer();
          }
        }));
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
