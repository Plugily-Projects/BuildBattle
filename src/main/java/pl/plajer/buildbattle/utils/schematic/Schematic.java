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

package pl.plajer.buildbattle.utils.schematic;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import pl.plajer.buildbattle.Main;

/**
 * @author Jojodmo - Schematic Reader
 * @author SamB440 - Schematic previews, centering and pasting block-by-block
 */
public class Schematic {

  private Main plugin;
  private File schematic;
  private List<Integer> pastes = new ArrayList<>();
  private int current = 0;
  private boolean pasted;

  public Schematic(Main plugin, File schematic) {
    this.plugin = plugin;
    this.schematic = schematic;
  }

  public List<Location> pasteSchematic(Location loc, Player paster, boolean preview, int time) {
    try {

      /*
       * Read the schematic file. Get the width, height, length, blocks, and block data.
       */
      FileInputStream fis = new FileInputStream(schematic);
      Object nbt = Class.forName("net.minecraft.server." + plugin.getVersion() + ".NBTCompressedStreamTools").getMethod("a", FileInputStream.class).invoke(null, fis);


      short width = (short) nbt.getClass().getMethod("getShort", String.class).invoke(null, "Width");
      short height = (short) nbt.getClass().getMethod("getShort", String.class).invoke(null, "Height");
      short length = (short) nbt.getClass().getMethod("getShort", String.class).invoke(null, "Length");

      byte[] blocks = (byte[]) nbt.getClass().getMethod("getByteArray", String.class).invoke(null, "Blocks");
      byte[] data = (byte[]) nbt.getClass().getMethod("getByteArray", String.class).invoke(null, "Data");

      fis.close();

      List<Integer> indexes = new ArrayList<>();
      List<Location> locations = new ArrayList<>();
      List<Integer> otherindex = new ArrayList<>();
      List<Location> otherloc = new ArrayList<>();

      /*
       * Loop through all the blocks within schematic size.
       */
      for (int x = 0; x < width; ++x) {
        for (int y = 0; y < height; ++y) {
          for (int z = 0; z < length; ++z) {
            int index = y * width * length + z * width + x;

            final Location location = new Location(loc.getWorld(), (x + loc.getX()) - (int) width / 2, y + paster.getLocation().getY(), (z + loc.getZ()) - (int) length / 2);
            /*
             * Ignore blocks that aren't air. Change this if you want the air to destroy blocks too.
             * Add items to blacklist if you want them placed last, or if they get broken.
             * IMPORTANT!
             * Make the block unsigned, so that blocks with an id over 127, like quartz and emerald, can be pasted.
             */
            Material material = Material.getMaterial(blocks[index] & 0xFF);
            List<Material> blacklist = Arrays.asList(Material.STATIONARY_LAVA,
                Material.STATIONARY_WATER,
                Material.GRASS,
                Material.ARMOR_STAND,
                Material.LONG_GRASS,
                Material.BANNER,
                Material.STANDING_BANNER,
                Material.WALL_BANNER,
                Material.CHORUS_FLOWER,
                Material.CROPS,
                Material.DOUBLE_PLANT,
                Material.CHORUS_PLANT,
                Material.YELLOW_FLOWER,
                Material.TORCH);
            if (material != Material.AIR) {
              if (!blacklist.contains(material)) {
                indexes.add(index);
                locations.add(location);
              } else {
                otherindex.add(index);
                otherloc.add(location);
              }
            }
          }
        }
      }

      /*
       * Make sure liquids are placed last.
       */

      indexes.addAll(otherindex);

      otherindex.clear();

      locations.addAll(otherloc);

      otherloc.clear();

      /*
       * ---------------------------
       * Delete this section of code if you want schematics to be pasted anywhere
       */

      boolean validated = true;

      for (Location validate : locations) {
        if ((validate.getBlock().getType() != Material.AIR || validate.clone().subtract(0, 1, 0).getBlock().getType() == Material.STATIONARY_WATER) || new Location(validate.getWorld(), validate.getX(), paster.getLocation().getY() - 1, validate.getZ()).getBlock().getType() == Material.AIR) {
          /*
           * Show fake block where block is interfering with schematic
           */

          paster.sendBlockChange(validate.getBlock().getLocation(), Material.STAINED_GLASS, (byte) 14);
          if (!preview) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> validate.getBlock().getState().update(), 60);
          }
          validated = false;
        } else {

          /*
           * Show fake block for air
           */

          paster.sendBlockChange(validate.getBlock().getLocation(), Material.STAINED_GLASS, (byte) 5);
          if (!preview) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> validate.getBlock().getState().update(), 60);
          }

        }
      }

      if (preview) {
        return locations;
      }
      if (!validated) {
        return null;
      }

      /*
       * ---------------------------
       */

      /*
       * Start pasting each block every tick
       */
      new BukkitRunnable() {
        @Override
        public void run() {
          /*
           * Get the block, set the type, data, and then update the state.
           */

          final Block block = locations.get(current).getBlock();
          block.setType(Material.getMaterial(blocks[indexes.get(current)] & 0xFF));
          block.setData(data[indexes.get(current)]);
          block.getState().update(true, false);

          /*
           * Play block effects
           */

          block.getLocation().getWorld().spawnParticle(Particle.CLOUD, block.getLocation(), 6);
          block.getLocation().getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());

          current++;

          if (current >= locations.size() || current >= indexes.size()) {
            this.cancel();
            pasted = true;
            current = 0;
          }
        }
      }.runTaskTimer(plugin, 0, time);

      return locations;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public List<Location> pasteSchematic(Location loc, Player paster, boolean preview) {
    return pasteSchematic(loc, paster, preview, 20);
  }

  public boolean isPasted() {
    return pasted;
  }

  public void setPasted(boolean pasted) {
    this.pasted = pasted;
  }
}
