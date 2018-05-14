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

package pl.plajer.buildbattle3.events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;
import pl.plajer.buildbattle3.BuildPlot;
import pl.plajer.buildbattle3.ConfigPreferences;
import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.user.User;
import pl.plajer.buildbattle3.VoteItems;
import pl.plajer.buildbattle3.arena.Arena;
import pl.plajer.buildbattle3.arena.ArenaRegistry;
import pl.plajer.buildbattle3.arena.ArenaState;
import pl.plajer.buildbattle3.entities.BuildBattleEntity;
import pl.plajer.buildbattle3.handlers.ChatManager;
import pl.plajer.buildbattle3.user.UserManager;
import pl.plajer.buildbattle3.items.SpecialItemManager;
import pl.plajer.buildbattle3.particles.ParticleMenu;
import pl.plajer.buildbattle3.particles.ParticleRemoveMenu;
import pl.plajer.buildbattle3.playerheads.PlayerHeadsMenu;
import pl.plajer.buildbattle3.utils.IngameMenu;

import java.util.ArrayList;

/**
 * Created by Tom on 17/08/2015.
 */
public class GameEvents implements Listener {

    private Main plugin;

    public GameEvents(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onVote(PlayerInteractEvent event) {
        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) return;
        if(event.getItem() == null) return;
        Arena arena = ArenaRegistry.getArena(event.getPlayer());
        if(arena == null) return;
        if(arena.getGameState() != ArenaState.IN_GAME) return;

        if(!event.getItem().hasItemMeta()) return;
        if(!event.getItem().getItemMeta().hasDisplayName()) return;
        if(!arena.isVoting()) return;
        if(arena.getVotingPlot().getOwner() == event.getPlayer().getUniqueId()) {
            event.getPlayer().sendMessage(ChatManager.colorMessage("In-Game.Messages.Voting-Messages.Cant-Vote-Own-Plot"));
            event.setCancelled(true);
            return;
        }
        UserManager.getUser(event.getPlayer().getUniqueId()).setInt("points", VoteItems.getPoints(event.getItem()));
        event.getPlayer().sendMessage(ChatManager.colorMessage("In-Game.Messages.Voting-Messages.Vote-Successful"));
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLeave(PlayerInteractEvent event) {
        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) return;
        Arena arena = ArenaRegistry.getArena(event.getPlayer());
        if(arena == null) return;
        ItemStack itemStack = event.getPlayer().getItemInHand();
        if(itemStack == null) return;
        if(itemStack.getItemMeta() == null) return;
        if(itemStack.getItemMeta().getDisplayName() == null) return;
        String key = SpecialItemManager.getRelatedSpecialItem(itemStack);
        if(key == null) return;
        if(SpecialItemManager.getRelatedSpecialItem(itemStack).equalsIgnoreCase("Leave")) {
            event.setCancelled(true);
            if(plugin.isBungeeActivated()) {
                plugin.getBungeeManager().connectToHub(event.getPlayer());
            } else {
                arena.leaveAttempt(event.getPlayer());
            }
        }
    }


