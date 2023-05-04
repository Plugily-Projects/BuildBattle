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

package plugily.projects.buildbattle.handlers.menu;

import org.bukkit.inventory.ItemStack;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.handlers.menu.registry.FloorChangeOption;
import plugily.projects.buildbattle.handlers.menu.registry.PlotResetOption;
import plugily.projects.buildbattle.handlers.menu.registry.TimeChangeOption;
import plugily.projects.buildbattle.handlers.menu.registry.WeatherChangeOption;
import plugily.projects.buildbattle.handlers.menu.registry.banner.BannerCreatorOption;
import plugily.projects.buildbattle.handlers.menu.registry.biomes.BiomeChangeOption;
import plugily.projects.buildbattle.handlers.menu.registry.biomes.BiomesRegistry;
import plugily.projects.buildbattle.handlers.menu.registry.particles.ParticleRegistry;
import plugily.projects.buildbattle.handlers.menu.registry.particles.ParticlesOption;
import plugily.projects.buildbattle.handlers.menu.registry.playerheads.PlayerHeadsOption;
import plugily.projects.buildbattle.handlers.menu.registry.playerheads.PlayerHeadsRegistry;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.inventory.common.item.ItemMap;
import plugily.projects.minigamesbox.inventory.common.item.SimpleClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class OptionsRegistry {

    private ParticleRegistry particleRegistry;
    private BiomesRegistry biomesRegistry;
    private PlayerHeadsRegistry playerHeadsRegistry;
    private final Set<MenuOption> registeredOptions = new HashSet<>();
    private int inventorySize = 5 * 9;
    private final ItemStack menuItem;
    private final Main plugin;

    private NormalFastInv optionsGui;

    public OptionsRegistry(Main plugin) {
        this.plugin = plugin;
        this.menuItem = plugin.getSpecialItemManager().getSpecialItemStack("OPTIONS_MENU");
    }

    public void registerOptions() {
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
        if (!registeredOptions.remove(option)) {
            throw new IllegalArgumentException("Cannot remove non existing option!");
        }
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
    public NormalFastInv getOptionsGui() {
        if (optionsGui != null)
            return optionsGui;
        NormalFastInv gui = new NormalFastInv(inventorySize, new MessageBuilder("MENU_OPTION_INVENTORY").asKey().build());
        gui.addClickHandler(clickEvent -> {
            if (clickEvent.getClickedInventory() != gui.getInventory()) {
                clickEvent.setCancelled(false);
            }
        });
        for (MenuOption option : registeredOptions) {
            gui.setItem(option.getSlot(), new SimpleClickableItem(option.getItemStack(), option::onClick));
        }
        return optionsGui = gui;
    }

    public Set<MenuOption> getRegisteredOptions() {
        return registeredOptions;
    }

    public ItemStack getMenuItem() {
        return menuItem;
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

    private ItemBuilder backButton;

    private SimpleClickableItem getBackItem() {
        if (backButton == null) {
            backButton = new ItemBuilder(XMaterial.STONE_BUTTON.parseItem())
                    .name(new MessageBuilder("MENU_BUTTONS_BACK_ITEM_NAME").asKey().build())
                    .lore(new MessageBuilder("MENU_BUTTONS_BACK_ITEM_LORE").asKey().build());
        }
        return new SimpleClickableItem(backButton.build(), event -> {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
            getOptionsGui().open(event.getWhoClicked());
        });
    }

    public void addGoBackItem(ItemMap page, int slot) {
        page.setItem(slot, getBackItem());
    }

    public void addGoBackItem(NormalFastInv gui, int slot) {
        gui.setItem(slot, getBackItem());
    }


}
