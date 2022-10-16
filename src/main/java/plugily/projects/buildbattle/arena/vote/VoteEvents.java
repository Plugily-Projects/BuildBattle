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

package plugily.projects.buildbattle.arena.vote;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.BuildArena;
import plugily.projects.buildbattle.arena.GuessArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;

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
  public void onVote(PlugilyPlayerInteractEvent event) {
    if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.PHYSICAL) {
      return;
    }

    if(VersionUtils.checkOffHand(event.getHand())) {
      return;
    }

    BaseArena arena = plugin.getArenaRegistry().getArena(event.getPlayer());
    if(arena == null || arena.getArenaState() != ArenaState.IN_GAME || arena instanceof GuessArena) {
      return;
    }

    BuildArena solo = (BuildArena) arena;
    if(solo.getArenaInGameState() != BaseArena.ArenaInGameState.PLOT_VOTING) {
      return;
    }

    Plot plot = solo.getVotingPlot();

    if(plugin.getVoteItems().getReportItem().equals(event.getItem())) {
      User user = plugin.getUserManager().getUser(event.getPlayer());

      user.adjustStatistic("REPORTS_TRIGGERED", 1);

      if(plot != null && plugin.getConfigPreferences().getOption("REPORT_COMMANDS")) {
        int reportsAmountNeeded = plugin.getConfig().getInt("Report.Amount", -1);

        if(reportsAmountNeeded == -1 || user.getStatistic("REPORTS_TRIGGERED") >= reportsAmountNeeded) {
          String reportCommand = plugin.getConfig().getString("Report.Execute", "kick %reported%");
          reportCommand = reportCommand.replace("%reporter%", event.getPlayer().getName());

          for(org.bukkit.entity.Player player : plot.getMembers()) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                reportCommand.replace("%reported%", player.getName()));
          }

          event.getPlayer().getInventory().remove(event.getItem());
          event.getPlayer().updateInventory();

          plugin.getRewardsHandler().performReward(event.getPlayer(), arena, plugin.getRewardsHandler().getRewardType("REPORT"));
        }
      }

      event.setCancelled(true);
      return;
    }

    if(plot != null && plot.getMembers().contains(event.getPlayer())) {
      new MessageBuilder("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_OWN").asKey().arena(arena).player(event.getPlayer()).sendPlayer();
      event.setCancelled(true);
      return;
    }

    plugin.getUserManager().getUser(event.getPlayer()).setStatistic("LOCAL_POINTS", plugin.getVoteItems().getPointsAndPlayVoteSound(event.getPlayer(), event.getItem()));

    new MessageBuilder("IN_GAME_MESSAGES_PLOT_VOTING_SUCCESS").asKey().arena(arena).player(event.getPlayer()).sendPlayer();
    event.setCancelled(true);
  }

}
