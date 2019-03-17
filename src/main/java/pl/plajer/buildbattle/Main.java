/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.buildbattle;

import java.util.Arrays;
import java.util.List;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.arena.ArenaManager;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.impl.BaseArena;
import pl.plajer.buildbattle.arena.vote.VoteEvents;
import pl.plajer.buildbattle.arena.vote.VoteItems;
import pl.plajer.buildbattle.commands.arguments.ArgumentsRegistry;
import pl.plajer.buildbattle.events.ChatEvents;
import pl.plajer.buildbattle.events.GameEvents;
import pl.plajer.buildbattle.events.JoinEvents;
import pl.plajer.buildbattle.events.LobbyEvents;
import pl.plajer.buildbattle.events.QuitEvents;
import pl.plajer.buildbattle.handlers.BungeeManager;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.handlers.PermissionManager;
import pl.plajer.buildbattle.handlers.PlaceholderManager;
import pl.plajer.buildbattle.handlers.SignManager;
import pl.plajer.buildbattle.handlers.items.SpecialItemsRegistry;
import pl.plajer.buildbattle.handlers.language.LanguageManager;
import pl.plajer.buildbattle.handlers.setup.SetupInventoryEvents;
import pl.plajer.buildbattle.menus.options.OptionsMenuHandler;
import pl.plajer.buildbattle.menus.options.OptionsRegistry;
import pl.plajer.buildbattle.menus.options.registry.particles.ParticleRefreshScheduler;
import pl.plajer.buildbattle.menus.themevoter.VoteMenuListener;
import pl.plajer.buildbattle.user.User;
import pl.plajer.buildbattle.user.UserManager;
import pl.plajer.buildbattle.utils.CuboidSelector;
import pl.plajer.buildbattle.utils.LegacyDataFixer;
import pl.plajer.buildbattle.utils.MessageUtils;
import pl.plajerlair.core.database.MySQLDatabase;
import pl.plajerlair.core.debug.Debugger;
import pl.plajerlair.core.debug.LogLevel;
import pl.plajerlair.core.services.ServiceRegistry;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.services.update.UpdateChecker;
import pl.plajerlair.core.utils.ConfigUtils;

/**
 * Created by Tom on 17/08/2015.
 */
public class Main extends JavaPlugin {

  private ChatManager chatManager;
  private ConfigPreferences configPreferences;
  private boolean forceDisable = false;
  private MySQLDatabase database;
  private UserManager userManager;
  private BungeeManager bungeeManager;
  private SignManager signManager;
  private CuboidSelector cuboidSelector;
  private VoteItems voteItems;
  private OptionsRegistry optionsRegistry;
  private SpecialItemsRegistry specialItemsRegistry;
  private String version;
  private List<String> filesToGenerate = Arrays.asList("arenas", "particles", "lobbyitems", "stats", "voteItems", "mysql", "biomes");

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

  public boolean is1_11_R1() {
    return version.equalsIgnoreCase("v1_11_R1");
  }

  public boolean is1_12_R1() {
    return version.equalsIgnoreCase("v1_12_R1");
  }

