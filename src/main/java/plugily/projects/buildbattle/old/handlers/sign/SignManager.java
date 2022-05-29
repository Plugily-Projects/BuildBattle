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

package plugily.projects.buildbattle.old.handlers.sign;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.Nullable;
import plugily.projects.commonsbox.minecraft.compat.ServerVersion;
import plugily.projects.commonsbox.minecraft.compat.VersionUtils;
import plugily.projects.commonsbox.minecraft.compat.events.api.CBPlayerInteractEvent;
import plugily.projects.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.commonsbox.minecraft.misc.stuff.ComplementAccessor;
import plugily.projects.commonsbox.minecraft.serialization.LocationSerializer;
import plugily.projects.buildbattle.old.Main;
import plugily.projects.buildbattle.old.arena.ArenaManager;
import plugily.projects.buildbattle.old.arena.ArenaRegistry;
import plugily.projects.buildbattle.old.arena.ArenaState;
import plugily.projects.buildbattle.old.handlers.language.LanguageManager;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * @author Plajer
 * <p>
 * Created at 04.05.2018
 */
public class SignManager implements Listener {

  private final Main plugin;
  private final List<ArenaSign> arenaSigns = new ArrayList<>();
  private final Map<ArenaState, String> gameStateToString = new EnumMap<>(ArenaState.class);
  private final List<String> signLines;

