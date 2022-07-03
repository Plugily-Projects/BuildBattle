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

package plugily.projects.buildbattle.handlers.menu.registry.particles;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.handlers.menu.OptionsRegistry;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.inventory.common.item.ItemMap;
import plugily.projects.minigamesbox.inventory.common.item.SimpleClickableItem;
import plugily.projects.minigamesbox.inventory.paged.PagedFastInv;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class ParticleRegistry {

  private PagedFastInv particles;
  private final List<String> blackListedParticles = Arrays.asList("BLOCK_CRACK", "ITEM_CRACK", "ITEM_TAKE", "BLOCK_DUST", "MOB_APPEARANCE", "FOOTSTEP", "REDSTONE");
  private final Set<ParticleItem> registeredParticles = new HashSet<>();
  private final Main plugin;

  public ParticleRegistry(OptionsRegistry registry) {
    this.plugin = registry.getPlugin();
    updateParticlesFile();
    registerParticles();
    registerInventory();
  }

  private void registerParticles() {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "particles");
    plugin.getDebugger().debug("Registering particles!");
    int i = 0;
    for(String particle : VersionUtils.getParticleValues()) {
      if(i >= 100) {
        plugin.getDebugger().debug("There are too many particles to register! Menu can't hold any more!");
        break;
      }
      boolean blacklisted = false;
      for(String blackList : blackListedParticles) {
        if(particle.contains(blackList)) {
          blacklisted = true;
          break;
        }
      }
      if(config.getBoolean(particle + ".disabled")) {
        blacklisted = true;
      }
      if(blacklisted) {
        continue;
      }
      List<String> lore = config.getStringList(particle + ".lore");
      for(int a = 0; a < lore.size(); a++) {
        lore.set(a, new MessageBuilder(lore.get(a)).build());
      }

      ParticleItem particleItem = new ParticleItem();
      particleItem.setItemStack(new ItemBuilder(XMaterial.matchXMaterial(config
          .getString(particle + ".material-name", "bedrock").toUpperCase()).orElse(XMaterial.BEDROCK).parseItem())
          .name(new MessageBuilder(config.getString(particle + ".displayname")).build())
          .lore(lore)
          .build());
      particleItem.setPermission(config.getString(particle + ".permission"));
      particleItem.setEffect(particle);
      registeredParticles.add(particleItem);
      i++;
    }
    plugin.getDebugger().debug("Registered in total " + i + " particles!");
    ConfigUtils.saveConfig(plugin, config, "particles");
  }

  private void updateParticlesFile() {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "particles");
    for(String particle : VersionUtils.getParticleValues()) {
      if(!config.isSet(particle)) {
        config.set(particle + ".displayname", "&6" + particle);
        config.set(particle + ".lore", Arrays.asList("&7Click to activate", "&7on your location"));
        config.set(particle + ".material-name", Material.PAPER.name());
        config.set(particle + ".permission", "particles.VIP");
        continue;
      }
      if(!config.isSet(particle + ".material-name")) {
        config.set(particle + ".material-name", Material.PAPER.name());
        plugin.getDebugger().debug("Found outdated item in particles.yml! We've converted it to the newest version!");
      }
    }
    ConfigUtils.saveConfig(plugin, config, "particles");
  }

  private void registerInventory() {
    int i = 0;
    Set<ParticleItem> particleItemsPage1 = new HashSet<>();
    Set<ParticleItem> particleItemsPage2 = new HashSet<>();
    for(ParticleItem item : registeredParticles) {
      (i >= 45 ? particleItemsPage2 : particleItemsPage1).add(item);
      i++;
    }

    PagedFastInv gui = new PagedFastInv(5 * 9, new MessageBuilder("MENU_OPTION_CONTENT_PARTICLE_INVENTORY").asKey().build());

    gui.setForceRefresh(true);

    ItemMap page1 = gui.createNewPage();
    ItemMap page2 = gui.createNewPage();

    page2.setItem(45, new SimpleClickableItem(new ItemStack(Material.ARROW), event -> {
      gui.setCurrentPage(1);
      gui.refresh();
    }));
    page1.setItem(53, new SimpleClickableItem(new ItemStack(Material.ARROW), event -> {
      gui.setCurrentPage(2);
      gui.refresh();
    }));
    addParticles(page1, particleItemsPage1);
    addParticles(page2, particleItemsPage2);
    addRemoveItem(page1);

    addRemoveItem(page2);

    setParticles(gui);
    page1.setItem(46, new SimpleClickableItem(plugin.getOptionsRegistry().getGoBackItem(), null));
    page2.setItem(46, new SimpleClickableItem(plugin.getOptionsRegistry().getGoBackItem(), null));
  }

  private void addRemoveItem(ItemMap itemMap) {
    itemMap.setItem(49, new SimpleClickableItem(new ItemBuilder(new ItemStack(Material.REDSTONE_BLOCK))
        .name(new MessageBuilder("MENU_OPTION_CONTENT_PARTICLE_ITEM_REMOVE_NAME").asKey().build())
        .lore(Collections.singletonList(new MessageBuilder("MENU_OPTION_CONTENT_PARTICLE_ITEM_REMOVE_LORE").asKey().build()))
        .build(), event -> {
      Player who = (Player) event.getWhoClicked();
      BaseArena arena = (BaseArena) plugin.getArenaRegistry().getArena(who);
      if(arena == null) {
        return;
      }
      event.setCancelled(false);
      who.closeInventory();
      ParticleRemoveMenu.openMenu(who, arena.getPlotManager().getPlot(who));
    }));
  }

  private void addParticles(ItemMap itemMap, Set<ParticleItem> particleItems) {
    for(ParticleItem item : particleItems) {
      itemMap.addItem(new SimpleClickableItem(item.getItemStack(), event -> {
        Player who = (Player) event.getWhoClicked();
        BaseArena arena = (BaseArena) plugin.getArenaRegistry().getArena(who);
        if(arena == null) {
          return;
        }
        Plot plot = arena.getPlotManager().getPlot(who);
        if(!who.hasPermission(item.getPermission())) {
          new MessageBuilder("IN_GAME_MESSAGES_PLOT_PERMISSION_PARTICLE").asKey().player(who).sendPlayer();
          return;
        }
        if(plot.getParticles().size() >= plugin.getConfig().getInt("Particle.Max-Amount", 25)) {
          new MessageBuilder("IN_GAME_MESSAGES_PLOT_LIMIT_PARTICLES").asKey().player(who).sendPlayer();
          return;
        }
        plot.getParticles().put(who.getLocation(), item.getEffect());
        plugin.getUserManager().getUser(who)
            .adjustStatistic("PARTICLES_USED", 1);
        new MessageBuilder("MENU_OPTION_CONTENT_PARTICLE_ADDED").asKey().player(who).sendPlayer();
      }));
    }
  }

  public PagedFastInv getParticles() {
    return particles;
  }

  public void setParticles(PagedFastInv particles) {
    this.particles = particles;
  }

  @Nullable
  public ParticleItem getItemByEffect(String effect) {
    for(ParticleItem item : registeredParticles) {
      if(item.getEffect().equals(effect)) {
        return item;
      }
    }
    return null;
  }

  public Set<ParticleItem> getRegisteredParticles() {
    return registeredParticles;
  }
}
