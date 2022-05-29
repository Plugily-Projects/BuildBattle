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

package plugily.projects.buildbattle.old.arena.managers.plots;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import plugily.projects.buildbattle.old.Main;
import plugily.projects.buildbattle.old.api.event.plot.BBPlayerChoosePlotEvent;
import plugily.projects.buildbattle.old.arena.ArenaRegistry;
import plugily.projects.buildbattle.old.handlers.items.SpecialItem;
import plugily.projects.buildbattle.old.handlers.items.SpecialItemsManager;
import plugily.projects.buildbattle.old.utils.Utils;
import plugily.projects.commonsbox.minecraft.compat.VersionUtils;
import plugily.projects.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.commonsbox.minecraft.item.ItemBuilder;
import plugily.projects.inventoryframework.gui.GuiItem;
import plugily.projects.inventoryframework.gui.type.ChestGui;
import plugily.projects.inventoryframework.pane.StaticPane;

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
  private final String fullTeam;
  private final String emptyTeam;
  private final String insideTeam;
  private final String teamName;
  private final SpecialItem baseItem;

  public PlotMenuHandler(Main plugin) {
    this.plugin = plugin;
    this.baseItem = plugin.getSpecialItemsManager().getSpecialItem(SpecialItemsManager.SpecialItems.PLOT_SELECTOR.getName());
    empty = XMaterial.GREEN_WOOL.parseItem();
    waiting = XMaterial.YELLOW_WOOL.parseItem();
    full = XMaterial.RED_WOOL.parseItem();
    inside = XMaterial.PINK_WOOL.parseItem();
    fullTeam = plugin.getChatManager().colorMessage("Plots.Team.Full");
    emptyTeam = plugin.getChatManager().colorMessage("Plots.Team.Empty");
    insideTeam = plugin.getChatManager().colorMessage("Plots.Team.Inside");
    teamName = plugin.getChatManager().colorMessage("Plots.Team.Name");

    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  private ItemStack getItemStack(Plot plot, int maxPlotMembers, Player player) {
    int plotMembers = plot.getMembers().size();

    if(plotMembers == 0) {
      return empty.clone();
    }

    if(plot.getMembers().contains(player)) {
      return inside.clone();
    }

    if(plotMembers == maxPlotMembers) {
      return full.clone();
    }

    return waiting.clone();
  }

  public void createMenu(Player player, BaseArena arena) {
    ChestGui gui = new ChestGui(Utils.serializeInt(arena.getPlotManager().getPlots().size()) / 9, plugin.getChatManager().colorMessage("Plots.Team.Menu-Name"));
    StaticPane pane = new StaticPane(9, gui.getRows());
    gui.addPane(pane);
    int x = 0;
    int y = 0;
    int plots = 0;
    for(Plot plot : arena.getPlotManager().getPlots()) {
      ItemStack itemStack = getItemStack(plot, arena.getPlotSize(), player);
      itemStack.setAmount(plot.getMembers().size() == 0 ? 1 : plot.getMembers().size());
      if(plot.getMembers().size() >= arena.getPlotSize()) {
        itemStack = new ItemBuilder(itemStack).lore(fullTeam).build();
      } else {
        itemStack = new ItemBuilder(itemStack).lore(emptyTeam).build();
      }
      if(plot.getMembers().size() > 0) {
        List<String> players = new ArrayList<>();
        for(Player inside : plot.getMembers()) {
          players.add("- " + inside.getName());
        }
        itemStack = new ItemBuilder(itemStack).lore(players).build();
      }
      if(plot.getMembers().contains(player)) {
        itemStack = new ItemBuilder(itemStack).lore(insideTeam).build();
      }
      itemStack = new ItemBuilder(itemStack).name(teamName.replace("%plot%", Integer.toString(plots))).build();
      int finalPlots = plots;
      pane.addItem(new GuiItem(itemStack, e -> {
        e.setCancelled(true);
        if(!(e.getWhoClicked() instanceof Player) || !(e.isLeftClick() || e.isRightClick())) {
          return;
        }
        BBPlayerChoosePlotEvent event = new BBPlayerChoosePlotEvent(player, plot, arena);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) {
          return;
        }
        if(!plot.addMember(player, arena, false)) {
          return;
        }
        player.sendMessage(plugin.getChatManager().colorMessage("Plots.Team.Plot-Choose").replace("%plot%", Integer.toString(finalPlots)));
        e.getWhoClicked().closeInventory();
      }), x, y);
      plots++;
      x++;
      if(x == 9) {
        x = 0;
        y++;
      }
    }
    gui.show(player);
  }

  @EventHandler
  public void onPlotMenuItemClick(PlayerInteractEvent e) {
    if(!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      return;
    }

    if(!VersionUtils.getItemInHand(e.getPlayer()).equals(baseItem.getItemStack())) {
      return;
    }

    BaseArena arena = ArenaRegistry.getArena(e.getPlayer());
    if(arena == null) {
      return;
    }

    e.setCancelled(true);
    createMenu(e.getPlayer(), arena);
  }

}
