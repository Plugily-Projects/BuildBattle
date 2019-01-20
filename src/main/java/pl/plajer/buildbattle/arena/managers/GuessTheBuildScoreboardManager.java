/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.buildbattle.arena.managers;

import java.util.List;

import me.clip.placeholderapi.PlaceholderAPI;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.plajer.buildbattle.arena.ArenaState;
import pl.plajer.buildbattle.arena.impl.BaseArena;
import pl.plajer.buildbattle.arena.impl.GuessTheBuildArena;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.handlers.language.LanguageManager;
import pl.plajerlair.core.utils.GameScoreboard;
import pl.plajerlair.core.utils.MinigameUtils;

/**
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 */
public class GuessTheBuildScoreboardManager extends ScoreboardManager {

  private GuessTheBuildArena arena;

  public GuessTheBuildScoreboardManager(BaseArena arena) {
    super(arena);
    this.arena = (GuessTheBuildArena) arena;
    for (ArenaState state : ArenaState.values()) {
      //not registering RESTARTING state and registering IN_GAME and ENDING later
      if (state == ArenaState.RESTARTING || state == ArenaState.IN_GAME || state == ArenaState.ENDING) {
        continue;
      }
      //todo migrator
      List<String> lines = LanguageManager.getLanguageList("Scoreboard.Content." + state.getFormattedName());
      getScoreboardContents().put(state.getFormattedName(), lines);
    }
    for (BaseArena.ArenaType type : BaseArena.ArenaType.values()) {
      List<String> playing = LanguageManager.getLanguageList("Scoreboard.Content.Playing-States." + type.getPrefix());
      List<String> ending = LanguageManager.getLanguageList("Scoreboard.Content.Ending-States." + type.getPrefix());
      //todo locale
      getScoreboardContents().put(ArenaState.IN_GAME.getFormattedName() + "_" + type.getPrefix(), playing);
      getScoreboardContents().put(ArenaState.ENDING.getFormattedName() + "_" + type.getPrefix(), ending);
    }
  }

  /**
   * Updates scoreboard to all players in arena
   */
  public void updateScoreboard() {
    if (arena.getPlayers().size() == 0 || arena.getArenaState() == ArenaState.RESTARTING) {
      return;
    }
    GameScoreboard scoreboard;
    for (Player p : arena.getPlayers()) {
      if (p == null) {
        continue;
      }
      scoreboard = new GameScoreboard("PL_BB", "BB_CR", getBoardTitle());
      List<String> lines = getScoreboardContents().get(arena.getArenaState().getFormattedName());
      if (arena.getArenaState() == ArenaState.IN_GAME || arena.getArenaState() == ArenaState.ENDING) {
        lines = getScoreboardContents().get(arena.getArenaState().getFormattedName() + "_" + arena.getArenaType().getPrefix());
      }
      for (String line : lines) {
        scoreboard.addRow(formatScoreboardLine(line, p));
      }
      scoreboard.finish();
      scoreboard.display(p);
    }
  }

  @Deprecated
  private String formatScoreboardLine(String string, Player player) {
    String returnString = string;
    returnString = StringUtils.replace(returnString, "%PLAYERS%", Integer.toString(arena.getPlayers().size()));
    returnString = StringUtils.replace(returnString, "%PLAYER%", player.getName());
    if (arena.getArenaType() != BaseArena.ArenaType.GUESS_THE_BUILD) {
      if (arena.isThemeVoteTime()) {
        returnString = StringUtils.replace(returnString, "%THEME%", ChatManager.colorMessage("In-Game.No-Theme-Yet"));
      } else {
        returnString = StringUtils.replace(returnString, "%THEME%", arena.getTheme());
      }
    } else {
      if (arena.isThemeSet()) {
        returnString = StringUtils.replace(returnString, "%CURRENT_TIMER%", ChatManager.colorMessage("Scoreboard.GTB-Current-Timer.Build-Time"));
      } else {
        returnString = StringUtils.replace(returnString, "%CURRENT_TIMER%", ChatManager.colorMessage("Scoreboard.GTB-Current-Timer.Starts-In"));
      }
      if (arena.getCurrentBuilder().equals(player)) {
        returnString = StringUtils.replace(returnString, "%THEME%", arena.getCurrentGTBTheme().getTheme());
      } else {
        returnString = StringUtils.replace(returnString, "%THEME%", ChatManager.colorMessage("Scoreboard.Theme-Unknown"));
      }
      returnString = StringUtils.replace(returnString, "%BUILDER%", arena.getCurrentBuilder().getName());
    }
    //todo ineffective
    try {
      for (int i = 1; i < 11; i++) {
        if (arena.getArenaState() != ArenaState.ENDING && i > 3) {
          break;
        }
        //todo may be errors?
        returnString = StringUtils.replace(returnString, "%" + i + "%",
            ((Player) arena.getPlayersPoints().keySet().toArray()[i]).getName());
        returnString = StringUtils.replace(returnString, "%" + i + "_PTS%",
            String.valueOf(arena.getPlayersPoints().get(arena.getPlayersPoints().keySet().toArray()[i])));
      }
    } catch (Exception ex) {
      Bukkit.broadcastMessage("Exception was called");
    }
    returnString = StringUtils.replace(returnString, "%MIN_PLAYERS%", Integer.toString(arena.getMinimumPlayers()));
    returnString = StringUtils.replace(returnString, "%MAX_PLAYERS%", Integer.toString(arena.getMaximumPlayers()));
    returnString = StringUtils.replace(returnString, "%TIMER%", Integer.toString(arena.getTimer()));
    //todo its the same
    returnString = StringUtils.replace(returnString, "%TIME_LEFT%", Long.toString(arena.getTimer()));
    returnString = StringUtils.replace(returnString, "%FORMATTED_TIME_LEFT%", MinigameUtils.formatIntoMMSS(arena.getTimer()));
    returnString = StringUtils.replace(returnString, "%ARENA_ID%", arena.getID());
    returnString = StringUtils.replace(returnString, "%MAPNAME%", arena.getMapName());
    if (!arena.isThemeVoteTime()) {
      if (arena.getArenaType() == BaseArena.ArenaType.TEAM && arena.getPlotManager().getPlot(player) != null) {
        if (arena.getPlotManager().getPlot(player).getOwners().size() == 2) {
          if (arena.getPlotManager().getPlot(player).getOwners().get(0).equals(player)) {
            returnString = StringUtils.replace(returnString, "%TEAMMATE%", arena.getPlotManager().getPlot(player).getOwners().get(1).getName());
          } else {
            returnString = StringUtils.replace(returnString, "%TEAMMATE%", arena.getPlotManager().getPlot(player).getOwners().get(0).getName());
          }
        } else {
          returnString = StringUtils.replace(returnString, "%TEAMMATE%", ChatManager.colorMessage("In-Game.Nobody"));
        }
      }
    } else {
      returnString = StringUtils.replace(returnString, "%TEAMMATE%", ChatManager.colorMessage("In-Game.Nobody"));
    }
    if (getPlugin().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      PlaceholderAPI.setPlaceholders(player, returnString);
    }
    returnString = ChatManager.colorRawMessage(returnString);
    return returnString;
  }

}
