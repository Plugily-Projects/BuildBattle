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

package plugily.projects.buildbattle.arena.states.guess;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.GuessArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.handlers.themes.BBTheme;
import plugily.projects.buildbattle.handlers.themes.ThemeManager;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.states.PluginInGameState;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.language.TitleBuilder;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.actionbar.ActionBar;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.inventory.common.item.SimpleClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>Created at 28.05.2022
 */
public class InGameState extends PluginInGameState {

  @Override
  public void handleCall(PluginArena arena) {
    super.handleCall(arena);
    GuessArena pluginArena = (GuessArena) getPlugin().getArenaRegistry().getArena(arena.getId());
    if(pluginArena == null) {
      return;
    }
    switch(pluginArena.getArenaInGameState()) {
      case THEME_VOTING:
        if(pluginArena.getBuildPlot() == null) {
          pluginArena.setNextPlot();
          new TitleBuilder("IN_GAME_MESSAGES_PLOT_GTB_THEME_BEING_SELECTED").asKey().arena(pluginArena).sendArena();
          openThemeSelectionInventoryToCurrentBuilder(pluginArena);
          break;
        }

        if(arena.getTimer() <= 0 || pluginArena.isCurrentThemeSet()) {
          forceSetTheme(pluginArena);

          new MessageBuilder("IN_GAME_MESSAGES_PLOT_GTB_ROUND").asKey().integer(pluginArena.getRound()).arena(pluginArena).sendArena();
          new TitleBuilder("IN_GAME_MESSAGES_PLOT_GTB_THEME_GUESS_TITLE").asKey().arena(pluginArena).sendArena();

          Bukkit.getScheduler().runTaskLater(getPlugin(), () -> pluginArena.getCurrentBuilders().forEach(player -> player.setGameMode(GameMode.CREATIVE)), 40);
          pluginArena.getCurrentBuilders().forEach(pluginArena::addMenuItem);

          setArenaTimer(getPlugin().getConfig().getInt("Time-Manager." + pluginArena.getArenaType().getPrefix() + ".In-Game"));
          pluginArena.setArenaInGameState(BaseArena.ArenaInGameState.BUILD_TIME);
          break;
        }
        break;
      case BUILD_TIME:
        // check not needed anymore
        // if(pluginArena.isCurrentThemeSet()) {
        int timer = arena.getTimer();

        if(timer <= 90) {
          if(timer == 90) {
            new MessageBuilder("IN_GAME_MESSAGES_PLOT_GTB_THEME_CHARS").asKey().arena(pluginArena).integer(pluginArena.getTheme().length()).sendArena();
          }
          sendThemeHints(arena, pluginArena);
        }
        if(timer <= 0) {
          //not all guessed
          new MessageBuilder("IN_GAME_MESSAGES_PLOT_GTB_THEME_WAS").asKey().value(pluginArena.getCurrentBBTheme().getTheme()).arena(pluginArena).sendArena();
          new TitleBuilder("IN_GAME_MESSAGES_PLOT_GTB_THEME_TITLE").asKey().value(pluginArena.getCurrentBBTheme().getTheme()).arena(pluginArena).sendArena();

          setArenaTimer(getPlugin().getConfig().getInt("Time-Manager." + pluginArena.getArenaType().getPrefix() + ".Round-Delay"));
          pluginArena.setArenaInGameState(BaseArena.ArenaInGameState.PLOT_VOTING);
        }
        //}
        handleBuildTime(pluginArena);
        break;
      case PLOT_VOTING:
        if(pluginArena.getRound() + 1 > pluginArena.getPlotList().size() * pluginArena.getArenaOption("GTB_ROUNDS_PER_PLOT")) {
          pluginArena.calculateWinnerPlot();
          adjustStatistics(pluginArena);

          pluginArena.teleportToWinnerPlot();
          pluginArena.executeEndRewards();
          getPlugin().getArenaManager().stopGame(false, arena);
        }
//round delay
        if(arena.getTimer() <= 0) {
          pluginArena.resetBuildPlot();
          setArenaTimer(getPlugin().getConfig().getInt("Time-Manager." + pluginArena.getArenaType().getPrefix() + ".Voting.Theme"));
          pluginArena.setArenaInGameState(BaseArena.ArenaInGameState.THEME_VOTING);
        }
        break;
      default:
        break;
    }
    // no players - stop game
    if(!pluginArena.enoughPlayersToContinue()) {
      getPlugin().getArenaManager().stopGame(true, pluginArena);
    }
  }

