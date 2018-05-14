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

package pl.plajer.buildbattle.commands;

import org.bukkit.entity.Player;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.user.User;
import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.user.UserManager;

/**
 * @author Plajer
 * <p>
 * Created at 13.05.2018
 */
public class GameCommands extends MainCommand {

    private Main plugin;

    public GameCommands(Main plugin) {
        this.plugin = plugin;
    }

    public void showStats(Player player) {
        User user = UserManager.getUser(player.getUniqueId());
        player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Header"));
        player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Wins") + user.getInt("wins"));
        player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Loses") + user.getInt("loses"));
        player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Games-Played") + user.getInt("gamesplayed"));
        player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Highest-Win") + user.getInt("highestwin"));
        player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Blocks-Placed") + user.getInt("blocksplaced"));
        player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Blocks-Broken") + user.getInt("blocksbroken"));
        player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Particles-Placed") + user.getInt("particles"));
        player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Footer"));
    }

    public void leaveGame(Player player) {
        if(plugin.getConfig().getBoolean("Disable-Leave-Command")) return;
        Arena arena = ArenaRegistry.getArena(player);
        if(arena == null) {
            System.out.print(player.getName() + " tried /leave but isn't in an arena!");
            return;
        }
        if(plugin.isBungeeActivated()) {
            plugin.getBungeeManager().connectToHub(player);
            System.out.print(player.getName() + " is teleported to the Hub Server");
        } else {
            arena.teleportToEndLocation(player);
            arena.leaveAttempt(player);
            System.out.print(player.getName() + " has left the arena! He is teleported to the end location.");
        }
    }

}
