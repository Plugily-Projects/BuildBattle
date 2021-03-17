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

package plugily.projects.buildbattle.menus.options.registry.particles;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.managers.plots.Plot;

import java.util.Map.Entry;

/**
 * Created by Tom on 24/08/2015.
 */
public class ParticleRemoveMenu {

  private static final Main plugin = JavaPlugin.getPlugin(Main.class);

  private ParticleRemoveMenu() {
  }

  public static void openMenu(Player player, Plot buildPlot) {
    Gui gui = new Gui(plugin, 6, plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Particle.In-Inventory-Item-Name"));
    StaticPane pane = new StaticPane(9, 6);

    int x = 0;
    int y = 0;
    for(Entry<Location, String> map : new java.util.HashMap<>(buildPlot.getParticles()).entrySet()) {
      Location location = map.getKey();
      ParticleItem particleItem = plugin.getOptionsRegistry().getParticleRegistry().getItemByEffect(map.getValue());
      if(particleItem == null) {
        continue;
      }

      ItemStack itemStack = new ItemBuilder(particleItem.getItemStack().clone()).lore(plugin.getChatManager().colorMessage("Menus.Location-Message"),
          ChatColor.GRAY + "  x: " + Math.round(location.getX()),
          ChatColor.GRAY + "  y: " + Math.round(location.getY()),
          ChatColor.GRAY + "  z: " + Math.round(location.getZ())).build();
      pane.addItem(new GuiItem(itemStack, event -> {
        buildPlot.getParticles().remove(location);
        event.getWhoClicked().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Particle.Particle-Removed"));
        gui.getItems().forEach(item -> {
          if(item.getItem().isSimilar(itemStack)) {
            item.setVisible(false);
          }
        });
        gui.update();
        event.getWhoClicked().closeInventory();
        openMenu(player, buildPlot);
      }), x, y);
      x++;
      if(x == 9) {
        x = 0;
        y++;
      }
    }

    pane.addItem(new GuiItem(new ItemBuilder(new ItemStack(Material.ARROW))
        .name(plugin.getChatManager().colorMessage("Menus.Buttons.Back-Button.Name"))
        .lore(plugin.getChatManager().colorMessage("Menus.Buttons.Back-Button.Lore"))
        .build(), event -> {
      event.getWhoClicked().closeInventory();
      event.getWhoClicked().openInventory(plugin.getOptionsRegistry().getParticleRegistry().getPage1());
    }), 4, 5);
    gui.addPane(pane);
    gui.show(player);
  }

}
