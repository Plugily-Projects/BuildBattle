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

package plugily.projects.buildbattle.handlers.themes;

public class BBTheme {

  private final String theme;
  private final Difficulty difficulty;

  public BBTheme(String theme, Difficulty difficulty) {
    this.theme = theme == null ? "" : theme;
    this.difficulty = difficulty;
  }

  public String getTheme() {
    return theme;
  }

  public Difficulty getDifficulty() {
    return difficulty;
  }

  public enum Difficulty {
    EASY(1), MEDIUM(2), HARD(3);

    private final int pointsReward;

    Difficulty(int pointsReward) {
      this.pointsReward = pointsReward;
    }

    public int getPointsReward() {
      return pointsReward;
    }
  }

}
