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

package plugily.projects.buildbattle.api.event.plot;

import org.bukkit.event.HandlerList;
import plugily.projects.buildbattle.api.event.BBEvent;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;

/**
 * @author Plajer
 * @since 4.0.0-pre-12
 * <p>
 * Called when plot is being reset
 */
public class BBPlotResetEvent extends BBEvent {

  private static final HandlerList HANDLERS = new HandlerList();
  private final Plot plot;

  public BBPlotResetEvent(BaseArena eventArena, Plot plot) {
    super(eventArena);
    this.plot = plot;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public Plot getPlot() {
    return plot;
  }

}
