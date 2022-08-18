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

package plugily.projects.buildbattle.handlers.themes.vote;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.BuildArena;
import plugily.projects.buildbattle.handlers.themes.ThemeManager;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;
import plugily.projects.minigamesbox.inventory.common.item.SimpleClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 19.06.2022
 */
public class VoteMenu {

  private final Main plugin;
  private VotePoll votePoll;
  private List<String> themeSelection = new ArrayList<>();
  private final BuildArena arena;

  private Map<Player, NormalFastInv> playerGuis = new HashMap<>();

  public VoteMenu(BuildArena arena) {
    this.arena = arena;
    this.plugin = arena.getPlugin();
    randomizeThemes();
  }

  private void randomizeThemes() {
    List<String> themesTotal = new ArrayList<>(plugin.getThemeManager().getThemes(ThemeManager.GameThemes.getByArenaType(arena.getArenaType())));
    //random themes order
    Collections.shuffle(themesTotal);
    List<String> randomThemes = new ArrayList<>(themesTotal.size());
    if(themesTotal.size() <= 5) {
      randomThemes.addAll(themesTotal);
    } else {
      Iterator<String> itr = themesTotal.iterator();
      int i = 0;
      while(itr.hasNext()) {
        if(i == 5) {
          break;
        }
        randomThemes.add(itr.next());
        itr.remove();
        i++;
      }
    }
    themeSelection = new ArrayList<>(randomThemes);
  }

  public void resetPoll() {
    randomizeThemes();
    votePoll = new VotePoll(arena, themeSelection);
  }

  private NormalFastInv getGUI() {
    NormalFastInv gui = new NormalFastInv(9 * themeSelection.size(), new MessageBuilder("MENU_THEME_INVENTORY").asKey().build());

    gui.addClickHandler(inventoryClickEvent -> inventoryClickEvent.setCancelled(true));

    gui.addCloseHandler(event -> {
      if(arena.getArenaInGameStage() == BaseArena.ArenaInGameStage.THEME_VOTING) {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
          HumanEntity humanEntity = event.getPlayer();
          Inventory inventory = event.getInventory();

          if (humanEntity.getOpenInventory().getTopInventory() != inventory) {
            humanEntity.openInventory(inventory);
          }
        });
      }
    });

    int totalVotes = votePoll.getPlayerVote().size();
    int i = 0;

    for(String theme : themeSelection) {
      int themeVotes = votePoll.getVoteAmount(theme);
      double percent;

      if(themeVotes == 0) {
        percent = 0.0;
      } else {
        percent = ((double) themeVotes / (double) totalVotes) * 100;
      }

      int multiplied = i * 9;

      gui.setItem(multiplied, new SimpleClickableItem(new ItemBuilder(new ItemStack(XMaterial.OAK_SIGN.parseMaterial()))
          .name(new MessageBuilder("MENU_THEME_ITEM_NAME").asKey().arena(arena).value(theme).integer((int) percent).build())
          .lore(new MessageBuilder("MENU_THEME_ITEM_LORE").asKey().arena(arena).value(theme).integer((int) percent).build().split(";"))
          .build(), event -> {
        HumanEntity humanEntity = event.getWhoClicked();

        if(!(humanEntity instanceof Player))
          return;

        Player player = (Player) humanEntity;

        new MessageBuilder(votePoll.addVote(player, theme) ? "MENU_THEME_VOTE_SUCCESS" : "MENU_THEME_VOTE_ALREADY").asKey().player(player).value(theme).sendPlayer();
      }));

      gui.setItem(multiplied + 1, new ItemBuilder(XMaterial.IRON_BARS.parseItem()).build());

      double vote = 0;

      for(int j = 0; j < 6; j++) {
        int slot = multiplied + 2 + j;

        if(vote > percent) {
          gui.setItem(slot, new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE.parseItem()).build());
        }

        gui.setItem(slot, new ItemBuilder(XMaterial.LIME_STAINED_GLASS_PANE.parseItem()).build());
        vote += 16.7;
      }

      gui.setItem(multiplied + 8, new SimpleClickableItem(new ItemBuilder(new ItemStack(Material.PAPER))
          .name(new MessageBuilder("MENU_THEME_VOTE_SUPER_ITEM_NAME").asKey().arena(arena).value(theme).build())
          .lore(new MessageBuilder("MENU_THEME_VOTE_SUPER_ITEM_LORE").asKey().arena(arena).value(theme).build().split(";"))
          .build(), event -> {
        HumanEntity humanEntity = event.getWhoClicked();

        if(!(humanEntity instanceof Player))
          return;

        Player player = (Player) humanEntity;
        User user = plugin.getUserManager().getUser(player);

        if(user.getStatistic("SUPER_VOTES") > 0) {
          user.adjustStatistic("SUPER_VOTES", -1);
          new MessageBuilder("MENU_THEME_VOTE_SUPER_USED").asKey().arena(arena).player(player).value(theme).sendArena();
          arena.setTheme(theme);
          arena.setTimer(0, true);
        }
      }));

      i++;
    }
    return gui;
  }

  public void updatePlayerGui(Player player, NormalFastInv gui) {
    String playerVote = votePoll.getPlayerVote().get(player);
    int userVotes = plugin.getUserManager().getUser(player).getStatistic("SUPER_VOTES");

    for(int i = 0; i < 5; i++) {
      int multiplied = i * 9;
      ClickableItem clickableItem = gui.getItem(multiplied);

      if (clickableItem == null)
        continue;

      ItemStack signItem = clickableItem.getItem();
      ItemMeta signMeta = signItem.getItemMeta();

      if(ComplementAccessor.getComplement().getDisplayName(signMeta).equals(playerVote)) {
        signMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        signMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        signItem.setItemMeta(signMeta);
      }

      if ((clickableItem = gui.getItem(multiplied + 8)) == null)
        continue;

      ComplementAccessor.getComplement().setLore(clickableItem.getItem().getItemMeta(), Arrays.stream(new MessageBuilder("MENU_THEME_VOTE_SUPER_ITEM_LORE").asKey().arena(arena).integer(userVotes).build().split(";")).collect(Collectors.toList()));
    }

    gui.refresh();
  }

  public void updateInventory(Player player) {
    NormalFastInv gui = playerGuis.get(player);

    if(gui != null) {
      updatePlayerGui(player, gui);
      return;
    }

    gui = getGUI();
    gui.setForceRefresh(true);
    playerGuis.put(player, gui);
    updatePlayerGui(player, gui);
    gui.open(player);
  }

  public List<String> getThemeSelection() {
    return themeSelection;
  }

  public VotePoll getVotePoll() {
    return votePoll;
  }

}
