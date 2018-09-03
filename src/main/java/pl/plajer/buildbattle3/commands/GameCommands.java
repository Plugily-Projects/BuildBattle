/*
 * BuildBattle 3 - Ultimate building competition minigame
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.arena.Arena;
import pl.plajer.buildbattle3.arena.ArenaManager;
import pl.plajer.buildbattle3.arena.ArenaRegistry;
import pl.plajer.buildbattle3.buildbattleapi.StatsStorage;
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
    player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Super-Votes") + user.getInt("supervotes"));
    player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Footer"));
  }

  public void showStatsOther(Player player, Player other) {
    User user = UserManager.getUser(other.getUniqueId());
    player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Header-Other").replace("%player%", other.getName()));
    player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Wins") + user.getInt("wins"));
    player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Loses") + user.getInt("loses"));
    player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Games-Played") + user.getInt("gamesplayed"));
    player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Highest-Win") + user.getInt("highestwin"));
    player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Blocks-Placed") + user.getInt("blocksplaced"));
    player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Blocks-Broken") + user.getInt("blocksbroken"));
    player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Particles-Placed") + user.getInt("particles"));
    player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Super-Votes") + user.getInt("supervotes"));
    player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Footer"));
  }

  public void leaveGame(CommandSender sender) {
    if (checkSenderIsConsole(sender)) return;
    if (!plugin.getConfig().getBoolean("Disable-Leave-Command")) {
      Player p = (Player) sender;
      Arena arena = ArenaRegistry.getArena(p);
      if (arena == null) return;
      p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Teleported-To-The-Lobby"));
      if (plugin.isBungeeActivated()) {
        plugin.getBungeeManager().connectToHub(p);
        System.out.print(p.getName() + " is teleported to the Hub Server");
      } else {
        arena.teleportToEndLocation(p);
        ArenaManager.leaveAttempt(p, arena);
        System.out.print(p.getName() + " has left the arena! He is teleported to the end location.");
      }
    }
  }

  public void sendTopStatistics(CommandSender sender, String stat) {
    try {
      StatsStorage.StatisticType statisticType = StatsStorage.StatisticType.valueOf(stat.toUpperCase());
      LinkedHashMap<UUID, Integer> stats = (LinkedHashMap<UUID, Integer>) StatsStorage.getStats(statisticType);
      sender.sendMessage(ChatManager.colorMessage("Commands.Statistics.Header"));
      for (int i = 0; i < 10; i++) {
        try {
          UUID current = (UUID) stats.keySet().toArray()[stats.keySet().toArray().length - 1];
          sender.sendMessage(ChatManager.colorMessage("Commands.Statistics.Format")
                  .replace("%position%", String.valueOf(i + 1))
                  .replace("%name%", Bukkit.getOfflinePlayer(current).getName())
                  .replace("%value%", String.valueOf(stats.get(current)))
                  .replace("%statistic%", StringUtils.capitalize(statisticType.toString().toLowerCase().replace("_", " ")))); //Games_played > Games played etc
          stats.remove(current);
        } catch (IndexOutOfBoundsException ex) {
          sender.sendMessage(ChatManager.colorMessage("Commands.Statistics.Format")
                  .replace("%position%", String.valueOf(i + 1))
                  .replace("%name%", "Empty")
                  .replace("%value%", "0")
                  .replace("%statistic%", StringUtils.capitalize(statisticType.toString().toLowerCase().replace("_", " "))));
        } catch (NullPointerException ex) {
          UUID current = (UUID) stats.keySet().toArray()[stats.keySet().toArray().length - 1];
          if (plugin.isDatabaseActivated()) {
            ResultSet set = plugin.getMySQLDatabase().executeQuery("SELECT name FROM buildbattlestats WHERE UUID='" + current.toString() + "'");
            try {
              if (set.next()) {
                sender.sendMessage(ChatManager.colorMessage("Commands.Statistics.Format")
                        .replace("%position%", String.valueOf(i + 1))
                        .replace("%name%", set.getString(1))
                        .replace("%value%", String.valueOf(stats.get(current)))
                        .replace("%statistic%", StringUtils.capitalize(statisticType.toString().toLowerCase().replace("_", " "))));
                return;
              }
            } catch (SQLException ignored) {
            }
          }
          sender.sendMessage(ChatManager.colorMessage("Commands.Statistics.Format")
                  .replace("%position%", String.valueOf(i + 1))
                  .replace("%name%", "Unknown Player")
                  .replace("%value%", String.valueOf(stats.get(current)))
                  .replace("%statistic%", StringUtils.capitalize(statisticType.toString().toLowerCase().replace("_", " "))));
        }
      }
    } catch (IllegalArgumentException e) {
      sender.sendMessage(ChatManager.colorMessage("Commands.Statistics.Invalid-Name"));
    }
  }

}
