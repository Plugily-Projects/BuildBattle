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

package pl.plajer.buildbattle3.plots;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.buildbattle3.ConfigPreferences;
import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.user.User;
import pl.plajer.buildbattle3.user.UserManager;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Tom on 17/08/2015.
 */
public class Plot {

    private Location maxPoint;
    private Location minPoint;
    private int points;
    private UUID uuid;
    private HashMap<Location, Particle> particles = new HashMap<>();
    private int entities = 0;

    public Plot() {}

    public int getEntities() {
        return entities;
    }

    public void addEntity() {
        entities++;
    }

    public void removeEntity() {
        if(entities == 0) return;
        entities--;
    }

    public HashMap<Location, Particle> getParticles() {
        return particles;
    }

    public void addParticle(Location location, Particle effect) {
        particles.put(location, effect);
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

    public UUID getOwner() {
        return uuid;
    }

    public void setOwner(UUID player) {
        this.uuid = player;
    }

    public void fullyResetPlot() {
        CuboidSelection cuboidSelection = new CuboidSelection(getMaxPoint().getWorld(), getMaxPoint(), getMinPoint());
        com.sk89q.worldedit.Vector min = cuboidSelection.getNativeMinimumPoint();
        com.sk89q.worldedit.Vector max = cuboidSelection.getNativeMaximumPoint();
        for(int x = min.getBlockX(); x <= max.getBlockX(); x = x + 1) {
            for(int y = min.getBlockY(); y <= max.getBlockY(); y = y + 1) {
                for(int z = min.getBlockZ(); z <= max.getBlockZ(); z = z + 1) {
                    Location tmpblock = new Location(getMaxPoint().getWorld(), x, y, z);
                    if(tmpblock.getBlock().getType() != Material.AIR) {
                        tmpblock.getBlock().setType(Material.AIR);

                    }
                }
            }
        }
        changeFloor(Material.getMaterial(ConfigPreferences.getDefaultFloorMaterial()));
        if(uuid != null) {
            User user = UserManager.getUser(uuid);
            user.setObject(null, "plot");
            this.setOwner(null);
            this.setPoints(0);
        }
        getParticles().clear();
        for(Entity entity : getCenter().getWorld().getEntities()) {
            if(isInPlotRange(entity.getLocation(), 3)) {
                if(JavaPlugin.getPlugin(Main.class).getServer().getPluginManager().isPluginEnabled("Citizens")) {
                    if(CitizensAPI.getNPCRegistry().isNPC(entity)) return;
                }
                if(entity.getType() != EntityType.PLAYER) {
                    entity.remove();
                }
            }
        }
    }

    public void resetPlot() {
        CuboidSelection cuboidSelection = new CuboidSelection(getMaxPoint().getWorld(), getMaxPoint(), getMinPoint());
        com.sk89q.worldedit.Vector min = cuboidSelection.getNativeMinimumPoint();
        com.sk89q.worldedit.Vector max = cuboidSelection.getNativeMaximumPoint();
        for(int x = min.getBlockX(); x <= max.getBlockX(); x = x + 1) {
            for(int y = min.getBlockY(); y <= max.getBlockY(); y = y + 1) {
                for(int z = min.getBlockZ(); z <= max.getBlockZ(); z = z + 1) {
                    Location tmpblock = new Location(getMaxPoint().getWorld(), x, y, z);
                    if(tmpblock.getBlock().getType() != Material.AIR) {
                        tmpblock.getBlock().setType(Material.AIR);

                    }
                }
            }
        }
        changeFloor(Material.getMaterial(ConfigPreferences.getDefaultFloorMaterial()));
        getParticles().clear();
        for(Entity entity : getCenter().getWorld().getEntities()) {
            if(isInPlotRange(entity.getLocation(), 3)) {
                if(JavaPlugin.getPlugin(Main.class).getServer().getPluginManager().isPluginEnabled("Citizens")) {
                    if(CitizensAPI.getNPCRegistry().isNPC(entity)) continue;
                }
                if(entity.getType() != EntityType.PLAYER) {
                    entity.remove();
                }
            }
        }
    }

    public Location getCenter() {
        double x, y, z;
        if(getMinPoint().getX() > getMaxPoint().getX()) {
            x = getMaxPoint().getX() + ((getMinPoint().getX() - getMaxPoint().getX()) / 2);
        } else {
            x = getMinPoint().getX() + ((getMaxPoint().getX() - getMinPoint().getX()) / 2);
        }
        if(getMinPoint().getY() > getMaxPoint().getY()) {
            y = getMaxPoint().getY() + ((getMinPoint().getY() - getMaxPoint().getY()) / 2);
        } else {
            y = getMinPoint().getY() + ((getMaxPoint().getY() - getMinPoint().getY()) / 2);
        }
        if(getMinPoint().getZ() > getMaxPoint().getZ()) {
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
        if(location.getWorld() == getMinPoint().getWorld() && location.getWorld() == getMaxPoint().getWorld()) {
            if(location.getX() >= getMinPoint().getX() && location.getX() <= getMaxPoint().getX()) {
                if(location.getY() >= getMinPoint().getY() && location.getY() <= getMaxPoint().getY()) {
                    if(location.getZ() >= getMinPoint().getZ() && location.getZ() <= getMaxPoint().getZ()) {
                        trueOrNot = true;
                    }
                }
            }
            if(location.getX() <= getMinPoint().getX() && location.getX() >= getMaxPoint().getX()) {
                if(location.getY() <= getMinPoint().getY() && location.getY() >= getMaxPoint().getY()) {
                    if(location.getZ() <= getMinPoint().getZ() && location.getZ() >= getMaxPoint().getZ()) {
                        trueOrNot = true;
                    }
                }
            }
        }
        return trueOrNot;
    }

    private void changeFloor(Material material) {
        double y;
        if(getMinPoint().getY() > getMaxPoint().getY()) {
            y = getMaxPoint().getY() - 1;
        } else {
            y = getMinPoint().getY() - 1;
        }
        CuboidSelection cuboidSelection = new CuboidSelection(getMaxPoint().getWorld(), getMaxPoint(), getMinPoint());
        com.sk89q.worldedit.Vector min = cuboidSelection.getNativeMinimumPoint();
        com.sk89q.worldedit.Vector max = cuboidSelection.getNativeMaximumPoint();
        for(int x = min.getBlockX(); x <= max.getBlockX(); x = x + 1) {
            for(int z = min.getBlockZ(); z <= max.getBlockZ(); z = z + 1) {
                Location tmpblock = new Location(getMaxPoint().getWorld(), x, y, z);
                tmpblock.getBlock().setType(material);
            }
        }
    }

    public void changeFloor(Material material, byte data) {
        if(material == Material.WATER_BUCKET) material = Material.WATER;
        if(material == Material.LAVA_BUCKET) material = Material.LAVA;
        double y;
        if(getMinPoint().getY() > getMaxPoint().getY()) {
            y = getMaxPoint().getY() - 1;
        } else {
            y = getMinPoint().getY() - 1;
        }
        CuboidSelection cuboidSelection = new CuboidSelection(getMaxPoint().getWorld(), getMaxPoint(), getMinPoint());
        com.sk89q.worldedit.Vector min = cuboidSelection.getNativeMinimumPoint();
        com.sk89q.worldedit.Vector max = cuboidSelection.getNativeMaximumPoint();
        for(int x = min.getBlockX(); x <= max.getBlockX(); x = x + 1) {
            for(int z = min.getBlockZ(); z <= max.getBlockZ(); z = z + 1) {
                Location tmpblock = new Location(getMaxPoint().getWorld(), x, y, z);
                tmpblock.getBlock().setType(material);
                tmpblock.getBlock().setData(data);
            }
        }
    }

    public Material getFloorMaterial() {
        Location location;
        if(getMinPoint().getY() > getMaxPoint().getY()) {
            location = getMaxPoint().clone();
        } else {
            location = getMinPoint().clone();
        }
        Material material = location.add(0, -1, 0).getBlock().getType();
        if(material == Material.WATER || material == Material.STATIONARY_WATER) return Material.WATER_BUCKET;
        if(material == Material.LAVA || material == Material.STATIONARY_LAVA) return Material.LAVA_BUCKET;
        if(material == Material.AIR || material == null) return Material.REDSTONE_BLOCK;
        return material;
    }

    public boolean isInFlyRange(Player player) {
        boolean trueOrNot = false;
        Location location = player.getLocation();
        if(location.getWorld() == getMinPoint().getWorld() && location.getWorld() == getMaxPoint().getWorld()) {
            if(location.getX() >= getMinPoint().getX() - 5 && location.getX() <= getMaxPoint().getX() + 5) {
                if(location.getY() >= getMinPoint().getY() - 5 && location.getY() <= getMaxPoint().getY() + 5) {
                    if(location.getZ() >= getMinPoint().getZ() - 5 && location.getZ() <= getMaxPoint().getZ() + 5) {
                        trueOrNot = true;
                    }
                }
            }
            if(location.getX() <= getMinPoint().getX() + 5 && location.getX() >= getMaxPoint().getX() - 5) {
                if(location.getY() <= getMinPoint().getY() && location.getY() >= getMaxPoint().getY() - 5) {
                    if(location.getZ() <= getMinPoint().getZ() + 5 && location.getZ() >= getMaxPoint().getZ() - 5) {
                        trueOrNot = true;
                    }
                }
            }
        }
        return trueOrNot;
    }

    public boolean isInPlotRange(Location location, int added) {
        if(location.getWorld() == getMinPoint().getWorld() && location.getWorld() == getMaxPoint().getWorld()) {
            if(location.getX() >= getMinPoint().getX() - added && location.getX() <= getMaxPoint().getX() + added) {
                if(location.getY() >= getMinPoint().getY() - added && location.getY() <= getMaxPoint().getY() + added) {
                    if(location.getZ() >= getMinPoint().getZ() - added && location.getZ() <= getMaxPoint().getZ() + added) {
                        return true;
                    }
                }
            }
            if(location.getX() <= getMinPoint().getX() + 5 && location.getX() >= getMaxPoint().getX() - 5) {
                if(location.getY() <= getMinPoint().getY() && location.getY() >= getMaxPoint().getY() - 5) {
                    return location.getZ() <= getMinPoint().getZ() + 5 && location.getZ() >= getMaxPoint().getZ() - 5;
                }
            }
        }
        return false;
    }

    public Location getTeleportLocation() {
        Location tploc = this.getCenter();
        while(tploc.getBlock().getType() != Material.AIR || tploc.add(0, 1, 0).getBlock().getType() != Material.AIR) tploc = tploc.add(0, 1, 0);
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

}
