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

package plugily.projects.buildbattle.api.event.game;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import plugily.projects.buildbattle.api.event.BBEvent;
import plugily.projects.buildbattle.arena.impl.BaseArena;

/**
 * Called when player joins arena
 */
public class BBGameJoinEvent extends BBEvent implements Cancellable {

  private static final HandlerList handlers = new HandlerList();
  private boolean cancelled;
  private final Player player;

  public BBGameJoinEvent(Player player, BaseArena arena) {
    super(arena);
    this.player = player;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }

  /**
   * Get player associated with this event
   *
   * @return player
   */
  public Player getPlayer() {
    return player;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

}
