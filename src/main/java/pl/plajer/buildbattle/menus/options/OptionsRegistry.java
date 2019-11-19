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

package pl.plajer.buildbattle.menus.options;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.menus.options.registry.FloorChangeOption;
import pl.plajer.buildbattle.menus.options.registry.PlotResetOption;
import pl.plajer.buildbattle.menus.options.registry.TimeChangeOption;
import pl.plajer.buildbattle.menus.options.registry.WeatherChangeOption;
import pl.plajer.buildbattle.menus.options.registry.banner.BannerCreatorOption;
import pl.plajer.buildbattle.menus.options.registry.biomes.BiomeChangeOption;
import pl.plajer.buildbattle.menus.options.registry.biomes.BiomesRegistry;
import pl.plajer.buildbattle.menus.options.registry.particles.ParticleRegistry;
import pl.plajer.buildbattle.menus.options.registry.particles.ParticlesOption;
import pl.plajer.buildbattle.menus.options.registry.playerheads.PlayerHeadsOption;
import pl.plajer.buildbattle.menus.options.registry.playerheads.PlayerHeadsRegistry;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class OptionsRegistry {

  private ParticleRegistry particleRegistry;
  private BiomesRegistry biomesRegistry;
  private PlayerHeadsRegistry playerHeadsRegistry;
  private Set<MenuOption> registeredOptions = new HashSet<>();
  private int inventorySize = 5 * 9;
  private Main plugin;

  public OptionsRegistry(Main plugin) {
    this.plugin = plugin;
    registerOptions();
  }

  private void registerOptions() {
    biomesRegistry = new BiomesRegistry(this);
    new BiomeChangeOption(this);

    new FloorChangeOption(this);

    //register particles
    particleRegistry = new ParticleRegistry(this);
    new ParticlesOption(this);

    //register player heads
    playerHeadsRegistry = new PlayerHeadsRegistry(this);
    new PlayerHeadsOption(this);

    new PlotResetOption(this);
    new TimeChangeOption(this);
    new WeatherChangeOption(this);
    new BannerCreatorOption(this);
  }

  /**
   * Registers new menu option available in options menu in game.
   *
   * @param option option to register
   * @throws IllegalArgumentException if option slot is same as one of already registered ones
   *                                  or ID of option is same as one of registered one
   */
  public void registerOption(MenuOption option) {
    for (MenuOption opt : registeredOptions) {
      if (opt.getSlot() == option.getSlot()) {
        throw new IllegalArgumentException("Cannot register new option on existing option slot!");
      }
      if (opt.getID().equals(option.getID())) {
        throw new IllegalArgumentException("Cannot register new option with equal identifier!");
      }
    }
    registeredOptions.add(option);
  }

  /**
   * Unregisters menu option that available in options menu
   *
   * @param option option to unregister
   * @throws IllegalArgumentException if option doesn't exist
   */
  public void unregisterOption(MenuOption option) {
    if (!registeredOptions.contains(option)) {
      throw new IllegalArgumentException("Cannot remove non existing option!");
    }
    registeredOptions.remove(option);
  }

  /**
   * Defines new size of options inventory
   *
   * @param size size to set
   */
  public void defineInventorySize(int size) {
    inventorySize = size;
  }

  /**
   * Creates inventory with all of registered menu options
   *
   * @return options inventory
   */
  public Inventory formatInventory() {
    Inventory inv = Bukkit.createInventory(null, inventorySize, plugin.getChatManager().colorMessage("Menus.Option-Menu.Inventory-Name"));
    for (MenuOption option : registeredOptions) {
      inv.setItem(option.getSlot(), option.getItemStack());
    }
    return inv;
  }

  public Set<MenuOption> getRegisteredOptions() {
    return registeredOptions;
  }

  @Deprecated
  public ItemStack getMenuItem() {
    return new ItemBuilder(Material.NETHER_STAR)
        .name(plugin.getChatManager().colorMessage("Menus.Option-Menu.Option-Item"))
        .lore(plugin.getChatManager().colorMessage("Menus.Option-Menu.Option-Item-Lore")).build();
  }

  public BiomesRegistry getBiomesRegistry() {
    return biomesRegistry;
  }

  public PlayerHeadsRegistry getPlayerHeadsRegistry() {
    return playerHeadsRegistry;
  }

  public ParticleRegistry getParticleRegistry() {
    return particleRegistry;
  }

  public Main getPlugin() {
    return plugin;
  }
}
