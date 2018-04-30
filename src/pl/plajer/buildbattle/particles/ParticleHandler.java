package pl.plajer.buildbattle.particles;

import pl.plajer.buildbattle.BuildPlot;
import pl.plajer.buildbattle.ConfigPreferences;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.arena.Arena;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import pl.plajer.buildbattle.arena.ArenaRegistry;

import java.util.ArrayList;

/**
 * Created by Tom on 23/08/2015.
 */
public class ParticleHandler extends BukkitRunnable {


    private static int amount = ConfigPreferences.getAmountFromOneParticle();
    private Main plugin;
    private long tick;
    private int particleOffset = ConfigPreferences.getParticlOffset();


    public ParticleHandler(Main main) {
        plugin = main;
    }

    public void start() {
        tick = ConfigPreferences.getParticleRefreshTick();
        this.runTaskTimer(plugin, tick, tick);
    }

    @Override
    public void run() {
        for(Arena arena : ArenaRegistry.getArenas()) {
            for(BuildPlot buildPlot : arena.getPlotManager().getPlots()) {
                for(Location location : buildPlot.getParticles().keySet()) {
                    if(!arena.getPlayers().isEmpty())
                        buildPlot.getParticles().get(location).display((float) particleOffset, (float) particleOffset, (float) particleOffset, (float) 1, amount, location, new ArrayList<>(arena.getPlayers()));
                }
            }
        }
    }
}
