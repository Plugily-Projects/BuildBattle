/*
 *  Village Defense 3 - Protect villagers from hordes of zombies
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

package pl.plajer.buildbattle3.scoreboards;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import pl.plajer.buildbattle3.ConfigPreferences;
import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.arena.Arena;
import pl.plajer.buildbattle3.utils.Util;

/**
 * Created by Tom on 31/01/2016.
 */
public class ScoreboardHandler {


    private Arena buildInstance;
    private ScoreboardLoader scoreboardLoader;
    private ScoreboardManager scoreboardManager;

    public ScoreboardHandler(Arena buildInstance) {
        this.buildInstance = buildInstance;
        scoreboardManager = Bukkit.getScoreboardManager();
        scoreboardLoader = new ScoreboardLoader();
    }


    public void updateScoreboard() {
        for(Player player : buildInstance.getPlayers()) {
            player.setScoreboard(loadScoreboard(player));
        }
    }


    private Scoreboard loadScoreboard(Player player) {
        ScoreboardLines scoreboardLines = scoreboardLoader.getScoreboardData().get(buildInstance.getGameState());
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        scoreboard.registerNewObjective("1", "dummy");
        Objective objective = scoreboard.getObjective("1");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(format(scoreboardLines.getTitle(), player));
        int i = scoreboardLines.getLines().size();
        for(String string : scoreboardLines.getLines()) {
            Score score = objective.getScore(format(string, player));
            score.setScore(i);
            i--;
        }
        return scoreboard;
    }


    private String format(String string, Player player) {
        string = string.replaceAll("%MIN_PLAYERS%", Integer.toString(buildInstance.getMinimumPlayers()));
        string = string.replaceAll("%PLAYERS%", Integer.toString(buildInstance.getPlayers().size()));
        string = string.replaceAll("%PLAYER%", player.getName());
        string = string.replaceAll("%THEME%", buildInstance.getTheme());
        string = string.replaceAll("%MIN_PLAYERS%", Integer.toString(buildInstance.getMinimumPlayers()));
        string = string.replaceAll("%MAX_PLAYERS%", Integer.toString(buildInstance.getMaximumPlayers()));
        string = string.replaceAll("%TIMER%", Integer.toString(buildInstance.getTimer()));
        string = string.replaceAll("%TIME_LEFT%", Long.toString(buildInstance.getTimeleft()));
        string = string.replaceAll("%FORMATTED_TIME_LEFT%", Util.formatIntoMMSS(buildInstance.getTimer()));
        string = string.replaceAll("%ARENA_ID%", buildInstance.getID());
        string = string.replaceAll("%MAPNAME%", buildInstance.getMapName());
        if(ConfigPreferences.isVaultEnabled()) {
            string = string.replaceAll("%MONEY%", Double.toString(Main.getEcon().getBalance(player.getName())));
            string = string.replaceAll("%GROUP%", Main.getPerms().getPrimaryGroup(player));
        }
        return string;
    }


}
