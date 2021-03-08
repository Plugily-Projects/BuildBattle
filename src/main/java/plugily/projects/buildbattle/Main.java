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

import me.tigerhix.lib.scoreboard.ScoreboardLib;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajerlair.commonsbox.database.MysqlDatabase;
import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion;
import pl.plajerlair.commonsbox.minecraft.compat.events.EventsInitializer;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.misc.MiscUtils;
import pl.plajerlair.commonsbox.minecraft.serialization.InventorySerializer;
import plugily.projects.buildbattle.api.StatsStorage;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.arena.vote.VoteEvents;
import plugily.projects.buildbattle.arena.vote.VoteItems;
import plugily.projects.buildbattle.commands.arguments.ArgumentsRegistry;
import plugily.projects.buildbattle.events.ChatEvents;
import plugily.projects.buildbattle.events.GameEvents;
import plugily.projects.buildbattle.events.JoinEvents;
import plugily.projects.buildbattle.events.LobbyEvents;
import plugily.projects.buildbattle.events.QuitEvents;
import plugily.projects.buildbattle.events.spectator.SpectatorEvents;
import plugily.projects.buildbattle.handlers.BungeeManager;
import plugily.projects.buildbattle.handlers.ChatManager;
import plugily.projects.buildbattle.handlers.HolidayManager;
import plugily.projects.buildbattle.handlers.PermissionManager;
import plugily.projects.buildbattle.handlers.PlaceholderManager;
import plugily.projects.buildbattle.handlers.items.SpecialItemsRegistry;
import plugily.projects.buildbattle.handlers.language.LanguageManager;
import plugily.projects.buildbattle.handlers.party.PartyHandler;
import plugily.projects.buildbattle.handlers.party.PartySupportInitializer;
import plugily.projects.buildbattle.handlers.reward.RewardsFactory;
import plugily.projects.buildbattle.handlers.setup.SetupInventoryEvents;
import plugily.projects.buildbattle.handlers.sign.SignManager;
import plugily.projects.buildbattle.menus.options.OptionsMenuHandler;
import plugily.projects.buildbattle.menus.options.OptionsRegistry;
import plugily.projects.buildbattle.menus.options.registry.banner.BannerMenu;
import plugily.projects.buildbattle.menus.themevoter.VoteMenuListener;
import plugily.projects.buildbattle.user.User;
import plugily.projects.buildbattle.user.UserManager;
import plugily.projects.buildbattle.user.data.MysqlManager;
import plugily.projects.buildbattle.utils.CuboidSelector;
import plugily.projects.buildbattle.utils.Debugger;
import plugily.projects.buildbattle.utils.ExceptionLogHandler;
import plugily.projects.buildbattle.utils.LegacyDataFixer;
import plugily.projects.buildbattle.utils.MessageUtils;
import plugily.projects.buildbattle.utils.UpdateChecker;
import plugily.projects.buildbattle.utils.services.ServiceRegistry;

import java.util.Arrays;

/**
 * Created by Tom on 17/08/2015.
 */
//todo setup handler recode
//todo arenas handler recode
//todo inventoryframework
public class Main extends JavaPlugin {

  private ArgumentsRegistry registry;
  private ExceptionLogHandler exceptionLogHandler;
  private ChatManager chatManager;
  private ConfigPreferences configPreferences;
  private MysqlDatabase database;
  private UserManager userManager;
  private BungeeManager bungeeManager;
  private SignManager signManager;
  private CuboidSelector cuboidSelector;
  private VoteItems voteItems;
  private OptionsRegistry optionsRegistry;
  private SpecialItemsRegistry specialItemsRegistry;
  private boolean forceDisable = false;
  private PartyHandler partyHandler;
  private RewardsFactory rewardsHandler;

public CuboidSelector getCuboidSelector() {
    return cuboidSelector;
  }

  public VoteItems getVoteItems() {
    return voteItems;
  }

  public OptionsRegistry getOptionsRegistry() {
    return optionsRegistry;
  }

  public BungeeManager getBungeeManager() {
    return bungeeManager;
  }

  public SignManager getSignManager() {
    return signManager;
  }

