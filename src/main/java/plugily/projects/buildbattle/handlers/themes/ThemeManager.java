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

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.minigamesbox.classic.handlers.holiday.Holiday;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 19.06.2022
 */
public class ThemeManager {

  private final Main plugin;
  private final FileConfiguration themesConfig;

  private final Map<GameThemes, List<String>> gameThemes = new HashMap<>();
  private final List<String> blacklistedNames;

  public final List<String> classicThemes, teamsThemes, GTBThemesEasy, GTBThemesMedium, GTBThemesHard;

  public ThemeManager(Main plugin) {
    this.plugin = plugin;

    themesConfig = ConfigUtils.getConfig(plugin, "themes");
    blacklistedNames = themesConfig.getStringList("Blacklisted");

    classicThemes = themesConfig.getStringList("Themes.Classic");
    teamsThemes = themesConfig.getStringList("Themes.Teams");
    GTBThemesEasy = themesConfig.getStringList("Themes.Guess-The-Build.Easy");
    GTBThemesMedium = themesConfig.getStringList("Themes.Guess-The-Build.Medium");
    GTBThemesHard = themesConfig.getStringList("Themes.Guess-The-Build.Hard");

    loadThemes(false);

    plugin.getDebugger().debug("Themes loaded: " + gameThemes);
  }

  public void saveThemesToConfig() {
    themesConfig.set("Themes.Classic", classicThemes);
    themesConfig.set("Themes.Teams", teamsThemes);
    themesConfig.set("Themes.Guess-The-Build.Easy", GTBThemesEasy);
    themesConfig.set("Themes.Guess-The-Build.Medium", GTBThemesMedium);
    themesConfig.set("Themes.Guess-The-Build.Hard", GTBThemesHard);

    ConfigUtils.saveConfig(plugin, themesConfig, "themes");
  }

  public void loadThemes(boolean ignoreHolidays) {
    // Remove all colours from theme names
    classicThemes.replaceAll(ChatColor::stripColor);
    teamsThemes.replaceAll(ChatColor::stripColor);
    GTBThemesEasy.replaceAll(ChatColor::stripColor);
    GTBThemesMedium.replaceAll(ChatColor::stripColor);
    GTBThemesHard.replaceAll(ChatColor::stripColor);

    if(!ignoreHolidays && !plugin.getHolidayManager().getEnabledHolidays().isEmpty()) {
      loadSpecialThemes();
      return;
    }

    gameThemes.put(GameThemes.SOLO, Collections.unmodifiableList(classicThemes));
    gameThemes.put(GameThemes.TEAM, Collections.unmodifiableList(teamsThemes));
    gameThemes.put(GameThemes.GUESS_THE_BUILD_EASY, Collections.unmodifiableList(GTBThemesEasy));
    gameThemes.put(GameThemes.GUESS_THE_BUILD_MEDIUM, Collections.unmodifiableList(GTBThemesMedium));
    gameThemes.put(GameThemes.GUESS_THE_BUILD_HARD, Collections.unmodifiableList(GTBThemesHard));
  }

  private void loadSpecialThemes() {
    for(Holiday holiday : plugin.getHolidayManager().getEnabledHolidays()) {
      List<String> themeList = themesConfig.getStringList("Holiday." + holiday.getName());

      if(themeList.isEmpty()) {
        continue;
      }

      Optional.ofNullable(gameThemes.get(GameThemes.CLASSIC)).ifPresent(themeList::addAll);

      gameThemes.put(GameThemes.CLASSIC, Collections.unmodifiableList(themeList));
      gameThemes.put(GameThemes.TEAMS, Collections.unmodifiableList(themeList));
      gameThemes.put(GameThemes.GUESS_THE_BUILD_EASY, Collections.unmodifiableList(themeList));
      gameThemes.put(GameThemes.GUESS_THE_BUILD_MEDIUM, Collections.unmodifiableList(themeList));
      gameThemes.put(GameThemes.GUESS_THE_BUILD_HARD, Collections.unmodifiableList(themeList));
    }
  }

  public List<String> getThemes(GameThemes type) {
    return type == null ? new ArrayList<>() : gameThemes.getOrDefault(type, new ArrayList<>());
  }

  public boolean isThemeBlacklisted(String theme) {
    for(String s : blacklistedNames) {
      if(s.equalsIgnoreCase(theme)) {
        return true;
      }
    }
    return false;
  }

  public enum GameThemes {

    SOLO("Classic"), TEAM("Teams"),
    GUESS_THE_BUILD_EASY("Guess-The-Build_EASY"), GUESS_THE_BUILD_MEDIUM("Guess-The-Build_MEDIUM"), GUESS_THE_BUILD_HARD("Guess-The-Build_HARD"),
    CLASSIC, TEAMS;

    public final String strip;

    public static final GameThemes[] VALUES = GameThemes.values();

    GameThemes() {
      strip = name();
    }

    GameThemes(String name) {
      strip = name.replace("-", "").replace("_", "");
    }

    @Nullable
    public static GameThemes getByArenaType(BaseArena.ArenaType arenaType) {
      switch(arenaType) {
      case SOLO:
        return SOLO;
      case TEAM:
        return TEAM;
      default:
        return null;
      }
    }

    @Nullable
    public static GameThemes getByDifficulty(GuessTheme.Difficulty difficulty) {
      switch(difficulty) {
      case EASY:
        return GUESS_THE_BUILD_EASY;
      case MEDIUM:
        return GUESS_THE_BUILD_MEDIUM;
      case HARD:
        return GUESS_THE_BUILD_HARD;
      default:
        return null;
      }
    }
  }
}
