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

package pl.plajer.buildbattle4.menus.themevoter;

/**
 * @author Plajer
 * <p>
 * Created at 15.09.2018
 */
public class GTBTheme {

  private String theme;
  private Difficulty difficulty;

  public GTBTheme(String theme, Difficulty difficulty) {
    this.theme = theme;
    this.difficulty = difficulty;
  }

  public String getTheme() {
    return theme;
  }

  public Difficulty getDifficulty() {
    return difficulty;
  }

  public enum Difficulty {
    EASY, MEDIUM, HARD
  }

}
