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

package pl.plajer.buildbattle.menus;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.utils.XMaterial;
import pl.plajerlair.core.utils.ItemBuilder;

/**
 * @author Plajer
 * <p>
 * Created at 30.11.2018
 */
public class GameInventories {

  private Map<InventoryType, Inventory> inventories = new HashMap<>();

  public GameInventories() {
    Inventory weatherInv = Bukkit.createInventory(null, 9, ChatManager.colorMessage("Menus.Option-Menu.Items.Weather.Inventory-Name"));
    weatherInv.addItem(new ItemBuilder(new ItemStack(Material.BUCKET)).name(ChatManager.colorMessage("Menus.Option-Menu.Items.Weather.Weather-Type.Clear")).build());
    weatherInv.addItem(new ItemBuilder(new ItemStack(Material.BUCKET)).name(ChatManager.colorMessage("Menus.Option-Menu.Items.Weather.Weather-Type.Downfall")).build());
    inventories.put(InventoryType.WEATHER, weatherInv);

    Inventory timeInv = Bukkit.createInventory(null, 9, ChatManager.colorMessage("Menus.Option-Menu.Items.Time.Inventory-Name"));
    timeInv.setItem(0, new ItemBuilder(XMaterial.CLOCK.parseItem()).name(ChatManager.colorMessage("Menus.Option-Menu.Items.Time.Time-Type.World-Time")).build());
    timeInv.setItem(1, new ItemBuilder(XMaterial.CLOCK.parseItem()).name(ChatManager.colorMessage("Menus.Option-Menu.Items.Weather.Weather-Type.Day")).build());
    timeInv.setItem(2, new ItemBuilder(XMaterial.CLOCK.parseItem()).name(ChatManager.colorMessage("Menus.Option-Menu.Items.Weather.Weather-Type.Sunset")).build());
    timeInv.setItem(3, new ItemBuilder(XMaterial.CLOCK.parseItem()).name(ChatManager.colorMessage("Menus.Option-Menu.Items.Weather.Weather-Type.Night")).build());
    timeInv.setItem(4, new ItemBuilder(XMaterial.CLOCK.parseItem()).name(ChatManager.colorMessage("Menus.Option-Menu.Items.Weather.Weather-Type.Sunrise")).build());
    inventories.put(InventoryType.TIME, timeInv);
  }

  /**
   * Opens inventory with target type to target player
   *
   * @param type   type of inventory to open ex. inventory with all time values
   * @param player player to open inventory to
   */
  public void openInventory(InventoryType type, Player player) {
    player.openInventory(inventories.get(type));
  }

  public enum InventoryType {
    WEATHER, TIME
  }

  //todo maybe replace with ArenaPlot.Time
  public enum TimeClickPosition {
    WORLD_TIME(0), DAY(1), SUNSET(2), NIGHT(3), SUNRISE(4);

    private int position;

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
      for (TimeClickPosition position : values()) {
        if (position.getPosition() == pos) {
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
