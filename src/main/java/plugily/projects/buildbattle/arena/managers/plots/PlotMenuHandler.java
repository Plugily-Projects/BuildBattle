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

package plugily.projects.buildbattle.arena.managers.plots;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.api.event.plot.PlotPlayerChooseEvent;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.minigamesbox.classic.handlers.items.SpecialItem;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.inventory.common.item.SimpleClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tigerpanzer_02 on 26/Jul/2021.
 */

public class PlotMenuHandler implements Listener {

  private final Main plugin;
  private final ItemStack empty;
  private final ItemStack waiting;
  private final ItemStack full;
  private final ItemStack inside;
  private final SpecialItem baseItem;

  public PlotMenuHandler(Main plugin) {
    this.plugin = plugin;
    this.baseItem = plugin.getSpecialItemManager().getSpecialItem("PLOT_SELECTOR");

    empty = XMaterial.GREEN_WOOL.parseItem().clone();
    waiting = XMaterial.YELLOW_WOOL.parseItem().clone();
    full = XMaterial.RED_WOOL.parseItem().clone();
    inside = XMaterial.PINK_WOOL.parseItem().clone();

    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  private ItemStack getItemStack(Plot plot, int maxPlotMembers, Player player) {
    if(plot.getMembers().isEmpty()) {
      return empty;
    }

    if(plot.getMembers().contains(player)) {
      return inside;
    }

    if(plot.getMembers().size() == maxPlotMembers) {
      return full;
    }

    return waiting;
  }

  public void createMenu(Player player, BaseArena arena) {
    NormalFastInv gui = new NormalFastInv(plugin.getBukkitHelper().serializeInt(arena.getPlotManager().getPlots().size()) / 9, new MessageBuilder("IN_GAME_MESSAGES_PLOT_SELECTOR_MENU_NAME").asKey().build());
    int plots = 0;
    int arenaPlotMemberSize = arena.getArenaOption("PLOT_MEMBER_SIZE");

    for(Plot plot : arena.getPlotManager().getPlots()) {
      ItemStack itemStack = getItemStack(plot, arenaPlotMemberSize, player);
      int plotMemberSize = plot.getMembersSize();

      itemStack.setAmount(plotMemberSize == 0 ? 1 : plotMemberSize);

      if(plotMemberSize >= arenaPlotMemberSize) {
        itemStack = new ItemBuilder(itemStack).lore(new MessageBuilder("IN_GAME_MESSAGES_PLOT_SELECTOR_FULL").asKey().build()).build();
      } else {
        itemStack = new ItemBuilder(itemStack).lore(new MessageBuilder("IN_GAME_MESSAGES_PLOT_SELECTOR_EMPTY").asKey().build()).build();
      }
      if(plotMemberSize != 0) {
        List<String> players = new ArrayList<>(plotMemberSize);
        for(Player plotMembers : plot.getMembers()) {
          players.add("- " + plotMembers.getName());
        }
        itemStack = new ItemBuilder(itemStack).lore(players).build();
      }
      if(plot.getMembers().contains(player)) {
        itemStack = new ItemBuilder(itemStack).lore(new MessageBuilder("IN_GAME_MESSAGES_PLOT_SELECTOR_INSIDE").asKey().build()).build();
      }
      itemStack = new ItemBuilder(itemStack).name(new MessageBuilder("IN_GAME_MESSAGES_PLOT_SELECTOR_NAME").asKey().integer(plots).build()).build();
      int finalPlots = plots;
      gui.addItem(new SimpleClickableItem(itemStack, event -> {
        event.setCancelled(true);

        if(!(event.isLeftClick() || event.isRightClick())) {
          return;
        }

        HumanEntity humanEntity = event.getWhoClicked();

        if (!(humanEntity instanceof Player)) {
          return;
        }

        PlotPlayerChooseEvent plotChooseEvent = new PlotPlayerChooseEvent(player, plot, arena);
        Bukkit.getPluginManager().callEvent(plotChooseEvent);

        if(plotChooseEvent.isCancelled() || !plot.addMember(player, arena, false)) {
          return;
        }

        new MessageBuilder("IN_GAME_MESSAGES_PLOT_SELECTOR_PLOT_CHOOSE").asKey().player(player).integer(finalPlots).sendPlayer();
        humanEntity.closeInventory();
      }));

      plots++;
    }

    gui.refresh();
    gui.open(player);
  }

  @EventHandler
  public void onPlotMenuItemClick(PlayerInteractEvent e) {
    if(!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      return;
    }

    if(!VersionUtils.getItemInHand(e.getPlayer()).equals(baseItem.getItemStack())) {
      return;
    }

    BaseArena arena = plugin.getArenaRegistry().getArena(e.getPlayer());
    if(arena == null) {
      return;
    }

    e.setCancelled(true);
    createMenu(e.getPlayer(), arena);
  }

}
