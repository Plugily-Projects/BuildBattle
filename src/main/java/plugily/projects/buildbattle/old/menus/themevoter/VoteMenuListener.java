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

package plugily.projects.buildbattle.old.menus.themevoter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import plugily.projects.commonsbox.minecraft.compat.VersionUtils;
import plugily.projects.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.commonsbox.minecraft.misc.stuff.ComplementAccessor;
import plugily.projects.buildbattle.old.ConfigPreferences;
import plugily.projects.buildbattle.old.Main;
import plugily.projects.buildbattle.old.api.StatsStorage;
import plugily.projects.buildbattle.old.arena.ArenaRegistry;
import plugily.projects.buildbattle.old.arena.ArenaState;
import plugily.projects.buildbattle.old.arena.impl.GuessTheBuildArena;
import plugily.projects.buildbattle.old.user.User;

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
    if(ComplementAccessor.getComplement().getTitle(e.getView()).equals(plugin.getChatManager().colorMessage("Menus.Theme-Voting.Inventory-Name"))) {
      if(!((SoloArena) arena).isThemeVoteTime() || arena.getArenaState() != ArenaState.IN_GAME) {
        return;
      }
      Bukkit.getScheduler().runTask(plugin, () -> e.getPlayer().openInventory(((SoloArena) arena).getVoteMenu().getInventory()));
    }
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    org.bukkit.inventory.ItemStack current = e.getCurrentItem();
    if(current == null) {
      return;
    }
    Player who = (Player) e.getWhoClicked();
    BaseArena arena = ArenaRegistry.getArena(who);
    if(!(arena instanceof SoloArena)) {
      return;
    }
    if(ComplementAccessor.getComplement().getTitle(e.getView()).equals(plugin.getChatManager().colorMessage("Menus.Theme-Voting.Inventory-Name"))) {
      e.setCancelled(true);

      SoloArena solo = (SoloArena) arena;

      if(current.getType() == XMaterial.OAK_SIGN.parseMaterial()) {
        String displayName = ComplementAccessor.getComplement().getDisplayName(current.getItemMeta());
        boolean success = solo.getVotePoll().addVote(who, ChatColor.stripColor(displayName));

        who.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Menus.Theme-Voting." + (!success ? "Already-Voted" : "Voted-Successfully")));
      }

      if(current.getType() == Material.PAPER) {
        User user = plugin.getUserManager().getUser(who);
        int votes = user.getStat(StatsStorage.StatisticType.SUPER_VOTES);

        if(votes > 0) {
          user.setStat(StatsStorage.StatisticType.SUPER_VOTES, votes - 1);

          String theme = solo.getVotePoll().getThemeByPosition(e.getSlot() + 1);

          plugin.getChatManager().broadcast(arena, plugin.getChatManager().colorMessage("Menus.Theme-Voting.Super-Vote-Used")
              .replace("%player%", who.getName()).replace("%theme%", theme));

          solo.setThemeVoteTime(false);
          arena.setTheme(theme);
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

}
