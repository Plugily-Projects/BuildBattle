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

package pl.plajer.buildbattle.commands.arguments.admin.arena;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.plajer.buildbattle.arena.ArenaManager;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.ArenaState;
import pl.plajer.buildbattle.arena.impl.Arena;
import pl.plajer.buildbattle.commands.arguments.ArgumentsRegistry;
import pl.plajer.buildbattle.commands.arguments.data.CommandArgument;
import pl.plajer.buildbattle.commands.arguments.data.LabelData;
import pl.plajer.buildbattle.commands.arguments.data.LabeledCommandArgument;
import pl.plajer.buildbattle.handlers.ChatManager;

/**
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 */
public class StopArgument {

  public StopArgument(ArgumentsRegistry registry) {
    registry.mapArgument("buildbattleadmin", new LabeledCommandArgument("stop", "buildbattle.admin.stop", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/bba stop", "/bba stop",
            "&7Stops the arena you're in\n&7&lYou must be in target arena!\n&6Permission: &7buildbattle.admin.stop")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        Arena arena = ArenaRegistry.getArena((Player) sender);
        if (arena == null) {
          sender.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Commands.Not-Playing"));
          return;
        }
        if (arena.getArenaState() != ArenaState.ENDING) {
          ArenaManager.stopGame(false, ArenaRegistry.getArena((Player) sender));
        }
      }
    });
  }

}
