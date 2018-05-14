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

package pl.plajer.buildbattle.scoreboards;

import org.bukkit.configuration.file.FileConfiguration;
import pl.plajer.buildbattle.arena.ArenaState;
import pl.plajer.buildbattle.handlers.ConfigurationManager;

import java.util.HashMap;

/**
 * Created by Tom on 31/01/2016.
 */
class ScoreboardLoader {

    private HashMap<ArenaState, ScoreboardLines> scoreboardData = new HashMap<>();

    public ScoreboardLoader() {
        loadLines();
    }

    public HashMap<ArenaState, ScoreboardLines> getScoreboardData() {
        return scoreboardData;
    }

    private void loadLines() {
        FileConfiguration config = ConfigurationManager.getConfig("scoreboard");
        for(String section : config.getConfigurationSection("scoreboard").getKeys(false)) {
            ScoreboardLines scoreboardLines = new ScoreboardLines();
            for(String line : config.getStringList("scoreboard." + section + ".lines")) {
                scoreboardLines.addLine(line);
            }
            scoreboardLines.setTitle(config.getString("scoreboard." + section + ".title"));
            scoreboardData.put(ArenaState.fromString(section), scoreboardLines);
        }
    }
}
