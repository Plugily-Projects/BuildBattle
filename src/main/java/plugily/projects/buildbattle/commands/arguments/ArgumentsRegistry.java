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

package plugily.projects.buildbattle.commands.arguments;

import org.checkerframework.checker.units.qual.Force;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.BuildArena;
import plugily.projects.buildbattle.arena.GuessArena;
import plugily.projects.buildbattle.commands.arguments.admin.ThemeArgument;
import plugily.projects.buildbattle.commands.arguments.admin.arena.AddNpcArgument;
import plugily.projects.buildbattle.commands.arguments.admin.arena.ForcePlayArgument;
import plugily.projects.buildbattle.commands.arguments.admin.arena.SetThemeArgument;
import plugily.projects.buildbattle.commands.arguments.admin.plot.AddPlotArgument;
import plugily.projects.buildbattle.commands.arguments.admin.plot.RemovePlotArgument;
import plugily.projects.buildbattle.commands.arguments.admin.plot.SelectPlotArgument;
import plugily.projects.buildbattle.commands.arguments.game.GuessArgument;
import plugily.projects.buildbattle.commands.arguments.game.MenuArgument;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.commands.arguments.PluginArgumentsRegistry;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author Plajer
 * <p>Created at 24.11.2018
 */
public class ArgumentsRegistry extends PluginArgumentsRegistry {

  public ArgumentsRegistry(Main plugin) {
    super(plugin);
    new AddNpcArgument(this);
    new ForcePlayArgument(this);
    new SetThemeArgument(this);
    new AddPlotArgument(this);
    new RemovePlotArgument(this);
    new SelectPlotArgument(this);
    new MenuArgument(this);
    new GuessArgument(this);
    new ThemeArgument(this);
  }

  @Override
  public List<IPluginArena> getSpecificFilteredArenas(List<IPluginArena> arenas, String filter) {
    switch(filter.toLowerCase(Locale.ENGLISH)) {
      case "gtb":
      case "guessthebuild":
      case "guess_the_build":
        return arenas.stream().filter(GuessArena.class::isInstance).collect(Collectors.toList());
      case "team":
        return arenas.stream().filter(BuildArena.class::isInstance).filter(arena -> ((BuildArena) arena).getArenaType().equals(BaseArena.ArenaType.TEAM)).collect(Collectors.toList());
      case "classic":
      case "solo":
        return arenas.stream().filter(BuildArena.class::isInstance).filter(arena -> ((BuildArena) arena).getArenaType().equals(BaseArena.ArenaType.SOLO)).collect(Collectors.toList());
      case "build":
        default:
        return arenas.stream().filter(BuildArena.class::isInstance).collect(Collectors.toList());
    }
  }

  
}
