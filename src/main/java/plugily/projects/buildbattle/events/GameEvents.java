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
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.item.ItemUtils;
import plugily.projects.buildbattle.ConfigPreferences;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.api.StatsStorage;
import plugily.projects.buildbattle.arena.ArenaManager;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.ArenaState;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.arena.impl.GuessTheBuildArena;
import plugily.projects.buildbattle.arena.impl.SoloArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.handlers.items.SpecialItem;
import plugily.projects.buildbattle.user.User;

/**
 * Created by Tom on 17/08/2015.
 */
public class GameEvents implements Listener {

  private final Main plugin;

  public GameEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onLeave(PlayerInteractEvent e) {
    if(e.getHand() == EquipmentSlot.OFF_HAND || e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.PHYSICAL) {
      return;
    }
    BaseArena arena = ArenaRegistry.getArena(e.getPlayer());
    if(arena == null) {
      return;
    }
    ItemStack itemStack = e.getPlayer().getInventory().getItemInMainHand();
    if(!ItemUtils.isItemStackNamed(itemStack)) {
      return;
    }
    SpecialItem item = plugin.getSpecialItemsRegistry().getRelatedSpecialItem(itemStack);
    if(item == null) {
      return;
    }
    if("Leave".equalsIgnoreCase(item.getName())) {
      e.setCancelled(true);
      if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
        plugin.getBungeeManager().connectToHub(e.getPlayer());
      } else {
        ArenaManager.leaveAttempt(e.getPlayer(), arena);
      }
    }
  }

  @EventHandler
  public void onOpenOptionMenu(PlayerInteractEvent e) {
    if(e.getHand() == EquipmentSlot.OFF_HAND || e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.PHYSICAL) {
      return;
    }
    ItemStack itemStack = e.getItem();
    if(!ItemUtils.isItemStackNamed(itemStack)) {
      return;
    }
    BaseArena arena = ArenaRegistry.getArena(e.getPlayer());
    if(arena == null || arena.getArenaState() != ArenaState.IN_GAME || arena instanceof SoloArena && ((SoloArena) arena).isVoting()) {
      return;
    }
    if(!plugin.getOptionsRegistry().getMenuItem().getItemMeta().getDisplayName().equalsIgnoreCase(itemStack.getItemMeta().getDisplayName())) {
      return;
    }
    e.getPlayer().openInventory(plugin.getOptionsRegistry().formatInventory());
  }

  @EventHandler
  public void onPistonExtendEvent(BlockPistonExtendEvent event) {
    for(BaseArena arena : ArenaRegistry.getArenas()) {
      for(Plot buildPlot : arena.getPlotManager().getPlots()) {
        for(Block block : event.getBlocks()) {
          if(!buildPlot.getCuboid().isInWithMarge(block.getLocation(), -1) && buildPlot.getCuboid().isIn(event.getBlock().getLocation())) {
            event.setCancelled(true);
          }
        }
      }
    }
  }

  @EventHandler
  public void onFoodChange(FoodLevelChangeEvent e) {
    if(!(e.getEntity().getType() == EntityType.PLAYER)) {
      return;
    }
    Player player = (Player) e.getEntity();
    if(ArenaRegistry.getArena(player) == null) {
      return;
    }
    e.setCancelled(true);
    player.setFoodLevel(20);
  }

  @EventHandler
  public void onWaterFlowEvent(BlockFromToEvent e) {
    for(BaseArena arena : ArenaRegistry.getArenas()) {
      for(Plot buildPlot : arena.getPlotManager().getPlots()) {
        if(!buildPlot.getCuboid().isIn(e.getToBlock().getLocation()) && buildPlot.getCuboid().isIn(e.getBlock().getLocation())) {
          e.setCancelled(true);
        }
        if(!buildPlot.getCuboid().isInWithMarge(e.getToBlock().getLocation(), -1) && buildPlot.getCuboid().isIn(e.getToBlock().getLocation())) {
          e.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void onTNTExplode(EntityExplodeEvent e) {
    for(BaseArena arena : ArenaRegistry.getArenas()) {
      for(Plot buildPlot : arena.getPlotManager().getPlots()) {
        if(buildPlot.getCuboid().isInWithMarge(e.getEntity().getLocation(), 0)) {
          e.blockList().clear();
          e.setCancelled(true);
        } else if(buildPlot.getCuboid().isInWithMarge(e.getEntity().getLocation(), 5)) {
          e.getEntity().getLocation().getBlock().setType(Material.TNT);
          e.blockList().clear();
          e.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void onTNTInteract(PlayerInteractEvent e) {
    if(e.getHand() == EquipmentSlot.OFF_HAND) {
      return;
    }
    Player player = e.getPlayer();
    BaseArena arena = ArenaRegistry.getArena(player);
    if(arena == null || player.getInventory().getItemInMainHand().getType() != Material.FLINT_AND_STEEL || e.getClickedBlock() == null) {
      return;
    }
    if(e.getClickedBlock().getType() == Material.TNT) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onEntityDamageEntity(EntityDamageByEntityEvent e) {
    if(e.getEntity().getType() != EntityType.PLAYER) {
      return;
    }
    Player player = (Player) e.getEntity();
    BaseArena arena = ArenaRegistry.getArena(player);
    if(arena != null) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onTreeGrow(StructureGrowEvent e) {
    BaseArena arena = ArenaRegistry.getArena(e.getPlayer());
    if(arena == null) {
      return;
    }
    Plot buildPlot = arena.getPlotManager().getPlot(e.getPlayer());
    if(buildPlot == null) {
      return;
    }
    for(BlockState blockState : e.getBlocks()) {
      if(!buildPlot.getCuboid().isIn(blockState.getLocation())) {
        blockState.setType(Material.AIR);
      }
    }
  }

  //todo weird code?
  @EventHandler
  public void onDispense(BlockDispenseEvent e) {
    for(BaseArena arena : ArenaRegistry.getArenas()) {
      for(Plot buildPlot : arena.getPlotManager().getPlots()) {
        if(!buildPlot.getCuboid().isInWithMarge(e.getBlock().getLocation(), -1) && buildPlot.getCuboid().isInWithMarge(e.getBlock().getLocation(), 5)) {
          e.setCancelled(true);
        }
      }
    }
  }

  @Deprecated
  //only a temporary code
  @EventHandler
  public void onPlayerHeadsClick(InventoryClickEvent e) {
    if(!ItemUtils.isItemStackNamed(e.getCurrentItem()) || !(e.getWhoClicked() instanceof Player)) {
      return;
    }
    BaseArena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
    if(arena == null) {
      return;
    }
    if(plugin.getOptionsRegistry().getPlayerHeadsRegistry().isHeadsMenu(e.getInventory())) {
      if(e.getCurrentItem().getType() != ItemUtils.PLAYER_HEAD_ITEM.getType()) {
        return;
      }
      e.getWhoClicked().getInventory().addItem(e.getCurrentItem().clone());
      e.setCancelled(true);
    }
  }

  @Deprecated
  @EventHandler
  public void onOptionItemClick(InventoryClickEvent e) {
    if(!(e.getWhoClicked() instanceof Player) || !ItemUtils.isItemStackNamed(e.getCurrentItem())) {
      return;
    }
    BaseArena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
    if(e.getCurrentItem().getType() != Material.NETHER_STAR || arena == null) {
      return;
    }
    if(!e.getCurrentItem().getItemMeta().getDisplayName().equals(plugin.getChatManager().colorMessage("Menus.Option-Menu.Option-Item"))) {
      return;
    }
    e.setResult(Event.Result.DENY);
    e.setCancelled(true);
  }

  @EventHandler
  public void onOptionItemClick(InventoryInteractEvent e) {
    if(!(e.getWhoClicked() instanceof Player) || !ItemUtils.isItemStackNamed(e.getWhoClicked().getItemOnCursor())) {
      return;
    }
    BaseArena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
    if(e.getWhoClicked().getItemOnCursor().getType() != Material.NETHER_STAR || arena == null) {
      return;
    }
    if(!e.getWhoClicked().getItemOnCursor().getItemMeta().getDisplayName().equals(plugin.getChatManager().colorMessage("Menus.Option-Menu.Option-Item"))) {
      return;
    }
    e.setResult(Event.Result.DENY);
    e.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPreCommand(PlayerCommandPreprocessEvent event) {
    if(ArenaRegistry.getArena(event.getPlayer()) == null) {
      return;
    }
    if(!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BLOCK_COMMANDS_IN_GAME)) {
      return;
    }
    String command = event.getMessage().substring(1);
    command = (command.indexOf(' ') >= 0 ? command.substring(0, command.indexOf(' ')) : command);
    for(String string : plugin.getConfigPreferences().getWhitelistedCommands()) {
      if(command.equalsIgnoreCase(string)) {
        return;
      }
    }
    if(event.getPlayer().isOp() || event.getPlayer().hasPermission("buildbattle.admin") || event.getPlayer().hasPermission("buildbattle.command.bypass")) {
      return;
    }
    if(command.equalsIgnoreCase("bb") || command.equalsIgnoreCase("buildbattle") || command.equalsIgnoreCase("bba") ||
        command.equalsIgnoreCase("buildbattleadmin")) {
      return;
    }
    event.setCancelled(true);
    event.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("In-Game.Only-Command-Ingame-Is-Leave"));
  }

  @EventHandler
  public void playerCommandExecution(PlayerCommandPreprocessEvent e) {
    if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.ENABLE_SHORT_COMMANDS)) {
      Player player = e.getPlayer();
      if(e.getMessage().equalsIgnoreCase("/start")) {
        player.performCommand("bba forcestart");
        e.setCancelled(true);
        return;
      }
      if(e.getMessage().equalsIgnoreCase("/leave")) {
        player.performCommand("bb leave");
        e.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onBucketEmpty(PlayerBucketEmptyEvent e) {
    BaseArena arena = ArenaRegistry.getArena(e.getPlayer());
    if(arena == null) {
      return;
    }
    Plot buildPlot = arena.getPlotManager().getPlot(e.getPlayer());
    if(buildPlot != null && !buildPlot.getCuboid().isIn(e.getBlockClicked().getRelative(e.getBlockFace()).getLocation())) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onBlockSpread(BlockSpreadEvent e) {
    for(BaseArena arena : ArenaRegistry.getArenas()) {
      if(!arena.getPlotManager().getPlots().isEmpty() && arena.getPlotManager().getPlots().get(0) != null) {
        if(arena.getPlotManager().getPlots().get(0).getCuboid() == null) {
          continue;
        }
        if(arena.getPlotManager().getPlots().get(0).getCuboid().getCenter().getWorld().equals(e.getBlock().getWorld())) {
          if(e.getSource().getType() == Material.FIRE) {
            e.setCancelled(true);
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onCreatureSpawn(CreatureSpawnEvent e) {
    for(BaseArena arena : ArenaRegistry.getArenas()) {
      if(arena.getPlotManager().getPlots().isEmpty() || arena.getPlotManager().getPlots().get(0) == null
          || !e.getEntity().getWorld().equals(arena.getPlotManager().getPlots().get(0).getCuboid().getCenter().getWorld())) {
        continue;
      }
      if(e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
        return;
      }
      if(e.getEntity().getType() == EntityType.WITHER || plugin.getConfig().getBoolean("Disable-Mob-Spawning-Completely", true)) {
        e.setCancelled(true);
        return;
      }
      for(Plot plot : arena.getPlotManager().getPlots()) {
        if(plot.getCuboid().isInWithMarge(e.getEntity().getLocation(), 1)) {
          if(plot.getEntities() >= plugin.getConfig().getInt("Mobs-Max-Amount-Per-Plot", 20)) {
            //todo maybe only for spawner player?
            for(Player p : plot.getOwners()) {
              p.sendMessage(plugin.getChatManager().colorMessage("In-Game.Max-Entities-Limit-Reached"));
            }
            e.setCancelled(true);
            return;
          }
          plot.addEntity();
          e.setCancelled(false);
          e.getEntity().setAI(false);
        }
      }
    }
  }

  @EventHandler
  public void onLeavesDecay(LeavesDecayEvent e) {
    for(BaseArena arena : ArenaRegistry.getArenas()) {
      for(Plot buildPlot : arena.getPlotManager().getPlots()) {
        if(buildPlot.getCuboid().isInWithMarge(e.getBlock().getLocation(), 5)) {
          e.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void onIgniteEvent(BlockIgniteEvent event) {
    for(BaseArena arena : ArenaRegistry.getArenas()) {
      for(Plot buildPlot : arena.getPlotManager().getPlots()) {
        if(buildPlot.getCuboid().isInWithMarge(event.getBlock().getLocation(), 5)) {
          event.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void onPistonRetractEvent(BlockPistonRetractEvent event) {
    for(BaseArena arena : ArenaRegistry.getArenas()) {
      for(Plot buildPlot : arena.getPlotManager().getPlots()) {
        for(Block block : event.getBlocks()) {
          if(!buildPlot.getCuboid().isInWithMarge(block.getLocation(), -1) && buildPlot.getCuboid().isIn(event.getBlock().getLocation())) {
            event.setCancelled(true);
          }
        }
      }
    }
  }

  @EventHandler
  public void onPlayerDropItem(PlayerDropItemEvent e) {
    if(ArenaRegistry.getArena(e.getPlayer()) == null) {
      return;
    }
    ItemStack drop = e.getItemDrop().getItemStack();
    if(!ItemUtils.isItemStackNamed(drop)) {
      return;
    }
    if(drop.getItemMeta().getDisplayName().equals(plugin.getChatManager().colorMessage("Menus.Option-Menu.Inventory-Name")) || plugin.getVoteItems().getPoints(drop) != 0) {
      e.setCancelled(true);
    }
  }


  @EventHandler(priority = EventPriority.HIGH)
  public void onBreak(BlockBreakEvent e) {
    BaseArena arena = ArenaRegistry.getArena(e.getPlayer());
    if(arena == null) {
      return;
    }
    if(arena.getArenaState() != ArenaState.IN_GAME || (arena instanceof SoloArena && ((SoloArena) arena).isVoting())
        || plugin.getConfigPreferences().getItemBlacklist().contains(e.getBlock().getType())) {
      e.setCancelled(true);
      return;
    }
    User user = plugin.getUserManager().getUser(e.getPlayer());
    Plot buildPlot = user.getCurrentPlot();
    if(buildPlot == null) {
      e.setCancelled(true);
      return;
    }
    if(buildPlot.getCuboid().isIn(e.getBlock().getLocation())) {
      user.addStat(StatsStorage.StatisticType.BLOCKS_BROKEN, 1);
      return;
    }
    e.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlace(BlockPlaceEvent e) {
    BaseArena arena = ArenaRegistry.getArena(e.getPlayer());
    if(arena == null) {
      return;
    }
    if(arena.getArenaState() != ArenaState.IN_GAME || plugin.getConfigPreferences().getItemBlacklist().contains(e.getBlock().getType())
        || (arena instanceof SoloArena && ((SoloArena) arena).isVoting())) {
      e.setCancelled(true);
      return;
    }
    if(arena instanceof GuessTheBuildArena && !e.getPlayer().equals(((GuessTheBuildArena) arena).getCurrentBuilder())) {
      e.setCancelled(true);
      return;
    }
    User user = plugin.getUserManager().getUser(e.getPlayer());
    Plot buildPlot = user.getCurrentPlot();
    if(buildPlot == null) {
      e.setCancelled(true);
      return;
    }
    if(buildPlot.getCuboid().isIn(e.getBlock().getLocation())) {
      user.addStat(StatsStorage.StatisticType.BLOCKS_PLACED, 1);
      return;
    }
    e.setCancelled(true);
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    BaseArena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
    if(arena == null) {
      return;
    }
    if(arena.getArenaState() != ArenaState.IN_GAME) {
      e.setCancelled(true);
      return;
    }
    if(arena instanceof SoloArena && !((SoloArena) arena).isVoting()) {
      return;
    }
    if(arena instanceof GuessTheBuildArena && e.getWhoClicked().equals(((GuessTheBuildArena) arena).getCurrentBuilder())) {
      return;
    }
    e.setCancelled(true);
  }

  @EventHandler
  public void onNPCClick(PlayerInteractEntityEvent e) {
    if(e.getHand() == EquipmentSlot.OFF_HAND || e.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
      return;
    }

    if(plugin.getUserManager().getUser(e.getPlayer()).isSpectator()) {
      return;
    }

    if(e.getRightClicked() instanceof Villager && e.getRightClicked().getCustomName() != null && e.getRightClicked().getCustomName().equalsIgnoreCase(plugin.getChatManager().colorMessage("In-Game.NPC.Floor-Change-NPC-Name"))) {
      BaseArena arena = ArenaRegistry.getArena(e.getPlayer());
      if(arena == null || arena.getArenaState() != ArenaState.IN_GAME) {
        return;
      }
      if(arena instanceof SoloArena && ((SoloArena) arena).isVoting()) {
        return;
      }
      Material material = e.getPlayer().getInventory().getItemInMainHand().getType();
      if(material != XMaterial.WATER_BUCKET.parseMaterial() && material != XMaterial.LAVA_BUCKET.parseMaterial()
          && !(material.isBlock() && material.isSolid() && material.isOccluding())) {
        return;
      }
      if(plugin.getConfigPreferences().getFloorBlacklist().contains(material)) {
        return;
      }
      arena.getPlotManager().getPlot(e.getPlayer()).changeFloor(material, e.getPlayer().getInventory().getItemInMainHand().getData().getData());
      e.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Floor.Floor-Changed"));
    }
  }

}
