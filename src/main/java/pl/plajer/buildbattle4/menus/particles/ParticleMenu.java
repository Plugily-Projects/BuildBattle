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

package pl.plajer.buildbattle4.menus.particles;

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

import pl.plajer.buildbattle4.ConfigPreferences;
import pl.plajer.buildbattle4.Main;
import pl.plajer.buildbattle4.arena.plots.ArenaPlot;
import pl.plajer.buildbattle4.handlers.ChatManager;
import pl.plajer.buildbattle4.user.UserManager;
import pl.plajer.buildbattle4.utils.Utils;
import pl.plajer.buildbattle4.utils.XMaterial;
import pl.plajerlair.core.utils.ConfigUtils;

/**
 * Created by Tom on 23/08/2015.
 */
public class ParticleMenu {

  private static List<ParticleItem> particleItems = new ArrayList<>();

  public static void openMenu(Player player) {
    Inventory inventory = player.getServer().createInventory(player, 6 * 9, ChatManager.colorMessage("Menus.Option-Menu.Particle-Inventory-Name"));
    for (ParticleItem particleItem : particleItems) {
      if (particleItem.isEnabled()) inventory.setItem(particleItem.getSlot(), particleItem.getItemStack());
    }
    ItemStack itemStack = new ItemStack(Material.REDSTONE_BLOCK);
    Utils.setItemNameAndLore(itemStack, ChatManager.colorMessage("Menus.Option-Menu.Particle-Remove"), new String[]{ChatManager.colorMessage("Menus.Option-Menu.Particle-Remove-Lore")});
    inventory.setItem(53, itemStack);
    player.openInventory(inventory);
  }


  public static void loadFromConfig() {
    FileConfiguration config = ConfigUtils.getConfig(JavaPlugin.getPlugin(Main.class), "particles");
    int slotCounter = 0;
    for (Particle particle : Particle.values()) {
      if (particle.toString().equalsIgnoreCase("BLOCK_CRACK") || particle.toString().equalsIgnoreCase("ITEM_CRACK")
              || particle.toString().equalsIgnoreCase("ITEM_TAKE") || particle.toString().equalsIgnoreCase("BLOCK_DUST")
          || particle.toString().equalsIgnoreCase("MOB_APPEARANCE") || particle.toString().equalsIgnoreCase("FOOTSTEP"))
        continue;
      if (!config.contains(particle.toString())) {
        config.set(particle.toString() + ".displayname", "&6" + particle.toString());
        config.set(particle.toString() + ".lore", Arrays.asList("Click to activate", "on your location"));
        config.set(particle.toString() + ".material-name", Material.PAPER.name());
        config.set(particle.toString() + ".enabled", true);
        config.set(particle.toString() + ".permission", "particles.VIP");
        config.set(particle.toString() + ".slot", slotCounter);
        slotCounter++;
      } else {
        if (!config.isSet(particle.toString() + ".material-name")) {
          config.set(particle.toString() + ".material-name", Material.PAPER.name());
          Main.debug("Found outdated item in particles.yml! We've converted it to the newest version!", System.currentTimeMillis());
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
      particleItem.setSlot(config.getInt(particle.toString() + ".slot"));
      particleItems.add(particleItem);
    }
    ConfigUtils.saveConfig(JavaPlugin.getPlugin(Main.class), config, "particles");
  }


  public static void onClick(Player player, ItemStack itemStack, ArenaPlot buildPlot) {
    for (ParticleItem particleItem : particleItems) {
      if (particleItem.getDisplayName().equalsIgnoreCase(itemStack.getItemMeta().getDisplayName()) && particleItem.getItemStack().getType() == itemStack.getType()) {
        if (!player.hasPermission(particleItem.getPermission())) {
          player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.No-Permission-For-Particle"));
        } else {
          if (buildPlot.getParticles().size() >= ConfigPreferences.getMaxParticles()) {
            player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Max-Particles-Limit-Reached"));
          } else {
            buildPlot.addParticle(player.getLocation(), particleItem.getEffect());
            UserManager.getUser(player.getUniqueId()).addInt("particles", 1);
            player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Particle-Added"));
          }
        }
      }
    }
  }


  public static ParticleItem getParticleItem(Particle effect) {
    for (ParticleItem particleItem : particleItems) {
      if (effect == particleItem.getEffect()) return particleItem;
    }
    return null;
  }
}
