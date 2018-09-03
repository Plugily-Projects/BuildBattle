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

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import pl.plajer.buildbattle3.ConfigPreferences;
import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.VoteItems;
import pl.plajer.buildbattle3.arena.Arena;
import pl.plajer.buildbattle3.arena.ArenaManager;
import pl.plajer.buildbattle3.arena.ArenaRegistry;
import pl.plajer.buildbattle3.arena.ArenaState;
import pl.plajer.buildbattle3.arena.plots.ArenaPlot;
import pl.plajer.buildbattle3.entities.BuildBattleEntity;
import pl.plajer.buildbattle3.handlers.ChatManager;
import pl.plajer.buildbattle3.handlers.items.SpecialItemManager;
import pl.plajer.buildbattle3.menus.OptionsMenu;
import pl.plajer.buildbattle3.menus.WeatherInventory;
import pl.plajer.buildbattle3.menus.particles.ParticleMenu;
import pl.plajer.buildbattle3.menus.particles.ParticleRemoveMenu;
import pl.plajer.buildbattle3.menus.playerheads.PlayerHeadsMenu;
import pl.plajer.buildbattle3.plajerlair.core.services.ReportedException;
import pl.plajer.buildbattle3.user.User;
import pl.plajer.buildbattle3.user.UserManager;

/**
 * Created by Tom on 17/08/2015.
 */
public class GameEvents implements Listener {

  private Main plugin;

