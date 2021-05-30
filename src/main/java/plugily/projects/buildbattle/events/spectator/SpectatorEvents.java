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

package plugily.projects.buildbattle.events.spectator;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import pl.plajerlair.commonsbox.minecraft.compat.events.api.CBEntityPickupItemEvent;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.user.User;

/**
 * @author Plajer
 * <p>
 * Created at 05.08.2018
 */
public class SpectatorEvents implements Listener {

  private final Main plugin;

  public SpectatorEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onSpectatorTarget(EntityTargetEvent e) {
    if(!(e.getTarget() instanceof Player)) {
      return;
    }
    if(plugin.getUserManager().getUser((Player) e.getTarget()).isSpectator()) {
      e.setCancelled(true);
      e.setTarget(null);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onSpectatorTarget(EntityTargetLivingEntityEvent e) {
    if(!(e.getTarget() instanceof Player)) {
      return;
    }
    if(plugin.getUserManager().getUser((Player) e.getTarget()).isSpectator()) {
      e.setCancelled(true);
      e.setTarget(null);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBlockPlace(BlockPlaceEvent event) {
    if(plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBlockBreak(BlockBreakEvent event) {
    if(plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onDropItem(PlayerDropItemEvent event) {
    if(plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBucketEmpty(PlayerBucketEmptyEvent event) {
    if(plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onInteract(PlayerInteractEntityEvent event) {
    if(plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onShear(PlayerShearEntityEvent event) {
    if(plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onConsume(PlayerItemConsumeEvent event) {
    if(plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onFoodLevelChange(FoodLevelChangeEvent event) {
    if(!(event.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getEntity();
    if(plugin.getUserManager().getUser(player).isSpectator()) {
      event.setFoodLevel(20);
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onDamage(EntityDamageEvent event) {
    if(!(event.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getEntity();
    User user = plugin.getUserManager().getUser(player);
    if(!user.isSpectator()) {
      return;
    }
    if(user.getArena() == null) {
      return;
    }
    if(player.getLocation().getY() < 1) {
      BaseArena arena = ArenaRegistry.getArena(player);
      if(arena != null) {
        player.teleport(arena.getPlotManager().getPlots().get(0).getTeleportLocation());
      }
      event.setDamage(0);
    }
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onDamageByBlock(EntityDamageByBlockEvent event) {
    if(!(event.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getEntity();
    if(plugin.getUserManager().getUser(player).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onDamageByEntity(EntityDamageByEntityEvent event) {
    if(!(event.getDamager() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getDamager();
    if(plugin.getUserManager().getUser(player).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPickup(CBEntityPickupItemEvent event) {
    if(event.getEntity() instanceof Player && plugin.getUserManager().getUser((Player) event.getEntity()).isSpectator()) {
      event.setCancelled(true);
    }
  }
}
