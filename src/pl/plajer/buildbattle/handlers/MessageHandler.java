package pl.plajer.buildbattle.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

/**
 * Created by Tom on 21/07/2015.
 */
public class MessageHandler {

    //private static Main plugin = JavaPlugin.getPlugin(Main.class);

    public static void sendTitleMessage(Player player, String text, int fadeInTime, int showTime, int fadeOutTime, ChatColor color) {
        //todo add support 1.9+ soon
        //if(plugin.is1_8_R3()) {
        try {
            Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + text + "\",color:" + color.name().toLowerCase() + "}");

            Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
            Object packet = titleConstructor.newInstance(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle, fadeInTime, showTime, fadeOutTime);

            sendPacket(player, packet);
        } catch(Exception ignored) {
        }
        /*} else {
            if(plugin.is1_9_R1()) {
                player.sendTitle(text, null);
            } else {
                player.sendTitle(text, null, fadeInTime, showTime, fadeOutTime);
            }
        }*/
    }

    private static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch(Exception ignored) {
        }
    }

    private static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        } catch(ClassNotFoundException ignored) {
        }
        return null;
    }
}
