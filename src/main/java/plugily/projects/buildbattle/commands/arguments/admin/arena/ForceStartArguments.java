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
import plugily.projects.buildbattle.ConfigPreferences;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.ArenaState;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.arena.impl.SoloArena;
import plugily.projects.buildbattle.commands.arguments.ArgumentsRegistry;
import plugily.projects.buildbattle.commands.arguments.data.CommandArgument;
import plugily.projects.buildbattle.commands.arguments.data.LabelData;
import plugily.projects.buildbattle.commands.arguments.data.LabeledCommandArgument;

/**
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 */
public class ForceStartArguments {

  public ForceStartArguments(ArgumentsRegistry registry) {
    registry.mapArgument("buildbattleadmin", new LabeledCommandArgument("forcestart", "buildbattle.admin.forcestart", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/bba forcestart &c[theme]", "/bba forcestart",
            "&7Force starts arena you're in\n&6Permission: &7buildbattle.admin.forcestart")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        BaseArena arena = ArenaRegistry.getArena((Player) sender);
        if(arena == null) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("Commands.No-Playing"));
          return;
        }
        if(args.length == 2 && !sender.hasPermission("buildbattle.admin.forcestart.theme")) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("Commands.No-Permission"));
          return;
        }
        if(arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING) {
          arena.setArenaState(ArenaState.STARTING);
          arena.setForceStart(true);
          arena.setTimer(0);
          if(args.length == 2 && arena instanceof SoloArena) {
            ((SoloArena) arena).setThemeVoteTime(false);
            arena.setTimer(registry.getPlugin().getConfigPreferences().getTimer(ConfigPreferences.TimerType.BUILD, arena));
            arena.setTheme(args[1]);
          }
          registry.getPlugin().getChatManager().broadcast(arena, registry.getPlugin().getChatManager().colorMessage("In-Game.Messages.Admin-Messages.Set-Starting-In-To-0"));
        }
      }
    });
  }

}
