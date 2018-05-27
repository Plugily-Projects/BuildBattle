/*
 *  Village Defense 3 - Protect villagers from hordes of zombies
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

package pl.plajer.buildbattle3.utils;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.handlers.ConfigurationManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Tom on 29/07/2014.
 */
public class Util {

    private static Main plugin = JavaPlugin.getPlugin(Main.class);

    public static ItemStack getPlayerHead(OfflinePlayer player) {
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwner(player.getName());
        itemStack.setItemMeta(skullMeta);
        itemStack.setDurability((short) SkullType.PLAYER.ordinal());
        return itemStack;
    }

    public static void addLore(ItemStack itemStack, String string) {
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore;
        if(meta != null && meta.hasLore()) {
            lore = meta.getLore();
        } else {
            lore = new ArrayList<>();
        }
        lore.add(string);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
    }

    public static ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore) {
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return item;
    }

    public static String formatIntoMMSS(int secsIn) {
        int minutes = secsIn / 60, seconds = secsIn % 60;
        return ((minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds);
    }

    public static String locationToString(Location location) {
        return location.getWorld().getName() + " , " + location.getX() + " , " + location.getY() + ", " + location.getZ();
    }

    public static void spawnRandomFirework(Location location) {
        Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        //Our random generator
        Random r = new Random();

        //Get the type
        int rt = r.nextInt(4) + 1;
        FireworkEffect.Type type = FireworkEffect.Type.BALL;
        if(rt == 1) type = FireworkEffect.Type.BALL;
        if(rt == 2) type = FireworkEffect.Type.BALL_LARGE;
        if(rt == 3) type = FireworkEffect.Type.BURST;
        if(rt == 4) type = FireworkEffect.Type.CREEPER;
        if(rt == 5) type = FireworkEffect.Type.STAR;

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

    public static int serializeInt(Integer i) {
        if((i % 9) == 0)
            return i;
        else
            return (int) ((Math.ceil(i / 9) * 9) + 9);
    }

    public static Location getLocation(boolean configUsage, String path) {
        String[] loc;
        if(configUsage) {
            loc = plugin.getConfig().getString(path).split(",");
        } else {
            loc = path.split(",");
        }
        plugin.getServer().createWorld(new WorldCreator(loc[0]));
        World w = plugin.getServer().getWorld(loc[0]);
        Double x = Double.parseDouble(loc[1]);
        Double y = Double.parseDouble(loc[2]);
        Double z = Double.parseDouble(loc[3]);
        float yaw = Float.parseFloat(loc[4]);
        float pitch = Float.parseFloat(loc[5]);
        return new Location(w, x, y, z, yaw, pitch);
    }

    public static void saveLocation(String path, Location loc) {
        FileConfiguration config = ConfigurationManager.getConfig("arenas");
        String location = loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();
        config.set(path, location);
        ConfigurationManager.saveConfig(config, "arenas");
    }

}
