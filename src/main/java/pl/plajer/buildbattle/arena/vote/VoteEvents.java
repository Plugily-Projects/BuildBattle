/*
 * BuildBattle 4 - Ultimate building competition minigame
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.buildbattle.arena.vote;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.ArenaState;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.utils.Utils;
import pl.plajerlair.core.services.exception.ReportedException;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class VoteEvents implements Listener {

  private Main plugin;

  public VoteEvents(Main plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onVote(PlayerInteractEvent event) {
    try {
      if (event.getHand() == EquipmentSlot.OFF_HAND || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
        return;
      }
      if (!Utils.isNamed(event.getItem())) {
        return;
      }
      Arena arena = ArenaRegistry.getArena(event.getPlayer());
      if (arena == null || arena.getArenaState() != ArenaState.IN_GAME || !arena.isVoting()) {
        return;
      }
      if (plugin.getVoteItems().getReportItem().equals(event.getItem())) {
        //todo attempt report
        event.setCancelled(true);
        return;
      }
      if (arena.getVotingPlot().getOwners().contains(event.getPlayer().getUniqueId())) {
        event.getPlayer().sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("In-Game.Messages.Voting-Messages.Cant-Vote-Own-Plot"));
        event.setCancelled(true);
        return;
      }
      plugin.getUserManager().getUser(event.getPlayer().getUniqueId()).setStat(StatsStorage.StatisticType.LOCAL_POINTS, plugin.getVoteItems().getPoints(event.getItem()));
      event.getPlayer().sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("In-Game.Messages.Voting-Messages.Vote-Successful"));
      event.setCancelled(true);
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

}
