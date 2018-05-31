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

package pl.plajer.buildbattle3.entities;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.arena.Arena;
import pl.plajer.buildbattle3.arena.ArenaRegistry;
import pl.plajer.buildbattle3.arena.ArenaState;
import pl.plajer.buildbattle3.handlers.ChatManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Tom on 1/02/2016.
 */
public class EntityMenuEvents implements Listener {

    private HashMap<UUID, BuildBattleEntity> links = new HashMap<>();

    public EntityMenuEvents(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onRightClickEntity(PlayerInteractEntityEvent event) {
        if(!JavaPlugin.getPlugin(Main.class).is1_8_R3()) if(event.getHand() == EquipmentSlot.OFF_HAND) return;
        Player player = event.getPlayer();
        Arena arena = ArenaRegistry.getArena(player);
        if(arena == null) return;
        event.setCancelled(true);
        if(arena.getArenaState() != ArenaState.IN_GAME || arena.isVoting()) return;
        EntityType type = event.getRightClicked().getType();
        if(type == EntityType.ITEM_FRAME || type == EntityType.ARMOR_STAND || type == EntityType.DROPPED_ITEM || type == EntityType.PRIMED_TNT
                || type == EntityType.FALLING_BLOCK || type == EntityType.COMPLEX_PART || type == EntityType.ENDER_CRYSTAL ||
                type == EntityType.LEASH_HITCH || type == EntityType.MINECART || type == EntityType.MINECART_CHEST || type == EntityType.MINECART_FURNACE ||
                type == EntityType.MINECART_COMMAND || type == EntityType.MINECART_HOPPER || type == EntityType.MINECART_MOB_SPAWNER || type == EntityType.MINECART_TNT
                || type == EntityType.PLAYER || type == EntityType.PAINTING || type == EntityType.WITHER_SKULL)
            return;
        if(event.getRightClicked().getCustomName().equalsIgnoreCase(ChatManager.colorMessage("In-Game.NPC.Floor-Change-NPC-Name"))) return;
        BuildBattleEntity buildBattleEntity = new BuildBattleEntity(((LivingEntity) event.getRightClicked()));
        player.openInventory(buildBattleEntity.getMenu());
        links.put(player.getUniqueId(), buildBattleEntity);

    }

    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent event) {
        if(event.getDamager().getType() != EntityType.PLAYER) return;
        Player player = (Player) event.getDamager();
        Arena arena = ArenaRegistry.getArena(player);
        if(arena == null) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        Arena arena = ArenaRegistry.getArena(player);
        if(arena == null) return;
        if(!event.getInventory().getTitle().equals(ChatManager.colorMessage("Menus.Entity-Menu-Name"))) return;
        if(event.getCurrentItem() == null) return;
        if(!event.getCurrentItem().hasItemMeta()) return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName()) return;
        String key = EntityItemManager.getRelatedEntityItemName(event.getCurrentItem());
        if(key.equalsIgnoreCase("Close")) {
            links.remove(player.getUniqueId());
            player.closeInventory();
            event.setCancelled(true);
        } else if(key.equalsIgnoreCase("Move-On") || key.equalsIgnoreCase("Move-Off")) {
            links.get(player.getUniqueId()).toggleMoveable();
            event.setCancelled(true);
            player.closeInventory();

        } else if(key.equalsIgnoreCase("Adult") || key.equalsIgnoreCase("Baby")) {
            links.get(player.getUniqueId()).switchAge();
            event.setCancelled(true);

        } else if(key.equalsIgnoreCase("Look-At-Me")) {
            links.get(player.getUniqueId()).setLook(player.getLocation());
            event.setCancelled(true);
            player.closeInventory();
        } else if(key.equals("Saddle-On") || key.equals("Saddle-Off")) {
            links.get(player.getUniqueId()).switchSaddle();
            event.setCancelled(true);
            player.closeInventory();
        } else if(key.equals("Despawn")) {
            links.get(player.getUniqueId()).remove();
            player.closeInventory();
            event.setCancelled(true);
            arena.getPlotManager().getPlot(player).removeEntity();

        } else if(key.equals("Profession-Villager-Selecting")) {
            Inventory inventory = Bukkit.createInventory(null, 9, ChatManager.colorMessage("Menus.Villager-Profession-Menu-Name"));
            Set<String> professions = new HashSet<>();
            professions.add("Blacksmith");
            professions.add("Librarian");
            professions.add("Farmer");
            professions.add("Butcher");
            professions.add("Priest");
            for(String string : professions) {
                EntityItem entityItem = EntityItemManager.getEntityItem("Profession." + string);
                inventory.setItem(entityItem.getSlot(), entityItem.getItemStack());
            }

            player.openInventory(inventory);
            event.setCancelled(true);
        }


    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVillagerProfessionChoose(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Arena arena = ArenaRegistry.getArena(player);
        if(arena == null) return;
        if(!event.getInventory().getTitle().equals(ChatManager.colorMessage("Menus.Villager-Profession-Menu-Name"))) return;
        if(event.getCurrentItem() == null) return;
        if(!event.getCurrentItem().hasItemMeta()) return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName()) return;
        String key = EntityItemManager.getRelatedEntityItemName(event.getCurrentItem());
        Villager villager = (Villager) links.get(player.getUniqueId()).getEntity();
        if(key.equalsIgnoreCase("Profession.Butcher")) {
            villager.setProfession(Villager.Profession.BUTCHER);
        } else if(key.equals("Profession.Blacksmith")) {
            villager.setProfession(Villager.Profession.BLACKSMITH);
        } else if(key.equals("Profession.Farmer")) {
            villager.setProfession(Villager.Profession.FARMER);
        } else if(key.equals("Profession.Priest")) {
            villager.setProfession(Villager.Profession.PRIEST);
        } else if(key.equals("Profession.Librarian")) {
            villager.setProfession(Villager.Profession.LIBRARIAN);
        }
        event.setCancelled(true);
        player.closeInventory();
    }


}
