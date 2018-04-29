package me.tomthedeveloper.buildbattle.scoreboards;

import me.tomthedeveloper.buildbattle.arena.ArenaState;
import me.tomthedeveloper.buildbattle.handlers.ConfigurationManager;
import org.bukkit.configuration.file.FileConfiguration;

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
