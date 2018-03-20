package me.tomthedeveloper.buildbattle.entities;

import me.TomTheDeveloper.Handlers.ChatManager;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by Tom on 8/01/2016.
 */
public class BuildBattleEntity {

    private Entity entity;


    public BuildBattleEntity(Entity entity) {
        this.entity = entity;
    }

    public UUID getUniqueID() {
        return entity.getUniqueId();
    }

    private Boolean isAdult() {
        switch(entity.getType()) {
            case COW:
                return ((Cow) entity).isAdult();
            case SHEEP:
                return ((Sheep) entity).isAdult();
            case PIG:
                return ((Pig) entity).isAdult();
            case WOLF:
                return ((Wolf) entity).isAdult();
            case ZOMBIE:
                return !((Zombie) entity).isBaby();
            case PIG_ZOMBIE:
                return !((PigZombie) entity).isBaby();
            case CHICKEN:
                return ((Chicken) entity).isAdult();
            case HORSE:
                return ((Horse) entity).isAdult();
            case MUSHROOM_COW:
                return ((MushroomCow) entity).isAdult();
            case VILLAGER:
                return ((Villager) entity).isAdult();
            default:
                return null;


        }

    }

    private Boolean isSaddled() {
        return entity.getType() == EntityType.HORSE && ((Horse) entity).getInventory().getSaddle() == null;
    }

    private void setSaddle(boolean b) {
        if(entity.getType() == EntityType.HORSE) {
            Horse horse = (Horse) entity;
            if(!b) {
                horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
            } else {
                horse.getInventory().setSaddle(new ItemStack(Material.AIR));
            }
        }
    }


    public EntityType getType() {
        return entity.getType();
    }

    private boolean isMoveable() {
        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();

        NBTTagCompound tag = new NBTTagCompound();

        nmsEntity.c(tag);
        return !tag.getBoolean("NoAI");
    }

    public void setMoveable(boolean moveable) {
        if(isMoveable()) {
            net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();

            NBTTagCompound tag = new NBTTagCompound();

            nmsEntity.c(tag);
            tag.setInt("NoAI", 1);
            EntityLiving el = (EntityLiving) nmsEntity;
            el.a(tag);
        } else {
            net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();

            NBTTagCompound tag = new NBTTagCompound();

            nmsEntity.c(tag);
            tag.setInt("NoAI", 0);
            EntityLiving el = (EntityLiving) nmsEntity;
            el.a(tag);
           /* Location location = entity.getLocation();
            EntityType entityType = entity.getType();
            Boolean adult = this.isAdult();
            entity.getWorld().spawnEntity(location,entityType);
            if(adult != null)
                setBaby(adult);
            entity.remove(); */


        }
    }

    public void switchSaddle() {
        setSaddle(!isSaddled());
    }

    public void switchMoveeable() {
        setMoveable(!isMoveable());
    }

    public void switchAge() {
        setBaby(isAdult());
    }

    private void setBaby(boolean baby) {
        switch(entity.getType()) {
            case COW:
                Cow cow = (Cow) entity;
                if(cow.isAdult()) {
                    cow.setBaby();
                } else {
                    cow.setAdult();
                }
                break;
            case SHEEP:
                Sheep sheep = (Sheep) entity;
                if(sheep.isAdult()) {
                    sheep.setBaby();
                } else {
                    sheep.setAdult();
                }
                break;
            case PIG:
                Pig pig = (Pig) entity;
                if(pig.isAdult()) {
                    pig.setBaby();
                } else {
                    pig.setAdult();
                }
                break;
            case WOLF:
                Wolf wolf = (Wolf) entity;
                if(wolf.isAdult()) {
                    wolf.setBaby();
                } else {
                    wolf.setAdult();
                }
                break;
            case ZOMBIE:
                ((Zombie) entity).setBaby(baby);
                break;
            case PIG_ZOMBIE:
                ((PigZombie) entity).setBaby(baby);
                break;
            case CHICKEN:
                Chicken chicken = (Chicken) entity;
                if(chicken.isAdult()) {
                    chicken.setBaby();
                } else {
                    chicken.setAdult();
                }
                break;
            case HORSE:
                Horse horse = (Horse) entity;
                if(horse.isAdult()) {
                    horse.setBaby();
                } else {
                    horse.setAdult();
                }
                break;
            case MUSHROOM_COW:
                MushroomCow mushroomCow = (MushroomCow) entity;
                if(mushroomCow.isAdult()) {
                    mushroomCow.setBaby();
                } else {
                    mushroomCow.setAdult();
                }
                break;
            case VILLAGER:
                Villager villager = (Villager) entity;
                if(villager.isAdult())
                    villager.setBaby();
                else
                    villager.setAdult();
            default:
                break;
        }
    }

    public void remove() {
        entity.remove();
        entity = null;

    }

/*    public Inventory openInventory(){
        Inventory inventory = Bukkit.createInventory(null, 9);

    } */


    public Entity getEntity() {
        return entity;
    }

