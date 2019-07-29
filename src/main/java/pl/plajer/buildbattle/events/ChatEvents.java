/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and contributors
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

package pl.plajer.buildbattle.events;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.impl.BaseArena;
import pl.plajer.buildbattle.arena.impl.GuessTheBuildArena;

/**
 * @author Plajer
 * <p>
 * Created at 31.12.2018
 */
public class ChatEvents implements Listener {

  private Main plugin;

  public ChatEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onChatIngame(AsyncPlayerChatEvent event) {
    BaseArena arena = ArenaRegistry.getArena(event.getPlayer());
    if (arena == null) {
      for (BaseArena loopArena : ArenaRegistry.getArenas()) {
        for (Player player : loopArena.getPlayers()) {
          event.getRecipients().remove(player);
        }
      }
      return;
    }
    event.getRecipients().clear();
    event.getRecipients().addAll(new ArrayList<>(arena.getPlayers()));

    if (!(arena instanceof GuessTheBuildArena)) {
      return;
    }
    GuessTheBuildArena gameArena = (GuessTheBuildArena) arena;
    if (gameArena.getWhoGuessed().contains(event.getPlayer())) {
      event.setCancelled(true);
      event.getPlayer().sendMessage(plugin.getChatManager().colorMessage("In-Game.Guess-The-Build.Chat.Cant-Talk-When-Guessed"));
      return;
    }
    if (gameArena.getCurrentBuilder() != null && gameArena.getCurrentBuilder().equals(event.getPlayer())) {
      event.setCancelled(true);
      event.getPlayer().sendMessage(plugin.getChatManager().colorMessage("In-Game.Guess-The-Build.Chat.Cant-Talk-When-Building"));
      return;
    }
    if (gameArena.getCurrentTheme() == null || !gameArena.getCurrentTheme().getTheme().equalsIgnoreCase(event.getMessage())) {
      return;
    }
    event.setCancelled(true);
    plugin.getChatManager().broadcast(arena, plugin.getChatManager().colorMessage("In-Game.Guess-The-Build.Chat.Guessed-The-Theme").replace("%player%", event.getPlayer().getName()));
    //todo how this works
    event.getPlayer().sendMessage(plugin.getChatManager().colorMessage("In-Game.Guess-The-Build.Plus-Points")
        .replace("%pts%", String.valueOf(gameArena.getCurrentTheme().getDifficulty().getPointsReward())));
    plugin.getUserManager().getUser(gameArena.getCurrentBuilder())
        .addStat(StatsStorage.StatisticType.LOCAL_GUESS_THE_BUILD_POINTS, gameArena.getCurrentTheme().getDifficulty().getPointsReward());
    gameArena.addWhoGuessed(event.getPlayer());

    //todo add api event for successful guess
  }

}
