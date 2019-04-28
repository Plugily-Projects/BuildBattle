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

package pl.plajer.buildbattle.menus.themevoter;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import pl.plajer.buildbattle.ConfigPreferences;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.ArenaState;
import pl.plajer.buildbattle.arena.impl.BaseArena;
import pl.plajer.buildbattle.arena.impl.GuessTheBuildArena;
import pl.plajer.buildbattle.arena.impl.SoloArena;
import pl.plajer.buildbattle.user.User;

/**
 * @author Plajer
 * <p>
 * Created at 07.07.2018
 */
public class VoteMenuListener implements Listener {

  private Main plugin;

  public VoteMenuListener(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent e) {
      BaseArena arena = ArenaRegistry.getArena((Player) e.getPlayer());
      if (e.getInventory() == null || e.getInventory().getName() == null || arena == null) {
        return;
      }
      if(!(arena instanceof SoloArena)) {
        return;
      }
      if (e.getInventory().getName().equals(plugin.getChatManager().colorMessage("Menus.Theme-Voting.Inventory-Name"))) {
        if (!((SoloArena) arena).isThemeVoteTime() || arena.getArenaState() != ArenaState.IN_GAME) {
          return;
        }
        Bukkit.getScheduler().runTask(plugin, () -> e.getPlayer().openInventory(((SoloArena) arena).getVoteMenu().getInventory()));
      }
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
      if (e.getInventory() == null || e.getInventory().getName() == null) {
        return;
      }
      if (e.getCurrentItem() == null) {
        return;
      }
      BaseArena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
      if(!(arena instanceof SoloArena)) {
        return;
      }
      if (e.getInventory().getName().equals(plugin.getChatManager().colorMessage("Menus.Theme-Voting.Inventory-Name"))) {
        e.setCancelled(true);
        if (e.getCurrentItem().getType() == Material.SIGN || /*1.13*/ ((plugin.is1_11_R1() || plugin.is1_12_R1()) &&
            e.getCurrentItem().getType() == Material.valueOf("SIGN_POST"))) {
          String displayName = e.getCurrentItem().getItemMeta().getDisplayName();
          displayName = ChatColor.stripColor(displayName);
          boolean success = ((SoloArena) arena).getVotePoll().addVote((Player) e.getWhoClicked(), displayName);
          if (!success) {
            e.getWhoClicked().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Menus.Theme-Voting.Already-Voted"));
          } else {
            e.getWhoClicked().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Menus.Theme-Voting.Voted-Successfully"));
          }
        }
        if (e.getCurrentItem().getType() == Material.PAPER) {
          User user = plugin.getUserManager().getUser((Player) e.getWhoClicked());
          if (user.getStat(StatsStorage.StatisticType.SUPER_VOTES) > 0) {
            user.setStat(StatsStorage.StatisticType.SUPER_VOTES, user.getStat(StatsStorage.StatisticType.SUPER_VOTES) - 1);
            plugin.getChatManager().broadcast(arena, plugin.getChatManager().colorMessage("Menus.Theme-Voting.Super-Vote-Used")
                .replace("%player%", e.getWhoClicked().getName()).replace("%theme%",
                    ((SoloArena) arena).getVotePoll().getThemeByPosition(e.getSlot() + 1)));
            ((SoloArena) arena).setThemeVoteTime(false);
            arena.setTheme(((SoloArena) arena).getVotePoll().getThemeByPosition(e.getSlot() + 1));
            arena.setTimer(plugin.getConfigPreferences().getTimer(ConfigPreferences.TimerType.BUILD, arena));
            String message = plugin.getChatManager().colorMessage("In-Game.Messages.Lobby-Messages.Game-Started");
            for (Player p : arena.getPlayers()) {
              p.closeInventory();
              p.teleport(arena.getPlotManager().getPlot(p).getTeleportLocation());
              p.sendMessage(plugin.getChatManager().getPrefix() + message);
            }
          }
        }
      }
  }

  @Deprecated
  @EventHandler
  public void onGTBInventoryClose(InventoryCloseEvent e) {
    BaseArena arena = ArenaRegistry.getArena((Player) e.getPlayer());
    if (!(arena instanceof GuessTheBuildArena)) {
      return;
    }
    if (!e.getInventory().getName().equals(plugin.getChatManager().colorMessage("Menus.Guess-The-Build-Theme-Selector.Inventory-Name"))) {
      return;
    }
    //cancel
    if (!((GuessTheBuildArena) arena).isThemeSet()) {
      Bukkit.getScheduler().runTask(plugin, () -> e.getPlayer().openInventory(e.getInventory()));
    }
  }

  @EventHandler
  public void onInventoryClickOnGuessTheBuild(InventoryClickEvent e) {
      if (e.getInventory() == null || e.getInventory().getName() == null) {
        return;
      }
      if (e.getCurrentItem() == null) {
        return;
      }
      BaseArena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
      if (!(arena instanceof GuessTheBuildArena)) {
        return;
      }
      //todo once you close you cant choose rip
      if (e.getInventory().getName().equals(plugin.getChatManager().colorMessage("Menus.Guess-The-Build-Theme-Selector.Inventory-Name"))) {
        e.setCancelled(true);
        if (e.getCurrentItem().getType() == Material.PAPER) {
          GTBTheme theme;
          String displayName = e.getCurrentItem().getItemMeta().getDisplayName();
          displayName = ChatColor.stripColor(displayName);
          switch (e.getSlot()) {
            case 11:
              theme = new GTBTheme(displayName, GTBTheme.Difficulty.EASY);
              break;
            case 13:
              theme = new GTBTheme(displayName, GTBTheme.Difficulty.MEDIUM);
              break;
            case 15:
              theme = new GTBTheme(displayName, GTBTheme.Difficulty.HARD);
              break;
            default:
              return;
          }
          ((GuessTheBuildArena) arena).setCurrentTheme(theme);
          ((GuessTheBuildArena) arena).setThemeSet(true);
          ((Player) e.getWhoClicked()).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(plugin.getChatManager().colorMessage("In-Game.Guess-The-Build.Theme-Is-Name")
              .replace("%THEME%", theme.getTheme())));
          e.getWhoClicked().closeInventory();

          String roundMessage = plugin.getChatManager().colorMessage("In-Game.Guess-The-Build.Current-Round")
              .replace("%ROUND%", String.valueOf(((GuessTheBuildArena) arena).getRound()))
              .replace("%MAXPLAYERS%", String.valueOf(arena.getPlayers().size()));
          for (Player p : arena.getPlayers()) {
            p.sendTitle(plugin.getChatManager().colorMessage("In-Game.Guess-The-Build.Start-Guessing-Title"), null, 5, 25, 5);
            p.sendMessage(roundMessage);
          }
        }
      }
  }

}
