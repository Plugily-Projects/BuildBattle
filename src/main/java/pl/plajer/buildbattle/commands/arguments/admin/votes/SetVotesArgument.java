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

package pl.plajer.buildbattle.commands.arguments.admin.votes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.commands.arguments.ArgumentsRegistry;
import pl.plajer.buildbattle.commands.arguments.data.CommandArgument;
import pl.plajer.buildbattle.commands.arguments.data.LabelData;
import pl.plajer.buildbattle.commands.arguments.data.LabeledCommandArgument;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.user.User;
import pl.plajer.buildbattle.utils.Utils;

/**
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 */
public class SetVotesArgument {

  public SetVotesArgument(ArgumentsRegistry registry) {
    registry.mapArgument("buildbattleadmin", new LabeledCommandArgument("setvotes", "buildbattle.admin.supervotes.set",
        CommandArgument.ExecutorType.BOTH, new LabelData("/bba setvotes  &6<amount> &c[player]", "/bba setvotes <amount>",
        "&7Set super votes to yourself or target player\n&7Can be used from console too\n&6Permission: &7buildbattle.admin.supervotes.set")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
          sender.sendMessage(ChatManager.getPrefix() + ChatColor.RED + "Please type amount of super votes to set!");
          return;
        }
        Player target;
        if (args.length == 2) {
          target = (Player) sender;
        } else {
          target = Bukkit.getPlayerExact(args[2]);
        }

        if (target == null) {
          sender.sendMessage(ChatManager.colorMessage("Commands.Player-Not-Found"));
          return;
        }

        if (Utils.isInteger(args[1])) {
          User user = registry.getPlugin().getUserManager().getUser(target.getUniqueId());
          user.setStat(StatsStorage.StatisticType.SUPER_VOTES, Integer.parseInt(args[1]));
          //todo translatable?
          sender.sendMessage(ChatManager.getPrefix() + ChatManager.colorRawMessage("&aSuper votes set."));
        } else {
          //todo translatable?
          sender.sendMessage(ChatManager.colorRawMessage("&cArgument isn't a number!"));
        }
      }
    });
  }

}
