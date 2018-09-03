/*
 * BuildBattle 3 - Ultimate building competition minigame
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
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

package pl.plajer.buildbattle3.entities;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import pl.plajer.buildbattle3.handlers.ChatManager;

/**
 * Created by Tom on 8/01/2016.
 */
public class BuildBattleEntity {

  private LivingEntity entity;

  public BuildBattleEntity(LivingEntity entity) {
    this.entity = entity;
  }

  private Boolean isAdult() {
    switch (entity.getType()) {
      case COW:
        return ((Cow) entity).isAdult();
      case SHEEP:
        return ((Sheep) entity).isAdult();
      case PIG:
        return ((Pig) entity).isAdult();
      case WOLF:
        return ((Wolf) entity).isAdult();
      case ZOMBIE:
        return !((Zombie) entity).isBaby();
      case PIG_ZOMBIE:
        return !((PigZombie) entity).isBaby();
      case CHICKEN:
        return ((Chicken) entity).isAdult();
      case HORSE:
        return ((Horse) entity).isAdult();
      case MUSHROOM_COW:
        return ((MushroomCow) entity).isAdult();
      case VILLAGER:
        return ((Villager) entity).isAdult();
      default:
        return null;
    }
  }

  private Boolean isSaddled() {
    return entity.getType() == EntityType.HORSE && ((Horse) entity).getInventory().getSaddle() == null;
  }

  private boolean isMoveable() {
    return entity.hasAI();
  }

  public void toggleMoveable() {
    entity.setAI(!isMoveable());
  }

  public void switchSaddle() {
    if (entity.getType() == EntityType.HORSE) {
      Horse horse = (Horse) entity;
      if (!isSaddled()) {
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
      } else {
        horse.getInventory().setSaddle(new ItemStack(Material.AIR));
      }
    }
  }

  public void switchAge() {
    if (((Ageable) entity).isAdult()) {
      ((Ageable) entity).setBaby();
    } else {
      ((Ageable) entity).setAdult();
    }
  }

  public void remove() {
    entity.remove();
  }

  public Entity getEntity() {
    return entity;
  }

  public Inventory getMenu() {
    switch (entity.getType()) {
      case COW:
      case WOLF:
      case SHEEP:
      case ZOMBIE:
      case PIG_ZOMBIE:
      case CHICKEN:
        return new InventoryBuilder().setSlots(9).addItem(this.isAdult() ? EntityItemManager.getEntityItem("Adult") : EntityItemManager.getEntityItem("Baby")).addItem(this.isMoveable() ? EntityItemManager.getEntityItem("Move-On") : EntityItemManager.getEntityItem("Move-Off")).addItem(EntityItemManager.getEntityItem("Look-At-Me")).addItem(EntityItemManager.getEntityItem("Close")).addItem(EntityItemManager.getEntityItem("Despawn")).build();
      case SILVERFISH:
      case ENDERMAN:
      case SPIDER:
      case CAVE_SPIDER:
      case SQUID:
        return new InventoryBuilder().setSlots(9).addItem(this.isMoveable() ? EntityItemManager.getEntityItem("Move-On") : EntityItemManager.getEntityItem("Move-Off")).addItem(EntityItemManager.getEntityItem("Look-At-Me")).addItem(EntityItemManager.getEntityItem("Close")).addItem(EntityItemManager.getEntityItem("Despawn")).build();
      case HORSE:
        return new InventoryBuilder().setSlots(9).addItem(this.isMoveable() ? EntityItemManager.getEntityItem("Move-On") : EntityItemManager.getEntityItem("Move-Off")).addItem(EntityItemManager.getEntityItem("Look-At-Me")).addItem(this.isAdult() ? EntityItemManager.getEntityItem("Adult") : EntityItemManager.getEntityItem("Baby")).addItem(this.isSaddled() ? EntityItemManager.getEntityItem("Saddle-Off") : EntityItemManager.getEntityItem("Saddle-On")).addItem(EntityItemManager.getEntityItem("Close")).addItem(EntityItemManager.getEntityItem("Despawn")).build();
      case VILLAGER:
        return new InventoryBuilder().setSlots(9).addItem(this.isAdult() ? EntityItemManager.getEntityItem("Adult") : EntityItemManager.getEntityItem("Baby")).addItem(this.isMoveable() ? EntityItemManager.getEntityItem("Move-On") : EntityItemManager.getEntityItem("Move-Off")).addItem(EntityItemManager.getEntityItem("Look-At-Me")).addItem(EntityItemManager.getEntityItem("Close")).addItem(EntityItemManager.getEntityItem("Despawn")).addItem(EntityItemManager.getEntityItem("Profession-Villager-Selecting")).build();
      default:
        return new InventoryBuilder().setSlots(9).addItem(this.isMoveable() ? EntityItemManager.getEntityItem("Move-On") : EntityItemManager.getEntityItem("Move-Off")).addItem(EntityItemManager.getEntityItem("Look-At-Me")).addItem(EntityItemManager.getEntityItem("Close")).addItem(EntityItemManager.getEntityItem("Despawn")).build();

    }


  }

  public void setLook(Location location) {
    entity.teleport(new Location(entity.getLocation().getWorld(), entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(), location.getYaw(), -location.getPitch()));
    entity.setAI(false);
  }

  public class InventoryBuilder {

    private int slots = 9;
    private String name = ChatManager.colorMessage("Menus.Entity-Menu-Name");
    private HashSet<EntityItem> items = new HashSet<>();

    private InventoryBuilder() {
    }

    InventoryBuilder setSlots(int slot) {
      this.slots = slot;
      return this;
    }

    InventoryBuilder addItem(EntityItem entityItem) {
      items.add(entityItem);
      return this;
    }

    Inventory build() {
      Inventory inventory = Bukkit.getServer().createInventory(null, slots, name);
      for (EntityItem entityItem : items) {
        inventory.setItem(entityItem.getSlot(), entityItem.getItemStack());

      }
      return inventory;
    }
  }
}


