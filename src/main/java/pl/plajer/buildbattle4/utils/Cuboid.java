/*
 * BuildBattle 4 - Ultimate building competition minigame
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.buildbattle4.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Cuboid {

  private final int xMin;
  private final int xMax;
  private final int yMin;
  private final int yMax;
  private final int zMin;
  private final int zMax;
  private final double xMinCentered;
  private final double xMaxCentered;
  private final double yMinCentered;
  private final double yMaxCentered;
  private final double zMinCentered;
  private final double zMaxCentered;
  private final World world;

  public Cuboid(final Location point1, final Location point2) {
    this.xMin = Math.min(point1.getBlockX(), point2.getBlockX());
    this.xMax = Math.max(point1.getBlockX(), point2.getBlockX());
    this.yMin = Math.min(point1.getBlockY(), point2.getBlockY());
    this.yMax = Math.max(point1.getBlockY(), point2.getBlockY());
    this.zMin = Math.min(point1.getBlockZ(), point2.getBlockZ());
    this.zMax = Math.max(point1.getBlockZ(), point2.getBlockZ());
    this.world = point1.getWorld();
    this.xMinCentered = this.xMin + 0.5;
    this.xMaxCentered = this.xMax + 0.5;
    this.yMinCentered = this.yMin + 0.5;
    this.yMaxCentered = this.yMax + 0.5;
    this.zMinCentered = this.zMin + 0.5;
    this.zMaxCentered = this.zMax + 0.5;
  }

  public List<Block> blockList() {
    final List<Block> bL = new ArrayList<>(this.getTotalBlockSize());
    for (int x = this.xMin; x <= this.xMax; ++x) {
      for (int y = this.yMin; y <= this.yMax; ++y) {
        for (int z = this.zMin; z <= this.zMax; ++z) {
          final Block b = this.world.getBlockAt(x, y, z);
          bL.add(b);
        }
      }
    }
    return bL;
  }

  public Location getCenter() {
    return new Location(this.world, (this.xMax - this.xMin) / 2 + this.xMin, (this.yMax - this.yMin) / 2 + this.yMin, (this.zMax - this.zMin) / 2 + this.zMin);
  }

  public double getDistance() {
    return this.getMinPoint().distance(this.getMaxPoint());
  }

  public double getDistanceSquared() {
    return this.getMinPoint().distanceSquared(this.getMaxPoint());
  }

  public int getHeight() {
    return this.yMax - this.yMin + 1;
  }

  public Location getMinPoint() {
    return new Location(this.world, this.xMin, this.yMin, this.zMin);
  }

  public Location getMaxPoint() {
    return new Location(this.world, this.xMax, this.yMax, this.zMax);
  }

  public Location getRandomLocation() {
    final Random rand = new Random();
    final int x = rand.nextInt(Math.abs(this.xMax - this.xMin) + 1) + this.xMin;
    final int y = rand.nextInt(Math.abs(this.yMax - this.yMin) + 1) + this.yMin;
    final int z = rand.nextInt(Math.abs(this.zMax - this.zMin) + 1) + this.zMin;
    return new Location(this.world, x, y, z);
  }

  public int getTotalBlockSize() {
    return this.getHeight() * this.getXWidth() * this.getZWidth();
  }

  public int getXWidth() {
    return this.xMax - this.xMin + 1;
  }

  public int getZWidth() {
    return this.zMax - this.zMin + 1;
  }

  public boolean isIn(final Location loc) {
    return loc.getWorld() == this.world && loc.getBlockX() >= this.xMin && loc.getBlockX() <= this.xMax && loc.getBlockY() >= this.yMin && loc.getBlockY() <= this.yMax && loc
            .getBlockZ() >= this.zMin && loc.getBlockZ() <= this.zMax;
  }

  public boolean isIn(final Player player) {
    return this.isIn(player.getLocation());
  }

  public boolean isInWithMarge(final Location loc, final double marge) {
    return loc.getWorld() == this.world && loc.getX() >= this.xMinCentered - marge && loc.getX() <= this.xMaxCentered + marge && loc.getY() >= this.yMinCentered - marge && loc
            .getY() <= this.yMaxCentered + marge && loc.getZ() >= this.zMinCentered - marge && loc.getZ() <= this.zMaxCentered + marge;
  }
}
