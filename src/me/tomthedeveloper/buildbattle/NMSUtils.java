package me.tomthedeveloper.buildbattle;

import net.minecraft.server.v1_12_R1.BiomeBase;
import net.minecraft.server.v1_12_R1.BiomeBase.BiomeMeta;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityAreaEffectCloud;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.EntityArrow;
import net.minecraft.server.v1_12_R1.EntityBat;
import net.minecraft.server.v1_12_R1.EntityBlaze;
import net.minecraft.server.v1_12_R1.EntityBoat;
import net.minecraft.server.v1_12_R1.EntityCaveSpider;
import net.minecraft.server.v1_12_R1.EntityChicken;
import net.minecraft.server.v1_12_R1.EntityCow;
import net.minecraft.server.v1_12_R1.EntityCreeper;
import net.minecraft.server.v1_12_R1.EntityDragonFireball;
import net.minecraft.server.v1_12_R1.EntityEgg;
import net.minecraft.server.v1_12_R1.EntityEnderCrystal;
import net.minecraft.server.v1_12_R1.EntityEnderDragon;
import net.minecraft.server.v1_12_R1.EntityEnderPearl;
import net.minecraft.server.v1_12_R1.EntityEnderSignal;
import net.minecraft.server.v1_12_R1.EntityEnderman;
import net.minecraft.server.v1_12_R1.EntityEndermite;
import net.minecraft.server.v1_12_R1.EntityEvoker;
import net.minecraft.server.v1_12_R1.EntityEvokerFangs;
import net.minecraft.server.v1_12_R1.EntityExperienceOrb;
import net.minecraft.server.v1_12_R1.EntityFallingBlock;
import net.minecraft.server.v1_12_R1.EntityFireball;
import net.minecraft.server.v1_12_R1.EntityFireworks;
import net.minecraft.server.v1_12_R1.EntityGhast;
import net.minecraft.server.v1_12_R1.EntityGiantZombie;
import net.minecraft.server.v1_12_R1.EntityGuardian;
import net.minecraft.server.v1_12_R1.EntityGuardianElder;
import net.minecraft.server.v1_12_R1.EntityHorse;
import net.minecraft.server.v1_12_R1.EntityHorseDonkey;
import net.minecraft.server.v1_12_R1.EntityHorseMule;
import net.minecraft.server.v1_12_R1.EntityHorseSkeleton;
import net.minecraft.server.v1_12_R1.EntityHorseZombie;
import net.minecraft.server.v1_12_R1.EntityIllagerIllusioner;
import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.EntityIronGolem;
import net.minecraft.server.v1_12_R1.EntityItem;
import net.minecraft.server.v1_12_R1.EntityItemFrame;
import net.minecraft.server.v1_12_R1.EntityLeash;
import net.minecraft.server.v1_12_R1.EntityLlama;
import net.minecraft.server.v1_12_R1.EntityLlamaSpit;
import net.minecraft.server.v1_12_R1.EntityMagmaCube;
import net.minecraft.server.v1_12_R1.EntityMinecartChest;
import net.minecraft.server.v1_12_R1.EntityMinecartCommandBlock;
import net.minecraft.server.v1_12_R1.EntityMinecartFurnace;
import net.minecraft.server.v1_12_R1.EntityMinecartHopper;
import net.minecraft.server.v1_12_R1.EntityMinecartMobSpawner;
import net.minecraft.server.v1_12_R1.EntityMinecartRideable;
import net.minecraft.server.v1_12_R1.EntityMinecartTNT;
import net.minecraft.server.v1_12_R1.EntityMushroomCow;
import net.minecraft.server.v1_12_R1.EntityOcelot;
import net.minecraft.server.v1_12_R1.EntityPainting;
import net.minecraft.server.v1_12_R1.EntityParrot;
import net.minecraft.server.v1_12_R1.EntityPig;
import net.minecraft.server.v1_12_R1.EntityPigZombie;
import net.minecraft.server.v1_12_R1.EntityPolarBear;
import net.minecraft.server.v1_12_R1.EntityPotion;
import net.minecraft.server.v1_12_R1.EntityRabbit;
import net.minecraft.server.v1_12_R1.EntitySheep;
import net.minecraft.server.v1_12_R1.EntityShulker;
import net.minecraft.server.v1_12_R1.EntityShulkerBullet;
import net.minecraft.server.v1_12_R1.EntitySilverfish;
import net.minecraft.server.v1_12_R1.EntitySkeleton;
import net.minecraft.server.v1_12_R1.EntitySkeletonStray;
import net.minecraft.server.v1_12_R1.EntitySkeletonWither;
import net.minecraft.server.v1_12_R1.EntitySlime;
import net.minecraft.server.v1_12_R1.EntitySmallFireball;
import net.minecraft.server.v1_12_R1.EntitySnowball;
import net.minecraft.server.v1_12_R1.EntitySnowman;
import net.minecraft.server.v1_12_R1.EntitySpectralArrow;
import net.minecraft.server.v1_12_R1.EntitySpider;
import net.minecraft.server.v1_12_R1.EntitySquid;
import net.minecraft.server.v1_12_R1.EntityTNTPrimed;
import net.minecraft.server.v1_12_R1.EntityThrownExpBottle;
import net.minecraft.server.v1_12_R1.EntityTypes;
import net.minecraft.server.v1_12_R1.EntityVex;
import net.minecraft.server.v1_12_R1.EntityVillager;
import net.minecraft.server.v1_12_R1.EntityVindicator;
import net.minecraft.server.v1_12_R1.EntityWitch;
import net.minecraft.server.v1_12_R1.EntityWither;
import net.minecraft.server.v1_12_R1.EntityWitherSkull;
import net.minecraft.server.v1_12_R1.EntityWolf;
import net.minecraft.server.v1_12_R1.EntityZombie;
import net.minecraft.server.v1_12_R1.EntityZombieHusk;
import net.minecraft.server.v1_12_R1.EntityZombieVillager;
import net.minecraft.server.v1_12_R1.GenericAttributes;
import net.minecraft.server.v1_12_R1.IAttribute;
import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.MinecraftKey;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

