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

package plugily.projects.buildbattle.menus.themevoter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import pl.plajerlair.commonsbox.minecraft.compat.VersionUtils;
import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.buildbattle.ConfigPreferences;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.api.StatsStorage;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.ArenaState;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.arena.impl.GuessTheBuildArena;
import plugily.projects.buildbattle.arena.impl.SoloArena;
import plugily.projects.buildbattle.user.User;

/**
 * @author Plajer
 * <p>
 * Created at 07.07.2018
 */
public class VoteMenuListener implements Listener {

  private final Main plugin;

  public VoteMenuListener(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent e) {
    BaseArena arena = ArenaRegistry.getArena((Player) e.getPlayer());
    if(!(arena instanceof SoloArena)) {
      return;
    }
    if(e.getView().getTitle().equals(plugin.getChatManager().colorMessage("Menus.Theme-Voting.Inventory-Name"))) {
      if(!((SoloArena) arena).isThemeVoteTime() || arena.getArenaState() != ArenaState.IN_GAME) {
        return;
      }
      Bukkit.getScheduler().runTask(plugin, () -> e.getPlayer().openInventory(((SoloArena) arena).getVoteMenu().getInventory()));
    }
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    if(e.getCurrentItem() == null) {
      return;
    }
    BaseArena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
    if(!(arena instanceof SoloArena)) {
      return;
    }
    if(e.getView().getTitle().equals(plugin.getChatManager().colorMessage("Menus.Theme-Voting.Inventory-Name"))) {
      e.setCancelled(true);
      if(e.getCurrentItem().getType() == XMaterial.OAK_SIGN.parseMaterial()) {
        String displayName = e.getCurrentItem().getItemMeta().getDisplayName();
        displayName = ChatColor.stripColor(displayName);
        boolean success = ((SoloArena) arena).getVotePoll().addVote((Player) e.getWhoClicked(), displayName);
        e.getWhoClicked().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Menus.Theme-Voting." + (!success ? "Already-Voted" : "Voted-Successfully")));
      }
      if(e.getCurrentItem().getType() == Material.PAPER) {
        User user = plugin.getUserManager().getUser((Player) e.getWhoClicked());
        if(user.getStat(StatsStorage.StatisticType.SUPER_VOTES) > 0) {
          user.setStat(StatsStorage.StatisticType.SUPER_VOTES, user.getStat(StatsStorage.StatisticType.SUPER_VOTES) - 1);
          plugin.getChatManager().broadcast(arena, plugin.getChatManager().colorMessage("Menus.Theme-Voting.Super-Vote-Used")
              .replace("%player%", e.getWhoClicked().getName()).replace("%theme%",
                  ((SoloArena) arena).getVotePoll().getThemeByPosition(e.getSlot() + 1)));
          ((SoloArena) arena).setThemeVoteTime(false);
          arena.setTheme(((SoloArena) arena).getVotePoll().getThemeByPosition(e.getSlot() + 1));
          arena.setTimer(plugin.getConfigPreferences().getTimer(ConfigPreferences.TimerType.BUILD, arena));
          String message = plugin.getChatManager().colorMessage("In-Game.Messages.Lobby-Messages.Game-Started");
          for(Player p : arena.getPlayers()) {
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
    if(!(arena instanceof GuessTheBuildArena)) {
      return;
    }
    if(!e.getView().getTitle().equals(plugin.getChatManager().colorMessage("Menus.Guess-The-Build-Theme-Selector.Inventory-Name"))) {
      return;
    }
    if(!((GuessTheBuildArena) arena).isThemeSet()) {
      Bukkit.getScheduler().runTask(plugin, () -> e.getPlayer().openInventory(e.getInventory()));
    }
  }

  @EventHandler
  public void onInventoryClickOnGuessTheBuild(InventoryClickEvent e) {
    if(e.getCurrentItem() == null) {
      return;
    }
    BaseArena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
    if(!(arena instanceof GuessTheBuildArena)) {
      return;
    }
    if(e.getView().getTitle().equals(plugin.getChatManager().colorMessage("Menus.Guess-The-Build-Theme-Selector.Inventory-Name"))) {
      e.setCancelled(true);
      if(e.getCurrentItem().getType() == Material.PAPER) {
        BBTheme theme;
        String displayName = e.getCurrentItem().getItemMeta().getDisplayName();
        displayName = ChatColor.stripColor(displayName);
        switch(e.getSlot()) {
          case 11:
            theme = new BBTheme(displayName, BBTheme.Difficulty.EASY);
            break;
          case 13:
            theme = new BBTheme(displayName, BBTheme.Difficulty.MEDIUM);
            break;
          case 15:
            theme = new BBTheme(displayName, BBTheme.Difficulty.HARD);
            break;
          default:
            return;
        }
        ((GuessTheBuildArena) arena).setCurrentTheme(theme);
        ((GuessTheBuildArena) arena).setThemeSet(true);
        arena.setTimer(plugin.getConfigPreferences().getTimer(ConfigPreferences.TimerType.BUILD, arena));
        VersionUtils.sendActionBar(((Player) e.getWhoClicked()), plugin.getChatManager().colorMessage("In-Game.Guess-The-Build.Theme-Is-Name")
            .replace("%THEME%", theme.getTheme()));
        e.getWhoClicked().closeInventory();

        String roundMessage = plugin.getChatManager().colorMessage("In-Game.Guess-The-Build.Current-Round")
            .replace("%ROUND%", String.valueOf(((GuessTheBuildArena) arena).getRound()))
            .replace("%MAXPLAYERS%", String.valueOf(arena.getPlayers().size()));
        for(Player p : arena.getPlayers()) {
          VersionUtils.sendTitle(p, plugin.getChatManager().colorMessage("In-Game.Guess-The-Build.Start-Guessing-Title"), 5, 25, 5);
          p.sendMessage(roundMessage);
        }
      }
    }
  }

}