  public GameEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onVote(PlayerInteractEvent event) {
    try {
      if (event.getHand() == EquipmentSlot.OFF_HAND) return;
      if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) return;
      if (event.getItem() == null) return;
      Arena arena = ArenaRegistry.getArena(event.getPlayer());
      if (arena == null) return;
      if (arena.getArenaState() != ArenaState.IN_GAME) return;

      if (!event.getItem().hasItemMeta()) return;
      if (!event.getItem().getItemMeta().hasDisplayName()) return;
      if (!arena.isVoting()) return;
      if (arena.getVotingPlot().getOwners().contains(event.getPlayer().getUniqueId())) {
        event.getPlayer().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Voting-Messages.Cant-Vote-Own-Plot"));
        event.setCancelled(true);
        return;
      }
      UserManager.getUser(event.getPlayer().getUniqueId()).setInt("points", VoteItems.getPoints(event.getItem()));
      event.getPlayer().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Voting-Messages.Vote-Successful"));
      event.setCancelled(true);
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onLeave(PlayerInteractEvent event) {
    try {
      if (event.getHand() == EquipmentSlot.OFF_HAND) return;
      if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) return;
      Arena arena = ArenaRegistry.getArena(event.getPlayer());
      if (arena == null) return;
      ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
      if (itemStack == null) return;
      if (itemStack.getItemMeta() == null) return;
      if (itemStack.getItemMeta().getDisplayName() == null) return;
      String key = SpecialItemManager.getRelatedSpecialItem(itemStack);
      if (key == null) return;
      if (SpecialItemManager.getRelatedSpecialItem(itemStack).equalsIgnoreCase("Leave")) {
        event.setCancelled(true);
        if (plugin.isBungeeActivated()) {
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
      if (event.getHand() == EquipmentSlot.OFF_HAND) return;
      if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) return;
      if (event.getItem() == null) return;
      Arena arena = ArenaRegistry.getArena(event.getPlayer());
      if (arena == null) return;
      if (arena.getArenaState() != ArenaState.IN_GAME) return;
      ItemStack itemStack = event.getItem();
      if (!itemStack.hasItemMeta()) return;
      if (!itemStack.getItemMeta().hasDisplayName()) return;
      if (arena.isVoting()) return;
      if (!OptionsMenu.getMenuItem().getItemMeta().getDisplayName().equalsIgnoreCase(itemStack.getItemMeta().getDisplayName())) return;
      OptionsMenu.openMenu(event.getPlayer(), arena.getPlotManager().getPlot(event.getPlayer()));
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onPistonExtendEvent(BlockPistonExtendEvent event) {
    try {
      for (Arena arena : ArenaRegistry.getArenas()) {
        for (ArenaPlot buildPlot : arena.getPlotManager().getPlots()) {
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
    if (!(event.getEntity().getType() == EntityType.PLAYER)) return;
    Player player = (Player) event.getEntity();
    if (ArenaRegistry.getArena(player) == null) return;
    event.setCancelled(true);
    player.setFoodLevel(20);
  }

  @EventHandler
  public void onWaterFlowEvent(BlockFromToEvent event) {
    try {
      for (Arena arena : ArenaRegistry.getArenas()) {
        for (ArenaPlot buildPlot : arena.getPlotManager().getPlots()) {
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
      for (Arena arena : ArenaRegistry.getArenas()) {
        for (ArenaPlot buildPlot : arena.getPlotManager().getPlots()) {
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
      if (event.getHand() == EquipmentSlot.OFF_HAND) return;
      Player player = event.getPlayer();
      Arena arena = ArenaRegistry.getArena(player);
      if (arena == null) return;
      if (player.getInventory().getItemInMainHand() == null) return;
      if (player.getInventory().getItemInMainHand().getType() != Material.FLINT_AND_STEEL) {
        return;
      }
      if (event.getClickedBlock() == null) return;
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
      if (event.getEntity().getType() != EntityType.PLAYER) return;
      Player player = (Player) event.getEntity();
      Arena arena = ArenaRegistry.getArena(player);
      if (arena == null) return;
      event.setCancelled(true);
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onTreeGrow(StructureGrowEvent event) {
    try {
      Arena arena = ArenaRegistry.getArena(event.getPlayer());
      if (arena == null) return;
      ArenaPlot buildPlot = arena.getPlotManager().getPlot(event.getPlayer());
      if (buildPlot == null) return;
      for (BlockState blockState : event.getBlocks()) {
        if (!buildPlot.getCuboid().isIn(blockState.getLocation())) blockState.setType(Material.AIR);
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onDispense(BlockDispenseEvent event) {
    for (Arena arena : ArenaRegistry.getArenas()) {
      for (ArenaPlot buildPlot : arena.getPlotManager().getPlots()) {
        if (!buildPlot.getCuboid().isInWithMarge(event.getBlock().getLocation(), -1) && buildPlot.getCuboid().isInWithMarge(event.getBlock().getLocation(), 5)) {
          event.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void onWeatherMenuClick(InventoryClickEvent e) {
    try {
      if (e.getInventory() == null || e.getInventory().getName() == null || !(e.getWhoClicked() instanceof Player) || ArenaRegistry.getArena((Player) e.getWhoClicked()) == null) {
        return;
      }
      if (e.getInventory().getName().equalsIgnoreCase(ChatManager.colorMessage("Menus.Option-Menu.Weather-Inventory-Name"))) {
        e.setCancelled(true);
        if (e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta() || !e.getCurrentItem().getItemMeta().hasDisplayName()) {
          return;
        }
        Arena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
        if (arena.getPlotManager().getPlot((Player) e.getWhoClicked()) == null) {
          return;
        }
        if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatManager.colorMessage("Menus.Option-Menu.Weather-Downfall"))) {
          arena.getPlotManager().getPlot((Player) e.getWhoClicked()).setWeatherType(WeatherType.DOWNFALL);
          e.getWhoClicked().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Menus.Option-Menu.Weather-Set"));
        } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatManager.colorMessage("Menus.Option-Menu.Weather-Clear"))) {
          arena.getPlotManager().getPlot((Player) e.getWhoClicked()).setWeatherType(WeatherType.CLEAR);
          e.getWhoClicked().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Menus.Option-Menu.Weather-Set"));
        }
        for (UUID owner : arena.getPlotManager().getPlot((Player) e.getWhoClicked()).getOwners()) {
          if (Bukkit.getPlayer(owner).isOnline()) {
            Bukkit.getPlayer(owner).setPlayerWeather(arena.getPlotManager().getPlot((Player) e.getWhoClicked()).getWeatherType());
          }
        }
      }
    } catch (Exception ex){
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onOptionMenuClick(InventoryClickEvent e) {
    try {
      if (e.getWhoClicked() instanceof Player && ArenaRegistry.getArena((Player) e.getWhoClicked()) != null && e.getCurrentItem() != null &&
              e.getCurrentItem().getType() == Material.NETHER_STAR && e.getCurrentItem().getItemMeta().hasDisplayName() &&
              e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatManager.colorMessage("Menus.Option-Menu.Option-Item"))) {
        e.setResult(Event.Result.DENY);
        e.setCursor(null);
        e.setCancelled(true);
      }
      if (e.getInventory() == null) return;
      if (e.getCurrentItem() == null) return;
      if (!e.getCurrentItem().hasItemMeta()) return;
      if (!e.getCurrentItem().getItemMeta().hasDisplayName()) return;
      String displayName = e.getCurrentItem().getItemMeta().getDisplayName();
      Player player = (Player) e.getWhoClicked();
      if (e.getInventory().getName().equals(ChatManager.colorMessage("Menus.Option-Menu.Inventory-Name"))) {
        e.setCancelled(true);
      }
      Arena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
      if (arena == null) return;

      if (arena.getArenaState() != ArenaState.IN_GAME) return;
      if (displayName.equalsIgnoreCase(ChatManager.colorMessage("Menus.Option-Menu.Particle-Option"))) {
        e.getWhoClicked().closeInventory();
        ParticleMenu.openMenu(player);
        return;
      } else if (displayName.equalsIgnoreCase(ChatManager.colorMessage("Menus.Option-Menu.Reset-Option"))) {
        e.getWhoClicked().closeInventory();
        arena.getPlotManager().getPlot((Player) e.getWhoClicked()).resetPlot();
        e.getWhoClicked().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Menus.Option-Menu.Reset-Option-Done"));
        return;
      } else if (displayName.equalsIgnoreCase(ChatManager.colorMessage("Menus.Option-Menu.Weather-Option"))) {
        e.getWhoClicked().closeInventory();
        WeatherInventory.openWeatherInventory((Player) e.getWhoClicked());
        return;
      }
      if (e.getInventory().getName().equalsIgnoreCase(ChatManager.colorMessage("Menus.Option-Menu.Particle-Remove"))) {
        ParticleRemoveMenu.onClick((Player) e.getWhoClicked(), e.getInventory(), e.getCurrentItem(), arena.getPlotManager().getPlot(player));
        return;
      } else if (e.getInventory().getName().equalsIgnoreCase(ChatManager.colorMessage("Menus.Option-Menu.Players-Heads-Inventory-Name"))) {
        PlayerHeadsMenu.onClickInMainMenu(player, e.getCurrentItem());
        return;
      } else if (PlayerHeadsMenu.getMenuNames().contains(e.getInventory().getName())) {
        PlayerHeadsMenu.onClickInDeeperMenu(player, e.getCurrentItem());
        return;
      } else if (displayName.equalsIgnoreCase(ChatManager.colorMessage("Menus.Option-Menu.Players-Heads-Option"))) {
        PlayerHeadsMenu.openMenu(player);
        return;
      } else if (e.getInventory().getName().equalsIgnoreCase(ChatManager.colorMessage("Menus.Option-Menu.Particle-Inventory-Name"))) {
        if (displayName.contains(ChatManager.colorMessage("Menus.Option-Menu.Particle-Remove"))) {
          e.getWhoClicked().closeInventory();
          ParticleRemoveMenu.openMenu(player, arena.getPlotManager().getPlot((Player) e.getWhoClicked()));
          return;
        }
        e.setCancelled(true);
        ParticleMenu.onClick(player, e.getCurrentItem(), arena.getPlotManager().getPlot((Player) e.getWhoClicked()));
        return;
      }
      if (e.getCursor() == null) {
        return;
      }
      if (!((e.getCursor().getType().isBlock() && e.getCursor().getType().isSolid()) || e.getCursor().getType() == Material.WATER_BUCKET || e.getCursor().getType() == Material.LAVA_BUCKET)) {
        return;
      }
      if (e.getCursor().getType() == null || e.getCursor().getType() == Material.SAPLING || e.getCursor().getType() == Material.TRAP_DOOR || e.getCursor().getType() == Material.WOOD_DOOR || e.getCursor().getType() == Material.IRON_TRAPDOOR || e.getCursor().getType() == Material.WOODEN_DOOR || e.getCursor().getType() == Material.ACACIA_DOOR || e.getCursor().getType() == Material.BIRCH_DOOR || e.getCursor().getType() == Material.WOOD_DOOR || e.getCursor().getType() == Material.JUNGLE_DOOR || e.getCursor().getType() == Material.SPRUCE_DOOR || e.getCursor().getType() == Material.IRON_DOOR || e.getCursor().getType() == Material.CHEST || e.getCursor().getType() == Material.TRAPPED_CHEST || e.getCursor().getType() == Material.FENCE_GATE || e.getCursor().getType() == Material.BED || e.getCursor().getType() == Material.LADDER || e.getCursor().getType() == Material.JUNGLE_FENCE_GATE || e.getCursor().getType() == Material.JUNGLE_DOOR_ITEM || e.getCursor().getType() == Material.SIGN || e.getCursor().getType() == Material.SIGN_POST || e.getCursor().getType() == Material.WALL_SIGN || e.getCursor().getType() == Material.CACTUS || e.getCursor().getType() == Material.ENDER_CHEST || e.getCursor().getType() == Material.PISTON_BASE
              || e.getCursor().getType() == Material.TNT || e.getCursor().getType() == Material.AIR) {
        e.setCancelled(true);
        return;
      }
      if (displayName.equalsIgnoreCase(ChatManager.colorMessage("Menus.Option-Menu.Floor-Option"))) {
        arena.getPlotManager().getPlot(player).changeFloor(e.getCursor().getType(), e.getCursor().getData().getData());
        player.sendMessage(ChatManager.colorMessage("Menus.Option-Menu.Floor-Changed"));
        e.getCursor().setAmount(0);
        e.getCursor().setType(Material.AIR);
        e.getCurrentItem().setType(Material.AIR);
        player.closeInventory();
        for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
          if (entity.getType() == EntityType.DROPPED_ITEM) {
            entity.remove();
          }
        }
      }
    } catch (Exception ex){
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPreCommand(PlayerCommandPreprocessEvent event) {
    try {
      if (ArenaRegistry.getArena(event.getPlayer()) == null) return;
      for (String string : ConfigPreferences.getWhitelistedCommands()) {
        if (event.getMessage().contains(string)) return;
      }
      if (event.getPlayer().isOp() || event.getPlayer().hasPermission("buildbattle.admin")) return;
      if (event.getMessage().contains("leave") || event.getMessage().contains("stats")) return;
      event.setCancelled(true);
      event.getPlayer().sendMessage(ChatManager.colorMessage("In-Game.Only-Command-Ingame-Is-Leave"));
    } catch (Exception ex){
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onBucketEmpty(PlayerBucketEmptyEvent event) {
    try {
      Arena arena = ArenaRegistry.getArena(event.getPlayer());
      if (arena == null) return;
      ArenaPlot buildPlot = arena.getPlotManager().getPlot(event.getPlayer());
      if (buildPlot == null) return;
      if (!buildPlot.getCuboid().isIn(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation())) event.setCancelled(true);
    } catch (Exception ex){
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onBlockSpread(BlockSpreadEvent event) {
    try {
      for (Arena arena : ArenaRegistry.getArenas()) {
        if (arena.getPlotManager().getPlots().size() != 0 && arena.getPlotManager().getPlots().get(0) != null) {
          if (arena.getPlotManager().getPlots().get(0).getCuboid().getCenter().getWorld().getName().equalsIgnoreCase(event.getBlock().getWorld().getName())) {
            if (event.getSource().getType() == Material.FIRE) event.setCancelled(true);
          }
        }
      }
    } catch (Exception ex){
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onCreatureSpawn(CreatureSpawnEvent event) {
    try {
      for (Arena arena : ArenaRegistry.getArenas()) {
        if (arena.getStartLocation() != null && event.getEntity().getWorld().equals(arena.getStartLocation().getWorld())) {
          if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
          if (event.getEntity().getType() == EntityType.WITHER || ConfigPreferences.isMobSpawningDisabled()) {
            event.setCancelled(true);
            return;
          }
          for (ArenaPlot buildplot : arena.getPlotManager().getPlots()) {
            if (buildplot.getCuboid().isInWithMarge(event.getEntity().getLocation(), 10)) {
              event.setCancelled(true);
              return;
            }
            if (buildplot.getCuboid().isInWithMarge(event.getEntity().getLocation(), 1)) {
              if (buildplot.getEntities() >= ConfigPreferences.getMaxMobs()) {
                //todo maybe only for spawner player?
                for (UUID u : buildplot.getOwners()) {
                  plugin.getServer().getPlayer(u).sendMessage(ChatManager.colorMessage("In-Game.Max-Entities-Limit-Reached"));
                }
                event.setCancelled(true);
                return;
              } else {
                buildplot.addEntity();
                event.setCancelled(false);
                new BuildBattleEntity(event.getEntity()).toggleMoveable();
              }
            }
          }
        }
      }
    } catch (Exception ex){
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onLeavesDecay(LeavesDecayEvent event) {
    for (Arena arena : ArenaRegistry.getArenas()) {
      for (ArenaPlot buildPlot : arena.getPlotManager().getPlots()) {
        if (buildPlot.getCuboid().isInWithMarge(event.getBlock().getLocation(), 5)) {
          event.setCancelled(true);
        }
      }
    }
  }


  @EventHandler
  public void onPlayerDropItem(PlayerDropItemEvent event) {
    try {
      if (ArenaRegistry.getArena(event.getPlayer()) == null) return;
      if (event.getItemDrop().getItemStack() == null) return;
      ItemStack drop = event.getItemDrop().getItemStack();
      if (!drop.hasItemMeta()) return;
      if (!drop.getItemMeta().hasDisplayName()) return;
      if (drop.getItemMeta().getDisplayName().equals(ChatManager.colorMessage("Menus.Option-Menu.Inventory-Name")) || VoteItems.getPoints(drop) != 0)
        event.setCancelled(true);
    } catch (Exception ex){
      new ReportedException(plugin, ex);
    }
  }


  @EventHandler(priority = EventPriority.HIGH)
  public void onBreak(BlockBreakEvent event) {
    try {
      Arena arena = ArenaRegistry.getArena(event.getPlayer());
      if (arena == null) return;
      if (arena.getArenaState() != ArenaState.IN_GAME) {
        event.setCancelled(true);
        return;
      }
      if (arena.isVoting()) {
        event.setCancelled(true);
        return;
      }
      if (arena.getBlacklist().contains(event.getBlock().getType())) {
        event.setCancelled(true);
        return;
      }
      User user = UserManager.getUser(event.getPlayer().getUniqueId());
      ArenaPlot buildPlot = (ArenaPlot) user.getObject("plot");
      if (buildPlot == null) {
        event.setCancelled(true);
        return;
      }
      if (buildPlot.getCuboid().isIn(event.getBlock().getLocation())) {
        UserManager.getUser(event.getPlayer().getUniqueId()).addInt("blocksbroken", 1);
        return;
      }
      event.setCancelled(true);
    } catch (Exception ex){
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlace(BlockPlaceEvent event) {
    try {
      Arena arena = ArenaRegistry.getArena(event.getPlayer());
      if (arena == null) return;
      if (arena.getArenaState() != ArenaState.IN_GAME) {
        event.setCancelled(true);
        return;
      }
      if (arena.getBlacklist().contains(event.getBlock().getType())) {
        event.setCancelled(true);
        return;
      }
      if (arena.isVoting()) {
        event.setCancelled(true);
        return;
      }
      User user = UserManager.getUser(event.getPlayer().getUniqueId());
      ArenaPlot buildPlot = (ArenaPlot) user.getObject("plot");
      if (buildPlot == null) {
        event.setCancelled(true);
        return;
      }
      if (buildPlot.getCuboid().isIn(event.getBlock().getLocation())) {
        UserManager.getUser(event.getPlayer().getUniqueId()).addInt("blocksplaced", 1);
        return;
      }
      event.setCancelled(true);
    } catch (Exception ex){
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    try {
      Arena arena = ArenaRegistry.getArena((Player) event.getWhoClicked());
      if (arena == null) return;
      if (arena.getArenaState() != ArenaState.IN_GAME) {
        event.setCancelled(true);
        return;
      }
      if (!arena.isVoting()) {
        return;
      }
      event.setCancelled(true);
    } catch (Exception ex){
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onChat(AsyncPlayerChatEvent event) {
    if (ArenaRegistry.getArena(event.getPlayer()) == null) {
      for (Player player : event.getRecipients()) {
        if (ArenaRegistry.getArena(event.getPlayer()) == null) return;
        event.getRecipients().remove(player);
      }
    }
    event.getRecipients().clear();
    event.getRecipients().addAll(ArenaRegistry.getArena(event.getPlayer()).getPlayers());
  }

  @EventHandler
  public void onNPCClick(PlayerInteractEntityEvent e) {
    try {
      if (e.getHand() == EquipmentSlot.OFF_HAND) return;
      if (e.getPlayer().getInventory().getItemInMainHand() == null || e.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) return;
      if (e.getRightClicked() instanceof Villager && e.getRightClicked().getCustomName() != null && e.getRightClicked().getCustomName().equalsIgnoreCase(ChatManager.colorMessage("In-Game.NPC.Floor-Change-NPC-Name"))) {
        Arena arena = ArenaRegistry.getArena(e.getPlayer());
        if (arena == null) return;
        if (!e.getPlayer().getInventory().getItemInMainHand().getType().isBlock()) return;
        if (arena.getBlacklist().contains(e.getPlayer().getInventory().getItemInMainHand().getType())) return;
        if (arena.getArenaState() != ArenaState.IN_GAME) return;
        if (arena.isVoting()) return;
        arena.getPlotManager().getPlot(e.getPlayer()).changeFloor(e.getPlayer().getInventory().getItemInMainHand().getType(), e.getPlayer().getInventory().getItemInMainHand().getData().getData());
        e.getPlayer().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Menus.Option-Menu.Floor-Changed"));
      }
    } catch (Exception ex){
      new ReportedException(plugin, ex);
    }
  }

}
