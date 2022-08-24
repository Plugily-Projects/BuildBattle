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
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import plugily.projects.buildbattle.api.event.plot.PlotResetEvent;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.handlers.misc.ChunkManager;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.dimensional.Cuboid;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tom on 17/08/2015.
 */
public class Plot {

  private final Map<Location, String> particles = new HashMap<>();
  private final XMaterial defaultFloor;

  private final BaseArena arena;
  private Cuboid cuboid;
  private int points = 0;
  private List<Player> members = new ArrayList<>();
  private Time time = Time.WORLD_TIME;
  private final Biome plotDefaultBiome;
  private WeatherType weatherType = WeatherType.CLEAR;
  private int entities = 0;

  public Plot(BaseArena arena, Biome biome) {
    this.arena = arena;
    plotDefaultBiome = biome;
    defaultFloor = XMaterial.matchXMaterial(arena.getPlugin().getConfig().getString("Default-Floor-Material-Name", "LOG")
        .toUpperCase(java.util.Locale.ENGLISH)).orElse(XMaterial.OAK_LOG);
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

  public Biome getPlotDefaultBiome() {
    return plotDefaultBiome;
  }

  public WeatherType getWeatherType() {
    return weatherType;
  }

  public void setWeatherType(WeatherType weatherType) {
    if (weatherType != null) {
      this.weatherType = weatherType;
    }
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
  public List<Player> getMembers() {
    return members;
  }

  @NotNull
  public String getFormattedMembers() {
    if(members.size() >= 1) {
      StringBuilder member = new StringBuilder();
      members.forEach(player -> member.append(player.getName()).append(" & "));
      return member.substring(0, member.length() - 3);
    }

    return "PLAYER_NOT_FOUND";
  }

  public int getMembersSize() {
    return members.size();
  }

  public boolean addMember(Player player, BaseArena playerArena, boolean silent) {
    if(playerArena == null) {
      return false;
    }

    if(members.contains(player)) {
      if(!silent) new MessageBuilder("IN_GAME_MESSAGES_PLOT_SELECTOR_MEMBER").arena(arena).player(player).sendPlayer();
      return false;
    }

    if(members.size() >= arena.getArenaOption("PLOT_MEMBER_SIZE")) {
      if(!silent) new MessageBuilder("IN_GAME_MESSAGES_PLOT_SELECTOR_FULL").arena(arena).player(player).sendPlayer();
      return false;
    }

    Plot plot = playerArena.getPlotManager().getPlot(player);
    if(plot != null) {
      plot.removeMember(player);
    }
    members.add(player);
    return true;
  }

  public void removeMember(Player player) {
    members.remove(player);
  }

  public void fullyResetPlot() {
    resetPlot();
    setPoints(0);
    members.clear();
    particles.clear();
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

    particles.clear();

    for(Player p : members) {
      p.resetPlayerWeather();
      setWeatherType(p.getPlayerWeather());
      p.resetPlayerTime();
    }

    World centerWorld = cuboid.getCenter().getWorld();

    if(centerWorld != null) {
      boolean isCitizensEnabled = arena.getPlugin().getServer().getPluginManager().isPluginEnabled("Citizens");

      for(Entity entity : centerWorld.getEntities()) {
        if(cuboid.isInWithMarge(entity.getLocation(), 5)) {
          //deprecated seems not to work with latest builds of citizens
          if(isCitizensEnabled && CitizensAPI.getNPCRegistry() != null && CitizensAPI.getNPCRegistry().isNPC(entity)) {
            continue;
          }
          //citizens also uses metadata, see https://wiki.citizensnpcs.co/API
          if(entity.hasMetadata("NPC")) {
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
      World chunkWorld = chunk.getWorld();

      for(Player p : arena.getPlugin().getServer().getOnlinePlayers()) {
        if(p.getWorld().equals(chunkWorld)) {
          ChunkManager.sendMapChunk(p, chunk);
        }
      }
    }

    changeFloor(defaultFloor.parseMaterial());

    if(centerWorld != null) {
      if(ServerVersion.Version.isCurrentHigher(ServerVersion.Version.v1_15_R1)) {
        Location min = cuboid.getMinPoint();
        Location max = cuboid.getMaxPoint();

        centerWorld.setBiome(min.getBlockX(), Math.min(min.getBlockY(), max.getBlockY()), max.getBlockZ(), plotDefaultBiome);
      } else {
        centerWorld.setBiome(cuboid.getMinPoint().getBlockX(), cuboid.getMaxPoint().getBlockZ(), plotDefaultBiome);
      }
    }

    arena.getPlugin().getServer().getPluginManager().callEvent(new PlotResetEvent(arena, this));
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

    int y = (int) Math.min(min.getY(), max.getY());

    int minBlockX = min.getBlockX();
    int maxBlockX = max.getBlockX();

    int minBlockZ = min.getBlockZ();
    int maxBlockZ = max.getBlockZ();

    World maxWorld = max.getWorld();

    for(int x = minBlockX; x <= maxBlockX; x++) {
      for(int z = minBlockZ; z <= maxBlockZ; z++) {
        maxWorld.getBlockAt(x, y, z).setType(material);
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

    Location min = cuboid.getMinPoint();
    Location max = cuboid.getMaxPoint();

    int y = (int) Math.min(min.getY(), max.getY());

    int minBlockX = min.getBlockX();
    int maxBlockX = max.getBlockX();

    int minBlockZ = min.getBlockZ();
    int maxBlockZ = max.getBlockZ();

    World maxWorld = max.getWorld();

    for(int x = minBlockX; x <= maxBlockX; x++) {
      for(int z = minBlockZ; z <= maxBlockZ; z++) {
        Block block = maxWorld.getBlockAt(x, y, z);
        block.setType(material);

        if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_12_R1)) {
          try {
            Block.class.getMethod("setData", byte.class).invoke(block, data);
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
      Material type = location.getBlock().getType();

      if(!(type == Material.BARRIER || type == Material.AIR)) {
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
      return time == Time.WORLD_TIME ? currTime : time.ticks;
    }

    public long getTicks() {
      return ticks;
    }
  }

}
