/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.buildbattle.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import pl.plajer.buildbattle.ConfigPreferences;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.arena.ArenaRegistry;

/**
 * @author Plajer
 * <p>
 * Created at 27.05.2018
 */
public class TabCompletion implements TabCompleter {

  private Main plugin;

  public TabCompletion(Main plugin) {
    this.plugin = plugin;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) {
      return null;
    }
    if (cmd.getName().equalsIgnoreCase("buildbattleadmin") && args.length == 1) {
      return Arrays.asList("addplot", "removeplot", "addnpc", "stop", "list", "forcestart", "reload", "delete", "addvotes", "setvotes");
    }
    if (cmd.getName().equalsIgnoreCase("buildbattle")) {
      if (args.length == 0) {
        List<String> arenaIds = new ArrayList<>();
        for (Arena arena : ArenaRegistry.getArenas()) {
          arenaIds.add(arena.getID());
        }
        return arenaIds;
      }
      if (args.length == 1) {
        if (!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
          return Arrays.asList("join", "leave", "stats", "top", "create", "randomjoin");
        } else {
          return Arrays.asList("join", "leave", "stats", "top", "create");
        }
      }
    }
    return null;
  }
}
