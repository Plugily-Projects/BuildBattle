/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.buildbattle.events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import pl.plajer.buildbattle.ConfigPreferences;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.arena.ArenaManager;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.ArenaState;
import pl.plajer.buildbattle.arena.impl.BaseArena;
import pl.plajer.buildbattle.arena.impl.SoloArena;
import pl.plajer.buildbattle.arena.managers.plots.Plot;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.handlers.items.SpecialItem;
import pl.plajer.buildbattle.menus.options.registry.particles.ParticleRemoveMenu;
import pl.plajer.buildbattle.user.User;
import pl.plajer.buildbattle.utils.Utils;
import pl.plajerlair.core.services.exception.ReportedException;

/**
 * Created by Tom on 17/08/2015.
 */
public class GameEvents implements Listener {

  private Main plugin;

  public GameEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onLeave(PlayerInteractEvent event) {
    try {
      if (event.getHand() == EquipmentSlot.OFF_HAND || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
        return;
      }
      BaseArena arena = ArenaRegistry.getArena(event.getPlayer());
      if (arena == null) {
        return;
      }
      ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
      if (!Utils.isNamed(itemStack)) {
        return;
      }
      SpecialItem item = plugin.getSpecialItemsRegistry().getRelatedSpecialItem(itemStack);
      if (item == null) {
        return;
      }
      if (item.getName().equalsIgnoreCase("Leave")) {
        event.setCancelled(true);
        if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
          plugin.getBungeeManager().connectToHub(event.getPlayer());
        } else {
          ArenaManager.leaveAttempt(event.getPlayer(), arena);
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }


  @EventHandler
  public void onOpenOptionMenu(PlayerInteractEvent event) {
    try {
      if (event.getHand() == EquipmentSlot.OFF_HAND || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
        return;
      }
      ItemStack itemStack = event.getItem();
      if (!Utils.isNamed(itemStack)) {
        return;
      }
      BaseArena arena = ArenaRegistry.getArena(event.getPlayer());
      if (arena == null || arena.getArenaState() != ArenaState.IN_GAME) {
        return;
      }
      if (arena instanceof SoloArena && ((SoloArena) arena).isVoting()) {
        return;
      }
      if (!plugin.getOptionsRegistry().getMenuItem().getItemMeta().getDisplayName().equalsIgnoreCase(itemStack.getItemMeta().getDisplayName())) {
        return;
      }
      event.getPlayer().openInventory(plugin.getOptionsRegistry().formatInventory());
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onPistonExtendEvent(BlockPistonExtendEvent event) {
    try {
      for (BaseArena arena : ArenaRegistry.getArenas()) {
        for (Plot buildPlot : arena.getPlotManager().getPlots()) {
          for (Block block : event.getBlocks()) {
            if (!buildPlot.getCuboid().isInWithMarge(block.getLocation(), -1) && buildPlot.getCuboid().isIn(event.getBlock().getLocation())) {
              event.setCancelled(true);
            }
          }
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onFoodChange(FoodLevelChangeEvent event) {
    if (!(event.getEntity().getType() == EntityType.PLAYER)) {
      return;
    }
    Player player = (Player) event.getEntity();
    if (ArenaRegistry.getArena(player) == null) {
      return;
    }
    event.setCancelled(true);
    player.setFoodLevel(20);
  }

  @EventHandler
  public void onWaterFlowEvent(BlockFromToEvent event) {
    try {
      for (BaseArena arena : ArenaRegistry.getArenas()) {
        for (Plot buildPlot : arena.getPlotManager().getPlots()) {
          if (!buildPlot.getCuboid().isIn(event.getToBlock().getLocation()) && buildPlot.getCuboid().isIn(event.getBlock().getLocation())) {
            event.setCancelled(true);
          }
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onTNTExplode(EntityExplodeEvent event) {
    try {
      for (BaseArena arena : ArenaRegistry.getArenas()) {
        for (Plot buildPlot : arena.getPlotManager().getPlots()) {
          if (buildPlot.getCuboid().isInWithMarge(event.getEntity().getLocation(), 0)) {
            event.blockList().clear();
            event.setCancelled(true);
          } else if (buildPlot.getCuboid().isInWithMarge(event.getEntity().getLocation(), 5)) {
            event.getEntity().getLocation().getBlock().setType(Material.TNT);
            event.blockList().clear();
            event.setCancelled(true);
          }
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onTNTInteract(PlayerInteractEvent event) {
    try {
      if (event.getHand() == EquipmentSlot.OFF_HAND) {
        return;
      }
      Player player = event.getPlayer();
      BaseArena arena = ArenaRegistry.getArena(player);
      if (arena == null) {
        return;
      }
      if (player.getInventory().getItemInMainHand() == null) {
        return;
      }
      if (player.getInventory().getItemInMainHand().getType() != Material.FLINT_AND_STEEL) {
        return;
      }
      if (event.getClickedBlock() == null) {
        return;
      }
      if (event.getClickedBlock().getType() == Material.TNT) {
        event.setCancelled(true);
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
    try {
      if (event.getEntity().getType() != EntityType.PLAYER) {
        return;
      }
      Player player = (Player) event.getEntity();
      BaseArena arena = ArenaRegistry.getArena(player);
      if (arena == null) {
        return;
      }
      event.setCancelled(true);
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onTreeGrow(StructureGrowEvent event) {
    try {
      BaseArena arena = ArenaRegistry.getArena(event.getPlayer());
      if (arena == null) {
        return;
      }
      Plot buildPlot = arena.getPlotManager().getPlot(event.getPlayer());
      if (buildPlot == null) {
        return;
      }
      for (BlockState blockState : event.getBlocks()) {
        if (!buildPlot.getCuboid().isIn(blockState.getLocation())) {
          blockState.setType(Material.AIR);
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  //todo weird code?
  @EventHandler
  public void onDispense(BlockDispenseEvent event) {
    for (BaseArena arena : ArenaRegistry.getArenas()) {
      for (Plot buildPlot : arena.getPlotManager().getPlots()) {
        if (!buildPlot.getCuboid().isInWithMarge(event.getBlock().getLocation(), -1) && buildPlot.getCuboid().isInWithMarge(event.getBlock().getLocation(), 5)) {
          event.setCancelled(true);
        }
      }
    }
  }

  @Deprecated
  //only a temporary code
  @EventHandler
  public void onPlayerHeadsClick(InventoryClickEvent e) {
    if (e.getInventory() == null || !Utils.isNamed(e.getCurrentItem()) || !(e.getWhoClicked() instanceof Player)) {
      return;
    }
    BaseArena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
    if (e.getInventory().getName() == null || arena == null) {
      return;
    }
    if (plugin.getOptionsRegistry().getPlayerHeadsRegistry().getMenuNames().contains(e.getInventory().getName())) {
      if (e.getCurrentItem().getType() != Utils.PLAYER_HEAD_ITEM.getType()) {
        return;
      }
      e.getWhoClicked().getInventory().addItem(e.getCurrentItem().clone());
      e.setCancelled(true);
    } else if (e.getInventory().getName().equals(ChatManager.colorMessage("Menus.Option-Menu.Items.Particle.In-Inventory-Item-Name"))) {
      ParticleRemoveMenu.onClick((Player) e.getWhoClicked(), e.getInventory(), e.getCurrentItem(),
          arena.getPlotManager().getPlot((Player) e.getWhoClicked()));
    }
  }

  @Deprecated
  @EventHandler
  public void onOptionItemClick(InventoryClickEvent e) {
    try {
      if (!(e.getWhoClicked() instanceof Player) || !Utils.isNamed(e.getCurrentItem())) {
        return;
      }
      BaseArena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
      if (e.getCurrentItem().getType() != Material.NETHER_STAR || arena == null) {
        return;
      }
      if (!e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatManager.colorMessage("Menus.Option-Menu.Option-Item"))) {
        return;
      }
      e.setResult(Event.Result.DENY);
      e.setCancelled(true);
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPreCommand(PlayerCommandPreprocessEvent event) {
    try {
      if (ArenaRegistry.getArena(event.getPlayer()) == null) {
        return;
      }
      for (String string : plugin.getConfigPreferences().getWhitelistedCommands()) {
        if (event.getMessage().contains(string)) {
          return;
        }
      }
      if (event.getPlayer().isOp() || event.getPlayer().hasPermission("buildbattle.admin") || event.getPlayer().hasPermission("buildbattle.command.bypass")) {
        return;
      }
      if (event.getMessage().startsWith("/bb") || event.getMessage().startsWith("/buildbattle") || event.getMessage().startsWith("/bba") ||
          event.getMessage().startsWith("/buildbattleadmin")) {
        return;
      }
      event.setCancelled(true);
      event.getPlayer().sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("In-Game.Only-Command-Ingame-Is-Leave"));
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onBucketEmpty(PlayerBucketEmptyEvent event) {
    try {
      BaseArena arena = ArenaRegistry.getArena(event.getPlayer());
      if (arena == null) {
        return;
      }
      Plot buildPlot = arena.getPlotManager().getPlot(event.getPlayer());
      if (buildPlot == null) {
        return;
      }
      if (!buildPlot.getCuboid().isIn(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation())) {
        event.setCancelled(true);
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onBlockSpread(BlockSpreadEvent event) {
    try {
      for (BaseArena arena : ArenaRegistry.getArenas()) {
        if (!arena.getPlotManager().getPlots().isEmpty() && arena.getPlotManager().getPlots().get(0) != null) {
          if (arena.getPlotManager().getPlots().get(0).getCuboid() == null) {
            continue;
          }
          if (arena.getPlotManager().getPlots().get(0).getCuboid().getCenter().getWorld().equals(event.getBlock().getWorld())) {
            if (event.getSource().getType() == Material.FIRE) {
              event.setCancelled(true);
            }
          }
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onCreatureSpawn(CreatureSpawnEvent event) {
    try {
      for (BaseArena arena : ArenaRegistry.getArenas()) {
        if (arena.getPlotManager().getPlots() == null || arena.getPlotManager().getPlots().isEmpty()) {
          continue;
        }
        if (arena.getPlotManager().getPlots().get(0) == null || event.getEntity().getWorld().equals(arena.getPlotManager().getPlots().get(0).getCuboid().getCenter().getWorld())) {
          continue;
        }
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
          return;
        }
        if (event.getEntity().getType() == EntityType.WITHER || plugin.getConfig().getBoolean("Disable-Mob-Spawning-Completely", true)) {
          event.setCancelled(true);
          return;
        }
        for (Plot plot : arena.getPlotManager().getPlots()) {
          if (plot.getCuboid().isInWithMarge(event.getEntity().getLocation(), 10)) {
            event.setCancelled(true);
            return;
          }
          if (plot.getCuboid().isInWithMarge(event.getEntity().getLocation(), 1)) {
            if (plot.getEntities() >= plugin.getConfig().getInt("Mobs-Max-Amount-Per-Plot", 20)) {
              //todo maybe only for spawner player?
              for (Player p : plot.getOwners()) {
                p.sendMessage(ChatManager.colorMessage("In-Game.Max-Entities-Limit-Reached"));
              }
              event.setCancelled(true);
              return;
            } else {
              plot.addEntity();
              event.setCancelled(false);
              event.getEntity().setAI(false);
            }
          }
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onLeavesDecay(LeavesDecayEvent event) {
    for (BaseArena arena : ArenaRegistry.getArenas()) {
      for (Plot buildPlot : arena.getPlotManager().getPlots()) {
        if (buildPlot.getCuboid().isInWithMarge(event.getBlock().getLocation(), 5)) {
          event.setCancelled(true);
        }
      }
    }
  }


  @EventHandler
  public void onPlayerDropItem(PlayerDropItemEvent event) {
    try {
      if (ArenaRegistry.getArena(event.getPlayer()) == null) {
        return;
      }
      ItemStack drop = event.getItemDrop().getItemStack();
      if (!Utils.isNamed(drop)) {
        return;
      }
      if (drop.getItemMeta().getDisplayName().equals(ChatManager.colorMessage("Menus.Option-Menu.Inventory-Name")) || plugin.getVoteItems().getPoints(drop) != 0) {
        event.setCancelled(true);
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }


  @EventHandler(priority = EventPriority.HIGH)
  public void onBreak(BlockBreakEvent event) {
    try {
      BaseArena arena = ArenaRegistry.getArena(event.getPlayer());
      if (arena == null) {
        return;
      }
      if (arena.getArenaState() != ArenaState.IN_GAME) {
        event.setCancelled(true);
        return;
      }
      if (arena instanceof SoloArena && ((SoloArena) arena).isVoting()) {
        event.setCancelled(true);
        return;
      }
      if (plugin.getConfigPreferences().getItemBlacklist().contains(event.getBlock().getType())) {
        event.setCancelled(true);
        return;
      }
      User user = plugin.getUserManager().getUser(event.getPlayer());
      Plot buildPlot = user.getCurrentPlot();
      if (buildPlot == null) {
        event.setCancelled(true);
        return;
      }
      if (buildPlot.getCuboid().isIn(event.getBlock().getLocation())) {
        user.addStat(StatsStorage.StatisticType.BLOCKS_BROKEN, 1);
        return;
      }
      event.setCancelled(true);
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlace(BlockPlaceEvent event) {
    try {
      BaseArena arena = ArenaRegistry.getArena(event.getPlayer());
      if (arena == null) {
        return;
      }
      if (arena.getArenaState() != ArenaState.IN_GAME) {
        event.setCancelled(true);
        return;
      }
      if (plugin.getConfigPreferences().getItemBlacklist().contains(event.getBlock().getType())) {
        event.setCancelled(true);
        return;
      }
      if (arena instanceof SoloArena && ((SoloArena) arena).isVoting()) {
        event.setCancelled(true);
        return;
      }
      User user = plugin.getUserManager().getUser(event.getPlayer());
      Plot buildPlot = user.getCurrentPlot();
      if (buildPlot == null) {
        event.setCancelled(true);
        return;
      }
      if (buildPlot.getCuboid().isIn(event.getBlock().getLocation())) {
        user.addStat(StatsStorage.StatisticType.BLOCKS_PLACED, 1);
        return;
      }
      event.setCancelled(true);
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    try {
      BaseArena arena = ArenaRegistry.getArena((Player) event.getWhoClicked());
      if (arena == null) {
        return;
      }
      if (arena.getArenaState() != ArenaState.IN_GAME) {
        event.setCancelled(true);
        return;
      }
      if (arena instanceof SoloArena && !((SoloArena) arena).isVoting()) {
        return;
      }
      event.setCancelled(true);
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onNPCClick(PlayerInteractEntityEvent e) {
    try {
      if (e.getHand() == EquipmentSlot.OFF_HAND) {
        return;
      }
      if (e.getPlayer().getInventory().getItemInMainHand() == null || e.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
        return;
      }
      if (e.getRightClicked() instanceof Villager && e.getRightClicked().getCustomName() != null && e.getRightClicked().getCustomName().equalsIgnoreCase(ChatManager.colorMessage("In-Game.NPC.Floor-Change-NPC-Name"))) {
        BaseArena arena = ArenaRegistry.getArena(e.getPlayer());
        if (arena == null || arena.getArenaState() != ArenaState.IN_GAME) {
          return;
        }
        if(arena instanceof SoloArena && ((SoloArena) arena).isVoting()) {
          return;
        }
        if (!e.getPlayer().getInventory().getItemInMainHand().getType().isBlock()) {
          return;
        }
        if (plugin.getConfigPreferences().getItemBlacklist().contains(e.getPlayer().getInventory().getItemInMainHand().getType())) {
          return;
        }
        arena.getPlotManager().getPlot(e.getPlayer()).changeFloor(e.getPlayer().getInventory().getItemInMainHand().getType(), e.getPlayer().getInventory().getItemInMainHand().getData().getData());
        e.getPlayer().sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Menus.Option-Menu.Items.Floor.Floor-Changed"));
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

}
