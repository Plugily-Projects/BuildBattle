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

package pl.plajer.buildbattle.commands.arguments.game;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.commands.arguments.ArgumentsRegistry;
import pl.plajer.buildbattle.commands.arguments.data.CommandArgument;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.user.User;

/**
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 */
public class StatsArgument {

  public StatsArgument(ArgumentsRegistry registry) {
    registry.mapArgument("buildbattle", new CommandArgument("stats", "", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        Player player = args.length == 2 ? Bukkit.getPlayerExact(args[1]) : (Player) sender;
        if (player == null || registry.getPlugin().getUserManager().getUser(player.getUniqueId()) == null) {
          sender.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Commands.Admin-Commands.Player-Not-Found"));
          return;
        }
        User user = registry.getPlugin().getUserManager().getUser(player.getUniqueId());
        if (player.equals(sender)) {
          sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Header"));
        } else {
          sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Header-Other").replace("%player%", player.getName()));
        }
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Wins") + user.getStat(StatsStorage.StatisticType.WINS));
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Loses") + user.getStat(StatsStorage.StatisticType.LOSES));
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Games-Played") + user.getStat(StatsStorage.StatisticType.GAMES_PLAYED));
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Highest-Win") + user.getStat(StatsStorage.StatisticType.HIGHEST_WIN));
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Blocks-Placed") + user.getStat(StatsStorage.StatisticType.BLOCKS_PLACED));
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Blocks-Broken") + user.getStat(StatsStorage.StatisticType.BLOCKS_BROKEN));
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Particles-Placed") + user.getStat(StatsStorage.StatisticType.PARTICLES_USED));
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Super-Votes") + user.getStat(StatsStorage.StatisticType.SUPER_VOTES));
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Footer"));
      }
    });
  }

}
