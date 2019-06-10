/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and contributors
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

package pl.plajer.buildbattle.arena.impl;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.arena.managers.plots.Plot;
import pl.plajer.buildbattle.arena.options.ArenaOption;
import pl.plajer.buildbattle.utils.Debugger;
import pl.plajer.buildbattle.utils.Partition;

/**
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 */
public class TeamArena extends SoloArena {

  public TeamArena(String id, Main plugin) {
    super(id, plugin);
  }

  @Override
  public void setMinimumPlayers(int amount) {
    if (amount <= 2) {
      Debugger.debug(Debugger.Level.WARN, "Minimum players amount for TEAM game mode arena cannot be less than 3! Setting amount to 3!");
      setOptionValue(ArenaOption.MINIMUM_PLAYERS, 3);
      return;
    }
    super.setMinimumPlayers(amount);
  }

  @Override
  public void distributePlots() {
    List<List<Player>> pairs = Partition.ofSize(new ArrayList<>(getPlayers()), 2);
    int i = 0;
    for (Plot plot : getPlotManager().getPlots()) {
      if (pairs.size() <= i) {
        break;
      }
      pairs.get(i).forEach(plot::addOwner);
      pairs.get(i).forEach(player -> getPlugin().getUserManager().getUser(player).setCurrentPlot(plot));
      i++;
    }
    /*if (!players.isEmpty()) {
      MessageUtils.errorOccurred();
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildBattle] [PLOT WARNING] Not enough plots in arena " + getID() + "!");
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[PLOT WARNING] Required " + Math.ceil((double) getPlayers().size() / 2) + " but have " + getPlotManager().getPlots().size());
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[PLOT WARNING] Instance was stopped!");
      ArenaManager.stopGame(false, this);
    }*/
  }

  @Override
  public String formatWinners(Plot plot, String string) {
    String str = string;
    if (plot.getOwners().size() == 1) {
      str = str.replace("%player%", plot.getOwners().get(0).getName());
    } else {
      str = str.replace("%player%", plot.getOwners().get(0).getName() + " & " + plot.getOwners().get(1).getName());
    }
    return str;
  }

  @Override
  public void voteForNextPlot() {
    if (getVotingPlot() != null) {
      for (Player player : getPlayers()) {
        getVotingPlot().setPoints(getVotingPlot().getPoints() + getPlugin().getUserManager().getUser(player).getStat(StatsStorage.StatisticType.LOCAL_POINTS));
        getPlugin().getUserManager().getUser(player).setStat(StatsStorage.StatisticType.LOCAL_POINTS, 0);
      }
    }
    for (Plot p : getPlotManager().getPlots()) {
      if (p.getOwners() != null && p.getOwners().size() == 2) {
        //removing second owner to not vote for same plot twice
        getQueue().remove(p.getOwners().get(1));
      }
    }
    voteRoutine();
  }

  @Override
  public boolean enoughPlayersToContinue() {
    return getPlayers().size() >= 2 && !getPlotManager().getPlot(getPlayers().get(0)).getOwners().contains(getPlayers().get(1));
  }

}
