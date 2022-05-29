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

import org.apache.commons.lang.StringUtils;
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.managers.PluginScoreboardManager;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.scoreboard.common.EntryBuilder;
import plugily.projects.minigamesbox.classic.utils.scoreboard.type.Entry;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.base.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tigerpanzer_02
 *     <p>Created at 19.12.2021
 */
public class ScoreboardManager extends PluginScoreboardManager {

  private final PluginArena arena;
  private final List<String> cachedBaseFormat = new ArrayList<>();

  public ScoreboardManager(PluginArena arena) {
    super(arena);
    this.arena = arena;
  }

  @Override
  public List<Entry> formatScoreboard(User user) {
    EntryBuilder builder = new EntryBuilder();
    List<String> lines;
    if (user.getArena().getArenaState() == ArenaState.FULL_GAME) {
      lines =
          user.getArena()
              .getPlugin()
              .getLanguageManager()
              .getLanguageList("Scoreboard.Content.Starting");
    } else {
      lines =
          user.getArena()
              .getPlugin()
              .getLanguageManager()
              .getLanguageList(
                  "Scoreboard.Content." + user.getArena().getArenaState().getFormattedName());
    }
    for (String line : lines) {
      if (line.contains("%arena_option_reset_blocks%")
          && arena.getArenaOption("RESET_BLOCKS") == 0) {
        continue;
      }
      if (line.contains("%scoreboard_bases_list%")) {
        if (cachedBaseFormat.isEmpty()) {
          for (Base base : ((Arena) user.getArena()).getBases()) {
            builder.next(formatBase(base, user));
          }
        } else {
          for (String cached : cachedBaseFormat) {
            builder.next(cached);
          }
        }
      } else {
        builder.next(new MessageBuilder(line).player(user.getPlayer()).arena(arena).build());
      }
    }
    return builder.build();
  }

  public String formatBase(Base base, User user) {
    Arena pluginArena = (Arena) arena.getPlugin().getArenaRegistry().getArena(arena.getId());
    if (pluginArena == null) {
      return "";
    }
    String formattedLine = new MessageBuilder("SCOREBOARD_BASES_FORMAT").asKey().build();
    formattedLine = StringUtils.replace(formattedLine, "%scoreboard_base_color%", base.getColor());
    formattedLine =
        StringUtils.replace(
            formattedLine, "%scoreboard_base_color_formatted%", base.getFormattedColor());
    formattedLine =
        StringUtils.replace(
            formattedLine,
            "%scoreboard_base_players_size%",
            String.valueOf(base.getPlayers().size()));
    boolean baseYou = false;
    if (formattedLine.contains("%scoreboard_base_yourself%")) baseYou = true;
    if (base.getPlayers().contains(user.getPlayer())) {
      formattedLine =
          StringUtils.replace(
              formattedLine,
              "%scoreboard_base_yourself%",
              new MessageBuilder("SCOREBOARD_BASES_INSIDE").asKey().build());
    } else {
      formattedLine =
          StringUtils.replace(
              formattedLine,
              "%scoreboard_base_yourself%",
              new MessageBuilder("SCOREBOARD_BASES_NOT_INSIDE").asKey().build());
    }
    if (formattedLine.contains("%scoreboard_base_points_formatted%")) {
      StringBuilder points = new StringBuilder();
      String got =
          arena
              .getPlugin()
              .getLanguageManager()
              .getLanguageMessage("Scoreboard.Mode." + pluginArena.getMode().toString() + ".Got");
      String missing =
          arena
              .getPlugin()
              .getLanguageManager()
              .getLanguageMessage(
                  "Scoreboard.Mode." + pluginArena.getMode().toString() + ".Missing");
      for (int i = 0; i + 1 <= pluginArena.getArenaOption("MODE_VALUE"); i++) {
        if (i >= base.getPoints()) {
          points.append(pluginArena.getMode() == Arena.Mode.HEARTS ? got : missing);
        } else {
          points.append(pluginArena.getMode() == Arena.Mode.HEARTS ? missing : got);
        }
      }
      formattedLine =
          StringUtils.replace(
              formattedLine, "%scoreboard_base_points_formatted%", points.toString());
    }
    formattedLine = new MessageBuilder(formattedLine).arena(arena).player(user.getPlayer()).build();
    if (!baseYou) cachedBaseFormat.add(formattedLine);
    return formattedLine;
  }

  public void resetBaseCache() {
    cachedBaseFormat.clear();
  }
}
