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

package plugily.projects.buildbattle.commands.arguments.admin;

import java.util.Locale;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.commands.arguments.ArgumentsRegistry;
import plugily.projects.buildbattle.handlers.themes.ThemeManager;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;

public final class ThemeArgument {

  private String printGameTypes() {
    StringBuilder stringBuilder = new StringBuilder();
    int length = ThemeManager.GameThemes.VALUES.length;

    for (int i = 0; i < length; i++) {
      stringBuilder.append(ThemeManager.GameThemes.VALUES[i].strip.toLowerCase(Locale.ENGLISH) + (i + 1 < length ? ", " : ""));
    }

    return stringBuilder.toString();
  }

  public ThemeArgument(ArgumentsRegistry registry) {
    registry.mapArgument("buildbattleadmin", new LabeledCommandArgument("theme", "buildbattle.admin.theme", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/bba theme &c[add/remove] [gameType] [theme]", "/bba theme",
        "&7Theme commands to add or remove themes for the specified game type\n&6Permission: &7buildbattle.admin.theme")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length < 4) {
          new MessageBuilder("COMMANDS_WRONG_USAGE").asKey().value("/" + registry.getPlugin().getCommandAdminPrefix() + " &c[add/remove] [gameType] [theme]").send(sender);
          return;
        }

        String gameType = args[2];
        ThemeManager.GameThemes theme = null;

        for(ThemeManager.GameThemes gameTheme : ThemeManager.GameThemes.VALUES) {
          if(gameTheme.strip.equalsIgnoreCase(gameType)) {
            theme = gameTheme;
            break;
          }
        }

        if(theme == null) {
          new MessageBuilder("&cThere is no game type with this name. Possible types: " + printGameTypes()).prefix().send(sender);
          return;
        }

        String themeName = ChatColor.stripColor(args[3]);
        ThemeManager themeManager = ((Main) registry.getPlugin()).getThemeManager();

        switch(args[1].toLowerCase(Locale.ENGLISH)) {
        case "add":
          if(themeManager.isThemeBlacklisted(themeName)) {
            new MessageBuilder("COMMANDS_THEME_BLACKLISTED").asKey().prefix().send(sender);
            return;
          }

          if(themeManager.getThemes(theme).contains(themeName)) {
            new MessageBuilder("&cThe given theme already exists.").prefix().send(sender);
            return;
          }

          switch(theme) {
          case SOLO:
          case CLASSIC:
            themeManager.classicThemes.add(themeName);
            break;
          case TEAM:
          case TEAMS:
            themeManager.teamsThemes.add(themeName);
            break;
          case GUESS_THE_BUILD_EASY:
            themeManager.GTBThemesEasy.add(themeName);
            break;
          case GUESS_THE_BUILD_MEDIUM:
            themeManager.GTBThemesMedium.add(themeName);
            break;
          case GUESS_THE_BUILD_HARD:
            themeManager.GTBThemesHard.add(themeName);
            break;
          default:
            return;
          }

          themeManager.loadThemes(true);
          themeManager.saveThemesToConfig();
          break;
        case "remove":
          boolean contained = false;

          switch(theme) {
          case SOLO:
          case CLASSIC:
            contained = themeManager.classicThemes.remove(themeName);
            break;
          case TEAM:
          case TEAMS:
            contained = themeManager.teamsThemes.remove(themeName);
            break;
          case GUESS_THE_BUILD_EASY:
            contained = themeManager.GTBThemesEasy.remove(themeName);
            break;
          case GUESS_THE_BUILD_MEDIUM:
            contained = themeManager.GTBThemesMedium.remove(themeName);
            break;
          case GUESS_THE_BUILD_HARD:
            contained = themeManager.GTBThemesHard.remove(themeName);
            break;
          default:
            return;
          }

          if(!contained) {
            new MessageBuilder("&cThe given theme doesn't exists.").prefix().send(sender);
            return;
          }

          themeManager.loadThemes(true);
          themeManager.saveThemesToConfig();
          break;
        default:
          break;
        }
      }
    });
  }

}
