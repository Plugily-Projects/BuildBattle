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

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.TestOnly;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.api.StatisticType;
import plugily.projects.minigamesbox.classic.arena.options.ArenaOption;
import plugily.projects.minigamesbox.classic.handlers.language.Message;
import plugily.projects.minigamesbox.classic.handlers.permissions.Permission;
import plugily.projects.minigamesbox.classic.handlers.permissions.PermissionCategory;
import plugily.projects.minigamesbox.classic.handlers.reward.RewardType;
import plugily.projects.minigamesbox.classic.preferences.ConfigOption;
import plugily.projects.minigamesbox.classic.utils.services.locale.Locale;
import plugily.projects.minigamesbox.classic.utils.services.locale.LocaleRegistry;

import java.io.File;
import java.util.Arrays;

/**
 * Created by Tom on 17/08/2015.
 * Updated by Tigerpanzer_02 on 03.12.2021
 */
public class Main extends PluginMain {

  private FileConfiguration entityUpgradesConfig;
  private ArenaRegistry arenaRegistry;
  private ArenaManager arenaManager;
  private ArgumentsRegistry argumentsRegistry;


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
    addArenaOptions();
    Arena.init(this);
    ArenaUtils.init(this);
    new ArenaEvents(this);
    arenaManager = new ArenaManager(this);
    arenaRegistry = new ArenaRegistry(this);
    arenaRegistry.registerArenas();
    getSignManager().loadSigns();
    getSignManager().updateSigns();
    argumentsRegistry = new ArgumentsRegistry(this);

    new PluginEvents(this);
    addPluginMetrics();
  }

  public void addAdditionalValues() {
    getConfigPreferences().registerOption("MOB_SPAWN", new ConfigOption("Mob.Spawn", false));
    getConfigPreferences().registerOption("HEAD_MENU_CUSTOM", new ConfigOption("Head-Menu.Custom", false));
    getConfigPreferences().registerOption("REPORT_COMMANDS", new ConfigOption("Report.Commands", false));
    getConfigPreferences().registerOption("HIDE_PLOT_OWNER", new ConfigOption("Hide-Plot-Owner", false));

    getArenaOptionManager().registerArenaOption("IN_PLOT_CHECKER", new ArenaOption("null", 0, true));
    getArenaOptionManager().registerArenaOption("PLOT_MEMBER_SIZE", new ArenaOption("plotmembersize", 1, true));

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
    getMessageManager().registerMessage("COMMANDS_ADMIN_ADDED_PLOT", new Message("Commands.Admin.Added-Plot", ""));
    getMessageManager().registerMessage("SCOREBOARD_THEME_UNKNOWN", new Message("Scoreboard.Theme-Unknown", ""));
    // scoreboard ingame classic/teams/guess-the-build/guess-the-build-waiting | ending classic/guess-the-build
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
  }
}
