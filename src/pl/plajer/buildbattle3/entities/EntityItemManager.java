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

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by Tom on 31/01/2016.
 */
class EntityItemManager {

    private static HashMap<String, EntityItem> entityItems = new HashMap<>();

    public static void addEntityItem(String name, EntityItem entityItem) {
        entityItems.put(name, entityItem);
    }

    public static EntityItem getEntityItem(String name) {
        if(entityItems.containsKey(name)) return entityItems.get(name);
        return null;
    }

    public static String getRelatedEntityItemName(ItemStack itemStack) {
        for(String key : entityItems.keySet()) {
            EntityItem entityItem = entityItems.get(key);
            if(entityItem.getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(itemStack.getItemMeta().getDisplayName()) && entityItem.getMaterial().equals(itemStack.getType())) {
                return key;
            }
        }
        return null;
    }

}
