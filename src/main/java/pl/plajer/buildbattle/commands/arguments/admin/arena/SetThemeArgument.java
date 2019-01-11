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
public class SetThemeArgument {

  public SetThemeArgument(ArgumentsRegistry registry) {
    registry.mapArgument("buildbattleadmin", new LabeledCommandArgument("settheme", "buildbattle.admin.settheme", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/bba settheme &6<theme>", "/bba settheme",
            "&7Set new arena theme\n&6Permission: &7buildbattle.admin.settheme\n&6You can set arena theme only when it started\n&6and only for 20 seconds after start!")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        Arena arena = ArenaRegistry.getArena((Player) sender);
        if (arena == null) {
          sender.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Commands.No-Playing"));
          return;
        }
        if (args.length == 1) {
          //todo translatable
          sender.sendMessage(ChatManager.getPrefix() + ChatManager.colorRawMessage("&cPlease type arena theme!"));
          return;
        }
        if (arena.getArenaState() == ArenaState.IN_GAME && (arena.getBuildTime() - arena.getTimer()) <= 20) {
          if (registry.getPlugin().getConfigPreferences().isThemeBlacklisted(args[1].toLowerCase())) {
            sender.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Commands.Admin-Commands.Theme-Blacklisted"));
            return;
          }
          arena.setTheme(args[1]);
          ChatManager.broadcast(arena, ChatManager.colorMessage("In-Game.Messages.Admin-Messages.Changed-Theme").replace("%THEME%", args[1]));
        } else {
          if (arena.getArenaState() == ArenaState.STARTING) {
            sender.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Commands.Wait-For-Start"));
          } else {
            sender.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("Commands.Arena-Started"));
          }
        }
      }
    });
  }

}
