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

package pl.plajer.buildbattle3.arena;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.buildbattleapi.BBGameEndEvent;
import pl.plajer.buildbattle3.user.UserManager;
import pl.plajer.buildbattle3.utils.Util;

/**
 * @author Plajer
 * <p>
 * Created at 25.05.2018
 */
public class ArenaManager {

    public static void stopGame(boolean quickStop, Arena arena) {
        Main.debug("Game stop event initiate, arena " + arena.getID(), System.currentTimeMillis());
        BBGameEndEvent gameEndEvent = new BBGameEndEvent(arena);
        Bukkit.getPluginManager().callEvent(gameEndEvent);
        for(final Player p : arena.getPlayers()) {
            UserManager.getUser(p.getUniqueId()).removeScoreboard();
            if(!quickStop) {
                if(JavaPlugin.getPlugin(Main.class).getConfig().getBoolean("Firework-When-Game-Ends")) {
                    new BukkitRunnable() {
                        int i = 0;

                        public void run() {
                            if(i == 4) this.cancel();
                            if(!arena.getPlayers().contains(p)) this.cancel();
                            Util.spawnRandomFirework(p.getLocation());
                            i++;
                        }
                    }.runTaskTimer(JavaPlugin.getPlugin(Main.class), 30, 30);
                }
            }
        }
        arena.setVoting(false);
        Main.debug("Game stop event finish, arena " + arena.getID(), System.currentTimeMillis());
    }

}
