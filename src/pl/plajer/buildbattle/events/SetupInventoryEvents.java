package pl.plajer.buildbattle.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.handlers.PermissionManager;

/**
 * Created by Tom on 15/06/2015.
 */
public class SetupInventoryEvents implements Listener {

    private Main plugin;

    public SetupInventoryEvents(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(event.getWhoClicked().getType() != EntityType.PLAYER) return;
        Player player = (Player) event.getWhoClicked();
        if(!player.hasPermission(PermissionManager.getEditGames())) return;
        if(!event.getInventory().getName().contains("Arena:")) return;
        if(event.getInventory().getHolder() != null) return;
        if(event.getCurrentItem() == null) return;
        if(!event.getCurrentItem().hasItemMeta()) return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName()) return;

        String name = event.getCurrentItem().getItemMeta().getDisplayName();

        Arena arena = ArenaRegistry.getArena(event.getInventory().getName().replace("Arena: ", ""));
        if(event.getCurrentItem().getType() == Material.NAME_TAG && event.getCursor().getType() == Material.NAME_TAG) {
            event.setCancelled(true);
            if(!event.getCursor().hasItemMeta()) {
                player.sendMessage(ChatColor.RED + "This item doesn't has a name!");
                return;
            }
            if(!event.getCursor().getItemMeta().hasDisplayName()) {
                player.sendMessage(ChatColor.RED + "This item doesn't has a name!");
                return;
            }

            player.performCommand("bb " + arena.getID() + " set MAPNAME " + event.getCursor().getItemMeta().getDisplayName());
            event.getCurrentItem().getItemMeta().setDisplayName(ChatColor.GOLD + "Set a mapname (currently: " + event.getCursor().getItemMeta().getDisplayName());
            return;
        }
        ClickType clickType = event.getClick();
        if(name.contains("End Location")) {
            event.setCancelled(true);

            player.performCommand("bb " + arena.getID() + " set ENDLOC");
            return;
        }
        if(name.contains("Start Location")) {
            event.setCancelled(true);

            player.performCommand("bb " + arena.getID() + " set STARTLOC");
            return;
        }
        if(name.contains("Lobby Location")) {
            event.setCancelled(true);
            player.performCommand("bb " + arena.getID() + " set LOBBYLOC");
            return;
        }
        if(name.contains("max players")) {
            event.setCancelled(true);
            if(clickType.isRightClick()) {
                event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() + 1);
                player.updateInventory();
                player.performCommand("bb " + arena.getID() + " set MAXPLAYERS " + event.getCurrentItem().getAmount());
                return;
            }
            if(clickType.isLeftClick()) {
                event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() - 1);
                player.updateInventory();
                player.performCommand("bb " + arena.getID() + " set MAXPLAYERS " + event.getCurrentItem().getAmount());
                return;
            }
        }

        if(name.contains("min players")) {
            event.setCancelled(true);
            if(clickType.isRightClick()) {
                event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() + 1);
                player.updateInventory();
                player.performCommand("bb " + arena.getID() + " set MINPLAYERS " + event.getCurrentItem().getAmount());
                return;
            }
            if(clickType.isLeftClick()) {
                event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() - 1);
                player.updateInventory();
                player.performCommand("bb " + arena.getID() + " set MINPLAYERS " + event.getCurrentItem().getAmount());
                return;
            }
        }
        if(name.contains("Add signs")) {
            event.setCancelled(true);
            player.performCommand("addsigns");
            return;
        }
        if(event.getCurrentItem().getType() != Material.NAME_TAG) {
            event.setCancelled(true);
        }
        Bukkit.getPluginManager().callEvent(new SetupInventoryClickEvent(arena, event.getCurrentItem(), player, clickType));


    }
}
