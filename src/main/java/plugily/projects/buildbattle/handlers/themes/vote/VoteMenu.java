/*
 *
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2022 Plugily Projects - maintained by Tigerpanzer_02 and contributors
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
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.BuildArena;
import plugily.projects.buildbattle.handlers.themes.ThemeManager;
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.api.user.IUser;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XEnchantment;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;
import plugily.projects.minigamesbox.inventory.common.item.SimpleClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

import java.util.*;
import java.util.function.Consumer;

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
    playerGuis.clear();
    votePoll = new VotePoll(arena, themeSelection);
  }

  private NormalFastInv getGUI(Player guiPlayer) {
    NormalFastInv gui = new NormalFastInv(9 * themeSelection.size(), new MessageBuilder("MENU_THEME_INVENTORY").asKey().build());

    gui.addCloseHandler(event -> {
      if(arena.getArenaState() == IArenaState.IN_GAME && arena.getArenaInGameState() == BaseArena.ArenaInGameState.THEME_VOTING) {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
          HumanEntity humanEntity = event.getPlayer();
          Inventory inventory = event.getInventory();

          if(humanEntity.getOpenInventory().getTopInventory() != inventory) {
            humanEntity.openInventory(inventory);
          }
        });
      }
    });

    int i = 0;

    for(String theme : themeSelection) {
      double percent = getPercent(theme);

      int multiplied = i * 9;

      gui.setItem(multiplied, new SimpleClickableItem(new ItemBuilder(new ItemStack(XMaterial.OAK_SIGN.parseMaterial()))
          .name(new MessageBuilder("MENU_THEME_ITEM_NAME").asKey().arena(arena).value(theme).integer((int) percent).build())
          .lore(new MessageBuilder("MENU_THEME_ITEM_LORE").asKey().arena(arena).value(theme).integer((int) percent).build().split(";"))
          .build(), getSignClickEvent(theme)));

      gui.setItem(multiplied + 1, ClickableItem.of(new ItemBuilder(XMaterial.IRON_BARS.parseItem()).name("&7->").colorizeItem().build()));

      setGlassPanes(gui, multiplied, percent);

      if (plugin.getConfigPreferences().getOption("SUPER_VOTES")) {
        gui.setItem(multiplied + 8, new SimpleClickableItem(new ItemBuilder(new ItemStack(Material.PAPER))
            .name(new MessageBuilder("MENU_THEME_VOTE_SUPER_ITEM_NAME").asKey().arena(arena).value(theme).build())
            .lore(new MessageBuilder("MENU_THEME_VOTE_SUPER_ITEM_LORE").asKey().player(guiPlayer).arena(arena).value(theme).build().split(";"))
            .build(), event -> {
          HumanEntity humanEntity = event.getWhoClicked();

          if(!(humanEntity instanceof Player))
            return;

          Player player = (Player) humanEntity;
          IUser user = plugin.getUserManager().getUser(player);

          if(user.getStatistic("SUPER_VOTES") > 0) {
            user.adjustStatistic("SUPER_VOTES", -1);
            new MessageBuilder("MENU_THEME_VOTE_SUPER_USED").asKey().arena(arena).player(player).value(theme).sendArena();
            arena.setTheme(theme);
            arena.setTimer(0, true);
          }
        }));
      }
      else {
        gui.setItem(multiplied + 8, ClickableItem.of(new ItemBuilder(XMaterial.IRON_BARS.parseItem()).name("&7<-").colorizeItem().build()));
      }

      i++;
    }
    return gui;
  }

  @NotNull
  private Consumer<InventoryClickEvent> getSignClickEvent(String theme) {
    return event -> {
      HumanEntity humanEntity = event.getWhoClicked();

      if(!(humanEntity instanceof Player)) {
        return;
      }
      Player player = (Player) humanEntity;

      new MessageBuilder(votePoll.addVote(player, theme) ? "MENU_THEME_VOTE_SUCCESS" : "MENU_THEME_VOTE_ALREADY").asKey().player(player).value(theme).sendPlayer();
    };
  }

  public void updatePlayerGui(Player player, NormalFastInv gui) {
    String playerVote = votePoll.getPlayerVote().get(player);
    int i = 0;
    for(String theme : themeSelection) {
      int multiplied = i * 9;
      ClickableItem clickableSignItem = gui.getItem(multiplied);
      if(clickableSignItem == null) {
        continue;
      }
      double percent = getPercent(theme);

      ItemBuilder itemBuilder = new ItemBuilder(clickableSignItem.getItem()).removeLore();
      itemBuilder.lore(new MessageBuilder("MENU_THEME_ITEM_LORE").asKey().arena(arena).value(theme).integer((int) percent).build().split(";"));
      if(theme.equals(playerVote)) {
        itemBuilder.enchantment(XEnchantment.UNBREAKING.get(), 1).flags(ItemFlag.HIDE_ENCHANTS);
      } else {
        itemBuilder.removeEnchants().removeFlags();
      }
      gui.setItem(multiplied, new SimpleClickableItem(itemBuilder.colorizeItem().build(), getSignClickEvent(theme)));

      setGlassPanes(gui, multiplied, percent);
      i++;
    }
    gui.refresh();
  }

  private double getPercent(String theme) {
    double percent;
    int totalVotes = votePoll.getPlayerVote().size();
    int themeVotes = votePoll.getVoteAmount(theme);

    if(themeVotes == 0) {
      percent = 0.0;
    } else {
      percent = ((double) themeVotes / (double) totalVotes) * 100;
    }
    return percent;
  }

  private static void setGlassPanes(NormalFastInv gui, int multiplied, double percent) {
    double vote = 0;

    for(int j = 0; j < 6; j++) {
      int slot = multiplied + 2 + j;

      if(vote > percent) {
        gui.setItem(slot, ClickableItem.of(new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE.parseItem()).name("???").build()));
        continue;
      }
      gui.setItem(slot, ClickableItem.of(new ItemBuilder(XMaterial.LIME_STAINED_GLASS_PANE.parseItem()).name("???").build()));
      vote += 16.7;
    }
  }

  public void updateInventory(Player player) {
    NormalFastInv gui = playerGuis.get(player);

    if(gui != null) {
      updatePlayerGui(player, gui);
      return;
    }

    gui = getGUI(player);
    gui.setForceRefresh(true);
    playerGuis.put(player, gui);

    gui.open(player);
  }

  public List<String> getThemeSelection() {
    return themeSelection;
  }

  public VotePoll getVotePoll() {
    return votePoll;
  }

}
