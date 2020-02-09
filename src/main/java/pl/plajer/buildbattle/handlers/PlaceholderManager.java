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

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import org.bukkit.entity.Player;

import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.impl.BaseArena;

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

  public String getIdentifier() {
    return "buildbattle";
  }

  @Override
  public String getPlugin() {
    return null;
  }

  public String getAuthor() {
    return "Plajer";
  }

  public String getVersion() {
    return "1.0.0";
  }

  @Override
  public String onPlaceholderRequest(Player player, String id) {
    if (player == null) {
      return null;
    }
    switch (id.toLowerCase()) {
      case "blocks_broken":
        return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.BLOCKS_BROKEN));
      case "blocks_placed":
        return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.BLOCKS_PLACED));
      case "games_played":
        return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.GAMES_PLAYED));
      case "wins":
        return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.WINS));
      case "loses":
        return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.LOSES));
      case "highest_win":
        return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.HIGHEST_WIN));
      case "particles_used":
        return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.PARTICLES_USED));
      default:
        return handleArenaPlaceholderRequest(id);
    }
  }

  private String handleArenaPlaceholderRequest(String id) {
    if (!id.contains(":")) {
      return null;
    }
    String[] data = id.split(":");
    BaseArena arena = ArenaRegistry.getArena(data[0]);
    if(arena == null) {
      return null;
    }
    switch (data[1].toLowerCase()) {
      case "players":
        return String.valueOf(arena.getPlayers().size());
      case "max_players":
        return String.valueOf(arena.getMaximumPlayers());
      case "state":
        return String.valueOf(arena.getArenaState());
      case "state_pretty":
        return arena.getArenaState().getFormattedName();
      case "mapname":
        return arena.getMapName();
      case "arenatype":
        return String.valueOf(arena.getArenaType());
      case "arenatype_pretty":
        return arena.getArenaType().getPrefix();
      default:
        return null;
    }
  }

}
