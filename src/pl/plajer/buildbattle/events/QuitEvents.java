package pl.plajer.buildbattle.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.User;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.handlers.UserManager;
import pl.plajer.buildbattle.stats.BuildBattleStats;

/**
 * @author Plajer
 * <p>
 * Created at 29.04.2018
 */
public class QuitEvents implements Listener {

    private Main plugin;

    public QuitEvents(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if(ArenaRegistry.getArena(event.getPlayer()) == null) return;
        if(!plugin.isBungeeActivated())
            ArenaRegistry.getArena(event.getPlayer()).leaveAttempt(event.getPlayer());
    }

    @EventHandler
    public void onQuitSaveStats(PlayerQuitEvent event) {
        if(ArenaRegistry.getArena(event.getPlayer()) != null) {
            ArenaRegistry.getArena(event.getPlayer()).leaveAttempt(event.getPlayer());
        }
        final User user = UserManager.getUser(event.getPlayer().getUniqueId());
        final Player player = event.getPlayer();

        if(plugin.isDatabaseActivated()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                for(final String s : BuildBattleStats.STATISTICS) {
                    int i;
                    try {
                        i = plugin.getMySQLDatabase().getStat(player.getUniqueId().toString(), s);
                    } catch(NullPointerException npe) {
                        i = 0;
                        System.out.print("COULDN'T GET STATS FROM PLAYER: " + player.getName());
                    }

                    if(i > user.getInt(s)) {
                        plugin.getMySQLDatabase().setStat(player.getUniqueId().toString(), s, user.getInt(s) + i);
                    } else {
                        plugin.getMySQLDatabase().setStat(player.getUniqueId().toString(), s, user.getInt(s));
                    }
                }
            });
            UserManager.removeUser(event.getPlayer().getUniqueId());
        } else {
            for(String s : BuildBattleStats.STATISTICS) {
                plugin.getFileStats().saveStat(player, s);
            }
        }
    }

}
