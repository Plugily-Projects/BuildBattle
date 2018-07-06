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

package pl.plajer.buildbattle3.plots;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import pl.plajer.buildbattle3.arena.Arena;
import pl.plajer.buildbattle3.arena.ArenaManager;
import pl.plajer.buildbattle3.user.UserManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Tom on 17/08/2015.
 */
public class PlotManager {

    private List<Plot> plots = new ArrayList<>();
    private List<Plot> plotsToClear = new ArrayList<>();
    private Arena buildInstance;

    public PlotManager(Arena buildInstance) {
        this.buildInstance = buildInstance;
    }

    public void addBuildPlot(Plot buildPlot) {
        plots.add(buildPlot);
    }

    public void distributePlots() {
        List<Player> players = new ArrayList<>(buildInstance.getPlayers());
        int times = 1;
        if(buildInstance.getArenaType() == Arena.ArenaType.TEAM) times++;
        for(int i = 0; i < times; i++) {
            for(Plot buildPlot : plots) {
                if(!players.isEmpty() && buildPlot.getOwners() == null || buildPlot.getOwners().isEmpty() && getPlot(players.get(0)) == null) {
                    buildPlot.addOwner(players.get(0).getUniqueId());
                    UserManager.getUser(players.get(0).getUniqueId()).setObject(buildPlot, "plot");

                    players.remove(0);
                } else {
                    break;
                }
            }
        }
        if(!players.isEmpty()) {
            System.out.print("YOU HAVENT SET ENOUGH PLOTS! SET FOR ARENA " + buildInstance.getID() + ". YOU HAVE TO SET " + players.size() + " MORE PLOTS!");
            System.out.print("STOPPING THE GAME");
            ArenaManager.stopGame(false, buildInstance);
        }
    }

    public Plot getPlot(Player player) {
        for(Plot buildPlot : plots) {
            if(buildPlot.getOwners() != null || !buildPlot.getOwners().isEmpty()) {
                if(buildPlot.getOwners().contains(player.getUniqueId())) return buildPlot;
            }
        }
        return null;
    }

    public Plot getPlot(UUID uuid) {
        for(Plot buildPlot : plots) {
            if(buildPlot.getOwners() != null || !buildPlot.getOwners().isEmpty()) {
                if(buildPlot.getOwners().contains(uuid)) return buildPlot;
            }
        }
        return null;
    }

    public void resetQeuedPlots() {
        for(Plot buildPlot : plotsToClear) {
            buildPlot.fullyResetPlot();
        }
        plotsToClear.clear();
    }

    public boolean isPlotsCleared() {
        return plotsToClear.isEmpty();
    }

    public void resetPlotsGradually() {
        if(plotsToClear.isEmpty()) return;

        plotsToClear.get(0).fullyResetPlot();
        plotsToClear.remove(0);
    }

    public void teleportToPlots() {
        for(Plot buildPlot : plots) {
            if(buildPlot.getOwners() != null || !buildPlot.getOwners().isEmpty()) {
                Location tploc = buildPlot.getCenter();
                while(tploc.getBlock().getType() != Material.AIR) tploc = tploc.add(0, 1, 0);
                for(UUID u : buildPlot.getOwners()) {
                    Player player = Bukkit.getServer().getPlayer(u);
                    if(player != null) {
                        player.teleport(buildPlot.getCenter());
                    }
                }
            }
        }
    }

    public List<Plot> getPlots() {
        return plots;
    }

}
