/*
 *  Village Defense 3 - Protect villagers from hordes of zombies
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

package pl.plajer.buildbattle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.arena.ArenaState;
import pl.plajer.buildbattle.user.UserManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Tom on 17/08/2015.
 */
public class PlotManager {

    private List<BuildPlot> plots = new ArrayList<>();
    private List<BuildPlot> plotsToClear = new ArrayList<>();
    private Arena buildInstance;

    public PlotManager(Arena buildInstance) {
        this.buildInstance = buildInstance;
    }

    public void addBuildPlot(BuildPlot buildPlot) {
        plots.add(buildPlot);
    }

    public void distributePlots() {
        List<Player> players = new ArrayList<>(buildInstance.getPlayers());
        for(BuildPlot buildPlot : plots) {
            if(!players.isEmpty() && buildPlot.getOwner() == null && getPlot(players.get(0)) == null) {
                buildPlot.setOwner(players.get(0).getUniqueId());
                UserManager.getUser(players.get(0).getUniqueId()).setObject(buildPlot, "plot");

                players.remove(0);
            } else {
                break;
            }
        }
        if(!players.isEmpty()) {
            System.out.print("YOU HAVENT SET ENOUGH PLOTS! SET FOR ARENA " + buildInstance.getID() + ". YOU HAVE TO SET " + players.size() + " MORE PLOTS!");
            System.out.print("STOPPING THE GAME");
            buildInstance.setGameState(ArenaState.ENDING);
        }
    }

    public BuildPlot getPlot(Player player) {
        for(BuildPlot buildPlot : plots) {
            if(buildPlot.getOwner() != null) {
                if(buildPlot.getOwner() == player.getUniqueId()) return buildPlot;
            }
        }
        return null;
    }

    public BuildPlot getPlot(UUID uuid) {
        for(BuildPlot buildPlot : plots) {
            if(buildPlot.getOwner() != null) {
                if(buildPlot.getOwner().equals(uuid)) return buildPlot;
            }
        }
        return null;
    }

    public void resetQeuedPlots() {
        for(BuildPlot buildPlot : plotsToClear) {
            buildPlot.reset();
        }
        plotsToClear.clear();
    }

    public boolean isPlotsCleared() {
        return plotsToClear.isEmpty();
    }

    public void resetPlotsGradually() {
        if(plotsToClear.isEmpty()) return;

        plotsToClear.get(0).reset();
        plotsToClear.remove(0);
    }

    public void teleportToPlots() {
        for(BuildPlot buildPlot : plots) {
            if(buildPlot.getOwner() != null) {
                Location tploc = buildPlot.getCenter();
                while(tploc.getBlock().getType() != Material.AIR) tploc = tploc.add(0, 1, 0);
                Player player = Bukkit.getServer().getPlayer(buildPlot.getOwner());
                if(player != null) {
                    player.teleport(buildPlot.getCenter());
                }
            }
        }
    }

    public List<BuildPlot> getPlots() {
        return plots;
    }

}