  public SignManager(Main plugin) {
    this.plugin = plugin;
    gameStateToString.put(ArenaState.WAITING_FOR_PLAYERS, plugin.getChatManager().colorMessage("Signs.Game-States.Inactive"));
    gameStateToString.put(ArenaState.STARTING, plugin.getChatManager().colorMessage("Signs.Game-States.Starting"));
    gameStateToString.put(ArenaState.IN_GAME, plugin.getChatManager().colorMessage("Signs.Game-States.In-Game"));
    gameStateToString.put(ArenaState.ENDING, plugin.getChatManager().colorMessage("Signs.Game-States.Ending"));
    gameStateToString.put(ArenaState.RESTARTING, plugin.getChatManager().colorMessage("Signs.Game-States.Restarting"));
    signLines = LanguageManager.getLanguageList("Signs.Lines");
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @Nullable
  public ArenaSign getArenaSignByBlock(Block block) {
    if (block == null) {
      return null;
    }

    Location blockLoc = block.getLocation();

    for (ArenaSign sign : arenaSigns) {
      if (sign.getSign().getLocation().equals(blockLoc)) {
        return sign;
      }
    }

    return null;
  }

  @EventHandler
  public void onSignChange(SignChangeEvent e) {
    if(!e.getPlayer().hasPermission("buildbattle.admin.sign.create") ||
        !ComplementAccessor.getComplement().getLine(e, 0).equalsIgnoreCase("[buildbattle]")) {
      return;
    }
    String line1 = ComplementAccessor.getComplement().getLine(e, 1);
    if(line1.isEmpty()) {
      e.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Signs.Please-Type-BaseArena-Name"));
      return;
    }
    for(BaseArena arena : ArenaRegistry.getArenas()) {
      if(!arena.getID().equalsIgnoreCase(line1)) {
        continue;
      }
      for(int i = 0; i < signLines.size(); i++) {
        ComplementAccessor.getComplement().setLine(e, i, formatSign(signLines.get(i), arena));
      }
      arenaSigns.add(new ArenaSign((Sign) e.getBlock().getState(), arena));
      e.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Signs.Sign-Created"));
      String location = e.getBlock().getWorld().getName() + "," + e.getBlock().getX() + "," + e.getBlock().getY() + "," + e.getBlock().getZ() + ",0.0,0.0";
      FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
      List<String> locs = config.getStringList("instances." + arena.getID() + ".signs");
      locs.add(location);
      config.set("instances." + arena.getID() + ".signs", locs);
      ConfigUtils.saveConfig(plugin, config, "arenas");
      return;
    }
    e.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Signs.BaseArena-Doesnt-Exists"));
  }

  private String formatSign(String msg, BaseArena a) {
    String formatted = msg;
    formatted = StringUtils.replace(formatted, "%mapname%", a.getMapName());

    int maxPlayers = a.getMaximumPlayers();
    int players = a.getPlayers().size();

    if(players >= maxPlayers) {
      formatted = StringUtils.replace(formatted, "%state%", plugin.getChatManager().colorMessage("Signs.Game-States.Full-Game"));
    } else {
      formatted = StringUtils.replace(formatted, "%state%", gameStateToString.get(a.getArenaState()));
    }
    formatted = StringUtils.replace(formatted, "%playersize%", Integer.toString(players));
    formatted = StringUtils.replace(formatted, "%maxplayers%", Integer.toString(maxPlayers));
    formatted = plugin.getChatManager().colorRawMessage(formatted);
    return formatted;
  }

  @EventHandler
  public void onSignDestroy(BlockBreakEvent e) {
    ArenaSign arenaSign = getArenaSignByBlock(e.getBlock());
    if (arenaSign == null) {
      return;
    }

    if(!e.getPlayer().hasPermission("buildbattle.admin.sign.break")) {
      return;
    }

    arenaSigns.remove(arenaSign);

    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    ConfigurationSection section = config.getConfigurationSection("instances");
    if (section == null)
      return;

    String location = e.getBlock().getWorld().getName() + "," + e.getBlock().getX() + ".0," + e.getBlock().getY() + ".0," + e.getBlock().getZ() + ".0," + "0.0,0.0";
    for(String arena : section.getKeys(false)) {
      for(String sign : section.getStringList(arena + ".signs")) {
        if(!sign.equals(location)) {
          continue;
        }
        List<String> signs = section.getStringList(arena + ".signs");
        signs.remove(location);
        section.set(arena + ".signs", signs);
        ConfigUtils.saveConfig(plugin, config, "arenas");
        e.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Signs.Sign-Removed"));
        return;
      }
    }
    e.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + ChatColor.RED + "Couldn't remove sign from configuration! Please do this manually!");
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onJoinAttempt(CBPlayerInteractEvent e) {
    if(VersionUtils.checkOffHand(e.getHand()) || e.getAction() != Action.RIGHT_CLICK_BLOCK || !(e.getClickedBlock().getState() instanceof Sign)) {
      return;
    }
    ArenaSign arenaSign = getArenaSignByBlock(e.getClickedBlock());
    if(arenaSign != null) {
      ArenaManager.joinAttempt(e.getPlayer(), arenaSign.getArena());
    }
  }

  public void loadSigns() {
    arenaSigns.clear();

    ConfigurationSection section = ConfigUtils.getConfig(plugin, "arenas").getConfigurationSection("instances");
    if (section == null)
      return;

    for(String path : section.getKeys(false)) {
      for(String sign : section.getStringList(path + ".signs")) {
        BaseArena arena = ArenaRegistry.getArena(path);

        if (arena == null)
          continue;

        Location loc = LocationSerializer.getLocation(sign);
        org.bukkit.block.BlockState state = loc.getBlock().getState();

        if(state instanceof Sign) {
          arenaSigns.add(new ArenaSign((Sign) state, arena));
        } else {
          Debugger.debug(Debugger.Level.WARN, "Block at loc " + loc + " for arena " + path + " not a sign");
        }
      }
    }
  }

  public void updateSigns() {
    for(ArenaSign arenaSign : arenaSigns) {
      Sign sign = arenaSign.getSign();

      for(int i = 0; i < signLines.size(); i++) {
        ComplementAccessor.getComplement().setLine(sign, i, formatSign(signLines.get(i), arenaSign.getArena()));
      }

      Block behind = arenaSign.getBehind();

      if(behind != null && plugin.getConfig().getBoolean("Signs-Block-States-Enabled", true)) {
        try {
          switch(arenaSign.getArena().getArenaState()) {
            case WAITING_FOR_PLAYERS:
              behind.setType(XMaterial.WHITE_STAINED_GLASS.parseMaterial());
              if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_12_R1)) {
                Block.class.getMethod("setData", byte.class).invoke(behind, (byte) 0);
              }
              break;
            case STARTING:
              behind.setType(XMaterial.YELLOW_STAINED_GLASS.parseMaterial());
              if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_12_R1)) {
                Block.class.getMethod("setData", byte.class).invoke(behind, (byte) 4);
              }
              break;
            case IN_GAME:
              behind.setType(XMaterial.ORANGE_STAINED_GLASS.parseMaterial());
              if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_12_R1)) {
                Block.class.getMethod("setData", byte.class).invoke(behind, (byte) 1);
              }
              break;
            case ENDING:
              behind.setType(XMaterial.GRAY_STAINED_GLASS.parseMaterial());
              if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_12_R1)) {
                Block.class.getMethod("setData", byte.class).invoke(behind, (byte) 7);
              }
              break;
            case RESTARTING:
              behind.setType(XMaterial.BLACK_STAINED_GLASS.parseMaterial());
              if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_12_R1)) {
                Block.class.getMethod("setData", byte.class).invoke(behind, (byte) 15);
              }
              break;
            default:
              break;
          }
        } catch(Exception ignored) {
        }
      }
      sign.update();
    }
  }

  public List<ArenaSign> getArenaSigns() {
    return arenaSigns;
  }

  public Map<ArenaState, String> getGameStateToString() {
    return gameStateToString;
  }
}
