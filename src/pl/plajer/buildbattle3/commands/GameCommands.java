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

package pl.plajer.buildbattle3.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.arena.Arena;
import pl.plajer.buildbattle3.arena.ArenaRegistry;
import pl.plajer.buildbattle3.handlers.ChatManager;
import pl.plajer.buildbattle3.user.User;
import pl.plajer.buildbattle3.user.UserManager;

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

    public void leaveGame(CommandSender sender) {
        if(checkSenderIsConsole(sender)) return;
        if(!plugin.getConfig().getBoolean("Disable-Leave-Command")) {
            Player p = (Player) sender;
            Arena arena = ArenaRegistry.getArena(p);
            if(arena == null) return;
            p.sendMessage(ChatManager.PREFIX + ChatManager.colorMessage("Commands.Teleported-To-The-Lobby"));
            if(plugin.isBungeeActivated()) {
                plugin.getBungeeManager().connectToHub(p);
                System.out.print(p.getName() + " is teleported to the Hub Server");
            } else {
                arena.teleportToEndLocation(p);
                arena.leaveAttempt(p);
                System.out.print(p.getName() + " has left the arena! He is teleported to the end location.");
            }
        }
    }

}