  public ConfigPreferences getConfigPreferences() {
    return configPreferences;
  }

  public SpecialItemsRegistry getSpecialItemsRegistry() {
    return specialItemsRegistry;
  }

  public ArgumentsRegistry getArgumentsRegistry() {
    return registry;
  }

  @Override
  public void onEnable() {
    if(!validateIfPluginShouldStart()) {
      return;
    }

    ServiceRegistry.registerService(this);
    exceptionLogHandler = new ExceptionLogHandler(this);
    Debugger.setEnabled(getDescription().getVersion().contains("debug") || getConfig().getBoolean("Debug"));
    Debugger.debug("Main setup started");
    saveDefaultConfig();
    for(String s : Arrays.asList("arenas", "particles", "lobbyitems", "stats", "voteItems", "mysql", "biomes", "bungee", "rewards")) {
      ConfigUtils.getConfig(this, s);
    }
    LanguageManager.init(this);
    chatManager = new ChatManager(LanguageManager.getLanguageMessage("In-Game.Plugin-Prefix"));
    configPreferences = new ConfigPreferences(this);
    new LegacyDataFixer(this);
    initializeClasses();
  }

  private void checkUpdate() {
    if(!getConfig().getBoolean("Update-Notifier.Enabled", true)) {
      return;
    }
    UpdateChecker.init(this, 44703).requestUpdateCheck().whenComplete((result, exception) -> {
      if(!result.requiresUpdate()) {
        return;
      }
      if(result.getNewestVersion().contains("b")) {
        if(getConfig().getBoolean("Update-Notifier.Notify-Beta-Versions", true)) {
          Debugger.sendConsoleMsg("&c[BuildBattle] Your software is ready for update! However it's a BETA VERSION. Proceed with caution.");
          Debugger.sendConsoleMsg("&c[BuildBattle] Current version %old%, latest version %new%".replace("%old%", getDescription().getVersion()).replace("%new%",
              result.getNewestVersion()));
        }
        return;
      }
      MessageUtils.updateIsHere();
      Debugger.sendConsoleMsg("&aYour Build Battle plugin is outdated! Download it to keep with latest changes and fixes.");
      Debugger.sendConsoleMsg("&aDisable this option in config.yml if you wish.");
      Debugger.sendConsoleMsg("&eCurrent version: &c" + getDescription().getVersion() + " &eLatest version: &a" + result.getNewestVersion());
    });
  }

