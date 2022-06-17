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

package plugily.projects.buildbattle.arena;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import plugily.projects.buildbattle.Main;
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.PluginArenaManager;

import java.util.Comparator;

/**
 * @author Plajer
 * <p>Created at 13.05.2018
 */
public class ArenaManager extends PluginArenaManager {

  private final Main plugin;

  public ArenaManager(Main plugin) {
    super(plugin);
    this.plugin = plugin;
  }

  @Override
  public void additionalPartyJoin(Player player, PluginArena arena, Player partyLeader) {
    BaseArena pluginBaseArena = (BaseArena) plugin.getArenaRegistry().getArena(arena.getId());
    if(pluginBaseArena == null) {
      return;
    }
    Base partyBase = null;
    if(arena.getPlayers().contains(partyLeader)) {
      if(pluginBaseArena.inBase(partyLeader)) {
        partyBase = pluginBaseArena.getBase(partyLeader);
      }
    }
    if(partyBase != null) {
      partyBase.addPlayer(player);
    }
  }

  @Override
  public void leaveAttempt(@NotNull Player player, @NotNull PluginArena arena) {
    BaseArena pluginBaseArena = (BaseArena) plugin.getArenaRegistry().getArena(arena.getId());
    if(pluginBaseArena == null) {
      return;
    }
    super.leaveAttempt(player, arena);
    if(arena instanceof GuessArena) {
      if(player == ((GuessArena) arena).getCurrentBuilder()) {
        ((GuessArena) arena).setCurrentBuilder(null);
        if(arena.getArenaState() == ArenaState.IN_GAME) {
          ((GuessArena) arena).setTimer(plugin.getConfig().getInt("Time-Manager." + pluginBaseArena.getArenaType().getPrefix() + ".Round-Delay"));
          ((GuessArena) arena).setArenaInGameStage(BaseArena.ArenaInGameStage.PLOT_VOTING);
        }
      }
    }
  }

  @Override
  public void stopGame(boolean quickStop, @NotNull PluginArena arena) {
    BaseArena pluginBaseArena = (BaseArena) plugin.getArenaRegistry().getArena(arena.getId());
    if(pluginBaseArena == null) {
      return;
    }
    for(Player player : arena.getPlayers()) {
      if(!quickStop) {
        switch(pluginBaseArena.getMode()) {
          case HEARTS:
            if(pluginBaseArena.isDeathPlayer(player)) {
              plugin
                  .getUserManager()
                  .addStat(player, plugin.getStatsStorage().getStatisticType("LOSES"));
              plugin
                  .getRewardsHandler()
                  .performReward(player, arena, plugin.getRewardsHandler().getRewardType("LOSE"));
            } else {
              plugin
                  .getUserManager()
                  .addStat(player, plugin.getStatsStorage().getStatisticType("WINS"));
              plugin
                  .getRewardsHandler()
                  .performReward(player, arena, plugin.getRewardsHandler().getRewardType("WIN"));
              plugin.getUserManager().addExperience(player, 5);
            }
            break;
          case POINTS:
            if(pluginBaseArena.getWinner().getPlayers().contains(player)) {
              plugin
                  .getUserManager()
                  .addStat(player, plugin.getStatsStorage().getStatisticType("WINS"));
              plugin.getUserManager().addExperience(player, 5);
              plugin
                  .getRewardsHandler()
                  .performReward(player, arena, plugin.getRewardsHandler().getRewardType("WIN"));
            } else {
              plugin
                  .getUserManager()
                  .addStat(player, plugin.getStatsStorage().getStatisticType("LOSES"));
              plugin
                  .getRewardsHandler()
                  .performReward(player, arena, plugin.getRewardsHandler().getRewardType("LOSE"));
            }
            break;
          default:
            break;
        }
      }
    }
    super.stopGame(quickStop, arena);
  }
}
