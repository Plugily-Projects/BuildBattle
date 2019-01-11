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

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.impl.Arena;
import pl.plajer.buildbattle.commands.TabCompletion;
import pl.plajer.buildbattle.commands.arguments.admin.ListArenasArgument;
import pl.plajer.buildbattle.commands.arguments.admin.arena.DeleteArgument;
import pl.plajer.buildbattle.commands.arguments.admin.arena.ForceStartArguments;
import pl.plajer.buildbattle.commands.arguments.admin.arena.ReloadArgument;
import pl.plajer.buildbattle.commands.arguments.admin.arena.SetThemeArgument;
import pl.plajer.buildbattle.commands.arguments.admin.arena.StopArgument;
import pl.plajer.buildbattle.commands.arguments.admin.plot.AddPlotArgument;
import pl.plajer.buildbattle.commands.arguments.admin.plot.PlotWandArgument;
import pl.plajer.buildbattle.commands.arguments.admin.plot.RemovePlotArgument;
import pl.plajer.buildbattle.commands.arguments.admin.votes.AddVotesArgument;
import pl.plajer.buildbattle.commands.arguments.admin.votes.SetVotesArgument;
import pl.plajer.buildbattle.commands.arguments.data.CommandArgument;
import pl.plajer.buildbattle.commands.arguments.data.LabelData;
import pl.plajer.buildbattle.commands.arguments.data.LabeledCommandArgument;
import pl.plajer.buildbattle.commands.arguments.game.CreateArgument;
import pl.plajer.buildbattle.commands.arguments.game.JoinArguments;
import pl.plajer.buildbattle.commands.arguments.game.LeaderboardArgument;
import pl.plajer.buildbattle.commands.arguments.game.LeaveArgument;
import pl.plajer.buildbattle.commands.arguments.game.StatsArgument;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.handlers.setup.SetupInventory;
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

  public ArgumentsRegistry(Main plugin) {
    this.plugin = plugin;
    TabCompletion completion = new TabCompletion(this);
    plugin.getCommand("buildbattle").setExecutor(this);
    plugin.getCommand("buildbattle").setTabCompleter(completion);
    plugin.getCommand("buildbattleadmin").setExecutor(this);
    plugin.getCommand("buildbattleadmin").setTabCompleter(completion);

    //register Build Battle basic arguments
    new CreateArgument(this);
    new JoinArguments(this);
    new LeaderboardArgument(this);
    new LeaveArgument(this);
    new StatsArgument(this);

    //register Build Battle admin arguments
    //arena related arguments
    new DeleteArgument(this);
    new ForceStartArguments(this);
    new ReloadArgument(this);
    new SetThemeArgument(this);
    new StopArgument(this);

    //player super votes related arguments
    new AddVotesArgument(this);
    new SetVotesArgument(this);

    //other admin related arguments
    new AddPlotArgument(this);
    new PlotWandArgument(this);
    new RemovePlotArgument(this);
    new ListArenasArgument(this);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    try {
      for (String mainCommand : mappedArguments.keySet()) {
        if (cmd.getName().equalsIgnoreCase(mainCommand)) {
          if (cmd.getName().equalsIgnoreCase("buildbattle")) {
            if (args.length == 0) {
              sender.sendMessage(ChatManager.colorMessage("Commands.Main-Command.Header"));
              sender.sendMessage(ChatManager.colorMessage("Commands.Main-Command.Description"));
              if (sender.hasPermission("buildbattle.admin")) {
                sender.sendMessage(ChatManager.colorMessage("Commands.Main-Command.Admin-Bonus-Description"));
              }
              sender.sendMessage(ChatManager.colorMessage("Commands.Main-Command.Footer"));
              return true;
            }
            if (args.length > 1 && args[1].equalsIgnoreCase("edit")) {
              if (args[1].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("create")) {
                if (!checkSenderIsExecutorType(sender, CommandArgument.ExecutorType.PLAYER) || !hasPermission(sender, "buildbattle.admin.create")) {
                  return true;
                }
                Arena arena = ArenaRegistry.getArena(args[0]);
                if (arena == null) {
                  sender.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
                  return true;
                }

                SetupInventory.sendProTip((Player) sender);
                new SetupInventory(arena).openInventory((Player) sender);
                return true;
              }
            }
          }
          if (cmd.getName().equalsIgnoreCase("buildbattleadmin")) {
            if (args.length == 0) {
              if (!sender.hasPermission("buildbattle.admin")) {
                return true;
              }
              sender.sendMessage(ChatColor.GREEN + "  " + ChatColor.BOLD + "Build Battle " + ChatColor.GRAY + plugin.getDescription().getVersion());
              sender.sendMessage(ChatColor.RED + " []" + ChatColor.GRAY + " = optional  " + ChatColor.GOLD + "<>" + ChatColor.GRAY + " = required");
              if (sender instanceof Player) {
                sender.sendMessage(ChatColor.GRAY + "Hover command to see more, click command to suggest it.");
              }
              List<LabelData> data = mappedArguments.get("buildbattleadmin").stream().filter(arg -> arg instanceof LabeledCommandArgument)
                  .map(arg -> ((LabeledCommandArgument) arg).getLabelData()).collect(Collectors.toList());
              data.add(new LabelData("/bb &6<arena>&f edit", "/bb <arena> edit",
                  "&7Edit existing arena\n&6Permission: &7buildbattle.admin.edit"));
              data.addAll(mappedArguments.get("buildbattle").stream().filter(arg -> arg instanceof LabeledCommandArgument)
                  .map(arg -> ((LabeledCommandArgument) arg).getLabelData()).collect(Collectors.toList()));
              for (LabelData labelData : data) {
                TextComponent component;
                if (sender instanceof Player) {
                  component = new TextComponent(labelData.getText());
                } else {
                  //more descriptive for console - split at \n to show only basic description
                  component = new TextComponent(labelData.getText() + " - " + labelData.getDescription().split("\n")[0]);
                }
                component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, labelData.getCommand()));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(labelData.getDescription()).create()));
                sender.spigot().sendMessage(component);
              }
              return true;
            }
          }
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

  public boolean hasPermission(CommandSender sender, String perm) {
    if (sender.hasPermission(perm)) {
      return true;
    }
    sender.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Commands.No-Permission"));
    return false;
  }

  public Map<String, List<CommandArgument>> getMappedArguments() {
    return mappedArguments;
  }

  public Main getPlugin() {
    return plugin;
  }

}
