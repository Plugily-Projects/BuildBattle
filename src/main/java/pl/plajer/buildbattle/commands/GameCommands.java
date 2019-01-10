/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.plajer.buildbattle.ConfigPreferences;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.arena.ArenaManager;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.user.User;

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

  private void showStats(User user) {
    Player player = user.toPlayer();
    player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Wins") + user.getStat(StatsStorage.StatisticType.WINS));
    player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Loses") + user.getStat(StatsStorage.StatisticType.LOSES));
    player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Games-Played") + user.getStat(StatsStorage.StatisticType.GAMES_PLAYED));
    player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Highest-Win") + user.getStat(StatsStorage.StatisticType.HIGHEST_WIN));
    player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Blocks-Placed") + user.getStat(StatsStorage.StatisticType.BLOCKS_PLACED));
    player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Blocks-Broken") + user.getStat(StatsStorage.StatisticType.BLOCKS_BROKEN));
    player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Particles-Placed") + user.getStat(StatsStorage.StatisticType.PARTICLES_USED));
    player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Super-Votes") + user.getStat(StatsStorage.StatisticType.SUPER_VOTES));
    player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Footer"));
  }

  public void showStats(Player player) {
    User user = plugin.getUserManager().getUser(player.getUniqueId());
    player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Header"));
    showStats(user);
  }

  @Deprecated //broken display
  public void showStatsOther(Player player, Player other) {
    User user = plugin.getUserManager().getUser(other.getUniqueId());
    player.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Header-Other").replace("%player%", other.getName()));
    showStats(user);
  }

  public void leaveGame(CommandSender sender) {
    if (checkSenderIsConsole(sender)) return;
    if (!plugin.getConfig().getBoolean("Disable-Leave-Command")) {
      Player p = (Player) sender;
      Arena arena = ArenaRegistry.getArena(p);
      if (arena == null) return;
      p.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Commands.Teleported-To-The-Lobby"));
      if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
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
      String statistic = StringUtils.capitalize(statisticType.toString().toLowerCase().replace("_", " "));
      for (int i = 0; i < 10; i++) {
        try {
          UUID current = (UUID) stats.keySet().toArray()[stats.keySet().toArray().length - 1];
          sender.sendMessage(ChatManager.colorMessage("Commands.Statistics.Format")
                  .replace("%position%", String.valueOf(i + 1))
                  .replace("%name%", Bukkit.getOfflinePlayer(current).getName())
                  .replace("%value%", String.valueOf(stats.get(current)))
              .replace("%statistic%", statistic)); //Games_played > Games played etc
          stats.remove(current);
        } catch (IndexOutOfBoundsException ex) {
          sender.sendMessage(ChatManager.colorMessage("Commands.Statistics.Format")
                  .replace("%position%", String.valueOf(i + 1))
                  .replace("%name%", "Empty")
                  .replace("%value%", "0")
              .replace("%statistic%", statistic));
        } catch (NullPointerException ex) {
          UUID current = (UUID) stats.keySet().toArray()[stats.keySet().toArray().length - 1];
          if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
            ResultSet set = plugin.getMySQLDatabase().executeQuery("SELECT name FROM buildbattlestats WHERE UUID='" + current.toString() + "'");
            try {
              if (set.next()) {
                sender.sendMessage(ChatManager.colorMessage("Commands.Statistics.Format")
                        .replace("%position%", String.valueOf(i + 1))
                        .replace("%name%", set.getString(1))
                        .replace("%value%", String.valueOf(stats.get(current)))
                    .replace("%statistic%", statistic));
                continue;
              }
            } catch (SQLException ignored) {
            }
          }
          sender.sendMessage(ChatManager.colorMessage("Commands.Statistics.Format")
                  .replace("%position%", String.valueOf(i + 1))
                  .replace("%name%", "Unknown Player")
                  .replace("%value%", String.valueOf(stats.get(current)))
              .replace("%statistic%", statistic));
        }
      }
    } catch (IllegalArgumentException e) {
      sender.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Commands.Statistics.Invalid-Name"));
    }
  }

}
