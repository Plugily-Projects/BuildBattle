package pl.plajer.buildbattle.scoreboards;


import org.bukkit.ChatColor;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tom on 31/01/2016.
 */
class ScoreboardLines {


    private LinkedList<String> lines = new LinkedList<>();
    private String title = null;


    public List<String> getLines() {
        return lines;
    }

    public String getTitle() {
        return ChatColor.translateAlternateColorCodes('&', title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addLine(String string) {
        lines.add(ChatColor.translateAlternateColorCodes('&', string));
    }

}