  private boolean validateIfPluginShouldStart() {
    try {
      Class.forName("org.spigotmc.SpigotConfig");
    } catch(Exception e) {
      MessageUtils.thisVersionIsNotSupported();
      Debugger.sendConsoleMsg("&cYour server software is not supported by Build Battle!");
      Debugger.sendConsoleMsg("&cWe support only Spigot and Spigot forks only! Shutting off...");
      forceDisable = true;
      getServer().getPluginManager().disablePlugin(this);
      return false;
    }
    if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_8_R1)) {
      MessageUtils.thisVersionIsNotSupported();
      Debugger.sendConsoleMsg("&cYour server version is not supported by Build Battle!");
      Debugger.sendConsoleMsg("&cSadly, we must shut off. Maybe you consider updating your server version?");
      forceDisable = true;
      getServer().getPluginManager().disablePlugin(this);
      return false;
    }
    return true;
  }

  //order matters
  private void initializeClasses() {
    ScoreboardLib.setPluginInstance(this);
    if(getConfig().getBoolean("BungeeActivated")) {
      bungeeManager = new BungeeManager(this);
    }
    if(configPreferences.getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
      FileConfiguration config = ConfigUtils.getConfig(this, "mysql");
      database = new MysqlDatabase(config.getString("user"), config.getString("password"), config.getString("address"), config.getLong("maxLifeTime"));
    }
    registry = new ArgumentsRegistry(this);
    userManager = new UserManager(this);
    PermissionManager.init();
    new SetupInventoryEvents(this);
    signManager = new SignManager(this);
    ArenaRegistry.registerArenas();
    signManager.loadSigns();
    signManager.updateSigns();
    specialItemsRegistry = new SpecialItemsRegistry(this);
    voteItems = new VoteItems();
    new VoteEvents(this);
    new LobbyEvents(this);
    new ChatEvents(this);
    optionsRegistry = new OptionsRegistry(this);
    new OptionsMenuHandler(this);
    Metrics metrics = new Metrics(this, 2491);
    metrics.addCustomChart(new org.bstats.charts.SimplePie("bungeecord_hooked", () -> String.valueOf(configPreferences.getOption(ConfigPreferences.Option.BUNGEE_ENABLED))));
    metrics.addCustomChart(new org.bstats.charts.SimplePie("locale_used", LanguageManager.getPluginLocale()::getPrefix));
    metrics.addCustomChart(new org.bstats.charts.SimplePie("update_notifier", () -> {
      if(getConfig().getBoolean("Update-Notifier.Enabled", true)) {
        return getConfig().getBoolean("Update-Notifier.Notify-Beta-Versions", true) ? "Enabled with beta notifier" : "Enabled";
      }
      return getConfig().getBoolean("Update-Notifier.Notify-Beta-Versions", true) ? "Beta notifier only" : "Disabled";
    }));
    new JoinEvents(this);
    new QuitEvents(this);
    if(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      new PlaceholderManager().register();
    }
    cuboidSelector = new CuboidSelector(this);
    UpdateChecker.init(this, 44703);
    checkUpdate();
    new GameEvents(this);
    new VoteMenuListener(this);
    new HolidayManager(this);
    new SpectatorEvents(this);
    BannerMenu.init(this);
    partyHandler = new PartySupportInitializer().initialize(this);
    rewardsHandler = new RewardsFactory(this);
    new EventsInitializer().initialize(this);
    MiscUtils.sendStartUpMessage(this, "BuildBattle", getDescription(),true, true);
  }

  @Override
  public void onDisable() {
    if(forceDisable) return;

    Debugger.debug("System disabling...");
    Bukkit.getLogger().removeHandler(exceptionLogHandler);
    for(BaseArena arena : ArenaRegistry.getArenas()) {
      for(Player player : arena.getPlayers()) {
        arena.getScoreboardManager().stopAllScoreboards();
        arena.doBarAction(BaseArena.BarAction.REMOVE, player);
        arena.teleportToEndLocation(player);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getActivePotionEffects().forEach(pe -> player.removePotionEffect(pe.getType()));
        if(configPreferences.getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
          InventorySerializer.loadInventory(this, player);
        }
      }
      arena.getPlotManager().getPlots().forEach(Plot::fullyResetPlot);
      arena.teleportAllToEndLocation();
    }
    saveAllUserStatistics();
    if(configPreferences.getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
      getMysqlDatabase().shutdownConnPool();
    }
  }

  private void saveAllUserStatistics() {
    for(Player player : getServer().getOnlinePlayers()) {
      User user = userManager.getUser(player);
      if(userManager.getDatabase() instanceof MysqlManager) {
        StringBuilder update = new StringBuilder(" SET ");
        for(StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
          if(!stat.isPersistent()) continue;
          if(update.toString().equalsIgnoreCase(" SET ")) {
            update.append(stat.getName()).append('=').append(user.getStat(stat));
          }
          update.append(", ").append(stat.getName()).append('=').append(user.getStat(stat));
        }
        String finalUpdate = update.toString();
        //copy of userManager#saveStatistic but without async database call that's not allowed in onDisable method.
        ((MysqlManager) userManager.getDatabase()).getDatabase().executeUpdate("UPDATE " + ((MysqlManager) getUserManager().getDatabase()).getTableName()
            + finalUpdate + " WHERE UUID='" + user.getPlayer().getUniqueId().toString() + "';");
        continue;
      }
      for(StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
        userManager.getDatabase().saveStatistic(user, stat);
      }
    }
  }

  public ChatManager getChatManager() {
    return chatManager;
  }

  public MysqlDatabase getMysqlDatabase() {
    return database;
  }

  public UserManager getUserManager() {
    return userManager;
  }

  public PartyHandler getPartyHandler() {
    return partyHandler;
  }

  public RewardsFactory getRewardsHandler() {
    return rewardsHandler;
  }

}
