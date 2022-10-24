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
import plugily.projects.minigamesbox.classic.preferences.ConfigPreferences;

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
    getConfigPreferences().registerOption("MOB_SPAWN", new ConfigOption("Mob.Spawn", false));
    getConfigPreferences().registerOption("HEAD_MENU_CUSTOM", new ConfigOption("Head-Menu.Custom", false));
    getConfigPreferences().registerOption("REPORT_COMMANDS", new ConfigOption("Report.Commands", false));
    getConfigPreferences().registerOption("PLOT_HIDE_OWNER", new ConfigOption("Plot.Hide-Owner", false));
    getConfigPreferences().registerOption("PLOT_MOVE_OUTSIDE", new ConfigOption("Plot.Move-Outside", false));
  }

  private void registerStatistics() {
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
  }

  private void registerPermission() {
    getPermissionsManager().registerPermissionCategory("POINTS_BOOSTER", new PermissionCategory("Points-Boost", null));
    getPermissionsManager().registerPermissionCategory("VOTING_BOOSTER", new PermissionCategory("Voting-Boost", null));
  }

  private void registerRewards() {
    getRewardsHandler().registerRewardType("GUESS", new RewardType("guessed"));
    getRewardsHandler().registerRewardType("GUESS_ALL", new RewardType("guessed-all"));
    getRewardsHandler().registerRewardType("VOTE", new RewardType("voted"));
    getRewardsHandler().registerRewardType("VOTE_ALL", new RewardType("voted-all"));
    getRewardsHandler().registerRewardType("REPORT", new RewardType("report"));
    getRewardsHandler().registerRewardType("PLACE", new RewardType("place"));
  }

  private void registerSpecialItems() {
    getSpecialItemManager().registerSpecialItem("OPTIONS_MENU", "Options-Menu");
    getSpecialItemManager().registerSpecialItem("PLOT_SELECTOR", "Plot-Selector");
  }

  private void registerArenaOptions() {
    getArenaOptionManager().registerArenaOption("IN_PLOT_CHECKER", new ArenaOption("null", 0, true));
    getArenaOptionManager().registerArenaOption("PLOT_MEMBER_SIZE", new ArenaOption("plotmembersize", 1, true));
  }

  private ConfigPreferences getConfigPreferences() {
    return plugin.getConfigPreferences();
  }

  private StatsStorage getStatsStorage() {
    return plugin.getStatsStorage();
  }

  private PermissionsManager getPermissionsManager() {
    return plugin.getPermissionsManager();
  }

  private RewardsFactory getRewardsHandler() {
    return plugin.getRewardsHandler();
  }

  private SpecialItemManager getSpecialItemManager() {
    return plugin.getSpecialItemManager();
  }

  private ArenaOptionManager getArenaOptionManager() {
    return plugin.getArenaOptionManager();
  }

}
