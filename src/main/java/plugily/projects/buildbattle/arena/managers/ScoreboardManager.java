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

package plugily.projects.buildbattle.arena.managers;

import org.bukkit.entity.Player;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.GuessArena;
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.managers.PluginScoreboardManager;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.scoreboard.common.EntryBuilder;
import plugily.projects.minigamesbox.classic.utils.scoreboard.type.Entry;

import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>Created at 19.12.2021
 */
public class ScoreboardManager extends PluginScoreboardManager {

  private final PluginArena arena;

  public ScoreboardManager(PluginArena arena) {
    super(arena);
    this.arena = arena;
  }

  @Override
  public List<Entry> formatScoreboard(User user) {
    EntryBuilder builder = new EntryBuilder();
    List<String> lines;
    PluginArena userArena = user.getArena();

    if(userArena.getArenaState() == ArenaState.FULL_GAME) {
      lines = userArena.getPlugin().getLanguageManager().getLanguageList("Scoreboard.Content.Starting");
    } else if(userArena.getArenaState() == ArenaState.IN_GAME) {
      if(userArena instanceof GuessArena) {
        lines = userArena.getPlugin().getLanguageManager().getLanguageList("Scoreboard.Content." + userArena.getArenaState().getFormattedName() + ".Guess-The-Build" + (((GuessArena) userArena).getArenaInGameState() == BaseArena.ArenaInGameState.PLOT_VOTING ? "-Waiting" : ""));
      } else {
        if(userArena.getArenaOption("PLOT_MEMBER_SIZE") <= 1) {
          lines = userArena.getPlugin().getLanguageManager().getLanguageList("Scoreboard.Content." + userArena.getArenaState().getFormattedName() + ".Classic");
        } else {
          lines = userArena.getPlugin().getLanguageManager().getLanguageList("Scoreboard.Content." + userArena.getArenaState().getFormattedName() + ".Teams");
        }
      }
    } else if(userArena.getArenaState() == ArenaState.ENDING) {
      if(userArena instanceof GuessArena) {
        lines = userArena.getPlugin().getLanguageManager().getLanguageList("Scoreboard.Content." + userArena.getArenaState().getFormattedName() + ".Guess-The-Build");
      } else {
        lines = userArena.getPlugin().getLanguageManager().getLanguageList("Scoreboard.Content." + userArena.getArenaState().getFormattedName() + ".Classic");
      }
    } else {
      lines = userArena.getPlugin().getLanguageManager().getLanguageList("Scoreboard.Content." + userArena.getArenaState().getFormattedName());
    }

    Player player = user.getPlayer();

    for(String line : lines) {
      builder.next(new MessageBuilder(line).player(player).arena(arena).build());
    }
    return builder.build();
  }

}
