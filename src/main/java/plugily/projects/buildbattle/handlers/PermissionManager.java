/*
 *
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2021 Plugily Projects - maintained by Tigerpanzer_02, 2Wild4You and contributors
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
 *
 */

package plugily.projects.buildbattle.handlers;

import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.utils.Debugger;

/**
 * Created by Tom on 14/08/2014.
 */
public class PermissionManager {

  private static final Main plugin = JavaPlugin.getPlugin(Main.class);
  private static String joinFullPerm = "buildbattle.fullgames";
  private static String joinPerm = "buildbattle.join.<arena>";
  private static String editGames = "buildbattle.editgames";

  private PermissionManager() {
  }

  public static void init() {
    setupPermissions();
  }

  public static String getJoinFullGames() {
    return joinFullPerm;
  }

  private static void setJoinFullGames(String joinFullGames) {
    PermissionManager.joinFullPerm = joinFullGames;
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
    setJoinFullGames(plugin.getConfig().getString("Basic-Permissions.Full-Games-Permission", "buildbattle.fullgames"));
    setEditGames(plugin.getConfig().getString("Basic-Permissions.Arena-Edit-Permission"));
    setJoinPerm(plugin.getConfig().getString("Basic-Permissions.Join-Permission"));
    Debugger.debug("Basic permissions registered");
  }

}
