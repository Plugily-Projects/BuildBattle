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

package plugily.projects.buildbattle.events;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.BuildArena;
import plugily.projects.buildbattle.arena.GuessArena;
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.utils.helper.ItemUtils;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;

/**
 * Created by Tom on 17/08/2015.
 */
final class OptionMenuEvents implements Listener {

  private final Main plugin;

  public OptionMenuEvents(Main plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onOpenOptionMenu(PlugilyPlayerInteractEvent event) {
    if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.PHYSICAL || VersionUtils.checkOffHand(event.getHand())) {
      return;
    }

    BaseArena arena = plugin.getArenaRegistry().getArena(event.getPlayer());
    if(arena == null || arena.getArenaState() != ArenaState.IN_GAME || (arena instanceof BuildArena && arena.getArenaInGameStage() != BaseArena.ArenaInGameStage.BUILD_TIME)) {
      return;
    }

    if(plugin.getOptionsRegistry().getMenuItem().isSimilar(event.getItem()))
      event.getPlayer().openInventory(plugin.getOptionsRegistry().formatInventory());
  }

  @Deprecated
  //only a temporary code
  @EventHandler
  public void onPlayerHeadsClick(InventoryClickEvent event) {
    if(!ItemUtils.isItemStackNamed(event.getCurrentItem()) || !(event.getWhoClicked() instanceof Player)) {
      return;
    }
    BaseArena arena = plugin.getArenaRegistry().getArena((Player) event.getWhoClicked());
    if(arena == null) {
      return;
    }
    if(plugin.getOptionsRegistry().getPlayerHeadsRegistry().isHeadsMenu(event.getInventory())) {
      if(event.getCurrentItem().getType() != ItemUtils.PLAYER_HEAD_ITEM.getType()) {
        return;
      }
      event.getWhoClicked().getInventory().addItem(event.getCurrentItem().clone());
      event.setCancelled(true);
    }
  }


  @EventHandler
  public void onPlayerDropItem(PlayerDropItemEvent event) {
    if(!plugin.getArenaRegistry().isInArena(event.getPlayer())) {
      return;
    }

    ItemStack drop = event.getItemDrop().getItemStack();

    if(drop.isSimilar(plugin.getOptionsRegistry().getMenuItem()) || plugin.getVoteItems().getPoints(drop) != 0) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    HumanEntity humanEntity = event.getWhoClicked();

    if (!(humanEntity instanceof Player))
      return;

    BaseArena arena = plugin.getArenaRegistry().getArena((Player) humanEntity);
    if(arena == null) {
      return;
    }
    if(arena.getArenaState() != ArenaState.IN_GAME) {
      event.setCancelled(true);
      return;
    }
    if(arena instanceof BuildArena && arena.getArenaInGameStage() == BaseArena.ArenaInGameStage.BUILD_TIME) {
      return;
    }
    if(arena instanceof GuessArena && humanEntity.equals(((GuessArena) arena).getCurrentBuilder())) {
      return;
    }
    event.setCancelled(true);
  }

}
