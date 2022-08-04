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

package plugily.projects.buildbattle;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import plugily.projects.buildbattle.arena.ArenaEvents;
import plugily.projects.buildbattle.arena.ArenaManager;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.BuildArena;
import plugily.projects.buildbattle.arena.GuessArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.arena.managers.plots.PlotMenuHandler;
import plugily.projects.buildbattle.arena.vote.VoteEvents;
import plugily.projects.buildbattle.arena.vote.VoteItems;
import plugily.projects.buildbattle.commands.arguments.ArgumentsRegistry;
import plugily.projects.buildbattle.events.OptionMenuEvents;
import plugily.projects.buildbattle.handlers.menu.OptionsMenuHandler;
import plugily.projects.buildbattle.handlers.menu.OptionsRegistry;
import plugily.projects.buildbattle.handlers.setup.SetupCategoryManager;
import plugily.projects.buildbattle.handlers.themes.ThemeManager;
import plugily.projects.buildbattle.handlers.misc.BlacklistManager;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.api.StatisticType;
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.options.ArenaOption;
import plugily.projects.minigamesbox.classic.handlers.language.Message;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.permissions.PermissionCategory;
import plugily.projects.minigamesbox.classic.handlers.placeholder.Placeholder;
import plugily.projects.minigamesbox.classic.handlers.reward.RewardType;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.categories.PluginSetupCategoryManager;
import plugily.projects.minigamesbox.classic.preferences.ConfigOption;
import plugily.projects.minigamesbox.classic.utils.services.locale.Locale;
import plugily.projects.minigamesbox.classic.utils.services.locale.LocaleRegistry;
import plugily.projects.minigamesbox.classic.utils.services.metrics.Metrics;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tom on 17/08/2015.
 * Updated by Tigerpanzer_02 on 03.12.2021
 */
//TODO OptionMenu change to InventoryLibrary
public class Main extends PluginMain {

  private VoteItems voteItems;
  private ThemeManager themeManager;
  private BlacklistManager blacklistManager;
  private OptionsRegistry optionsRegistry;
  private ArenaRegistry arenaRegistry;
  private ArenaManager arenaManager;
  private ArgumentsRegistry argumentsRegistry;
  private PlotMenuHandler plotMenuHandler;


  @TestOnly
  public Main() {
    super();
  }

  @TestOnly
  protected Main(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
    super(loader, description, dataFolder, file);
  }

  @Override
  public void onEnable() {
    long start = System.currentTimeMillis();
    registerLocales();
    super.onEnable();
    getDebugger().debug("[System] [Plugin] Initialization start");
    registerPlaceholders();
    addMessages();
    addAdditionalValues();
    initializePluginClasses();
    getDebugger().debug("Full {0} plugin enabled", getName());
    getDebugger().debug("[System] [Plugin] Initialization finished took {0}ms", System.currentTimeMillis() - start);
  }

  public void initializePluginClasses() {
    addFileName("themes");
    addFileName("vote_items");
    addArenaOptions();
    blacklistManager = new BlacklistManager(this);
    BaseArena.init(this);
    new ArenaEvents(this);
    arenaManager = new ArenaManager(this);
    arenaRegistry = new ArenaRegistry(this);
    arenaRegistry.registerArenas();
    getSignManager().loadSigns();
    getSignManager().updateSigns();
    argumentsRegistry = new ArgumentsRegistry(this);
    voteItems = new VoteItems(this);
    new VoteEvents(this);
    themeManager = new ThemeManager(this);
    plotMenuHandler = new PlotMenuHandler(this);
    optionsRegistry = new OptionsRegistry(this);
    optionsRegistry.registerOptions();
    new OptionsMenuHandler(this);
    new OptionMenuEvents(this);
    addPluginMetrics();
  }

  private void addPluginMetrics() {
    getMetrics().addCustomChart(new Metrics.SimplePie("hooked_addons", () -> {
      if(getServer().getPluginManager().getPlugin("BuildBattle-Extras") != null) {
        return "Extras";
      }
      return "None";
    }));
  }

  private void addArenaOptions() {
    getArenaOptionManager().registerArenaOption("IN_PLOT_CHECKER", new ArenaOption("null", 0, true));
    getArenaOptionManager().registerArenaOption("PLOT_MEMBER_SIZE", new ArenaOption("plotmembersize", 1, true));
  }

