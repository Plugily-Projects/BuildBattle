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

/**
 * Created by Tom on 15/08/2014.
 */
//todo delet
public class PlayerAddCommandEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private boolean cancel = false;
    private String[] argument;
    private String ID;

    public PlayerAddCommandEvent(Player player, String[] arguments, String ID) {
        this.player = player;
        this.argument = arguments;
        this.ID = ID;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String[] getArguments() {
        return argument;
    }

    public String getArenaID() {
        return ID;
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
