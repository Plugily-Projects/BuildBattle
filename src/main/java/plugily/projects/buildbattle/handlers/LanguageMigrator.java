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

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import plugily.projects.buildbattle.Main;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.migrator.MigratorUtils;

import java.io.File;

/*
  NOTE FOR CONTRIBUTORS - Please do not touch this class if you don't know how it works! You can break migrator modifying these values!
 */

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 07.2025
 */
@SuppressWarnings("deprecation")
public class LanguageMigrator {

  public enum PluginFileVersion {
    /*ARENA_SELECTOR(0),*/ BUNGEE(1), CONFIG(1), LANGUAGE(2),
    /*LEADERBOARDS(0),*/ MYSQL(1), PERMISSIONS(1), POWERUPS(1),
    /*SIGNS(0),*/ SPECIAL_ITEMS(1), SPECTATOR(1)/*, STATS(0)*/,
    MAIN_MENU(1);

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
      if(fileName.equalsIgnoreCase(PluginFileVersion.MAIN_MENU.name())) {
        fileName = "heads/mainmenu";
      }
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
      Bukkit.getLogger().info("[System notify] The " + fileName + "  file is outdated! Updating...");
      for(int i = oldVersion; i < newVersion; i++) {
        executeUpdate(file, pluginFileVersion, i);
      }

