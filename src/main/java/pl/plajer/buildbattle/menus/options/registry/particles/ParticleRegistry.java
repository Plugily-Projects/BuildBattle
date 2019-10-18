/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Tigerpanzer_02, Plajer and contributors
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

package pl.plajer.buildbattle.menus.options.registry.particles;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.menus.options.OptionsRegistry;
import pl.plajer.buildbattle.utils.Debugger;
import pl.plajer.buildbattle.utils.Utils;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class ParticleRegistry {

  private Inventory inventory;
  private List<String> blackListedParticles = Arrays.asList("BLOCK_CRACK", "ITEM_CRACK", "ITEM_TAKE", "BLOCK_DUST", "MOB_APPEARANCE", "FOOTSTEP", "REDSTONE");
  private Set<ParticleItem> registeredParticles = new HashSet<>();
  private Main plugin;

  public ParticleRegistry(OptionsRegistry registry) {
    this.plugin = registry.getPlugin();
    updateParticlesFile();
    registerParticles();
    registerInventory();
  }

  private void registerParticles() {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "particles");
    Debugger.debug(Debugger.Level.TASK, "Registering particles!");
    int i = 0;
    for (Particle particle : Particle.values()) {
      if (i >= 52) {
        Debugger.debug(Debugger.Level.WARN, "There are too many particles to register! Menu can't hold any more!");
        break;
      }
      boolean blacklisted = false;
      for (String blackList : blackListedParticles) {
        if (particle.name().contains(blackList)) {
          blacklisted = true;
          break;
        }
      }
      if (blacklisted) {
        continue;
      }
      ParticleItem particleItem = new ParticleItem();
      particleItem.setItemStack(new ItemBuilder(XMaterial.fromString(config
          .getString(particle.toString() + ".material-name").toUpperCase()).parseItem())
          .name(plugin.getChatManager().colorRawMessage(config.getString(particle.toString() + ".displayname")))
          .lore(config.getStringList(particle.toString() + ".lore")
              .stream().map(lore -> lore = plugin.getChatManager().colorRawMessage(lore)).collect(Collectors.toList()))
          .build());
      particleItem.setPermission(config.getString(particle.toString() + ".permission"));
      particleItem.setEffect(particle);
      registeredParticles.add(particleItem);
      i++;
    }
    Debugger.debug(Debugger.Level.INFO, "Registered in total " + i + " particles!");
    ConfigUtils.saveConfig(plugin, config, "particles");
  }

  private void updateParticlesFile() {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "particles");
    for (Particle particle : Particle.values()) {
      if (!config.isSet(particle.toString())) {
        config.set(particle.toString() + ".displayname", "&6" + particle.toString());
        config.set(particle.toString() + ".lore", Arrays.asList("&7Click to activate", "&7on your location"));
        config.set(particle.toString() + ".material-name", Material.PAPER.name());
        config.set(particle.toString() + ".permission", "particles.VIP");
        continue;
      }
      if (!config.isSet(particle.toString() + ".material-name")) {
        config.set(particle.toString() + ".material-name", Material.PAPER.name());
        Debugger.debug(Debugger.Level.WARN, "Found outdated item in particles.yml! We've converted it to the newest version!");
      }
    }
    ConfigUtils.saveConfig(plugin, config, "particles");
  }

  private void registerInventory() {
    Inventory inv = Bukkit.createInventory(null, Utils.serializeInt(registeredParticles.size()),
        plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Particle.Inventory-Name"));
    for (ParticleItem item : registeredParticles) {
      inv.addItem(item.getItemStack());
    }
    inv.setItem(Utils.serializeInt(registeredParticles.size()) - 1, new ItemBuilder(new ItemStack(Material.REDSTONE_BLOCK))
        .name(plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Particle.In-Inventory-Item-Name"))
        .lore(Collections.singletonList(plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Particle.In-Inventory-Item-Lore")))
        .build());
    inventory = inv;
  }

  public Inventory getInventory() {
    return inventory;
  }

  public void setInventory(Inventory inventory) {
    this.inventory = inventory;
  }

  @Nullable
  public ParticleItem getItemByEffect(Particle effect) {
    for (ParticleItem item : registeredParticles) {
      if (item.getEffect() == effect) {
        return item;
      }
    }
    return null;
  }

  public Set<ParticleItem> getRegisteredParticles() {
    return registeredParticles;
  }
}
