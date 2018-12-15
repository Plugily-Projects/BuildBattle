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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle.ConfigPreferences;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.utils.ItemBuilder;
import pl.plajerlair.core.utils.MinigameUtils;
import pl.plajerlair.core.utils.XMaterial;

/**
 * @author Plajer
 * <p>
 * Created at 07.07.2018
 */
public class VoteMenu {

  private Main plugin = JavaPlugin.getPlugin(Main.class);
  private Inventory inventory;
  private VotePoll votePoll;
  private Arena arena;

  public VoteMenu(Arena arena) {
    this.arena = arena;
    this.inventory = Bukkit.createInventory(null, 9 * 5, ChatManager.colorMessage("Menus.Theme-Voting.Inventory-Name"));
  }

  private void setItem(ItemStack itemStack, int pos) {
    inventory.setItem(pos, itemStack);
  }

  public void resetPoll() {
    try {
      List<String> themesTotal = ConfigPreferences.getThemes(arena.getArenaType().getPrefix());
      //random themes order
      Collections.shuffle(themesTotal);
      List<String> randomThemes = new ArrayList<>();
      if (themesTotal.size() <= 5) {
        randomThemes.addAll(themesTotal);
      } else {
        Iterator<String> itr = themesTotal.iterator();
        int i = 0;
        while (itr.hasNext()) {
          if (i == 5) {
            break;
          }
          randomThemes.add(itr.next());
          itr.remove();
          i++;
        }
      }
      this.inventory = Bukkit.createInventory(null, 9 * (randomThemes.size() > 5 ? 5 : randomThemes.size()), ChatManager.colorMessage("Menus.Theme-Voting.Inventory-Name"));
      for (int i = 0; i < randomThemes.size(); i++) {
        setItem(new ItemBuilder(new ItemStack(Material.SIGN))
            .name(ChatManager.colorMessage("Menus.Theme-Voting.Theme-Item-Name").replace("%theme%", randomThemes.get(i)))
            .lore(ChatManager.colorMessage("Menus.Theme-Voting.Theme-Item-Lore").replace("%theme%", randomThemes.get(i))
                .replace("%percent%", String.valueOf("0.0")).replace("%time-left%", String.valueOf(arena.getTimer())).split(";"))
            .build(), i * 9);
        setItem(new ItemBuilder(XMaterial.IRON_BARS.parseItem()).build(), (i * 9) + 1);
        for (int j = 0; j < 6; j++) {
          setItem(new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE.parseItem()).build(), (i * 9) + 1 + j + 1);
        }
        setItem(new ItemBuilder(new ItemStack(Material.PAPER))
            .name(ChatManager.colorMessage("Menus.Theme-Voting.Super-Vote-Item-Name").replace("%theme%", randomThemes.get(i)))
            .lore(ChatManager.colorMessage("Menus.Theme-Voting.Super-Vote-Item-Lore").replace("%theme%", randomThemes.get(i)).split(";"))
            .build(), (i * 9) + 8);
      }
      votePoll = new VotePoll(arena, randomThemes);
    } catch (Exception ex) {
      new ReportedException(JavaPlugin.getPlugin(Main.class), ex);
    }
  }

  public Inventory getInventory() {
    return inventory;
  }

  public VotePoll getVotePoll() {
    return votePoll;
  }

  public void updateInventory(Player player) {
    try {
      int totalVotes = votePoll.getPlayerVote().size();
      int i = 0;
      for (String theme : votePoll.getVotedThemes().keySet()) {
        double percent;
        if (Double.isNaN(votePoll.getVotedThemes().get(theme)) || votePoll.getVotedThemes().get(theme) == 0) {
          percent = 0.0;
        } else {
          percent = ((double) votePoll.getVotedThemes().get(theme) / (double) totalVotes) * 100;
        }
        ItemStack stack = new ItemBuilder(new ItemStack(Material.SIGN))
            .name(ChatManager.colorMessage("Menus.Theme-Voting.Theme-Item-Name").replace("%theme%", theme))
            .lore(ChatManager.colorMessage("Menus.Theme-Voting.Theme-Item-Lore").replace("%theme%", theme)
                .replace("%percent%", String.valueOf(MinigameUtils.round(percent, 2))).replace("%time-left%", String.valueOf(arena.getTimer())).split(";"))
            .build();
        if (votePoll.getPlayerVote().containsKey(player) && votePoll.getPlayerVote().get(player).equals(theme)) {
          ItemMeta meta = stack.getItemMeta();
          meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
          meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
          stack.setItemMeta(meta);
        }
        setItem(stack, i * 9);
        for (int j = 0; j < 6; j++) {
          setItem(new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE.parseItem()).build(), (i * 9) + 1 + j + 1);
        }
        setItem(new ItemBuilder(new ItemStack(Material.PAPER))
            .name(ChatManager.colorMessage("Menus.Theme-Voting.Super-Vote-Item-Name").replace("%theme%", theme))
            .lore(ChatManager.colorMessage("Menus.Theme-Voting.Super-Vote-Item-Lore").replace("%theme%", theme)
                .replace("%owned%", String.valueOf(plugin.getUserManager().getUser(player.getUniqueId()).getStat(StatsStorage.StatisticType.SUPER_VOTES))).split(";"))
            .build(), (i * 9) + 8);
        if (votePoll.getVotedThemes().get(theme) > 0) {
          double vote = 0;
          for (int j = 0; j < 6; j++) {
            if (vote > percent) {
              break;
            }
            setItem(new ItemBuilder(XMaterial.LIME_STAINED_GLASS_PANE.parseItem()).build(), (i * 9) + 1 + j + 1);
            vote += 16.7;
          }
        }
        i++;
      }
      player.openInventory(inventory);
    } catch (Exception ex) {
      new ReportedException(JavaPlugin.getPlugin(Main.class), ex);
    }
  }

}
