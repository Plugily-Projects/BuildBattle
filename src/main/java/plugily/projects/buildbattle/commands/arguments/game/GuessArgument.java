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

package plugily.projects.buildbattle.commands.arguments.game;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.GuessArena;
import plugily.projects.buildbattle.commands.arguments.ArgumentsRegistry;
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;

import java.util.Arrays;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 30.05.2021
 */
public class GuessArgument {

  public GuessArgument(ArgumentsRegistry registry) {
    registry.mapArgument("buildbattle", new CommandArgument("guess", "", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length < 2) {
          new MessageBuilder("COMMANDS_WRONG_USAGE").asKey().value("/bb guess <word>").send(sender);
          return;
        }

        Player player = (Player) sender;
        BaseArena arena = (BaseArena) registry.getPlugin().getArenaRegistry().getArena(player);

        if(!(arena instanceof GuessArena) || arena.getArenaState() != IArenaState.IN_GAME) {
          new MessageBuilder("COMMANDS_NOT_PLAYING").asKey().player(player).sendPlayer();
          return;
        }

        GuessArena gameArena = (GuessArena) arena;

        if(gameArena.getArenaInGameState() != BaseArena.ArenaInGameState.BUILD_TIME) {
          new MessageBuilder("COMMANDS_NOT_PLAYING").asKey().player(player).sendPlayer();
          return;
        }

        if(gameArena.getWhoGuessed().contains(player)) {
          new MessageBuilder("IN_GAME_MESSAGES_PLOT_GTB_THEME_GUESS_CANT_TALK").asKey().arena(gameArena).player(player).sendPlayer();
          return;
        }

        if(gameArena.getCurrentBuilders().contains(player)) {
          new MessageBuilder("IN_GAME_MESSAGES_PLOT_GTB_THEME_GUESS_BUILDER").asKey().arena(gameArena).player(player).sendPlayer();
          return;
        }

        if(gameArena.getCurrentBBTheme() == null || gameArena.getCurrentBBTheme().getThemes().stream().noneMatch(theme -> theme.equalsIgnoreCase(Arrays.toString(args).split(" ", 2)[1].replace(",", "").replace("]", "")))) {
          return;
        }

        gameArena.broadcastPlayerGuessed(player);
      }
    });
  }


}
