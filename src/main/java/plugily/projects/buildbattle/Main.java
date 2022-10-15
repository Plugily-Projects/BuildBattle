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

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.TestOnly;
import plugily.projects.buildbattle.arena.ArenaEvents;
import plugily.projects.buildbattle.arena.ArenaManager;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.managers.plots.PlotMenuHandler;
import plugily.projects.buildbattle.arena.vote.VoteEvents;
import plugily.projects.buildbattle.arena.vote.VoteItems;
import plugily.projects.buildbattle.boot.AdditionalValueInitializer;
import plugily.projects.buildbattle.boot.MessageInitializer;
import plugily.projects.buildbattle.boot.PlaceholderInitializer;
import plugily.projects.buildbattle.commands.arguments.ArgumentsRegistry;
import plugily.projects.buildbattle.events.OptionMenuEvents;
import plugily.projects.buildbattle.handlers.menu.OptionsMenuHandler;
import plugily.projects.buildbattle.handlers.menu.OptionsRegistry;
import plugily.projects.buildbattle.handlers.misc.BlacklistManager;
import plugily.projects.buildbattle.handlers.setup.SetupCategoryManager;
import plugily.projects.buildbattle.handlers.themes.ThemeManager;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.categories.PluginSetupCategoryManager;
import plugily.projects.minigamesbox.classic.utils.services.metrics.Metrics;

import java.io.File;

/**
 * Created by Tom on 17/08/2015.
 * Updated by Tigerpanzer_02 on 03.12.2021
 */
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
    MessageInitializer messageInitializer = new MessageInitializer(this);
    super.onEnable();
    getDebugger().debug("[System] [Plugin] Initialization start");
    new PlaceholderInitializer(this);
    messageInitializer.registerMessages();
    new AdditionalValueInitializer(this);
    initializePluginClasses();
    getDebugger().debug("Full {0} plugin enabled", getName());
    getDebugger().debug("[System] [Plugin] Initialization finished took {0}ms", System.currentTimeMillis() - start);
  }

  public void initializePluginClasses() {
    addFileName("themes");
    addFileName("vote_items");
    blacklistManager = new BlacklistManager(this);
    themeManager = new ThemeManager(this);
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
