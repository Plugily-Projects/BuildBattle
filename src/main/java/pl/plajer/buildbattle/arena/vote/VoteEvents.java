/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and contributors
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
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.ArenaState;
import pl.plajer.buildbattle.arena.impl.BaseArena;
import pl.plajer.buildbattle.arena.impl.GuessTheBuildArena;
import pl.plajer.buildbattle.arena.impl.SoloArena;
import pl.plajer.buildbattle.utils.Utils;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class VoteEvents implements Listener {

  private Main plugin;

  public VoteEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onVote(PlayerInteractEvent e) {
    if (e.getHand() == EquipmentSlot.OFF_HAND || e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
        return;
      }
    if (!Utils.isNamed(e.getItem())) {
        return;
      }
    BaseArena arena = ArenaRegistry.getArena(e.getPlayer());
      if(arena instanceof GuessTheBuildArena) {
        return;
      }
      if (arena == null || arena.getArenaState() != ArenaState.IN_GAME || !((SoloArena) arena).isVoting()) {
        return;
      }
    if (plugin.getVoteItems().getReportItem().equals(e.getItem())) {
        //todo attempt report
      e.setCancelled(true);
        return;
      }
    if (((SoloArena) arena).getVotingPlot().getOwners().contains(e.getPlayer())) {
      e.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Cant-Vote-Own-Plot"));
      e.setCancelled(true);
        return;
      }
    plugin.getUserManager().getUser(e.getPlayer()).setStat(StatsStorage.StatisticType.LOCAL_POINTS, plugin.getVoteItems().getPoints(e.getItem()));
    plugin.getVoteItems().playVoteSound(e.getPlayer(), e.getItem());
    e.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("In-Game.Messages.Voting-Messages.Vote-Successful"));
    e.setCancelled(true);
  }

}