    @EventHandler
    public void onOpenOptionMenu(PlayerInteractEvent event) {
        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) return;
        if(event.getItem() == null) return;
        Arena arena = ArenaRegistry.getArena(event.getPlayer());
        if(arena == null) return;
        if(arena.getGameState() != ArenaState.IN_GAME) return;
        ItemStack itemStack = event.getItem();
        if(!itemStack.hasItemMeta()) return;
        if(!itemStack.getItemMeta().hasDisplayName()) return;
        if(arena.isVoting()) return;
        if(!IngameMenu.getMenuItem().getItemMeta().getDisplayName().equalsIgnoreCase(itemStack.getItemMeta().getDisplayName())) return;
        IngameMenu.openMenu(event.getPlayer(), arena.getPlotManager().getPlot(event.getPlayer()));
    }

    @EventHandler
    public void onPistonExtendEvent(BlockPistonExtendEvent event) {
        for(Arena arena : ArenaRegistry.getArenas()) {
            for(BuildPlot buildPlot : arena.getPlotManager().getPlots()) {
                for(Block block : event.getBlocks()) {
                    if(!buildPlot.isInPlotRange(block.getLocation(), -1) && buildPlot.isInPlot(event.getBlock().getLocation())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if(!(event.getEntity().getType() == EntityType.PLAYER)) return;
        Player player = (Player) event.getEntity();
        if(ArenaRegistry.getArena(player) == null) return;
        event.setCancelled(true);
        player.setFoodLevel(20);
    }

    @EventHandler
    public void onWaterFlowEvent(BlockFromToEvent event) {
        for(Arena arena : ArenaRegistry.getArenas()) {
            for(BuildPlot buildPlot : arena.getPlotManager().getPlots()) {
                if(!buildPlot.isInPlot(event.getToBlock().getLocation()) && buildPlot.isInPlot(event.getBlock().getLocation())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onTNTExplode(EntityExplodeEvent event) {
        for(Arena arena : ArenaRegistry.getArenas()) {
            for(BuildPlot buildPlot : arena.getPlotManager().getPlots()) {
                if(buildPlot.isInPlotRange(event.getEntity().getLocation(), 0)) {
                    event.blockList().clear();
                    event.setCancelled(true);
                } else if(buildPlot.isInPlotRange(event.getEntity().getLocation(), 5)) {
                    event.getEntity().getLocation().getBlock().setType(Material.TNT);
                    event.blockList().clear();
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onTNTInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Arena arena = ArenaRegistry.getArena(player);
        if(arena == null) return;
        if(player.getItemInHand() == null) return;
        if(player.getItemInHand().getType() != Material.FLINT_AND_STEEL) {
            return;
        }
        if(event.getClickedBlock() == null) return;
        if(event.getClickedBlock().getType() == Material.TNT) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if(event.getEntity().getType() != EntityType.PLAYER) return;
        Player player = (Player) event.getEntity();
        Arena arena = ArenaRegistry.getArena(player);
        if(arena == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onGetDamaged(EntityDamageByEntityEvent event) {
        if(event.getDamager().getType() != EntityType.PLAYER) return;
        Player player = (Player) event.getDamager();
        Arena arena = ArenaRegistry.getArena(player);
        if(arena == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(event.getEntity().getType() != EntityType.PLAYER) return;
        Player player = (Player) event.getEntity();
        Arena arena = ArenaRegistry.getArena(player);
        if(arena == null) return;
        event.setCancelled(true);
    }


    @EventHandler
    public void onTreeGrow(StructureGrowEvent event) {
        Arena arena = ArenaRegistry.getArena(event.getPlayer());
        if(arena == null) return;
        BuildPlot buildPlot = arena.getPlotManager().getPlot(event.getPlayer());
        if(buildPlot == null) return;
        for(BlockState blockState : event.getBlocks()) {
            if(!buildPlot.isInPlot(blockState.getLocation())) blockState.setType(Material.AIR);
        }

    }

    @EventHandler
    public void onDispense(BlockDispenseEvent event) {
        for(Arena arena : ArenaRegistry.getArenas()) {
            for(BuildPlot buildPlot : arena.getPlotManager().getPlots()) {
                if(!buildPlot.isInPlotRange(event.getBlock().getLocation(), -1) && buildPlot.isInPlotRange(event.getBlock().getLocation(), 5)) {
                    event.setCancelled(true);
                }
            }

        }
    }

    @EventHandler
    public void onOptionMenuClick(InventoryClickEvent e) {
        if(e.getInventory() == null) return;
        if(e.getCurrentItem() == null) return;
        if(!e.getCurrentItem().hasItemMeta()) return;
        if(!e.getCurrentItem().getItemMeta().hasDisplayName()) return;
        String displayName = e.getCurrentItem().getItemMeta().getDisplayName();
        Player player = (Player) e.getWhoClicked();
        if(e.getInventory().getName().equals(ChatManager.colorMessage("Menus.Option-Menu.Inventory-Name"))) {
            e.setCancelled(true);
        }
        Arena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
        if(arena == null) return;

        if(arena.getGameState() != ArenaState.IN_GAME) return;
        if(displayName.equalsIgnoreCase(ChatManager.colorMessage("Menus.Option-Menu.Particle-Option"))) {
            e.getWhoClicked().closeInventory();
            ParticleMenu.openMenu(player);
            return;
        } else if(e.getInventory().getName().equalsIgnoreCase(ChatManager.colorMessage("Menus.Option-Menu.Particle-Remove"))) {
            ParticleRemoveMenu.onClick(e.getInventory(), e.getCurrentItem(), arena.getPlotManager().getPlot(player));
            return;
        } else if(e.getInventory().getName().equalsIgnoreCase(ChatManager.colorMessage("Menus.Option-Menu.Players-Heads-Inventory-Name"))) {
            PlayerHeadsMenu.onClickInMainMenu(player, e.getCurrentItem());
            return;
        } else if(PlayerHeadsMenu.getMenuNames().contains(e.getInventory().getName())) {
            PlayerHeadsMenu.onClickInDeeperMenu(player, e.getCurrentItem(), e.getInventory().getName());
            return;
        } else if(displayName.equalsIgnoreCase(ChatManager.colorMessage("Menus.Option-Menu.Players-Heads-Option"))) {
            PlayerHeadsMenu.openMenu(player);
            return;
        } else if(e.getInventory().getName().equalsIgnoreCase(ChatManager.colorMessage("Menus.Option-Menu.Particle-Inventory-Name"))) {
            if(displayName.contains(ChatManager.colorMessage("Menus.Option-Menu.Particle-Remove"))) {
                e.getWhoClicked().closeInventory();
                ParticleRemoveMenu.openMenu(player, arena.getPlotManager().getPlot((Player) e.getWhoClicked()));
                return;
            }
            ParticleMenu.onClick(player, e.getCurrentItem(), arena.getPlotManager().getPlot((Player) e.getWhoClicked()));
            return;
        } else if(e.getInventory().getName().equalsIgnoreCase(ChatManager.colorMessage("Menus.Option-Menu.Banners-Option-Inventory-Name"))) {
            e.getWhoClicked().closeInventory();
            e.getWhoClicked().sendMessage("Soon :)");
            return;
        }
        if(e.getCursor() == null) {
            return;
        }
        if(!((e.getCursor().getType().isBlock() && e.getCursor().getType().isSolid()) || e.getCursor().getType() == Material.WATER_BUCKET || e.getCursor().getType() == Material.LAVA_BUCKET)) {
            return;
        }

        if(e.getCursor().getType() == null || e.getCursor().getType() == Material.SAPLING || e.getCursor().getType() == Material.TRAP_DOOR || e.getCursor().getType() == Material.WOOD_DOOR || e.getCursor().getType() == Material.IRON_TRAPDOOR || e.getCursor().getType() == Material.WOODEN_DOOR || e.getCursor().getType() == Material.ACACIA_DOOR || e.getCursor().getType() == Material.BIRCH_DOOR || e.getCursor().getType() == Material.WOOD_DOOR || e.getCursor().getType() == Material.JUNGLE_DOOR || e.getCursor().getType() == Material.SPRUCE_DOOR || e.getCursor().getType() == Material.IRON_DOOR || e.getCursor().getType() == Material.CHEST || e.getCursor().getType() == Material.TRAPPED_CHEST || e.getCursor().getType() == Material.FENCE_GATE || e.getCursor().getType() == Material.BED || e.getCursor().getType() == Material.LADDER || e.getCursor().getType() == Material.JUNGLE_FENCE_GATE || e.getCursor().getType() == Material.JUNGLE_DOOR_ITEM || e.getCursor().getType() == Material.SIGN || e.getCursor().getType() == Material.SIGN_POST || e.getCursor().getType() == Material.WALL_SIGN || e.getCursor().getType() == Material.CACTUS || e.getCursor().getType() == Material.ENDER_CHEST || e.getCursor().getType() == Material.PISTON_BASE

                || e.getCursor().getType() == Material.TNT || e.getCursor().getType() == Material.AIR) {
            e.setCancelled(true);
            return;
        }
        if(displayName.equalsIgnoreCase(ChatManager.colorMessage("Menus.Option-Menu.Floor-Option"))) {
            arena.getPlotManager().getPlot(player).changeFloor(e.getCursor().getType(), e.getCursor().getData().getData());
            player.sendMessage(ChatManager.colorMessage("Menus.Option-Menu.Floor-Changed"));
            //todo wtf
            e.getCursor().setAmount(0);
            e.getCursor().setType(Material.AIR);
            e.getCurrentItem().setType(Material.AIR);
            player.closeInventory();
            for(Entity entity : player.getNearbyEntities(5, 5, 5)) {
                if(entity.getType() == EntityType.DROPPED_ITEM) {
                    entity.remove();
                }
            }
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void onChatIngame(AsyncPlayerChatEvent event) {
        if(ArenaRegistry.getArena(event.getPlayer()) == null) {
            for(Arena arena : ArenaRegistry.getArenas()) {
                for(Player player : arena.getPlayers()) {
                    event.getRecipients().remove(player);
                }
            }
            return;
        }

        Arena arena = ArenaRegistry.getArena(event.getPlayer());
        event.getRecipients().clear();
        event.getRecipients().addAll(new ArrayList<>(arena.getPlayers()));


    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void disableCommands(PlayerCommandPreprocessEvent event) {
        if(ArenaRegistry.getArena(event.getPlayer()) == null) return;
        for(String string : ConfigPreferences.getWhitelistedCommands()) {
            if(event.getMessage().contains(string)) return;
        }
        //todo more
        if(event.getMessage().contains("leave") || event.getMessage().contains("stats")) {
            return;
        }
        //todo perm
        if(event.getPlayer().isOp() || event.getPlayer().hasPermission("minigames.edit")) return;
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatManager.colorMessage("In-Game.Only-Command-Ingame-Is-Leave"));
    }

    @EventHandler
    public void playerEmtpyBucket(PlayerBucketEmptyEvent event) {
        Arena arena = ArenaRegistry.getArena(event.getPlayer());
        if(arena == null) return;
        BuildPlot buildPlot = arena.getPlotManager().getPlot(event.getPlayer());
        if(buildPlot == null) return;
        if(!buildPlot.isInPlot(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation())) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        for(Arena arena : ArenaRegistry.getArenas()) {
            if(arena.getPlotManager().getPlots().size() != 0 && arena.getPlotManager().getPlots().get(0) != null) {
                if(arena.getPlotManager().getPlots().get(0).getCenter().getWorld().getName().equalsIgnoreCase(event.getBlock().getWorld().getName())) {
                    if(event.getSource().getType() == Material.FIRE) event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onWitherBoss(CreatureSpawnEvent event) {
        if(plugin.isBungeeActivated() && event.getEntity().getType() == EntityType.WITHER) {
            event.setCancelled(true);
            return;
        }
        if(event.getEntity().getType() == EntityType.WITHER || ConfigPreferences.isMobSpawningDisabled()) {
            for(Arena arena : ArenaRegistry.getArenas()) {
                for(BuildPlot buildplot : arena.getPlotManager().getPlots()) {
                    if(buildplot.isInPlotRange(event.getEntity().getLocation(), 10)) event.setCancelled(true);
                }
            }
        } else {
            for(Arena arena : ArenaRegistry.getArenas()) {
                for(BuildPlot buildplot : arena.getPlotManager().getPlots()) {
                    if(buildplot.isInPlotRange(event.getEntity().getLocation(), 1)) {
                        if(buildplot.getEntities() >= ConfigPreferences.getMaxMobs()) {
                            plugin.getServer().getPlayer(buildplot.getOwner()).sendMessage(ChatManager.colorMessage("In-Game.Max-Entities-Limit-Reached"));
                            event.setCancelled(true);
                            return;
                        } else {
                            buildplot.addEntity();
                            new BuildBattleEntity(event.getEntity()).toggleMoveable();
                        }

                    }
                }
            }
        }
    }

    @EventHandler
    public void LeaveDecay(LeavesDecayEvent event) {
        for(Arena arena : ArenaRegistry.getArenas()) {
            for(BuildPlot buildPlot : arena.getPlotManager().getPlots()) {
                if(buildPlot.isInPlotRange(event.getBlock().getLocation(), 5)) {
                    event.setCancelled(true);
                }
            }
        }
    }


    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if(event.getItemDrop().getItemStack() == null) return;
        ItemStack drop = event.getItemDrop().getItemStack();
        if(!drop.hasItemMeta()) return;
        if(!drop.getItemMeta().hasDisplayName()) return;
        if(drop.getItemMeta().getDisplayName().equals(ChatManager.colorMessage("Menus.Option-Menu.Inventory-Name")) || VoteItems.getPoints(drop) != 0)
            event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        Arena arena = ArenaRegistry.getArena(event.getPlayer());
        if(arena == null) return;
        if(arena.getGameState() != ArenaState.IN_GAME) {
            event.setCancelled(true);
            return;
        }
        if(arena.isVoting()) {
            event.setCancelled(true);
            return;
        }
        if(arena.getBlacklist().contains(event.getBlock().getTypeId())) {
            event.setCancelled(true);
            return;
        }
        User user = UserManager.getUser(event.getPlayer().getUniqueId());
        BuildPlot buildPlot = (BuildPlot) user.getObject("plot");
        if(buildPlot == null) {
            event.setCancelled(true);
            return;
        }
        if(buildPlot.isInPlot(event.getBlock().getLocation())) {
            UserManager.getUser(event.getPlayer().getUniqueId()).addInt("blocksbroken", 1);
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlace(BlockPlaceEvent event) {
        Arena arena = ArenaRegistry.getArena(event.getPlayer());
        if(arena == null) return;
        if(arena.getGameState() != ArenaState.IN_GAME) {
            event.setCancelled(true);
            return;
        }
        if(arena.getBlacklist().contains(event.getBlock().getTypeId())) {
            event.setCancelled(true);
            return;
        }
        if(arena.isVoting()) {
            event.setCancelled(true);
            return;
        }
        User user = UserManager.getUser(event.getPlayer().getUniqueId());
        BuildPlot buildPlot = (BuildPlot) user.getObject("plot");
        if(buildPlot == null) {
            event.setCancelled(true);
            return;
        }
        if(buildPlot.isInPlot(event.getBlock().getLocation())) {
            UserManager.getUser(event.getPlayer().getUniqueId()).addInt("blocksplaced", 1);
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Arena arena = ArenaRegistry.getArena((Player) event.getWhoClicked());
        if(arena == null) return;
        if(arena.getGameState() != ArenaState.IN_GAME) {
            event.setCancelled(true);
            return;
        }
        if(!arena.isVoting()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if(ArenaRegistry.getArena(event.getPlayer()) == null) {
            for(Player player : event.getRecipients()) {
                if(ArenaRegistry.getArena(event.getPlayer()) == null) return;
                event.getRecipients().remove(player);

            }
        }
        event.getRecipients().clear();
        event.getRecipients().addAll(ArenaRegistry.getArena(event.getPlayer()).getPlayers());
    }

}
