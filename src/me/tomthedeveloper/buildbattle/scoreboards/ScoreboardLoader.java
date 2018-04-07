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


    private GameAPI plugin;
    private HashMap<GameState, ScoreboardLines> scoreboardData = new HashMap<>();


    public ScoreboardLoader(GameAPI plugin) {
        this.plugin = plugin;
        pasteScoreboardConfig();
        loadLines();
    }

    public HashMap<GameState, ScoreboardLines> getScoreboardData() {
        return scoreboardData;
    }


    private void pasteScoreboardConfig() {

        File file = new File(plugin.getPlugin().getDataFolder() + File.separator + "scoreboard.yml");
        if(!file.exists()) {
            System.out.print("Creating new file scoreboards.yml");
            System.out.print("Writing to file scoreboards.yml");

            InputStream inputStream = ScoreboardLoader.class.getResourceAsStream("/scoreboard.yml");
            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file);
                int read;
                byte[] bytes = new byte[1024];
                while((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                System.out.println("Done!");
            } catch(IOException e) {
                e.printStackTrace();
            } finally {
                if(inputStream != null) {
                    try {
                        inputStream.close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
                if(outputStream != null) {
                    try {
                        outputStream.close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }

                }


            }
        }
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
