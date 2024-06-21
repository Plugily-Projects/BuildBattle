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

import plugily.projects.buildbattle.Main;
import plugily.projects.minigamesbox.api.preferences.IConfigPreferences;
import plugily.projects.minigamesbox.classic.api.StatisticType;
import plugily.projects.minigamesbox.classic.api.StatsStorage;
import plugily.projects.minigamesbox.classic.arena.options.ArenaOption;
import plugily.projects.minigamesbox.classic.arena.options.ArenaOptionManager;
import plugily.projects.minigamesbox.classic.handlers.items.SpecialItemManager;
import plugily.projects.minigamesbox.classic.handlers.permissions.PermissionCategory;
import plugily.projects.minigamesbox.classic.handlers.permissions.PermissionsManager;
import plugily.projects.minigamesbox.classic.handlers.reward.RewardType;
import plugily.projects.minigamesbox.classic.handlers.reward.RewardsFactory;
import plugily.projects.minigamesbox.classic.preferences.ConfigOption;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 15.10.2022
 */
public class AdditionalValueInitializer {

  private final Main plugin;

  public AdditionalValueInitializer(Main plugin) {
    this.plugin = plugin;
    registerConfigOptions();
    registerStatistics();
    registerPermission();
    registerRewards();
    registerSpecialItems();
    registerArenaOptions();
  }

  private void registerConfigOptions() {
    IConfigPreferences configPreferences = plugin.getConfigPreferences();

    configPreferences.registerOption("MOB_SPAWN", new ConfigOption("Mob.Spawn", false));
    configPreferences.registerOption("HEAD_MENU_CUSTOM", new ConfigOption("Head-Menu.Custom", false));
    configPreferences.registerOption("REPORT_COMMANDS", new ConfigOption("Report.Commands", false));
    configPreferences.registerOption("PLOT_HIDE_OWNER", new ConfigOption("Plot.Hide-Owner", false));
    configPreferences.registerOption("PLOT_MOVE_OUTSIDE", new ConfigOption("Plot.Move-Outside", false));
    configPreferences.registerOption("SUPER_VOTES", new ConfigOption("Super-Votes", true));
  }

  private void registerStatistics() {
    StatsStorage statsStorage = plugin.getStatsStorage();

    statsStorage.registerStatistic("BLOCKS_PLACED", new StatisticType("blocks_placed", true, "int(11) NOT NULL DEFAULT '0'"));
    statsStorage.registerStatistic("BLOCKS_BROKEN", new StatisticType("blocks_broken", true, "int(11) NOT NULL DEFAULT '0'"));
    statsStorage.registerStatistic("POINTS_HIGHEST", new StatisticType("points_highest", true, "int(11) NOT NULL DEFAULT '0'"));
    statsStorage.registerStatistic("POINTS_HIGHEST_WIN", new StatisticType("points_highest_win", true, "int(11) NOT NULL DEFAULT '0'"));
    statsStorage.registerStatistic("POINTS_TOTAL", new StatisticType("points_total", true, "int(11) NOT NULL DEFAULT '0'"));
    statsStorage.registerStatistic("PARTICLES_USED", new StatisticType("particles_used", true, "int(11) NOT NULL DEFAULT '0'"));
    statsStorage.registerStatistic("SUPER_VOTES", new StatisticType("super_votes", true, "int(11) NOT NULL DEFAULT '0'"));
    statsStorage.registerStatistic("LOCAL_POINTS", new StatisticType("local_points", false, "int(11) NOT NULL DEFAULT '0'"));
    statsStorage.registerStatistic("LOCAL_POINTS_GTB", new StatisticType("local_points_gtb", false, "int(11) NOT NULL DEFAULT '0'"));
    statsStorage.registerStatistic("REPORTS_TRIGGERED", new StatisticType("reports_triggered", false, "int(11) NOT NULL DEFAULT '0'"));
  }

  private void registerPermission() {
    PermissionsManager permissionsManager = plugin.getPermissionsManager();

    permissionsManager.registerPermissionCategory("POINTS_BOOSTER", new PermissionCategory("Points-Boost", null));
    permissionsManager.registerPermissionCategory("VOTING_BOOSTER", new PermissionCategory("Voting-Boost", null));
  }

  private void registerRewards() {
    RewardsFactory rewardsFactory = plugin.getRewardsHandler();

    rewardsFactory.registerRewardType("GUESS", new RewardType("guessed"));
    rewardsFactory.registerRewardType("GUESS_ALL", new RewardType("guessed-all"));
    rewardsFactory.registerRewardType("VOTE", new RewardType("voted"));
    rewardsFactory.registerRewardType("VOTE_ALL", new RewardType("voted-all"));
    rewardsFactory.registerRewardType("REPORT", new RewardType("report"));
    rewardsFactory.registerRewardType("PLACE", new RewardType("place"));
  }

  private void registerSpecialItems() {
    SpecialItemManager specialItemManager = plugin.getSpecialItemManager();

    specialItemManager.registerSpecialItem("OPTIONS_MENU", "Options-Menu");
    specialItemManager.registerSpecialItem("PLOT_SELECTOR", "Plot-Selector");
  }

  private void registerArenaOptions() {
    ArenaOptionManager arenaOptionManager = plugin.getArenaOptionManager();

    arenaOptionManager.registerArenaOption("IN_PLOT_CHECKER", new ArenaOption("null", 0, true));
    arenaOptionManager.registerArenaOption("PLOT_MEMBER_SIZE", new ArenaOption("plotmembersize", 1, true));
    arenaOptionManager.registerArenaOption("GTB_ROUNDS_PER_PLOT", new ArenaOption("roundsperplot", 2, true));
  }
}
