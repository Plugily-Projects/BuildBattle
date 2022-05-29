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

package plugily.projects.buildbattle.old.arena;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import plugily.projects.buildbattle.old.ConfigPreferences;
import plugily.projects.buildbattle.old.Main;
import plugily.projects.buildbattle.old.api.StatsStorage;
import plugily.projects.buildbattle.old.api.event.game.BBGameEndEvent;
import plugily.projects.buildbattle.old.api.event.game.BBGameJoinEvent;
import plugily.projects.buildbattle.old.api.event.game.BBGameLeaveEvent;
import plugily.projects.buildbattle.old.arena.impl.GuessTheBuildArena;
import plugily.projects.buildbattle.old.arena.managers.plots.Plot;
import plugily.projects.buildbattle.old.handlers.ChatManager;
import plugily.projects.buildbattle.old.handlers.PermissionManager;
import plugily.projects.buildbattle.old.handlers.items.SpecialItem;
import plugily.projects.buildbattle.old.handlers.party.GameParty;
import plugily.projects.buildbattle.old.user.User;
import plugily.projects.commonsbox.minecraft.compat.VersionUtils;
import plugily.projects.commonsbox.minecraft.misc.MiscUtils;
import plugily.projects.commonsbox.minecraft.misc.stuff.ComplementAccessor;
import plugily.projects.commonsbox.minecraft.serialization.InventorySerializer;

import java.util.Random;

/**
 * @author Plajer
 * <p>
 * Created at 25.05.2018
 */
public class ArenaManager {

  private static final Main plugin = JavaPlugin.getPlugin(Main.class);
  private static final Random random = new Random();

  private ArenaManager() {
  }

