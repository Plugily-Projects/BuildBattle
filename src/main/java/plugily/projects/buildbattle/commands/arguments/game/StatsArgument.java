/*
 *
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2021 Plugily Projects - maintained by Tigerpanzer_02, 2Wild4You and contributors
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
 *
 */

package plugily.projects.buildbattle.commands.arguments.game;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.buildbattle.api.StatsStorage;
import plugily.projects.buildbattle.commands.arguments.ArgumentsRegistry;
import plugily.projects.buildbattle.commands.arguments.data.CommandArgument;
import plugily.projects.buildbattle.handlers.ChatManager;
import plugily.projects.buildbattle.user.User;

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
        ChatManager cm = registry.getPlugin().getChatManager();
        if(player == null || registry.getPlugin().getUserManager().getUser(player) == null) {
          sender.sendMessage(cm.getPrefix() + cm.colorMessage("Commands.Player-Not-Found"));
          return;
        }
        User user = registry.getPlugin().getUserManager().getUser(player);
        if(player.equals(sender)) {
          sender.sendMessage(cm.colorMessage("Commands.Stats-Command.Header"));
        } else {
          sender.sendMessage(cm.colorMessage("Commands.Stats-Command.Header-Other").replace("%player%", player.getName()));
        }
        sender.sendMessage(cm.colorMessage("Commands.Stats-Command.Wins") + user.getStat(StatsStorage.StatisticType.WINS));
        sender.sendMessage(cm.colorMessage("Commands.Stats-Command.Loses") + user.getStat(StatsStorage.StatisticType.LOSES));
        sender.sendMessage(cm.colorMessage("Commands.Stats-Command.Games-Played") + user.getStat(StatsStorage.StatisticType.GAMES_PLAYED));
        sender.sendMessage(cm.colorMessage("Commands.Stats-Command.Highest-Win") + user.getStat(StatsStorage.StatisticType.HIGHEST_WIN));
        sender.sendMessage(cm.colorMessage("Commands.Stats-Command.Blocks-Placed") + user.getStat(StatsStorage.StatisticType.BLOCKS_PLACED));
        sender.sendMessage(cm.colorMessage("Commands.Stats-Command.Blocks-Broken") + user.getStat(StatsStorage.StatisticType.BLOCKS_BROKEN));
        sender.sendMessage(cm.colorMessage("Commands.Stats-Command.Particles-Placed") + user.getStat(StatsStorage.StatisticType.PARTICLES_USED));
        sender.sendMessage(cm.colorMessage("Commands.Stats-Command.Super-Votes") + user.getStat(StatsStorage.StatisticType.SUPER_VOTES));
        sender.sendMessage(cm.colorMessage("Commands.Stats-Command.Footer"));
      }
    });
  }

}
