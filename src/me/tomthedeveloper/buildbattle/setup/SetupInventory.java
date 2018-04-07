package me.tomthedeveloper.buildbattle.setup;

import me.tomthedeveloper.buildbattle.game.GameInstance;
import me.tomthedeveloper.buildbattle.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Tom on 15/06/2015.
 */
public class SetupInventory {

    private Inventory inventory;

    public SetupInventory(GameInstance gameInstance) {
        this.inventory = Bukkit.createInventory(null, 9 * 2, "Arena: " + gameInstance.getID());

        addItem(new ItemBuilder(new ItemStack(Material.REDSTONE_BLOCK)).name(ChatColor.GOLD + "Set End Location").lore(ChatColor.GRAY + "Click to set the end location.").lore(ChatColor.GRAY + "on the place where you are standing").build(), GameInstance.getPlugin().getAbreviation() + " " + gameInstance.getID() + " set ENDLOC");
        addItem(new ItemBuilder(new ItemStack(Material.LAPIS_BLOCK)).name(ChatColor.GOLD + "Set Lobby Location").lore(ChatColor.GRAY + "Click to set the lobby location").lore(ChatColor.GRAY + "on the place where you are standing").build(), GameInstance.getPlugin().getAbreviation() + " " + gameInstance.getID() + " set LOBBYLOC");
        addItem(new ItemBuilder(new ItemStack(Material.EMERALD_BLOCK)).name(ChatColor.GOLD + "Set Start Location").lore(ChatColor.GRAY + "Click to set the start location.").lore(ChatColor.GRAY + "on the place where you are standing").build(), GameInstance.getPlugin().getAbreviation() + " " + gameInstance.getID() + " set STARTLOC");
        addItem(new ItemBuilder(new ItemStack(Material.EMERALD, gameInstance.getMIN_PLAYERS())).name(ChatColor.GOLD + "Set min players").lore(ChatColor.GRAY + "LEFT click to decrease").lore(ChatColor.GRAY + "RIGHT click to increase").build(), GameInstance.getPlugin().getAbreviation() + " " + gameInstance.getID() + " set MINPLAYERS " + gameInstance.getMIN_PLAYERS());
        addItem(new ItemBuilder(new ItemStack(Material.EMERALD, gameInstance.getMAX_PLAYERS())).name(ChatColor.GOLD + "Set max players").lore(ChatColor.GRAY + "LEFT click to decrease").lore(ChatColor.GRAY + "RIGHT click to increase").build(), GameInstance.getPlugin().getAbreviation() + " " + gameInstance.getID() + " set MAXPLAYERS " + gameInstance.getMAX_PLAYERS());
        if(!GameInstance.getPlugin().isBungeeActivated()) {
            addItem(new ItemBuilder(new ItemStack(Material.SIGN)).name(ChatColor.GOLD + "Add signs").lore(ChatColor.GRAY + "Select a region with your").lore(ChatColor.GRAY + "world edit wand and click this.").lore(ChatColor.RED + "Be aware that you have Bungee disabled!").build(), "addsigns");
        }
        addItem(new ItemBuilder(new ItemStack(Material.NAME_TAG)).name(ChatColor.GOLD + "Set a mapname (currently: " + gameInstance.getMapName()).lore(ChatColor.GRAY + "Replace this nametag with another nametag").lore(ChatColor.GRAY + "that is named as the mapname").build(), GameInstance.getPlugin().getAbreviation() + " " + gameInstance.getID() + " set MAPNAME <NAME>");
    }

    public void addItem(ItemStack itemStack, String command) {
        inventory.addItem(new ItemBuilder(itemStack).lore(ChatColor.RED + "Command: " + ChatColor.GRAY + "/" + command).build());
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void openInventory(Player player) {
        player.openInventory(inventory);
    }


}