/**
 * A Free-to-use library class for registering custom entities in Minecraft,
 * using the Spigot server software (ver. 1.12-pre) as its API.
 *
 * @author jetp250
 */
public class NMSUtils {

    public static final CraftServer CRAFTBUKKIT_SERVER;
    public static final MinecraftServer MINECRAFT_SERVER;
    private static final Field META_LIST_MONSTER;
    private static final Field META_LIST_CREATURE;
    private static final Field META_LIST_WATER_CREATURE;
    private static final Field META_LIST_AMBIENT;
    private static final BiomeBase[] BIOMES;

    static {
        final Class<BiomeBase> clazz = BiomeBase.class;
        Field monster = null;
        Field creature = null;
        Field water = null;
        Field ambient = null;
        try {
            // These fields may vary depending on your version.
            // The new names can be found under
            // net.minecraft.server.<version>.BiomeBase.class
            monster = clazz.getDeclaredField("t");
            creature = clazz.getDeclaredField("u");
            water = clazz.getDeclaredField("v");
            ambient = clazz.getDeclaredField("w");
        } catch(final Exception e) {
            Bukkit.getLogger().warning("Wrong server version / software; BiomeMeta fields not found, aborting.");
        }
        META_LIST_MONSTER = monster;
        META_LIST_CREATURE = creature;
        META_LIST_WATER_CREATURE = water;
        META_LIST_AMBIENT = ambient;
        CRAFTBUKKIT_SERVER = (CraftServer) Bukkit.getServer();
        MINECRAFT_SERVER = NMSUtils.CRAFTBUKKIT_SERVER.getServer();
        BIOMES = new BiomeBase[BiomeBase.i.a()];
        final Iterator<BiomeBase> iterator = BiomeBase.i.iterator();
        int index = 0;
        while(iterator.hasNext()) {
            NMSUtils.BIOMES[index++] = iterator.next();
        }
    }

    /**
     * Registers an Item (Not an ItemStack!) to be available for use. an ItemStack
     * can then be created using <code>new ItemStack(item)</code>.
     *
     * @param name - The name of the item, can be anything
     * @param id   - The ID of the item, will be rendered depending on this
     * @param item - The net.minecraft.server.version.Item itself
     */
    public static void registerItem(final Plugin provider, final String name, final int id, final Item item) {
        Item.REGISTRY.a(id, new MinecraftKey(provider.getName(), name), item);
    }

