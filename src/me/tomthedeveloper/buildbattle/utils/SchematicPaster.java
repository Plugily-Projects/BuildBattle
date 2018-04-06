package me.tomthedeveloper.buildbattle.utils;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;

/**
 * Created by Tom on 7/08/2014.
 */
public class SchematicPaster {

    public static void pasteSchematic(String filename, Location location) {

        World worldf = Bukkit.getWorld(location.getWorld().getName());
        BukkitWorld BWf = new BukkitWorld(worldf);
        EditSession es = new EditSession(BWf, 1000000000);
        Vector v = new com.sk89q.worldedit.Vector(location.getX(), location.getY(), location.getZ());

        File file = new File("plugins" + File.separator + "WorldEdit" + File.separator + "schematics" + File.separator + filename + ".schematic");
        try {
            CuboidClipboard c1 = SchematicFormat.MCEDIT.load(file);

            try {
                c1.paste(es, v, false);
            } catch(MaxChangedBlocksException e) {
                e.printStackTrace();
            }
        } catch(IOException e) {
            e.printStackTrace();
        } catch(DataException e) {
            e.printStackTrace();
        }
    }
}
