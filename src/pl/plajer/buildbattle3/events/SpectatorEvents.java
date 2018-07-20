/*
 * BuildBattle 3 - Ultimate building competition minigame
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
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

package pl.plajer.buildbattle3.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.arena.ArenaRegistry;
import pl.plajer.buildbattle3.user.UserManager;

/**
 * Created by Tom on 1/08/2014.
 */
public class SpectatorEvents implements Listener {

  public SpectatorEvents(Main plugin) {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }


  @EventHandler(priority = EventPriority.HIGH)
  public void onBlockPlace(BlockPlaceEvent event) {
    if (UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator()) event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBlockBreak(BlockBreakEvent event) {
    if (UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator()) event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onDropItem(PlayerDropItemEvent event) {
    if (UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator()) event.setCancelled(true);
  }


  @EventHandler(priority = EventPriority.HIGH)
  public void onspectate(PlayerBucketEmptyEvent event) {
    if (UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator()) event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onspectate(PlayerInteractEntityEvent event) {
    if (UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator()) event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onSpectate(PlayerShearEntityEvent event) {
    if (UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator()) event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onSpectate(PlayerItemConsumeEvent event) {
    if (UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator()) event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onSpectate(FoodLevelChangeEvent event) {
    if (!(event.getEntity() instanceof Player)) return;
    Player player = (Player) event.getEntity();
    if (UserManager.getUser(player.getUniqueId()).isSpectator()) event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onSpectate(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) return;
    Player player = (Player) event.getEntity();
    if (!UserManager.getUser(player.getUniqueId()).isSpectator()) return;
    if (ArenaRegistry.getArena(player) == null) return;
    if (player.getLocation().getY() < 1) player.teleport(ArenaRegistry.getArena(player).getStartLocation());
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onSpectate(EntityDamageByBlockEvent event) {
    if (!(event.getEntity() instanceof Player)) return;
    Player player = (Player) event.getEntity();
    if (UserManager.getUser(player.getUniqueId()).isSpectator()) event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onSpectate(EntityDamageByEntityEvent event) {
    if (!(event.getDamager() instanceof Player)) return;
    Player player = (Player) event.getDamager();
    if (UserManager.getUser(player.getUniqueId()).isSpectator()) event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onSpectate(PlayerPickupItemEvent event) {
    if (UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator()) event.setCancelled(true);
  }


}
