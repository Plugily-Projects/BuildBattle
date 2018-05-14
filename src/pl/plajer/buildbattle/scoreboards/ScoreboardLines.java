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

package pl.plajer.buildbattle.scoreboards;


import org.bukkit.ChatColor;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tom on 31/01/2016.
 */
class ScoreboardLines {


    private LinkedList<String> lines = new LinkedList<>();
    private String title = null;


    public List<String> getLines() {
        return lines;
    }

    public String getTitle() {
        return ChatColor.translateAlternateColorCodes('&', title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addLine(String string) {
        lines.add(ChatColor.translateAlternateColorCodes('&', string));
    }

}
