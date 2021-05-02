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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.buildbattle.ConfigPreferences;
import plugily.projects.buildbattle.arena.ArenaManager;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.ArenaState;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.commands.arguments.ArgumentsRegistry;
import plugily.projects.buildbattle.commands.arguments.data.CommandArgument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Plajer
 * <p>
 * Created at 03.12.2018
 */
public class JoinArguments {

  public JoinArguments(ArgumentsRegistry registry) {
    //join argument
    registry.mapArgument("buildbattle", new CommandArgument("join", "", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
          sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.Type-Arena-Name"));
          return;
        }
        if(ArenaRegistry.getArena(((Player) sender)) != null) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("In-Game.Messages.Already-Playing"));
          return;
        }
        if(args[1].equalsIgnoreCase("maxplayers") && ArenaRegistry.getArena("maxplayers") == null) {
          if(args[2].isEmpty()) {
            sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.Invalid-Args"));
            return;
          }
          switch(args[2].toLowerCase()) {
            case "solo":
            case "team":
            case "gtb":
            case "guess_the_build":
            default:
              if(args[2].equalsIgnoreCase("gtb") || args[2].equalsIgnoreCase("guess_the_build")) {
                args[2] = "GUESS_THE_BUILD";
              }

              BaseArena.ArenaType type = BaseArena.ArenaType.SOLO;
              try {
                type = BaseArena.ArenaType.valueOf(args[2].toUpperCase());
              } catch (IllegalArgumentException ex) {
              }

              List<BaseArena> baseArenas = new ArrayList<>();
              Map<BaseArena, Integer> arenas = new HashMap<>();
              for(BaseArena arena : ArenaRegistry.getArenas()) {
                if(arena.getArenaType() == type) {
                  arenas.put(arena, arena.getPlayers().size());
                  baseArenas.add(arena);
                }
              }
              if(arenas.isEmpty()) {
                ArenaManager.joinAttempt((Player) sender, ArenaRegistry.getArenas().get(ThreadLocalRandom.current().nextInt(ArenaRegistry.getArenas().size())));
                return;
              }
              if(ArenaRegistry.getArenaPlayersOnline() == 0) {
                BaseArena arena = baseArenas.get(ThreadLocalRandom.current().nextInt(baseArenas.size()));
                ArenaManager.joinAttempt((Player) sender, arena);
                return;
              }
              Stream<Map.Entry<BaseArena, Integer>> sorted = arenas.entrySet().stream().sorted(Map.Entry.comparingByValue());
              if(sorted.findFirst().isPresent()) {
                BaseArena arena = sorted.findFirst().get().getKey();
                ArenaManager.joinAttempt((Player) sender, arena);
                return;
              }
          }
        }
        for(BaseArena arena : ArenaRegistry.getArenas()) {
          if(args[1].equalsIgnoreCase(arena.getID())) {
            ArenaManager.joinAttempt((Player) sender, arena);
            return;
          }
        }
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.No-Arena-Like-That"));
      }
    });

    //random join argument, register only for multi arena
    if(!registry.getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
      registry.mapArgument("buildbattle", new CommandArgument("randomjoin", "", CommandArgument.ExecutorType.PLAYER) {
        @Override
        public void execute(CommandSender sender, String[] args) {
          if(ArenaRegistry.getArena(((Player) sender)) != null) {
            sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("In-Game.Messages.Already-Playing"));
            return;
          }
          if(args.length == 1) {
            sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.Invalid-Args"));
            return;
          }
          switch(args[1].toLowerCase()) {
            case "solo":
            case "team":
            case "gtb":
            case "guess_the_build":
              if(args[1].equalsIgnoreCase("gtb") || args[1].equalsIgnoreCase("guess_the_build")) {
                args[1] = "GUESS_THE_BUILD";
              }
              BaseArena.ArenaType t = BaseArena.ArenaType.SOLO;
              try {
                t = BaseArena.ArenaType.valueOf(args[1].toUpperCase());
              } catch (IllegalArgumentException ex) {
              }

              BaseArena.ArenaType type = t;
              //check starting arenas -> random
              List<BaseArena> arenas = ArenaRegistry.getArenas().stream().filter(baseArena -> baseArena.getArenaType() == type).filter(arena -> arena.getArenaState() == ArenaState.STARTING && arena.getPlayers().size() < arena.getMaximumPlayers()).collect(Collectors.toList());
              if(!arenas.isEmpty()) {
                BaseArena arena = arenas.get(ThreadLocalRandom.current().nextInt(arenas.size()));
                ArenaManager.joinAttempt((Player) sender, arena);
                return;
              }
              //check waiting arenas -> random
              arenas = ArenaRegistry.getArenas().stream().filter(baseArena -> baseArena.getArenaType() == type).filter(arena -> (arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING)
                  && arena.getPlayers().size() < arena.getMaximumPlayers()).collect(Collectors.toList());
              if(!arenas.isEmpty()) {
                BaseArena arena = arenas.get(ThreadLocalRandom.current().nextInt(arenas.size()));
                ArenaManager.joinAttempt((Player) sender, arena);
                return;
              }
              sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("Commands.No-Free-Arenas"));
              return;
            default:
              sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("Commands.Invalid-Args"));
          }
        }
      });
    }
  }
}
