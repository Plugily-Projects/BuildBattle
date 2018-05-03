package pl.plajer.buildbattle.utils;

/**
 * Created by Tom on 30/07/2014.
 */

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import pl.plajer.buildbattle.utils.ReflectionHandler.PackageType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public enum ParticleEffect {
    EXPLOSION_NORMAL("ExplosionNormal", 0, -1, ParticleProperty.DIRECTIONAL),
    EXPLOSION_LARGE("ExplosionLarge", 1, -1),
    EXPLOSION_HUGE("ExplosionHuge", 2, -1),
    FIREWORKS_SPARK("fireworksSpark", 3, -1, ParticleProperty.DIRECTIONAL),
    WATER_BUBBLE("WaterBubble", 4, -1, ParticleProperty.DIRECTIONAL),
    WATER_SPLASH("WATERSPLASH", 5, -1, ParticleProperty.DIRECTIONAL),
    WATER_WAKE("WaterWake", 6, 7, ParticleProperty.DIRECTIONAL),
    SUSPENDED("suspended", 7, -1),
    SUSPENDED_DEPTH("SuspendedDepth", 8, -1, ParticleProperty.DIRECTIONAL),
    CRIT("crit", 9, -1, ParticleProperty.DIRECTIONAL),
    CRIT_MAGIC("CritMagic", 10, -1, ParticleProperty.DIRECTIONAL),
    SMOKE_NORMAL("SmokeNormal", 11, -1, ParticleProperty.DIRECTIONAL),
    SMOKE_LARGE("SmokeLarge", 12, -1, ParticleProperty.DIRECTIONAL),
    SPELL("Spell", 13, -1),
    SPELL_INSTANT("SpellInstant", 14, -1),
    SPELL_MOB("SpellMob", 15, -1, ParticleProperty.COLORABLE),
    SPELL_MOB_AMBIENT("SpellMobAmbient", 16, -1, ParticleProperty.COLORABLE),
    SPELL_WITCH("SpellWitch", 17, -1),
    DRIP_WATER("DripWater", 18, -1),
    DRIP_LAVA("DripLava", 19, -1),
    VILLAGER_ANGRY("VillagerAngry", 20, -1),
    VILLAGER_HAPPY("VillagerHappy", 21, -1, ParticleProperty.DIRECTIONAL),
    TOWN_AURA("townaura", 22, -1, ParticleProperty.DIRECTIONAL),
    NOTE("note", 23, -1, ParticleProperty.COLORABLE),
    PORTAL("portal", 24, -1, ParticleProperty.DIRECTIONAL),
    ENCHANTMENT_TABLE("enchantmenttable", 25, -1, ParticleProperty.DIRECTIONAL),
    FLAME("flame", 26, -1, ParticleProperty.DIRECTIONAL),
    LAVA("lava", 27, -1),
    FOOTSTEP("footstep", 28, -1),
    CLOUD("cloud", 29, -1, ParticleProperty.DIRECTIONAL),
    REDSTONE("redstone", 30, -1, ParticleProperty.COLORABLE),
    SNOWBALL("snowball", 31, -1),
    SNOW_SHOVEL("snowshovel", 32, -1, ParticleProperty.DIRECTIONAL),
    SLIME("slime", 33, -1),
    HEART("heart", 34, -1),
    BARRIER("barrier", 35, 8),
    ITEM_CRACK("itemcrack", 36, -1, ParticleProperty.DIRECTIONAL, ParticleProperty.REQUIRES_DATA),
    BLOCK_CRACK("blockcrack", 37, -1, ParticleProperty.DIRECTIONAL, ParticleProperty.REQUIRES_DATA),
    BLOCK_DUST("blockdust", 38, 7, ParticleProperty.DIRECTIONAL, ParticleProperty.REQUIRES_DATA),
    WATER_DROP("waterdrop", 39, 8),
    ITEM_TAKE("ItemTake", 40, 8),
    MOB_APPEARANCE("mobappearance", 41, 8);


    private static final Map<String, ParticleEffect> NAME_MAP = new HashMap<>();
    private static final Map<Integer, ParticleEffect> ID_MAP = new HashMap<>();

    // Initialize map for quick name and id lookup
    static {
        for(ParticleEffect effect : values()) {
            NAME_MAP.put(effect.name, effect);
            ID_MAP.put(effect.id, effect);
        }
    }

    private final String name;
    private final int id;
    private final int requiredVersion;
    private final List<ParticleProperty> properties;

    ParticleEffect(String name, int id, int requiredVersion, ParticleProperty... properties) {
        this.name = name;
        this.id = id;
        this.requiredVersion = requiredVersion;
        this.properties = Arrays.asList(properties);
    }

    private static boolean isWater(Location location) {
        Material material = location.getBlock().getType();
        return material == Material.WATER || material == Material.STATIONARY_WATER;
    }

    private static boolean isLongDistance(Location location, List<Player> players) {
        for(Player player : players) {
            if(player.getLocation().distanceSquared(location) < 65536) {
                continue;
            }
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public boolean hasProperty(ParticleProperty property) {
        return properties.contains(property);
    }

    public boolean isSupported() {
        if(requiredVersion == -1) {
            return true;
        }
        return ParticlePacket.getVersion() >= requiredVersion;
    }

    public void display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, List<Player> players) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
        if(!isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if(hasProperty(ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleDataException("This particle effect requires additional data");
        }
        if(hasProperty(ParticleProperty.REQUIRES_WATER) && !isWater(center)) {
            throw new IllegalArgumentException("There is no water at the center location");
        }
        new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, isLongDistance(center, players), null).sendTo(center, players);
    }

    public enum ParticleProperty {
        REQUIRES_WATER, REQUIRES_DATA, DIRECTIONAL, COLORABLE;
    }

    public static abstract class ParticleData {
        private final Material material;
        private final byte data;
        private final int[] packetData;

        @SuppressWarnings("deprecation")
        public ParticleData(Material material, byte data) {
            this.material = material;
            this.data = data;
            this.packetData = new int[]{material.getId(), data};
        }

        public Material getMaterial() {
            return material;
        }

        public byte getData() {
            return data;
        }

        public int[] getPacketData() {
            return packetData;
        }

        public String getPacketDataString() {
            return "_" + packetData[0] + "_" + packetData[1];
        }
    }

    private static final class ParticleDataException extends RuntimeException {
        private static final long serialVersionUID = 3203085387160737484L;

        public ParticleDataException(String message) {
            super(message);
        }
    }

    private static final class ParticleVersionException extends RuntimeException {
        private static final long serialVersionUID = 3203085387160737484L;

        public ParticleVersionException(String message) {
            super(message);
        }
    }

    public static final class ParticlePacket {
        private static int version;
        private static Class<?> enumParticle;
        private static Constructor<?> packetConstructor;
        private static Method getHandle;
        private static Field playerConnection;
        private static Method sendPacket;
        private static boolean initialized;
        private final ParticleEffect effect;
        private final float offsetX;
        private final float offsetY;
        private final float offsetZ;
        private final float speed;
        private final int amount;
        private final boolean longDistance;
        private final ParticleData data;
        private Object packet;

        public ParticlePacket(ParticleEffect effect, float offsetX, float offsetY, float offsetZ, float speed, int amount, boolean longDistance, ParticleData data) throws IllegalArgumentException {
            initialize();
            if(speed < 0) {
                throw new IllegalArgumentException("The speed is lower than 0");
            }
            if(amount < 0) {
                throw new IllegalArgumentException("The amount is lower than 0");
            }
            this.effect = effect;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.speed = speed;
            this.amount = amount;
            this.longDistance = longDistance;
            this.data = data;
        }

        public static void initialize() throws VersionIncompatibleException {
            if(initialized) {
                return;
            }
            try {
                version = Integer.parseInt(Character.toString(ReflectionHandler.PackageType.getServerVersion().charAt(3)));
                if(version > 7) {
                    enumParticle = ReflectionHandler.PackageType.MINECRAFT_SERVER.getClass("EnumParticle");
                }
                Class<?> packetClass = PackageType.MINECRAFT_SERVER.getClass(version < 7 ? "Packet63WorldParticles" : "PacketPlayOutWorldParticles");
                packetConstructor = ReflectionHandler.getConstructor(packetClass);
                getHandle = ReflectionHandler.getMethod("CraftPlayer", PackageType.CRAFTBUKKIT_ENTITY, "getHandle");
                playerConnection = ReflectionHandler.getField("EntityPlayer", PackageType.MINECRAFT_SERVER, false, "playerConnection");
                sendPacket = ReflectionHandler.getMethod(playerConnection.getType(), "sendPacket", PackageType.MINECRAFT_SERVER.getClass("Packet"));
            } catch(Exception exception) {
                throw new VersionIncompatibleException("Your current bukkit version seems to be incompatible with this library", exception);
            }
            initialized = true;
        }

        public static int getVersion() {
            return version;
        }

        private void initializePacket(Location center) throws PacketInstantiationException {
            if(packet != null) {
                return;
            }
            try {
                packet = packetConstructor.newInstance();
                if(version < 8) {
                    String name = effect.getName();
                    if(data != null) {
                        name += data.getPacketDataString();
                    }
                    ReflectionHandler.setValue(packet, true, "a", name);
                } else {
                    ReflectionHandler.setValue(packet, true, "a", enumParticle.getEnumConstants()[effect.getId()]);
                    ReflectionHandler.setValue(packet, true, "j", longDistance);
                    if(data != null) {
                        ReflectionHandler.setValue(packet, true, "k", data.getPacketData());
                    }
                }
                ReflectionHandler.setValue(packet, true, "b", (float) center.getX());
                ReflectionHandler.setValue(packet, true, "c", (float) center.getY());
                ReflectionHandler.setValue(packet, true, "d", (float) center.getZ());
                ReflectionHandler.setValue(packet, true, "e", offsetX);
                ReflectionHandler.setValue(packet, true, "f", offsetY);
                ReflectionHandler.setValue(packet, true, "g", offsetZ);
                ReflectionHandler.setValue(packet, true, "h", speed);
                ReflectionHandler.setValue(packet, true, "i", amount);
            } catch(Exception exception) {
                throw new PacketInstantiationException("Packet instantiation failed", exception);
            }
        }

        public void sendTo(Location center, Player player) throws PacketInstantiationException, PacketSendingException {
            initializePacket(center);
            try {
                sendPacket.invoke(playerConnection.get(getHandle.invoke(player)), packet);
            } catch(Exception exception) {
                throw new PacketSendingException("Failed to send the packet to player '" + player.getName() + "'", exception);
            }
        }

        public void sendTo(Location center, List<Player> players) throws IllegalArgumentException {
            if(players.isEmpty()) {
                throw new IllegalArgumentException("The player list is empty");
            }
            for(Player player : players) {
                sendTo(center, player);
            }
        }

        private static final class VersionIncompatibleException extends RuntimeException {
            private static final long serialVersionUID = 3203085387160737484L;

            public VersionIncompatibleException(String message, Throwable cause) {
                super(message, cause);
            }
        }

        private static final class PacketInstantiationException extends RuntimeException {
            private static final long serialVersionUID = 3203085387160737484L;

            public PacketInstantiationException(String message, Throwable cause) {
                super(message, cause);
            }
        }

        private static final class PacketSendingException extends RuntimeException {
            private static final long serialVersionUID = 3203085387160737484L;

            public PacketSendingException(String message, Throwable cause) {
                super(message, cause);
            }
        }
    }
}