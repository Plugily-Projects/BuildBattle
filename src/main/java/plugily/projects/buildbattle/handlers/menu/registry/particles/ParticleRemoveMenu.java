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

package plugily.projects.buildbattle.handlers.menu.registry.particles;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.inventory.common.item.SimpleClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Created by Tom on 24/08/2015.
 */
public class ParticleRemoveMenu {

  private static final Main plugin = JavaPlugin.getPlugin(Main.class);

  private ParticleRemoveMenu() {
  }

  public static void openMenu(Player player, Plot buildPlot) {
    NormalFastInv gui = new NormalFastInv(54, new MessageBuilder("MENU_OPTION_CONTENT_PARTICLE_INVENTORY").asKey().build());

    for(Entry<Location, String> map : new HashMap<>(buildPlot.getParticles()).entrySet()) {
      ParticleItem particleItem = plugin.getOptionsRegistry().getParticleRegistry().getItemByEffect(map.getValue());
      if(particleItem == null) {
        continue;
      }

      Location location = map.getKey();
      ItemStack itemStack = new ItemBuilder(particleItem.getItemStack().clone()).removeLore().lore(new MessageBuilder("MENU_LOCATION").asKey().build(),
          ChatColor.GRAY + "  x: " + Math.round(location.getX()),
          ChatColor.GRAY + "  y: " + Math.round(location.getY()),
          ChatColor.GRAY + "  z: " + Math.round(location.getZ())).build();
      gui.addItem(new SimpleClickableItem(itemStack, event -> {
        buildPlot.getParticles().remove(location);

        new MessageBuilder("MENU_OPTION_CONTENT_PARTICLE_REMOVED").asKey().player((Player) event.getWhoClicked()).sendPlayer();
        gui.refresh();
        event.setCancelled(true);
        event.getWhoClicked().closeInventory();
        openMenu(player, buildPlot);
      }));
    }

    gui.setItem(45, plugin.getOptionsRegistry().getGoBackItem());
    gui.open(player);
  }

}
