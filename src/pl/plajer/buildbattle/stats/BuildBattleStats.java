package pl.plajer.buildbattle.stats;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.handlers.ConfigurationManager;
import pl.plajer.buildbattle.handlers.UserManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Tom on 30/12/2015.
 */
public enum BuildBattleStats {
    BLOCKS_PLACED("blocksplaced"), BLOCKS_BROKEN("blocksbroken"), GAMES_PLAYED("gamesplayed"), WINS("wins"), LOSES("loses"), HIGHEST_WIN("highestwin"), PARTICLES("particles");

    public static Main plugin;
    public static List<String> STATISTICS = new ArrayList<>();

    static {
        STATISTICS.add("blocksplaced");
        STATISTICS.add("blocksbroken");
        STATISTICS.add("gamesplayed");
        STATISTICS.add("wins");
        STATISTICS.add("loses");
        STATISTICS.add("highestwin");
        STATISTICS.add("particles");
    }

    private String name;

    BuildBattleStats(String name) {
        this.name = name;
    }

    private static Map sortByValue(Map unsortMap) {
        List list = new LinkedList(unsortMap.entrySet());

        list.sort((o1, o2) -> ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue()));

        Map sortedMap = new LinkedHashMap();
        for(Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public Map<UUID, Integer> getStats() {
        if(plugin.isDatabaseActivated()) return plugin.getMySQLDatabase().getColumn(name);
        else {
            FileConfiguration config = ConfigurationManager.getConfig("stats");
            Map<UUID, Integer> stats = new LinkedHashMap<>();
            for(String string : config.getKeys(false)) {
                stats.put(UUID.fromString(string), config.getInt(string + "." + name));
            }
            return sortByValue(stats);
        }

    }

    public int getStat(Player player) {
        return UserManager.getUser(player.getUniqueId()).getInt(name);
    }


}
