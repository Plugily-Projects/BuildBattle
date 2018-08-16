/*
 * BuildBattle 3 - Ultimate building competition minigame
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
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

package pl.plajer.buildbattle3.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.handlers.ChatManager;

/**
 * @author Plajer
 * <p>
 * Created at 16.08.2018
 */
public class CuboidSelector implements Listener {

  private Map<Player, Selection> selections = new HashMap<>();

  public CuboidSelector(Main plugin){
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  public void giveSelectorWand(Player p) {
    ItemStack stack = new ItemBuilder(Material.BLAZE_ROD).name(ChatManager.colorRawMessage("&6&lPlot selector")).build();
    p.getInventory().addItem(stack);

    p.sendMessage(ChatManager.colorRawMessage(ChatManager.PLUGIN_PREFIX + "&eYou received plot selector wand!"));
    p.sendMessage(ChatManager.colorRawMessage(ChatManager.PLUGIN_PREFIX + "&eSelect bottom corner using left click!"));
  }

  public Selection getSelection(Player p){
    if(selections.containsKey(p)){
      return selections.get(selections.get(p));
    }
    return null;
  }

  @EventHandler
  public void onWandUse(PlayerInteractEvent e) {
    if (e.getItem().hasItemMeta() && e.getItem().getItemMeta().hasDisplayName() && e.getItem().getItemMeta().getDisplayName().equals(ChatManager.colorRawMessage("&6&lPlot selector"))) {
      Action action = e.getAction();
      switch (action) {
        case LEFT_CLICK_BLOCK:
          selections.put(e.getPlayer(), new Selection(e.getClickedBlock().getLocation(), null));
          e.getPlayer().sendMessage(ChatManager.colorRawMessage(ChatManager.PLUGIN_PREFIX + "&eNow select top corner using right click!"));
          break;
        case RIGHT_CLICK_BLOCK:
          if(!selections.containsKey(e.getPlayer())){
            e.getPlayer().sendMessage(ChatManager.colorRawMessage(ChatManager.PLUGIN_PREFIX + "&cPlease select bottom corner using left click first!"));
            break;
          }
          selections.put(e.getPlayer(), new Selection(selections.get(e.getPlayer()).getFirstPos(), e.getClickedBlock().getLocation()));
          e.getPlayer().sendMessage(ChatManager.colorRawMessage(ChatManager.PLUGIN_PREFIX + "&eNow you can add plot via setup menu!"));
          break;
        case LEFT_CLICK_AIR:
          e.getPlayer().sendMessage(ChatManager.colorRawMessage(ChatManager.PLUGIN_PREFIX + "&cPlease select solid block (not air)!"));
          break;
        case RIGHT_CLICK_AIR:
          e.getPlayer().sendMessage(ChatManager.colorRawMessage(ChatManager.PLUGIN_PREFIX + "&cPlease select solid block (not air)!"));
          break;
        default:
          break;
      }
    }
  }

  public class Selection {

    private Location firstPos;
    private Location secondPos;

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
