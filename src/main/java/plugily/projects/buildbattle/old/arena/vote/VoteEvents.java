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

package plugily.projects.buildbattle.old.arena.vote;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import plugily.projects.commonsbox.minecraft.compat.VersionUtils;
import plugily.projects.commonsbox.minecraft.compat.events.api.CBPlayerInteractEvent;
import plugily.projects.commonsbox.minecraft.item.ItemUtils;
import plugily.projects.buildbattle.old.ConfigPreferences;
import plugily.projects.buildbattle.old.Main;
import plugily.projects.buildbattle.old.api.StatsStorage;
import plugily.projects.buildbattle.old.arena.ArenaRegistry;
import plugily.projects.buildbattle.old.arena.ArenaState;
import plugily.projects.buildbattle.old.arena.impl.GuessTheBuildArena;
import plugily.projects.buildbattle.old.arena.managers.plots.Plot;
import plugily.projects.buildbattle.old.handlers.reward.Reward;
import plugily.projects.buildbattle.old.user.User;

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
    if(arena == null || arena.getArenaState() != ArenaState.IN_GAME || arena instanceof GuessTheBuildArena) {
      return;
    }

    SoloArena solo = (SoloArena) arena;
    if (!solo.isVoting())
      return;

    User user = plugin.getUserManager().getUser(e.getPlayer());
    Plot plot = solo.getVotingPlot();

    if(plugin.getVoteItems().getReportItem().equals(e.getItem())) {
      user.setStat(StatsStorage.StatisticType.REPORTS, user.getStat(StatsStorage.StatisticType.REPORTS) + 1);

      if(plot != null && plugin.getConfigPreferences().getOption(ConfigPreferences.Option.RUN_COMMAND_ON_REPORT)) {
        int reportsAmountNeeded = plugin.getConfig().getInt("Run-Command-On-Report.Reports-Amount-To-Run", -1);

        if(reportsAmountNeeded == -1 || user.getStat(StatsStorage.StatisticType.REPORTS) >= reportsAmountNeeded) {
          String reportCommand = plugin.getConfig().getString("Run-Command-On-Report.Command", "kick %reported%");
          reportCommand = reportCommand.replace("%reporter%", e.getPlayer().getName());

          for (org.bukkit.entity.Player player : plot.getMembers()) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                reportCommand.replace("%reported%", player.getName()));
          }

          e.getPlayer().getInventory().remove(e.getItem());
          e.getPlayer().updateInventory();

          plugin.getRewardsHandler().performReward(e.getPlayer(), arena, Reward.RewardType.REPORT, -1);
          plugin.getRewardsHandler().lastCode = null;
        }
      }

      e.setCancelled(true);
      return;
    }

    if(plot != null && plot.getMembers().contains(e.getPlayer())) {
      e.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Cant-Vote-Own-Plot"));
      e.setCancelled(true);
      return;
    }

    user.setStat(StatsStorage.StatisticType.LOCAL_POINTS, plugin.getVoteItems().getPointsAndPlayVoteSound(e.getPlayer(), e.getItem()));

    e.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Vote-Successful"));
    e.setCancelled(true);
  }

}
