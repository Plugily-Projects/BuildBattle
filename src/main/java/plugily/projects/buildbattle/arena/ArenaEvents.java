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

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.entity.EntityDismountEvent;
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.PluginArenaEvents;
import plugily.projects.minigamesbox.classic.handlers.items.SpecialItem;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.language.TitleBuilder;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyEntityPickupItemEvent;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerPickupArrow;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XSound;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.base.Base;
import plugily.projects.thebridge.arena.managers.ScoreboardManager;
import plugily.projects.thebridge.kits.level.ArcherKit;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Plajer
 *     <p>Created at 13.03.2018
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
    if (!(e.getEntity() instanceof ArmorStand)
        || !"TheBridgeArmorStand".equals(e.getEntity().getCustomName())) {
      return;
    }
    if (!(e.getDismounted() instanceof Player)) {
      return;
    }
    if (e.getDismounted().isDead()) {
      e.getEntity().remove();
    }
    // we could use setCancelled here but for 1.12 support we cannot (no api)
    e.getDismounted().addPassenger(e.getEntity());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockBreakEvent(BlockBreakEvent event) {
    Player player = event.getPlayer();
    BaseArena baseArena = plugin.getArenaRegistry().getArena(player);
    if (baseArena == null || baseArena.getArenaState() != ArenaState.IN_GAME) {
      return;
    }
    if (!canBuild(baseArena, player, event.getBlock().getLocation())) {
      event.setCancelled(true);
      return;
    }
    if (baseArena.getPlacedBlocks().contains(event.getBlock())) {
      baseArena.removePlacedBlock(event.getBlock());
      // Does not work?
      event.getBlock().getDrops().clear();
      // Alternative
      event.getBlock().setType(XMaterial.AIR.parseMaterial());
    }
    event.setCancelled(true);
  }

  @EventHandler
  public void onBuild(BlockPlaceEvent event) {
    Player player = event.getPlayer();
    BaseArena baseArena = plugin.getArenaRegistry().getArena(player);
    if (baseArena == null || baseArena.getArenaState() != ArenaState.IN_GAME) {
      return;
    }
    if (!canBuild(baseArena, player, event.getBlock().getLocation())) {
      event.setCancelled(true);
      return;
    }
    baseArena.addPlacedBlock(event.getBlock());
  }

  public boolean canBuild(BaseArena baseArena, Player player, Location location) {
    if (!baseArena.getArenaBorder().isIn(location)) {
      new MessageBuilder("IN_GAME_MESSAGES_ARENA_BUILD_BREAK")
          .asKey()
          .player(player)
          .arena(baseArena)
          .sendPlayer();
      return false;
    }
    for (Base base : baseArena.getBases()) {
      if (base.getBaseCuboid().isIn(location)) {
        new MessageBuilder("IN_GAME_MESSAGES_ARENA_BUILD_BREAK")
            .asKey()
            .player(player)
            .arena(baseArena)
            .sendPlayer();
        return false;
      }
    }
    return true;
  }

  private void rewardLastAttacker(BaseArena baseArena, Player victim) {
    if (baseArena.getHits().containsKey(victim)) {
      Player attacker = baseArena.getHits().get(victim);
      baseArena.removeHits(victim);
      plugin
          .getRewardsHandler()
          .performReward(attacker, plugin.getRewardsHandler().getRewardType("KILL"));
      plugin.getUserManager().addStat(attacker, plugin.getStatsStorage().getStatisticType("KILLS"));
      plugin.getUserManager().addExperience(attacker, 2);
      plugin.getUserManager().getUser(attacker).adjustStatistic("LOCAL_KILLS", 1);
      new MessageBuilder("IN_GAME_MESSAGES_ARENA_KILLED")
          .asKey()
          .player(victim)
          .arena(baseArena)
          .send(attacker);
      new MessageBuilder("IN_GAME_MESSAGES_ARENA_DEATH")
          .asKey()
          .player(victim)
          .value(attacker.getName())
          .arena(baseArena)
          .sendArena();
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onHit(EntityDamageByEntityEvent e) {
    if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
      Player victim = (Player) e.getEntity();
      Player attack = (Player) e.getDamager();
      if (!ArenaUtils.areInSameArena(victim, attack)) {
        return;
      }
      BaseArena baseArena = plugin.getArenaRegistry().getArena(victim);
      if (baseArena == null || baseArena.getArenaState() != ArenaState.IN_GAME) {
        return;
      }
      if (plugin.getUserManager().getUser(attack).isSpectator()) {
        e.setCancelled(true);
        return;
      }
      if (baseArena.isTeammate(attack, victim)) {
        e.setCancelled(true);
        return;
      }
      baseArena.addHits(victim, attack);
    }
  }

  private final HashMap<Player, Long> cooldownPortal = new HashMap<>();
  private final HashMap<Player, Long> cooldownOutside = new HashMap<>();

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerMove(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    BaseArena baseArena = plugin.getArenaRegistry().getArena(player);
    if (baseArena == null || baseArena.getArenaState() != ArenaState.IN_GAME) {
      return;
    }
    if (baseArena.isResetRound() && !plugin.getUserManager().getUser(player).isSpectator()) {
      if (baseArena.getBase(event.getPlayer()).getCageCuboid() != null) {
        return;
      }
      if (event.getFrom().getZ() != event.getTo().getZ()
          && event.getFrom().getX() != event.getTo().getX()) {
        event.setCancelled(true);
        return;
      }
      return;
    }
    if (!baseArena.inBase(player)) {
      return;
    }
    if (!baseArena.getArenaBorder().isInWithMarge(player.getLocation(), 5)) {
      if (cooldownOutside.containsKey(player)
          && cooldownOutside.get(player) <= System.currentTimeMillis() - 1500) {
        cooldownOutside.remove(player);
        return;
      }
      player.damage(100);
      plugin
          .getDebugger()
          .debug(
              Level.INFO,
              "Killed "
                  + player.getName()
                  + " because he is more than 5 blocks outside baseArena location, Location: "
                  + player.getLocation()
                  + "; ArenaBorder: "
                  + baseArena.getArenaBorder().getMinPoint()
                  + ";"
                  + baseArena.getArenaBorder().getMaxPoint()
                  + ";"
                  + baseArena.getArenaBorder().getCenter());
      return;
    }
    if (cooldownPortal.containsKey(player)) {
      if (cooldownPortal.get(player) <= System.currentTimeMillis() - 5000)
        cooldownPortal.remove(player);
      return;
    }
    if (baseArena.getBase(player).getPortalCuboid().isIn(player)) {
      cooldownPortal.put(player, System.currentTimeMillis());
      new MessageBuilder("IN_GAME_MESSAGES_ARENA_PORTAL_OWN").asKey().player(player).sendPlayer();
      // prevent players being stuck on portal location
      Bukkit.getScheduler()
          .runTaskLater(
              plugin,
              () -> {
                if (player != null) {
                  if (baseArena
                      .getBase(player)
                      .getPortalCuboid()
                      .isInWithMarge(player.getLocation(), 1)) {
                    player.damage(100);
                    plugin
                        .getDebugger()
                        .debug(
                            Level.INFO,
                            "Killed "
                                + player.getName()
                                + " because he is more than 3 seconds on own portal (seems to stuck)");
                  }
                }
              },
              20 * 3 /* 3 seconds as cooldown to prevent instant respawning */);
      return;
    }
    for (Base base : baseArena.getBases()) {
      if (base.getPortalCuboid().isIn(player)) {
        if (base.getPoints() >= baseArena.getArenaOption("MODE_VALUE")) {
          cooldownPortal.put(player, System.currentTimeMillis());
          new MessageBuilder("IN_GAME_MESSAGES_ARENA_PORTAL_OUT")
              .asKey()
              .player(player)
              .sendPlayer();
          return;
        }
        cooldownPortal.put(player, System.currentTimeMillis());
        if (base.getAlivePlayersSize() == 0) {
          continue;
        }
        baseArena.resetRound();
        player.teleport(baseArena.getBase(player).getPlayerSpawnPoint());
        if (baseArena.getMode() == BaseArena.Mode.HEARTS) {
          base.addPoint();
        } else if (baseArena.getMode() == BaseArena.Mode.POINTS) {
          baseArena.getBase(player).addPoint();
        }
        new TitleBuilder("IN_GAME_MESSAGES_ARENA_PORTAL_SCORED_TITLE")
            .asKey()
            .arena(baseArena)
            .player(player)
            .value(base.getFormattedColor())
            .sendArena();
        new MessageBuilder("IN_GAME_MESSAGES_ARENA_PORTAL_OPPONENT")
            .asKey()
            .player(player)
            .arena(baseArena)
            .value(base.getFormattedColor())
            .sendArena();
        ((ScoreboardManager) baseArena.getScoreboardManager()).resetBaseCache();
        plugin
            .getUserManager()
            .addStat(player, plugin.getStatsStorage().getStatisticType("SCORED_POINTS"));
        plugin.getUserManager().addExperience(player, 10);
        plugin
            .getUserManager()
            .addStat(player, plugin.getStatsStorage().getStatisticType("LOCAL_SCORED_POINTS"));
        return;
      }
    }
  }

  @Override
  public boolean additionalFallDamageRules(
      Player victim, PluginArena arena, EntityDamageEvent event) {
    BaseArena pluginBaseArena = (BaseArena) plugin.getArenaRegistry().getArena(arena.getId());
    if (pluginBaseArena == null) {
      return false;
    }
    if (pluginBaseArena.getBase(victim) != null) {
      if (pluginBaseArena.getBase(victim).isDamageCooldown()) {
        event.setCancelled(true);
        return true;
      }
    }
    return false;
  }

  @Override
  public void handleIngameVoidDeath(Player victim, PluginArena arena) {
    BaseArena pluginBaseArena = (BaseArena) plugin.getArenaRegistry().getArena(arena.getId());
    if (pluginBaseArena == null) {
      return;
    }
    victim.damage(1000.0);
    victim.teleport(pluginBaseArena.getBase(victim).getPlayerRespawnPoint());
  }

  @EventHandler
  public void onBowShot(EntityShootBowEvent e) {
    if (!(e.getEntity() instanceof Player)) {
      return;
    }
    User user = plugin.getUserManager().getUser((Player) e.getEntity());
    BaseArena pluginBaseArena = (BaseArena) plugin.getArenaRegistry().getArena(user.getPlayer());
    if (pluginBaseArena == null) {
      return;
    }
    if (pluginBaseArena.isResetRound()) {
      e.setCancelled(true);
      return;
    }
    if (user.getCooldown("bow_shot") == 0) {
      int cooldown = 5;
      if ((user.getKit() instanceof ArcherKit)) {
        cooldown = 3;
      }
      user.setCooldown("bow_shot", plugin.getConfig().getInt("Bow-Cooldown", cooldown));
      Player player = (Player) e.getEntity();
      plugin
          .getBukkitHelper()
          .applyActionBarCooldown(player, plugin.getConfig().getInt("Bow-Cooldown", cooldown));
      VersionUtils.setDurability(e.getBow(), (short) 0);
    } else {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onArrowPickup(PlugilyPlayerPickupArrow e) {
    if (plugin.getArenaRegistry().isInArena(e.getPlayer())) {
      e.getItem().remove();
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onItemPickup(PlugilyEntityPickupItemEvent e) {
    if (!(e.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player) e.getEntity();
    BaseArena pluginBaseArena = (BaseArena) plugin.getArenaRegistry().getArena(player);
    if (pluginBaseArena == null) {
      return;
    }
    e.setCancelled(true);

    // User user = plugin.getUserManager().getUser(player);
    // if(user.isSpectator() || pluginBaseArena.getArenaState() != ArenaState.IN_GAME) {
    //  return;
    // }
  }

  @EventHandler
  public void onArrowDamage(EntityDamageByEntityEvent e) {
    if (!(e.getDamager() instanceof Arrow)) {
      return;
    }
    if (!(((Arrow) e.getDamager()).getShooter() instanceof Player)) {
      return;
    }
    Player attacker = (Player) ((Arrow) e.getDamager()).getShooter();
    if (!(e.getEntity() instanceof Player)) {
      return;
    }
    Player victim = (Player) e.getEntity();
    if (!ArenaUtils.areInSameArena(attacker, victim)) {
      return;
    }
    // we won't allow to suicide
    if (attacker == victim) {
      e.setCancelled(true);
      return;
    }
    BaseArena baseArena = plugin.getArenaRegistry().getArena(attacker);
    if (plugin.getUserManager().getUser(attacker).isSpectator()) {
      e.setCancelled(true);
      return;
    }
    if (baseArena.isTeammate(attacker, victim)) {
      e.setCancelled(true);
      return;
    }
    baseArena.addHits(victim, attacker);

    XSound.ENTITY_PLAYER_DEATH.play(victim.getLocation(), 50, 1);

    if (victim.getHealth() - e.getDamage() < 0) {
      return;
    }
    DecimalFormat df = new DecimalFormat("##.##");
    new MessageBuilder("IN_GAME_MESSAGES_ARENA_BOW_DAMAGE")
        .asKey()
        .player(victim)
        .value(df.format(victim.getHealth() - e.getDamage()))
        .send(attacker);
  }

  // todo fast die -> just teleporting the player instead of death and respawn
  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerDie(PlayerDeathEvent e) {
    BaseArena baseArena = plugin.getArenaRegistry().getArena(e.getEntity());
    if (baseArena == null) {
      return;
    }
    e.setDeathMessage("");
    e.getDrops().clear();
    e.setDroppedExp(0);
    // plugin.getCorpseHandler().spawnCorpse(e.getEntity(), baseArena);
    e.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 7 * 20, 0));
    Player player = e.getEntity();
    if (baseArena.getArenaState() == ArenaState.STARTING) {
      return;
    } else if (baseArena.getArenaState() == ArenaState.ENDING
        || baseArena.getArenaState() == ArenaState.RESTARTING) {
      player.getInventory().clear();
      player.setFlying(false);
      player.setAllowFlight(false);
      return;
    }
    if (baseArena.getBase(player) == null) {
      return;
    }
    // if mode hearts and they are out it should set spec mode for them
    if (baseArena.getMode() == BaseArena.Mode.HEARTS
        && baseArena.getBase(player).getPoints() >= baseArena.getArenaOption("MODE_VALUE")) {
      User user = plugin.getUserManager().getUser(player);
      user.setSpectator(true);
      ArenaUtils.hidePlayer(player, baseArena);
      player.getInventory().clear();
      if (baseArena.getArenaState() != ArenaState.ENDING
          && baseArena.getArenaState() != ArenaState.RESTARTING) {
        baseArena.addDeathPlayer(player);
      }
      List<Player> players = baseArena.getBase(player).getPlayers();
      if (players.stream().allMatch(baseArena::isDeathPlayer)) {
        baseArena.addOut();
      }
    } else {
      player.setGameMode(GameMode.SURVIVAL);
      ArenaUtils.hidePlayersOutsideTheGame(player, baseArena);
      player.getInventory().clear();
    }
    // we must call it ticks later due to instant respawn bug
    Bukkit.getScheduler()
        .runTaskLater(
            plugin,
            () -> {
              e.getEntity().spigot().respawn();
            },
            5);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onRespawn(PlayerRespawnEvent e) {
    Player player = e.getPlayer();
    BaseArena baseArena = plugin.getArenaRegistry().getArena(player);
    if (baseArena == null) {
      return;
    }
    if (baseArena.getArenaState() == ArenaState.STARTING
        || baseArena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS) {
      e.setRespawnLocation(baseArena.getLobbyLocation());
      return;
    } else if (baseArena.getArenaState() == ArenaState.ENDING
        || baseArena.getArenaState() == ArenaState.RESTARTING) {
      e.setRespawnLocation(baseArena.getSpectatorLocation());
      return;
    }
    if (baseArena.getPlayers().contains(player)) {
      User user = plugin.getUserManager().getUser(player);
      if (baseArena.inBase(player) && !user.isSpectator()) {
        cooldownOutside.put(player, System.currentTimeMillis());
        if (e.getPlayer().getLastDamageCause() != null
            && e.getPlayer().getLastDamageCause().getCause()
                != EntityDamageEvent.DamageCause.VOID) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * 20, 0));
        }
        e.setRespawnLocation(baseArena.getBase(player).getPlayerRespawnPoint());
        player.setAllowFlight(false);
        player.setFlying(false);
        ArenaUtils.hidePlayersOutsideTheGame(player, baseArena);
        player.setGameMode(GameMode.SURVIVAL);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        plugin
            .getRewardsHandler()
            .performReward(player, plugin.getRewardsHandler().getRewardType("DEATH"));
        // Restock or give KitItems makes no difference? if using restock we need to save inventory
        // as items are lost on dead
        // todo Maybe change it after fast dead is implemented (teleporting instead of dying)
        plugin.getUserManager().getUser(player).getKit().giveKitItems(player);
        if (!baseArena.getHits().containsKey(player)) {
          new MessageBuilder(MessageBuilder.ActionType.DEATH).arena(baseArena).player(player).sendArena();
        }
        plugin.getUserManager().addStat(player, plugin.getStatsStorage().getStatisticType("DEATHS"));
        user.adjustStatistic("LOCAL_DEATHS", 1);
        rewardLastAttacker(baseArena, player);
        Bukkit.getScheduler()
            .runTaskLater(
                plugin,
                () -> {
                  if (player != null) {
                    if (!baseArena
                        .getBase(player)
                        .getPortalCuboid()
                        .isInWithMarge(player.getLocation(), 5)) {
                      player.teleport(baseArena.getBase(player).getPlayerRespawnPoint());
                      player.getInventory().clear();
                      plugin.getUserManager().getUser(player).getKit().giveKitItems(player);
                      player.updateInventory();
                    }
                  }
                },
                5 /* 1/4 of a second as cooldown to prevent respawn from other plugins */);
      } else {
        e.setRespawnLocation(baseArena.getSpectatorLocation());
        player.setAllowFlight(true);
        player.setFlying(true);
        user.setSpectator(true);
        ArenaUtils.hidePlayer(player, baseArena);
        VersionUtils.setCollidable(player, false);
        player.setGameMode(GameMode.SURVIVAL);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        plugin.getUserManager().addStat(player, plugin.getStatsStorage().getStatisticType("DEATHS"));
        plugin.getSpecialItemManager().addSpecialItemsOfStage(player, SpecialItem.DisplayStage.SPECTATOR);
      }
    }
  }

  @EventHandler
  public void onItemMove(InventoryClickEvent e) {
    if (e.getWhoClicked() instanceof Player
        && plugin.getArenaRegistry().isInArena((Player) e.getWhoClicked())) {
      if (plugin.getArenaRegistry().getArena(((Player) e.getWhoClicked())).getArenaState()
          != ArenaState.IN_GAME) {
        if (e.getClickedInventory() == e.getWhoClicked().getInventory()) {
          if (e.getView().getType() == InventoryType.CRAFTING
              || e.getView().getType() == InventoryType.PLAYER) {
            e.setResult(Event.Result.DENY);
          }
        }
      }
    }
  }
}
