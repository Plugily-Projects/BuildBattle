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

package plugily.projects.buildbattle.arena.managers.plots;

import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion;
import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import pl.plajerlair.commonsbox.minecraft.dimensional.Cuboid;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.api.event.plot.BBPlotResetEvent;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tom on 17/08/2015.
 */
public class Plot {

  private static final Main plugin = JavaPlugin.getPlugin(Main.class);
  private final Map<Location, String> particles = new HashMap<>();
  private final BaseArena arena;
  private Cuboid cuboid;
  private int points = 0;
  private List<Player> owners = new ArrayList<>();
  private Time time = Time.WORLD_TIME;
  private final Biome plotDefaultBiome;
  private WeatherType weatherType = WeatherType.CLEAR;
  private int entities = 0;

  public Plot(BaseArena arena, Biome biome) {
    this.arena = arena;
    plotDefaultBiome = biome;
  }

  public int getEntities() {
    return entities;
  }

  public void addEntity() {
    entities++;
  }

  public void removeEntity() {
    if(entities > 0) {
      entities--;
    }
  }

  public Map<Location, String> getParticles() {
    return particles;
  }

  @Deprecated
  public void addParticle(Location location, String effect) {
    particles.put(location, effect);
  }

  public Biome getPlotDefaultBiome() {
    return plotDefaultBiome;
  }

  public WeatherType getWeatherType() {
    return weatherType;
  }

  public void setWeatherType(WeatherType weatherType) {
    this.weatherType = weatherType;
  }

  public Time getTime() {
    return time;
  }

  public void setTime(Time time) {
    this.time = time;
  }

  public Cuboid getCuboid() {
    return cuboid;
  }

  public void setCuboid(Cuboid cuboid) {
    this.cuboid = cuboid;
  }

  @NotNull
  public List<Player> getOwners() {
    return owners;
  }

  public void setOwners(List<Player> players) {
    this.owners = players == null ? new ArrayList<>() : players;
  }

  public void addOwner(Player player) {
    owners.add(player);
  }

  public void fullyResetPlot() {
    resetPlot();
    if(!owners.isEmpty()) {
      for(Player p : owners) {
        plugin.getUserManager().getUser(p).setCurrentPlot(null);
        setOwners(new ArrayList<>());
        setPoints(0);
      }
    }
    getParticles().clear();
  }

  public void resetPlot() {
    if(cuboid == null) {
      return;
    }

    for(Block block : cuboid.blockList()) {
      //to ensure 1.14 blocks support (that will be seen as air in api-version 1.13)
      //we set all blocks to air so 1.14 ones will update too
      block.setType(Material.AIR);
    }

    getParticles().clear();

    for(Player p : owners) {
      p.resetPlayerWeather();
      setWeatherType(p.getPlayerWeather());
      p.resetPlayerTime();
    }

    Location center = cuboid.getCenter();
    if(center.getWorld() != null) {
      for(Entity entity : center.getWorld().getEntities()) {
        if(cuboid.isInWithMarge(entity.getLocation(), 5)) {
          if(plugin.getServer().getPluginManager().isPluginEnabled("Citizens") && CitizensAPI.getNPCRegistry() != null
              && CitizensAPI.getNPCRegistry().isNPC(entity)) {
            continue;
          }

          if(entity.getType() != EntityType.PLAYER) {
            entity.remove();
          }
        }
      }
    }

    for(Block block : cuboid.blockList()) {
      block.setBiome(plotDefaultBiome);
    }

    for(Chunk chunk : cuboid.chunkList()) {
      for(Player p : Bukkit.getOnlinePlayers()) {
        if(p.getWorld().equals(chunk.getWorld())) {
          Utils.sendMapChunk(p, chunk);
        }
      }
    }

    changeFloor(XMaterial.matchXMaterial(plugin.getConfig().getString("Default-Floor-Material-Name", "LOG")
        .toUpperCase()).orElse(XMaterial.OAK_LOG).parseMaterial());

    if(ServerVersion.Version.isCurrentHigher(ServerVersion.Version.v1_15_R1)) {
      int y = Math.min(cuboid.getMinPoint().getBlockY(), cuboid.getMaxPoint().getBlockY());

      center.getWorld().setBiome(cuboid.getMinPoint().getBlockX(), y, cuboid.getMaxPoint().getBlockZ(), plotDefaultBiome);
    } else {
      center.getWorld().setBiome(cuboid.getMinPoint().getBlockX(), cuboid.getMaxPoint().getBlockZ(), plotDefaultBiome);
    }

    Bukkit.getServer().getPluginManager().callEvent(new BBPlotResetEvent(arena, this));
  }

  public int getPoints() {
    return points;
  }

  public void setPoints(int points) {
    this.points = points;
  }

  private void changeFloor(Material material) {
    Location min = cuboid.getMinPoint();
    Location max = cuboid.getMaxPoint();
    double y = Math.min(min.getY(), max.getY());
    for(int x = min.getBlockX(); x <= max.getBlockX(); x++) {
      for(int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
        Location tmpblock = new Location(max.getWorld(), x, y, z);
        tmpblock.getBlock().setType(material);
      }
    }
  }

  public void changeFloor(Material material, byte data) {
    if(material == Material.WATER_BUCKET || material == Material.MILK_BUCKET) {
      material = Material.WATER;
    }
    if(material == Material.LAVA_BUCKET) {
      material = Material.LAVA;
    }
    double y = Math.min(cuboid.getMinPoint().getY(), cuboid.getMaxPoint().getY());
    Location min = cuboid.getMinPoint();
    Location max = cuboid.getMaxPoint();
    for(int x = min.getBlockX(); x <= max.getBlockX(); x++) {
      for(int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
        Location tmpblock = new Location(cuboid.getMaxPoint().getWorld(), x, y, z);
        tmpblock.getBlock().setType(material);
        if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_12_R1)) {
          try {
            Block.class.getMethod("setData", byte.class).invoke(tmpblock.getBlock(), data);
          } catch(Exception e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  public Location getTeleportLocation() {
    Location tploc = cuboid.getCenter();
    while(tploc.getBlock().getType() != Material.AIR || tploc.add(0, 1, 0).getBlock().getType() != Material.AIR) {
      tploc = tploc.add(0, 1, 0);
    }
    boolean enclosed = false;
    int counter = 0;
    Location location = tploc.clone();
    while(counter != 10) {
      if(!(location.getBlock().getType() == Material.BARRIER || location.getBlock().getType() == Material.AIR)) {
        enclosed = true;
        tploc = location;
        counter = 9;
      }
      location.add(0, 1, 0);
      counter++;
    }
    if(enclosed) {
      while(tploc.getBlock().getType() != Material.AIR || tploc.add(0, 1, 0).getBlock().getType() != Material.AIR) {
        tploc = tploc.add(0, 1, 0);
      }
    }
    return tploc;
  }

  /**
   * Enum that represents current plot time
   */
  public enum Time {
    WORLD_TIME(-1), DAY(1000), NOON(6000), SUNSET(12000), SUNRISE(23000), NIGHT(13000), MIDNIGHT(18000);

    private final long ticks;

    Time(long ticks) {
      this.ticks = ticks;
    }

    public static long format(Time time, long currTime) {
      return time == Time.WORLD_TIME ? currTime : time.getTicks();
    }

    public long getTicks() {
      return ticks;
    }
  }

}
