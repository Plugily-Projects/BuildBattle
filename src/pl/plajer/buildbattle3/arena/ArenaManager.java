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

package pl.plajer.buildbattle3.arena;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import pl.plajer.buildbattle3.ConfigPreferences;
import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.buildbattleapi.BBGameEndEvent;
import pl.plajer.buildbattle3.buildbattleapi.BBGameJoinEvent;
import pl.plajer.buildbattle3.buildbattleapi.BBGameLeaveEvent;
import pl.plajer.buildbattle3.handlers.ChatManager;
import pl.plajer.buildbattle3.handlers.PermissionManager;
import pl.plajer.buildbattle3.handlers.items.SpecialItem;
import pl.plajer.buildbattle3.handlers.items.SpecialItemManager;
import pl.plajer.buildbattle3.plajerlair.core.services.ReportedException;
import pl.plajer.buildbattle3.plajerlair.core.utils.InventoryUtils;
import pl.plajer.buildbattle3.plajerlair.core.utils.MinigameUtils;
import pl.plajer.buildbattle3.user.User;
import pl.plajer.buildbattle3.user.UserManager;

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
   * @param p player to join
   * @param a arena to join
   * @see BBGameJoinEvent
   */
  public static void joinAttempt(Player p, Arena a) {
    try {
      Main.debug("Initial join attempt, " + p.getName(), System.currentTimeMillis());
      BBGameJoinEvent bbGameJoinEvent = new BBGameJoinEvent(p, a);
      Bukkit.getPluginManager().callEvent(bbGameJoinEvent);
      if (!a.isReady()) {
        p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Arena-Not-Configured"));
        return;
      }
      if (bbGameJoinEvent.isCancelled()) {
        p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Join-Cancelled-Via-API"));
        return;
      }
      if (!plugin.isBungeeActivated()) {
        if (!(p.hasPermission(PermissionManager.getJoinPerm().replace("<arena>", "*")) || p.hasPermission(PermissionManager.getJoinPerm().replace("<arena>", a.getID())))) {
          p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Join-No-Permission"));
          return;
        }
      }
      if (ArenaRegistry.getArena(p) != null) {
        p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Already-Playing"));
        return;
      }
      if ((a.getArenaState() == ArenaState.IN_GAME || a.getArenaState() == ArenaState.ENDING || a.getArenaState() == ArenaState.RESTARTING)) {
        p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Arena-Started"));
        return;
      }
      if (a.getPlayers().size() == a.getMaximumPlayers()) {
        p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Arena-Is-Full"));
        return;
      }
      Main.debug("Final join attempt, " + p.getName(), System.currentTimeMillis());
      if (plugin.isInventoryManagerEnabled()) InventoryUtils.saveInventoryToFile(plugin, p);
      if (ConfigPreferences.isBarEnabled()) {
        a.getGameBar().addPlayer(p);
      }
      a.teleportToLobby(p);
      a.addPlayer(p);
      p.setHealth(20.0);
      p.setFoodLevel(20);
      p.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
      p.getInventory().clear();
      a.showPlayers();
      ChatManager.broadcastAction(a, p, ChatManager.ActionType.JOIN);
      p.updateInventory();
      for (Player player : a.getPlayers()) {
        a.showPlayer(player);
      }
      if (ConfigPreferences.isHidePlayersOutsideGameEnabled()) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
          if (!a.getPlayers().contains(player)) {
            p.hidePlayer(player);
            player.hidePlayer(p);
          }
        }
      }
      SpecialItem leaveItem = SpecialItemManager.getSpecialItem("Leave");
      p.getInventory().setItem(leaveItem.getSlot(), leaveItem.getItemStack());
    } catch (Exception ex){
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
  public static void leaveAttempt(Player p, Arena a) {
    try {
      Main.debug("Initial leave attempt, " + p.getName(), System.currentTimeMillis());
      BBGameLeaveEvent bbGameLeaveEvent = new BBGameLeaveEvent(p, a);
      Bukkit.getPluginManager().callEvent(bbGameLeaveEvent);
      a.getQueue().remove(p.getUniqueId());
      User user = UserManager.getUser(p.getUniqueId());
      if (a.getArenaState() == ArenaState.IN_GAME || a.getArenaState() == ArenaState.ENDING)
        UserManager.getUser(p.getUniqueId()).addInt("gamesplayed", 1);
      a.teleportToEndLocation(p);
      a.removePlayer(p);
      ChatManager.broadcastAction(a, p, ChatManager.ActionType.LEAVE);
      user.removeScoreboard();
      if (a.getPlotManager().getPlot(p) != null) {
        a.getPlotManager().getPlot(p).fullyResetPlot();
      }

      p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
      p.setFoodLevel(20);
      p.setFlying(false);
      p.setAllowFlight(false);
      //set weather to world's weather because it was changed in game for each plot
      if (p.getWorld().hasStorm()) {
        p.setPlayerWeather(WeatherType.DOWNFALL);
      } else {
        p.setPlayerWeather(WeatherType.CLEAR);
      }
      if (ConfigPreferences.isBarEnabled()) {
        a.getGameBar().removePlayer(p);
      }
      p.getInventory().setArmorContents(null);
      p.getInventory().clear();
      for (PotionEffect effect : p.getActivePotionEffects()) {
        p.removePotionEffect(effect.getType());
      }
      p.setFireTicks(0);
      if (a.getPlayers().size() == 0) {
        a.setGameState(ArenaState.RESTARTING);
        a.setTimer(0);
      }
      p.setGameMode(GameMode.SURVIVAL);
      if (plugin.isInventoryManagerEnabled()) {
        InventoryUtils.loadInventory(plugin, p);
      }
      for (Player player : plugin.getServer().getOnlinePlayers()) {
        if (!a.getPlayers().contains(player)) {
          p.showPlayer(player);
          player.showPlayer(p);
        }
      }
    } catch (Exception ex){
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
  public static void stopGame(boolean quickStop, Arena arena) {
    try {
      Main.debug("Game stop event initiate, arena " + arena.getID(), System.currentTimeMillis());
      BBGameEndEvent gameEndEvent = new BBGameEndEvent(arena);
      Bukkit.getPluginManager().callEvent(gameEndEvent);
      for (final Player p : arena.getPlayers()) {
        UserManager.getUser(p.getUniqueId()).removeScoreboard();
        if (!quickStop) {
          if (JavaPlugin.getPlugin(Main.class).getConfig().getBoolean("Firework-When-Game-Ends")) {
            new BukkitRunnable() {
              int i = 0;

              public void run() {
                if (i == 4) this.cancel();
                if (!arena.getPlayers().contains(p)) this.cancel();
                MinigameUtils.spawnRandomFirework(p.getLocation());
                i++;
              }
            }.runTaskTimer(JavaPlugin.getPlugin(Main.class), 30, 30);
          }
        }
      }
      arena.setGameState(ArenaState.ENDING);
      arena.setTimer(10);
      arena.setVoting(false);
      Main.debug("Game stop event finish, arena " + arena.getID(), System.currentTimeMillis());
    } catch (Exception ex){
      new ReportedException(plugin, ex);
    }
  }

}
