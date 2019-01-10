/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.buildbattle.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle.Main;
import pl.plajerlair.core.utils.XMaterial;

/**
 * Created by Tom on 29/07/2014.
 */
public class Utils {

  private static Main plugin = JavaPlugin.getPlugin(Main.class);
  public static final ItemStack PLAYER_HEAD_ITEM = (plugin.is1_12_R1() || plugin.is1_11_R1())
      ? new ItemStack(Material.SKULL_ITEM, 1, (short) 3) : XMaterial.PLAYER_HEAD.parseItem();

  /**
   * Checks whether itemstack is named (not null, has meta and display name)
   *
   * @param stack item stack to check
   * @return true if named, false otherwise
   */
  public static boolean isNamed(ItemStack stack) {
    if (stack == null) {
      return false;
    }
    return stack.hasItemMeta() && stack.getItemMeta().hasDisplayName();
  }

  public static ItemStack getSkull(String url) {
    ItemStack head = PLAYER_HEAD_ITEM.clone();
    if (url.isEmpty()) {
      return head;
    }

    SkullMeta headMeta = (SkullMeta) head.getItemMeta();
    GameProfile profile = new GameProfile(UUID.randomUUID(), null);
    profile.getProperties().put("textures", new Property("textures", url));
    Field profileField;
    try {
      profileField = headMeta.getClass().getDeclaredField("profile");
      profileField.setAccessible(true);
      profileField.set(headMeta, profile);

    } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ignored) {
    }

    head.setItemMeta(headMeta);
    return head;
  }

  public static ItemStack setItemNameAndLore(ItemStack item, String name, List<String> lore) {
    ItemMeta im = item.getItemMeta();
    im.setDisplayName(name);
    im.setLore(lore);
    item.setItemMeta(im);
    return item;
  }

  public static Map sortByValue(Map unsortMap) {
    List list = new LinkedList(unsortMap.entrySet());
    list.sort((o1, o2) -> ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue()));
    Map sortedMap = new LinkedHashMap();
    for (Object aList : list) {
      Map.Entry entry = (Map.Entry) aList;
      sortedMap.put(entry.getKey(), entry.getValue());
    }
    return sortedMap;
  }

  public static void sendPacket(Player player, Object packet) {
    try {
      Object handle = player.getClass().getMethod("getHandle").invoke(player);
      Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
      playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
    } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | NoSuchFieldException ex) {
      ex.printStackTrace();
    }
  }

  public static Class<?> getNMSClass(String nmsClassName) {
    try {
      return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + nmsClassName);
    } catch (ClassNotFoundException ex) {
      ex.printStackTrace();
      Bukkit.getConsoleSender().sendMessage("Reflection failed for " + nmsClassName);
      return null;
    }
  }

}
