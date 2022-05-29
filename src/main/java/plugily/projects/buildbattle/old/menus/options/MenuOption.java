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

package plugily.projects.buildbattle.old.menus.options;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class MenuOption {

  private int slot;
  private final String id;
  private final ItemStack itemStack;
  private String inventoryName;

  public MenuOption(int slot, String id, ItemStack itemStack) {
    this.slot = slot;
    this.id = id;
    this.itemStack = itemStack;
  }

  public MenuOption(int slot, String id, ItemStack itemStack, String inventoryName) {
    this.slot = slot;
    this.id = id;
    this.itemStack = itemStack;
    this.inventoryName = inventoryName;
  }

  public int getSlot() {
    return slot;
  }

  public void setSlot(int slot) {
    this.slot = slot;
  }

  public String getID() {
    return id;
  }

  public ItemStack getItemStack() {
    return itemStack;
  }

  public String getInventoryName() {
    return inventoryName;
  }

  /**
   * Called when item is clicked within Options Menu inventory
   *
   * @param e passed InventoryClickEvent from Options Menu
   */
  public void onClick(InventoryClickEvent e) {
  }

  /**
   * Called when anything is clicked in inventory registered in onClick method
   * Method won't be called if inventory is not enabled.
   * Method won't be called also if e.getCurrentItem() is not named or null
   *
   * @param e passed InventoryClickEvent when anything is clicked within target MenuOption
   * @see #onClick(InventoryClickEvent)
   */
  public void onTargetClick(InventoryClickEvent e) {
  }
}
