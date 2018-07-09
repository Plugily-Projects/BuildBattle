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

package pl.plajer.buildbattle3.themevoter;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.plajer.buildbattle3.arena.Arena;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * @author Plajer
 * <p>
 * Created at 08.07.2018
 */
public class VotePoll {

    private Arena arena;
    private Map<String, Integer> votedThemes = new HashMap<>();
    //todo cleaning
    private Map<Player, String> playerVote = new HashMap<>();

    public VotePoll(Arena arena, List<String> votedThemes) {
        this.arena = arena;
        for(String theme : votedThemes){
            this.votedThemes.put(theme, 0);
        }
    }

    public Arena getArena() {
        return arena;
    }

    public Map<String, Integer> getVotedThemes() {
        return votedThemes;
    }

    public boolean addVote(Player player, String theme) {
        if(playerVote.containsKey(player)) {
            if(playerVote.get(player).equals(theme)) {
                return false;
            }
            votedThemes.put(playerVote.get(player), votedThemes.get(playerVote.get(player)) - 1);
        }
        votedThemes.put(theme, votedThemes.get(theme) + 1);
        playerVote.put(player, theme);
        return true;
    }

    public Map<Player, String> getPlayerVote() {
        return playerVote;
    }

    public String getVotedTheme() {
        LinkedHashMap<String, Integer> bestTheme = (LinkedHashMap<String, Integer>) sortByValue(votedThemes);
        return (String) bestTheme.keySet().toArray()[bestTheme.keySet().toArray().length - 1];
    }

    private static Map sortByValue(Map unsortMap) {
        List list = new LinkedList(unsortMap.entrySet());
        list.sort((o1, o2) -> ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue()));
        Map sortedMap = new LinkedHashMap();
        for(Object aList : list) {
            Map.Entry entry = (Map.Entry) aList;
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

}