  private void sendThemeHints(PluginArena arena, GuessArena pluginArena) {
    for(Player player : arena.getPlayers()) {
      if(pluginArena.getCurrentBuilders().contains(player)) {
        continue;
      }
      if(pluginArena.getWhoGuessed().contains(player)) {
        getPlugin().getActionBarManager().addActionBar(player, new ActionBar(new MessageBuilder(pluginArena.getCurrentBBTheme().getTheme()), ActionBar.ActionBarType.DISPLAY));
        continue;
      }
      int themeLength = pluginArena.getCurrentBBTheme().getTheme().length();
      List<Integer> charsAt = new ArrayList<>(themeLength);

      for(int i = 0; i < themeLength; i++) {
        if(!pluginArena.getRemovedCharsAt().contains(i)) {
          charsAt.add(i);
        }
      }

      if(themeLength - pluginArena.getRemovedCharsAt().size() > 2) {
        int timer = arena.getTimer();

        if(timer % 10 == 0 && timer <= 70) {
          pluginArena.getRemovedCharsAt().add(charsAt.get(charsAt.size() == 1 ? 0 : pluginArena.getPlugin().getRandom().nextInt(charsAt.size())));
          continue;
        }
      }

      StringBuilder actionbar = new StringBuilder();
      for(int i = 0; i < themeLength; i++) {
        char charAt = pluginArena.getCurrentBBTheme().getTheme().charAt(i);

        if(Character.isWhitespace(charAt)) {
          actionbar.append("  ");
          continue;
        }
        if(pluginArena.getRemovedCharsAt().contains(i)) {
          actionbar.append(charAt).append(' ');
          continue;
        }
        actionbar.append("_ ");
      }
      getPlugin().getActionBarManager().addActionBar(player, new ActionBar(new MessageBuilder(actionbar.toString()), ActionBar.ActionBarType.DISPLAY));
    }
  }

  private void forceSetTheme(GuessArena pluginArena) {
    if(pluginArena.isCurrentThemeSet()) {
      return;
    }
    BBTheme.Difficulty difficulty = BBTheme.Difficulty.EASY;
    switch(pluginArena.getPlugin().getRandom().nextInt(2 + 1)) {
      case 1:
        difficulty = BBTheme.Difficulty.MEDIUM;
        break;
      case 2:
        difficulty = BBTheme.Difficulty.HARD;
        break;
      default:
        break;
    }

    setTheme(pluginArena, getThemeByDifficulty(pluginArena, difficulty));
    pluginArena.getCurrentBuilders().forEach(player ->
        getPlugin().getActionBarManager().addActionBar(player, new ActionBar(new MessageBuilder("IN_GAME_MESSAGES_PLOT_GTB_THEME_NAME").asKey().arena(pluginArena),
            ActionBar.ActionBarType.DISPLAY)));

    pluginArena.getCurrentBuilders().forEach(HumanEntity::closeInventory);
  }


