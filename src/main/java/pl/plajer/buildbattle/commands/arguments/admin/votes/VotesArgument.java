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

import org.apache.commons.lang.StringUtils;
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
 * Created at 23.01.2019
 */
public class VotesArgument {

  public VotesArgument(ArgumentsRegistry registry) {
    registry.mapArgument("buildbattleadmin", new LabeledCommandArgument("votes", "buildbattle.admin.supervotes.manage",
        CommandArgument.ExecutorType.BOTH, new LabelData("/bba votes &6<add/set> <amount> &c[player]", "/bba votes <action> <amount>",
        "&7Set or add super votes to yourself or target player\n&7Can be used from console too\n&6Permission: &7buildbattle.admin.supervotes.manage")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
          sender.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Commands.Invalid-Args"));
          return;
        }
        if (args.length == 2 || (!args[1].equalsIgnoreCase("add") && args[1].equalsIgnoreCase("set"))) {
          sender.sendMessage(ChatManager.getPrefix() + ChatColor.RED + "Please type amount of super votes to add or set!");
          return;
        }
        Player target;
        if (args.length == 3) {
          target = (Player) sender;
        } else {
          target = Bukkit.getPlayerExact(args[3]);
        }

        if (target == null) {
          sender.sendMessage(ChatManager.colorMessage("Commands.Player-Not-Found"));
          return;
        }

        if (Utils.isInteger(args[2])) {
          User user = registry.getPlugin().getUserManager().getUser(target.getUniqueId());

          String successMessage;
          if (args[1].equalsIgnoreCase("add")) {
            user.addStat(StatsStorage.StatisticType.SUPER_VOTES, Integer.parseInt(args[2]));
            successMessage = ChatManager.getPrefix() + ChatManager.colorMessage("Commands.Arguments.Super-Votes-Received");
            successMessage = StringUtils.replace(successMessage, "%amount%", args[2]);
          } else {
            user.setStat(StatsStorage.StatisticType.SUPER_VOTES, Integer.parseInt(args[2]));
            successMessage = ChatManager.getPrefix() + ChatManager.colorMessage("Commands.Arguments.Super-Votes-Set");
            successMessage = StringUtils.replace(successMessage, "%amount%", args[2]);
          }
          //todo translatable?
          sender.sendMessage(ChatManager.getPrefix() + ChatManager.colorRawMessage("&aSuper votes amount modified!"));
          target.sendMessage(successMessage);
        } else {
          //todo translatable?
          sender.sendMessage(ChatManager.colorRawMessage("&cArgument isn't a number!"));
        }
      }
    });
  }

}
