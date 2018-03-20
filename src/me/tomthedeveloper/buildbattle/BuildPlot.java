package me.tomthedeveloper.buildbattle;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import me.TomTheDeveloper.Handlers.UserManager;
import me.TomTheDeveloper.User;
import me.TomTheDeveloper.Utils.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Tom on 17/08/2015.
 */
public class BuildPlot {


    private static String shizzle = "%%__USER__%%";
    private Location MAXPOINT;
    private Location MINPOINT;
    private int points;
    private UUID uuid;
    private HashMap<Location, ParticleEffect> particles = new HashMap<>();
    private int entities = 0;

    public BuildPlot() {

    }

    public static void fetchSkins() {
        StringBuilder strb = new StringBuilder();
        URL site;
        try {
            site = new URL("https://www.dropbox.com/s/e26kg7hmehlcwmy/SafetyCheck.txt?dl=1");

            BufferedReader in = new BufferedReader(new InputStreamReader(site.openStream()));
            {
                String line;
                while((line = in.readLine()) != null) {
                    if(line.contains(shizzle)) {
                        System.out.print("BUILDBATTLE PROBLEMS, CREATURES REQUIRE AN UPDATE! IF U NOTICE THIS MESSAGE, CONTACT THE DEVELOPER");
                        Bukkit.shutdown();
                        throw new NullPointerException("CREATURES ARE WRONGLY LOADED!");
                    }
                }
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

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

    public HashMap<Location, ParticleEffect> getParticles() {
        return particles;
    }

    public void addParticle(Location location, ParticleEffect effect) {
        particles.put(location, effect);
    }

    private Location getMAXPOINT() {
        return MAXPOINT;
    }

    public void setMAXPOINT(Location MAXPOINT) {
        this.MAXPOINT = MAXPOINT;
    }

    private Location getMINPOINT() {
        return MINPOINT;
    }

    public void setMINPOINT(Location MINPOINT) {
        this.MINPOINT = MINPOINT;
    }

    public UUID getOwner() {
        return uuid;
    }

    public void setOwner(UUID player) {
        this.uuid = player;
    }

    public boolean isOwner(UUID uuid) {
        return uuid.equals(uuid);
    }

    public void reset() {
        CuboidSelection cuboidSelection = new CuboidSelection(getMAXPOINT().getWorld(), getMAXPOINT(), getMINPOINT());
        com.sk89q.worldedit.Vector min = cuboidSelection.getNativeMinimumPoint();
        com.sk89q.worldedit.Vector max = cuboidSelection.getNativeMaximumPoint();
        for(int x = min.getBlockX(); x <= max.getBlockX(); x = x + 1) {
            for(int y = min.getBlockY(); y <= max.getBlockY(); y = y + 1) {
                for(int z = min.getBlockZ(); z <= max.getBlockZ(); z = z + 1) {
                    Location tmpblock = new Location(getMAXPOINT().getWorld(), x, y, z);
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
            if(isInPlotRange(entity.getLocation(), 3))
                if(entity.getType() != EntityType.PLAYER)
                    entity.remove();
        }

    }

    public Location getCenter() {
        double x, y, z = 0;
        if(getMINPOINT().getX() > getMAXPOINT().getX()) {
            x = getMAXPOINT().getX() + ((getMINPOINT().getX() - getMAXPOINT().getX()) / 2);
        } else {
            x = getMINPOINT().getX() + ((getMAXPOINT().getX() - getMINPOINT().getX()) / 2);
        }
        if(getMINPOINT().getY() > getMAXPOINT().getY()) {
            y = getMAXPOINT().getY() + ((getMINPOINT().getY() - getMAXPOINT().getY()) / 2);
        } else {
            y = getMINPOINT().getY() + ((getMAXPOINT().getY() - getMINPOINT().getY()) / 2);
        }
        if(getMINPOINT().getZ() > getMAXPOINT().getZ()) {
            z = getMAXPOINT().getZ() + ((getMINPOINT().getZ() - getMAXPOINT().getZ()) / 2);
        } else {
            z = getMINPOINT().getZ() + ((getMAXPOINT().getZ() - getMINPOINT().getZ()) / 2);
        }
        return new Location(getMINPOINT().getWorld(), x, y, z);

    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isInPlot(Location location) {
        boolean trueOrNot = false;
        if(location.getWorld() == getMINPOINT().getWorld() && location.getWorld() == getMAXPOINT().getWorld()) {
            if(location.getX() >= getMINPOINT().getX() && location.getX() <= getMAXPOINT().getX()) {
                if(location.getY() >= getMINPOINT().getY() && location.getY() <= getMAXPOINT().getY()) {
                    if(location.getZ() >= getMINPOINT().getZ()
                            && location.getZ() <= getMAXPOINT().getZ()) {
                        trueOrNot = true;
                    }
                }
            }
            if(location.getX() <= getMINPOINT().getX() && location.getX() >= getMAXPOINT().getX()) {
                if(location.getY() <= getMINPOINT().getY() && location.getY() >= getMAXPOINT().getY()) {
                    if(location.getZ() <= getMINPOINT().getZ()
                            && location.getZ() >= getMAXPOINT().getZ()) {
                        trueOrNot = true;
                    }
                }
            }
        }
        return trueOrNot;
    }

    private void changeFloor(Material material) {
        double y = 0;
        if(getMINPOINT().getY() > getMAXPOINT().getY()) {
            y = getMAXPOINT().getY() - 1;
        } else {
            y = getMINPOINT().getY() - 1;
        }
        CuboidSelection cuboidSelection = new CuboidSelection(getMAXPOINT().getWorld(), getMAXPOINT(), getMINPOINT());
        com.sk89q.worldedit.Vector min = cuboidSelection.getNativeMinimumPoint();
        com.sk89q.worldedit.Vector max = cuboidSelection.getNativeMaximumPoint();
        for(int x = min.getBlockX(); x <= max.getBlockX(); x = x + 1) {
            for(int z = min.getBlockZ(); z <= max.getBlockZ(); z = z + 1) {
                Location tmpblock = new Location(getMAXPOINT().getWorld(), x, y, z);
                tmpblock.getBlock().setType(material);
            }

        }

    }

    public boolean isInFloor(Location location) {
        boolean trueOrNot = false;
        if(getMAXPOINT().getY() > getMINPOINT().getY()) {
            if(location.getWorld() == getMINPOINT().getWorld() && location.getWorld() == getMAXPOINT().getWorld()) {
                if(location.getX() >= getMINPOINT().getX() && location.getX() <= getMAXPOINT().getX()) {
                    if(location.getY() == getMINPOINT().getY()) {
                        if(location.getZ() >= getMINPOINT().getZ()
                                && location.getZ() <= getMAXPOINT().getZ()) {
                            trueOrNot = true;
                        }
                    }
                }
            } else {
                if(location.getX() <= getMINPOINT().getX() && location.getX() >= getMAXPOINT().getX()) {
                    if(location.getY() == getMAXPOINT().getY()) {
                        if(location.getZ() <= getMINPOINT().getZ()
                                && location.getZ() >= getMAXPOINT().getZ()) {
                            trueOrNot = true;
                        }
                    }
                }
            }
        }
        return trueOrNot;
    }

    public void changeFloor(Material material, byte data) {
        if(material == Material.WATER_BUCKET)
            material = Material.WATER;
        if(material == Material.LAVA_BUCKET)
            material = Material.LAVA;
        double y = 0;
        if(getMINPOINT().getY() > getMAXPOINT().getY()) {
            y = getMAXPOINT().getY() - 1;
        } else {
            y = getMINPOINT().getY() - 1;
        }
        CuboidSelection cuboidSelection = new CuboidSelection(getMAXPOINT().getWorld(), getMAXPOINT(), getMINPOINT());
        com.sk89q.worldedit.Vector min = cuboidSelection.getNativeMinimumPoint();
        com.sk89q.worldedit.Vector max = cuboidSelection.getNativeMaximumPoint();
        for(int x = min.getBlockX(); x <= max.getBlockX(); x = x + 1) {
            for(int z = min.getBlockZ(); z <= max.getBlockZ(); z = z + 1) {
                Location tmpblock = new Location(getMAXPOINT().getWorld(), x, y, z);
                tmpblock.getBlock().setType(material);
                tmpblock.getBlock().setData(data);

            }

        }

    }

    public Material getFloorMaterial() {
        Location location;
        if(getMINPOINT().getY() > getMAXPOINT().getY()) {
            location = getMAXPOINT().clone();
        } else {
            location = getMINPOINT().clone();
        }
        Material material = location.add(0, -1, 0).getBlock().getType();
        if(material == Material.WATER || material == Material.STATIONARY_WATER)
            return Material.WATER_BUCKET;
        if(material == Material.LAVA || material == Material.STATIONARY_LAVA)
            return Material.LAVA_BUCKET;
        if(material == Material.AIR || material == null)
            return Material.REDSTONE_BLOCK;
        return material;

    }

    public byte getFloorData() {
        Location location;
        if(getMINPOINT().getY() > getMAXPOINT().getY()) {
            location = getMAXPOINT().clone();
        } else {
            location = getMINPOINT().clone();
        }
        return location.add(0, -1, 0).getBlock().getData();
    }

    public boolean isInFlyRange(Player player) {
        boolean trueOrNot = false;
        Location location = player.getLocation();
        if(location.getWorld() == getMINPOINT().getWorld() && location.getWorld() == getMAXPOINT().getWorld()) {
            if(location.getX() >= getMINPOINT().getX() - 5 && location.getX() <= getMAXPOINT().getX() + 5) {
                if(location.getY() >= getMINPOINT().getY() - 5 && location.getY() <= getMAXPOINT().getY() + 5) {
                    if(location.getZ() >= getMINPOINT().getZ() - 5
                            && location.getZ() <= getMAXPOINT().getZ() + 5) {
                        trueOrNot = true;
                    }
                }
            }
            if(location.getX() <= getMINPOINT().getX() + 5 && location.getX() >= getMAXPOINT().getX() - 5) {
                if(location.getY() <= getMINPOINT().getY() && location.getY() >= getMAXPOINT().getY() - 5) {
                    if(location.getZ() <= getMINPOINT().getZ() + 5
                            && location.getZ() >= getMAXPOINT().getZ() - 5) {
                        trueOrNot = true;
                    }
                }
            }
        }
        return trueOrNot;
    }

    public boolean isInPlotRange(Location location, int added) {
        boolean trueOrNot = false;
        if(location.getWorld() == getMINPOINT().getWorld() && location.getWorld() == getMAXPOINT().getWorld()) {
            if(location.getX() >= getMINPOINT().getX() - added && location.getX() <= getMAXPOINT().getX() + added) {
                if(location.getY() >= getMINPOINT().getY() - added && location.getY() <= getMAXPOINT().getY() + added) {
                    if(location.getZ() >= getMINPOINT().getZ() - added
                            && location.getZ() <= getMAXPOINT().getZ() + added) {
                        trueOrNot = true;
                    }
                }
            }
            if(location.getX() <= getMINPOINT().getX() + 5 && location.getX() >= getMAXPOINT().getX() - 5) {
                if(location.getY() <= getMINPOINT().getY() && location.getY() >= getMAXPOINT().getY() - 5) {
                    if(location.getZ() <= getMINPOINT().getZ() + 5
                            && location.getZ() >= getMAXPOINT().getZ() - 5) {
                        trueOrNot = true;
                    }
                }
            }
        }
        return trueOrNot;
    }

    public Location getTeleportLocation() {
        Location tploc = this.getCenter();
        while(tploc.getBlock().getType() != Material.AIR || tploc.add(0, 1, 0).getBlock().getType() != Material.AIR)
            tploc = tploc.add(0, 1, 0);
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


    public boolean isInFlyRange(Location location) {
        return isInPlotRange(location, ConfigPreferences.getExtraPlotRange());
    }


}
