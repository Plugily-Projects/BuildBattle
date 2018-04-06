package me.tomthedeveloper.buildbattle.events;

import me.tomthedeveloper.buildbattle.GameAPI;
import me.tomthedeveloper.buildbattle.handlers.UserManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

/**
 * Created by Tom on 2/08/2014.
 */
public class onDoubleJump implements Listener {

    private GameAPI plugin;

    public onDoubleJump(GameAPI plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onFly(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if(plugin.getGameInstanceManager().getGameInstance(player) == null) return;
        if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.ADVENTURE || player.getGameMode() == GameMode.SPECTATOR) {
            event.getPlayer().setAllowFlight(true);
            return;
        }
        if(UserManager.getUser(player.getUniqueId()).isSpectator()) return;
        if(UserManager.getUser(player.getUniqueId()).isFakeDead()) return;
        if(!UserManager.getUser(player.getUniqueId()).getAllowDoubleJump()) return;

        event.setCancelled(true);
        if(UserManager.getUser(event.getPlayer().getUniqueId()).hasDoubleJumped()) return;

        player.setAllowFlight(false);
        player.setFlying(false);
        UserManager.getUser(event.getPlayer().getUniqueId()).doubleJumped();
        player.setVelocity(player.getLocation().getDirection().multiply(0.7).setY(1));

    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(plugin.is1_8_R3()) {
            if(event.getPlayer().getGameMode() == GameMode.CREATIVE || event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
                event.getPlayer().setAllowFlight(true);
                return;
            }
        } else {
            if(event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                event.getPlayer().setAllowFlight(true);
                return;
            }
        }
        if(UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator()) return;
        if(event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN.DOWN).getType() != Material.AIR) return;
        if(event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if(!UserManager.getUser(event.getPlayer().getUniqueId()).getAllowDoubleJump()) {
            event.getPlayer().setAllowFlight(false);
            return;
        }


        event.getPlayer().setAllowFlight(true);


    }

    @EventHandler
    public void onJumpDelete(PlayerMoveEvent event) {
        if(event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN.DOWN).getType() == Material.AIR) return;
        UserManager.getUser(event.getPlayer().getUniqueId()).reNewDoubleJump();

    }


}
