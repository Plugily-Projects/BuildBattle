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

package pl.plajer.buildbattle.arena.impl;

import org.bukkit.entity.Player;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.arena.managers.plots.Plot;

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
    //todo test
    return getPlayers().size() >= 2 && !getPlotManager().getPlot(getPlayers().get(0)).getOwners().contains(getPlayers().get(1));
  }

}
