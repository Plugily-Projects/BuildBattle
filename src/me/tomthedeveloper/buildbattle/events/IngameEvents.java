package me.tomthedeveloper.buildbattle.events;

import me.tomthedeveloper.buildbattle.BuildPlot;
import me.tomthedeveloper.buildbattle.ConfigPreferences;
import me.tomthedeveloper.buildbattle.GameAPI;
import me.tomthedeveloper.buildbattle.Main;
import me.tomthedeveloper.buildbattle.User;
import me.tomthedeveloper.buildbattle.VoteItems;
import me.tomthedeveloper.buildbattle.arena.Arena;
import me.tomthedeveloper.buildbattle.arena.ArenaState;
import me.tomthedeveloper.buildbattle.entities.BuildBattleEntity;
import me.tomthedeveloper.buildbattle.handlers.ChatManager;
import me.tomthedeveloper.buildbattle.handlers.UserManager;
import me.tomthedeveloper.buildbattle.items.SpecialItemManager;
import me.tomthedeveloper.buildbattle.particles.ParticleMenu;
import me.tomthedeveloper.buildbattle.particles.ParticleRemoveMenu;
import me.tomthedeveloper.buildbattle.playerheads.PlayerHeadsMenu;
import me.tomthedeveloper.buildbattle.utils.IngameMenu;
import org.bukkit.ChatColor;
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
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * Created by Tom on 17/08/2015.
 */
public class IngameEvents implements Listener {

    private Main plugin;
    private GameAPI gameAPI;

    public IngameEvents(Main main) {
        this.plugin = main;
        this.gameAPI = plugin.getGameAPI();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if((plugin.isBungeeActivated() && gameAPI.getGameInstanceManager().getArenas().get(0).getGameState() == ArenaState.INGAME) || (plugin.isBungeeActivated() && gameAPI.getGameInstanceManager().getArenas().get(0).getGameState() == ArenaState.ENDING) || (plugin.isBungeeActivated() && gameAPI.getGameInstanceManager().getArenas().get(0).getGameState() == ArenaState.RESTARTING))
            event.getPlayer().kickPlayer(ChatManager.getSingleMessage("Kicked-Game-Already-Started", ChatManager.HIGHLIGHTED + "Kicked! Game has already started!"));
    }

    @EventHandler
    public void onPreJoin(AsyncPlayerPreLoginEvent event) {
        if((plugin.isBungeeActivated() && gameAPI.getGameInstanceManager().getArenas().get(0).getGameState() == ArenaState.INGAME) || (plugin.isBungeeActivated() && gameAPI.getGameInstanceManager().getArenas().get(0).getGameState() == ArenaState.ENDING) || (plugin.isBungeeActivated() && gameAPI.getGameInstanceManager().getArenas().get(0).getGameState() == ArenaState.RESTARTING)) {
            event.setKickMessage(ChatManager.getSingleMessage("Kicked-Game-Already-Started", ChatManager.HIGHLIGHTED + "Kicked! Game has already started!"));
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        }
    }

    @EventHandler
    public void onVote(PlayerInteractEvent event) {
        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) return;
        if(event.getItem() == null) return;
        Arena arena = gameAPI.getGameInstanceManager().getArena(event.getPlayer());
        if(arena == null) return;
        if(arena.getGameState() != ArenaState.INGAME) return;

