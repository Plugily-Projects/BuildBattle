/*
 *
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2022 Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

package plugily.projects.buildbattle.commands.arguments.admin.arena;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.BuildArena;
import plugily.projects.buildbattle.commands.arguments.ArgumentsRegistry;
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;

import java.util.StringJoiner;

/**
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 */
public class ForcePlayArgument {

  public ForcePlayArgument(ArgumentsRegistry registry) {
    registry.mapArgument("buildbattleadmin", new LabeledCommandArgument("forceplay", "buildbattle.admin.forceplay", CommandArgument.ExecutorType.BOTH,
        new LabelData("/bba forceplay <arena> <theme>", "/bba forceplay <arena> <theme>",
            "&7Start a arena by command \n&6Permission: &7buildbattle.admin.forceplay\n&cNOT FOR PRODUCTION!")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
          //todo translatable
          new MessageBuilder("&cPlease type arena theme!").prefix().send(sender);
          return;
        }
        BaseArena arena;
        int themeArgStart = 1;
        if(!(sender instanceof Player)) {
          if(args.length == 2) {
            //todo translatable
            new MessageBuilder("&cPlease type arena name!").prefix().send(sender);
            return;
          }
          arena = (BaseArena) registry.getPlugin().getArenaRegistry().getArena(args[1]);
          themeArgStart = 2;
        } else {
          arena = (BaseArena) registry.getPlugin().getArenaRegistry().getArena((Player) sender);
        }
        if(arena == null) {
          new MessageBuilder("COMMANDS_NOT_PLAYING").asKey().send(sender);
          return;
        }
        if(!(arena instanceof BuildArena)) {
          //todo translatable
          new MessageBuilder("&cCan't set theme on this arena type!").prefix().send(sender);
          return;
        }
        StringJoiner themeName = new StringJoiner(" ");

        for(int i = themeArgStart; i < args.length; i++)
          themeName.add(args[i]);
        if(arena.getArenaInGameState() == BaseArena.ArenaInGameState.BUILD_TIME || arena.getArenaState() == IArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == IArenaState.STARTING || arena.getArenaState() == IArenaState.FULL_GAME || arena.getArenaInGameState() == BaseArena.ArenaInGameState.THEME_VOTING) {
          if(arena.getPlugin().getThemeManager().isThemeBlacklisted(themeName.toString())) {
            new MessageBuilder("COMMANDS_THEME_BLACKLISTED").asKey().prefix().send(sender);
            return;
          }
          Bukkit.getOnlinePlayers().forEach(player -> {
            registry.getPlugin().getArenaManager().joinAttempt(player, arena);
          });
          arena.setForceStart(true);
          arena.setTheme(themeName.toString());
          new MessageBuilder("IN_GAME_MESSAGES_ADMIN_CHANGED_THEME").asKey().prefix().value(themeName.toString()).arena(arena).sendArena();
        } else {
          new MessageBuilder("&cWrong state to force theme!").prefix().send(sender);
        }
      }
    });
  }

}
