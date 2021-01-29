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

package plugily.projects.buildbattle.arena.managers;

import me.clip.placeholderapi.PlaceholderAPI;
import me.tigerhix.lib.scoreboard.common.EntryBuilder;
import me.tigerhix.lib.scoreboard.type.Entry;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import plugily.projects.buildbattle.arena.ArenaState;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.arena.impl.GuessTheBuildArena;
import plugily.projects.buildbattle.handlers.language.LanguageManager;
import plugily.projects.buildbattle.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 */
public class GuessTheBuildScoreboardManager extends ScoreboardManager {

  private final Map<String, List<String>> scoreboardContents = new HashMap<>();
  private final GuessTheBuildArena arena;

  public GuessTheBuildScoreboardManager(BaseArena arena) {
    super(arena);
    this.arena = (GuessTheBuildArena) arena;
    for(ArenaState state : ArenaState.values()) {
      //not registering RESTARTING state and registering IN_GAME and ENDING later
      if(state == ArenaState.RESTARTING || state == ArenaState.IN_GAME || state == ArenaState.ENDING) {
        continue;
      }
      //todo migrator
      List<String> lines = LanguageManager.getLanguageList("Scoreboard.Content." + state.getFormattedName());
      scoreboardContents.put(state.getFormattedName(), lines);
    }
    List<String> playing = LanguageManager.getLanguageList("Scoreboard.Content.Playing-States." + BaseArena.ArenaType.GUESS_THE_BUILD.getPrefix());
    List<String> ending = LanguageManager.getLanguageList("Scoreboard.Content.Ending-States." + BaseArena.ArenaType.GUESS_THE_BUILD.getPrefix());
    //todo locale
    scoreboardContents.put(ArenaState.IN_GAME.getFormattedName() + "_" + BaseArena.ArenaType.GUESS_THE_BUILD.getPrefix(), playing);
    scoreboardContents.put(ArenaState.ENDING.getFormattedName() + "_" + BaseArena.ArenaType.GUESS_THE_BUILD.getPrefix(), ending);
  }

  //todo maybe not needed and private
  @Override
  public List<Entry> formatScoreboard(User user) {
    EntryBuilder builder = new EntryBuilder();
    List<String> lines = scoreboardContents.get(arena.getArenaState().getFormattedName());
    if(arena.getArenaState() == ArenaState.IN_GAME || arena.getArenaState() == ArenaState.ENDING) {
      lines = scoreboardContents.get(arena.getArenaState().getFormattedName() + "_" + BaseArena.ArenaType.GUESS_THE_BUILD.getPrefix());
    }
    for(String line : lines) {
      builder.next(formatScoreboardLine(line, user));
    }
    return builder.build();
  }

  @Override
  public String formatScoreboardLine(String string, User user) {
    Player player = user.getPlayer();
    String returnString = string;
    returnString = StringUtils.replace(returnString, "%PLAYERS%", Integer.toString(arena.getPlayers().size()));
    returnString = StringUtils.replace(returnString, "%PLAYER%", player.getName());
    if(arena.isThemeSet()) {
      returnString = StringUtils.replace(returnString, "%CURRENT_TIMER%", getPlugin().getChatManager().colorMessage("Scoreboard.GTB-Current-Timer.Build-Time"));
    } else {
      returnString = StringUtils.replace(returnString, "%CURRENT_TIMER%", getPlugin().getChatManager().colorMessage("Scoreboard.GTB-Current-Timer.Starts-In"));
    }
    if(arena.getCurrentBuilder() != null) {
      if((arena.getCurrentBuilder().equals(player) && arena.getCurrentTheme() != null) || arena.getWhoGuessed().contains(player)) {
        returnString = StringUtils.replace(returnString, "%THEME%", arena.getCurrentTheme().getTheme());
      } else {
        returnString = StringUtils.replace(returnString, "%THEME%", getPlugin().getChatManager().colorMessage("Scoreboard.Theme-Unknown"));
      }
      returnString = StringUtils.replace(returnString, "%BUILDER%", arena.getCurrentBuilder().getName());
    } else {
      returnString = StringUtils.replace(returnString, "%THEME%", getPlugin().getChatManager().colorMessage("Scoreboard.Theme-Unknown"));
      returnString = StringUtils.replace(returnString, "%BUILDER%", getPlugin().getChatManager().colorMessage("Scoreboard.Theme-Unknown"));
    }
    if(arena.getArenaState() == ArenaState.IN_GAME || arena.getArenaState() == ArenaState.ENDING) {
      int max = arena.getArenaState() == ArenaState.IN_GAME ? 3 : 10;
      List<Map.Entry<Player, Integer>> list = new ArrayList<>(arena.getPlayersPoints().entrySet());
      for(int i = 0; i <= max; i++) {
        if(list.size() - 1 < i) {
          returnString = StringUtils.replace(returnString, "%" + (i + 1) + "%", "None");
          returnString = StringUtils.replace(returnString, "%" + (i + 1) + "_PTS%", "0");
          continue;
        }
        Map.Entry<Player, Integer> entry = list.get(i);
        returnString = StringUtils.replace(returnString, "%" + (i + 1) + "%", entry.getKey().getName());
        returnString = StringUtils.replace(returnString, "%" + (i + 1) + "_PTS%", String.valueOf(entry.getValue()));
      }
    }
    returnString = replaceValues(returnString);
    if(getPlugin().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      returnString = PlaceholderAPI.setPlaceholders(player, returnString);
    }
    returnString = getPlugin().getChatManager().colorRawMessage(returnString);
    return returnString;
  }

}
