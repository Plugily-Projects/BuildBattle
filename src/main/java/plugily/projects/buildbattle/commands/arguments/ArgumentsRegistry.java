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

package plugily.projects.buildbattle.commands.arguments;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion;
import pl.plajerlair.commonsbox.string.StringMatcher;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.commands.TabCompletion;
import plugily.projects.buildbattle.commands.arguments.admin.ListArenasArgument;
import plugily.projects.buildbattle.commands.arguments.admin.ReloadArgument;
import plugily.projects.buildbattle.commands.arguments.admin.arena.*;
import plugily.projects.buildbattle.commands.arguments.admin.plot.AddPlotArgument;
import plugily.projects.buildbattle.commands.arguments.admin.plot.PlotWandArgument;
import plugily.projects.buildbattle.commands.arguments.admin.plot.RemovePlotArgument;
import plugily.projects.buildbattle.commands.arguments.admin.votes.VotesArgument;
import plugily.projects.buildbattle.commands.arguments.data.CommandArgument;
import plugily.projects.buildbattle.commands.arguments.data.LabelData;
import plugily.projects.buildbattle.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.buildbattle.commands.arguments.game.*;
import plugily.projects.buildbattle.handlers.setup.SetupInventory;
import plugily.projects.buildbattle.utils.Debugger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Plajer
 * <p>
 * Created at 03.12.2018
 */
public class ArgumentsRegistry implements CommandExecutor {

  private final Map<String, List<CommandArgument>> mappedArguments = new HashMap<>();
  private final Main plugin;

  public ArgumentsRegistry(Main plugin) {
    this.plugin = plugin;
    TabCompletion completion = new TabCompletion(this);
    Optional.ofNullable(plugin.getCommand("buildbattle")).ifPresent(bb -> {
      bb.setExecutor(this);
      bb.setTabCompleter(completion);
    });
    Optional.ofNullable(plugin.getCommand("buildbattleadmin")).ifPresent(bba -> {
      bba.setExecutor(this);
      bba.setTabCompleter(completion);
    });

    //register Build Battle basic arguments
    new CreateArgument(this);
    new JoinArguments(this);
    new LeaderboardArgument(this);
    new LeaveArgument(this);
    new StatsArgument(this);
    new ArenaSelectorArgument(this);

    //register Build Battle admin arguments
    //arena related arguments
    new AddNpcArgument(this);
    new DeleteArgument(this);
    new ForceStartArguments(this);
    new ReloadArgument(this);
    new SetThemeArgument(this);
    new StopArgument(this);
    new VotesArgument(this);

    //other admin related arguments
    new AddPlotArgument(this);
    new PlotWandArgument(this);
    new RemovePlotArgument(this);
    new ListArenasArgument(this);
  }

  //todo complex
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    for (String mainCommand : mappedArguments.keySet()) {
      if (cmd.getName().equalsIgnoreCase(mainCommand)) {
        if (cmd.getName().equalsIgnoreCase("buildbattle")) {
          if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(plugin.getChatManager().colorMessage("Commands.Main-Command.Header"));
            sender.sendMessage(plugin.getChatManager().colorMessage("Commands.Main-Command.Description"));
            if (sender.hasPermission("buildbattle.admin")) {
              sender.sendMessage(plugin.getChatManager().colorMessage("Commands.Main-Command.Admin-Bonus-Description"));
            }
            sender.sendMessage(plugin.getChatManager().colorMessage("Commands.Main-Command.Footer"));
            return true;
          }
          if (args.length > 1 && args[1].equalsIgnoreCase("edit")) {
            if (args[1].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("create")) {
              if (!checkSenderIsExecutorType(sender, CommandArgument.ExecutorType.PLAYER) || !hasPermission(sender, "buildbattle.admin.create")) {
                return true;
              }
              BaseArena arena = ArenaRegistry.getArena(args[0]);
              if (arena == null) {
                sender.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Commands.No-Arena-Like-That"));
                return true;
              }

              SetupInventory.sendProTip((Player) sender);
              new SetupInventory(arena).openInventory((Player) sender);
              return true;
            }
          }
        }
        if (cmd.getName().equalsIgnoreCase("buildbattleadmin") && (args.length == 0 || args[0].equalsIgnoreCase("help"))) {
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
            if (sender instanceof Player) {
              TextComponent component = new TextComponent(labelData.getText());
              component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, labelData.getCommand()));

              // Backwards compatibility
              if (ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_16_R1)) {
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(labelData.getDescription())));
              } else {
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(labelData.getDescription())));
              }

              ((Player) sender).spigot().sendMessage(component);
            } else {
              //more descriptive for console - split at \n to show only basic description
              Debugger.sendConsoleMsg(labelData.getText() + " - " + labelData.getDescription().split("\n")[0]);
            }
          }
          return true;
        }
        for (CommandArgument argument : mappedArguments.get(mainCommand)) {
          if (argument.getArgumentName().equalsIgnoreCase(args[0])) {
            for (String perm : argument.getPermissions()) {
              if (perm.isEmpty() || hasPermission(sender, perm)) {
                break;
              }

              //user has no permission to execute command
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
          sender.sendMessage(plugin.getChatManager().colorMessage("Commands.Did-You-Mean").replace("%command%", label + " " + matches.get(0).getMatch()));
          return true;
        }
      }
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
        sender.sendMessage(plugin.getChatManager().colorMessage("Commands.Only-By-Player"));
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
    sender.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Commands.No-Permission"));
    return false;
  }

  public Map<String, List<CommandArgument>> getMappedArguments() {
    return mappedArguments;
  }

  public Main getPlugin() {
    return plugin;
  }

}
