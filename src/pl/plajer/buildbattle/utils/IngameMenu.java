package pl.plajer.buildbattle.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.plajer.buildbattle.BuildPlot;
import pl.plajer.buildbattle.handlers.ChatManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Tom on 18/08/2015.
 */
public class IngameMenu {

    public static ItemStack getMenuItem() {
        return new ItemBuilder(Material.NETHER_STAR).name(ChatManager.getSingleMessage("Options-Menu-Item", ChatColor.GREEN + "Options")).lore(ChatManager.getSingleMessage("Options-Lore", ChatColor.GRAY + "Right click to open")).build();
    }

    private static Inventory createMenu(BuildPlot plot) {
        Inventory inv = Bukkit.createInventory(null, 3 * 9, ChatManager.getSingleMessage("Ingame-Menu-Name", "Option Menu"));

        ItemStack headOption = new ItemStack(Material.SKULL_ITEM, 1);
        headOption.setTypeId((byte) SkullType.PLAYER.ordinal());
        ItemMeta headMeta = headOption.getItemMeta();
        headMeta.setDisplayName(ChatManager.getSingleMessage("Heads-Option-Name", ChatColor.GREEN + "Heads"));
        headMeta.setLore(Collections.singletonList(ChatManager.getSingleMessage("Heads-Option-Lore", ChatColor.GRAY + "Open for heads menu!")));
        headOption.setItemMeta(headMeta);
        inv.setItem(11, headOption);

        ItemStack particleOption = new ItemStack(Material.YELLOW_FLOWER, 1);
        ItemMeta particleMeta = particleOption.getItemMeta();
        particleMeta.setDisplayName(ChatManager.getSingleMessage("Particle-Option-Name", ChatColor.GREEN + "Particles"));
        particleMeta.setLore(Collections.singletonList(ChatManager.getSingleMessage("Particle-Option-Lore", ChatColor.GRAY + "Click to open menu")));
        particleOption.setItemMeta(particleMeta);
        inv.setItem(13, particleOption);

        ItemStack floorOption = new ItemStack(plot.getFloorMaterial(), 1);
        ItemMeta floorMeta = floorOption.getItemMeta();
        floorMeta.setDisplayName(ChatManager.getSingleMessage("Floor-Option-Name", ChatColor.GREEN + "Floor Material"));
        List<String> lore = new ArrayList<>();
        lore.add(ChatManager.getSingleMessage("Drag-And-Drop-Item-Here", "Drag and drop an item here"));
        lore.add(ChatManager.getSingleMessage("To-Change-Floor", "to change the floor"));
        floorMeta.setLore(lore);
        floorOption.setItemMeta(floorMeta);
        inv.setItem(15, floorOption);
        return inv;
    }

    public static void openMenu(Player player, BuildPlot buildPlot) {
        player.openInventory(createMenu(buildPlot));
    }


}
