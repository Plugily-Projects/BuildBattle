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

package pl.plajer.buildbattle.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.arena.ArenaManager;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.ArenaState;
import pl.plajer.buildbattle.arena.impl.BaseArena;
import pl.plajer.buildbattle.handlers.language.LanguageManager;
import pl.plajerlair.core.debug.Debugger;
import pl.plajerlair.core.debug.LogLevel;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.utils.ConfigUtils;
import pl.plajerlair.core.utils.LocationUtils;
import pl.plajerlair.core.utils.XMaterial;

/**
 * @author Plajer
 * <p>
 * Created at 04.05.2018
 */
public class SignManager implements Listener {

  private Main plugin;
  private Map<Sign, BaseArena> loadedSigns = new HashMap<>();
  private Map<ArenaState, String> gameStateToString = new HashMap<>();
  private List<String> signLines;

  public SignManager(Main plugin) {
    this.plugin = plugin;
    gameStateToString.put(ArenaState.WAITING_FOR_PLAYERS, plugin.getChatManager().colorMessage("Signs.Game-States.Inactive"));
    gameStateToString.put(ArenaState.STARTING, plugin.getChatManager().colorMessage("Signs.Game-States.Starting"));
    gameStateToString.put(ArenaState.IN_GAME, plugin.getChatManager().colorMessage("Signs.Game-States.In-Game"));
    gameStateToString.put(ArenaState.ENDING, plugin.getChatManager().colorMessage("Signs.Game-States.Ending"));
    gameStateToString.put(ArenaState.RESTARTING, plugin.getChatManager().colorMessage("Signs.Game-States.Restarting"));
    signLines = LanguageManager.getLanguageList("Signs.Lines");
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    loadSigns();
    updateSignScheduler();
  }

