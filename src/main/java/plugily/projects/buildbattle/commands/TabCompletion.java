package plugily.projects.buildbattle.commands;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TabCompletion implements TabCompleter {
    private final HashMultimap<String, String> completesListMap;

    public TabCompletion() {
        completesListMap = HashMultimap.create();
        add("bb", "join", "randomjoin", "stats", "leave", "top", "create");
        add("bba", "list", "stop", "forcestart", "reload", "delete", "addplot", "removeplot", "addnpc", "settheme", "votes", "plotwand");
        add("arena", "[arena]");
        add("soloteam", "[solo/team]");
        add("onlineplayer", "(online player)");
        add("statistic", "[statistic]");
        add("theme", "(theme)");
        add("plotID", "[plot ID]");
        add("addset", "[add/set]");
        add("amount", "[amount]");
        add("player", "(player)");
    }

    private void add(String key, String... args) {
        completesListMap.putAll(key, Arrays.asList(args));
    }

    private List<String> getPartial(String token, Iterable<String> collection) {
        return StringUtil.copyPartialMatches(token, collection, new ArrayList<>());
    }

    public
    @Override List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }
        switch (args.length) {
            case 1:
                if (label.equalsIgnoreCase("bb") || label.equalsIgnoreCase("buildbattle")) {
                    return getPartial(args[0], completesListMap.get("bb"));
                }
                if (label.equalsIgnoreCase("bba") || label.equalsIgnoreCase("buildbattleadmin")) {
                    return getPartial(args[0], completesListMap.get("bba"));
                }
            case 2:
                if (completesListMap.get("bb").contains(args[0].toLowerCase())) {
                    if (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("create")) {
                        return getPartial(args[1], completesListMap.get("arena"));
                    }
                    if (args[0].equalsIgnoreCase("randomjoin")) {
                        return getPartial(args[1], completesListMap.get("soloteam"));
                    }
                    if (args[0].equalsIgnoreCase("stats")) {
                        return getPartial(args[1], completesListMap.get("onlineplayer"));
                    }
                    if (args[0].equalsIgnoreCase("top")) {
                        return getPartial(args[1], completesListMap.get("statistic"));
                    }

                }
                if (completesListMap.get("bba").contains(args[0].toLowerCase())) {
                    if (args[0].equalsIgnoreCase("forcestart")) {
                        return getPartial(args[1], completesListMap.get("theme"));
                    }
                    if (args[0].equalsIgnoreCase("delete")) {
                        return getPartial(args[1], completesListMap.get("arena"));
                    }
                    if (args[0].equalsIgnoreCase("removeplot")) {
                        return getPartial(args[1], completesListMap.get("arena"));
                    }
                    if (args[0].equalsIgnoreCase("settheme")) {
                        return getPartial(args[1], completesListMap.get("theme"));
                    }
                    if (args[0].equalsIgnoreCase("votes")) {
                        return getPartial(args[1], completesListMap.get("addset"));
                    }
                }
            case 3:
                if (completesListMap.get("bba").contains(args[0].toLowerCase())) {
                    if (args[0].equalsIgnoreCase("removeplot")) {
                        return getPartial(args[1], completesListMap.get("plotID"));
                    }
                    if (args[0].equalsIgnoreCase("votes")) {
                        return getPartial(args[1], completesListMap.get("amount"));
                    }
                }
            case 4:
                if (completesListMap.get("bba").contains(args[0].toLowerCase()) && args[0].equalsIgnoreCase("votes")) {
                   return getPartial(args[1], completesListMap.get("player"));
                }
            default: return ImmutableList.of();
        }
    }
}
