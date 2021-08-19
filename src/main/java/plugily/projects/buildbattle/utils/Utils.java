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

package plugily.projects.buildbattle.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.buildbattle.Main;
import plugily.projects.commonsbox.minecraft.compat.PacketUtils;
import plugily.projects.commonsbox.minecraft.compat.ServerVersion;
import plugily.projects.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.commonsbox.minecraft.item.ItemBuilder;

/**
 * Created by Tom on 29/07/2014.
 */
public class Utils {

  private Utils() {
  }

  private static final Main plugin = JavaPlugin.getPlugin(Main.class);

  private static Class<?> packetPlayOutMapChunk, chunkClass;
  private static Constructor<?> mapChunkConstructor;
  private static Method chunkHandleMethod;

  static {
    packetPlayOutMapChunk = PacketUtils.classByName("net.minecraft.network.protocol.game", "PacketPlayOutMapChunk");
    chunkClass = PacketUtils.classByName("net.minecraft.world.level.chunk", "Chunk");

    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_17_R1)) {
      try {
        mapChunkConstructor = packetPlayOutMapChunk.getConstructor(chunkClass);
      } catch(NoSuchMethodException e) {
        e.printStackTrace();
      }
    }
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
  public static int serializeInt(int i) {
    if(i == 0) return 9; //The function bellow doesn't work if i == 0, so return 9 in case that happens.
    return (i % 9) == 0 ? i : (i + 9 - 1) / 9 * 9;
  }

  private static ItemBuilder backButton;

  public static ItemStack getGoBackItem() {
    if (backButton == null)
      backButton = new ItemBuilder(XMaterial.STONE_BUTTON.parseItem())
          .name(plugin.getChatManager().colorMessage("Menus.Option-Menu.Go-Back-Button.Item-Name"))
          .lore(plugin.getChatManager().colorMessage("Menus.Option-Menu.Go-Back-Button.Item-Lore"));

    return backButton.build();
  }

  public static void sendMapChunk(Player player, Chunk chunk) {
    try {
      if (chunkHandleMethod == null)
        chunkHandleMethod = chunk.getClass().getMethod("getHandle");

      if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_17_R1)) {
        PacketUtils.sendPacket(player, mapChunkConstructor.newInstance(chunkHandleMethod.invoke(chunk)));
        return;
      }

      if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_16_R1)) {
        if (mapChunkConstructor == null)
          mapChunkConstructor = packetPlayOutMapChunk.getConstructor(chunkClass, int.class, boolean.class);

        PacketUtils.sendPacket(player, mapChunkConstructor.newInstance(chunkHandleMethod.invoke(chunk), 65535, false));
        return;
      }

      if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_10_R2)) {
        if (mapChunkConstructor == null)
          mapChunkConstructor = packetPlayOutMapChunk.getConstructor(chunkClass, boolean.class, int.class);

        PacketUtils.sendPacket(player, mapChunkConstructor.newInstance(chunkHandleMethod.invoke(chunk), true, 65535));
        return;
      }

      if (mapChunkConstructor == null)
        mapChunkConstructor = packetPlayOutMapChunk.getConstructor(chunkClass, int.class);

      PacketUtils.sendPacket(player, mapChunkConstructor.newInstance(chunkHandleMethod.invoke(chunk), 65535));
    } catch(ReflectiveOperationException exception) {
      exception.printStackTrace();
    }
  }

}
