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

package pl.plajer.buildbattle3.menus.particles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import pl.plajer.buildbattle3.ConfigPreferences;
import pl.plajer.buildbattle3.arena.plots.ArenaPlot;
import pl.plajer.buildbattle3.handlers.ChatManager;
import pl.plajer.buildbattle3.handlers.ConfigurationManager;
import pl.plajer.buildbattle3.user.UserManager;
import pl.plajer.buildbattle3.utils.Util;

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
    Util.setItemNameAndLore(itemStack, ChatManager.colorMessage("Menus.Option-Menu.Particle-Remove"), new String[]{ChatManager.colorMessage("Menus.Option-Menu.Particle-Remove-Lore")});
    inventory.setItem(53, itemStack);
    player.openInventory(inventory);
  }


  public static void loadFromConfig() {
    FileConfiguration config = ConfigurationManager.getConfig("particles");
    int slotCounter = 0;
    for (Particle particle : Particle.values()) {
      if (particle == Particle.BLOCK_CRACK || particle == Particle.ITEM_CRACK || particle == Particle.ITEM_TAKE || particle == Particle.BLOCK_DUST || particle == Particle.MOB_APPEARANCE)
        continue;
      if (!config.contains(particle.toString())) {
        config.set(particle.toString() + ".data", 0);
        config.set(particle.toString() + ".displayname", "&6" + particle.toString());
        config.set(particle.toString() + ".lore", Arrays.asList("Click to activate", "on your location"));
        config.set(particle.toString() + ".material", org.bukkit.Material.PAPER.getId());
        config.set(particle.toString() + ".enabled", true);
        config.set(particle.toString() + ".permission", "particles.VIP");
        config.set(particle.toString() + ".slot", slotCounter);
        slotCounter++;
      }
      ParticleItem particleItem = new ParticleItem();
      particleItem.setData(config.getInt(particle.toString() + ".data"));
      particleItem.setEnabled(config.getBoolean(particle.toString() + ".enabled"));
      particleItem.setMaterial(org.bukkit.Material.getMaterial(config.getInt(particle.toString() + ".material")));
      particleItem.setLore(config.getStringList(particle.toString() + ".lore"));
      particleItem.setDisplayName(config.getString(particle.toString() + ".displayname"));
      particleItem.setPermission(config.getString(particle.toString() + ".permission"));
      particleItem.setEffect(particle);
      particleItem.setSlot(config.getInt(particle.toString() + ".slot"));
      particleItems.add(particleItem);
    }
    ConfigurationManager.saveConfig(config, "particles");
  }


  public static void onClick(Player player, ItemStack itemStack, ArenaPlot buildPlot) {
    for (ParticleItem particleItem : particleItems) {
      if (particleItem.getDisplayName().equalsIgnoreCase(itemStack.getItemMeta().getDisplayName()) && particleItem.getMaterial() == itemStack.getType()) {
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