  public void addAdditionalValues() {
    getConfigPreferences().registerOption("MOB_SPAWN", new ConfigOption("Mob.Spawn", false));
    getConfigPreferences().registerOption("HEAD_MENU_CUSTOM", new ConfigOption("Head-Menu.Custom", false));
    getConfigPreferences().registerOption("REPORT_COMMANDS", new ConfigOption("Report.Commands", false));
    getConfigPreferences().registerOption("HIDE_PLOT_OWNER", new ConfigOption("Hide-Plot-Owner", false));

    getStatsStorage().registerStatistic("BLOCKS_PLACED", new StatisticType("blocks_placed", true, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("BLOCKS_BROKEN", new StatisticType("blocks_broken", true, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("POINTS_HIGHEST", new StatisticType("points_highest", true, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("POINTS_HIGHEST_WIN", new StatisticType("points_highest_win", true, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("POINTS_TOTAL", new StatisticType("points_total", true, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("PARTICLES_USED", new StatisticType("particles_used", true, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("SUPER_VOTES", new StatisticType("super_votes", true, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("LOCAL_POINTS", new StatisticType("local_points", false, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("LOCAL_POINTS_GTB", new StatisticType("local_points_gtb", false, "int(11) NOT NULL DEFAULT '0'"));
    getStatsStorage().registerStatistic("REPORTS_TRIGGERED", new StatisticType("reports_triggered", false, "int(11) NOT NULL DEFAULT '0'"));

    getPermissionsManager().registerPermissionCategory("POINTS_BOOSTER", new PermissionCategory("Points-Boost", null));
    getPermissionsManager().registerPermissionCategory("VOTING_BOOSTER", new PermissionCategory("Voting-Boost", null));

    getRewardsHandler().registerRewardType("GUESS", new RewardType("guessed"));
    getRewardsHandler().registerRewardType("GUESS_ALL", new RewardType("guessed-all"));
    getRewardsHandler().registerRewardType("VOTE", new RewardType("voted"));
    getRewardsHandler().registerRewardType("VOTE_ALL", new RewardType("voted-all"));
    getRewardsHandler().registerRewardType("REPORT", new RewardType("report"));
    getRewardsHandler().registerRewardType("PLACE", new RewardType("place"));

    getSpecialItemManager().registerSpecialItem("OPTIONS_MENU", "Options-Menu");
    getSpecialItemManager().registerSpecialItem("PLOT_SELECTOR", "Plot-Selector");
  }

  public void registerPlaceholders() {
    getPlaceholderManager().registerPlaceholder(new Placeholder("theme", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getTheme(arena);
      }

      @Override
      public String getValue(PluginArena arena) {
        return getTheme(arena);
      }

      @Nullable
      private String getTheme(PluginArena arena) {
        BaseArena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        String theme = pluginArena.getTheme();
        if(theme.equalsIgnoreCase("theme")) {
          theme = new MessageBuilder("SCOREBOARD_THEME_UNKNOWN").asKey().build();
        }
        return theme;
      }
    });

    getPlaceholderManager().registerPlaceholder(new Placeholder("difficulty", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getTheme(arena);
      }

      @Override
      public String getValue(PluginArena arena) {
        return getTheme(arena);
      }

      @Nullable
      private String getTheme(PluginArena arena) {
        BaseArena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena instanceof GuessArena) {
          return ((GuessArena) pluginArena).getCurrentTheme().getDifficulty().name();
        }
        return null;
      }
    });

    getPlaceholderManager().registerPlaceholder(new Placeholder("difficulty_pointsreward", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getTheme(arena);
      }

      @Override
      public String getValue(PluginArena arena) {
        return getTheme(arena);
      }

      @Nullable
      private String getTheme(PluginArena arena) {
        BaseArena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena instanceof GuessArena) {
          return Integer.toString(((GuessArena) pluginArena).getCurrentTheme().getDifficulty().getPointsReward());
        }
        return null;
      }
    });

    getPlaceholderManager().registerPlaceholder(new Placeholder("builder", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getBuilder(arena);
      }

      @Override
      public String getValue(PluginArena arena) {
        return getBuilder(arena);
      }

      @Nullable
      private String getBuilder(PluginArena arena) {
        BaseArena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(!(pluginArena instanceof GuessArena)) {
          return null;
        }
        return ((GuessArena) pluginArena).getCurrentBuilder().getName();
      }
    });

    getPlaceholderManager().registerPlaceholder(new Placeholder("type", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getType(arena);
      }

      @Override
      public String getValue(PluginArena arena) {
        return getType(arena);
      }

      @Nullable
      private String getType(PluginArena arena) {
        BaseArena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        return pluginArena.getArenaType().toString();
      }
    });
    getPlaceholderManager().registerPlaceholder(new Placeholder("type_pretty", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getType(arena);
      }

      @Override
      public String getValue(PluginArena arena) {
        return getType(arena);
      }

      @Nullable
      private String getType(PluginArena arena) {
        BaseArena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        return pluginArena.getArenaType().getPrefix();
      }
    });
    for(int i = 0; i <= 32; i++) {
      final int number = i;
      getPlaceholderManager().registerPlaceholder(new Placeholder("place_member_" + number, Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
        @Override
        public String getValue(Player player, PluginArena arena) {
          return getPlace(arena);
        }

        @Override
        public String getValue(PluginArena arena) {
          return getPlace(arena);
        }

        @Nullable
        private String getPlace(PluginArena arena) {
          BaseArena pluginArena = getArenaRegistry().getArena(arena.getId());
          if(pluginArena == null) {
            return null;
          }
          if(pluginArena instanceof BuildArena) {
            if(pluginArena.getArenaInGameStage() != BaseArena.ArenaInGameStage.PLOT_VOTING || pluginArena.getArenaState() != ArenaState.ENDING) {
              return null;
            }
            return ((BuildArena) pluginArena).getTopList().get(number).toString();
          }
          if(pluginArena instanceof GuessArena) {
            if(pluginArena.getArenaInGameStage() != BaseArena.ArenaInGameStage.PLOT_VOTING || pluginArena.getArenaState() != ArenaState.ENDING) {
              return null;
            }
            return new ArrayList<>(((GuessArena) pluginArena).getPlayersPoints().entrySet()).get(number).getKey().getName();
          }
          return pluginArena.getArenaInGameStage().toString();
        }
      });

      getPlaceholderManager().registerPlaceholder(new Placeholder("place_points_" + number, Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
        @Override
        public String getValue(Player player, PluginArena arena) {
          return getPlace(arena);
        }

        @Override
        public String getValue(PluginArena arena) {
          return getPlace(arena);
        }

        @Nullable
        private String getPlace(PluginArena arena) {
          BaseArena pluginArena = getArenaRegistry().getArena(arena.getId());
          if(pluginArena == null) {
            return null;
          }
          if(pluginArena instanceof BuildArena) {
            if(pluginArena.getArenaInGameStage() != BaseArena.ArenaInGameStage.PLOT_VOTING || pluginArena.getArenaState() != ArenaState.ENDING) {
              return null;
            }
            return ((BuildArena) pluginArena).getTopList().get(number).toString();
          }
          if(pluginArena instanceof GuessArena) {
            if(pluginArena.getArenaInGameStage() != BaseArena.ArenaInGameStage.PLOT_VOTING || pluginArena.getArenaState() != ArenaState.ENDING) {
              return null;
            }
            return String.valueOf(new ArrayList<>(((GuessArena) pluginArena).getPlayersPoints().entrySet()).get(number).getValue());
          }
          return pluginArena.getArenaInGameStage().toString();
        }
      });
    }
    getPlaceholderManager().registerPlaceholder(new Placeholder("ingame_stage", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getStage(arena);
      }

      @Override
      public String getValue(PluginArena arena) {
        return getStage(arena);
      }

      @Nullable
      private String getStage(PluginArena arena) {
        BaseArena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        return pluginArena.getArenaInGameStage().toString();
      }
    });

    getPlaceholderManager().registerPlaceholder(new Placeholder("ingame_stage", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getStage(arena);
      }

      @Override
      public String getValue(PluginArena arena) {
        return getStage(arena);
      }

      @Nullable
      private String getStage(PluginArena arena) {
        BaseArena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        return pluginArena.getArenaInGameStage().toString();
      }
    });

    getPlaceholderManager().registerPlaceholder(new Placeholder("ingame_stage_pretty", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getStage(arena);
      }

      @Override
      public String getValue(PluginArena arena) {
        return getStage(arena);
      }

      @Nullable
      private String getStage(PluginArena arena) {
        BaseArena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        return pluginArena.getArenaInGameStage().getPrefix();
      }
    });


    getPlaceholderManager().registerPlaceholder(new Placeholder("team", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getMembers(player, arena);
      }

      @Nullable
      private String getMembers(Player player, PluginArena arena) {
        BaseArena pluginArena = getArenaRegistry().getArena(arena.getId());
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
            members.deleteCharAt(members.length() - 2);
            return new MessageBuilder(members.toString()).build();
          }
        }
        return new MessageBuilder("IN_GAME_MESSAGES_PLOT_NOBODY").asKey().build();
      }
    });

    getPlaceholderManager().registerPlaceholder(new Placeholder("summary_player", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getSummary(arena, player);
      }

      @Override
      public String getValue(PluginArena arena) {
        return "";
      }

      @Nullable
      private String getSummary(PluginArena arena, Player player) {
        BaseArena pluginArena = arenaRegistry.getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        if(pluginArena instanceof BuildArena) {
          Plot plot = ((BuildArena) pluginArena).getWinnerPlot();
          if(plot != null && !plot.getMembers().isEmpty() && plot.getMembers().contains(player)) {
            return new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_WIN").asKey().arena(pluginArena).build();
          }
        }
        if(pluginArena instanceof GuessArena) {
          if(((GuessArena) pluginArena).getWinner() == player) {
            return new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_WIN").asKey().arena(pluginArena).build();
          }
        }
        return new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_LOSE").asKey().arena(pluginArena).build();
      }
    });

    getPlaceholderManager().registerPlaceholder(new Placeholder("summary_place_own", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getSummary(arena, player);
      }

      @Override
      public String getValue(PluginArena arena) {
        return "";
      }

      @Nullable
      private String getSummary(PluginArena arena, Player player) {
        BaseArena pluginArena = arenaRegistry.getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        if(pluginArena instanceof BuildArena) {
          if(pluginArena.getArenaInGameStage() != BaseArena.ArenaInGameStage.PLOT_VOTING || pluginArena.getArenaState() != ArenaState.ENDING) {
            return null;
          }
          List<List<Player>> playerList = new ArrayList<>(((BuildArena) pluginArena).getTopList().values());
          for(int playerPlace = 0; playerPlace < playerList.size(); playerPlace++) {
            if(playerList.get(playerPlace).contains(player)) {
              return new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_OWN").asKey().integer(playerPlace).arena(pluginArena).build();
            }
          }
        }
        if(pluginArena instanceof GuessArena) {
          if(pluginArena.getArenaInGameStage() != BaseArena.ArenaInGameStage.PLOT_VOTING || pluginArena.getArenaState() != ArenaState.ENDING) {
            return null;
          }
          int playerPlace = new ArrayList<>(((GuessArena) pluginArena).getPlayersPoints().keySet()).indexOf(player);
          if(playerPlace != -1) {
            return new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_OWN").asKey().integer(playerPlace).arena(pluginArena).build();
          }
        }
        return new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_OWN").asKey().integer(0).arena(pluginArena).build();
      }
    });


    getPlaceholderManager().registerPlaceholder(new Placeholder("summary", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getSummary(arena);
      }

      @Override
      public String getValue(PluginArena arena) {
        return getSummary(arena);
      }

      @Nullable
      private String getSummary(PluginArena arena) {
        BaseArena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        if(pluginArena instanceof BuildArena) {
          Plot plot = ((BuildArena) pluginArena).getWinnerPlot();
          if(plot != null && !plot.getMembers().isEmpty()) {
            return new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_WINNER").asKey().arena(pluginArena).build();
          }
        }
        if(pluginArena instanceof GuessArena) {
          if(((GuessArena) pluginArena).getWinner() != null) {
            return new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_WINNER").asKey().arena(pluginArena).build();
          }
        }
        return null;
      }
    });
    getPlaceholderManager().registerPlaceholder(new Placeholder("summary_place_list", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return getSummary(arena);
      }

      @Override
      public String getValue(PluginArena arena) {
        return getSummary(arena);
      }

      @Nullable
      private String getSummary(PluginArena arena) {
        BaseArena pluginArena = getArenaRegistry().getArena(arena.getId());
        if(pluginArena == null) {
          return null;
        }
        int places = pluginArena.getPlayersLeft().size();
        if(places > 16) {
          places = 16;
        }
        StringBuilder placeSummary = new StringBuilder();
        for(int i = 1; i < places; i++) {
          //number = place, value = members + points
          placeSummary.append("\n").append(new MessageBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_PLACE").asKey().integer(i).value("%arena_place_members_" + i + "% (%arena_place_points_" + i + "%)").arena(pluginArena).build());
        }
        return placeSummary.toString();
      }
    });


  }

  public void registerLocales() {
    Arrays.asList(new Locale("Chinese (Traditional)", "简体中文", "zh_HK", "POEditor contributors", Arrays.asList("中文(傳統)", "中國傳統", "chinese_traditional", "zh")),
            new Locale("Chinese (Simplified)", "简体中文", "zh_CN", "POEditor contributors", Arrays.asList("简体中文", "中文", "chinese", "chinese_simplified", "cn")),
            new Locale("Czech", "Český", "cs_CZ", "POEditor contributors", Arrays.asList("czech", "cesky", "český", "cs")),
            new Locale("Dutch", "Nederlands", "nl_NL", "POEditor contributors", Arrays.asList("dutch", "nederlands", "nl")),
            new Locale("English", "English", "en_GB", "Tigerpanzer_02", Arrays.asList("default", "english", "en")),
            new Locale("French", "Français", "fr_FR", "POEditor contributors", Arrays.asList("french", "francais", "français", "fr")),
            new Locale("German", "Deutsch", "de_DE", "Tigerkatze and POEditor contributors", Arrays.asList("deutsch", "german", "de")),
            new Locale("Hungarian", "Magyar", "hu_HU", "POEditor contributors", Arrays.asList("hungarian", "magyar", "hu")),
            new Locale("Indonesian", "Indonesia", "id_ID", "POEditor contributors", Arrays.asList("indonesian", "indonesia", "id")),
            new Locale("Italian", "Italiano", "it_IT", "POEditor contributors", Arrays.asList("italian", "italiano", "it")),
            new Locale("Korean", "한국의", "ko_KR", "POEditor contributors", Arrays.asList("korean", "한국의", "kr")),
            new Locale("Lithuanian", "Lietuviešu", "lt_LT", "POEditor contributors", Arrays.asList("lithuanian", "lietuviešu", "lietuviesu", "lt")),
            new Locale("Polish", "Polski", "pl_PL", "Plajer", Arrays.asList("polish", "polski", "pl")),
            new Locale("Portuguese (BR)", "Português Brasileiro", "pt_BR", "POEditor contributors", Arrays.asList("brazilian", "brasil", "brasileiro", "pt-br", "pt_br")),
            new Locale("Romanian", "Românesc", "ro_RO", "POEditor contributors", Arrays.asList("romanian", "romanesc", "românesc", "ro")),
            new Locale("Russian", "Pусский", "ru_RU", "POEditor contributors", Arrays.asList("russian", "pусский", "pyccknn", "russkiy", "ru")),
            new Locale("Spanish", "Español", "es_ES", "POEditor contributors", Arrays.asList("spanish", "espanol", "español", "es")),
            new Locale("Thai", "Thai", "th_TH", "POEditor contributors", Arrays.asList("thai", "th")),
            new Locale("Turkish", "Türk", "tr_TR", "POEditor contributors", Arrays.asList("turkish", "turk", "türk", "tr")),
            new Locale("Vietnamese", "Việt", "vn_VN", "POEditor contributors", Arrays.asList("vietnamese", "viet", "việt", "vn")))
        .forEach(LocaleRegistry::registerLocale);
  }

  public void addMessages() {
    getMessageManager().registerMessage("COMMANDS_THEME_BLACKLISTED", new Message("Commands.Theme-Blacklisted", ""));
    getMessageManager().registerMessage("COMMANDS_ADMIN_ADDED_PLOT", new Message("Commands.Admin.Added-Plot", ""));
    getMessageManager().registerMessage("SCOREBOARD_THEME_UNKNOWN", new Message("Scoreboard.Theme-Unknown", ""));
    // scoreboard ingame classic/teams/guess-the-build/guess-the-build-waiting | ending classic/guess-the-build
    getMessageManager().registerMessage("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_WINNER", new Message("In-Game.Messages.Game-End.Placeholders.Winner", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_PLACE", new Message("In-Game.Messages.Game-End.Placeholders.Place", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_OWN", new Message("In-Game.Messages.Game-End.Placeholders.Own", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ADMIN_CHANGED_THEME", new Message("In-Game.Messages.Admin.Changed-Theme", ""));

    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_", new Message("In-Game.Messages.Plot.", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_PARTICLE_ADDED", new Message("In-Game.Messages.Plot.Added-Particle", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_NOBODY", new Message("In-Game.Messages.Plot.Nobody", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_TIME_LEFT_TITLE", new Message("In-Game.Messages.Plot.Time-Left.Title", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_TIME_LEFT_CHAT", new Message("In-Game.Messages.Plot.Time-Left.Chat", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_LIMIT_ENTITIES", new Message("In-Game.Messages.Plot.Limit.Entities", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_LIMIT_PARTICLES", new Message("In-Game.Messages.Plot.Limit.Particles", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_PERMISSION_HEAD", new Message("In-Game.Messages.Plot.Permission.Head", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_PERMISSION_PARTICLE", new Message("In-Game.Messages.Plot.Permission.Particle", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_PERMISSION_BIOME", new Message("In-Game.Messages.Plot.Permission.Biome", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_PERMISSION_OUTSIDE", new Message("In-Game.Messages.Plot.Permission.Outside", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_PERMISSION_FLOOR_ITEM", new Message("In-Game.Messages.Plot.Permission.Floor-Item", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_SELECTOR_FULL", new Message("In-Game.Messages.Plot.Selector.Full", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_SELECTOR_EMPTY", new Message("In-Game.Messages.Plot.Selector.Empty", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_SELECTOR_INSIDE", new Message("In-Game.Messages.Plot.Selector.Inside", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_SELECTOR_NAME", new Message("In-Game.Messages.Plot.Selector.Name", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_SELECTOR_MENU_NAME", new Message("In-Game.Messages.Plot.Selector.Menu-Name", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_SELECTOR_PLOT_CHOOSE", new Message("In-Game.Messages.Plot.Selector.Plot-Choose", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_SELECTOR_MEMBER", new Message("In-Game.Messages.Plot.Selector.Member", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_VOTING_NAME", new Message("In-Game.Messages.Plot.Voting.Name", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_CHAT", new Message("In-Game.Messages.Plot.Voting.Plot-Owner.Chat", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_TITLE", new Message("In-Game.Messages.Plot.Voting.Plot-Owner.Title", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_NEXT", new Message("In-Game.Messages.Plot.Voting.Plot-Owner.Next", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_WAS", new Message("In-Game.Messages.Plot.Voting.Plot-Owner.Was", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_OWN", new Message("In-Game.Messages.Plot.Voting.Plot-Owner.Own", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_VOTING_SUCCESS", new Message("In-Game.Messages.Plot.Voting.Success", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_VOTING_WINNER", new Message("In-Game.Messages.Plot.Voting.Winner", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_BUILDER", new Message("In-Game.Messages.Plot.Guess-The-Build.Builder", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_ROUND", new Message("In-Game.Messages.Plot.Guess-The-Build.Round", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_THEME_CHARS", new Message("In-Game.Messages.Plot.Guess-The-Build.Theme.Chars", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_THEME_NAME", new Message("In-Game.Messages.Plot.Guess-The-Build.Theme.Name", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_THEME_WAS", new Message("In-Game.Messages.Plot.Guess-The-Build.Theme.Was", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_THEME_TITLE", new Message("In-Game.Messages.Plot.Guess-The-Build.Theme.Title", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_THEME_GUESSED", new Message("In-Game.Messages.Plot.Guess-The-Build.Theme.Guessed", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_THEME_BEING_SELECTED", new Message("In-Game.Messages.Plot.Guess-The-Build.Theme.Being-Selected", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_THEME_GUESS_TITLE", new Message("In-Game.Messages.Plot.Guess-The-Build.Theme.Guess.Title", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_THEME_GUESS_POINTS", new Message("In-Game.Messages.Plot.Guess-The-Build.Theme.Guess.Points", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_THEME_GUESS_GUESSED", new Message("In-Game.Messages.Plot.Guess-The-Build.Theme.Guess.Guessed", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_THEME_GUESS_CANT_TALK", new Message("In-Game.Messages.Plot.Guess-The-Build.Theme.Guess.Cant-Talk", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_THEME_GUESS_BUILDER", new Message("In-Game.Messages.Plot.Guess-The-Build.Theme.Guess.Builder", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_NPC_NAME", new Message("In-Game.Messages.Plot.NPC.Name", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_NPC_CREATED", new Message("In-Game.Messages.Plot.NPC.Created", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_NPC_CITIZENS", new Message("In-Game.Messages.Plot.NPC.Install-Citizens", ""));


    getMessageManager().registerMessage("MENU_PERMISSION", new Message("Menu.Permission", ""));
    getMessageManager().registerMessage("MENU_BUTTONS_BACK_ITEM_NAME", new Message("Menu.Buttons.Back.Item.Name", ""));
    getMessageManager().registerMessage("MENU_BUTTONS_BACK_ITEM_LORE", new Message("Menu.Buttons.Back.Item.Lore", ""));
    getMessageManager().registerMessage("MENU_LOCATION", new Message("Menu.Location", ""));
    getMessageManager().registerMessage("MENU_OPTION_INVENTORY", new Message("Menu.Option.Inventory", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_PARTICLE_INVENTORY", new Message("Menu.Option.Content.Particle.Inventory", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_PARTICLE_ITEM_NAME", new Message("Menu.Option.Content.Particle.Item.Name", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_PARTICLE_ITEM_LORE", new Message("Menu.Option.Content.Particle.Item.Lore", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_PARTICLE_ITEM_REMOVE_NAME", new Message("Menu.Option.Content.Particle.Item.Remove.Name", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_PARTICLE_ITEM_REMOVE_LORE", new Message("Menu.Option.Content.Particle.Item.Remove.Lore", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_PARTICLE_ADDED", new Message("Menu.Option.Content.Particle.Added", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_PARTICLE_REMOVED", new Message("Menu.Option.Content.Particle.Removed", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_HEADS_INVENTORY", new Message("Menu.Option.Content.Heads.Inventory", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_HEADS_ITEM_NAME", new Message("Menu.Option.Content.Heads.Item.Name", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_HEADS_ITEM_LORE", new Message("Menu.Option.Content.Heads.Item.Lore", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_FLOOR_ITEM_NAME", new Message("Menu.Option.Content.Floor.Item.Name", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_FLOOR_ITEM_LORE", new Message("Menu.Option.Content.Floor.Item.Lore", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_FLOOR_CHANGED", new Message("Menu.Option.Content.Floor.Changed", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_TIME_INVENTORY", new Message("Menu.Option.Content.Time.Inventory", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_TIME_ITEM_NAME", new Message("Menu.Option.Content.Time.Item.Name", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_TIME_ITEM_LORE", new Message("Menu.Option.Content.Time.Item.Lore", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_TIME_TYPE_WORLD", new Message("Menu.Option.Content.Time.Type.World", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_TIME_TYPE_DAY", new Message("Menu.Option.Content.Time.Type.Day", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_TIME_TYPE_NOON", new Message("Menu.Option.Content.Time.Type.Noon", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_TIME_TYPE_SUNSET", new Message("Menu.Option.Content.Time.Type.Sunset", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_TIME_TYPE_NIGHT", new Message("Menu.Option.Content.Time.Type.NIGHt", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_TIME_TYPE_MIDNIGHT", new Message("Menu.Option.Content.Time.Type.MidNight", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_TIME_TYPE_SUNRISE", new Message("Menu.Option.Content.Time.Type.Sunrise", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_TIME_CHANGED", new Message("Menu.Option.Content.Time.Changed", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_BIOME_INVENTORY", new Message("Menu.Option.Content.Biome.Inventory", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_BIOME_ITEM_NAME", new Message("Menu.Option.Content.Biome.Item.Name", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_BIOME_ITEM_LORE", new Message("Menu.Option.Content.Biome.Item.Lore", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_BIOME_CHANGED", new Message("Menu.Option.Content.Biome.Changed", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_WEATHER_INVENTORY", new Message("Menu.Option.Content.Weather.Inventory", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_WEATHER_ITEM_NAME", new Message("Menu.Option.Content.Weather.Item.Name", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_WEATHER_ITEM_LORE", new Message("Menu.Option.Content.Weather.Item.Lore", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_WEATHER_TYPE_DOWNFALL", new Message("Menu.Option.Content.Weather.Type.Downfall", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_WEATHER_TYPE_CLEAR", new Message("Menu.Option.Content.Weather.Type.Clear", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_WEATHER_CHANGED", new Message("Menu.Option.Content.Weather.Changed", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_BANNER_INVENTORY_COLOR", new Message("Menu.Option.Content.Banner.Inventory.Color", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_BANNER_INVENTORY_LAYER", new Message("Menu.Option.Content.Banner.Inventory.Layer", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_BANNER_INVENTORY_LAYER_COLOR", new Message("Menu.Option.Content.Banner.Inventory.Layer-Color", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_BANNER_ITEM_NAME", new Message("Menu.Option.Content.Banner.Item.Name", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_BANNER_ITEM_LORE", new Message("Menu.Option.Content.Banner.Item.Lore", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_BANNER_ITEM_CREATE_NAME", new Message("Menu.Option.Content.Banner.Item.Create.Name", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_BANNER_ITEM_CREATE_LORE", new Message("Menu.Option.Content.Banner.Item.Create.Lore", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_BANNER_CHANGED", new Message("Menu.Option.Content.Banner.Changed", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_RESET_ITEM_NAME", new Message("Menu.Option.Content.Reset.Item.Name", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_RESET_ITEM_LORE", new Message("Menu.Option.Content.Reset.Item.Lore", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_RESET_CHAT", new Message("Menu.Option.Content.Reset.Chat", ""));
    getMessageManager().registerMessage("MENU_THEME_INVENTORY", new Message("Menu.Theme.Inventory", ""));
    getMessageManager().registerMessage("MENU_THEME_ITEM_NAME", new Message("Menu.Theme.Item.Name", ""));
    getMessageManager().registerMessage("MENU_THEME_ITEM_LORE", new Message("Menu.Theme.Item.Lore", ""));
    getMessageManager().registerMessage("MENU_THEME_VOTE_SUCCESS", new Message("Menu.Theme.Vote.Success", ""));
    getMessageManager().registerMessage("MENU_THEME_VOTE_ALREADY", new Message("Menu.Theme.Vote.Already", ""));
    getMessageManager().registerMessage("MENU_THEME_VOTE_SUPER_ITEM_NAME", new Message("Menu.Theme.Vote.Super.Item.Name", ""));
    getMessageManager().registerMessage("MENU_THEME_VOTE_SUPER_ITEM_LORE", new Message("Menu.Theme.Vote.Super.Item.Lore", ""));
    getMessageManager().registerMessage("MENU_THEME_VOTE_SUPER_USED", new Message("Menu.Theme.Vote.Super.Used", ""));
    getMessageManager().registerMessage("MENU_THEME_GTB_INVENTORY", new Message("Menu.Theme.Guess-The-Build.Inventory", ""));
    getMessageManager().registerMessage("MENU_THEME_GTB_ITEM_NAME", new Message("Menu.Theme.Guess-The-Build.Item.Name", ""));
    getMessageManager().registerMessage("MENU_THEME_GTB_ITEM_LORE", new Message("Menu.Theme.Guess-The-Build.Item.Lore", ""));
    getMessageManager().registerMessage("MENU_THEME_GTB_DIFFICULTIES_EASY", new Message("Menu.Theme.Guess-The-Build.Difficulties.Easy", ""));
    getMessageManager().registerMessage("MENU_THEME_GTB_DIFFICULTIES_MEDIUM", new Message("Menu.Theme.Guess-The-Build.Difficulties.Medium", ""));
    getMessageManager().registerMessage("MENU_THEME_GTB_DIFFICULTIES_HARD", new Message("Menu.Theme.Guess-The-Build.Difficulties.Hard", ""));

    getMessageManager().registerMessage("LEADERBOARD_STATISTICS_POINTS_HIGHEST_WIN", new Message("Leaderboard.Statistics.Highest-Win", ""));
    getMessageManager().registerMessage("LEADERBOARD_STATISTICS_POINTS_HIGHEST", new Message("Leaderboard.Statistics.Highest-Points", ""));
    getMessageManager().registerMessage("LEADERBOARD_STATISTICS_POINTS_TOTAL", new Message("Leaderboard.Statistics.Total-Points-Earned", ""));
    getMessageManager().registerMessage("LEADERBOARD_STATISTICS_BLOCKS_PLACED", new Message("Leaderboard.Statistics.Blocks-Placed", ""));
    getMessageManager().registerMessage("LEADERBOARD_STATISTICS_BLOCKS_BROKEN", new Message("Leaderboard.Statistics.Blocks-Broken", ""));
    getMessageManager().registerMessage("LEADERBOARD_STATISTICS_PARTICLES_USED", new Message("Leaderboard.Statistics.Particles-Placed", ""));
    getMessageManager().registerMessage("LEADERBOARD_STATISTICS_SUPER_VOTES", new Message("Leaderboard.Statistics.Super-Votes", ""));

  }

  public VoteItems getVoteItems() {
    return voteItems;
  }

  public ThemeManager getThemeManager() {
    return themeManager;
  }

  public BlacklistManager getBlacklistManager() {
    return blacklistManager;
  }

  public OptionsRegistry getOptionsRegistry() {
    return optionsRegistry;
  }

  public PlotMenuHandler getPlotMenuHandler() {
    return plotMenuHandler;
  }


  @Override
  public ArenaRegistry getArenaRegistry() {
    return arenaRegistry;
  }

  @Override
  public ArenaManager getArenaManager() {
    return arenaManager;
  }

  @Override
  public ArgumentsRegistry getArgumentsRegistry() {
    return argumentsRegistry;
  }

  @Override
  public PluginSetupCategoryManager getSetupCategoryManager(SetupInventory setupInventory) {
    return new SetupCategoryManager(setupInventory);
  }
}
