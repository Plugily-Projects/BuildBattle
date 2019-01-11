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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import pl.plajer.buildbattle.arena.ArenaManager;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.impl.Arena;
import pl.plajer.buildbattle.commands.arguments.ArgumentsRegistry;
import pl.plajer.buildbattle.commands.arguments.data.CommandArgument;
import pl.plajer.buildbattle.commands.arguments.data.LabelData;
import pl.plajer.buildbattle.commands.arguments.data.LabeledCommandArgument;

/**
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 */
public class ReloadArgument {

  public ReloadArgument(ArgumentsRegistry registry) {
    registry.mapArgument("buildbattleadmin", new LabeledCommandArgument("reload", "buildbattle.admin.reload", CommandArgument.ExecutorType.BOTH,
        new LabelData("/bba reload", "/bba reload", "&7Reload all game arenas\n&7&lThey will be stopped!\n"
            + "&c&lNot recommended!\n&6Permission: &7buildbattle.admin.reload")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        for (Arena arena : ArenaRegistry.getArenas()) {
          ArenaManager.stopGame(true, arena);
        }
        registry.getPlugin().getConfigPreferences().loadOptions();
        ArenaRegistry.registerArenas();
        sender.sendMessage(ChatColor.GREEN + "Plugin reloaded!");
      }
    });
  }

}
