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

package pl.plajer.buildbattle3.plajerlair.core.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Basic utilities useful for minigames
 */
public class MinigameUtils {

  /**
   * Add lore to item
   *
   * @param itemStack itemstack to add lore
   * @param string    string to add
   */
  public static void addLore(ItemStack itemStack, String string) {
    ItemMeta meta = itemStack.getItemMeta();
    List<String> lore = new ArrayList<>();
    if (meta != null && meta.hasLore()) {
      lore.addAll(meta.getLore());
    }
    lore.add(string);
    meta.setLore(lore);
    itemStack.setItemMeta(meta);
  }

  /**
   * Format seconds to mm:ss, ex 615 seconds 60:15 (60 minutes, 15 seconds)
   *
   * @param secsIn seconds to format
   * @return String with formatted time
   */
  public static String formatIntoMMSS(int secsIn) {
    int minutes = secsIn / 60,
            seconds = secsIn % 60;
    return ((minutes < 10 ? "0" : "") + minutes
            + ":" + (seconds < 10 ? "0" : "") + seconds);
  }

  /**
   * Round double to x places, ex 1.321135 round to 2 places = 1.32
   *
   * @param value  double to round
   * @param places how many places after dot/comma save
   * @return rounded double value
   */
  public static double round(double value, int places) {
    if (places < 0) {
      return value;
    }
    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

  /**
   * Serialize int to use it in Inventories size
   * ex. you have 38 kits and it will serialize it to 45 (9*5)
   * because it is valid inventory size
   * next ex. you have 55 items and it will serialize it to 63 (9*7) not 54 because it's too less
   *
   * @param i integer to serialize
   * @return serialized number
   */
  public static int serializeInt(Integer i) {
    if ((i % 9) == 0) {
      return i;
    } else {
      return (int) ((Math.ceil(i / 9) * 9) + 9);
    }
  }

  /**
   * Save location to string ex world,10,20,30
   *
   * @param location location to string
   * @return location saved to string
   */
  public static String locationToString(Location location) {
    return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ();
  }

  /**
   * Spawns random firework at location
   *
   * @param location location to spawn firework there
   */
  public static void spawnRandomFirework(Location location) {
    Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
    FireworkMeta fwm = fw.getFireworkMeta();

    //Our random generator
    Random r = new Random();

    //Get the type
    int rt = r.nextInt(4) + 1;
    FireworkEffect.Type type;
    switch (rt) {
      case 1:
        type = FireworkEffect.Type.BALL;
        break;
      case 2:
        type = FireworkEffect.Type.BALL_LARGE;
        break;
      case 3:
        type = FireworkEffect.Type.BURST;
        break;
      case 4:
        type = FireworkEffect.Type.CREEPER;
        break;
      case 5:
        type = FireworkEffect.Type.STAR;
        break;
      default:
        type = FireworkEffect.Type.BALL;
        break;
    }

    //Get our random colours
    int r1i = r.nextInt(250) + 1;
    int r2i = r.nextInt(250) + 1;
    Color c1 = Color.fromBGR(r1i);
    Color c2 = Color.fromBGR(r2i);

    //Create our effect with this
    FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();

    //Then apply the effect to the meta
    fwm.addEffect(effect);

    //Generate some random power and set it
    int rp = r.nextInt(2) + 1;
    fwm.setPower(rp);
    fw.setFireworkMeta(fwm);
  }

  public static void saveLoc(JavaPlugin plugin, FileConfiguration file, String fileName, String path, Location loc) {
    String location = loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();
    file.set(path, location);
    ConfigUtils.saveConfig(plugin, file, fileName);
  }

  public static Location getLocation(String path) {
    String[] loc = path.split(",");
    Bukkit.getServer().createWorld(new WorldCreator(loc[0]));
    World w = Bukkit.getServer().getWorld(loc[0]);
    Double x = Double.parseDouble(loc[1]);
    Double y = Double.parseDouble(loc[2]);
    Double z = Double.parseDouble(loc[3]);
    float yaw = Float.parseFloat(loc[4]);
    float pitch = Float.parseFloat(loc[5]);
    return new Location(w, x, y, z, yaw, pitch);
  }

  public static String getProgressBar(int current, int max, int totalBars, String symbol, String completedColor, String notCompletedColor) {

    float percent = (float) current / max;

    int progressBars = (int) (totalBars * percent);

    int leftOver = (totalBars - progressBars);

    StringBuilder sb = new StringBuilder();
    sb.append(ChatColor.translateAlternateColorCodes('&', completedColor));
    for (int i = 0; i < progressBars; i++) {
      sb.append(symbol);
    }
    sb.append(ChatColor.translateAlternateColorCodes('&', notCompletedColor));
    for (int i = 0; i < leftOver; i++) {
      sb.append(symbol);
    }
    return sb.toString();
  }

}
