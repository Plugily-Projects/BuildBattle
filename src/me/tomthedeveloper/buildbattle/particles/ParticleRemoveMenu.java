package me.tomthedeveloper.buildbattle.particles;

import me.TomTheDeveloper.Handlers.ChatManager;
import me.TomTheDeveloper.Utils.Util;
import me.tomthedeveloper.buildbattle.BuildPlot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tom on 24/08/2015.
 */
public class ParticleRemoveMenu {


    public static void openMenu(Player player, BuildPlot buildPlot) {
        Inventory inventory = player.getServer().createInventory(player, 6 * 9, ChatManager.getSingleMessage("Particle-Remove-Menu-Name", ChatColor.RED + "Remove Particles"));

        for(Location location : buildPlot.getParticles().keySet()) {
            ParticleItem particleItem = ParticleMenu.getParticleItem(buildPlot.getParticles().get(location));
            ItemStack itemStack = particleItem.getItemStack();
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setLore(new ArrayList<>());
            itemStack.setItemMeta(itemMeta);
            Util.addLore(itemStack, ChatManager.getSingleMessage("Location", ChatColor.GRAY + "Location: "));
            Util.addLore(itemStack, ChatColor.GRAY + "  x: " + Math.round(location.getX()));
            Util.addLore(itemStack, ChatColor.GRAY + "  y: " + Math.round(location.getY()));
            Util.addLore(itemStack, ChatColor.GRAY + "  z: " + Math.round(location.getZ()));
            inventory.addItem(itemStack);
        }

        player.openInventory(inventory);
    }


    public static void onClick(Inventory inventory, ItemStack itemStack, BuildPlot buildPlot) {
        List<String> lore = itemStack.getItemMeta().getLore();
        double x = 0, y = 0, z = 0;
        for(String string : lore) {
            if(string.contains("x:")) {
                x = getInt(ChatColor.stripColor(string));
            }
            if(string.contains("y:")) {
                y = getInt(ChatColor.stripColor(string));
            }
            if(string.contains("z:")) {
                z = getInt(ChatColor.stripColor(string));
            }
        }

        for(Location location : buildPlot.getParticles().keySet()) {

            if(Math.round(location.getX()) == x
                    && Math.round(location.getY()) == y
                    && Math.round(location.getZ()) == z) {
                buildPlot.getParticles().remove(location);
                Bukkit.getServer().getPlayer(buildPlot.getOwner()).sendMessage(ChatManager.getSingleMessage("Particle-Removed", ChatColor.GREEN + "Particle Removed!"));
                inventory.remove(itemStack);
                Bukkit.getServer().getPlayer(buildPlot.getOwner()).updateInventory();
                break;
            }
        }
    }


    private static int getInt(String string) {
        Pattern pattern = Pattern.compile("-?\\d+");
        Matcher matcher = pattern.matcher(string);
        if(matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return 0;
    }

   /* public Location getLocationFromLore(ItemStack itemStack){
        List<String> lore = itemStack.getItemMeta().getLore();
        //lore.get(lore.size()-1)
        re
    } */
}
