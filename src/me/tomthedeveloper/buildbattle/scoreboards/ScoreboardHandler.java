package me.tomthedeveloper.buildbattle.scoreboards;

import me.TomTheDeveloper.Game.GameInstance;
import me.tomthedeveloper.buildbattle.Main;
import me.tomthedeveloper.buildbattle.ConfigPreferences;
import me.tomthedeveloper.buildbattle.instance.BuildInstance;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

/**
 * Created by Tom on 31/01/2016.
 */
public class ScoreboardHandler {


    private BuildInstance buildInstance;
    private ScoreboardLoader scoreboardLoader;
    private ScoreboardManager scoreboardManager;

    public ScoreboardHandler(BuildInstance buildInstance) {
        this.buildInstance = buildInstance;
        scoreboardManager = Bukkit.getScoreboardManager();
        scoreboardLoader = new ScoreboardLoader(GameInstance.getPlugin());
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
        string = string.replaceAll("%MIN_PLAYERS%", Integer.toString(buildInstance.getMIN_PLAYERS()));
        string = string.replaceAll("%PLAYERS%", Integer.toString(buildInstance.getPlayers().size()));
        string = string.replaceAll("%PLAYER%", player.getName());
        string = string.replaceAll("%THEME%", buildInstance.getTheme());
        string = string.replaceAll("%MIN_PLAYERS%", Integer.toString(buildInstance.getMIN_PLAYERS()));
        string = string.replaceAll("%MAX_PLAYERS%", Integer.toString(buildInstance.getMAX_PLAYERS()));
        string = string.replaceAll("%TIMER%", Integer.toString(buildInstance.getTimer()));
        string = string.replaceAll("%TIME_LEFT%", Long.toString(buildInstance.getTimeleft()));
        string = string.replaceAll("%FORMATTED_TIME_LEFT%", buildInstance.getFormattedTimeLeft());
        string = string.replaceAll("%ARENA_ID%", buildInstance.getID());
        string = string.replaceAll("%MAPNAME%", buildInstance.getMapName());
        if(ConfigPreferences.isVaultEnabled()) {
            string.replaceAll("%MONEY%", Double.toString(Main.getEcon().getBalance(player.getName())));
            string.replaceAll("%GROUP%", Main.getPerms().getPrimaryGroup(player));
        }
        return string;
    }


}
