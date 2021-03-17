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

package plugily.projects.buildbattle.handlers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import plugily.projects.buildbattle.api.StatsStorage;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.impl.BaseArena;

/**
 * @author Plajer
 * <p>
 * Created at 06.05.2018
 */
public class PlaceholderManager extends PlaceholderExpansion {

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public String getIdentifier() {
    return "buildbattle";
  }

  @Override
  public String getAuthor() {
    return "Plugily Projects";
  }

  @Override
  public String getVersion() {
    return "1.0.1";
  }

  @Override
  public String onPlaceholderRequest(Player player, String id) {
    if(player == null) {
      return null;
    }
    switch(id.toLowerCase()) {
      case "blocks_broken":
        return Integer.toString(StatsStorage.getUserStats(player, StatsStorage.StatisticType.BLOCKS_BROKEN));
      case "blocks_placed":
        return Integer.toString(StatsStorage.getUserStats(player, StatsStorage.StatisticType.BLOCKS_PLACED));
      case "games_played":
        return Integer.toString(StatsStorage.getUserStats(player, StatsStorage.StatisticType.GAMES_PLAYED));
      case "wins":
        return Integer.toString(StatsStorage.getUserStats(player, StatsStorage.StatisticType.WINS));
      case "loses":
        return Integer.toString(StatsStorage.getUserStats(player, StatsStorage.StatisticType.LOSES));
      case "highest_win":
        return Integer.toString(StatsStorage.getUserStats(player, StatsStorage.StatisticType.HIGHEST_WIN));
      case "particles_used":
        return Integer.toString(StatsStorage.getUserStats(player, StatsStorage.StatisticType.PARTICLES_USED));
      case "super_votes":
        return Integer.toString(StatsStorage.getUserStats(player, StatsStorage.StatisticType.SUPER_VOTES));
      default:
        return handleArenaPlaceholderRequest(id);
    }
  }

  private String handleArenaPlaceholderRequest(String id) {
    if(!id.contains(":")) {
      return null;
    }
    String[] data = id.split(":");
    BaseArena arena = ArenaRegistry.getArena(data[0]);
    if(arena == null) {
      return null;
    }
    switch(data[1].toLowerCase()) {
      case "players":
        return Integer.toString(arena.getPlayers().size());
      case "max_players":
        return Integer.toString(arena.getMaximumPlayers());
      case "state":
        return arena.getArenaState().toString().toLowerCase();
      case "state_pretty":
        return arena.getArenaState().getPlaceholder();
      case "mapname":
        return arena.getMapName();
      case "arenatype":
        return arena.getArenaType().toString().toLowerCase();
      case "arenatype_pretty":
        return arena.getArenaType().getPrefix();
      default:
        return null;
    }
  }

}
