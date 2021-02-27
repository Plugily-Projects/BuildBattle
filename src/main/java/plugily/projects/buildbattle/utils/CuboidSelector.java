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

package plugily.projects.buildbattle.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import pl.plajerlair.commonsbox.minecraft.compat.events.api.CBPlayerInteractEvent;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import pl.plajerlair.commonsbox.minecraft.item.ItemUtils;
import plugily.projects.buildbattle.Main;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Plajer
 * <p>
 * Created at 16.08.2018
 */
public class CuboidSelector implements Listener {

  private final Main plugin;
  private final Map<Player, Selection> selections = new HashMap<>();

  public CuboidSelector(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  public void giveSelectorWand(Player p) {
    ItemStack stack = new ItemBuilder(Material.BLAZE_ROD).name(plugin.getChatManager().colorRawMessage("&6&lPlot selector")).build();
    p.getInventory().addItem(stack);

    p.sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&eYou received plot selector wand!"));
    p.sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&eSelect bottom corner using left click!"));
  }

  public Selection getSelection(Player p) {
    return selections.getOrDefault(p, null);
  }

  public void removeSelection(Player p) {
    selections.remove(p);
  }

  @EventHandler
  public void onWandUse(CBPlayerInteractEvent e) {
    if(!ItemUtils.isItemStackNamed(e.getItem()) || !plugin.getComplement().getDisplayName(e.getItem().getItemMeta()).equals(plugin.getChatManager().colorRawMessage("&6&lPlot selector"))) {
      return;
    }
    e.setCancelled(true);
    switch(e.getAction()) {
      case LEFT_CLICK_BLOCK:
        selections.put(e.getPlayer(), new Selection(e.getClickedBlock().getLocation(), null));
        e.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&eNow select top corner using right click!"));
        break;
      case RIGHT_CLICK_BLOCK:
        if(!selections.containsKey(e.getPlayer())) {
          e.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&cPlease select bottom corner using left click first!"));
          break;
        }
        selections.put(e.getPlayer(), new Selection(selections.get(e.getPlayer()).getFirstPos(), e.getClickedBlock().getLocation()));
        e.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&eNow you can add plot via setup menu!"));
        break;
      case LEFT_CLICK_AIR:
      case RIGHT_CLICK_AIR:
        e.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&cPlease select solid block (not air)!"));
        break;
      default:
        break;
    }
  }

  public class Selection {

    private final Location firstPos;
    private final Location secondPos;

    public Selection(Location firstPos, Location secondPos) {
      this.firstPos = firstPos;
      this.secondPos = secondPos;
    }

    public Location getFirstPos() {
      return firstPos;
    }

    public Location getSecondPos() {
      return secondPos;
    }
  }

}
