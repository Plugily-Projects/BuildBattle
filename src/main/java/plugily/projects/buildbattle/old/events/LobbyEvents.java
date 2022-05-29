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

package plugily.projects.buildbattle.old.events;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import plugily.projects.commonsbox.minecraft.compat.VersionUtils;
import plugily.projects.buildbattle.old.Main;
import plugily.projects.buildbattle.old.arena.ArenaRegistry;
import plugily.projects.buildbattle.old.arena.ArenaState;

/**
 * @author Plajer
 * <p>
 * Created at 31.12.2018
 */
public class LobbyEvents implements Listener {

  public LobbyEvents(Main plugin) {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onFoodLose(FoodLevelChangeEvent event) {
    if(event.getEntity().getType() != EntityType.PLAYER) {
      return;
    }
    BaseArena arena = ArenaRegistry.getArena((Player) event.getEntity());
    if(arena != null && (arena.getArenaState() == ArenaState.STARTING || arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS)) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onLobbyDamage(EntityDamageEvent event) {
    if(event.getEntity().getType() != EntityType.PLAYER) {
      return;
    }
    Player player = (Player) event.getEntity();
    BaseArena arena = ArenaRegistry.getArena(player);
    if(arena == null || arena.getArenaState() == ArenaState.IN_GAME) {
      return;
    }
    event.setCancelled(true);
    player.setHealth(VersionUtils.getMaxHealth(player));
  }

  @EventHandler
  public void onItemFrameRotate(PlayerInteractEntityEvent event) {
    BaseArena arena = ArenaRegistry.getArena(event.getPlayer());
    if(arena == null || arena.getArenaState() == ArenaState.IN_GAME) {
      return;
    }
    if(event.getRightClicked().getType() == EntityType.ITEM_FRAME && ((ItemFrame) event.getRightClicked()).getItem().getType() != Material.AIR) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onHangingBreak(HangingBreakByEntityEvent event) {
    if(event.getEntity().getType() != EntityType.PLAYER) {
      return;
    }

    BaseArena arena = ArenaRegistry.getArena((Player) event.getEntity());
    if(arena == null || arena.getArenaState() == ArenaState.IN_GAME) {
      return;
    }
    event.setCancelled(true);
  }

}
