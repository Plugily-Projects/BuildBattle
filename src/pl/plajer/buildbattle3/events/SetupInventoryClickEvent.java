/*
 *  Village Defense 3 - Protect villagers from hordes of zombies
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

package pl.plajer.buildbattle3.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import pl.plajer.buildbattle3.arena.Arena;

/**
 * Created by Tom on 15/06/2015.
 */
//todo delet
public class SetupInventoryClickEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Arena arena;
    private ItemStack itemStack;
    private ClickType clickType;
    private boolean cancel = false;


    public SetupInventoryClickEvent(Arena arena, ItemStack itemStack, Player player, ClickType clickType) {
        this.player = player;
        this.arena = arena;
        this.itemStack = itemStack;
        this.clickType = clickType;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public Arena getArena() {
        return arena;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public ClickType getClickType() {
        return clickType;
    }

    public boolean isCancelled() {
        return this.cancel;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancel = cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
