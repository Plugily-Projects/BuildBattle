package pl.plajer.buildbattle.commands;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 7/08/2014.
 */
public class SignCommands implements CommandExecutor {

    private Main plugin;
    private int counter = 0;

    public SignCommands(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player && command.getLabel().equalsIgnoreCase("addsigns")) {
            Player player = (Player) commandSender;
            Selection selection = plugin.getWorldEditPlugin().getSelection(player);
            int i = plugin.getConfig().getConfigurationSection("signs").getKeys(false).size();
            i = i + 2;
            if(selection == null) {
                player.sendMessage("You have to select a region with 1 or more signs in it with World Edit before clicking on the sign");
                return true;
            }
            if(selection instanceof CuboidSelection) {
                CuboidSelection cuboidSelection = (CuboidSelection) selection;
                Vector min = cuboidSelection.getNativeMinimumPoint();
                Vector max = cuboidSelection.getNativeMaximumPoint();
                for(int x = min.getBlockX(); x <= max.getBlockX(); x = x + 1) {
                    for(int y = min.getBlockY(); y <= max.getBlockY(); y = y + 1) {
                        for(int z = min.getBlockZ(); z <= max.getBlockZ(); z = z + 1) {
                            Location tmpblock = new Location(player.getWorld(), x, y, z);
                            if(tmpblock.getBlock().getState() instanceof Sign && !getSigns().contains(tmpblock.getBlock().getState())) {
                                Util.saveLoc("signs." + i, tmpblock);
                                counter++;
                                i++;
                            }

                        }
                    }
                }

            } else {
                if(selection.getMaximumPoint().getBlock().getState() instanceof Sign && !getSigns().contains(selection.getMaximumPoint().getBlock().getState())) {
                    plugin.getSignManager().registerSign((Sign) selection.getMaximumPoint().getBlock().getState());
                    Util.saveLoc("signs." + i, selection.getMaximumPoint());
                    counter++;
                    i++;
                }
                if(selection.getMinimumPoint().getBlock().getState() instanceof Sign && !getSigns().contains(selection.getMinimumPoint().getBlock().getState())) {
                    plugin.getSignManager().registerSign((Sign) selection.getMinimumPoint().getBlock().getState());
                    Util.saveLoc("signs." + i, selection.getMinimumPoint());
                    counter++;
                    i++;
                }
            }
            plugin.saveConfig();
            player.sendMessage(ChatColor.GREEN + "" + counter + " signs added!");

        }
        return true;
    }

    public List<Sign> getSigns() {
        List<Sign> list = new ArrayList<>();
        for(String s : plugin.getConfig().getConfigurationSection("signs").getKeys(false)) {
            s = "signs." + s;
            Location location = Util.getLocation(s);
            if(location.getBlock().getState() instanceof Sign) list.add((Sign) location.getBlock().getState());
        }
        return list;
    }
}