    /**
     * Adds a random spawn for the mob with the specified arguments.
     * <p>
     * If you're using a custom entity class, remember to <b>register</b> it before
     * using this! Otherwise it'll not be rendered by the client.
     * <p>
     * If {@link #isAccessible()} returns false, the process will not be executed.
     *
     * @param type   - The mob type to spawn
     * @param data   - The spawn data (chance, amount, spawn weight..)
     * @param biomes - The array of biomes to let the mobs spawn in, use
     *               {@link Biome#COLLECTION_ALL} for all of them.
     * @see #registerEntity(MobType, Class, boolean)
     */
    public static boolean addRandomSpawn(final Type type, final SpawnData data, final Biome... biomes) {
        if(type.isSpecial()) {
            return false;
        }
        final Field field;
        if((field = type.getMeta().getField()) == null) {
            return false;
        }
        try {
            field.setAccessible(true);
            for(final Biome biome : biomes) {
                final BiomeBase[] array = biome.getNMSBiomeArray();
                for(final BiomeBase base : array) {
                    @SuppressWarnings("unchecked") final List<BiomeMeta> list = (List<BiomeMeta>) field.get(base);
                    list.add(data);
                    field.set(base, list);
                }
            }
            return true;
        } catch(final Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Registers the custom class to be available for use.
     *
     * @param type        - The type of your mob
     * @param customClass - Your custom class that'll be used
     * @param biomes      - Should your mob be set as a default in each biome? Only one
     *                    custom entity of this type entity can have this set as 'true'.
     */
    public static void registerEntity(final Plugin provider, final Type type, final Class<? extends Entity> customClass, final boolean biomes) {
        NMSUtils.registerEntity(provider, type.getName(), type, customClass, biomes);
    }

    /**
     * Registers the custom class to be available for use.
     *
     * @param id          - The mob id. BE CAREFUL with this. Your Minecraft client renders
     *                    the entity based on this, and if used improperly, will cause
     *                    unexpected behavior!
     * @param name        - The 'savegame id' of the mob.
     * @param type        - The type of your mob
     * @param customClass - Your custom class that'll be used
     * @param biomes      - The array of biomes to make the mob spawn in.
     * @see #registerEntity(int, String, MobType, Class, Biome[])
     * @see EntityType#getName() EntityType#getName() for the savegame id.
     * @see EntityType#getId() EntityType#getId() for the correct mob id.
     */
    @SuppressWarnings("unchecked")
    public static void registerEntity(final Plugin provider, final int id, final String name, final Type type, final Class<? extends Entity> customClass, final Biome... biomes) {
        final MinecraftKey key = new MinecraftKey(provider.getName(), name);
        EntityTypes.b.a(id, key, customClass);
        if(!EntityTypes.d.contains(key)) {
            EntityTypes.d.add(key);
        }
        if(biomes.length == 0 || type.isSpecial()) {
            return;
        }
        final Field field;
        if((field = type.getMeta().getField()) == null) {
            return;
        }
        try {
            field.setAccessible(true);
            for(final Biome biome : biomes) {
                final BiomeBase[] array = biome.getNMSBiomeArray();
                for(final BiomeBase base : array) {
                    final List<BiomeMeta> list = (List<BiomeMeta>) field.get(base);
                    for(final BiomeMeta meta : list) {
                        if(meta.b == type.getNMSClass()) {
                            meta.b = (Class<? extends EntityInsentient>) customClass;
                            break;
                        }
                    }
                }
            }
        } catch(final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Registers the custom class to be available for use.
     *
     * @param name        - The 'savegame id' of the mob.
     * @param type        - The type of your mob
     * @param customClass - Your custom class that'll be used
     * @param biomes      - Should your mob be set as a default in each biome? Only one
     *                    custom entity of this type entity can have this set as 'true'.
     * @see #registerEntity(int, String, MobType, Class, Biome[])
     * @see EntityType#getName() EntityType#getName() for the savegame id.
     */
    @SuppressWarnings("unchecked")
    public static void registerEntity(final Plugin provider, final String name, final Type type, final Class<? extends Entity> customClass, final boolean biomes) {
        final MinecraftKey key = new MinecraftKey(provider.getName(), name);
        EntityTypes.b.a(type.getId(), key, customClass);
        if(!EntityTypes.d.contains(key)) {
            EntityTypes.d.add(key);
        }
        if(!biomes || type.isSpecial()) {
            return;
        }
        final Field field;
        if((field = type.getMeta().getField()) == null) {
            return;
        }
        try {
            field.setAccessible(true);
            for(final BiomeBase base : NMSUtils.BIOMES) {
                final List<BiomeMeta> list = (List<BiomeMeta>) field.get(base);
                for(final BiomeMeta meta : list) {
                    if(meta.b == type.getNMSClass()) {
                        meta.b = (Class<? extends EntityInsentient>) customClass;
                        break;
                    }
                }
            }
            field.setAccessible(false);
        } catch(final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * An enum containing all the biomes of Minecraft. The descriptions are taken
     * from the <a href="http://minecraft.gamepedia.com/Biome">Minecraft Wiki
     * Page</a>, which also has images for each biome.
     */
    public enum Biome {

        /**
         * A large, open biome made entirely of water going up to y=63, with underwater
         * relief on the sea floor, such as small mountains and plains, usually
         * including gravel. Oceans typically extend under 3,000 blocks in any
         * direction, around 60% of the Overworld's surface is covered in Ocean. Small
         * islands with infrequent vegetation can be found in oceans. Passive mobs are
         * unable to spawn on these islands, but hostiles can. Cavern entrances can be
         * found infrequently at the bottom of the ocean. In the Console versions, they
         * surround the edges of the map.
         */
        OCEAN(0), /**
         * A relatively flat and grassy biome with rolling hills and few trees. Gullies,
         * water holes, and NPC villages are common. Cave openings and water or lava
         * springs are easily identifiable due to the flat unobstructed terrain. Passive
         * mobs spawn often in plains biomes; this biome and its variants are also one
         * of the only biomes where horses spawn naturally.
         */
        PLAINS(1), /**
         * A barren and relatively inhospitable biome consisting mostly of sand dunes,
         * dead bushes, and cacti. Sandstone is commonly found underneath the sand. The
         * only passive mobs to spawn naturally in deserts are gold rabbits, their
         * coloring well-camouflaged against the sand. Sugar cane can be found if the
         * desert is next to an ocean or river biome. The lack of visual obstruction
         * makes mobs highly visible at night. Desert villages, desert wells and desert
         * temples are found exclusively in this biome. This biome sometimes appear as
         * edge of mesa biome.
         */
        DESERT(2), /**
         * A highland biome (with some mountaintops reaching y=130 or even higher) with
         * a few scattered oak and spruce trees. Cliffs, peaks, valleys, waterfalls,
         * overhangs, floating islands, caverns, and many other structures exist,
         * offering outstanding views. This is one of the few biomes where llamas spawn
         * naturally. Snowfall also occurs above certain heights, thus creating "snow
         * caps" on the top of the mountains. Falling is a significant risk, as there
         * are many steep ledges large enough to cause severe fall damage or even death.
         * Extreme hills are the only biomes where emerald ores and silverfish can be
         * found naturally.
         */
        EXTREME_HILLS(3), /**
         * A biome with a lot of oak and birch trees, occasional hills, and a fair
         * amount of tall grass. Mushrooms and flowers can occasionally be found here.
         * This is one of the most preferred biomes to start out in, due to the
         * abundance of wood. However, the frequency of trees also makes it dangerous to
         * navigate at night, due to obscured vision. Forest biomes are also one of the
         * smallest biomes.
         */
        FOREST(4), /**
         * A biome densely filled with spruce trees, ferns and large ferns. Wolves tend
         * to spawn here fairly commonly.
         */
        TAIGA(5), /**
         * A biome characterized by a mix of flat, dry areas around sea level and
         * shallow pools of brackish green water with floating lily pads. Clay, sand,
         * and dirt are commonly found at the bottom of these pools. Trees are often
         * covered with dark green vines, and can be found growing out from the water.
         * Witch huts generate exclusively in swamps. Slimes will also spawn naturally
         * at night, most commonly on full moons, making this an especially dangerous
         * biome at night. Temperature varies randomly within the biome, not affected by
         * altitude, causing foliage and grass colors to vary.
         */
        SWAMPLAND(6), /**
         * A biome that consists of water blocks that form an elongated, curving shape
         * similar to a real river. Unlike real rivers, however, they have no current.
         * Rivers cut through terrain or separate the main biomes. They attempt to join
         * up with ocean on the other side, but will sometimes loop around to the same
         * area of ocean. Rarely, they can have no connection to the ocean and form a
         * circle. They have a dull green grass hue, much like the ocean, and trace
         * amounts of oak trees tend to generate there as well. Rivers are also a
         * reliable source of clay. These biomes are good for fishing.
         */
        RIVER(7), /**
         * This is the biome used to generate the Nether. In a Superflat world using
         * this biome, the Overworld will contain only Nether mobs (ghasts, packs of
         * zombie pigmen and occasional magma cubes) and won't receive rain, but it
         * won't have Nether structures (such as Nether quartz or glowstone veins or
         * Nether fortresses), and it can still have water lakes and mineshafts, beds
         * will also explode in this biome, even when in the Overworld.
         */
        HELL(8), /**
         * This biome is used to generate the End. The ender dragon, and large amounts
         * of endermen, spawn in this biome. Most of the End's structure is provided by
         * the dimension itself rather than the biome. It does not rain or snow in this
         * biome unlike the other low temperature biomes. The outer islands in the End
         * can be accessed using the End gateway portal after the ender dragon has been
         * defeated. These contain endermen, chorus plants and End cities. End cites are
         * the only place where shulkers naturally spawn .If the biome is used for a
         * superflat world, the sky will be dark gray and an ender dragon will spawn at
         * 0,0 coordinates in the Overworld. Only endermen will spawn at night.
         */
        SKY(9), /**
         * A variant of the ocean biome that is completely frozen over. However, warmer
         * rivers occasionally run through it. Doesn't generate in default worlds, but
         * can be accessed using the Customized world type.
         */
        FROZEN_OCEAN(10), /**
         * This variant of river only generates in ice plains biomes. The surface layer
         * of water is frozen.
         */
        FROZEN_RIVER(11), /**
         * A somewhat rare but expansive, flat biome with a huge amount of snow. All
         * sources of water exposed to the sky are frozen over. Sugar cane will generate
         * in this biome, but can become uprooted when chunks load as the water sources
         * freeze to ice. There are very few natural oak and spruce trees in this biome.
         * It has a lower chance of spawning passive mobs during world generation than
         * other biomes (7% versus 10%), however it is one of the few biomes where Polar
         * bears and Strays spawn. Due to the biome's size, snow and ice cover, and
         * scarcity of wood and animals, initial survival becomes difficult in
         * comparison to other biomes.
         */
        ICE_PLAINS(12), /**
         * Snowy hills that generate in the {@link #ICE_PLAINS}. Ice Mountains are
         * usually taller, with height comparable to Extreme Hills biomes, and has a
         * lower chance of spawning passive mobs during world generation than other
         * biomes (7% versus 10%).
         */
        ICE_MOUNTAINS(13), /**
         * This rare biome consists of a mixture of flat landscape and steep hills and
         * has mycelium instead of grass as its surface. However, if you do place down
         * grass, it is a very bright green color, not unlike that of the jungle.
         * Mushroom islands are most often adjacent to an ocean and are often found
         * isolated from other biomes. It is one of the only biomes where huge mushrooms
         * can generate naturally, and where mushrooms can grow in full sunlight.
         * <p>
         * <p>
         * Technically no mobs other than mooshrooms spawn naturally in this biome,
         * including the usual night-time hostile mobs. This also applies to caves,
         * abandoned mine shafts, and other dark structures, meaning exploring
         * underground is supposedly safe. However, mob spawners will still spawn mobs,
         * and the player will also still be able to breed animals and spawn mobs using
         * eggs.
         */
        MUSHROOM_ISLAND(14), /**
         * Mushroom shores represent the flat shore area of the mushroom biome.
         */
        MUSHROOM_ISLAND_SHORE(15), /**
         * Generated on the shores of oceans, beaches are composed of sand. Beaches
         * penetrate the landscape, removing the original blocks and placing in sand
         * blocks. Some beaches generate with gravel instead of sand. These are also
         * useful for fishing.
         */
        BEACHES(16), /**
         * Basically its respective base biome, {@link #DESERT}, but partially taller.
         */
        DESERT_HILLS(17), /**
         * Basically its respective base biome, {@link #FOREST}, but partially taller.
         */
        FOREST_HILLS(18), /**
         * Basically its respective base biome, {@link #TAIGA}, but partially taller.
         */
        TAIGA_HILLS(19), /**
         * Similar to the jungle edge sub-biome, this sub-biome generates exclusively at
         * the edge of extreme hills biomes (or any variant). Doesn't generate in
         * default worlds, but can be accessed using the Customized world type.
         */
        EXTREME_HILLS_EDGE(20), /**
         * A very dense, but rather uncommon tropical biome. It features large jungle
         * trees that can reach up to 31 blocks tall with 2×2 thick trunks. Oak trees
         * are also common. The landscape is lush green and quite hilly, with many small
         * lakes of water often nestled into deep valleys, sometimes above sea level.
         * Leaves cover much of the forest floor—these "bush trees" have single-blocks
         * of jungle wood for trunks, surrounded by oak leaves. When inside a jungle,
         * the sky will become noticeably lighter. Vines are found alongside most blocks
         * and may cover the surface of caves. Ocelots, jungle temples, melons, and
         * cocoa plants can be found exclusively in this biome. Melons generate in small
         * patches, similar to pumpkins.
         */
        JUNGLE(21), /**
         * Basically its respective base biome, {@link #JUNGLE}, but partially taller.
         */
        JUNGLE_HILLS(22), /**
         * A very rare biome that only generates at the border of a jungle biome and any
         * other biome. Similar to a jungle but with much fewer and smaller trees, and
         * relatively flat terrain.
         */
        JUNGLE_EDGE(23), /**
         * A variation of the Ocean biome. In deep ocean biomes, the ocean can exceed 30
         * blocks in depth, making it twice as deep as the normal ocean. In contrast to
         * default oceans, the ground is mainly covered with gravel. Ocean monuments
         * generate in deep oceans, which spawn guardians.
         */
        DEEP_OCEAN(24), /**
         * This stone-covered biome often appears adjacent to mountains and the ocean.
         * Depending on the height of the nearby land, it can generate medium slopes or
         * huge cliffs.
         */
        STONE_BEACH(25), /**
         * A beach with snowy weather conditions. Often found when the Ice Plain biome
         * borders an ocean biome.
         */
        COLD_BEACH(26), /**
         * A forest made solely out of birch trees.
         */
        BIRCH_FOREST(27), /**
         * Basically its respective base biome, {@link #BIRCH_FOREST}, but partially
         * taller.
         */
        BIRCH_FOREST_HILLS(28), /**
         * This biome is composed of dark oak trees, a mostly closed roof of leaves, and
         * occasional large mushrooms. Trees in this forest are so plentiful and so
         * close together, that at some spots it may become dark enough for hostile mobs
         * to spawn, even during the day. Rarely, a woodland mansion may spawn
         */
        ROOFED_FOREST(29), /**
         * A snowy variation of the taiga biome, where ferns(and large ferns) and spruce
         * trees grow. It is one of the few places where wolves will naturally spawn.
         */
        COLD_TAIGA(30), /**
         * Basically its respective base biome, {@link #COLD_TAIGA}, but partially
         * taller.
         */
        COLD_TAIGA_HILLS(31), /**
         * Mega taiga is an uncommon biome composed of spruce trees, much like the
         * standard taiga biome. However, some trees are 2×2 thick and very tall, not
         * unlike large jungle trees. Moss stone boulders appear frequently, brown
         * mushrooms are common and podzol can be found along the forest floor. There
         * are also patches of coarse dirt, which will not grow grass. Wolves may also
         * spawn here, as they do in normal taiga biomes.
         */
        MEGA_TAIGA(32), /**
         * Basically its respective base biome, {@link #MEGA_TAIGA}, but partially
         * taller.
         */
        MEGA_TAIGA_HILLS(33), /**
         * A variant of the regular extreme hills biome, adding larger mountains,
         * steeper cliffs, and a moderate amount of spruce trees and scattered oak
         * trees.
         */
        EXTREME_HILLS_WITH_TREES(34), /**
         * A relatively flat and dry biome with a dry grass color and scattered acacia
         * trees. Villages can generate in this biome, and it is the only biome where
         * both horses and llamas spawn naturally.
         */
        SAVANNA(35), /**
         * Variation of the {@link #SAVANNA} biome. Like the hills biomes, but flattened
         * at the top, coming to rest at about 20 to 30 blocks above sea level.
         */
        SAVANNA_PLATEAU(36), /**
         * A rare biome consisting of hardened clay, stained clay, and dead bushes –
         * similar to a desert. Red sand will also generate here instead of regular
         * sand, with occasional cacti. Its composition is useful when other sources of
         * clay are scarce. However, finding mesa biomes can be difficult due to their
         * rarity. On the other hand, it offers great variety - there are a total of 6
         * variations of this biome to explore.
         * <p>
         * <p>
         * Mesas can contain above ground abandoned mineshafts. They also allow gold ore
         * to generate near surface levels, rather than just at layer 32 and below.
         */
        MESA(37), /**
         * A rare variation of the mesa plateau, where small forests of dry oak trees
         * and grass generate at the top of the plateaus.
         */
        MESA_PLATEAU_F(38), /**
         * Variation of the {@link #MESA} bioime. Like the hills biomes, but flattened
         * at the top, coming to rest at about 20 to 30 blocks above sea level. Only
         * generates in savanna and mesa biomes.
         */
        MESA_PLATEAU(39), /**
         * A completely empty biome that generates only a single structure: a 33×33
         * stone platform with a single block of cobblestone in the center. No mobs
         * (passive or hostile) can spawn without spawn eggs, monster spawners or
         * commands. Can only be accessed through The Void superflat preset.
         */
        VOID(127), /**
         * A rare variation of the plains biome, where sunflowers naturally spawn in
         * abundance.
         */
        SUNFLOWER_PLAINS(129), /**
         * Unlike in normal deserts, patches of water can be found in this biome.
         */
        DESERT_M(130), /**
         * Variant of the regular extreme hills biome that features higher mountain
         * peaks, most of which reach into the clouds. Mountains here are composed
         * mainly of gravel and a little bit of dirt and grass, with a sparse population
         * of spruce and oak trees.
         */
        EXTREME_HILLS_M(131), /**
         * A variant of the forest biome that has fewer trees but has a huge amount of
         * various flowers. There are certain flowers that are exclusive to the flower
         * forest.
         */
        FLOWER_FOREST(132), /**
         * Mountainous version of the regular taiga biome.
         */
        TAIGA_M(133), /**
         * A slightly hillier swampland with greener grass. Witch huts do not generate
         * in this biome, unlike the normal swampland biome.
         */
        SWAMPLAND_M(134), /**
         * A rare variation of the Ice Plains biome that features large spikes of packed
         * ice, as well as packed ice 'lakes'. Usually the spikes are 10 to 20 blocks
         * tall, but some long, thin spikes can reach over 50 blocks in height. All
         * grass blocks in this biome are replaced with blocks of snow. It has a lower
         * chance of spawning passive mobs during world generation than other biomes (7%
         * versus 10%).
         */
        ICE_SPIKES(140), /**
         * Much more mountainous version of the normal jungle, with foliage so thick
         * that the ground is barely visible. A very resource-demanding biome. Due to
         * the hilly nature of the terrain in this biome, and the height of the tall
         * jungle trees, trees frequently reach into and go above the clouds. Extremely
         * dense foliage and treacherous hilly terrain make this a very difficult biome
         * to navigate, especially at night.
         */
        JUNGLE_M(149), /**
         * The rarest biome in Minecraft. A slightly more mountainous variation of the
         * jungle edge biome, which only generates on the border of a jungle M and
         * another biome. There are very few to no tall trees in this relatively tiny
         * biome.
         */
        JUNGLE_EDGE_M(151), /**
         * A variation of the birch forest biome which features taller birch trees than
         * usual.
         */
        BIRCH_FOREST_M(155), /**
         * Basically its respective base biome, {@link #BIRCH_FOREST_M}, but partially
         * taller.
         */
        BIRCH_FOREST_HILLS_M(156), /**
         * A mountainous version of the roofed forest biome, with steep cliffs lining
         * the edge.
         */
        ROOFED_FOREST_M(157), /**
         * Large, mountainous version of the snowy {@link #COLD_TAIGA}.
         */
        COLD_TAIGA_M(158), /**
         * A variation of the mega taiga. In this biome there is a much higher density
         * of smaller spruce trees. Also, the tall trees look very similar to regular
         * spruce trees, as opposed to the short-topped trees in the normal mega taiga
         * biome.
         */
        MEGA_SPRUCE_TAIGA(160), /**
         * Basically its respective base biome, {@link #MEGA_SPRUCE_TAIGA}, but
         * partially taller.
         */
        REDWOOD_TAIGA_HILLS_M(161), /**
         * A rare variant of the extreme hills+ biome where huge gravel mountains
         * appear, with sparse oak and spruce trees and small patches of grass.
         */
        EXTREME_HILLS_PLUS_M(162), /**
         * Variant of the savanna biome. Dirt paths and giant mountains are prevalent in
         * this biome. However, this biome is unique in that its mountains can generate
         * past the clouds, and even to the world height limit, without using the
         * AMPLIFIED world type.
         */
        SAVANNA_M(163), /**
         * The savanna plateau M biome features incredibly large and steep cliffs that
         * jut violently out of the terrain, compared to the regular savanna plateau;
         * these cliffs generally exceed cloud height, sometimes above y=200, and
         * sometimes even border the world height limit.
         */
        SAVANNA_PLATEAU_M(164), /**
         * Mesa (Bryce) is a variant of the mesa biome, featuring a low desert-like
         * ground area with tall, thin, spire-shaped columns of hardened clay, similar
         * to the structures in the real Bryce Canyon.
         */
        MESA_BRYCE(165), /**
         * Very rare variant of the Mesa Plateau F biome. Features steeper cliffs than
         * the normal Mesa Plateau F biome, with a reduced occurrence of trees and
         * smaller, more erratic hills.
         */
        MESA_PLATEAU_F_M(166), /**
         * The mesa plateau M biome features slightly flatter terrain and smaller
         * plateaus in general, compared to the average mesa plateau biome.
         */
        MESA_PLATEAU_M(167), /**
         * A Collection of all mesa biomes.
         */
        COLLECTION_MESA(37, 38, 39, 165, 166, 167), /**
         * A Collection of all forest biomes - excluding swamplands, jungles and extreme
         * hills with trees. 16 biomes in total.
         */
        COLLECTION_FORESTS_ALL(4, 18, 27, 28, 29, 30, 31, 32, 33, 132, 133, 155, 156, 157, 158, 160), /**
         * A Collection consisting of all 3 desert biomes; {@link #DESERT},
         * {@link #DESERT_HILLS} and {@link #DESERT_M}.
         */
        COLLECTION_DESERTS(2, 17, 130), /**
         * A Collection of all 3 plains; consisting of {@link #PLAINS},
         * {@link #SUNFLOWER_PLAINS} and {@link #ICE_PLAINS}.
         */
        COLLECTION_PLAINS(1, 12, 129), /**
         * A Collection consisting of all 5 icy biomes; {@link #ICE_MOUNTAINS},
         * {@link #ICE_PLAINS}, {@link #ICE_SPIKES}, {@link #FROZEN_OCEAN} and
         * {@link #FROZEN_RIVER}.
         */
        COLLECTION_ICY_BIOMES(12, 13, 10, 11, 140), /**
         * A Collection consisting of all taiga biomes - including mega- and cold
         * taigas. 8 biomes in total.
         */
        COLLECTION_TAIGA(30, 31, 32, 33, 133, 158, 161, 5), /**
         * A Collection consisting of all 5 water-y biomes, oceans and rivers.
         */
        COLLECTION_WATER(0, 7, 10, 11, 24), /**
         * A Collection consisting of all 3 beaches; {@link #BEACHES},
         * {@link #STONE_BEACH} and {@link #COLD_BEACH}.
         */
        COLLECTION_BEACHES(16, 25, 26), /**
         * A Collection consisting of all 5 jungle biomes; {@link #JUNGLE},
         * {@link #JUNGLE_M}, {@link #JUNGLE_HILLS}, {@link #JUNGLE_EDGE} and
         * {@link #JUNGLE_EDGE_M}.
         */
        COLLECTION_JUNGLE(21, 22, 23, 149, 151), /**
         * A Collection consisting of all 4 extreme hills biomes;
         * {@link #EXTREME_HILLS_EDGE}, {@link #EXTREME_HILLS_M},
         * {@link #EXTREME_HILLS_PLUS_M} , {@link #EXTREME_HILLS_WITH_TREES} and
         * {@link #EXTREME_HILLS}.
         */
        COLLECTION_EXTREME_HILLS(3, 20, 34, 131, 162), /**
         * A Collection consisting of the two swampland biomes; {@link #SWAMPLAND} and
         * {@link #SWAMPLAND_M}.
         */
        COLLECTION_SWAMPLAND(6, 134), /**
         * A Collection consisting of all x savanna variants; {@link #SAVANNA_M},
         * {@link #SAVANNA_PLATEAU_M}, {@link #SAVANNA_PLATEAU} and {@link #SAVANNA}.
         */
        COLLECTION_SAVANNA(35, 36, 163, 164), /**
         * ~lé sigh~ A Collection consisting of every single biome type in the game.
         */
        COLLECTION_ALL(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 127, 129, 130, 131, 132, 133, 134, 140, 149, 151, 155, 156, 157, 158, 160, 161, 162, 163, 164, 165, 166, 167);
        private static final Biome[] ID_LOOKUP_TABLE = new Biome[168];

        static {
            final Biome[] values = Biome.values();
            for(final Biome biome : values) {
                Biome.ID_LOOKUP_TABLE[BiomeBase.REGISTRY_ID.a(biome.biome)] = biome;
            }
        }

        private final BiomeBase[] biomes;
        private final BiomeBase biome;

        private Biome(final int... ids) {
            assert ids.length > 0;
            this.biomes = new BiomeBase[ids.length];
            for(int i = 0; i < this.biomes.length; ++i) {
                this.biomes[i] = BiomeBase.REGISTRY_ID.getId(ids[i]);
            }
            this.biome = this.biomes[0];
        }

        public static Biome fromId(final int id) {
            return Biome.ID_LOOKUP_TABLE[id];
        }

        public BiomeBase asNMSBiome() {
            return this.biome;
        }

        public BiomeBase[] getNMSBiomeArray() {
            return this.biomes;
        }
    }

    public enum Type {
        DROPPED_ITEM(1, "item", EntityItem.class, MobMeta.UNDEFINED, true), EXPERIENCE_ORB(2, "xp_orb", EntityExperienceOrb.class, MobMeta.UNDEFINED, true), AREA_EFFECT_CLOUD(3, "area_effect_cloud", EntityAreaEffectCloud.class, MobMeta.UNDEFINED, true), ELDER_GUARDIAN(4, "elder_guardian", EntityGuardianElder.class, MobMeta.MONSTER, false), WITHER_SKELETON(5, "wither_skeleton", EntitySkeletonWither.class, MobMeta.MONSTER, false), STRAY(6, "stray", EntitySkeletonStray.class, MobMeta.MONSTER, false), THROWN_EGG(7, "egg", EntityEgg.class, MobMeta.UNDEFINED, true), LEAD_KNOT(8, "leash_knot", EntityLeash.class, MobMeta.UNDEFINED, true), PAINTING(9, "painting", EntityPainting.class, MobMeta.UNDEFINED, true), ARROW(10, "arrow", EntityArrow.class, MobMeta.UNDEFINED, true), SNOWBALL(11, "snowball", EntitySnowball.class, MobMeta.UNDEFINED, true), FIREBALL(12, "fireball", EntityFireball.class, MobMeta.UNDEFINED, true), SMALL_FIREBALL(13, "fireball", EntitySmallFireball.class, MobMeta.UNDEFINED, true), ENDER_PEARL(14, "ender_pearl", EntityEnderPearl.class, MobMeta.UNDEFINED, true), EYE_OF_ENDER(15, "eye_of_ender_signal", EntityEnderSignal.class, MobMeta.UNDEFINED, true), POTION(16, "potion", EntityPotion.class, MobMeta.UNDEFINED, true), EXP_BOTTLE(17, "xp_bottle", EntityThrownExpBottle.class, MobMeta.UNDEFINED, true), ITEM_FRAME(18, "item_frame", EntityItemFrame.class, MobMeta.UNDEFINED, true), WITHER_SKULL(19, "wither_skull", EntityWitherSkull.class, MobMeta.UNDEFINED, true), PRIMED_TNT(20, "tnt", EntityTNTPrimed.class, MobMeta.UNDEFINED, true), FALLING_BLOCK(21, "falling_block", EntityFallingBlock.class, MobMeta.UNDEFINED, true), FIREWORK_ROCKET(22, "fireworks_rocket", EntityFireworks.class, MobMeta.UNDEFINED, true), HUSK(23, "husk", EntityZombieHusk.class, MobMeta.MONSTER, false), SPECTRAL_ARROW(24, "spectral_arrow", EntitySpectralArrow.class, MobMeta.UNDEFINED, true), SHULKER_BULLET(25, "shulker_bullet", EntityShulkerBullet.class, MobMeta.UNDEFINED, true), DRAGON_FIREBALL(26, "dragon_fireball", EntityDragonFireball.class, MobMeta.UNDEFINED, true), ZOMBIE_VILLAGER(27, "zombie_villager", EntityZombieVillager.class, MobMeta.MONSTER, false), SKELETON_HORSE(28, "skeleton_horse", EntityHorseSkeleton.class, MobMeta.CREATURE, false), ZOMBIE_HORSE(29, "zombie_horse", EntityHorseZombie.class, MobMeta.CREATURE, false), ARMOR_STAND(30, "armor_stand", EntityArmorStand.class, MobMeta.UNDEFINED, true), DONKEY(31, "donkey", EntityHorseDonkey.class, MobMeta.CREATURE, false), MULE(32, "mule", EntityHorseMule.class, MobMeta.CREATURE, false), EVOCATION_FANGS(33, "evocation_fangs", EntityEvokerFangs.class, MobMeta.UNDEFINED, true), EVOKER(34, "evocation_illager", EntityEvoker.class, MobMeta.MONSTER, false), VEX(35, "vex", EntityVex.class, MobMeta.MONSTER, false), VINDICATOR(36, "vindication_illager", EntityVindicator.class, MobMeta.MONSTER, false), ILLUSIONER(37, "illusion_illager", EntityIllagerIllusioner.class, MobMeta.MONSTER, false), COMMAND_BLOCK_MINECART(40, "commandblock_minecart", EntityMinecartCommandBlock.class, MobMeta.UNDEFINED, true), BOAT(41, "boat", EntityBoat.class, MobMeta.UNDEFINED, true), MINECART(42, "minecart", EntityMinecartRideable.class, MobMeta.UNDEFINED, true), CHEST_MINECART(43, "chest_minecart", EntityMinecartChest.class, MobMeta.UNDEFINED, true), FURNACE_MINECART(44, "furnace_minecart", EntityMinecartFurnace.class, MobMeta.UNDEFINED, true), TNT_MINECART(45, "tnt_minecart", EntityMinecartTNT.class, MobMeta.UNDEFINED, true), HOPPER_MINECART(46, "hopper_minecart", EntityMinecartHopper.class, MobMeta.UNDEFINED, true), SPAWNER_MINECART(47, "spawner_minecart", EntityMinecartMobSpawner.class, MobMeta.UNDEFINED, true), CREEPER(50, "creeper", EntityCreeper.class, MobMeta.MONSTER, false), SKELETON(51, "skeleton", EntitySkeleton.class, MobMeta.MONSTER, false), SPIDER(52, "spider", EntitySpider.class, MobMeta.MONSTER, false), GIANT(53, "giant", EntityGiantZombie.class, MobMeta.MONSTER, false), ZOMBIE(54, "zombie", EntityZombie.class, MobMeta.MONSTER, false), SLIME(55, "slime", EntitySlime.class, MobMeta.MONSTER, false), GHAST(56, "ghast", EntityGhast.class, MobMeta.MONSTER, false), ZOMBIE_PIGMAN(57, "zombie_pigman", EntityPigZombie.class, MobMeta.MONSTER, false), ENDERMAN(58, "enderman", EntityEnderman.class, MobMeta.MONSTER, false), CAVE_SPIDER(59, "cave_spider", EntityCaveSpider.class, MobMeta.MONSTER, false), SILVERFISH(60, "silverfish", EntitySilverfish.class, MobMeta.MONSTER, false), BLAZE(61, "blaze", EntityBlaze.class, MobMeta.MONSTER, false), MAGMACUBE(62, "magma_cube", EntityMagmaCube.class, MobMeta.MONSTER, false), ENDER_DRAGON(63, "ender_dragon", EntityEnderDragon.class, MobMeta.MONSTER, false), WITHER(64, "wither", EntityWither.class, MobMeta.MONSTER, false), BAT(65, "bat", EntityBat.class, MobMeta.AMBIENT, false), WITCH(66, "witch", EntityWitch.class, MobMeta.MONSTER, false), ENDERMITE(67, "endermite", EntityEndermite.class, MobMeta.MONSTER, false), GUARDIAN(68, "guardian", EntityGuardian.class, MobMeta.MONSTER, false), SHULKER(69, "shulker", EntityShulker.class, MobMeta.MONSTER, false), PIG(90, "pig", EntityPig.class, MobMeta.CREATURE, false), SHEEP(91, "sheep", EntitySheep.class, MobMeta.CREATURE, false), COW(92, "cow", EntityCow.class, MobMeta.CREATURE, false), CHICKEN(93, "chicken", EntityChicken.class, MobMeta.CREATURE, false), SQUID(94, "squid", EntitySquid.class, MobMeta.WATER_CREATURE, false), WOLF(95, "wolf", EntityWolf.class, MobMeta.CREATURE, false), MOOSHROOM(96, "mooshroom", EntityMushroomCow.class, MobMeta.CREATURE, false), SNOWMAN(97, "snowman", EntitySnowman.class, MobMeta.CREATURE, false), OCELOT(98, "ocelot", EntityOcelot.class, MobMeta.CREATURE, false), IRON_GOLEM(99, "villager_golem", EntityIronGolem.class, MobMeta.CREATURE, false), HORSE(100, "horse", EntityHorse.class, MobMeta.CREATURE, false), RABBIT(101, "rabbit", EntityRabbit.class, MobMeta.CREATURE, false), POLARBEAR(102, "polar_bear", EntityPolarBear.class, MobMeta.CREATURE, false), LLAMA(103, "llama", EntityLlama.class, MobMeta.CREATURE, false), LLAMA_SPIT(104, "llama_spit", EntityLlamaSpit.class, MobMeta.UNDEFINED, true), PARROT(105, "parrot", EntityParrot.class, MobMeta.CREATURE, false), VILLAGER(120, "villager", EntityVillager.class, MobMeta.CREATURE, false), ENDER_CRYSTAL(200, "ender_crystal", EntityEnderCrystal.class, MobMeta.UNDEFINED, true);

        private final int id;
        private final String name;
        private final Class<? extends Entity> clazz;
        private final MobMeta meta;
        private final boolean special;

        private Type(final int id, final String name, final Class<? extends Entity> nmsClazz, final MobMeta meta, final boolean special) {
            this.id = id;
            this.name = name;
            this.clazz = nmsClazz;
            this.meta = meta;
            this.special = special;
        }

        public MobMeta getMeta() {
            return this.meta;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public Class<? extends Entity> getNMSClass() {
            return this.clazz;
        }

        public boolean isSpecial() {
            return this.special;
        }
    }

    public enum MobMeta {
        MONSTER(NMSUtils.META_LIST_MONSTER), CREATURE(NMSUtils.META_LIST_CREATURE), WATER_CREATURE(NMSUtils.META_LIST_WATER_CREATURE), AMBIENT(NMSUtils.META_LIST_AMBIENT), UNDEFINED(null);

        private final Field field;

        private MobMeta(final Field field) {
            this.field = field;
        }

        /**
         * @return the BiomeMeta list field of this entity.
         * <p>
         * <b>Undefined will not be accepted and returns null.</b>
         * </p>
         */
        public Field getField() {
            return this.field;
        }
    }

    public static enum Attributes {
        MAX_HEALTH("generic.maxHealth", GenericAttributes.maxHealth), MOVEMENT_SPEED("generic.movementSpeed", GenericAttributes.MOVEMENT_SPEED), ATTACK_DAMAGE("generic.attackDamage", GenericAttributes.ATTACK_DAMAGE), FOLLOW_RANGE("generic.followRange", GenericAttributes.FOLLOW_RANGE), LUCK("generic.luck", GenericAttributes.j), ARMOR("generic.armor", GenericAttributes.h), ARMOR_TOUGHNESS("generic.armorToughness", GenericAttributes.i), ATTACK_SPEED("generic.attackSpeed", GenericAttributes.g), KNOCKBACK_RESISTANCE("generic.knockbackResistance", GenericAttributes.c);

        private final String name;
        private final IAttribute attribute;

        private Attributes(final String nmsName, final IAttribute nmsAttribute) {
            this.name = nmsName;
            this.attribute = nmsAttribute;
        }

        /**
         * Returns the NMS name of the attribute. For example, <code>MAX_HEALTH</code>
         * returns <code>"generic.maxHealth"</code>, and so on and so forth.
         *
         * @return The name as a String.
         */
        public String getName() {
            return this.name;
        }

        /**
         * @return the IAttribute value of this type, used in place of
         * <code>GenericAttributes.h</code> (for Attributes.ARMOR as an
         * example).
         */
        public IAttribute asIAttribute() {
            return this.attribute;
        }
    }

    public static class SpawnData extends BiomeMeta {

        /**
         * Creates a new instance of SpawnData, and at the same time, a new instanceof
         * BiomeMeta, used to add random spawns and such.
         *
         * @param customClass - Your class to spawn
         * @param spawnWeight - The chance for the mob(s) to spawn.
         * @param minSpawns   - The minimum amount of entities spawned at once.
         * @param maxSpawns   - The maximum amount of entities spawned at once.
         */
        public SpawnData(final Class<? extends EntityInsentient> customClass, final int spawnWeight, final int minSpawns, final int maxSpawns) {
            super(customClass, spawnWeight, minSpawns, maxSpawns);
        }

        public Class<? extends EntityInsentient> getCustomClass() {
            return this.b;
        }

        public int getSpawnWeight() {
            return this.a;
        }

        public int getMinSpawns() {
            return this.c;
        }

        public int getMaxSpawns() {
            return this.d;
        }
    }

    public class NBTTagType {
        public static final int COMPOUND = 10;
        public static final int LIST = 9;
        public static final int STRING = 8;
        public static final int LONG_ARRAY = 12;
        public static final int INT_ARRAY = 11;
        public static final int BYTE_ARRAY = 7;
        public static final int DOUBLE = 6;
        public static final int FLOAT = 5;
        public static final int LONG = 4;
        public static final int INT = 3;
        public static final int SHORT = 2;
        public static final int BYTE = 1;
        public static final int BOOLEAN = 1;
        public static final int END = 0;
    }
}