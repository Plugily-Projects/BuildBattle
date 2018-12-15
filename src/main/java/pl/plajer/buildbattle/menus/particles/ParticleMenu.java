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

package pl.plajer.buildbattle.menus.particles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.api.StatsStorage;
import pl.plajer.buildbattle.arena.plots.Plot;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.utils.Utils;
import pl.plajerlair.core.debug.Debugger;
import pl.plajerlair.core.debug.LogLevel;
import pl.plajerlair.core.utils.ConfigUtils;
import pl.plajerlair.core.utils.XMaterial;

/**
 * Created by Tom on 23/08/2015.
 */
public class ParticleMenu {

  private static Main plugin = JavaPlugin.getPlugin(Main.class);
  private static List<ParticleItem> particleItems = new ArrayList<>();

  public static void openMenu(Player player) {
    Inventory inventory = player.getServer().createInventory(player, 6 * 9, ChatManager.colorMessage("Menus.Option-Menu.Items.Particle.Inventory-Name"));
    for (ParticleItem particleItem : particleItems) {
      if (particleItem.isEnabled()) {
        inventory.addItem(particleItem.getItemStack());
      }
    }
    ItemStack itemStack = new ItemStack(Material.REDSTONE_BLOCK);
    Utils.setItemNameAndLore(itemStack, ChatManager.colorMessage("Menus.Option-Menu.Items.Particle.In-Inventory-Item-Name"),
        new String[] {ChatManager.colorMessage("Menus.Option-Menu.Items.Particle.In-Inventory-Item-Lore")});
    inventory.setItem(53, itemStack);
    player.openInventory(inventory);
  }


  public static void loadFromConfig() {
    FileConfiguration config = ConfigUtils.getConfig(JavaPlugin.getPlugin(Main.class), "particles");
    for (Particle particle : Particle.values()) {
      if (particle.toString().equals("BLOCK_CRACK") || particle.toString().equals("ITEM_CRACK")
          || particle.toString().equals("ITEM_TAKE") || particle.toString().equals("BLOCK_DUST")
          || particle.toString().equals("MOB_APPEARANCE") || particle.toString().equals("FOOTSTEP") || particle.toString().equals("REDSTONE")) {
        continue;
      }
      if (!config.contains(particle.toString())) {
        config.set(particle.toString() + ".displayname", "&6" + particle.toString());
        config.set(particle.toString() + ".lore", Arrays.asList("Click to activate", "on your location"));
        config.set(particle.toString() + ".material-name", Material.PAPER.name());
        config.set(particle.toString() + ".enabled", true);
        config.set(particle.toString() + ".permission", "particles.VIP");
      } else {
        if (!config.isSet(particle.toString() + ".material-name")) {
          config.set(particle.toString() + ".material-name", Material.PAPER.name());
          Debugger.debug(LogLevel.WARN, "Found outdated item in particles.yml! We've converted it to the newest version!");
        }
      }
      ConfigUtils.saveConfig(JavaPlugin.getPlugin(Main.class), config, "particles");
      ParticleItem particleItem = new ParticleItem();
      ItemStack stack = XMaterial.fromString(config.getString(particle.toString() + ".material-name").toUpperCase()).parseItem();
      ItemMeta meta = stack.getItemMeta();
      meta.setDisplayName(ChatManager.colorRawMessage(config.getString(particle.toString() + ".displayname")));
      List<String> colorizedLore = new ArrayList<>();
      for (String str : config.getStringList(particle.toString() + ".lore")) {
        colorizedLore.add(ChatManager.colorRawMessage(str));
      }
      meta.setLore(colorizedLore);
      stack.setItemMeta(meta);

      particleItem.setItemStack(stack);
      particleItem.setEnabled(config.getBoolean(particle.toString() + ".enabled"));
      particleItem.setPermission(config.getString(particle.toString() + ".permission"));
      particleItem.setEffect(particle);
      particleItems.add(particleItem);
    }
    ConfigUtils.saveConfig(JavaPlugin.getPlugin(Main.class), config, "particles");
  }


  public static void onClick(Player player, ItemStack itemStack, Plot buildPlot) {
    for (ParticleItem particleItem : particleItems) {
      if (particleItem.getDisplayName().equalsIgnoreCase(itemStack.getItemMeta().getDisplayName()) && particleItem.getItemStack().getType() == itemStack.getType()) {
        if (!player.hasPermission(particleItem.getPermission())) {
          player.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("In-Game.No-Permission-For-Particle"));
        } else {
          if (buildPlot.getParticles().size() >= plugin.getConfig().getInt("Max-Amount-Particles", 25)) {
            player.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("In-Game.Max-Particles-Limit-Reached"));
          } else {
            buildPlot.addParticle(player.getLocation(), particleItem.getEffect());
            plugin.getUserManager().getUser(player.getUniqueId()).addStat(StatsStorage.StatisticType.PARTICLES_USED, 1);
            player.sendMessage(ChatManager.getPrefix() + ChatManager.colorMessage("In-Game.Particle-Added"));
          }
        }
      }
    }
  }


  public static ParticleItem getParticleItem(Particle effect) {
    for (ParticleItem particleItem : particleItems) {
      if (effect == particleItem.getEffect()) {
        return particleItem;
      }
    }
    return null;
  }
}