      updatePluginFileVersion(file, configuration, oldVersion, newVersion);
      Bukkit.getLogger().info("[System notify] " + fileName + " updated, no comments were removed :)");
      Bukkit.getLogger().info("[System notify] You're using latest " + fileName + " file now! Nice!");
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
      case MAIN_MENU:
        switch(version) {
          case 0:
            MigratorUtils.addNewLines(file, "# Use HeadDatabase in addition to the configured ones\n" +
                "# You can remove categories from the catalog which shouldn't be loaded!\n" +
                "# Catalog:\n" +
                "#      - alphabet\n" +
                "#      - animals\n" +
                "#      - blocks\n" +
                "#      - decoration\n" +
                "#      - food-drinks\n" +
                "#      - humanoid\n" +
                "#      - humans\n" +
                "#      - miscellaneous\n" +
                "#      - monsters\n" +
                "#      - plants\n" +
                "database-alphabet:\n" +
                "  displayname: '&6%value% heads'\n" +
                "  texture: \"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzI1MDhlMmNhNjUwMGJjZTMwNTM5YzM4ODg0MmE1NjcyYjdiYzI5YTY4NzZkZDZhNTAyNTY3MmUyNTJkMjVkYSJ9fX0=\"\n" +
                "  lore:\n" +
                "    - \"Click to open\"\n" +
                "    - \"%value% menu\"\n" +
                "  enabled: true\n" +
                "  database: true\n" +
                "  config: alphabet\n" +
                "  permission: buildbattle.heads\n" +
                "  menuname: \"%value% blocks\"\n" +
                "database-animals:\n" +
                "  displayname: '&6%value% heads'\n" +
                "  texture: \"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTQyYjllYzM0OTYwNjRiMDg1ZjdkMzBjZTkwYTBjOGY3NjM2YzhlZjUzMDNiMjBjMjVjYTEwYTk5N2JkNzQzMyJ9fX0=\"\n" +
                "  lore:\n" +
                "    - \"Click to open\"\n" +
                "    - \"%value% menu\"\n" +
                "  enabled: true\n" +
                "  database: true\n" +
                "  config: animals\n" +
                "  permission: buildbattle.heads\n" +
                "  menuname: \"%value% blocks\"\n" +
                "database-blocks:\n" +
                "  displayname: '&6%value% heads'\n" +
                "  texture: \"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzViNDQ4ZmQ5NWM4NzRjOTVmZTc0ODQ0NDFhNDM5NGQ2NDZiNzJiYzgyYTUyNzQ4M2ZkYzcwY2E3OTg2ZmNhNSJ9fX0=\"\n" +
                "  lore:\n" +
                "    - \"Click to open\"\n" +
                "    - \"%value% menu\"\n" +
                "  enabled: true\n" +
                "  database: true\n" +
                "  config: blocks\n" +
                "  permission: buildbattle.heads\n" +
                "  menuname: \"%value% blocks\"\n" +
                "database-decoration:\n" +
                "  displayname: '&6%value% heads'\n" +
                "  texture: \"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjNhZjk0YTUwZmFmNTBhZTAyMzZjNzExZWMxMzZiMjgwOGFjODJiYjE3MWQ0MmIzOGQxNjc2MGQyMjBmMjU4MiJ9fX0=\"\n" +
                "  lore:\n" +
                "    - \"Click to open\"\n" +
                "    - \"%value% menu\"\n" +
                "  enabled: true\n" +
                "  database: true\n" +
                "  config: decoration\n" +
                "  permission: buildbattle.heads\n" +
                "  menuname: \"%value% blocks\"\n" +
                "database-food-drinks:\n" +
                "  displayname: '&6%value% heads'\n" +
                "  texture: \"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDUxMzdmYzBjZDUxMjAyNjcyMzgyYmZhZTAxZmViM2UxZTJiNzMxMDdlOThkMmM2YzhmNzE5ZjFkYTUzMGU2OCJ9fX0=\"\n" +
                "  lore:\n" +
                "    - \"Click to open\"\n" +
                "    - \"%value% menu\"\n" +
                "  enabled: true\n" +
                "  database: true\n" +
                "  config: food-drinks\n" +
                "  permission: buildbattle.heads\n" +
                "  menuname: \"%value% blocks\"\n" +
                "database-humanoid:\n" +
                "  displayname: '&6%value% heads'\n" +
                "  texture: \"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTc5Yzc3ZjEyYzg2NzM5OGE0MTgxMDgxZmI1YmY3MGM1ZmYzMjcxNWM2ODk3NTBjNmU3MTdkMDY5ZTgxMzFhOSJ9fX0=\"\n" +
                "  lore:\n" +
                "    - \"Click to open\"\n" +
                "    - \"%value% menu\"\n" +
                "  enabled: true\n" +
                "  database: true\n" +
                "  config: humanoid\n" +
                "  permission: buildbattle.heads\n" +
                "  menuname: \"%value% blocks\"\n" +
                "database-humans:\n" +
                "  displayname: '&6%value% heads'\n" +
                "  texture: \"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDQ5MmRmZTNjZThjYjYxYjkwODYwZWZmOTM4Y2I4M2UxOWMxNmU5Y2IyZDliZjJhZDc4OTZjOTBmNWIyZTFmMCJ9fX0=\"\n" +
                "  lore:\n" +
                "    - \"Click to open\"\n" +
                "    - \"%value% menu\"\n" +
                "  enabled: true\n" +
                "  database: true\n" +
                "  config: humans\n" +
                "  permission: buildbattle.heads\n" +
                "  menuname: \"%value% blocks\"\n" +
                "database-miscellaneous:\n" +
                "  displayname: '&6%value% heads'\n" +
                "  texture: \"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDQ3OTE3MTkzZTlmYjdjYjk2OGZiYTAzYTJlMjg0YTA0NjEyMWIyZGMwMTU0MWNhY2RiYTUzYTRmOWJiZjcwOCJ9fX0=\"\n" +
                "  lore:\n" +
                "    - \"Click to open\"\n" +
                "    - \"%value% menu\"\n" +
                "  enabled: true\n" +
                "  database: true\n" +
                "  config: miscellaneous\n" +
                "  permission: buildbattle.heads\n" +
                "  menuname: \"%value% blocks\"\n" +
                "database-monsters:\n" +
                "  displayname: '&6%value% heads'\n" +
                "  texture: \"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTgyYjQxZjY3YjMzNzE0NWM1ZWI4M2I5ZTAwMTU1NzQ4MDE2MGE1NWU5MWVmODYzZWIwYmYwNTU3Mzg0MmNlMSJ9fX0=\"\n" +
                "  lore:\n" +
                "    - \"Click to open\"\n" +
                "    - \"%value% menu\"\n" +
                "  enabled: true\n" +
                "  database: true\n" +
                "  config: monsters\n" +
                "  permission: buildbattle.heads\n" +
                "  menuname: \"%value% blocks\"\n" +
                "database-plants:\n" +
                "  displayname: '&6%value% heads'\n" +
                "  texture: \"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA4MzY2YjgxMWM0MzU5YWVhNjY4NjMwMGNmZDQ1MGQ5ZWFhNDYxNGMzYWYwOGExN2YxNTEzZjVkMmY0OGM0YSJ9fX0=\"\n" +
                "  lore:\n" +
                "    - \"Click to open\"\n" +
                "    - \"%value% menu\"\n" +
                "  enabled: true\n" +
                "  database: true\n" +
                "  config: plants\n" +
                "  permission: buildbattle.heads\n" +
                "  menuname: \"%value% blocks\"\n" +
                "database-search:\n" +
                "  displayname: '&6%value% heads'\n" +
                "  texture: \"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjViOTVkYTEyODE2NDJkYWE1ZDAyMmFkYmQzZTdjYjY5ZGMwOTQyYzgxY2Q2M2JlOWMzODU3ZDIyMmUxYzhkOSJ9fX0=\"\n" +
                "  lore:\n" +
                "    - \"Click to open\"\n" +
                "    - \"%value% menu\"\n" +
                "  enabled: true\n" +
                "  database: true\n" +
                "  config: search\n" +
                "  permission: buildbattle.heads\n" +
                "  menuname: \"%value% blocks\"");
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
