package me.tomthedeveloper.buildbattle.particles;

import me.TomTheDeveloper.Game.GameInstance;
import me.tomthedeveloper.buildbattle.Main;
import me.tomthedeveloper.buildbattle.BuildPlot;
import me.tomthedeveloper.buildbattle.ConfigPreferences;
import me.tomthedeveloper.buildbattle.instance.BuildInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
        for(GameInstance gameInstance : plugin.getGameAPI().getGameInstanceManager().getGameInstances()) {
            BuildInstance buildInstance = (BuildInstance) gameInstance;
            for(BuildPlot buildPlot : buildInstance.getPlotManager().getPlots()) {
                for(Location location : buildPlot.getParticles().keySet()) {
                    if(!gameInstance.getPlayers().isEmpty())
                        buildPlot.getParticles().get(location).display((float) particleOffset, (float) particleOffset, (float) particleOffset, (float) 1, amount, location, new ArrayList<>(gameInstance.getPlayers()));


                }
            }
        }
    }
}
