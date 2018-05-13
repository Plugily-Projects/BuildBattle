package pl.plajer.buildbattle.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.buildbattle.Main;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Plajer
 * <p>
 * Created at 13.05.2018
 */
public class Reflectionfixmeplease {

    public static void displayParticle(Location loc, Particle particle, float x, float y, float z, int amount) {
        if(!JavaPlugin.getPlugin(Main.class).is1_8_R3()) {
            loc.getWorld().spawnParticle(particle, loc, amount, (double) x, (double) y, (double) z);
        } else {
            try {
                Class<?> packetClass = getNMSClass("PacketPlayOutWorldParticles");
                Constructor<?> packetConstructor = packetClass.getConstructor(String.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, int.class);
                Object packet = packetConstructor.newInstance(particle, loc.getX(), loc.getY(), loc.getZ(), x, y, z, 0, amount);
                Method sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
                for(Player p : loc.getWorld().getPlayers()) {
                    sendPacket.invoke(getConnection(p), packet);
                }
            } catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    private static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        } catch(ClassNotFoundException ignored) {
        }
        return null;
    }

    private static Object getConnection(Player player) throws SecurityException, NoSuchMethodException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method getHandle = player.getClass().getMethod("getHandle");
        Object nmsPlayer = getHandle.invoke(player);
        Field conField = nmsPlayer.getClass().getField("playerConnection");
        Object con = conField.get(nmsPlayer);
        return con;
    }

}
