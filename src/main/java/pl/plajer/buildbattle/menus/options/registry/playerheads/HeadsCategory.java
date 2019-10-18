/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Tigerpanzer_02, Plajer and contributors
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

package pl.plajer.buildbattle.menus.options.registry.playerheads;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 * Represents category of heads
 * ex. category of animal heads
 */
public class HeadsCategory {

  private String categoryID;
  private ItemStack itemStack;
  private String permission;
  private Inventory inventoryView;

  public HeadsCategory(String categoryID) {
    this.categoryID = categoryID;
  }

  public String getCategoryID() {
    return categoryID;
  }

  public ItemStack getItemStack() {
    return itemStack;
  }

  public void setItemStack(ItemStack itemStack) {
    this.itemStack = itemStack;
  }

  public String getPermission() {
    return permission;
  }

  public void setPermission(String permission) {
    this.permission = permission;
  }

  public Inventory getInventory() {
    return inventoryView;
  }

  public void setInventory(Inventory inventoryView) {
    this.inventoryView = inventoryView;
  }
}
