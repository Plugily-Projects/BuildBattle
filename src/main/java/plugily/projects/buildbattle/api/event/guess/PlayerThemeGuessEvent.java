/*
 *
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2022 Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

package plugily.projects.buildbattle.api.event.guess;

import org.bukkit.event.HandlerList;
import plugily.projects.buildbattle.arena.GuessArena;
import plugily.projects.buildbattle.handlers.themes.GuessTheme;
import plugily.projects.minigamesbox.api.events.PlugilyEvent;


/**
 * @author Tigerpanzer_02
 * @since 5.0.0
 * <p>
 * Called when player guess the theme right
 */
public class PlayerThemeGuessEvent extends PlugilyEvent {

  private static final HandlerList HANDLERS = new HandlerList();
  private final GuessTheme theme;

  public PlayerThemeGuessEvent(GuessArena eventArena, GuessTheme theme) {
    super(eventArena);
    this.theme = theme;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public GuessTheme getTheme() {
    return theme;
  }
}
