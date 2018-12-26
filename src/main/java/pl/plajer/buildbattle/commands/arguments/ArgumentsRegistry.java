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

package pl.plajer.buildbattle.commands.arguments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.commands.arguments.data.CommandArgument;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.utils.StringMatcher;

/**
 * @author Plajer
 * <p>
 * Created at 03.12.2018
 */
public class ArgumentsRegistry implements CommandExecutor {

  private Main plugin;
  private Map<String, List<CommandArgument>> mappedArguments = new HashMap<>();

  //todo help commands
  //todo add rest of command arguments
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    try {
      for (String mainCommand : mappedArguments.keySet()) {
        if (cmd.getName().equalsIgnoreCase(mainCommand)) {
          for (CommandArgument argument : mappedArguments.get(mainCommand)) {
            if (argument.getArgumentName().equalsIgnoreCase(args[0])) {
              boolean hasPerm = false;
              for (String perm : argument.getPermissions()) {
                if (perm.equals("")) {
                  hasPerm = true;
                  break;
                }
                if (sender.hasPermission(perm)) {
                  hasPerm = true;
                  break;
                }
              }
              if (!hasPerm) {
                return true;
              }
              if (checkSenderIsExecutorType(sender, argument.getValidExecutors())) {
                argument.execute(sender, args);
              }
              //return true even if sender is not good executor or hasn't got permission
              return true;
            }
          }

          //sending did you mean help
          List<StringMatcher.Match> matches = StringMatcher.match(args[0], mappedArguments.get(cmd.getName().toLowerCase()).stream().map(CommandArgument::getArgumentName).collect(Collectors.toList()));
          if (!matches.isEmpty()) {
            sender.sendMessage(ChatManager.colorMessage("Commands.Did-You-Mean").replace("%command%", label + " " + matches.get(0).getMatch()));
            return true;
          }
        }
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
    return false;
  }

  private boolean checkSenderIsExecutorType(CommandSender sender, CommandArgument.ExecutorType type) {
    switch (type) {
      case BOTH:
        return sender instanceof ConsoleCommandSender || sender instanceof Player;
      case CONSOLE:
        return sender instanceof ConsoleCommandSender;
      case PLAYER:
        if (sender instanceof Player) {
          return true;
        }
        sender.sendMessage(ChatManager.colorMessage("Commands.Only-By-Player"));
        return false;
      default:
        return false;
    }
  }

  /**
   * Maps new argument to the main command
   *
   * @param mainCommand mother command ex. /mm
   * @param argument    argument to map ex. leave (for /mm leave)
   */
  public void mapArgument(String mainCommand, CommandArgument argument) {
    List<CommandArgument> args = mappedArguments.getOrDefault(mainCommand, new ArrayList<>());
    args.add(argument);
    mappedArguments.put(mainCommand, args);
  }

  public Map<String, List<CommandArgument>> getMappedArguments() {
    return mappedArguments;
  }

  public Main getPlugin() {
    return plugin;
  }

}
