/*
 * BuildBattle 4 - Ultimate building competition minigame
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle.ConfigPreferences;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.user.User;
import pl.plajer.buildbattle.user.UserManager;
import pl.plajerlair.core.services.exception.ReportedException;

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
  public void onInventoryClick(InventoryClickEvent e) {
    try {
      if (e.getInventory() == null || e.getInventory().getName() == null) {
        return;
      }
      if (e.getCurrentItem() == null) {
        return;
      }
      if (e.getInventory().getName().equals(ChatManager.colorMessage("Menus.Theme-Voting.Inventory-Name"))) {
        e.setCancelled(true);
        Arena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
        if (arena == null) {
          return;
        }
        if (e.getCurrentItem().getType() == Material.SIGN || /*1.13*/ ((plugin.is1_11_R1() || plugin.is1_12_R1()) &&
            e.getCurrentItem().getType() == Material.valueOf("SIGN_POST"))) {
          String displayName = e.getCurrentItem().getItemMeta().getDisplayName();
          displayName = ChatColor.stripColor(displayName);
          boolean success = arena.getVotePoll().addVote((Player) e.getWhoClicked(), displayName);
          if (!success) {
            e.getWhoClicked().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Menus.Theme-Voting.Already-Voted"));
          } else {
            e.getWhoClicked().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Menus.Theme-Voting.Voted-Successfully"));
          }
        }
        if (e.getCurrentItem().getType() == Material.PAPER) {
          User u = UserManager.getUser(e.getWhoClicked().getUniqueId());
          if (u.getStat(StatsStorage.StatisticType.SUPER_VOTES) > 0) {
            u.setStat(StatsStorage.StatisticType.SUPER_VOTES, u.getStat(StatsStorage.StatisticType.SUPER_VOTES) - 1);
            ChatManager.broadcast(arena, ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Menus.Theme-Voting.Super-Vote-Used")
                .replace("%player%", e.getWhoClicked().getName()).replace("%theme%", arena.getVotePoll().getThemeByPosition(e.getSlot() + 1)));
            arena.setThemeVoteTime(false);
            arena.setTheme(arena.getVotePoll().getThemeByPosition(e.getSlot() + 1));
            arena.setTimer(ConfigPreferences.getBuildTime(arena));
            String message = ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Game-Started");
            for (Player p : arena.getPlayers()) {
              p.closeInventory();
              p.teleport(arena.getPlotManager().getPlot(p).getTeleportLocation());
              p.sendMessage(ChatManager.PLUGIN_PREFIX + message);
            }
          }
        }
      }
    } catch (Exception ex) {
      new ReportedException(JavaPlugin.getPlugin(Main.class), ex);
    }
  }

  @EventHandler
  public void onInventoryClickOnGuessTheBuild(InventoryClickEvent e) {
    try {
      if (e.getInventory() == null || e.getInventory().getName() == null) {
        return;
      }
      if (e.getCurrentItem() == null) {
        return;
      }
      if (e.getInventory().getName().equals(ChatManager.colorMessage("Menus.Guess-The-Build-Theme-Selector.Inventory-Name"))) {
        e.setCancelled(true);
        Arena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
        if (arena == null) {
          return;
        }
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
          arena.setCurrentGTBTheme(theme);
          arena.setGTBThemeSet(true);
          ((Player) e.getWhoClicked()).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatManager.colorMessage("In-Game.Guess-The-Build.Theme-Is-Name")
              .replace("%THEME%", theme.getTheme())));

        }
      }
    } catch (Exception ex) {
      new ReportedException(JavaPlugin.getPlugin(Main.class), ex);
    }
  }

}
