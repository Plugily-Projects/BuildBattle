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

package plugily.projects.buildbattle.arena;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.arena.PluginArenaEvents;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEntityEvent;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerPickupArrow;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;

/**
 * @author Plajer
 * <p>Created at 13.03.2018
 */
public class ArenaEvents extends PluginArenaEvents {

  private final Main plugin;

  public ArenaEvents(Main plugin) {
    super(plugin);
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBreak(BlockBreakEvent event) {
    BaseArena arena = plugin.getArenaRegistry().getArena(event.getPlayer());
    if(arena == null) {
      return;
    }
    if(arena.getArenaState() != IArenaState.IN_GAME || (arena instanceof BuildArena && arena.getArenaInGameState() == BaseArena.ArenaInGameState.PLOT_VOTING)
        || plugin.getBlacklistManager().getItemList().contains(event.getBlock().getType())) {
      event.setCancelled(true);
      return;
    }

    Plot buildPlot = arena.getPlotManager().getPlot(event.getPlayer());

    if(buildPlot != null && buildPlot.getCuboid() != null && buildPlot.getCuboid().isIn(event.getBlock().getLocation())) {
      plugin.getUserManager().getUser(event.getPlayer()).adjustStatistic("BLOCKS_BROKEN", 1);
      return;
    }
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlace(BlockPlaceEvent event) {
    BaseArena arena = plugin.getArenaRegistry().getArena(event.getPlayer());
    if(arena == null) {
      return;
    }
    if(arena.getArenaState() != IArenaState.IN_GAME || plugin.getBlacklistManager().getItemList().contains(event.getBlock().getType())
        || (arena instanceof BuildArena && arena.getArenaInGameState() == BaseArena.ArenaInGameState.PLOT_VOTING)) {
      event.setCancelled(true);
      return;
    }
    if(arena instanceof GuessArena && !((GuessArena) arena).getCurrentBuilders().contains(event.getPlayer())) {
      event.setCancelled(true);
      return;
    }

    Plot buildPlot = arena.getPlotManager().getPlot(event.getPlayer());

    if(buildPlot != null && buildPlot.getCuboid() != null && buildPlot.getCuboid().isIn(event.getBlock().getLocation())) {
      plugin.getUserManager().getUser(event.getPlayer()).adjustStatistic("BLOCKS_PLACED", 1);
      return;
    }
    event.setCancelled(true);
  }

  @EventHandler
  public void onItemFrameRotate(PlayerInteractEntityEvent event) {
    if(event.getRightClicked().getType() == EntityType.ITEM_FRAME && ((ItemFrame) event.getRightClicked()).getItem().getType() != Material.AIR) {
      Location entityLocation = event.getRightClicked().getLocation();

      for(IPluginArena arena : plugin.getArenaRegistry().getArenas()) {
        if(!(arena instanceof BaseArena)) {
          continue;
        }
        for(Plot buildPlot : ((BaseArena) arena).getPlotManager().getPlots()) {
          if(buildPlot.getCuboid() != null && !buildPlot.getCuboid().isInWithMarge(entityLocation, -1) && buildPlot.getCuboid().isIn(entityLocation)) {
            event.setCancelled(true);
          }
        }
      }
    }
  }

  @EventHandler
  public void onNPCClick(PlugilyPlayerInteractEntityEvent event) {
    if(VersionUtils.checkOffHand(event.getHand()) || event.getRightClicked().getType() != EntityType.VILLAGER) {
      return;
    }

    ItemStack hand = VersionUtils.getItemInHand(event.getPlayer());

    if(hand.getType() == Material.AIR) {
      return;
    }

    BaseArena arena = plugin.getArenaRegistry().getArena(event.getPlayer());

    if(arena == null || arena.getArenaState() != IArenaState.IN_GAME) {
      return;
    }

    if(arena instanceof BuildArena && arena.getArenaInGameState() == BaseArena.ArenaInGameState.PLOT_VOTING) {
      return;
    }

    if(plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      return;
    }

    String customName = event.getRightClicked().getCustomName();

    if(customName == null) {
      return;
    }

    // Citizens includes 1 more color before name so we removes all
    customName = ChatColor.stripColor(customName);

    if(!customName.equalsIgnoreCase(ChatColor.stripColor(new MessageBuilder("IN_GAME_MESSAGES_PLOT_NPC_NAME").asKey().build()))) {
      return;
    }

    Material material = hand.getType();

    if(material != XMaterial.WATER_BUCKET.parseMaterial() && material != XMaterial.LAVA_BUCKET.parseMaterial()
        && !(material.isBlock() && material.isSolid() && material.isOccluding())) {
      new MessageBuilder("IN_GAME_MESSAGES_PLOT_PERMISSION_FLOOR_ITEM").asKey().player(event.getPlayer()).sendPlayer();
      return;
    }

    if(plugin.getBlacklistManager().getFloorList().contains(material)) {
      new MessageBuilder("IN_GAME_MESSAGES_PLOT_PERMISSION_FLOOR_ITEM").asKey().player(event.getPlayer()).sendPlayer();
      return;
    }

    Plot playerPlot = arena.getPlotManager().getPlot(event.getPlayer());

    if(playerPlot != null) {

      // Prevent "Legacy material support initialisation"
      playerPlot.changeFloor(material, ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_12_R1) ? hand.getData().getData() : 0);
      new MessageBuilder("MENU_OPTION_CONTENT_FLOOR_CHANGED").asKey().player(event.getPlayer()).sendPlayer();
    }
  }

  @EventHandler
  public void onEnderchestClick(PlugilyPlayerInteractEvent event) {
    if(event.getClickedBlock() == null)
      return;

    BaseArena arena = plugin.getArenaRegistry().getArena(event.getPlayer());
    if(arena == null) {
      return;
    }

    if(arena.getArenaState() != IArenaState.IN_GAME || event.getClickedBlock().getType() == XMaterial.ENDER_CHEST.parseMaterial()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onMinecartMove(VehicleMoveEvent event) {
    Vehicle vehicle = event.getVehicle();
    if(vehicle.getType() != EntityType.MINECART) {
      return;
    }
    for(IPluginArena arena : plugin.getArenaRegistry().getArenas()) {
      if(!(arena instanceof BaseArena)) {
        continue;
      }
      for(Plot buildPlot : ((BaseArena) arena).getPlotManager().getPlots()) {
        if(buildPlot.getCuboid() != null && !buildPlot.getCuboid().isInWithMarge(event.getTo(), -1) && buildPlot.getCuboid().isIn(event.getTo())) {
          ((Minecart) vehicle).setMaxSpeed(0);
          vehicle.setVelocity(vehicle.getVelocity().zero());
        }
      }
    }
  }

  @EventHandler
  public void onIgniteEvent(BlockIgniteEvent event) {
    Location blockLocation = event.getBlock().getLocation();

    for(IPluginArena arena : plugin.getArenaRegistry().getArenas()) {
      if(!(arena instanceof BaseArena)) {
        continue;
      }
      for(Plot buildPlot : ((BaseArena) arena).getPlotManager().getPlots()) {
        if(buildPlot.getCuboid() != null && buildPlot.getCuboid().isInWithMarge(blockLocation, 5)) {
          event.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void onPistonRetractEvent(BlockPistonRetractEvent event) {
    Location blockLocation = event.getBlock().getLocation();

    for(IPluginArena arena : plugin.getArenaRegistry().getArenas()) {
      if(!(arena instanceof BaseArena)) {
        continue;
      }
      for(Plot buildPlot : ((BaseArena) arena).getPlotManager().getPlots()) {
        for(Block block : event.getBlocks()) {
          if(buildPlot.getCuboid() != null && !buildPlot.getCuboid().isInWithMarge(block.getLocation(), -1) && buildPlot.getCuboid().isIn(blockLocation)) {
            event.setCancelled(true);
          }
        }
      }
    }
  }

  @EventHandler
  public void onLeavesDecay(LeavesDecayEvent event) {
    Location blockLocation = event.getBlock().getLocation();

    for(IPluginArena arena : plugin.getArenaRegistry().getArenas()) {
      if(!(arena instanceof BaseArena)) {
        continue;
      }
      for(Plot buildPlot : ((BaseArena) arena).getPlotManager().getPlots()) {
        if(buildPlot.getCuboid() != null && buildPlot.getCuboid().isInWithMarge(blockLocation, 5)) {
          event.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void onBucketEmpty(PlayerBucketEmptyEvent event) {
    BaseArena arena = plugin.getArenaRegistry().getArena(event.getPlayer());
    if(arena == null) {
      return;
    }

    Plot buildPlot = arena.getPlotManager().getPlot(event.getPlayer());

    if(buildPlot != null && buildPlot.getCuboid() != null && !buildPlot.getCuboid().isIn(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onCreatureSpawn(CreatureSpawnEvent event) {
    if(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
      return;
    }

    int maxMobPerPlot = plugin.getConfig().getInt("Mob.Max-Amount", 20);
    Location entityLoc = event.getEntity().getLocation();

    for(IPluginArena arena : plugin.getArenaRegistry().getArenas()) {
      if(!(arena instanceof BaseArena)) {
        continue;
      }

      BaseArena baseArena = (BaseArena) arena;

      if(baseArena.getPlotManager().getPlots().isEmpty()) {
        continue;
      }

      Plot first = baseArena.getPlotManager().getPlots().get(0);
      if(first == null || (first.getCuboid() != null && !entityLoc.getWorld().equals(first.getCuboid().getCenter().getWorld()))) {
        continue;
      }

      if(event.getEntity().getType() == EntityType.WITHER || !plugin.getConfigPreferences().getOption("MOB_SPAWN")) {
        event.setCancelled(true);
        return;
      }

      for(Plot plot : baseArena.getPlotManager().getPlots()) {
        if(plot.getCuboid() != null && plot.getCuboid().isInWithMarge(entityLoc, 1)) {
          if(plot.getEntities() >= maxMobPerPlot) {
            plot.getMembers().forEach(player -> new MessageBuilder("IN_GAME_MESSAGES_PLOT_LIMIT_ENTITIES").asKey().arena(arena).player(player).sendPlayer());
            event.setCancelled(true);
            return;
          }

          for(String entityNames : plugin.getConfig().getStringList("Mob.Restricted")) {
            if(event.getEntity().getType().name().equalsIgnoreCase(entityNames)) {
              event.setCancelled(true);
              return;
            }
          }

          plot.addEntity();
          event.setCancelled(false);

          if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
            event.getEntity().setAI(false);
          }
        }
      }
    }
  }

  @EventHandler
  public void onBlockSpread(BlockSpreadEvent event) {
    if(event.getSource().getType() != Material.FIRE)
      return;

    for(IPluginArena arena : plugin.getArenaRegistry().getArenas()) {
      if(!(arena instanceof BaseArena)) {
        continue;
      }
      BaseArena base = (BaseArena) arena;
      if(!base.getPlotManager().getPlots().isEmpty()) {
        Plot plot = base.getPlotManager().getPlots().get(0);

        if(plot != null && plot.getCuboid() != null
            && event.getBlock().getWorld().equals(plot.getCuboid().getCenter().getWorld())) {
          event.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void onDispense(BlockDispenseEvent event) {
    Location blockLoc = event.getBlock().getLocation();

    for(IPluginArena arena : plugin.getArenaRegistry().getArenas()) {
      if(!(arena instanceof BaseArena)) {
        continue;
      }
      for(Plot buildPlot : ((BaseArena) arena).getPlotManager().getPlots()) {
        if(buildPlot.getCuboid() != null && !buildPlot.getCuboid().isInWithMarge(blockLoc, -1) && buildPlot.getCuboid().isInWithMarge(blockLoc, 5)) {
          event.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void onTreeGrow(StructureGrowEvent event) {
    BaseArena arena = plugin.getArenaRegistry().getArena(event.getPlayer());
    if(arena == null) {
      return;
    }
    Plot buildPlot = arena.getPlotManager().getPlot(event.getPlayer());
    if(buildPlot == null || buildPlot.getCuboid() == null) {
      return;
    }
    for(BlockState blockState : event.getBlocks()) {
      if(!buildPlot.getCuboid().isIn(blockState.getLocation())) {
        blockState.setType(Material.AIR);
      }
    }
  }


  @EventHandler
  public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
    if(event.getEntity().getType() != EntityType.PLAYER) {
      return;
    }
    BaseArena arena = plugin.getArenaRegistry().getArena((Player) event.getEntity());
    if(arena == null || arena.getArenaState() != IArenaState.IN_GAME) {
      return;
    }
    if(event.getEntity().getType() != EntityType.PLAYER) {
      return;
    }
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onDamage(EntityDamageEvent event) {
    if(event.getEntity().getType() != EntityType.PLAYER) {
      return;
    }
    BaseArena arena = plugin.getArenaRegistry().getArena((Player) event.getEntity());
    if(arena == null || arena.getArenaState() != IArenaState.IN_GAME) {
      return;
    }
    event.setCancelled(true);
    Player player = (Player) event.getEntity();
    if(player.getLocation().getY() < 1) {
      Plot plot = arena.getPlotManager().getPlot(player);
      if(plot != null) {
        VersionUtils.teleport(player, plot.getTeleportLocation());
      }
    }
  }

  @EventHandler
  public void onTNTInteract(PlugilyPlayerInteractEvent event) {
    if(event.getClickedBlock() == null || VersionUtils.checkOffHand(event.getHand())) {
      return;
    }

    if(!plugin.getArenaRegistry().isInArena(event.getPlayer()) || VersionUtils.getItemInHand(event.getPlayer()).getType() != Material.FLINT_AND_STEEL) {
      return;
    }
    if(event.getClickedBlock().getType() == Material.TNT) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onTNTExplode(EntityExplodeEvent event) {
    Location entityLocation = event.getEntity().getLocation();

    for(IPluginArena arena : plugin.getArenaRegistry().getArenas()) {
      if(!(arena instanceof BaseArena)) {
        continue;
      }
      for(Plot buildPlot : ((BaseArena) arena).getPlotManager().getPlots()) {
        if(buildPlot.getCuboid() != null && buildPlot.getCuboid().isInWithMarge(entityLocation, 5)) {
          event.blockList().clear();
          event.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void onOtherBlockExplode(BlockExplodeEvent event) {
    Location blockLocation = event.getBlock().getLocation();

    for(IPluginArena arena : plugin.getArenaRegistry().getArenas()) {
      if(!(arena instanceof BaseArena)) {
        continue;
      }
      for(Plot buildPlot : ((BaseArena) arena).getPlotManager().getPlots()) {
        if(buildPlot.getCuboid() != null && buildPlot.getCuboid().isInWithMarge(blockLocation, 5)) {
          event.blockList().clear();
          event.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void onWaterFlowEvent(BlockFromToEvent event) {
    Location toBlock = event.getToBlock().getLocation();
    Location blockLoc = event.getBlock().getLocation();

    for(IPluginArena arena : plugin.getArenaRegistry().getArenas()) {
      if(!(arena instanceof BaseArena)) {
        continue;
      }
      for(Plot buildPlot : ((BaseArena) arena).getPlotManager().getPlots()) {
        if(buildPlot.getCuboid() != null) {
          if(!buildPlot.getCuboid().isIn(toBlock) && buildPlot.getCuboid().isIn(blockLoc)) {
            event.setCancelled(true);
          }
          if(!buildPlot.getCuboid().isInWithMarge(toBlock, -1) && buildPlot.getCuboid().isIn(toBlock)) {
            event.setCancelled(true);
          }
        }
      }
    }
  }

  @EventHandler
  public void onFoodChange(FoodLevelChangeEvent event) {
    if(event.getEntity().getType() != EntityType.PLAYER) {
      return;
    }
    Player player = (Player) event.getEntity();
    if(!plugin.getArenaRegistry().isInArena(player)) {
      return;
    }
    event.setCancelled(true);
    player.setFoodLevel(20);
  }


  @EventHandler
  public void onPistonExtendEvent(BlockPistonExtendEvent event) {
    Location blockLoc = event.getBlock().getLocation();
    for(IPluginArena arena : plugin.getArenaRegistry().getArenas()) {
      if(!(arena instanceof BaseArena)) {
        continue;
      }
      for(Plot buildPlot : ((BaseArena) arena).getPlotManager().getPlots()) {
        if(buildPlot.getCuboid() != null && buildPlot.getCuboid().isIn(blockLoc)) {
          for(Block block : event.getBlocks()) {
            if(!buildPlot.getCuboid().isInWithMarge(block.getLocation(), -1)) {
              event.setCancelled(true);
            }
          }
        }
      }
    }
  }


  @EventHandler
  public void onArrowPickup(PlugilyPlayerPickupArrow event) {
    if(plugin.getArenaRegistry().isInArena(event.getPlayer())) {
      event.getItem().remove();
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onItemMove(InventoryClickEvent event) {
    HumanEntity humanEntity = event.getWhoClicked();

    if(humanEntity instanceof Player) {
      BaseArena baseArena = plugin.getArenaRegistry().getArena((Player) humanEntity);

      if(baseArena != null && baseArena.getArenaState() != IArenaState.IN_GAME) {
        if(event.getClickedInventory() == humanEntity.getInventory()) {
          if(event.getView().getType() == InventoryType.CRAFTING
              || event.getView().getType() == InventoryType.PLAYER) {
            event.setResult(Event.Result.DENY);
          }
        }
      }
    }
  }

  @EventHandler
  public void onGTBGuessChat(AsyncPlayerChatEvent event) {
    Player player = event.getPlayer();
    BaseArena arena = plugin.getArenaRegistry().getArena(player);
    if(!(arena instanceof GuessArena)) {
      return;
    }
    if(arena.getArenaState() != IArenaState.IN_GAME) {
      return;
    }
    if(arena.getSpectators().contains(player)) {
      return;
    }
    GuessArena gameArena = (GuessArena) arena;
    if(gameArena.getWhoGuessed().contains(player)) {
      event.setCancelled(true);
      new MessageBuilder("IN_GAME_MESSAGES_PLOT_GTB_THEME_GUESS_CANT_TALK").asKey().arena(gameArena).player(player).sendPlayer();
      return;
    }
    if(gameArena.getCurrentBuilders().contains(player)) {
      event.setCancelled(true);
      new MessageBuilder("IN_GAME_MESSAGES_PLOT_GTB_THEME_GUESS_BUILDER").asKey().arena(gameArena).player(player).sendPlayer();
      return;
    }
    if(gameArena.getCurrentBBTheme() == null || !gameArena.getCurrentBBTheme().getTheme().equalsIgnoreCase(event.getMessage())) {
      return;
    }
    event.setCancelled(true);
    gameArena.broadcastPlayerGuessed(player);
  }

  @EventHandler
  public void onPlayerDropItem(PlayerDropItemEvent event) {
    if(!plugin.getArenaRegistry().isInArena(event.getPlayer())) {
      return;
    }
    ItemStack drop = event.getItemDrop().getItemStack();
    if(plugin.getVoteItems().getPoints(drop) != 0) {
      event.setCancelled(true);
    }
  }

}
