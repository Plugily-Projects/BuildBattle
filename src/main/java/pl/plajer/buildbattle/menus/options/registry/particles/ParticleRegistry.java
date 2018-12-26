/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.menus.options.OptionsRegistry;
import pl.plajerlair.core.debug.Debugger;
import pl.plajerlair.core.debug.LogLevel;
import pl.plajerlair.core.utils.ConfigUtils;
import pl.plajerlair.core.utils.ItemBuilder;
import pl.plajerlair.core.utils.MinigameUtils;
import pl.plajerlair.core.utils.XMaterial;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class ParticleRegistry {

  private List<String> blackListedParticles = Arrays.asList("BLOCK_CRACK", "ITEM_CRACK", "ITEM_TAKE", "BLOCK_DUST", "MOB_APPEARANCE", "FOOTSTEP", "REDSTONE");
  private Set<ParticleItem> registeredParticles = new HashSet<>();
  private Main plugin;

  public ParticleRegistry(OptionsRegistry registry) {
    this.plugin = registry.getPlugin();
    updateParticlesFile();
    registerParticles();
  }

  private void registerParticles() {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "particles");
    for (Particle particle : Particle.values()) {
      if (blackListedParticles.contains(particle.toString())) {
        continue;
      }
      ParticleItem particleItem = new ParticleItem();
      particleItem.setItemStack(new ItemBuilder(XMaterial.fromString(config
          .getString(particle.toString() + ".material-name").toUpperCase()).parseItem())
          .name(ChatManager.colorRawMessage(config.getString(particle.toString() + ".displayname")))
          .lore(config.getStringList(particle.toString() + ".lore")
              .stream().map((lore) -> lore = ChatManager.colorRawMessage(lore)).collect(Collectors.toList()))
          .build());
      particleItem.setPermission(config.getString(particle.toString() + ".permission"));
      particleItem.setEffect(particle);
      registeredParticles.add(particleItem);
    }
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
        Debugger.debug(LogLevel.WARN, "Found outdated item in particles.yml! We've converted it to the newest version!");
      }
    }
    ConfigUtils.saveConfig(plugin, config, "particles");
  }

  public Inventory getInventory() {
    Inventory inv = Bukkit.createInventory(null, MinigameUtils.serializeInt(registeredParticles.size()) + 9,
        ChatManager.colorMessage("Menus.Option-Menu.Items.Particle.Inventory-Name"));
    for (ParticleItem item : registeredParticles) {
      inv.addItem(item.getItemStack());
    }
    inv.setItem(inv.getSize(), new ItemBuilder(new ItemStack(Material.REDSTONE_BLOCK))
        .name(ChatManager.colorMessage("Menus.Option-Menu.Items.Particle.In-Inventory-Item-Name"))
        .lore(Collections.singletonList(ChatManager.colorMessage("Menus.Option-Menu.Items.Particle.In-Inventory-Item-Lore")))
        .build());
    return inv;
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
