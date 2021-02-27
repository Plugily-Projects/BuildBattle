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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import pl.plajerlair.commonsbox.minecraft.compat.VersionUtils;
import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import pl.plajerlair.commonsbox.minecraft.misc.MiscUtils;
import pl.plajerlair.commonsbox.minecraft.serialization.InventorySerializer;
import plugily.projects.buildbattle.ConfigPreferences;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.api.StatsStorage;
import plugily.projects.buildbattle.api.event.game.BBGameEndEvent;
import plugily.projects.buildbattle.api.event.game.BBGameJoinEvent;
import plugily.projects.buildbattle.api.event.game.BBGameLeaveEvent;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.arena.impl.GuessTheBuildArena;
import plugily.projects.buildbattle.arena.impl.SoloArena;
import plugily.projects.buildbattle.arena.impl.TeamArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.handlers.ChatManager;
import plugily.projects.buildbattle.handlers.PermissionManager;
import plugily.projects.buildbattle.handlers.items.SpecialItem;
import plugily.projects.buildbattle.handlers.party.GameParty;
import plugily.projects.buildbattle.user.User;
import plugily.projects.buildbattle.utils.Debugger;

/**
 * @author Plajer
 * <p>
 * Created at 25.05.2018
 */
public class ArenaManager {

  private static final Main plugin = JavaPlugin.getPlugin(Main.class);

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
      player.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("In-Game.Arena-Not-Configured"));
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

    //check if player is in party and send party members to the game
    if(plugin.getPartyHandler().isPlayerInParty(player)) {
      GameParty party = plugin.getPartyHandler().getParty(player);
      if(party.getLeader() == player) {
        if(arena.getMaximumPlayers() - arena.getPlayers().size() >= party.getPlayers().size()) {
          for(Player partyPlayer : party.getPlayers()) {
            if(partyPlayer == player) {
              continue;
            }
            if(ArenaRegistry.getArena(partyPlayer) != null) {
              if(ArenaRegistry.getArena(partyPlayer).getArenaState() == ArenaState.IN_GAME) {
                continue;
              }
              leaveAttempt(partyPlayer, ArenaRegistry.getArena(partyPlayer));
            }
            partyPlayer.sendMessage(chatManager.getPrefix() + chatManager.formatMessage(arena, chatManager.colorMessage("In-Game.Join-As-Party-Member"), partyPlayer));
            joinAttempt(partyPlayer, arena);
          }
        } else {
          player.sendMessage(chatManager.getPrefix() + chatManager.formatMessage(arena, chatManager.colorMessage("In-Game.Messages.Lobby-Messages.Not-Enough-Space-For-Party"), player));
          return;
        }
      }
    }

    //Arena join permission when bungee false
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
        plugin.getComplement().kickPlayer(player, chatManager.getPrefix() + chatManager.colorMessage("Commands.Arena-Restarting"));
      } else {
        player.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("Commands.Arena-Restarting"));
      }
      return;
    }

    if(arena.getPlayers().size() >= arena.getMaximumPlayers() && arena.getArenaState() == ArenaState.STARTING) {
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
        loopPlayer.sendMessage(chatManager + chatManager.colorMessage("In-Game.Messages.Lobby-Messages.You-Were-Kicked-For-Premium-Slot"));
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
    arena.getScoreboardManager().createScoreboard(user);
    if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
      InventorySerializer.saveInventoryToFile(plugin, player);
    }

    player.setExp(1);
    player.setFoodLevel(20);
    player.setHealth(VersionUtils.getHealth(player));
    player.setLevel(0);
    player.setWalkSpeed(0.2f);
    player.setFlySpeed(0.1f);

    arena.doBarAction(BaseArena.BarAction.ADD, player);
    player.getInventory().clear();
    player.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});

    //Set player as spectator as the game is already started
    SpecialItem leaveItem = plugin.getSpecialItemsRegistry().getSpecialItem("Leave");
    if(arena.getArenaState() == ArenaState.IN_GAME || arena.getArenaState() == ArenaState.ENDING) {
      if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DISABLE_SPECTATORS)) {
        return;
      }

      arena.addSpectator(player);

      player.teleport(arena.getPlotManager().getPlots().get(0).getTeleportLocation());
      player.sendMessage(chatManager.colorMessage("In-Game.Spectator.You-Are-Spectator"));
      player.getInventory().clear();

      player.getInventory().setItem(0, new ItemBuilder(XMaterial.COMPASS.parseItem()).name(chatManager.colorMessage("In-Game.Spectator.Spectator-Item-Name")).build());
      player.getInventory().setItem(4, new ItemBuilder(XMaterial.COMPARATOR.parseItem()).name(chatManager.colorMessage("In-Game.Spectator.Settings-Menu.Item-Name")).build());
      player.getInventory().setItem(8, leaveItem.getItemStack());

      player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
      player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));

      //Hide player from other players
      arena.getPlayers().forEach(onlinePlayer -> VersionUtils.hidePlayer(plugin, onlinePlayer, player));

      user.setSpectator(true);
      player.setCollidable(false);
      player.setGameMode(GameMode.ADVENTURE);
      player.setAllowFlight(true);
      player.setFlying(true);

      for(Player spectator : arena.getPlayers()) {
        if(plugin.getUserManager().getUser(spectator).isSpectator()) {
          VersionUtils.showPlayer(plugin, player, spectator);
        } else {
          VersionUtils.hidePlayer(plugin, player, spectator);
        }
      }
      return;
    }

    arena.addPlayer(player);

    arena.teleportToLobby(player);
    player.getInventory().setItem(leaveItem.getSlot(), leaveItem.getItemStack());
    player.updateInventory();

    chatManager.broadcastAction(arena, player, ChatManager.ActionType.JOIN);
    VersionUtils.sendTitles(player, chatManager.colorMessage("In-Game.Messages.Join-Title").replace("%THEME%", arena.getTheme()), chatManager.colorMessage("In-Game.Messages.Join-SubTitle").replace("%THEME%", arena.getTheme()) , 5, 40, 5);
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
    BBGameLeaveEvent bbGameLeaveEvent = new BBGameLeaveEvent(player, arena);
    Bukkit.getPluginManager().callEvent(bbGameLeaveEvent);

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
    arena.getScoreboardManager().removeScoreboard(user);

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

    if(arena instanceof SoloArena) {
      ((SoloArena) arena).getQueue().remove(player);
    }

    arena.removePlayer(player);
    plugin.getChatManager().broadcastAction(arena, player, ChatManager.ActionType.LEAVE);

    // Games played +1
    if(arena.getArenaState() == ArenaState.IN_GAME || arena.getArenaState() == ArenaState.ENDING) {
      user.addStat(StatsStorage.StatisticType.GAMES_PLAYED, 1);
    }

    user.setStat(StatsStorage.StatisticType.LOCAL_GUESS_THE_BUILD_POINTS, 0);

    Plot plot = arena.getPlotManager().getPlot(player);
    if(plot != null) {
      if(arena instanceof TeamArena) {
        plot.getOwners().remove(player);
        if(plot.getOwners().size() > 1) {
          plot.fullyResetPlot();
        }
      } else
        plot.fullyResetPlot();
    }
    if(arena instanceof GuessTheBuildArena) {
      ((GuessTheBuildArena) arena).getWhoGuessed().remove(player);
    }

    if(arena.getPlayers().isEmpty() && arena.getArenaState() != ArenaState.WAITING_FOR_PLAYERS) {
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
    BBGameEndEvent gameEndEvent = new BBGameEndEvent(arena);
    Bukkit.getPluginManager().callEvent(gameEndEvent);
    for(Player player : arena.getPlayers()) {
      if(!quickStop) {
        spawnFireworks(arena, player);
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
          this.cancel();
          return;
        }
        MiscUtils.spawnRandomFirework(player.getLocation());
        i++;
      }
    }.runTaskTimer(plugin, 30, 30);
  }

}
