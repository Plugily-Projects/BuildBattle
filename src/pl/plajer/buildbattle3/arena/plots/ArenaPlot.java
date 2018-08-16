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

package pl.plajer.buildbattle3.arena.plots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.citizensnpcs.api.CitizensAPI;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.WeatherType;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle3.ConfigPreferences;
import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.user.User;
import pl.plajer.buildbattle3.user.UserManager;
import pl.plajer.buildbattle3.utils.Cuboid;
import pl.plajerlair.core.services.ReportedException;

/**
 * Created by Tom on 17/08/2015.
 */
public class ArenaPlot {

  private Location maxPoint;
  private Location minPoint;
  private int points;
  private List<UUID> uuids = new ArrayList<>();
  private Map<Location, Particle> particles = new HashMap<>();
  private WeatherType weatherType = WeatherType.CLEAR;
  private int entities = 0;

  public ArenaPlot() {
  }

  public int getEntities() {
    return entities;
  }

  public void addEntity() {
    entities++;
  }

  public void removeEntity() {
    if (entities == 0) return;
    entities--;
  }

  public Map<Location, Particle> getParticles() {
    return particles;
  }

  public void addParticle(Location location, Particle effect) {
    particles.put(location, effect);
  }

  public WeatherType getWeatherType() {
    return weatherType;
  }

  public void setWeatherType(WeatherType weatherType) {
    this.weatherType = weatherType;
  }

  private Location getMaxPoint() {
    return maxPoint;
  }

  public void setMaxPoint(Location maxPoint) {
    this.maxPoint = maxPoint;
  }

  private Location getMinPoint() {
    return minPoint;
  }

  public void setMinPoint(Location minPoint) {
    this.minPoint = minPoint;
  }

  public List<UUID> getOwners() {
    return uuids;
  }

  public void setOwners(List<UUID> players) {
    this.uuids = players;
  }

  public void addOwner(UUID player) {
    this.uuids.add(player);
  }

  public void fullyResetPlot() {
    try {
      Cuboid cuboid = new Cuboid(getMinPoint(), getMaxPoint());
      for(Block block : cuboid.blockList()){
        if(block.getType() != Material.AIR) {
          block.setType(Material.AIR);
        }
      }
      for(UUID u : uuids){
        Player p = Bukkit.getPlayer(u);
        if (p.getWorld().hasStorm()) {
          p.setPlayerWeather(WeatherType.DOWNFALL);
          setWeatherType(WeatherType.DOWNFALL);
        } else {
          p.setPlayerWeather(WeatherType.CLEAR);
          setWeatherType(WeatherType.CLEAR);
        }
      }
      if (uuids != null || !uuids.isEmpty()) {
        for (UUID u : uuids) {
          User user = UserManager.getUser(u);
          user.setObject(null, "plot");
          this.setOwners(new ArrayList<>());
          this.setPoints(0);
        }
      }
      getParticles().clear();
      for (Entity entity : getCenter().getWorld().getEntities()) {
        if (isInPlotRange(entity.getLocation(), 3)) {
          if (JavaPlugin.getPlugin(Main.class).getServer().getPluginManager().isPluginEnabled("Citizens")) {
            if (CitizensAPI.getNPCRegistry().isNPC(entity)) return;
          }
          if (entity.getType() != EntityType.PLAYER) {
            entity.remove();
          }
        }
      }
      changeFloor(ConfigPreferences.getDefaultFloorMaterial());
    } catch (Exception ex) {
      new ReportedException(JavaPlugin.getPlugin(Main.class), ex);
    }
  }

  public void resetPlot() {
    try {
      Cuboid cuboid = new Cuboid(getMinPoint(), getMaxPoint());
      for(Block block : cuboid.blockList()){
        if(block.getType() != Material.AIR) {
          block.setType(Material.AIR);
        }
      }
      getParticles().clear();
      for(UUID u : uuids){
        Player p = Bukkit.getPlayer(u);
        if (p.getWorld().hasStorm()) {
          p.setPlayerWeather(WeatherType.DOWNFALL);
          setWeatherType(WeatherType.DOWNFALL);
        } else {
          p.setPlayerWeather(WeatherType.CLEAR);
          setWeatherType(WeatherType.CLEAR);
        }
      }
      for (Entity entity : getCenter().getWorld().getEntities()) {
        if (isInPlotRange(entity.getLocation(), 3)) {
          if (JavaPlugin.getPlugin(Main.class).getServer().getPluginManager().isPluginEnabled("Citizens")) {
            if (CitizensAPI.getNPCRegistry().isNPC(entity)) continue;
          }
          if (entity.getType() != EntityType.PLAYER) {
            entity.remove();
          }
        }
      }
      changeFloor(ConfigPreferences.getDefaultFloorMaterial());
    } catch (Exception ex) {
      new ReportedException(JavaPlugin.getPlugin(Main.class), ex);
    }
  }

