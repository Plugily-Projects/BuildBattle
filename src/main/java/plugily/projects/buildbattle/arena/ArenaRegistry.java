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

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.PluginArenaRegistry;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.dimensional.Cuboid;
import plugily.projects.minigamesbox.classic.utils.hologram.ArmorStandHologram;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.base.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 27/07/2014.
 */
public class ArenaRegistry extends PluginArenaRegistry {

  private final Main plugin;

  public ArenaRegistry(Main plugin) {
    super(plugin);
    this.plugin = plugin;
  }


  @Override
  public PluginArena getNewArena(String id) {
    return new BaseArena(id);
  }

  @Override
  public boolean additionalValidatorChecks(ConfigurationSection section, PluginArena arena, String id) {
    boolean checks = super.additionalValidatorChecks(section, arena, id);
    if(!checks) return false;

    if(!section.getBoolean(id + ".isdone")) {
      plugin.getDebugger().sendConsoleMsg(new MessageBuilder("VALIDATOR_INVALID_ARENA_CONFIGURATION").asKey().value("NOT VALIDATED").arena(arena).build());
      return false;
    }

    ((BaseArena) arena).setMidLocation(LocationSerializer.getLocation(section.getString(id + ".midlocation", "world,364.0,63.0,-72.0,0.0,0.0")));
    ((BaseArena) arena).setArenaBorder(new Cuboid(LocationSerializer.getLocation(section.getString(id + ".arenalocation1", "world,364.0,63.0,-72.0,0.0,0.0")), LocationSerializer.getLocation(section.getString(id + ".arenalocation2", "world,364.0,63.0,-72.0,0.0,0.0"))));
    int bases = 0;
    if(section.contains(id + ".bases")) {
      if(section.isConfigurationSection(id + ".bases")) {
        for(String baseID : section.getConfigurationSection(id + ".bases").getKeys(false)) {
          if(section.isSet(id + ".bases." + baseID + ".isdone")) {
            Base base = new Base(
                section.getString(id + ".bases." + baseID + ".color"),
                LocationSerializer.getLocation(section.getString(id+ ".bases." + baseID + ".baselocation1")),
                LocationSerializer.getLocation(section.getString(id+ ".bases." + baseID + ".baselocation2")),
                LocationSerializer.getLocation(section.getString(id + ".bases." + baseID + ".spawnpoint")),
                LocationSerializer.getLocation(section.getString(id + ".bases." + baseID + ".respawnpoint")),
                LocationSerializer.getLocation(section.getString(id + ".bases." + baseID + ".portallocation1")),
                LocationSerializer.getLocation(section.getString(id + ".bases." + baseID + ".portallocation2")),
                section.getInt(id + ".maximumsize")
            );
            ((BaseArena) arena).addBase(base);
            if(section.getString(id + ".bases." + baseID + ".cagelocation1") != null)
              base.setCageCuboid(new Cuboid(LocationSerializer.getLocation(section.getString(id + ".bases." + baseID + ".cagelocation1")), LocationSerializer.getLocation(section.getString(id + ".bases." + baseID + ".cagelocation2"))));
            ArmorStandHologram portal = new ArmorStandHologram(plugin.getBukkitHelper().getBlockCenter(LocationSerializer.getLocation(section.getString(id + ".bases." + baseID + ".portalhologram"))));
            for(String str : plugin.getLanguageManager().getLanguageMessage(plugin.getMessageManager().getPath("IN_GAME_MESSAGES_ARENA_PORTAL_HOLOGRAM")).split(";")) {
              portal.appendLine(str.replace("%arena_base_color_formatted%", base.getFormattedColor()));
            }
            base.setArmorStandHologram(portal);
            bases++;
          } else {
            plugin.getDebugger().sendConsoleMsg(new MessageBuilder("VALIDATOR_INVALID_ARENA_CONFIGURATION").asKey().value("NO BASES CONFIGURED").arena(arena).build());
            return false;
          }
        }
      } else {
        plugin.getDebugger().sendConsoleMsg(new MessageBuilder("VALIDATOR_INVALID_ARENA_CONFIGURATION").asKey().value("BASES NOT FOUND").arena(arena).build());
        return false;
      }
    } else {
      plugin.getDebugger().sendConsoleMsg(new MessageBuilder("VALIDATOR_INVALID_ARENA_CONFIGURATION").asKey().value("BASE NOT FOUND").arena(arena).build());
      return false;
    }
    if(bases < 2) {
      plugin.getDebugger().sendConsoleMsg(new MessageBuilder("VALIDATOR_INVALID_ARENA_CONFIGURATION").asKey().value("NOT ENOUGH BASES").arena(arena).build());
      return false;
    }
    arena.setArenaOption("BASE_PLAYER_SIZE", section.getInt(id + ".maximumsize", 3));
    arena.setMaximumPlayers(bases * arena.getArenaOption("BASE_PLAYER_SIZE"));
    if(section.contains(id + ".mode")) {
      ((BaseArena) arena).setMode(BaseArena.Mode.valueOf(section.getString(id + ".mode").toUpperCase()));
    } else {
      ((BaseArena) arena).setMode(BaseArena.Mode.POINTS);
    }
    arena.setArenaOption("MODE_VALUE", section.getInt(id + ".modevalue", 5));
    arena.setArenaOption("RESET_BLOCKS", section.getInt(id + ".resetblocks", 0));
    arena.setArenaOption("RESET_TIME", section.getInt(id + ".resettime", 5));
    for(Base base : ((BaseArena) arena).getBases()) {
      if(!((BaseArena) arena).getArenaBorder().isIn(base.getBaseCuboid().getCenter())) {
        plugin.getDebugger().sendConsoleMsg(new MessageBuilder("VALIDATOR_INVALID_ARENA_CONFIGURATION").asKey().arena(arena).value("YOUR BASE CUBOIDS ARE NOT INSIDE ARENA CUBOID").build());
        return false;
      }
    }
    return true;
  }

  @Override
  public @Nullable BaseArena getArena(Player player) {
    PluginArena pluginArena = super.getArena(player);
    if(pluginArena instanceof BaseArena) {
      return (BaseArena) pluginArena;
    }
    return null;
  }

  @Override
  public @Nullable BaseArena getArena(String id) {
    PluginArena pluginArena = super.getArena(id);
    if(pluginArena instanceof BaseArena) {
      return (BaseArena) pluginArena;
    }
    return null;
  }

  public @NotNull List<BaseArena> getPluginArenas() {
    List<BaseArena> baseArenas = new ArrayList<>();
    for(PluginArena pluginArena : super.getArenas()) {
      if(pluginArena instanceof BaseArena) {
        baseArenas.add((BaseArena) pluginArena);
      }
    }
    return baseArenas;
  }
}
