/*
 *  Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
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
 */

package pl.plajer.buildbattle3.particles;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.plajer.buildbattle3.handlers.ChatManager;
import pl.plajer.buildbattle3.plots.Plot;
import pl.plajer.buildbattle3.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tom on 24/08/2015.
 */
public class ParticleRemoveMenu {

    public static void openMenu(Player player, Plot buildPlot) {
        Inventory inventory = player.getServer().createInventory(player, 6 * 9, ChatManager.colorMessage("Menus.Option-Menu.Particle-Remove"));

        for(Location location : buildPlot.getParticles().keySet()) {
            ParticleItem particleItem = ParticleMenu.getParticleItem(buildPlot.getParticles().get(location));
            ItemStack itemStack = particleItem.getItemStack();
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setLore(new ArrayList<>());
            itemStack.setItemMeta(itemMeta);
            //todo translatable
            Util.addLore(itemStack, ChatManager.colorMessage("Menus.Location-Message"));
            Util.addLore(itemStack, "§7  x: " + Math.round(location.getX()));
            Util.addLore(itemStack, "§7  y: " + Math.round(location.getY()));
            Util.addLore(itemStack, "§7  z: " + Math.round(location.getZ()));
            inventory.addItem(itemStack);
        }

        player.openInventory(inventory);
    }

    public static void onClick(Inventory inventory, ItemStack itemStack, Plot buildPlot) {
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
            if(Math.round(location.getX()) == x && Math.round(location.getY()) == y && Math.round(location.getZ()) == z) {
                buildPlot.getParticles().remove(location);
                Bukkit.getServer().getPlayer(buildPlot.getOwner()).sendMessage(ChatManager.colorMessage("In-Game.Particle-Removed"));
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
}
