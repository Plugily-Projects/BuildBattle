package me.tomthedeveloper.buildbattle.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tom on 12/08/2014.
 */
public class WeaponHelper {


    public static ItemStack getEnchantedBow(Enchantment enchantment, int level) {

        ItemStack itemStack = new ItemStack(Material.BOW);
        itemStack.addUnsafeEnchantment(enchantment, level);
        return itemStack;
    }

    public static ItemStack getEnchantedBow(Enchantment[] enchantments, int[] levels) {
        ItemStack itemStack = new ItemStack(Material.BOW);
        Map<Enchantment, Integer> enchantmentsmap = new HashMap<Enchantment, Integer>();
        int i = 0;
        for(Enchantment enchantment : enchantments) {
            enchantmentsmap.put(enchantment, levels[i]);
            i++;
        }
        itemStack.addUnsafeEnchantments(enchantmentsmap);
        return itemStack;
    }

    public static ItemStack getEnchanted(ItemStack itemStack, Enchantment[] enchantments, int[] levels) {
        Map<Enchantment, Integer> enchantmentsmap = new HashMap<Enchantment, Integer>();
        int i = 0;
        for(Enchantment enchantment : enchantments) {
            enchantmentsmap.put(enchantment, levels[i]);
            i++;
        }
        itemStack.addUnsafeEnchantments(enchantmentsmap);
        return itemStack;
    }

    public static ItemStack getUnBreakingSword(ResourceType type, int level) {
        ItemStack itemStack;
        switch(type) {
            case WOOD:
                itemStack = new ItemStack(Material.WOOD_SWORD);
                itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, level);
                return itemStack;
            case IRON:
                itemStack = new ItemStack(Material.IRON_SWORD);
                itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, level);
                return itemStack;
            case GOLD:
                itemStack = new ItemStack(Material.GOLD_SWORD);
                itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, level);
                return itemStack;
            case DIAMOND:
                itemStack = new ItemStack(Material.DIAMOND_SWORD);
                itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, level);
                return itemStack;
            case STONE:
                itemStack = new ItemStack(Material.STONE_SWORD);
                itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, level);
                return itemStack;
            default:
                return getUnBreakingSword(ResourceType.WOOD, 10);
        }
    }


    public enum ResourceType {
        WOOD, GOLD, STONE, DIAMOND, IRON;
    }


}
