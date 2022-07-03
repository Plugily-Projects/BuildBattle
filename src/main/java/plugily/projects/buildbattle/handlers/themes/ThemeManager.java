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

package plugily.projects.buildbattle.handlers.themes;

import org.bukkit.configuration.file.FileConfiguration;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.minigamesbox.classic.handlers.holiday.Holiday;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 19.06.2022
 */
public class ThemeManager {

  private final Main plugin;
  private final FileConfiguration themes;
  private final Map<String, List<String>> gameThemes = new HashMap<>();

  public ThemeManager(Main plugin) {
    this.plugin = plugin;
    themes = ConfigUtils.getConfig(plugin, "arenas");
    loadThemes();
    plugin.getDebugger().debug("Themes loaded: " + gameThemes);
  }

  private void loadThemes() {
    if(!plugin.getHolidayManager().getEnabledHolidays().isEmpty()) {
      loadSpecialThemes();
      return;
    }
    gameThemes.put(BaseArena.ArenaType.SOLO.getPrefix(), themes.getStringList("Themes.Classic"));
    gameThemes.put(BaseArena.ArenaType.TEAM.getPrefix(), themes.getStringList("Themes.Teams"));
    gameThemes.put(BaseArena.ArenaType.GUESS_THE_BUILD.getPrefix() + "_EASY", themes.getStringList("Themes.Guess-The-Build.Easy"));
    gameThemes.put(BaseArena.ArenaType.GUESS_THE_BUILD.getPrefix() + "_MEDIUM", themes.getStringList("Themes.Guess-The-Build.Medium"));
    gameThemes.put(BaseArena.ArenaType.GUESS_THE_BUILD.getPrefix() + "_HARD", themes.getStringList("Themes.Guess-The-Build.Hard"));
  }

  private void loadSpecialThemes() {
    for(Holiday holiday : plugin.getHolidayManager().getEnabledHolidays()) {
      List<String> themeList = themes.getStringList("Holiday." + holiday.getName());
      if(themeList.isEmpty()) {
        continue;
      }
      themeList.addAll(gameThemes.getOrDefault("Classic", new ArrayList<>()));
      gameThemes.put("Classic", themeList);
      gameThemes.put("Teams", themeList);
      gameThemes.put("Guess-The-Build", themeList);
    }
  }

  public Map<String, List<String>> getGameThemes() {
    return gameThemes;
  }

  public List<String> getThemes(String accessor) {
    return Collections.unmodifiableList(gameThemes.getOrDefault(accessor, new ArrayList<>()));
  }

  public boolean isThemeBlacklisted(String theme) {
    for(String s : themes.getStringList("Blacklisted")) {
      if(s.equalsIgnoreCase(theme)) {
        return true;
      }
    }
    return false;
  }

}
