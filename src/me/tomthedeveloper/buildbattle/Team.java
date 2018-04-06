package me.tomthedeveloper.buildbattle;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Tom on 12/07/2014.
 */
public enum Team {


    BLUE("BLUE", ChatColor.BLUE), RED("RED", ChatColor.RED), GREEN("GREEN", ChatColor.GREEN), NATURE("NATURE", ChatColor.WHITE), YELLOW("YELLOW", ChatColor.YELLOW), PURPLE("PURPLE", ChatColor.LIGHT_PURPLE);


    private ChatColor color;
    private String str;
    private HashMap<Material, Integer> teaminventory = new HashMap<Material, Integer>();
    private List<UUID> teamlist = new ArrayList<UUID>();

    Team(String str, ChatColor color) {
        this.color = color;
        this.str = str;
    }

    public static boolean hasTeam(Player p) {
        Team[] teams = {RED, BLUE, YELLOW, PURPLE, GREEN};
        boolean b = false;
        UUID uuid = p.getUniqueId();
        for(Team team : teams) {
            if(team.getUUIDs().contains(uuid)) {
                b = true;
                break;
            }
        }
        return b;

    }

    public static boolean hasTeam(UUID uuid) {
        Team[] teams = {RED, BLUE, YELLOW, PURPLE, GREEN};
        boolean b = false;
        for(Team team : teams) {
            if(team.getUUIDs().contains(uuid)) {
                b = true;
                break;
            }
        }
        return b;
    }

    public ChatColor getChatColor() {
        return color;
    }

    public String getString() {
        return str;
    }

    public HashMap<Material, Integer> getTeamInventory() {
        return teaminventory;
    }

    public void addMaterialToInventory(Material m) {
        if(teaminventory.containsKey(m)) {
            teaminventory.put(m, teaminventory.get(m) + 1);
        } else {
            teaminventory.put(m, 1);
        }
    }

    public void addMaterialToInventory(Material m, int i) {
        if(teaminventory.containsKey(m)) {
            teaminventory.put(m, teaminventory.get(m) + i);
        } else {
            teaminventory.put(m, i);
        }
    }

    public void addPlayer(Player p) {
        teamlist.add(p.getUniqueId());
    }

    public void addUUID(UUID uuid) {
        teamlist.add(uuid);
    }

    public void deletePlayer(Player p) {
        teamlist.remove(p.getUniqueId());
    }

    public void deleteUUID(UUID uuid) {
        teamlist.remove(uuid);
    }

    public int getSize() {
        return teamlist.size();
    }

    public ArrayList<Player> getPlayers() {
        ArrayList<Player> players = new ArrayList<Player>();
        for(int i = 0; i <= teamlist.size(); i++) {
            players.add(Bukkit.getPlayer(teamlist.get(i)));
        }
        return players;
    }

    public List<UUID> getUUIDs() {
        return teamlist;
    }

    public boolean isJoinable() {
        int red_int = Team.RED.getSize();

        int team_sizes[] = {Team.RED.getSize(), Team.BLUE.getSize(), Team.GREEN.getSize(), Team.YELLOW.getSize(), Team.PURPLE.getSize()};

        int i = getSize();

        for(int size : team_sizes) {
            if(i - size <= -2) return false;
        }

        return true;

    }


}
