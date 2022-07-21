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

package plugily.projects.buildbattle.arena;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityDismountEvent;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.PluginArenaEvents;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyEntityPickupItemEvent;
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

  @EventHandler
  public void onArmorStandEject(EntityDismountEvent e) {
    if(!(e.getEntity() instanceof ArmorStand)
        || !"BuildBattleArmorStand".equals(e.getEntity().getCustomName())) {
      return;
    }
    if(!(e.getDismounted() instanceof Player)) {
      return;
    }
    if(e.getDismounted().isDead()) {
      e.getEntity().remove();
    }
    // we could use setCancelled here but for 1.12 support we cannot (no api)
    e.getDismounted().addPassenger(e.getEntity());
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBreak(BlockBreakEvent event) {
    BaseArena arena = plugin.getArenaRegistry().getArena(event.getPlayer());
    if(arena == null) {
      return;
    }
    if(arena.getArenaState() != ArenaState.IN_GAME || (arena instanceof BuildArena && arena.getArenaInGameStage() == BaseArena.ArenaInGameStage.PLOT_VOTING)
        || plugin.getBlacklistManager().getItemList().contains(event.getBlock().getType())) {
      event.setCancelled(true);
      return;
    }
    User user = plugin.getUserManager().getUser(event.getPlayer());
    Plot buildPlot = arena.getPlotManager().getPlot(event.getPlayer());

    if(buildPlot != null && buildPlot.getCuboid() != null && buildPlot.getCuboid().isIn(event.getBlock().getLocation())) {
      user.adjustStatistic("BLOCKS_BROKEN", 1);
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
    if(arena.getArenaState() != ArenaState.IN_GAME || plugin.getBlacklistManager().getItemList().contains(event.getBlock().getType())
        || (arena instanceof BuildArena && arena.getArenaInGameStage() == BaseArena.ArenaInGameStage.PLOT_VOTING)) {
      event.setCancelled(true);
      return;
    }
    if(arena instanceof GuessArena && !event.getPlayer().equals(((GuessArena) arena).getCurrentBuilder())) {
      event.setCancelled(true);
      return;
    }
    User user = plugin.getUserManager().getUser(event.getPlayer());
    Plot buildPlot = arena.getPlotManager().getPlot(event.getPlayer());

    if(buildPlot != null && buildPlot.getCuboid() != null && buildPlot.getCuboid().isIn(event.getBlock().getLocation())) {
      user.adjustStatistic("BLOCKS_PLACED", 1);
      return;
    }
    event.setCancelled(true);
  }

  @EventHandler
  public void onItemFrameRotate(PlayerInteractEntityEvent event) {
    if(event.getRightClicked().getType() == EntityType.ITEM_FRAME && ((ItemFrame) event.getRightClicked()).getItem().getType() != Material.AIR) {
      for(PluginArena arena : plugin.getArenaRegistry().getArenas()) {
        if(!(arena instanceof BaseArena)) {
          continue;
        }
        for(Plot buildPlot : ((BaseArena) arena).getPlotManager().getPlots()) {
          if(buildPlot.getCuboid() != null && !buildPlot.getCuboid().isInWithMarge(event.getRightClicked().getLocation(), -1) && buildPlot.getCuboid().isIn(event.getRightClicked().getLocation())) {
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

    if(hand.getType() == Material.AIR || plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      return;
    }

    String customName = event.getRightClicked().getCustomName();

    if(customName != null && customName.equalsIgnoreCase(new MessageBuilder("IN_GAME_MESSAGES_PLOT_NPC_NAME").asKey().build())) {
      BaseArena arena = plugin.getArenaRegistry().getArena(event.getPlayer());

      if(arena == null || arena.getArenaState() != ArenaState.IN_GAME) {
        return;
      }

      if(arena instanceof BuildArena && arena.getArenaInGameStage() == BaseArena.ArenaInGameStage.PLOT_VOTING) {
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
        playerPlot.changeFloor(material, hand.getData().getData());
        new MessageBuilder("MENU_OPTION_CONTENT_FLOOR_CHANGED").asKey().player(event.getPlayer()).sendPlayer();
      }
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

    if(arena.getArenaState() != ArenaState.IN_GAME || event.getClickedBlock().getType() == XMaterial.ENDER_CHEST.parseMaterial()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onMinecartMove(VehicleMoveEvent event) {
    Vehicle vehicle = event.getVehicle();
    if(vehicle.getType() != EntityType.MINECART) {
      return;
    }
    for(PluginArena arena : plugin.getArenaRegistry().getArenas()) {
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
    for(PluginArena arena : plugin.getArenaRegistry().getArenas()) {
      if(!(arena instanceof BaseArena)) {
        continue;
      }
      for(Plot buildPlot : ((BaseArena) arena).getPlotManager().getPlots()) {
        if(buildPlot.getCuboid() != null && buildPlot.getCuboid().isInWithMarge(event.getBlock().getLocation(), 5)) {
          event.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void onPistonRetractEvent(BlockPistonRetractEvent event) {
    for(PluginArena arena : plugin.getArenaRegistry().getArenas()) {
      if(!(arena instanceof BaseArena)) {
        continue;
      }
      for(Plot buildPlot : ((BaseArena) arena).getPlotManager().getPlots()) {
        for(Block block : event.getBlocks()) {
          if(buildPlot.getCuboid() != null && !buildPlot.getCuboid().isInWithMarge(block.getLocation(), -1) && buildPlot.getCuboid().isIn(event.getBlock().getLocation())) {
            event.setCancelled(true);
          }
        }
      }
    }
  }

  @EventHandler
  public void onLeavesDecay(LeavesDecayEvent event) {
    for(PluginArena arena : plugin.getArenaRegistry().getArenas()) {
      if(!(arena instanceof BaseArena)) {
        continue;
      }
      for(Plot buildPlot : ((BaseArena) arena).getPlotManager().getPlots()) {
        if(buildPlot.getCuboid() != null && buildPlot.getCuboid().isInWithMarge(event.getBlock().getLocation(), 5)) {
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

    for(PluginArena arena : plugin.getArenaRegistry().getArenas()) {
      if(!(arena instanceof BaseArena)) {
        continue;
      }
      if(((BaseArena) arena).getPlotManager().getPlots().isEmpty()) {
        continue;
      }

      Plot first = ((BaseArena) arena).getPlotManager().getPlots().get(0);
      if(first == null || (first.getCuboid() != null && !entityLoc.getWorld().equals(first.getCuboid().getCenter().getWorld()))) {
        continue;
      }

      if(event.getEntity().getType() == EntityType.WITHER || !plugin.getConfigPreferences().getOption("MOB_SPAWN")) {
        event.setCancelled(true);
        return;
      }

      for(Plot plot : ((BaseArena) arena).getPlotManager().getPlots()) {
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

    for(PluginArena arena : plugin.getArenaRegistry().getArenas()) {
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

    for(PluginArena arena : plugin.getArenaRegistry().getArenas()) {
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
    BaseArena arena = plugin.getArenaRegistry().getArena((Player) event.getEntity());
    if(arena == null || arena.getArenaState() != ArenaState.IN_GAME) {
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
    if(arena == null || arena.getArenaState() != ArenaState.IN_GAME) {
      return;
    }
    event.setCancelled(true);
    Player player = (Player) event.getEntity();
    if(player.getLocation().getY() < 1) {
      Plot plot = arena.getPlotManager().getPlot(player);
      if(plot != null) {
        player.teleport(plot.getTeleportLocation());
      }
    }
  }

  @EventHandler
  public void onTNTInteract(PlugilyPlayerInteractEvent event) {
    if(event.getClickedBlock() == null || VersionUtils.checkOffHand(event.getHand())) {
      return;
    }
    BaseArena arena = plugin.getArenaRegistry().getArena(event.getPlayer());
    if(arena == null || VersionUtils.getItemInHand(event.getPlayer()).getType() != Material.FLINT_AND_STEEL) {
      return;
    }
    if(event.getClickedBlock().getType() == Material.TNT) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onTNTExplode(EntityExplodeEvent event) {
    for(PluginArena arena : plugin.getArenaRegistry().getArenas()) {
      if(!(arena instanceof BaseArena)) {
        continue;
      }
      for(Plot buildPlot : ((BaseArena) arena).getPlotManager().getPlots()) {
        if(buildPlot.getCuboid() != null && buildPlot.getCuboid().isInWithMarge(event.getEntity().getLocation(), 5)) {
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

    for(PluginArena arena : plugin.getArenaRegistry().getArenas()) {
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
    if(plugin.getArenaRegistry().getArena(player) == null) {
      return;
    }
    event.setCancelled(true);
    player.setFoodLevel(20);
  }


  @EventHandler
  public void onPistonExtendEvent(BlockPistonExtendEvent event) {
    Location blockLoc = event.getBlock().getLocation();
    for(PluginArena arena : plugin.getArenaRegistry().getArenas()) {
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
  public void onArrowPickup(PlugilyPlayerPickupArrow e) {
    if(plugin.getArenaRegistry().isInArena(e.getPlayer())) {
      e.getItem().remove();
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onItemPickup(PlugilyEntityPickupItemEvent e) {
    if(!(e.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player) e.getEntity();
    BaseArena pluginBaseArena = plugin.getArenaRegistry().getArena(player);
    if(pluginBaseArena == null) {
      return;
    }
    e.setCancelled(true);

    // User user = plugin.getUserManager().getUser(player);
    // if(user.isSpectator() || pluginBaseArena.getArenaState() != ArenaState.IN_GAME) {
    //  return;
    // }
  }


  @EventHandler
  public void onItemMove(InventoryClickEvent e) {
    org.bukkit.entity.HumanEntity who = e.getWhoClicked();

    if(who instanceof Player && plugin.getArenaRegistry().isInArena((Player) who)) {
      if(plugin.getArenaRegistry().getArena((Player) who).getArenaState() != ArenaState.IN_GAME) {
        if(e.getClickedInventory() == who.getInventory()) {
          if(e.getView().getType() == InventoryType.CRAFTING
              || e.getView().getType() == InventoryType.PLAYER) {
            e.setResult(Event.Result.DENY);
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
    GuessArena gameArena = (GuessArena) arena;
    if(gameArena.getWhoGuessed().contains(player)) {
      event.setCancelled(true);
      new MessageBuilder("IN_GAME_MESSAGES_PLOT_GTB_THEME_GUESS_CANT_TALK").asKey().arena(gameArena).player(player).sendPlayer();
      return;
    }
    if(player == gameArena.getCurrentBuilder()) {
      event.setCancelled(true);
      new MessageBuilder("IN_GAME_MESSAGES_PLOT_GTB_THEME_GUESS_BUILDER").asKey().arena(gameArena).player(player).sendPlayer();
      return;
    }
    if(gameArena.getCurrentTheme() == null || !gameArena.getCurrentTheme().getTheme().equalsIgnoreCase(event.getMessage())) {
      return;
    }
    event.setCancelled(true);
    gameArena.broadcastPlayerGuessed(player);
  }

}
