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

package plugily.projects.buildbattle.commands.arguments.admin.arena;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.buildbattle.arena.ArenaManager;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.ArenaState;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.commands.arguments.ArgumentsRegistry;
import plugily.projects.buildbattle.commands.arguments.data.CommandArgument;
import plugily.projects.buildbattle.commands.arguments.data.LabelData;
import plugily.projects.buildbattle.commands.arguments.data.LabeledCommandArgument;

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
        BaseArena arena = ArenaRegistry.getArena((Player) sender);
        if(arena == null) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("Commands.No-Playing"));
          return;
        }
        if(arena.getArenaState() != ArenaState.ENDING) {
          ArenaManager.stopGame(false, arena);
        }
        sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("Commands.Arguments.Success"));
      }
    });
  }

}