  /**
   * Attempts player to join arena.
   * Calls BBGameJoinEvent.
   * Can be cancelled only via above-mentioned event
   *
   * @param player player to join
   * @param arena  arena to join
   * @see BBGameJoinEvent
   */
  public static void joinAttempt(Player player, BaseArena arena) {
    Debugger.debug("Initial join attempt, " + player.getName());
    BBGameJoinEvent bbGameJoinEvent = new BBGameJoinEvent(player, arena);
    Bukkit.getPluginManager().callEvent(bbGameJoinEvent);

    ChatManager chatManager = plugin.getChatManager();

    if(!arena.isReady()) {
      player.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("In-Game.BaseArena-Not-Configured"));
      return;
    }
    if(bbGameJoinEvent.isCancelled()) {
      player.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("In-Game.Join-Cancelled-Via-API"));
      return;
    }
    if(ArenaRegistry.getArena(player) != null) {
      player.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("In-Game.Messages.Already-Playing"));
      return;
    }

    Plot partyPlot = null;
    GameParty party = plugin.getPartyHandler().getParty(player);

    //check if player is in party and send party members to the game
    if(party != null) {
      Debugger.debug("[Party] Initialized party check " + player.getName());

      if(party.getLeader() == player) {
        if(arena.getMaximumPlayers() - arena.getPlayers().size() >= party.getPlayers().size()) {
          partyPlot = arena.getPlotManager().getPlots().get(random.nextInt(arena.getPlotManager().getPlots().size()));

          for(Player partyPlayer : party.getPlayers()) {
            if(partyPlayer == player) {
              continue;
            }

            BaseArena partyPlayerArena = ArenaRegistry.getArena(partyPlayer);

            if(partyPlayerArena != null) {
              if(partyPlayerArena.getArenaState() == ArenaState.IN_GAME) {
                continue;
              }

              Debugger.debug("[Party] Remove party member " + partyPlayer.getName() + " from other not ingame arena " + player.getName());
              leaveAttempt(partyPlayer, partyPlayerArena);
            }

            partyPlayer.sendMessage(chatManager.getPrefix() + chatManager.formatMessage(arena, chatManager.colorMessage("In-Game.Join-As-Party-Member"), partyPlayer));
            joinAttempt(partyPlayer, arena);
            Debugger.debug("[Party] Added party member " + partyPlayer.getName() + " to arena of " + player.getName());
          }
        } else {
          player.sendMessage(chatManager.getPrefix() + chatManager.formatMessage(arena, chatManager.colorMessage("In-Game.Messages.Lobby-Messages.Not-Enough-Space-For-Party"), player));
          Debugger.debug("[Party] Not enough space for party of " + player.getName());
          return;
        }
      }

      Player partyLeader = party.getLeader();
      if(arena.getPlayers().contains(partyLeader)) {
        Plot partyLeaderPlot = arena.getPlotManager().getPlot(partyLeader);
        if(partyLeaderPlot != null) {
          partyPlot = partyLeaderPlot;
        }
      }
      Debugger.debug("[Party] Party check done for " + player.getName());
    }
    //BaseArena join permission when bungee false
    if(!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED) &&
        (!(player.hasPermission(PermissionManager.getJoinPerm().replace("<arena>", "*"))
            || player.hasPermission(PermissionManager.getJoinPerm().replace("<arena>", arena.getID()))))) {
      player.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("In-Game.Join-No-Permission")
          .replace("%permission%", PermissionManager.getJoinPerm().replace("<arena>", arena.getID())));
      return;
    }

    if((arena.getArenaState() == ArenaState.IN_GAME || arena.getArenaState() == ArenaState.ENDING)
        && plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DISABLE_SPECTATORS)) {
      return;
    }

    if(arena.getArenaState() == ArenaState.RESTARTING) {
      if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
        ComplementAccessor.getComplement().kickPlayer(player, chatManager.getPrefix() + chatManager.colorMessage("Commands.BaseArena-Restarting"));
      } else {
        player.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("Commands.BaseArena-Restarting"));
      }
      return;
    }

    if(arena.getArenaState() == ArenaState.STARTING && arena.getPlayers().size() >= arena.getMaximumPlayers()) {
      if(!player.hasPermission(PermissionManager.getJoinFullGames())) {
        player.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("In-Game.Full-Game-No-Permission"));
        return;
      }
      boolean foundSlot = false;
      for(Player loopPlayer : arena.getPlayers()) {
        if(loopPlayer.hasPermission(PermissionManager.getJoinFullGames())) {
          continue;
        }
        leaveAttempt(loopPlayer, arena);
        loopPlayer.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("In-Game.Messages.Lobby-Messages.You-Were-Kicked-For-Premium-Slot"));
        chatManager.broadcast(arena, chatManager.formatMessage(arena, chatManager.colorMessage("In-Game.Messages.Lobby-Messages.Kicked-For-Premium-Slot"), loopPlayer));
        foundSlot = true;
        break;
      }
      if(!foundSlot) {
        player.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("In-Game.No-Slots-For-Premium"));
        return;
      }
    }

    Debugger.debug("Final join attempt, " + player.getName());
    User user = plugin.getUserManager().getUser(player);
    user.lastBoard = player.getScoreboard();
    //reset scoreboard
    player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

    arena.getScoreboardManager().createScoreboard(user);
    if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
      InventorySerializer.saveInventoryToFile(plugin, player);
    }

    player.setExp(1);
    player.setFoodLevel(20);
    player.setHealth(VersionUtils.getMaxHealth(player));
    player.setLevel(0);
    player.setWalkSpeed(0.2f);
    player.setFlySpeed(0.1f);

    arena.doBarAction(BaseArena.BarAction.ADD, player);
    player.getInventory().clear();
    player.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});

    //Set player as spectator as the game is already started
    if(arena.getArenaState() == ArenaState.IN_GAME || arena.getArenaState() == ArenaState.ENDING) {
      arena.addSpectator(player);

      player.teleport(arena.getPlotManager().getPlots().get(0).getTeleportLocation());
      player.sendMessage(chatManager.colorMessage("In-Game.Spectator.You-Are-Spectator"));
      player.getInventory().clear();

      for(SpecialItem item : plugin.getSpecialItemsManager().getSpecialItems()) {
        if(item.getDisplayStage() != SpecialItem.DisplayStage.SPECTATOR) {
          continue;
        }
        player.getInventory().setItem(item.getSlot(), item.getItemStack());
      }

      player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
      player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));

      //Hide player from other players
      arena.getPlayers().forEach(onlinePlayer -> VersionUtils.hidePlayer(plugin, onlinePlayer, player));

      user.setSpectator(true);

      VersionUtils.setCollidable(player, false);

      player.setGameMode(GameMode.ADVENTURE);
      player.setAllowFlight(true);
      player.setFlying(true);

      for(Player spectator : arena.getPlayers()) {
        if(plugin.getUserManager().getUser(spectator).isSpectator()) {
          VersionUtils.showPlayer(plugin, player, spectator);
        } else {
          VersionUtils.hidePlayer(plugin, spectator, player);
        }
      }
      return;
    }
    if(partyPlot != null) {
      partyPlot.addMember(player, arena, false);
    }
    arena.addPlayer(player);

    arena.teleportToLobby(player);
    if(arena.getArenaState() == ArenaState.STARTING || arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS) {
      for(SpecialItem item : plugin.getSpecialItemsManager().getSpecialItems()) {
        if(arena.getArenaType() == BaseArena.ArenaType.TEAM && item.getDisplayStage() == SpecialItem.DisplayStage.TEAM) {
          player.getInventory().setItem(item.getSlot(), item.getItemStack());
          continue;
        }
        if(item.getDisplayStage() != SpecialItem.DisplayStage.LOBBY) {
          continue;
        }
        player.getInventory().setItem(item.getSlot(), item.getItemStack());
      }
    }
    player.updateInventory();

    chatManager.broadcastAction(arena, player, ChatManager.ActionType.JOIN);
    VersionUtils.sendTitles(player, chatManager.colorMessage("In-Game.Messages.Join-Title").replace("%ARENANAME%", arena.getMapName()),
        chatManager.colorMessage("In-Game.Messages.Join-SubTitle").replace("%ARENANAME%", arena.getMapName()), 5, 40, 5);
    plugin.getSignManager().updateSigns();
  }

  /**
   * Attempts player to leave arena.
   * Calls BBGameLeaveEvent event.
   *
   * @param player player to leave
   * @param arena  arena to leave
   * @see BBGameLeaveEvent
   */
  public static void leaveAttempt(Player player, BaseArena arena) {
    Debugger.debug("Initial leave attempt, " + player.getName());
    Bukkit.getPluginManager().callEvent(new BBGameLeaveEvent(player, arena));

    arena.teleportToEndLocation(player);

    arena.doBarAction(BaseArena.BarAction.REMOVE, player);
    player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    VersionUtils.setMaxHealth(player, 20);
    player.getInventory().clear();
    player.getInventory().setArmorContents(null);
    player.resetPlayerTime();
    player.resetPlayerWeather();
    player.setAllowFlight(false);
    player.setExp(0);
    player.setFireTicks(0);
    player.setFlying(false);
    player.setFoodLevel(20);
    player.setLevel(0);
    player.setGameMode(GameMode.SURVIVAL);
    player.setWalkSpeed(0.2f);

    User user = plugin.getUserManager().getUser(player);
    user.removeScoreboard(arena);

    if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
      InventorySerializer.loadInventory(plugin, player);
    }

    for(Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
      if(ArenaRegistry.getArena(onlinePlayer) == null) {
        VersionUtils.showPlayer(plugin, onlinePlayer, player);
      }
      VersionUtils.showPlayer(plugin, player, onlinePlayer);
    }

    // Spectator
    if(user.isSpectator() || arena.getSpectators().contains(player)) {
      arena.removeSpectator(player);
      user.setSpectator(false);
      return;
    }

    Plot plot = arena.getPlotManager().getPlot(player);
    if(plot != null && plot.getMembers().size() <= 1) {
      if(arena instanceof SoloArena) {
        ((SoloArena) arena).getQueue().remove(plot);
      }
    }

    arena.removePlayer(player);
    plugin.getChatManager().broadcastAction(arena, player, ChatManager.ActionType.LEAVE);

    // Games played +1
    if(arena.getArenaState() == ArenaState.IN_GAME || arena.getArenaState() == ArenaState.ENDING) {
      user.addStat(StatsStorage.StatisticType.GAMES_PLAYED, 1);
    }

    user.setStat(StatsStorage.StatisticType.LOCAL_GUESS_THE_BUILD_POINTS, 0);

    if(plot != null) {
      if(arena instanceof TeamArena) {
        plot.getMembers().remove(player);
        if(plot.getMembers().size() < 1) {
          plot.fullyResetPlot();
        }
      } else
        plot.fullyResetPlot();
    }
    if(arena instanceof GuessTheBuildArena) {
      ((GuessTheBuildArena) arena).getWhoGuessed().remove(player);
    }

    if(arena.getArenaState() != ArenaState.WAITING_FOR_PLAYERS && arena.getPlayers().isEmpty()) {
      arena.setArenaState(ArenaState.RESTARTING);
      arena.setTimer(0);
    }

    plugin.getSignManager().updateSigns();
  }

  /**
   * Stops current arena. Calls BBGameEndEvent event
   *
   * @param quickStop should arena be stopped immediately? (use only in important cases)
   * @param arena     arena to stop
   * @see BBGameEndEvent
   */
  public static void stopGame(boolean quickStop, BaseArena arena) {
    Debugger.debug("Game stop event initiate, arena " + arena.getID());
    Bukkit.getPluginManager().callEvent(new BBGameEndEvent(arena));
    if(!quickStop) {
      for(Player player : arena.getPlayers()) {
        spawnFireworks(arena, player);
        for(SpecialItem item : plugin.getSpecialItemsManager().getSpecialItems()) {
          if(item.getDisplayStage() != SpecialItem.DisplayStage.SPECTATOR) {
            continue;
          }
          player.getInventory().setItem(item.getSlot(), item.getItemStack());
        }
        User user = plugin.getUserManager().getUser(player);
        user.removeScoreboard(arena);
      }
    }
    arena.getScoreboardManager().stopAllScoreboards();
    arena.setArenaState(ArenaState.ENDING);
    arena.setTimer(10);
    if(arena instanceof SoloArena) {
      ((SoloArena) arena).setVoting(false);
    }
    Debugger.debug("Game stop event finish, arena " + arena.getID());
  }

  private static void spawnFireworks(BaseArena arena, Player player) {
    if(!plugin.getConfig().getBoolean("Firework-When-Game-Ends", true)) {
      return;
    }
    new BukkitRunnable() {
      int i = 0;

      @Override
      public void run() {
        if(i == 4 || !arena.getPlayers().contains(player)) {
          cancel();
          return;
        }
        MiscUtils.spawnRandomFirework(player.getLocation());
        i++;
      }
    }.runTaskTimer(plugin, 30, 30);
  }

}
