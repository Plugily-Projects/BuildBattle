/*
 *  MiniGamesBox - Library box with massive content that could be seen as minigames core.
 *  Copyright (C) 2023 Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package plugily.projects.buildbattle.handlers;

import org.bukkit.configuration.file.FileConfiguration;
import plugily.projects.buildbattle.Main;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.migrator.MigratorUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

/*
  NOTE FOR CONTRIBUTORS - Please do not touch this class if you don't know how it works! You can break migrator modifying these values!
 */

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
@SuppressWarnings("deprecation")
public class LanguageMigrator {

  public enum PluginFileVersion {
    /*ARENA_SELECTOR(0),*/ BUNGEE(1), CONFIG(1), LANGUAGE(2),
    /*LEADERBOARDS(0),*/ MYSQL(1), PERMISSIONS(1), POWERUPS(1),
    /*SIGNS(0),*/ SPECIAL_ITEMS(1), SPECTATOR(1)/*, STATS(0)*/;

    private final int version;

    PluginFileVersion(int version) {
      this.version = version;
    }

    public int getVersion() {
      return version;
    }
  }

  private final Main plugin;

  public LanguageMigrator(Main plugin) {
    this.plugin = plugin;
    updatePluginFiles();
  }

  private void updatePluginFiles() {
    for(PluginFileVersion pluginFileVersion : PluginFileVersion.values()) {
      String fileName = pluginFileVersion.name().toLowerCase();
      int newVersion = pluginFileVersion.getVersion();
      File file = new File(plugin.getDataFolder() + "/" + fileName + ".yml");
      FileConfiguration configuration = ConfigUtils.getConfig(plugin, fileName, false);
      if(configuration == null) {
        continue;
      }
      int oldVersion = configuration.getInt("Do-Not-Edit.File-Version", 0);
      if(oldVersion == newVersion) {
        continue;
      }
      plugin.getDebugger().debug(Level.WARNING, "[System notify] The " + fileName + "  file is outdated! Updating...");
      for(int i = oldVersion; i < newVersion; i++) {
        executeUpdate(file, pluginFileVersion, i);
      }

      updatePluginFileVersion(file, configuration, oldVersion, newVersion);
      plugin.getDebugger().debug(Level.WARNING, "[System notify] " + fileName + " updated, no comments were removed :)");
      plugin.getDebugger().debug(Level.WARNING, "[System notify] You're using latest " + fileName + " file now! Nice!");

    }
  }

  private void executeUpdate(File file, PluginFileVersion pluginFileVersion, int version) {
    switch(pluginFileVersion) {
      case LANGUAGE:
        switch(version) {
          case 1:
            MigratorUtils.insertAfterLine(file, "      Heads:", "        Database:\n" +
                "          Lore: \"Get Head %value%\"");
            break;
          default:
            break;
        }
        break;
      default:
        break;
    }
  }

  public void updatePluginFileVersion(File file, FileConfiguration fileConfiguration, int oldVersion, int newVersion) {
    int coreVersion = fileConfiguration.getInt("Do-Not-Edit.Core-Version", 0);
    updateFileVersion(file, coreVersion, coreVersion, newVersion, oldVersion);
  }

  private void updateFileVersion(File file, int coreVersion, int oldCoreVersion, int fileVersion, int oldFileVersion) {
    MigratorUtils.removeLineFromFile(file, "# Don't edit it. But who's stopping you? It's your server!");
    MigratorUtils.removeLineFromFile(file, "# Really, don't edit ;p");
    MigratorUtils.removeLineFromFile(file, "# You edited it, huh? Next time hurt yourself!");
    MigratorUtils.removeLineFromFile(file, "Do-Not-Edit:");
    MigratorUtils.removeLineFromFile(file, "  File-Version: " + oldFileVersion + "");
    MigratorUtils.removeLineFromFile(file, "  Core-Version: " + oldCoreVersion + "");
    MigratorUtils.addNewLines(file, "# Don't edit it. But who's stopping you? It's your server!\r\n" +
        "# Really, don't edit ;p\r\n" +
        "# You edited it, huh? Next time hurt yourself!\r\n" +
        "Do-Not-Edit:\r\n" +
        "  File-Version: " + fileVersion + "\r\n" +
        "  Core-Version: " + coreVersion + "\r\n");
  }

}