        if(!event.getItem().hasItemMeta()) return;
        if(!event.getItem().getItemMeta().hasDisplayName()) return;
        if(!arena.isVoting()) return;
        if(arena.getVotingPlot().getOwner() == event.getPlayer().getUniqueId()) {
            event.getPlayer().sendMessage(ChatManager.getSingleMessage("Cant-Vote-On-Own-Plot", ChatColor.RED + "U can't vote on your own plot!!"));
            event.setCancelled(true);
            return;
        }
        UserManager.getUser(event.getPlayer().getUniqueId()).setInt("points", VoteItems.getPoints(event.getItem()));
        event.getPlayer().sendMessage(ChatManager.getSingleMessage("Voted", ChatColor.GREEN + "Voted succesfully!"));
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLeave(PlayerInteractEvent event) {
        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) return;
        Arena arena = gameAPI.getGameInstanceManager().getArena(event.getPlayer());
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
        Arena arena = gameAPI.getGameInstanceManager().getArena(event.getPlayer());
        if(arena == null) return;
        if(arena.getGameState() != ArenaState.INGAME) return;
        ItemStack itemStack = event.getItem();
        if(!itemStack.hasItemMeta()) return;
        if(!itemStack.getItemMeta().hasDisplayName()) return;
        if(arena.isVoting()) return;
        if(!IngameMenu.getMenuItem().getItemMeta().getDisplayName().equalsIgnoreCase(itemStack.getItemMeta().getDisplayName())) return;
        IngameMenu.openMenu(event.getPlayer(), arena.getPlotManager().getPlot(event.getPlayer()));
    }

    @EventHandler
    public void onPistonExtendEvent(BlockPistonExtendEvent event) {
        for(Arena arena : gameAPI.getGameInstanceManager().getArenas()) {
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
        if(gameAPI.getGameInstanceManager().getArena(player) == null) return;
        event.setCancelled(true);
        player.setFoodLevel(20);
    }

    @EventHandler
    public void onWaterFlowEvent(BlockFromToEvent event) {
        for(Arena arena : gameAPI.getGameInstanceManager().getArenas()) {
            for(BuildPlot buildPlot : arena.getPlotManager().getPlots()) {
                if(!buildPlot.isInPlot(event.getToBlock().getLocation()) && buildPlot.isInPlot(event.getBlock().getLocation())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onTntExplode(EntityExplodeEvent event) {
        for(Arena arena : gameAPI.getGameInstanceManager().getArenas()) {
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
    public void cancelTNT(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Arena arena = gameAPI.getGameInstanceManager().getArena(player);
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
        Arena arena = gameAPI.getGameInstanceManager().getArena(player);
        if(arena == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onGetDamaged(EntityDamageByEntityEvent event) {
        if(event.getDamager().getType() != EntityType.PLAYER) return;
        Player player = (Player) event.getDamager();
        Arena arena = gameAPI.getGameInstanceManager().getArena(player);
        if(arena == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(event.getEntity().getType() != EntityType.PLAYER) return;
        Player player = (Player) event.getEntity();
        Arena arena = gameAPI.getGameInstanceManager().getArena(player);
        if(arena == null) return;
        event.setCancelled(true);
    }


    @EventHandler
    public void onTreeGrow(StructureGrowEvent event) {
        Arena arena = gameAPI.getGameInstanceManager().getArena(event.getPlayer());
        if(arena == null) return;
        BuildPlot buildPlot = arena.getPlotManager().getPlot(event.getPlayer());
        if(buildPlot == null) return;
        for(BlockState blockState : event.getBlocks()) {
            if(!buildPlot.isInPlot(blockState.getLocation())) blockState.setType(Material.AIR);
        }

    }

    @EventHandler
    public void onDispense(BlockDispenseEvent event) {
        for(Arena arena : gameAPI.getGameInstanceManager().getArenas()) {
            for(BuildPlot buildPlot : arena.getPlotManager().getPlots()) {
                if(!buildPlot.isInPlotRange(event.getBlock().getLocation(), -1) && buildPlot.isInPlotRange(event.getBlock().getLocation(), 5)) {
                    event.setCancelled(true);
                }
            }

        }
    }

    @EventHandler
    public void onFloorChange(InventoryClickEvent event) {
        if(event.getCurrentItem() == null) return;
        if(!event.getCurrentItem().hasItemMeta()) return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName()) return;
        ItemStack currentItem = event.getCurrentItem();
        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        Player player = (Player) event.getWhoClicked();
        // if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatManager.getSingleMessage("Ingame-Menu-Name", "Option Menu")))
        //   event.setCancelled(true);


        Arena arena = gameAPI.getGameInstanceManager().getArena((Player) event.getWhoClicked());
        if(arena == null) return;

        if(arena.getGameState() != ArenaState.INGAME) return;
        if(displayName.equalsIgnoreCase(ChatManager.getSingleMessage("Particle-Option-Name", ChatColor.GREEN + "Particles"))) {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
            ParticleMenu.openMenu(player, arena.getPlotManager().getPlot((Player) event.getWhoClicked()));
            return;
        }
        String inventoryName = event.getInventory().getName();
        if(inventoryName.equalsIgnoreCase(ChatManager.getSingleMessage("Particle-Remove-Menu-Name", "Remove Particles"))) {
            event.setCancelled(true);
            ParticleRemoveMenu.onClick(event.getInventory(), event.getCurrentItem(), arena.getPlotManager().getPlot(player));

            return;
        }
        if(inventoryName.equalsIgnoreCase(ChatManager.getSingleMessage("Player-Head-Main-Inventory-Name", "Player Head Menu"))) {
            event.setCancelled(true);
            PlayerHeadsMenu.onClickInMainMenu(player, event.getCurrentItem());

            return;
        }
        if(PlayerHeadsMenu.getMenuNames().contains(event.getInventory().getName())) {
            event.setCancelled(true);
            PlayerHeadsMenu.onClickInDeeperMenu(player, event.getCurrentItem(), event.getInventory().getName());
            return;
        }
        if(displayName.equalsIgnoreCase(ChatManager.getSingleMessage("Heads-Option-Name", ChatColor.GREEN + "Particles"))) {

            event.setCancelled(true);
            PlayerHeadsMenu.openMenu(player);
        }
        if(inventoryName.equalsIgnoreCase(ChatManager.getSingleMessage("Particle-Menu-Name", "Particle Menu"))) {

            if(displayName.contains(ChatManager.getSingleMessage("Remove-Particle-Item-Name", ChatColor.RED + "Remove Particles"))) {
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
                ParticleRemoveMenu.openMenu(player, arena.getPlotManager().getPlot((Player) event.getWhoClicked()));
                return;
            }
            ParticleMenu.onClick(player, event.getCurrentItem(), arena.getPlotManager().getPlot((Player) event.getWhoClicked()));

            event.setCancelled(true);
        }
        if(event.getCursor() == null) {
            event.setCancelled(true);
            return;
        }
        if(!((event.getCursor().getType().isBlock() && event.getCursor().getType().isSolid()) || event.getCursor().getType() == Material.WATER_BUCKET || event.getCursor().getType() == Material.LAVA_BUCKET)) {
            event.setCancelled(true);
            return;
        }

        if(event.getCursor().getType() == null || event.getCursor().getType() == Material.SAPLING || event.getCursor().getType() == Material.TRAP_DOOR || event.getCursor().getType() == Material.WOOD_DOOR || event.getCursor().getType() == Material.IRON_TRAPDOOR || event.getCursor().getType() == Material.WOODEN_DOOR || event.getCursor().getType() == Material.ACACIA_DOOR || event.getCursor().getType() == Material.BIRCH_DOOR || event.getCursor().getType() == Material.WOOD_DOOR || event.getCursor().getType() == Material.JUNGLE_DOOR || event.getCursor().getType() == Material.SPRUCE_DOOR || event.getCursor().getType() == Material.IRON_DOOR || event.getCursor().getType() == Material.CHEST || event.getCursor().getType() == Material.TRAPPED_CHEST || event.getCursor().getType() == Material.FENCE_GATE || event.getCursor().getType() == Material.BED || event.getCursor().getType() == Material.LADDER || event.getCursor().getType() == Material.JUNGLE_FENCE_GATE || event.getCursor().getType() == Material.JUNGLE_DOOR_ITEM || event.getCursor().getType() == Material.SIGN || event.getCursor().getType() == Material.SIGN_POST || event.getCursor().getType() == Material.WALL_SIGN || event.getCursor().getType() == Material.CACTUS || event.getCursor().getType() == Material.ENDER_CHEST || event.getCursor().getType() == Material.PISTON_BASE

                || event.getCursor().getType() == Material.TNT || event.getCursor().getType() == Material.AIR) {
            event.setCancelled(true);
            return;
        }
        if(displayName.equalsIgnoreCase(ChatManager.getSingleMessage("Floor-Option-Name", ChatColor.GREEN + "Floor Material"))) {
            arena.getPlotManager().getPlot(player).changeFloor(event.getCursor().getType(), event.getCursor().getData().getData());
            player.sendMessage(ChatManager.getSingleMessage("Floor-Changed", ChatColor.GREEN + "Floor changed!"));
            event.getCursor().setAmount(0);
            event.getCursor().setType(Material.AIR);
            event.getCurrentItem().setType(Material.AIR);
            player.closeInventory();
            for(Entity entity : player.getNearbyEntities(5, 5, 5)) {
                if(entity.getType() == EntityType.DROPPED_ITEM) {
                    entity.remove();
                }
            }
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onChatIngame(AsyncPlayerChatEvent event) {
        if(gameAPI.getGameInstanceManager().getArena(event.getPlayer()) == null) {
            for(Arena arena : gameAPI.getGameInstanceManager().getArenas()) {
                for(Player player : arena.getPlayers()) {
                    event.getRecipients().remove(player);
                }
            }
            return;
        }

        Arena arena = gameAPI.getGameInstanceManager().getArena(event.getPlayer());
        event.getRecipients().clear();
        event.getRecipients().addAll(new ArrayList<>(arena.getPlayers()));


    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void disableCommands(PlayerCommandPreprocessEvent event) {
        if(gameAPI.getGameInstanceManager().getArena(event.getPlayer()) == null) return;
        Boolean whitelisted = false;
        for(String string : ConfigPreferences.getWhitelistedCommands()) {
            if(event.getMessage().contains(string)) whitelisted = true;
        }
        if(event.getMessage().contains("leave") || event.getMessage().contains("stats") || whitelisted) {
            return;
        }
        if(event.getPlayer().isOp() || event.getPlayer().hasPermission("minigames.edit")) return;
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatManager.getSingleMessage("Only-Command-Ingame-Is-Leave", ChatColor.RED + "You have to leave the game first to perform commands. The only command that works is /leave!"));


    }

    @EventHandler
    public void playerEmtpyBucket(PlayerBucketEmptyEvent event) {
        Arena arena = gameAPI.getGameInstanceManager().getArena(event.getPlayer());
        if(arena == null) return;
        BuildPlot buildPlot = arena.getPlotManager().getPlot(event.getPlayer());
        if(buildPlot == null) return;

        if(!buildPlot.isInPlot(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation())) event.setCancelled(true);

    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        for(Arena arena : gameAPI.getGameInstanceManager().getArenas()) {
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
            for(Arena arena : gameAPI.getGameInstanceManager().getArenas()) {
                for(BuildPlot buildplot : arena.getPlotManager().getPlots()) {
                    if(buildplot.isInPlotRange(event.getEntity().getLocation(), 10)) event.setCancelled(true);
                }
            }
        } else {
            for(Arena arena : gameAPI.getGameInstanceManager().getArenas()) {
                for(BuildPlot buildplot : arena.getPlotManager().getPlots()) {
                    if(buildplot.isInPlotRange(event.getEntity().getLocation(), 1)) {
                        if(buildplot.getEntities() >= ConfigPreferences.getMaxMobs()) {
                            plugin.getServer().getPlayer(buildplot.getOwner()).sendMessage(ChatManager.getSingleMessage("Max-Entities-Reached", ChatColor.RED + "Max entities reached!"));
                            event.setCancelled(true);
                            return;
                        } else {
                            buildplot.addEntity();
                            new BuildBattleEntity(event.getEntity()).setMoveable(false);
                        }

                    }
                }
            }
        }
    }

    @EventHandler
    public void LeaveDecay(LeavesDecayEvent event) {
        for(Arena arena : gameAPI.getGameInstanceManager().getArenas()) {
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
        if(drop.getItemMeta().getDisplayName().equals(ChatManager.getSingleMessage("Options-Menu-Item", ChatColor.GREEN + "Options")) || VoteItems.getPoints(drop) != 0)
            event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        Arena arena = gameAPI.getGameInstanceManager().getArena(event.getPlayer());
        if(arena == null) return;
        if(arena.getGameState() != ArenaState.INGAME) {
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
        Arena arena = gameAPI.getGameInstanceManager().getArena(event.getPlayer());
        if(arena == null) return;
        if(arena.getGameState() != ArenaState.INGAME) {
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
        Arena arena = gameAPI.getGameInstanceManager().getArena((Player) event.getWhoClicked());
        if(arena == null) return;
        if(arena.getGameState() != ArenaState.INGAME) {
            event.setCancelled(true);
            return;
        }
        if(!arena.isVoting()) {
            return;
        }
        event.setCancelled(true);
    }

}
