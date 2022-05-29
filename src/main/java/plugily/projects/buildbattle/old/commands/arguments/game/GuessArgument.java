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

package plugily.projects.buildbattle.old.commands.arguments.game;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.buildbattle.old.arena.ArenaRegistry;
import plugily.projects.buildbattle.old.arena.ArenaState;
import plugily.projects.buildbattle.old.arena.impl.GuessTheBuildArena;
import plugily.projects.buildbattle.old.commands.arguments.ArgumentsRegistry;
import plugily.projects.buildbattle.old.commands.arguments.data.CommandArgument;

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
          sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.Invalid-Args"));
          return;
        }

        Player player = (Player) sender;
        BaseArena arena = ArenaRegistry.getArena(player);

        if(arena == null || arena.getArenaType() != BaseArena.ArenaType.GUESS_THE_BUILD || arena.getArenaState() != ArenaState.IN_GAME) {
          player.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("Commands.No-Playing"));
          return;
        }

        GuessTheBuildArena gameArena = (GuessTheBuildArena) arena;

        if(gameArena.getWhoGuessed().contains(player)) {
          player.sendMessage(registry.getPlugin().getChatManager().colorMessage("In-Game.Guess-The-Build.Chat.Cant-Talk-When-Guessed"));
          return;
        }

        if(player == gameArena.getCurrentBuilder()) {
          player.sendMessage(registry.getPlugin().getChatManager().colorMessage("In-Game.Guess-The-Build.Chat.Cant-Talk-When-Building"));
          return;
        }

        if(gameArena.getCurrentTheme() == null || !gameArena.getCurrentTheme().getTheme().equalsIgnoreCase(Arrays.toString(args).split(" ", 2)[1].replace(",", "").replace("]", ""))) {
          return;
        }

        gameArena.broadcastPlayerQuessed(player);
      }
    });
  }


}
