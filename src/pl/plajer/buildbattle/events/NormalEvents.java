package pl.plajer.buildbattle.events;

import pl.plajer.buildbattle.BuildPlot;
import pl.plajer.buildbattle.GameAPI;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.User;
import pl.plajer.buildbattle.handlers.UserManager;
import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.stats.MySQLDatabase;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 28/08/2015.
 */
public class NormalEvents implements Listener {

    private Main plugin;
    private GameAPI gameAPI;

    public NormalEvents(Main plugin) {
        this.plugin = plugin;
        gameAPI = plugin.getGameAPI();
    }

    @EventHandler
    public void onTntExplode(BlockExplodeEvent event) {
        for(Arena arena : plugin.getGameAPI().getGameInstanceManager().getArenas()) {
            for(BuildPlot buildPlot : arena.getPlotManager().getPlots()) {
                if(buildPlot.isInPlotRange(event.getBlock().getLocation(), 0)) {
                    event.blockList().clear();
                } else if(buildPlot.isInPlotRange(event.getBlock().getLocation(), 5)) {
                    event.getBlock().getLocation().getBlock().setType(Material.TNT);
                    event.blockList().clear();
                    event.setCancelled(true);
                }
            }
        }
    }


    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        if(plugin.isBungeeActivated()) gameAPI.getGameInstanceManager().getArenas().get(0).teleportToLobby(event.getPlayer());
        if(!plugin.isDatabaseActivated()) {
            List<String> temp = new ArrayList<>();
            temp.add("gamesplayed");
            temp.add("wins");
            temp.add("loses");
            temp.add("highestwin");
            temp.add("blocksplaced");
            temp.add("blocksbroken");
            temp.add("particles");
            for(String s : temp) {
                plugin.getFileStats().loadStat(event.getPlayer(), s);
            }
            return;
        }
        final String playername = event.getPlayer().getUniqueId().toString();
        final Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            MySQLDatabase database = plugin.getMySQLDatabase();
            ResultSet resultSet = database.executeQuery("SELECT UUID from buildbattlestats WHERE UUID='" + playername + "'");
            try {
                if(!resultSet.next()) {
                    database.insertPlayer(playername);
                    return;
                }
                int gamesplayed;
                int wins;
                int highestwin;
                int loses;
                int blocksPlaced;
                int blocksBroken;
                int particles;
                gamesplayed = database.getStat(player.getUniqueId().toString(), "gamesplayed");
                wins = database.getStat(player.getUniqueId().toString(), "wins");
                loses = database.getStat(player.getUniqueId().toString(), "loses");
                highestwin = database.getStat(player.getUniqueId().toString(), "highestwin");
                blocksPlaced = database.getStat(player.getUniqueId().toString(), "blocksplaced");
                blocksBroken = database.getStat(player.getUniqueId().toString(), "blocksbroken");
                particles = database.getStat(player.getUniqueId().toString(), "particles");
                User user1 = UserManager.getUser(player.getUniqueId());

                user1.setInt("gamesplayed", gamesplayed);
                user1.setInt("wins", wins);
                user1.setInt("highestwin", highestwin);
                user1.setInt("loses", loses);
                user1.setInt("blocksplaced", blocksPlaced);
                user1.setInt("blocksbroken", blocksBroken);
                user1.setInt("particles", particles);
            } catch(SQLException e1) {
                System.out.print("CONNECTION FAILED FOR PLAYER " + playername);
                //e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        });

    }
}
