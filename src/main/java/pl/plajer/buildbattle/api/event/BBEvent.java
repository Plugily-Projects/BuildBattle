/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and contributors
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

package pl.plajer.buildbattle.api.event;

import org.bukkit.event.Event;

import pl.plajer.buildbattle.arena.impl.BaseArena;

/**
 * Represents BuildBattle game related events.
 */
public abstract class BBEvent extends Event {

  protected BaseArena arena;

  public BBEvent(BaseArena eventArena) {
    arena = eventArena;
  }

  /**
   * Returns event arena
   * Returns null when called from BBPlayerStatisticChangeEvent when super votes are added via command
   * To get more information about arena you can check instanceof SoloArena, TeamArena and GuessTheBuildArena
   * and then cast it.
   *
   * @return event arena
   */
  public BaseArena getArena() {
    return arena;
  }
}
