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

package plugily.projects.buildbattle.commands.arguments.admin;

import org.bukkit.command.CommandSender;
import plugily.projects.buildbattle.arena.ArenaRegistry;
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
public class ListArenasArgument {

  public ListArenasArgument(ArgumentsRegistry registry) {
    registry.mapArgument("buildbattleadmin", new LabeledCommandArgument("list", "buildbattle.admin.list", CommandArgument.ExecutorType.BOTH,
        new LabelData("/bba list", "/bba list",
            "&7Shows list with all loaded arenas\n&6Permission: &7buildbattle.admin.list")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.Admin-Commands.List-Command.Header"));
        int i = 0;
        for(BaseArena arena : ArenaRegistry.getArenas()) {
          sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.Admin-Commands.List-Command.Format").replace("%arena%", arena.getID())
              .replace("%status%", arena.getArenaState().getPlaceholder()).replace("%players%", String.valueOf(arena.getPlayers().size()))
              .replace("%maxplayers%", String.valueOf(arena.getMaximumPlayers())));
          i++;
        }
        if(i == 0) {
          sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.Admin-Commands.List-Command.No-Arenas"));
          sender.sendMessage(registry.getPlugin().getChatManager().colorRawMessage("&e&lTIP: &7You can get free maps with configs at our wiki! Just head to https://wiki.plugily.xyz/minecraft/buildbattle/free_maps.php"));
        }
      }
    });
  }

}