  public Location getCenter() {
    double x, y, z;
    if (getMinPoint().getX() > getMaxPoint().getX()) {
      x = getMaxPoint().getX() + ((getMinPoint().getX() - getMaxPoint().getX()) / 2);
    } else {
      x = getMinPoint().getX() + ((getMaxPoint().getX() - getMinPoint().getX()) / 2);
    }
    if (getMinPoint().getY() > getMaxPoint().getY()) {
      y = getMaxPoint().getY() + ((getMinPoint().getY() - getMaxPoint().getY()) / 2);
    } else {
      y = getMinPoint().getY() + ((getMaxPoint().getY() - getMinPoint().getY()) / 2);
    }
    if (getMinPoint().getZ() > getMaxPoint().getZ()) {
      z = getMaxPoint().getZ() + ((getMinPoint().getZ() - getMaxPoint().getZ()) / 2);
    } else {
      z = getMinPoint().getZ() + ((getMaxPoint().getZ() - getMinPoint().getZ()) / 2);
    }
    return new Location(getMinPoint().getWorld(), x, y, z);

  }

  public int getPoints() {
    return points;
  }

  public void setPoints(int points) {
    this.points = points;
  }

  public boolean isInPlot(Location location) {
    boolean trueOrNot = false;
    if (location.getWorld() == getMinPoint().getWorld() && location.getWorld() == getMaxPoint().getWorld()) {
      if (location.getX() >= getMinPoint().getX() && location.getX() <= getMaxPoint().getX()) {
        if (location.getY() >= getMinPoint().getY() && location.getY() <= getMaxPoint().getY()) {
          if (location.getZ() >= getMinPoint().getZ() && location.getZ() <= getMaxPoint().getZ()) {
            trueOrNot = true;
          }
        }
      }
      if (location.getX() <= getMinPoint().getX() && location.getX() >= getMaxPoint().getX()) {
        if (location.getY() <= getMinPoint().getY() && location.getY() >= getMaxPoint().getY()) {
          if (location.getZ() <= getMinPoint().getZ() && location.getZ() >= getMaxPoint().getZ()) {
            trueOrNot = true;
          }
        }
      }
    }
    return trueOrNot;
  }

  private void changeFloor(Material material) {
    try {
      double y;
      if (getMinPoint().getY() > getMaxPoint().getY()) {
        y = getMaxPoint().getY();
      } else {
        y = getMinPoint().getY();
      }
      Cuboid cuboid = new Cuboid(getMinPoint(), getMaxPoint());
      Location min = cuboid.getPoint1();
      Location max = cuboid.getPoint2();
      for (int x = min.getBlockX(); x <= max.getBlockX(); x = x + 1) {
        for (int z = min.getBlockZ(); z <= max.getBlockZ(); z = z + 1) {
          Location tmpblock = new Location(getMaxPoint().getWorld(), x, y, z);
          tmpblock.getBlock().setType(material);
        }
      }
    } catch (Exception ex) {
      new ReportedException(JavaPlugin.getPlugin(Main.class), ex);
    }
  }

  public void changeFloor(Material material, byte data) {
    try {
      if (material == Material.WATER_BUCKET) material = Material.WATER;
      if (material == Material.LAVA_BUCKET) material = Material.LAVA;
      double y;
      if (getMinPoint().getY() > getMaxPoint().getY()) {
        y = getMaxPoint().getY();
      } else {
        y = getMinPoint().getY();
      }
      Cuboid cuboid = new Cuboid(getMinPoint(), getMaxPoint());
      Location min = cuboid.getPoint1();
      Location max = cuboid.getPoint2();
      for (int x = min.getBlockX(); x <= max.getBlockX(); x = x + 1) {
        for (int z = min.getBlockZ(); z <= max.getBlockZ(); z = z + 1) {
          Location tmpblock = new Location(getMaxPoint().getWorld(), x, y, z);
          tmpblock.getBlock().setType(material);
          tmpblock.getBlock().setData(data);
        }
      }
    } catch (Exception ex) {
      new ReportedException(JavaPlugin.getPlugin(Main.class), ex);
    }
  }

