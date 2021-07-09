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

import plugily.projects.inventoryframework.gui.GuiItem;
import plugily.projects.inventoryframework.gui.type.ChestGui;
import plugily.projects.inventoryframework.pane.OutlinePane;
import plugily.projects.inventoryframework.pane.PaginatedPane;
import plugily.projects.inventoryframework.pane.StaticPane;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import plugily.projects.commonsbox.minecraft.compat.VersionUtils;
import plugily.projects.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.commonsbox.minecraft.item.ItemBuilder;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.api.StatsStorage;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.menus.options.OptionsRegistry;
import plugily.projects.buildbattle.utils.Debugger;

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

  private ChestGui particles;
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
    Debugger.debug(Debugger.Level.TASK, "Registering particles!");
    int i = 0;
    for(String particle : VersionUtils.getParticleValues()) {
      if(i >= 100) {
        Debugger.debug(Debugger.Level.WARN, "There are too many particles to register! Menu can't hold any more!");
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
        lore.set(a, plugin.getChatManager().colorRawMessage(lore.get(a)));
      }

      ParticleItem particleItem = new ParticleItem();
      particleItem.setItemStack(new ItemBuilder(XMaterial.matchXMaterial(config
          .getString(particle + ".material-name", "bedrock").toUpperCase()).orElse(XMaterial.BEDROCK).parseItem())
          .name(plugin.getChatManager().colorRawMessage(config.getString(particle + ".displayname")))
          .lore(lore)
          .build());
      particleItem.setPermission(config.getString(particle + ".permission"));
      particleItem.setEffect(particle);
      registeredParticles.add(particleItem);
      i++;
    }
    Debugger.debug(Debugger.Level.INFO, "Registered in total " + i + " particles!");
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
        Debugger.debug(Debugger.Level.WARN, "Found outdated item in particles.yml! We've converted it to the newest version!");
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

    ChestGui gui = new ChestGui(6, plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Particle.Inventory-Name"));

    PaginatedPane pane = new PaginatedPane(0, 0, 9, 5);

//page one
    OutlinePane pageOne = getParticlePage(particleItemsPage1);
    pane.addPane(0, pageOne);

//page two
    OutlinePane pageTwo = getParticlePage(particleItemsPage2);
    pane.addPane(1, pageTwo);

    gui.addPane(pane);

//page selection
    StaticPane back = new StaticPane(2, 5, 1, 1);
    StaticPane forward = new StaticPane(6, 5, 1, 1);

    back.addItem(new GuiItem(new ItemStack(Material.ARROW), event -> {
      pane.setPage(pane.getPage() - 1);

      if(pane.getPage() == 0) {
        back.setVisible(false);
      }

      forward.setVisible(true);
      gui.update();
      event.setCancelled(true);
    }), 0, 0);

    back.setVisible(false);

    forward.addItem(new GuiItem(new ItemStack(Material.ARROW), event -> {
      pane.setPage(pane.getPage() + 1);

      if(pane.getPage() == pane.getPages() - 1) {
        forward.setVisible(false);
      }

      back.setVisible(true);
      gui.update();
      event.setCancelled(true);
    }), 0, 0);

    gui.addPane(back);
    gui.addPane(forward);

    StaticPane particleRemover = new StaticPane(4, 5, 1, 1);
    particleRemover.addItem(new GuiItem(new ItemBuilder(new ItemStack(Material.REDSTONE_BLOCK))
        .name(plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Particle.In-Inventory-Item-Name"))
        .lore(Collections.singletonList(plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Particle.In-Inventory-Item-Lore")))
        .build(), event -> {
      Player who = (Player) event.getWhoClicked();
      BaseArena arena = ArenaRegistry.getArena(who);
      if(arena == null) {
        return;
      }
      event.setCancelled(false);
      who.closeInventory();
      ParticleRemoveMenu.openMenu(who, arena.getPlotManager().getPlot(who));
    }), 0, 0);
    gui.addPane(particleRemover);

    setParticles(gui);
  }

  private OutlinePane getParticlePage(Set<ParticleItem> particleItems) {
    OutlinePane page = new OutlinePane(0, 0, 9, 5);
    for(ParticleItem item : particleItems) {
      page.addItem(new GuiItem(item.getItemStack(), event -> {
        Player who = (Player) event.getWhoClicked();
        BaseArena arena = ArenaRegistry.getArena(who);
        if(arena == null) {
          return;
        }
        Plot plot = arena.getPlotManager().getPlot(who);
        if(!who.hasPermission(item.getPermission())) {
          who.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("In-Game.No-Permission-For-Particle"));
          return;
        }
        if(plot.getParticles().size() >= plugin.getConfig().getInt("Max-Amount-Particles", 25)) {
          who.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("In-Game.Max-Particles-Limit-Reached"));
          return;
        }
        plot.getParticles().put(who.getLocation(), item.getEffect());
        plugin.getUserManager().getUser(who)
            .addStat(StatsStorage.StatisticType.PARTICLES_USED, 1);
        who.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("In-Game.Particle-Added"));
      }));
    }
    return page;
  }

  public ChestGui getParticles() {
    return particles;
  }

  public void setParticles(ChestGui particles) {
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