  private void openThemeSelectionInventoryToCurrentBuilder(GuessArena pluginArena) {
    if(pluginArena.getCurrentBuilders().isEmpty()) {
      return;
    }

    pluginArena.getCurrentBuilders().forEach(HumanEntity::closeInventory);

    NormalFastInv gui = new NormalFastInv(9 * 3, new MessageBuilder("MENU_THEME_GTB_INVENTORY").asKey().build());
    gui.addClickHandler(inventoryClickEvent -> inventoryClickEvent.setCancelled(true));
    gui.addCloseHandler(event -> {
      if(!pluginArena.isCurrentThemeSet()) {
        Bukkit.getScheduler().runTask(getPlugin(), () -> event.getPlayer().openInventory(event.getInventory()));
      }
    });


    BBTheme easy = getThemeByDifficulty(pluginArena, BBTheme.Difficulty.EASY);
    gui.setItem(11, new SimpleClickableItem(new ItemBuilder(Material.PAPER).name(getThemeItemName(pluginArena).value(easy.getTheme()).build())
        .lore(getThemeItemLore(pluginArena).value(new MessageBuilder("MENU_THEME_GTB_DIFFICULTIES_EASY").asKey().build()).integer(easy.getDifficulty().getPointsReward()).build().split(";")).build(), event -> {
      setTheme(pluginArena, easy);
    }));

    BBTheme medium = getThemeByDifficulty(pluginArena, BBTheme.Difficulty.MEDIUM);
    gui.setItem(13, new SimpleClickableItem(new ItemBuilder(Material.PAPER).name(getThemeItemName(pluginArena).value(medium.getTheme()).build())
        .lore(getThemeItemLore(pluginArena).value(new MessageBuilder("MENU_THEME_GTB_DIFFICULTIES_MEDIUM").asKey().build()).integer(medium.getDifficulty().getPointsReward()).build().split(";")).build(), event -> {
      setTheme(pluginArena, medium);
    }));

    BBTheme hard = getThemeByDifficulty(pluginArena, BBTheme.Difficulty.HARD);
    gui.setItem(15, new SimpleClickableItem(new ItemBuilder(Material.PAPER).name(getThemeItemName(pluginArena).value(hard.getTheme()).build())
        .lore(getThemeItemLore(pluginArena).value(new MessageBuilder("MENU_THEME_GTB_DIFFICULTIES_HARD").asKey().build()).integer(hard.getDifficulty().getPointsReward()).build().split(";")).build(), event -> {
      setTheme(pluginArena, hard);
    }));

    getPlugin().getDebugger().debug("Opened Theme Selector for {0}", pluginArena.getCurrentBuilders().toString());
    pluginArena.getCurrentBuilders().forEach(gui::open);
  }

  public MessageBuilder getThemeItemName(GuessArena pluginArena) {
    return new MessageBuilder("MENU_THEME_GTB_ITEM_NAME").asKey().arena(pluginArena);
  }

  public MessageBuilder getThemeItemLore(GuessArena pluginArena) {
    return new MessageBuilder("MENU_THEME_GTB_ITEM_LORE").asKey().arena(pluginArena);
  }


  private BBTheme getThemeByDifficulty(GuessArena pluginArena, BBTheme.Difficulty difficulty) {
    List<String> themes = pluginArena.getPlugin().getThemeManager().getThemes(ThemeManager.GameThemes.getByDifficulty(difficulty));
    List<String> themesFilter = new ArrayList<>(themes);
    themesFilter.removeAll(pluginArena.getPlayedThemes());
    if(themesFilter.isEmpty()) {
      themesFilter = themes;
    }
    String themeName = themesFilter.get(getPlugin().getRandom().nextInt(themesFilter.size()));
    BBTheme theme = new BBTheme(themeName, difficulty);
    pluginArena.getPlayedThemes().add(themeName);
    return theme;
  }

  private void setTheme(GuessArena pluginArena, BBTheme theme) {
    pluginArena.setCurrentTheme(theme);
  }

  private void adjustStatistics(GuessArena pluginArena) {
    for(Plot plot : pluginArena.getPlotManager().getTopPlotsOrder()) {
      plot.getMembers().forEach(player -> {
        User user = getPlugin().getUserManager().getUser(player);
        /*
        //ToDo GTB stats
        if(plot.getPoints() > user.getStatistic("POINTS_HIGHEST")) {
          user.setStatistic("POINTS_HIGHEST", plot.getPoints());
        }
        */
        user.adjustStatistic("POINTS_TOTAL", plot.getPoints());
        if(plot == pluginArena.getWinnerPlot()) {
          user.adjustStatistic("WINS", 1);
        } else {
          user.adjustStatistic("LOSES", 1);
        }
        getPlugin().getUserManager().addExperience(player, 5);
        /*
        if(plot.getPoints() > user.getStatistic("POINTS_HIGHEST_WIN")) {
          user.setStatistic("POINTS_HIGHEST_WIN", plot.getPoints());
        }
        */
      });
    }
  }

  private void handleBuildTime(GuessArena pluginArena) {
    int timer = pluginArena.getTimer();

    for(int timers : getPlugin().getConfig().getIntegerList("Time-Manager.Time-Left-Intervals")) {
      if(timers == timer) {
        pluginArena.sendBuildLeftTimeMessage();
        break;
      }
    }
    pluginArena.checkPlayerOutSidePlot();
  }
}
