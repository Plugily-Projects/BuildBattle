package me.tomthedeveloper.buildbattle.utils;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tom on 29/07/2014.
 */
public class Util {


    public static void addLore(ItemStack itemStack, String string) {

        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore;
        if(meta != null && meta.hasLore()) {
            lore = meta.getLore();
        } else {
            lore = new ArrayList<String>();
        }
        lore.add(string);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);


    }

    public static Queue<Block> getLineOfSight(LivingEntity entity, HashSet<Byte> transparent, int maxDistance, int maxLength) {
        if(maxDistance > 120) {
            maxDistance = 120;
        }


        Queue<Block> blocks = new LinkedList<Block>();
        Iterator<Block> itr = new BlockIterator(entity, maxDistance);
        while(itr.hasNext()) {
            Block block = itr.next();
            blocks.add(block);

            if(maxLength != 0 && blocks.size() > maxLength) {
                blocks.remove(0);
            }
            int id = block.getTypeId();
            if(transparent == null) {
                if(id != 0 && id != 50 && id != 59 && id != 31 && id != 175 && id != 38 && id != 37 && id != 6 && id != 106) {

                    break;
                }
            } else {
                if(!transparent.contains((byte) id)) break;
            }

        }
        return blocks;
    }

    public static Entity[] getNearbyEntities(Location l, int radius) {

        int chunkRadius = radius < 16 ? 1 : radius / 16;
        HashSet<Entity> radiusEntities = new HashSet<Entity>();
        for(int chX = 0 - chunkRadius; chX <= chunkRadius; chX++) {
            for(int chZ = 0 - chunkRadius; chZ <= chunkRadius; chZ++) {
                int x = (int) l.getX(), y = (int) l.getY(), z = (int) l.getZ();
                for(Entity e : new Location(l.getWorld(), x + chX * 16, y, z + chZ * 16).getChunk().getEntities()) {
                    if(!(l.getWorld().getName().equalsIgnoreCase(e.getWorld().getName()))) continue;
                    if(e.getLocation().distanceSquared(l) <= radius * radius && e.getLocation().getBlock() != l.getBlock()) {
                        radiusEntities.add(e);
                    }
                }
            }
        }
        return radiusEntities.toArray(new Entity[radiusEntities.size()]);
    }

    public static ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore) {
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return item;
    }

    public static String formatIntoHHMMSS(int secsIn) {

        int hours = secsIn / 3600, remainder = secsIn % 3600, minutes = remainder / 60, seconds = remainder % 60;

        return ((hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds);

    }

    public static String formatIntoMMSS(int secsIn) {

        int minutes = secsIn / 60, seconds = secsIn % 60;

        return ((minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds);

    }

    public static String formatIntoMMSS(long secsIn) {

        long minutes = secsIn / 60, seconds = secsIn % 60;

        return ((minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds);

    }

    public static void setLocation(FileConfiguration config, Location location, String path) {


        config.set(path + ".world", location.getWorld().getName());
        config.set(path + ".x", location.getX());
        config.set(path + ".z", location.getZ());
        config.set(path + ".y", location.getY());
        config.set(path + ".pitch", location.getPitch());
        config.set(path + ".yaw", location.getYaw());

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


    public static List<String> splitString(String string, int max) {
        List<String> matchList = new ArrayList<String>();
        Pattern regex = Pattern.compile(".{1," + max + "}(?:\\s|$)", Pattern.DOTALL);
        Matcher regexMatcher = regex.matcher(string);
        while(regexMatcher.find()) {
            matchList.add(regexMatcher.group());
        }
        return matchList;
    }


}
