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

package plugily.projects.buildbattle.boot;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.BuildArena;
import plugily.projects.buildbattle.arena.GuessArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.handlers.themes.BBTheme;
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.placeholder.Placeholder;
import plugily.projects.minigamesbox.classic.handlers.placeholder.PlaceholderManager;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 15.10.2022
 */
public class PlaceholderInitializer {

  private final Main plugin;

  public PlaceholderInitializer(Main plugin) {
    this.plugin = plugin;
    registerPlaceholders();
  }

  private void registerPlaceholders() {
    PlaceholderManager placeholderManager = plugin.getPlaceholderManager();
    ArenaRegistry arenaRegistry = plugin.getArenaRegistry();

    placeholderManager.registerPlaceholder(new Placeholder("theme", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, IPluginArena arena) {
        return getTheme(player, arena);
      }

      @Override
      public String getValue(IPluginArena arena) {
        return getTheme(arena);
      }

      @Nullable
      private String getTheme(IPluginArena arena) {
        BaseArena pluginArena = arenaRegistry.getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        String theme = pluginArena.getTheme();
        if(theme == null) {
          theme = new MessageBuilder("SCOREBOARD_THEME_UNKNOWN").asKey().build();
        }
        return theme;
      }

      @Nullable
      private String getTheme(Player player, IPluginArena arena) {
        String theme = getTheme(arena);
        BaseArena pluginArena = arenaRegistry.getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        if(!(pluginArena instanceof GuessArena)) {
          return theme;
        }
        if(theme.equalsIgnoreCase(new MessageBuilder("SCOREBOARD_THEME_UNKNOWN").asKey().build())) {
          if(((GuessArena) pluginArena).getCurrentBuilders().contains(player) || ((GuessArena) pluginArena).getWhoGuessed().contains(player)) {
            BBTheme guessTheme = ((GuessArena) pluginArena).getCurrentBBTheme();
            theme = guessTheme == null ? new MessageBuilder("SCOREBOARD_THEME_UNKNOWN").asKey().build() : guessTheme.getTheme();
          }
        }
        return theme;
      }
    });

