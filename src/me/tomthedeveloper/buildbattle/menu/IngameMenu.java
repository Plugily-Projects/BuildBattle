package me.tomthedeveloper.buildbattle.menu;

import me.tomthedeveloper.buildbattle.BuildPlot;
import me.tomthedeveloper.buildbattle.handlers.ChatManager;
import me.tomthedeveloper.buildbattle.menuapi.IconMenu;
import me.tomthedeveloper.buildbattle.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 18/08/2015.
 */
public class IngameMenu implements Listener {


    private ItemStack menuItem;
    private BuildPlot buildPlot;

    public IngameMenu(BuildPlot buildPlot) {
        this.buildPlot = buildPlot;


    }

    public static ItemStack getMenuItem() {
        return new ItemBuilder(Material.NETHER_STAR).name(ChatManager.getSingleMessage("Options-Menu-Item", ChatColor.GREEN + "Options")).lore(ChatManager.getSingleMessage("Options-Lore", ChatColor.GRAY + "Right click to open")).build();
    }


    private static IconMenu createMenu(BuildPlot plot) {
        IconMenu iconMenu = new IconMenu(ChatManager.getSingleMessage("Ingame-Menu-Name", "Option Menu"), 3 * 9);
        MenuOption floorOption = new MenuOption(plot);
        floorOption.setMaterial(plot.getFloorMaterial());
        floorOption.setData(plot.getFloorData());
        floorOption.setDisplayname(ChatManager.getSingleMessage("Floor-Option-Name", ChatColor.GREEN + "Floor Material"));
        List<String> lorelist = new ArrayList<>();
        lorelist.add(ChatManager.getSingleMessage("Drag-And-Drop-Item-Here", "Drag and drop an item here"));
        lorelist.add(ChatManager.getSingleMessage("To-Change-Floor", "to change the floor"));
        floorOption.setLore(lorelist);
        iconMenu.addOption(floorOption.getItemStack(), 15);
        MenuOption particleOption = new MenuOption(plot);
        particleOption.setMaterial(Material.YELLOW_FLOWER);
        particleOption.setDisplayname(ChatManager.getSingleMessage("Particle-Option-Name", ChatColor.GREEN + "Particles"));
        List<String> particlelorelist = new ArrayList<>();
        particlelorelist.add(ChatManager.getSingleMessage("Particle-Option-Lore", ChatColor.GRAY + "Click to open menu"));
        particleOption.setLore(particlelorelist);
        iconMenu.addOption(particleOption.getItemStack(), 13);
        MenuOption playerheadoption = new MenuOption(plot);
        playerheadoption.setMaterial(Material.SKULL_ITEM);
        playerheadoption.setData((byte) SkullType.PLAYER.ordinal());
        playerheadoption.setDisplayname(ChatManager.getSingleMessage("Heads-Option-Name", ChatColor.GREEN + "Heads"));
        List<String> playerheadlore = new ArrayList<>();
        playerheadlore.add(ChatManager.getSingleMessage("Heads-Option-Lore", ChatColor.GRAY + "Open for heads menu!"));
        playerheadoption.setLore(playerheadlore);
        iconMenu.addOption(playerheadoption.getItemStack(), 11);
        return iconMenu;
    }

    public static void openMenu(Player player, BuildPlot buildPlot) {
        createMenu(buildPlot).open(player);
    }


}
