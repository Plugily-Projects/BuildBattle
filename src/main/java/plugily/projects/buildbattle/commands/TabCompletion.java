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

package plugily.projects.buildbattle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.commands.arguments.ArgumentsRegistry;
import plugily.projects.buildbattle.commands.arguments.data.CommandArgument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Plajer
 * <p>
 * Created at 27.05.2018
 */
public class TabCompletion implements TabCompleter {

  private final ArgumentsRegistry registry;

  public TabCompletion(ArgumentsRegistry registry) {
    this.registry = registry;
  }

  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
    List<String> cmds = new ArrayList<>();

    if(cmd.getName().equalsIgnoreCase("buildbattleadmin")) {
      if(args.length == 1) {
        cmds.addAll(registry.getMappedArguments().get(cmd.getName().toLowerCase()).stream().map(CommandArgument::getArgumentName)
            .collect(Collectors.toList()));
      } else if(args.length == 2) {
        if(args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("removeplot")
            || args[0].equalsIgnoreCase("addnpc") || args[0].equalsIgnoreCase("addplot")) {
          cmds.addAll(ArenaRegistry.getArenas().stream().map(BaseArena::getID).collect(Collectors.toList()));
        } else if(args[0].equalsIgnoreCase("settheme")) {
          for(List<String> l : registry.getPlugin().getConfigPreferences().getGameThemes().values()) {
            cmds.addAll(l);
          }
        } else if(args[0].equalsIgnoreCase("votes")) {
          cmds.addAll(Arrays.asList("add", "set"));
        }
      } else if(args.length == 3 && args[0].equalsIgnoreCase("removeplot")) {
        FileConfiguration config = ConfigUtils.getConfig(registry.getPlugin(), "arenas");
        String path = "instances." + ArenaRegistry.getArena(args[1]).getID() + ".plots";
        if (config.isConfigurationSection(path)) {
          cmds.addAll(config.getConfigurationSection(path).getKeys(false));
        }
      }
    }

    if(cmd.getName().equalsIgnoreCase("buildbattle")) {
      if(args.length == 2 && (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("randomjoin"))) {
        if(args[0].equalsIgnoreCase("randomjoin")) {
          cmds.addAll(Arrays.asList("solo", "team", "gtb", "guess_the_build"));
        } else {
          cmds.addAll(ArenaRegistry.getArenas().stream().map(BaseArena::getID).collect(Collectors.toList()));
        }
      } else if(args.length == 1) {
        cmds.addAll(registry.getMappedArguments().get(cmd.getName().toLowerCase()).stream().map(CommandArgument::getArgumentName)
            .collect(Collectors.toList()));
      }
    }

    return cmds.isEmpty() ? null : cmds; // Completes the player names
  }
}
