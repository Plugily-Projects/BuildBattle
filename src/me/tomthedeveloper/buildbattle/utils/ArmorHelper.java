package me.tomthedeveloper.buildbattle.utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * Created by Tom on 7/08/2014.
 */
public class ArmorHelper {


    public static void setArmor(LivingEntity player, ArmorType type) {
        switch(type) {
            case LEATHER:
                player.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
                player.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
                player.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
                player.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
                break;
            case IRON:
                player.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
                player.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                player.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                player.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
                break;
            case GOLD:
                player.getEquipment().setBoots(new ItemStack(Material.GOLD_BOOTS));
                player.getEquipment().setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
                player.getEquipment().setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
                player.getEquipment().setHelmet(new ItemStack(Material.GOLD_HELMET));
                break;
            case DIAMOND:
                player.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                player.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                player.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                player.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                break;
        }

    }

    public static void setArmor(Player player, ArmorType type) {
        switch(type) {
            case LEATHER:
                player.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
                player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
                player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
                player.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
                break;
            case IRON:
                player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
                player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
                break;
            case GOLD:
                player.getInventory().setBoots(new ItemStack(Material.GOLD_BOOTS));
                player.getInventory().setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
                player.getInventory().setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
                player.getInventory().setHelmet(new ItemStack(Material.GOLD_HELMET));
                break;
            case DIAMOND:
                player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                player.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                break;
        }

    }


    public static void setColouredArmor(Color color, Player player) {
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta helmetmeta = (LeatherArmorMeta) helmet.getItemMeta();
        LeatherArmorMeta chestplatemeta = (LeatherArmorMeta) chestplate.getItemMeta();
        LeatherArmorMeta leggingsmeta = (LeatherArmorMeta) leggings.getItemMeta();
        LeatherArmorMeta bootsmeta = (LeatherArmorMeta) boots.getItemMeta();
        helmetmeta.setColor(color);
        chestplatemeta.setColor(color);
        leggingsmeta.setColor(color);
        bootsmeta.setColor(color);
        helmet.setItemMeta(helmetmeta);
        chestplate.setItemMeta(chestplatemeta);
        leggings.setItemMeta(leggingsmeta);
        boots.setItemMeta(bootsmeta);
        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
    }

    public static void clearArmor(Player player) {
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }


    public enum ArmorType {
        LEATHER, IRON, DIAMOND, GOLD;
    }

}


