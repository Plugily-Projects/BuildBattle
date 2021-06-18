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

package plugily.projects.buildbattle.arena;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.buildbattle.ConfigPreferences;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.arena.impl.SoloArena;


/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 18.06.2021
 */
public class ArenaUtils {

  private static final Main plugin = JavaPlugin.getPlugin(Main.class);

  public static void arenaForceStart(Player player, String theme) {
    if(!plugin.getArgumentsRegistry().hasPermission(player, "buildbattle.admin.forcestart")) {
      return;
    }
    BaseArena arena = ArenaRegistry.getArena(player);
    if(arena == null) {
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Commands.No-Playing"));
      return;
    }
    if(arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING) {
      arena.setArenaState(ArenaState.STARTING);
      arena.setForceStart(true);
      arena.setTimer(0);
      if(!theme.isEmpty() && arena instanceof SoloArena) {
        ((SoloArena) arena).setThemeVoteTime(false);
        arena.setTimer(plugin.getConfigPreferences().getTimer(ConfigPreferences.TimerType.BUILD, arena));
        arena.setTheme(theme);
      }
      plugin.getChatManager().broadcast(arena, plugin.getChatManager().colorMessage("In-Game.Messages.Admin-Messages.Set-Starting-In-To-0"));
    }
  }

}
