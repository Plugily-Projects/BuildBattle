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

package pl.plajer.buildbattle4.handlers;

import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle4.Main;

/**
 * Created by Tom on 14/08/2014.
 */
public class PermissionManager {

  private static Main plugin = JavaPlugin.getPlugin(Main.class);
  private static String joinPerm = "buildbattle.join.<arena>";
  private static String editGames = "buildbattle.editgames";

  public static void init() {
    setupPermissions();
  }

  public static String getJoinPerm() {
    return joinPerm;
  }

  public static void setJoinPerm(String joinPerm) {
    PermissionManager.joinPerm = joinPerm;
  }

  public static String getEditGames() {
    return editGames;
  }

  public static void setEditGames(String editGames) {
    PermissionManager.editGames = editGames;
  }

  private static void setupPermissions() {
    setEditGames(plugin.getConfig().getString("Basic-Permissions.Arena-Edit-Permission"));
    setJoinPerm(plugin.getConfig().getString("Basic-Permissions.Join-Permission"));
    Main.debug("Basic permissions registered", System.currentTimeMillis());
  }

}
