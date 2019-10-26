/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Tigerpanzer_02, Plajer and contributors
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

package pl.plajer.buildbattle.handlers;

import java.time.LocalDateTime;

import pl.plajer.buildbattle.Main;

/**
 * @author Plajer
 * <p>
 * Created at 24.03.2019
 */
public class HolidayManager {

  private HolidayType currentHoliday = HolidayType.NONE;

  public HolidayManager(Main plugin) {
    if (!plugin.getConfig().getBoolean("Holidays-Enabled", true)) {
      return;
    }
    LocalDateTime time = LocalDateTime.now();

    int day = time.getDayOfMonth();
    int month = time.getMonthValue();

    switch (month) {
      case 2:
        if (day >= 10 && day <= 18) {
          currentHoliday = HolidayType.VALENTINES_DAY;
          //replace themes
          plugin.getConfigPreferences().getGameThemes().put("Classic", plugin.getConfig().getStringList("Holiday-Themes.Valentines-Day"));
          plugin.getConfigPreferences().getGameThemes().put("Team", plugin.getConfig().getStringList("Holiday-Themes.Valentines-Day"));
        }
        break;
      case 3:
        //4 days before april fools
        if (day >= 28) {
          currentHoliday = HolidayType.APRIL_FOOLS;
          plugin.getConfigPreferences().getGameThemes().put("Classic", plugin.getConfig().getStringList("Holiday-Themes.April-Fools"));
          plugin.getConfigPreferences().getGameThemes().put("Team", plugin.getConfig().getStringList("Holiday-Themes.April-Fools"));
        }
        break;
      case 4:
        //4 days after april fools
        if (day <= 5) {
          currentHoliday = HolidayType.APRIL_FOOLS;
          plugin.getConfigPreferences().getGameThemes().put("Classic", plugin.getConfig().getStringList("Holiday-Themes.April-Fools"));
          plugin.getConfigPreferences().getGameThemes().put("Team", plugin.getConfig().getStringList("Holiday-Themes.April-Fools"));
        }
        break;
      case 10:
        //4 days before halloween
        if (day >= 27) {
          currentHoliday = HolidayType.HALLOWEEN;
          plugin.getConfigPreferences().getGameThemes().put("Classic", plugin.getConfig().getStringList("Holiday-Themes.Halloween"));
          plugin.getConfigPreferences().getGameThemes().put("Team", plugin.getConfig().getStringList("Holiday-Themes.Halloween"));
        }
        break;
      case 11:
        //4 days after halloween
        if (day <= 4) {
          currentHoliday = HolidayType.HALLOWEEN;
          plugin.getConfigPreferences().getGameThemes().put("Classic", plugin.getConfig().getStringList("Holiday-Themes.Halloween"));
          plugin.getConfigPreferences().getGameThemes().put("Team", plugin.getConfig().getStringList("Holiday-Themes.Halloween"));
        }
        break;
      case 12:
        if (day >= 21 && day <= 29) {
          currentHoliday = HolidayType.CHRISTMAS;
          plugin.getConfigPreferences().getGameThemes().put("Classic", plugin.getConfig().getStringList("Holiday-Themes.Christmas"));
          plugin.getConfigPreferences().getGameThemes().put("Team", plugin.getConfig().getStringList("Holiday-Themes.Christmas"));
        }
      default:
        break;
    }
  }

  public HolidayType getCurrentHoliday() {
    return currentHoliday;
  }

  public enum HolidayType {
    HALLOWEEN, APRIL_FOOLS, CHRISTMAS, NONE, VALENTINES_DAY
  }

}
