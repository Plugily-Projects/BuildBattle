package me.tomthedeveloper.buildbattle.utils;

import me.tomthedeveloper.buildbattle.GameAPI;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.Random;

/**
 * Created by Tom on 2/08/2014.
 */
public class Items {

    public static GameAPI gameAPI;
    private static ItemStack spectatoritem = null;

    public static ItemStack getSpecatorItemStack() {
        if(spectatoritem != null) return spectatoritem;
        ItemStack itemStack = new ItemStack(Material.COMPASS);
        Util.setItemNameAndLore(itemStack, ChatColor.GOLD + "Specator Menu", new String[]{ChatColor.GRAY + "Right click to open menu!"});
        return itemStack;
    }

    public static void setSpectatoritem(ItemStack itemStack) {
        spectatoritem = itemStack;
    }

    public static ItemStack getRandomFireworkItem() {
        ItemStack firework = new ItemStack(Material.FIREWORK, 1);
        FireworkMeta metaData = (FireworkMeta) firework.getItemMeta();
        Random random = new Random();
        metaData.addEffects(FireworkEffect.builder().withColor(Color.fromRGB(random.nextInt(250))).with(FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length - 1)]).build());
        metaData.setPower(2);
        firework.setItemMeta(metaData);
        return firework;
    }

    public static ItemStack getPotion(PotionType type, int tier, boolean splash, int amount) {
        if(gameAPI.is1_7_R4() || gameAPI.is1_8_R3()) {
            Potion potion = new Potion(type);
            potion.setLevel(tier);
            potion.setSplash(splash);
            return potion.toItemStack(amount);
        } else {
            //FOR 1.9 AND LATER
            ItemStack potion;
            if(!splash) {
                potion = new ItemStack(Material.POTION, 1);
            } else {
                potion = new ItemStack(Material.SPLASH_POTION, 1);
            }

            PotionMeta meta = (PotionMeta) potion.getItemMeta();
            if(tier >= 2 && !splash) {
                meta.setBasePotionData(new PotionData(type, false, true));
            } else {
                meta.setBasePotionData(new PotionData(type, false, false));
            }
            potion.setItemMeta(meta);
            return potion;
        }
    }

    public static ItemStack getPlayerHead(OfflinePlayer player) {
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwner(player.getName());
        itemStack.setItemMeta(skullMeta);
        itemStack.setDurability((short) SkullType.PLAYER.ordinal());
        return itemStack;
    }


}
