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

package plugily.projects.buildbattle.events;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import pl.plajerlair.commonsbox.minecraft.compat.VersionUtils;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.ArenaState;
import plugily.projects.buildbattle.arena.impl.BaseArena;

/**
 * @author Plajer
 * <p>
 * Created at 31.12.2018
 */
public class LobbyEvents implements Listener {

  public LobbyEvents(Main plugin) {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onFoodLose(FoodLevelChangeEvent e) {
    if(e.getEntity().getType() != EntityType.PLAYER) {
      return;
    }
    Player player = (Player) e.getEntity();
    BaseArena arena = ArenaRegistry.getArena(player);
    if(arena != null && (arena.getArenaState() == ArenaState.STARTING || arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS)) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onLobbyDamage(EntityDamageEvent e) {
    if(e.getEntity().getType() != EntityType.PLAYER) {
      return;
    }
    Player player = (Player) e.getEntity();
    BaseArena arena = ArenaRegistry.getArena(player);
    if(arena == null || arena.getArenaState() == ArenaState.IN_GAME) {
      return;
    }
    e.setCancelled(true);
    player.setHealth(VersionUtils.getHealth(player));
  }

}
