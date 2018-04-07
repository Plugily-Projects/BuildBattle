package me.tomthedeveloper.buildbattle.scoreboards;

import me.tomthedeveloper.buildbattle.GameAPI;
import me.tomthedeveloper.buildbattle.game.GameState;
import me.tomthedeveloper.buildbattle.handlers.ConfigurationManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Created by Tom on 31/01/2016.
 */
class ScoreboardLoader {

    private HashMap<GameState, ScoreboardLines> scoreboardData = new HashMap<>();

    public ScoreboardLoader() {
        loadLines();
    }

    public HashMap<GameState, ScoreboardLines> getScoreboardData() {
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
            scoreboardData.put(GameState.fromString(section), scoreboardLines);
        }
    }
}
