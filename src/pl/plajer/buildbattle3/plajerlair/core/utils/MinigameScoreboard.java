/*
 * BuildBattle 3 - Ultimate building competition minigame
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
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

package pl.plajer.buildbattle3.plajerlair.core.utils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * Create game scoreboard here
 */
public class MinigameScoreboard {

  private final String name, criterion;

  private final Scoreboard bukkitScoreboard;
  private final Objective obj;
  private String title;
  private Row[] rows = new Row[0];
  private List<Row> rowCache = new ArrayList<>();
  private boolean finished = false;

  public MinigameScoreboard(String name, String criterion, String title) {
    this.name = name;
    this.criterion = criterion;
    this.title = title;

    this.bukkitScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    this.obj = this.bukkitScoreboard.registerNewObjective(name, criterion);

    this.obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    this.obj.setDisplayName(title);
  }

  public String getName() {
    return name;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;

    this.obj.setDisplayName(title);
  }

  public Row[] getRows() {
    return rows;
  }

  public void display(Player player) {
    player.setScoreboard(this.bukkitScoreboard);
  }

  public @Nullable
  Row addRow(String message) {
    if (this.finished) {
      new NullPointerException("Can not add rows if scoreboard is already finished").printStackTrace();
      return null;
    }

    try {
      final Row row = new Row(this, message, rows.length);

      this.rowCache.add(row);

      return row;
    } catch (Exception e) {
      return null;
    }
  }

  public void finish() {
    if (this.finished) {
      new NullPointerException("Can not finish if scoreboard is already finished").printStackTrace();
      return;
    }

    this.finished = true;

    for (int i = rowCache.size() - 1; i >= 0; i--) {
      final Row row = rowCache.get(i);

      final Team team = this.bukkitScoreboard.registerNewTeam(name + "." + criterion + "." + (i + 1));
      row.team = team;
      team.addEntry(ChatColor.values()[i] + "");
      this.obj.getScore(ChatColor.values()[i] + "").setScore(rowCache.size() - i);

      row.team = team;
      row.setMessage(row.message);
    }

    this.rows = rowCache.toArray(new Row[0]);
  }


  public static class Row {

    private final MinigameScoreboard scoreboard;
    private final int rownInScoreboard;
    private Team team;
    private String message;

    public Row(MinigameScoreboard sb, String message, int row) {
      this.scoreboard = sb;
      this.message = message;
      this.rownInScoreboard = row;
    }

    public MinigameScoreboard getScoreboard() {
      return scoreboard;
    }

    public int getRownInScoreboard() {
      return rownInScoreboard;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;

      if (scoreboard.finished) {
        String partOne = getFirstSplit(message);
        String partTwo;
        if (getFirstSplit(message).endsWith("§")) {
          partOne = partOne.substring(0, partOne.length() - 1);
          partTwo = getFirstSplit("§" + getSecondSplit(message));
        } else {
          partTwo = getFirstSplit(ChatColor.getLastColors(partOne) + getSecondSplit(message));
        }
        this.team.setPrefix(partOne);
        this.team.setSuffix(partTwo);
      }
    }

    private String getFirstSplit(String s) {
      return s.length() > 16 ? s.substring(0, 16) : s;
    }

    private String getSecondSplit(String s) {
      if (s.length() > 32) {
        s = s.substring(0, 32);
      }
      return s.length() > 16 ? s.substring(16) : "";
    }
  }

}
