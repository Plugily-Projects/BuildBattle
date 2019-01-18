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

package pl.plajer.buildbattle.arena;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import pl.plajer.buildbattle.ConfigPreferences;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.api.event.game.BBGameEndEvent;
import pl.plajer.buildbattle.api.event.game.BBGameJoinEvent;
import pl.plajer.buildbattle.api.event.game.BBGameLeaveEvent;
import pl.plajer.buildbattle.arena.impl.BaseArena;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.handlers.PermissionManager;
import pl.plajer.buildbattle.handlers.items.SpecialItem;
import pl.plajer.buildbattle.user.User;
import pl.plajerlair.core.debug.Debugger;
import pl.plajerlair.core.debug.LogLevel;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.utils.InventoryUtils;
import pl.plajerlair.core.utils.MinigameUtils;

/**
 * @author Plajer
 * <p>
 * Created at 25.05.2018
 */
public class ArenaManager {

  private static Main plugin = JavaPlugin.getPlugin(Main.class);

  /**
   * Attempts player to join arena.
   * Calls BBGameJoinEvent.
   * Can be cancelled only via above-mentioned event
   *
   * @param player player to join
   * @param arena arena to join
   * @see BBGameJoinEvent
   */
  public static void joinAttempt(Player player, BaseArena arena) {
    try {
      Debugger.debug(LogLevel.INFO, "Initial join attempt, " + player.getName());
      BBGameJoinEvent bbGameJoinEvent = new BBGameJoinEvent(player, arena);
      Bukkit.getPluginManager().callEvent(bbGameJoinEvent);
      if (!arena.isReady()) {
        player.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("In-Game.Arena-Not-Configured"));
        return;
      }
      if (bbGameJoinEvent.isCancelled()) {
        player.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("In-Game.Join-Cancelled-Via-API"));
        return;
      }
      if (!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
        if (!(player.hasPermission(PermissionManager.getJoinPerm().replace("<arena>", "*")) || player.hasPermission(PermissionManager.getJoinPerm().replace("<arena>", arena.getID())))) {
          player.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("In-Game.Join-No-Permission"));
          return;
        }
      }
      if (ArenaRegistry.getArena(player) != null) {
        player.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("In-Game.Messages.Already-Playing"));
        return;
      }
      if ((arena.getArenaState() == ArenaState.IN_GAME || arena.getArenaState() == ArenaState.ENDING || arena.getArenaState() == ArenaState.RESTARTING)) {
        player.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Commands.Arena-Started"));
        return;
      }
      if (arena.getPlayers().size() == arena.getMaximumPlayers()) {
        player.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Commands.Arena-Is-Full"));
        return;
      }
      Debugger.debug(LogLevel.INFO, "Final join attempt, " + player.getName());
      if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
        InventoryUtils.saveInventoryToFile(plugin, player);
      }
      if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BOSSBAR_ENABLED)) {
        arena.getGameBar().addPlayer(player);
      }
      arena.teleportToLobby(player);
      arena.addPlayer(player);
      player.setExp(1);
      player.setLevel(0);
      player.setHealth(20.0);
      player.setFoodLevel(20);
      player.getInventory().setArmorContents(new ItemStack[] {new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
      player.getInventory().clear();
      arena.showPlayers();
      ChatManager.broadcastAction(arena, player, ChatManager.ActionType.JOIN);
      player.updateInventory();
      for (Player player : arena.getPlayers()) {
        arena.showPlayer(player);
      }
      for (Player player : plugin.getServer().getOnlinePlayers()) {
        if (!arena.getPlayers().contains(player)) {
          player.hidePlayer(player);
          player.hidePlayer(player);
        }
      }
      SpecialItem leaveItem = plugin.getSpecialItemsRegistry().getSpecialItem("Leave");
      player.getInventory().setItem(leaveItem.getSlot(), leaveItem.getItemStack());
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  /**
   * Attempts player to leave arena.
   * Calls BBGameLeaveEvent event.
   *
   * @param p player to leave
   * @param a arena to leave
   * @see BBGameLeaveEvent
   */
  public static void leaveAttempt(Player p, BaseArena a) {
    try {
      Debugger.debug(LogLevel.INFO, "Initial leave attempt, " + p.getName());
      BBGameLeaveEvent bbGameLeaveEvent = new BBGameLeaveEvent(p, a);
      Bukkit.getPluginManager().callEvent(bbGameLeaveEvent);
      a.getQueue().remove(p.getUniqueId());
      User user = plugin.getUserManager().getUser(p.getUniqueId());
      if (a.getArenaState() == ArenaState.IN_GAME || a.getArenaState() == ArenaState.ENDING) {
        user.addStat(StatsStorage.StatisticType.GAMES_PLAYED, 1);
      }
      a.teleportToEndLocation(p);
      a.removePlayer(p);
      ChatManager.broadcastAction(a, p, ChatManager.ActionType.LEAVE);
      user.removeScoreboard();
      if (a.getPlotManager().getPlot(p) != null) {
        a.getPlotManager().getPlot(p).fullyResetPlot();
      }

      p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
      p.setExp(0);
      p.setLevel(0);
      p.setFoodLevel(20);
      p.setFlying(false);
      p.setAllowFlight(false);
      p.resetPlayerWeather();
      p.resetPlayerTime();
      if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BOSSBAR_ENABLED)) {
        a.getGameBar().removePlayer(p);
      }
      p.getInventory().setArmorContents(null);
      p.getInventory().clear();
      for (PotionEffect effect : p.getActivePotionEffects()) {
        p.removePotionEffect(effect.getType());
      }
      p.setFireTicks(0);
      if (a.getPlayers().size() == 0 && a.getArenaState() != ArenaState.WAITING_FOR_PLAYERS) {
        a.setArenaState(ArenaState.RESTARTING);
        a.setTimer(0);
      }
      p.setGameMode(GameMode.SURVIVAL);
      if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
        InventoryUtils.loadInventory(plugin, p);
      }
      for (Player players : plugin.getServer().getOnlinePlayers()) {
        if (ArenaRegistry.getArena(players) == null) {
          players.showPlayer(p);
        }
        p.showPlayer(players);
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  /**
   * Stops current arena. Calls BBGameEndEvent event
   *
   * @param quickStop should arena be stopped immediately? (use only in important cases)
   * @param arena     arena to stop
   * @see BBGameEndEvent
   */
  public static void stopGame(boolean quickStop, BaseArena arena) {
    try {
      Debugger.debug(LogLevel.INFO, "Game stop event initiate, arena " + arena.getID());
      BBGameEndEvent gameEndEvent = new BBGameEndEvent(arena);
      Bukkit.getPluginManager().callEvent(gameEndEvent);
      for (final Player p : arena.getPlayers()) {
        plugin.getUserManager().getUser(p.getUniqueId()).removeScoreboard();
        if (!quickStop) {
          if (JavaPlugin.getPlugin(Main.class).getConfig().getBoolean("Firework-When-Game-Ends")) {
            new BukkitRunnable() {
              int i = 0;

              public void run() {
                if (i == 4) {
                  this.cancel();
                }
                if (!arena.getPlayers().contains(p)) {
                  this.cancel();
                }
                MinigameUtils.spawnRandomFirework(p.getLocation());
                i++;
              }
            }.runTaskTimer(JavaPlugin.getPlugin(Main.class), 30, 30);
          }
        }
      }
      arena.setArenaState(ArenaState.ENDING);
      arena.setTimer(10);
      arena.setVoting(false);
      Debugger.debug(LogLevel.INFO, "Game stop event finish, arena " + arena.getID());
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

}