  public Material getFloorMaterial() {
    try {
      Location location;
      if (getMinPoint().getY() > getMaxPoint().getY()) {
        location = getMaxPoint().clone();
      } else {
        location = getMinPoint().clone();
      }
      Material material = location.add(0, -1, 0).getBlock().getType();
      if (material == Material.WATER || material == Material.STATIONARY_WATER) return Material.WATER_BUCKET;
      if (material == Material.LAVA || material == Material.STATIONARY_LAVA) return Material.LAVA_BUCKET;
      if (material == Material.AIR || material == null) return Material.REDSTONE_BLOCK;
      return material;
    } catch (Exception ex) {
      new ReportedException(JavaPlugin.getPlugin(Main.class), ex);
      return Material.REDSTONE_BLOCK;
    }
  }

  public boolean isInFlyRange(Player player) {
    boolean trueOrNot = false;
    Location location = player.getLocation();
    if (location.getWorld() == getMinPoint().getWorld() && location.getWorld() == getMaxPoint().getWorld()) {
      if (location.getX() >= getMinPoint().getX() - 5 && location.getX() <= getMaxPoint().getX() + 5) {
        if (location.getY() >= getMinPoint().getY() - 5 && location.getY() <= getMaxPoint().getY() + 5) {
          if (location.getZ() >= getMinPoint().getZ() - 5 && location.getZ() <= getMaxPoint().getZ() + 5) {
            trueOrNot = true;
          }
        }
      }
      if (location.getX() <= getMinPoint().getX() + 5 && location.getX() >= getMaxPoint().getX() - 5) {
        if (location.getY() <= getMinPoint().getY() && location.getY() >= getMaxPoint().getY() - 5) {
          if (location.getZ() <= getMinPoint().getZ() + 5 && location.getZ() >= getMaxPoint().getZ() - 5) {
            trueOrNot = true;
          }
        }
      }
    }
    return trueOrNot;
  }

  public boolean isInPlotRange(Location location, int added) {
    if (location.getWorld() == getMinPoint().getWorld() && location.getWorld() == getMaxPoint().getWorld()) {
      if (location.getX() >= getMinPoint().getX() - added && location.getX() <= getMaxPoint().getX() + added) {
        if (location.getY() >= getMinPoint().getY() - added && location.getY() <= getMaxPoint().getY() + added) {
          if (location.getZ() >= getMinPoint().getZ() - added && location.getZ() <= getMaxPoint().getZ() + added) {
            return true;
          }
        }
      }
      if (location.getX() <= getMinPoint().getX() + 5 && location.getX() >= getMaxPoint().getX() - 5) {
        if (location.getY() <= getMinPoint().getY() && location.getY() >= getMaxPoint().getY() - 5) {
          return location.getZ() <= getMinPoint().getZ() + 5 && location.getZ() >= getMaxPoint().getZ() - 5;
        }
      }
    }
    return false;
  }

  public Location getTeleportLocation() {
    try {
      Location tploc = this.getCenter();
      while (tploc.getBlock().getType() != Material.AIR || tploc.add(0, 1, 0).getBlock().getType() != Material.AIR) tploc = tploc.add(0, 1, 0);
      boolean enclosed = false;
      int counter = 0;
      Location location = tploc.clone();
      while (counter != 10) {
        if (!(location.getBlock().getType() == Material.BARRIER || location.getBlock().getType() == Material.AIR)) {
          enclosed = true;
          tploc = location;
          counter = 9;
        }
        location.add(0, 1, 0);
        counter++;
      }
      if (enclosed) {
        while (tploc.getBlock().getType() != Material.AIR || tploc.add(0, 1, 0).getBlock().getType() != Material.AIR) {
          tploc = tploc.add(0, 1, 0);
        }
      }
      return tploc;
    } catch (Exception ex) {
      new ReportedException(JavaPlugin.getPlugin(Main.class), ex);
      return new Location(Bukkit.getWorld("world"), 0, 0, 0);
    }
  }

}