  @EventHandler
  public void onSignChange(SignChangeEvent e) {
    try {
      if (!e.getPlayer().hasPermission("buildbattle.admin.sign.create")) {
        return;
      }
      if (!e.getLine(0).equalsIgnoreCase("[buildbattle]")) {
        return;
      }
      if (e.getLine(1).isEmpty()) {
        e.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Signs.Please-Type-Arena-Name"));
        return;
      }
      for (BaseArena arena : ArenaRegistry.getArenas()) {
        if (!arena.getID().equalsIgnoreCase(e.getLine(1))) {
          continue;
        }
        for (int i = 0; i < signLines.size(); i++) {
          e.setLine(i, formatSign(signLines.get(i), arena));
        }
        loadedSigns.put((Sign) e.getBlock().getState(), arena);
        e.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Signs.Sign-Created"));
        String location = e.getBlock().getWorld().getName() + "," + e.getBlock().getX() + "," + e.getBlock().getY() + "," + e.getBlock().getZ() + ",0.0,0.0";
        FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
        List<String> locs = config.getStringList("instances." + arena.getID() + ".signs");
        locs.add(location);
        config.set("instances." + arena.getID() + ".signs", locs);
        ConfigUtils.saveConfig(plugin, config, "arenas");
        return;
      }
      e.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Signs.Arena-Doesnt-Exists"));
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  private String formatSign(String msg, BaseArena a) {
    String formatted = msg;
    formatted = StringUtils.replace(formatted, "%mapname%", a.getMapName());
    if (a.getPlayers().size() >= a.getMaximumPlayers()) {
      formatted = StringUtils.replace(formatted, "%state%", plugin.getChatManager().colorMessage("Signs.Game-States.Full-Game"));
    } else {
      formatted = StringUtils.replace(formatted, "%state%", gameStateToString.get(a.getArenaState()));
    }
    formatted = StringUtils.replace(formatted, "%playersize%", String.valueOf(a.getPlayers().size()));
    formatted = StringUtils.replace(formatted, "%maxplayers%", String.valueOf(a.getMaximumPlayers()));
    formatted = plugin.getChatManager().colorRawMessage(formatted);
    return formatted;
  }

  @EventHandler
  public void onSignDestroy(BlockBreakEvent e) {
    try {
      if (!e.getPlayer().hasPermission("buildbattle.admin.sign.break")) {
        return;
      }
      if (loadedSigns.get(e.getBlock().getState()) == null) {
        return;
      }
      loadedSigns.remove(e.getBlock().getState());
      FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
      String location = e.getBlock().getWorld().getName() + "," + e.getBlock().getX() + ".0," + e.getBlock().getY() + ".0," + e.getBlock().getZ() + ".0," + "0.0,0.0";
      for (String arena : config.getConfigurationSection("instances").getKeys(false)) {
        for (String sign : config.getStringList("instances." + arena + ".signs")) {
          if (!sign.equals(location)) {
            continue;
          }
          List<String> signs = config.getStringList("instances." + arena + ".signs");
          signs.remove(location);
          config.set(arena + ".signs", signs);
          ConfigUtils.saveConfig(plugin, config, "arenas");
          e.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Signs.Sign-Removed"));
          return;
        }
      }
      e.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + ChatColor.RED + "Couldn't remove sign from configuration! Please do this manually!");
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onJoinAttempt(PlayerInteractEvent e) {
    if (e.getHand() == EquipmentSlot.OFF_HAND) {
      return;
    }
    if (e.getAction() == Action.RIGHT_CLICK_BLOCK &&
        e.getClickedBlock().getState() instanceof Sign && loadedSigns.containsKey(e.getClickedBlock().getState())) {
      ArenaManager.joinAttempt(e.getPlayer(), loadedSigns.get(e.getClickedBlock().getState()));
    }
  }

  public void loadSigns() {
    loadedSigns.clear();
    for (String path : ConfigUtils.getConfig(plugin, "arenas").getConfigurationSection("instances").getKeys(false)) {
      for (String sign : ConfigUtils.getConfig(plugin, "arenas").getStringList("instances." + path + ".signs")) {
        Location loc = LocationUtils.getLocation(sign);
        if (loc.getBlock().getState() instanceof Sign) {
          loadedSigns.put((Sign) loc.getBlock().getState(), ArenaRegistry.getArena(path));
        } else {
          Debugger.debug(LogLevel.WARN, "Block at loc " + loc + " for arena " + path + " not a sign");
        }
      }
    }
  }

  private void updateSignScheduler() {
    if (!plugin.getConfig().getBoolean("Signs-Block-States-Enabled", true)) {
      return;
    }
    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
      for (Map.Entry<Sign, BaseArena> entry : loadedSigns.entrySet()) {
        Sign s = entry.getKey();
        for (int i = 0; i < signLines.size(); i++) {
          s.setLine(i, formatSign(signLines.get(i), loadedSigns.get(s)));
        }
        Block behind = s.getBlock().getRelative(((org.bukkit.material.Sign) s.getData()).getAttachedFace());
        switch (entry.getValue().getArenaState()) {
          case WAITING_FOR_PLAYERS:
            behind.setType(XMaterial.WHITE_STAINED_GLASS.parseMaterial());
            if (plugin.is1_11_R1() || plugin.is1_12_R1()) {
              behind.setData((byte) 0);
            }
            break;
          case STARTING:
            behind.setType(XMaterial.YELLOW_STAINED_GLASS.parseMaterial());
            if (plugin.is1_11_R1() || plugin.is1_12_R1()) {
              behind.setData((byte) 4);
            }
            break;
          case IN_GAME:
            behind.setType(XMaterial.ORANGE_STAINED_GLASS.parseMaterial());
            if (plugin.is1_11_R1() || plugin.is1_12_R1()) {
              behind.setData((byte) 1);
            }
            break;
          case ENDING:
            behind.setType(XMaterial.GRAY_STAINED_GLASS.parseMaterial());
            if (plugin.is1_11_R1() || plugin.is1_12_R1()) {
              behind.setData((byte) 7);
            }
            break;
          case RESTARTING:
            behind.setType(XMaterial.BLACK_STAINED_GLASS.parseMaterial());
            if (plugin.is1_11_R1() || plugin.is1_12_R1()) {
              behind.setData((byte) 15);
            }
            break;
        }
        s.update();
      }
    }, 10, 10);
  }

  public Map<Sign, BaseArena> getLoadedSigns() {
    return loadedSigns;
  }
}
