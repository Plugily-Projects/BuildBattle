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

package plugily.projects.buildbattle;

import org.bukkit.Material;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.utils.Debugger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tom on 17/08/2015.
 */
public class ConfigPreferences {

  private final Main plugin;

  private final Map<String, List<String>> gameThemes = new HashMap<>();
  private final List<String> endGameCommands = new ArrayList<>(), whitelistedCommands = new ArrayList<>();
  private final List<Material> itemBlacklist = new ArrayList<>(), floorBlacklist = new ArrayList<>();
  private final Map<Option, Boolean> options = new EnumMap<>(Option.class);

  public ConfigPreferences(Main plugin) {
    this.plugin = plugin;
    loadOptions();
  }

  /**
   * Clears all options and loads them again
   */
  public void loadOptions() {
    options.clear();
    gameThemes.clear();
    endGameCommands.clear();
    whitelistedCommands.clear();

    for(Option option : Option.values()) {
      options.put(option, plugin.getConfig().getBoolean(option.getPath(), option.getDefault()));
    }
    endGameCommands.addAll(plugin.getConfig().getStringList("End-Game-Commands"));
    whitelistedCommands.addAll(plugin.getConfig().getStringList("Whitelisted-Commands"));
    loadThemes();
    loadBlackList();
  }

  private void loadThemes() {
    gameThemes.put(BaseArena.ArenaType.SOLO.getPrefix(), plugin.getConfig().getStringList("Themes.Classic"));
    gameThemes.put(BaseArena.ArenaType.TEAM.getPrefix(), plugin.getConfig().getStringList("Themes.Teams"));
    gameThemes.put(BaseArena.ArenaType.GUESS_THE_BUILD.getPrefix() + "_EASY", plugin.getConfig().getStringList("Themes.Guess-The-Build.Easy"));
    gameThemes.put(BaseArena.ArenaType.GUESS_THE_BUILD.getPrefix() + "_MEDIUM", plugin.getConfig().getStringList("Themes.Guess-The-Build.Medium"));
    gameThemes.put(BaseArena.ArenaType.GUESS_THE_BUILD.getPrefix() + "_HARD", plugin.getConfig().getStringList("Themes.Guess-The-Build.Hard"));
    Debugger.debug("Themes loaded: " + gameThemes);
  }

  public Map<String, List<String>> getGameThemes() {
    return gameThemes;
  }

  public List<String> getThemes(String accessor) {
    return Collections.unmodifiableList(gameThemes.get(accessor));
  }

  public boolean isThemeBlacklisted(String theme) {
    for(String s : plugin.getConfig().getStringList("Blacklisted-Themes")) {
      if(s.equalsIgnoreCase(theme)) {
        return true;
      }
    }
    return false;
  }

  public List<String> getWinCommands(Position pos) {
    return plugin.getConfig().getStringList("Win-Commands." + pos.getName());
  }

  public List<String> getEndGameCommands() {
    return Collections.unmodifiableList(endGameCommands);
  }

  public List<Material> getItemBlacklist() {
    return Collections.unmodifiableList(itemBlacklist);
  }

  public List<Material> getFloorBlacklist() {
    return Collections.unmodifiableList(floorBlacklist);
  }

  private void loadBlackList() {
    for(String item : plugin.getConfig().getStringList("Blacklisted-Item-Names")) {
      item = item.toUpperCase();

      if(!XMaterial.matchXMaterial(item).isPresent()) {
        Debugger.sendConsoleMsg("&c[BuildBattle] Invalid black listed item! " + item + " doesn't exist, are you sure it's properly named?");
        continue;
      }

      itemBlacklist.add(XMaterial.matchXMaterial(item).get().parseMaterial());
    }

    for(String item : plugin.getConfig().getStringList("Blacklisted-Floor-Materials")) {
      item = item.toUpperCase();

      if(!XMaterial.matchXMaterial(item).isPresent()) {
        Debugger.sendConsoleMsg("&c[BuildBattle] Invalid black listed item! " + item + " doesn't exist, are you sure it's properly named?");
        continue;
      }

      floorBlacklist.add(XMaterial.matchXMaterial(item).get().parseMaterial());
    }
  }

  public List<String> getWhitelistedCommands() {
    return Collections.unmodifiableList(whitelistedCommands);
  }

  public int getTimer(TimerType type, BaseArena arena) {
    String prefix = "Time-Manager." + arena.getArenaType().getPrefix() + ".";
    switch(type) {
      case BUILD:
        return plugin.getConfig().getInt(prefix + "Build-Time", 200);
      case LOBBY:
        return plugin.getConfig().getInt(prefix + "Lobby-Starting-Time", 60);
      case PLOT_VOTE:
        return plugin.getConfig().getInt(prefix + "Voting-Time-In-Seconds", 20);
      case THEME_VOTE:
        return plugin.getConfig().getInt(prefix + "Theme-Voting-Time-In-Seconds", 25);
      case DELAYED_TASK:
        return plugin.getConfig().getInt(prefix + "Delay-Between-Rounds-In-Seconds", 5);
      case TIME_SHORTENER:
        return plugin.getConfig().getInt(prefix + "Time-Shortener-In-Seconds", 10);
      case THEME_SELECTION:
        return plugin.getConfig().getInt(prefix + "Theme-Selection-Time-In-Seconds", 15);
      case ALL_GUESSED:
        return plugin.getConfig().getInt(prefix + "All-Guessed-In-Seconds", 5);
      default:
        return 0;
    }
  }

  /**
   * Returns whether option value is true or false
   *
   * @param option option to get value from
   * @return true or false based on user plugin.getConfig()uration
   */
  public boolean getOption(Option option) {
    return options.get(option);
  }

  public enum TimerType {
    BUILD, LOBBY, PLOT_VOTE, THEME_VOTE, DELAYED_TASK, TIME_SHORTENER, THEME_SELECTION, ALL_GUESSED
  }

  public enum Option {
    BOSSBAR_ENABLED("Boss-Bar-Enabled", true), BUNGEE_ENABLED("BungeeActivated", false), DATABASE_ENABLED("DatabaseActivated", false),
    INVENTORY_MANAGER_ENABLED("InventoryManager", true), BLOCK_COMMANDS_IN_GAME("Block-Commands-In-Game", true), REWARDS("Rewards-Enabled", false),
    HEADS_COMMAND("Command-Instead-Of-Head-Menu.Enabled", false), ENABLE_SHORT_COMMANDS("Enable-Short-Commands", false),
    DISABLE_SEPARATE_CHAT("Disable-Separate-Chat", false), DISABLE_PARTIES("Disable-Parties", true), ANNOUNCE_PLOTOWNER_LATER("Announce-PlotOwner-Later", false),
    RUN_COMMAND_ON_REPORT("Run-Command-On-Report.Enabled", false), DISABLE_SPECTATORS("Disable-Spectators", false);

    private final String path;
    private final boolean def;

    Option(String path, boolean def) {
      this.path = path;
      this.def = def;
    }

    public String getPath() {
      return path;
    }

    /**
     * @return default value of option if absent in plugin.getConfig()
     */
    public boolean getDefault() {
      return def;
    }
  }

  public enum Position {
    FIRST("First"), SECOND("Second"), THIRD("Third");

    private final String name;

    Position(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

}
