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

package plugily.projects.buildbattle.handlers.themes.vote;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.BuildArena;
import plugily.projects.minigamesbox.classic.commonsbox.sorter.SortUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Plajer
 * <p>
 * Created at 08.07.2018
 */
public class VotePoll {

  private final BuildArena arena;
  private final Map<String, Integer> votedThemes = new LinkedHashMap<>();
  private final Map<Player, String> playerVote = new HashMap<>();
  private static final Main plugin = JavaPlugin.getPlugin(Main.class);

  public VotePoll(BuildArena arena, List<String> votedThemes) {
    this.arena = arena;
    for(String theme : votedThemes) {
      this.votedThemes.put(theme, 0);
    }
  }

  public BuildArena getArena() {
    return arena;
  }

  public Map<String, Integer> getVotedThemes() {
    return votedThemes;
  }

  public int getVoteAmount(String theme) {
    return votedThemes.getOrDefault(theme, 0);
  }

  public boolean addVote(Player player, String theme) {
    String playerVoteTheme = playerVote.get(player);
    if(playerVoteTheme != null) {
      if(playerVoteTheme.equals(theme)) {
        return false;
      }
      votedThemes.put(playerVoteTheme, votedThemes.get(playerVoteTheme) - 1);
    } else {
      plugin.getRewardsHandler().performReward(player, arena, plugin.getRewardsHandler().getRewardType("VOTE"), -1);
    }
    votedThemes.put(theme, votedThemes.getOrDefault(theme, 0) + 1);
    playerVote.put(player, theme);
    return true;
  }

  public Map<Player, String> getPlayerVote() {
    return playerVote;
  }

  public String getVotedTheme() {
    Object[] themes = SortUtils.sortByValue(votedThemes).keySet().toArray();
    return themes.length != 0 ? (String) themes[themes.length - 1] : "";
  }

  public String getThemeByPosition(int position) {
    if(position % 9 != 0) {
      return "Incompatible operation";
    }
    int i = 1;
    for(String theme : votedThemes.keySet()) {
      if(position / 9 == i) {
        return theme;
      }
      i++;
    }
    return "none";
  }

}
