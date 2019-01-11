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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.bukkit.entity.Player;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.arena.managers.plots.PlotManager;

/**
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 */
public class ArenaTemp {

  private Main plugin;
  private Map<Integer, List<Player>> topPlayers = new HashMap<>();
  private String theme = "Theme";
  private PlotManager plotManager;
  private Queue<Player> voteQueue = new LinkedList<>();

}
