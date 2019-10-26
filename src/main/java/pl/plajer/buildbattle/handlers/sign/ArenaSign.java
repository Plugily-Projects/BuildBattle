/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Tigerpanzer_02, Plajer and contributors
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

package pl.plajer.buildbattle.handlers.sign;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.jetbrains.annotations.Nullable;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.arena.impl.BaseArena;

/**
 * Created for 1.14 compatibility purposes, it will cache block behind sign that will be
 * accessed via reflection on 1.14 which is expensive
 */
public class ArenaSign {

  private static Main plugin;
  private Sign sign;
  private Block behind;
  private BaseArena arena;

  public ArenaSign(Sign sign, BaseArena arena) {
    this.sign = sign;
    this.arena = arena;
    setBehindBlock();
  }

  public static void init(Main plugin) {
    ArenaSign.plugin = plugin;
  }

  private void setBehindBlock() {
    this.behind = null;
    if (sign.getBlock().getType() == Material.WALL_SIGN) {
      if (plugin.is1_14_R1()) {
        this.behind = getBlockBehind();
      } else {
        this.behind = getBlockBehindLegacy();
      }
    }
  }

  private Block getBlockBehind() {
    try {
      Object blockData = sign.getBlock().getState().getClass().getMethod("getBlockData").invoke(sign.getBlock().getState());
      BlockFace face = (BlockFace) blockData.getClass().getMethod("getFacing").invoke(blockData);

      Location loc = sign.getLocation();
      Location location = new Location(sign.getWorld(), loc.getBlockX() - face.getModX(), loc.getBlockY() - face.getModY(),
          loc.getBlockZ() - face.getModZ());
      return location.getBlock();
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
      return null;
    }
  }

  private Block getBlockBehindLegacy() {
    return sign.getBlock().getRelative(((org.bukkit.material.Sign) sign.getData()).getAttachedFace());
  }

  public Sign getSign() {
    return sign;
  }

  @Nullable
  public Block getBehind() {
    return behind;
  }

  public BaseArena getArena() {
    return arena;
  }

}