    placeholderManager.registerPlaceholder(new Placeholder("difficulty", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, IPluginArena arena) {
        return getTheme(arena);
      }

      @Override
      public String getValue(IPluginArena arena) {
        return getTheme(arena);
      }

      @Nullable
      private String getTheme(IPluginArena arena) {
        BaseArena pluginArena = arenaRegistry.getArena(arena.getId());
        if(pluginArena instanceof GuessArena) {
          BBTheme theme = ((GuessArena) pluginArena).getCurrentBBTheme();
          if(theme == null) {
            return null;
          }
          return theme.getDifficulty().name();
        }
        return null;
      }
    });

    placeholderManager.registerPlaceholder(new Placeholder("difficulty_points_reward", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, IPluginArena arena) {
        return getTheme(arena);
      }

      @Override
      public String getValue(IPluginArena arena) {
        return getTheme(arena);
      }

      @Nullable
      private String getTheme(IPluginArena arena) {
        BaseArena pluginArena = arenaRegistry.getArena(arena.getId());
        if(pluginArena instanceof GuessArena) {
          BBTheme theme = ((GuessArena) pluginArena).getCurrentBBTheme();
          if(theme == null) {
            return "0";
          }
          return Integer.toString(theme.getDifficulty().getPointsReward());
        }
        return null;
      }
    });

    placeholderManager.registerPlaceholder(new Placeholder("builder", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, IPluginArena arena) {
        return getBuilder(arena);
      }

      @Override
      public String getValue(IPluginArena arena) {
        return getBuilder(arena);
      }

      @Nullable
      private String getBuilder(IPluginArena arena) {
        BaseArena pluginArena = arenaRegistry.getArena(arena.getId());
        if(!(pluginArena instanceof GuessArena)) {
          return null;
        }
        if(((GuessArena) pluginArena).getBuildPlot() == null || ((GuessArena) pluginArena).getBuildPlot().getMembers().isEmpty()) {
          return "???";
        }
        return ((GuessArena) pluginArena).getBuildPlot().getFormattedMembers();
      }
    });

    placeholderManager.registerPlaceholder(new Placeholder("type", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, IPluginArena arena) {
        return getType(arena);
      }

      @Override
      public String getValue(IPluginArena arena) {
        return getType(arena);
      }

      @Nullable
      private String getType(IPluginArena arena) {
        BaseArena pluginArena = arenaRegistry.getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        return pluginArena.getArenaType().toString();
      }
    });
    placeholderManager.registerPlaceholder(new Placeholder("type_pretty", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, IPluginArena arena) {
        return getType(arena);
      }

      @Override
      public String getValue(IPluginArena arena) {
        return getType(arena);
      }

      @Nullable
      private String getType(IPluginArena arena) {
        BaseArena pluginArena = arenaRegistry.getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        return pluginArena.getArenaType().getPrefix();
      }
    });
    for(int i = 1; i <= 32; i++) {
      final int number = i;
      placeholderManager.registerPlaceholder(new Placeholder("place_member_" + number, Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
        @Override
        public String getValue(Player player, IPluginArena arena) {
          return getPlace(arena);
        }

        @Override
        public String getValue(IPluginArena arena) {
          return getPlace(arena);
        }

        @Nullable
        private String getPlace(IPluginArena arena) {
          BaseArena pluginArena = arenaRegistry.getArena(arena.getId());
          if(pluginArena == null) {
            return null;
          }
          if(pluginArena.getArenaInGameState() != BaseArena.ArenaInGameState.PLOT_VOTING && pluginArena.getArenaState() != IArenaState.ENDING) {
            return "???";
          }
          List<Plot> plotRanking = pluginArena.getPlotManager().getTopPlotsOrder();
          if(plotRanking.size() < number) {
            return "???";
          }
          StringBuilder members = new StringBuilder();
          plotRanking.get(number - 1).getMembers().forEach(player -> members.append(player.getName()).append(" & "));
          members.delete(members.length() - 3, members.length());
          return new MessageBuilder(members.toString()).build();
        }
      });

      placeholderManager.registerPlaceholder(new Placeholder("place_points_" + number, Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
        @Override
        public String getValue(Player player, IPluginArena arena) {
          return getPlace(arena);
        }

        @Override
        public String getValue(IPluginArena arena) {
          return getPlace(arena);
        }

        @Nullable
        private String getPlace(IPluginArena arena) {
          BaseArena pluginArena = arenaRegistry.getArena(arena.getId());
          if(pluginArena == null) {
            return null;
          }
          if(pluginArena.getArenaInGameState() != BaseArena.ArenaInGameState.PLOT_VOTING && pluginArena.getArenaState() != IArenaState.ENDING) {
            return "???";
          }
          List<Plot> plotRanking = pluginArena.getPlotManager().getTopPlotsOrder();
          if(plotRanking.size() < number) {
            return "???";
          }
          return String.valueOf(plotRanking.get(number - 1).getPoints());
        }
      });
    }
    placeholderManager.registerPlaceholder(new Placeholder("ingame_state", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, IPluginArena arena) {
        return getState(arena);
      }

      @Override
      public String getValue(IPluginArena arena) {
        return getState(arena);
      }

      @Nullable
      private String getState(IPluginArena arena) {
        BaseArena pluginArena = arenaRegistry.getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        if(pluginArena.getArenaInGameState() == null) {
          return null;
        }
        return pluginArena.getArenaInGameState().toString();
      }
    });

    placeholderManager.registerPlaceholder(new Placeholder("ingame_state_pretty", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, IPluginArena arena) {
        return getState(arena);
      }

      @Override
      public String getValue(IPluginArena arena) {
        return getState(arena);
      }

      @Nullable
      private String getState(IPluginArena arena) {
        BaseArena pluginArena = arenaRegistry.getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        if(pluginArena.getArenaInGameState() == null) {
          return null;
        }
        return pluginArena.getArenaInGameState().getPrefix();
      }
    });


    placeholderManager.registerPlaceholder(new Placeholder("teammates", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, IPluginArena arena) {
        return getMembers(player, arena);
      }

      @Nullable
      private String getMembers(Player player, IPluginArena arena) {
        BaseArena pluginArena = arenaRegistry.getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        if(!(pluginArena instanceof BuildArena)) {
          return new MessageBuilder("IN_GAME_MESSAGES_PLOT_NOBODY").asKey().build();
        }
        Plot plot = pluginArena.getPlotManager().getPlot(player);

        if(plot != null) {
          if(plot.getMembers().size() > 1) {
            StringBuilder members = new StringBuilder();
            plot.getMembers().forEach(p -> members.append(p.getName()).append(" & "));
            members.delete(members.length() - 3, members.length());
            return new MessageBuilder(members.toString()).build();
          }
        }
        return new MessageBuilder("IN_GAME_MESSAGES_PLOT_NOBODY").asKey().build();
      }
    });

    placeholderManager.registerPlaceholder(new Placeholder("summary_player", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, IPluginArena arena) {
        return getSummary(arena, player);
      }

      @Nullable
      private String getSummary(IPluginArena arena, Player player) {
        BaseArena pluginArena = arenaRegistry.getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        Plot plot = pluginArena.getWinnerPlot();
        if(plot != null && !plot.getMembers().isEmpty() && plot.getMembers().contains(player)) {
          return new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_WIN").asKey().arena(pluginArena).build();
        }
        return new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_LOSE").asKey().arena(pluginArena).build();
      }
    });

    placeholderManager.registerPlaceholder(new Placeholder("summary_place_own", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, IPluginArena arena) {
        return getSummary(arena, player);
      }

      @Nullable
      private String getSummary(IPluginArena arena, Player player) {
        BaseArena pluginArena = arenaRegistry.getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        if(pluginArena.getArenaInGameState() != BaseArena.ArenaInGameState.PLOT_VOTING && pluginArena.getArenaState() != IArenaState.ENDING) {
          return null;
        }
        List<Plot> plotRanking = pluginArena.getPlotManager().getTopPlotsOrder();
        List<Plot> potentialPlot = plotRanking.stream().filter(plot -> plot.getMembers().contains(player)).collect(Collectors.toList());
        if(potentialPlot.isEmpty()) {
          return new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_OWN").asKey().integer(0).arena(pluginArena).build();
        }
        int plotIndex = plotRanking.indexOf(potentialPlot.get(0));
        if(plotIndex == -1) {
          return new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_OWN").asKey().integer(0).arena(pluginArena).build();
        }
        return new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_OWN").asKey().integer(plotIndex + 1).arena(pluginArena).build();
      }
    });


    placeholderManager.registerPlaceholder(new Placeholder("summary", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, IPluginArena arena) {
        return getSummary(arena);
      }

      @Override
      public String getValue(IPluginArena arena) {
        return getSummary(arena);
      }

      @Nullable
      private String getSummary(IPluginArena arena) {
        BaseArena pluginArena = arenaRegistry.getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        Plot plot = pluginArena.getWinnerPlot();
        if(plot != null && !plot.getMembers().isEmpty()) {
          return new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_WINNER").asKey().arena(pluginArena).build();
        }
        return null;
      }
    });
    placeholderManager.registerPlaceholder(new Placeholder("summary_place_list", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, IPluginArena arena) {
        return getSummary(arena);
      }

      @Override
      public String getValue(IPluginArena arena) {
        return getSummary(arena);
      }

      @Nullable
      private String getSummary(IPluginArena arena) {
        BaseArena pluginArena = arenaRegistry.getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        if(pluginArena.getArenaInGameState() != BaseArena.ArenaInGameState.PLOT_VOTING && pluginArena.getArenaState() != IArenaState.ENDING) {
          return null;
        }
        int places = pluginArena.getPlotManager().getTopPlotsOrder().size();
        if(places > 16) {
          places = 16;
        }
        StringBuilder placeSummary = new StringBuilder();
        for(int i = 1; i <= places; i++) {
          //number = place, value = members + points
          placeSummary.append("\n").append(new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_PLACE").asKey().integer(i).value("%arena_place_member_" + i + "% (%arena_place_points_" + i + "%)").arena(pluginArena).build());
        }
        return placeSummary.toString();
      }
    });
    placeholderManager.registerPlaceholder(new Placeholder("gtb_rounds", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, IPluginArena arena) {
        return getSummary(arena);
      }

      @Override
      public String getValue(IPluginArena arena) {
        return getSummary(arena);
      }

      @Nullable
      private String getSummary(IPluginArena arena) {
        BaseArena pluginArena = arenaRegistry.getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        return String.valueOf(pluginArena.getPlotList().size() * pluginArena.getArenaOption("GTB_ROUNDS_PER_PLOT"));
      }
    });

  }
}
