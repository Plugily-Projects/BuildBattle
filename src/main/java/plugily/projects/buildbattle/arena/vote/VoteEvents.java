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

package plugily.projects.buildbattle.arena.vote;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import pl.plajerlair.commonsbox.minecraft.compat.VersionUtils;
import pl.plajerlair.commonsbox.minecraft.compat.events.api.CBPlayerInteractEvent;
import pl.plajerlair.commonsbox.minecraft.item.ItemUtils;
import plugily.projects.buildbattle.ConfigPreferences;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.api.StatsStorage;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.ArenaState;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.arena.impl.GuessTheBuildArena;
import plugily.projects.buildbattle.arena.impl.SoloArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.handlers.reward.Reward;
import plugily.projects.buildbattle.user.User;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class VoteEvents implements Listener {

  private final Main plugin;

  public VoteEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onVote(CBPlayerInteractEvent e) {
    if(VersionUtils.checkOffHand(e.getHand()) || e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.PHYSICAL) {
      return;
    }

    if(!ItemUtils.isItemStackNamed(e.getItem())) {
      return;
    }

    BaseArena arena = ArenaRegistry.getArena(e.getPlayer());
    if(arena == null || arena instanceof GuessTheBuildArena || arena.getArenaState() != ArenaState.IN_GAME || !((SoloArena) arena).isVoting()) {
      return;
    }

    User user = plugin.getUserManager().getUser(e.getPlayer());
    Plot plot = ((SoloArena) arena).getVotingPlot();
    if(plugin.getVoteItems().getReportItem().equals(e.getItem())) {
      user.setStat(StatsStorage.StatisticType.REPORTS, user.getStat(StatsStorage.StatisticType.REPORTS) + 1);
      int reportsAmountNeeded = plugin.getConfig().getInt("Run-Command-On-Report.Reports-Amount-To-Run", -1);

      if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.RUN_COMMAND_ON_REPORT)
          && (reportsAmountNeeded == -1 || user.getStat(StatsStorage.StatisticType.REPORTS) >= reportsAmountNeeded) && plot != null) {
        plot.getOwners().forEach(player -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            plugin.getConfig().getString("Run-Command-On-Report.Command", "kick %reported%")
                .replace("%reported%", player.getName()).replace("%reporter%", e.getPlayer().getName())));
        plugin.getRewardsHandler().performReward(e.getPlayer(), Reward.RewardType.REPORT, -1);
      }

      e.setCancelled(true);
      return;
    }

    if(plot != null && plot.getOwners().contains(e.getPlayer())) {
      e.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Cant-Vote-Own-Plot"));
      e.setCancelled(true);
      return;
    }

    user.setStat(StatsStorage.StatisticType.LOCAL_POINTS, plugin.getVoteItems().getPoints(e.getItem()));
    plugin.getVoteItems().playVoteSound(e.getPlayer(), e.getItem());
    e.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Vote-Successful"));
    e.setCancelled(true);
  }

}
