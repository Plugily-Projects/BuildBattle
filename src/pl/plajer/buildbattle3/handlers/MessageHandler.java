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

package pl.plajer.buildbattle3.handlers;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.buildbattle3.Main;

/**
 * Created by Tom on 21/07/2015.
 */
public class MessageHandler {

    private static Main plugin = JavaPlugin.getPlugin(Main.class);

    public static void sendTitleMessage(Player player, String text, int fadeInTime, int showTime, int fadeOutTime) {
        if(plugin.is1_9_R1()) {
            player.sendTitle(text, null);
        } else {
            player.sendTitle(text, null, fadeInTime, showTime, fadeOutTime);
        }
    }
}
