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

import org.bukkit.event.HandlerList;
import plugily.projects.buildbattle.api.event.BBEvent;
import plugily.projects.buildbattle.arena.ArenaState;
import plugily.projects.buildbattle.arena.impl.BaseArena;

/**
 * Called when arena states changes
 */
public class BBGameChangeStateEvent extends BBEvent {

  private static final HandlerList handlers = new HandlerList();
  private final ArenaState gameState;
  private final ArenaState previous;

  public BBGameChangeStateEvent(ArenaState gameState, BaseArena arena, ArenaState previous) {
    super(arena);
    this.gameState = gameState;
    this.previous = previous;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public ArenaState getState() {
    return gameState;
  }

  public ArenaState getPreviousState() {
    return previous;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