  @Override
  public void onEnable() {
    ServiceRegistry.registerService(this);
    try {
      version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
      try {
        Class.forName("org.spigotmc.SpigotConfig");
      } catch (Exception e) {
        MessageUtils.thisVersionIsNotSupported();
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Your server software is not supported by Build Battle!");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "We support only Spigot and Spigot forks only! Shutting off...");
        forceDisable = true;
        getServer().getPluginManager().disablePlugin(this);
        return;
      }
      if (version.contains("v1_10") || version.contains("v1_9") || version.contains("v1_8") || version.contains("v1_7") || version.contains("v1_6")) {
        MessageUtils.thisVersionIsNotSupported();
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Your server version is not supported by BuildBattle!");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Sadly, we must shut off. Maybe you consider updating your server version?");
        forceDisable = true;
        getServer().getPluginManager().disablePlugin(this);
        return;
      }
      Debugger.setEnabled(getConfig().getBoolean("Debug", false));
      Debugger.setPrefix("[Build Battle Debugger]");
      Debugger.debug(LogLevel.INFO, "Main setup started");
      saveDefaultConfig();
      for (String s : filesToGenerate) {
        ConfigUtils.getConfig(this, s);
      }
      LanguageManager.init(this);
      chatManager = new ChatManager(ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageMessage("In-Game.Plugin-Prefix")));
      configPreferences = new ConfigPreferences(this);
      new LegacyDataFixer(this);
      initializeClasses();
      if (getConfig().getBoolean("BungeeActivated")) {
        bungeeManager = new BungeeManager(this);
      }
      if (configPreferences.getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
        FileConfiguration config = ConfigUtils.getConfig(this, "mysql");
        database = new MySQLDatabase(this, config.getString("address"), config.getString("user"), config.getString("password"),
            config.getInt("min-connections"), config.getInt("max-connections"));
      }
      userManager = new UserManager(this);
    } catch (Exception ex) {
      new ReportedException(this, ex);
    }
  }

  private void checkUpdate() {
    if (!getConfig().getBoolean("Update-Notifier.Enabled", true)) {
      return;
    }
    UpdateChecker.init(this, 44703).requestUpdateCheck().whenComplete((result, exception) -> {
      if (!result.requiresUpdate()) {
        return;
      }
      if (result.getNewestVersion().contains("b")) {
        if (getConfig().getBoolean("Update-Notifier.Notify-Beta-Versions", true)) {
          Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildBattle] Your software is ready for update! However it's a BETA VERSION. Proceed with caution.");
          Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildBattle] Current version %old%, latest version %new%".replace("%old%", getDescription().getVersion()).replace("%new%",
              result.getNewestVersion()));
        }
        return;
      }
      MessageUtils.updateIsHere();
      Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Your Build Battle plugin is outdated! Download it to keep with latest changes and fixes.");
      Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Disable this option in config.yml if you wish.");
      Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Current version: " + ChatColor.RED + getDescription().getVersion() + ChatColor.YELLOW + " Latest version: " + ChatColor.GREEN + result.getNewestVersion());
    });
  }

  @Override
  public void onDisable() {
    if (forceDisable) {
      return;
    }
    Debugger.debug(LogLevel.INFO, "System disabling...");
    for (final Player player : getServer().getOnlinePlayers()) {
      BaseArena arena = ArenaRegistry.getArena(player);
      if (arena != null) {
        player.setGameMode(GameMode.SURVIVAL);
        if (configPreferences.getOption(ConfigPreferences.Option.BOSSBAR_ENABLED)) {
          arena.getGameBar().removePlayer(player);
        }
        ArenaManager.leaveAttempt(player, arena);
      }
      final User user = userManager.getUser(player);
      for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
        userManager.saveStatistic(user, stat);
      }
      userManager.removeUser(user);
    }
    if (configPreferences.getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
      getMySQLDatabase().getManager().shutdownConnPool();
    }
  }

  private void initializeClasses() {
    PermissionManager.init();
    new SetupInventoryEvents(this);
    new ArgumentsRegistry(this);
    ArenaRegistry.registerArenas();
    //load signs after arenas
    signManager = new SignManager(this);
    specialItemsRegistry = new SpecialItemsRegistry(this);
    voteItems = new VoteItems();
    new VoteEvents(this);
    new LobbyEvents(this);
    new ChatEvents(this);
    optionsRegistry = new OptionsRegistry(this);
    new OptionsMenuHandler(this);
    new ParticleRefreshScheduler(this);
    Metrics metrics = new Metrics(this);
    metrics.addCustomChart(new Metrics.SimplePie("bungeecord_hooked", () -> String.valueOf(configPreferences.getOption(ConfigPreferences.Option.BUNGEE_ENABLED))));
    metrics.addCustomChart(new Metrics.SimplePie("locale_used", () -> LanguageManager.getPluginLocale().getPrefix()));
    metrics.addCustomChart(new Metrics.SimplePie("update_notifier", () -> {
      if (getConfig().getBoolean("Update-Notifier.Enabled", true)) {
        if (getConfig().getBoolean("Update-Notifier.Notify-Beta-Versions", true)) {
          return "Enabled with beta notifier";
        } else {
          return "Enabled";
        }
      } else {
        if (getConfig().getBoolean("Update-Notifier.Notify-Beta-Versions", true)) {
          return "Beta notifier only";
        } else {
          return "Disabled";
        }
      }
    }));
    new JoinEvents(this);
    new QuitEvents(this);
    if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      new PlaceholderManager().register();
    }
    cuboidSelector = new CuboidSelector(this);
    UpdateChecker.init(this, 44703);
    checkUpdate();
    new GameEvents(this);
    new VoteMenuListener(this);
  }

  public ChatManager getChatManager() {
    return chatManager;
  }

  public MySQLDatabase getMySQLDatabase() {
    return database;
  }

  public UserManager getUserManager() {
    return userManager;
  }

}