    public Inventory getMenu() {
        switch(entity.getType()) {
            case COW:
            case WOLF:
            case SHEEP:
            case ZOMBIE:
            case PIG_ZOMBIE:
            case CHICKEN:
                return new InventoryBuilder()
                        .setSlots(9)
                        .addItem(this.isAdult() ? EntityItemManager.getEntityItem("Adult") : EntityItemManager.getEntityItem("Baby"))
                        .addItem(this.isMoveable() ? EntityItemManager.getEntityItem("Move-On") : EntityItemManager.getEntityItem("Move-Off"))
                        .addItem(EntityItemManager.getEntityItem("Look-At-Me"))
                        .addItem(EntityItemManager.getEntityItem("Close"))
                        .addItem(EntityItemManager.getEntityItem("Despawn"))
                        .build();
            case SILVERFISH:
            case ENDERMAN:
            case SPIDER:
            case CAVE_SPIDER:
            case SQUID:
                return new InventoryBuilder()
                        .setSlots(9)
                        .addItem(this.isMoveable() ? EntityItemManager.getEntityItem("Move-On") : EntityItemManager.getEntityItem("Move-Off"))
                        .addItem(EntityItemManager.getEntityItem("Look-At-Me"))
                        .addItem(EntityItemManager.getEntityItem("Close"))
                        .addItem(EntityItemManager.getEntityItem("Despawn"))
                        .build();
            case HORSE:
                return new InventoryBuilder()
                        .setSlots(9)
                        .addItem(this.isMoveable() ? EntityItemManager.getEntityItem("Move-On") : EntityItemManager.getEntityItem("Move-Off"))
                        .addItem(EntityItemManager.getEntityItem("Look-At-Me"))
                        .addItem(this.isAdult() ? EntityItemManager.getEntityItem("Adult") : EntityItemManager.getEntityItem("Baby"))
                        .addItem(this.isSaddled() ? EntityItemManager.getEntityItem("Saddle-Off") : EntityItemManager.getEntityItem("Saddle-On"))
                        .addItem(EntityItemManager.getEntityItem("Close"))
                        .addItem(EntityItemManager.getEntityItem("Despawn"))
                        .build();
            case VILLAGER:
                return new InventoryBuilder()
                        .setSlots(9)
                        .addItem(this.isAdult() ? EntityItemManager.getEntityItem("Adult") : EntityItemManager.getEntityItem("Baby"))
                        .addItem(this.isMoveable() ? EntityItemManager.getEntityItem("Move-On") : EntityItemManager.getEntityItem("Move-Off"))
                        .addItem(EntityItemManager.getEntityItem("Look-At-Me"))
                        .addItem(EntityItemManager.getEntityItem("Close"))
                        .addItem(EntityItemManager.getEntityItem("Despawn"))
                        .addItem(EntityItemManager.getEntityItem("Profession-Villager-Selecting"))
                        .build();

            default:
                return new InventoryBuilder()
                        .setSlots(9)
                        .addItem(this.isMoveable() ? EntityItemManager.getEntityItem("Move-On") : EntityItemManager.getEntityItem("Move-Off"))
                        .addItem(EntityItemManager.getEntityItem("Look-At-Me"))
                        .addItem(EntityItemManager.getEntityItem("Close"))
                        .addItem(EntityItemManager.getEntityItem("Despawn"))
                        .build();

        }


    }

    public void setLook(Location location) {
    /*    CraftEntity craftEntity = ((CraftEntity) entity);
        EntityInsentient entEntity = (EntityInsentient) craftEntity.getHandle();
            try {
                Field field = entEntity.goalSelector.getClass().getDeclaredField("b");
                field.setAccessible(true);

                ((List<?>) field.get(entEntity.goalSelector)).clear();
            } catch(Exception e) {
                e.printStackTrace();
            }

        entEntity.getControllerLook().a(entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(), location.getYaw(),(location).getPitch());
 */

        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();

        NBTTagCompound tag = new NBTTagCompound();

        nmsEntity.c(tag);
        int AI = tag.getInt("NoAI");
        tag.setInt("NoAI", 0);
        entity.teleport(new Location(entity.getLocation().getWorld(), entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(),
                location.getYaw(), -location.getPitch()));
        tag.setInt("NoAI", AI);
        EntityLiving el = (EntityLiving) nmsEntity;
        el.a(tag);
    }

    public class InventoryBuilder {

        private int slots = 9;
        private String name = ChatManager.getSingleMessage("Entity-Menu-Name", "Entity Menu");
        private HashSet<EntityItem> items = new HashSet<>();


        private InventoryBuilder() {
        }

        InventoryBuilder setSlots(int slot) {
            this.slots = slot;
            return this;
        }

        InventoryBuilder addItem(EntityItem entityItem) {
            items.add(entityItem);
            return this;
        }

        Inventory build() {
            Inventory inventory = Bukkit.getServer().createInventory(null, slots, name);
            for(EntityItem entityItem : items) {
                inventory.setItem(entityItem.getSlot(), entityItem.getItemStack());

            }
            return inventory;
        }
    }
}


