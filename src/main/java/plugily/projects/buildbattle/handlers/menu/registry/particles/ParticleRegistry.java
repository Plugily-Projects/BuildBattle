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

package plugily.projects.buildbattle.handlers.menu.registry.particles;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
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
import plugily.projects.minigamesbox.inventory.utils.fastinv.InventoryScheme;
import plugily.projects.minigamesbox.inventory.utils.fastinv.PaginatedFastInv;

import java.util.*;


/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class ParticleRegistry {

  private PaginatedFastInv particles;
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
    plugin.getDebugger().debug("Registering particles!");

    FileConfiguration config = ConfigUtils.getConfig(plugin, "particles");
    int i = 0;

    for(String particle : VersionUtils.PARTICLE_VALUES) {
      if(i >= 100) {
        plugin.getDebugger().debug("There are too many particles to register! Menu can't hold any more!");
        break;
      }

      if(blackListedParticles.contains(particle) || config.getBoolean(particle + ".disabled", false)) {
        continue;
      }

      List<String> lore = config.getStringList(particle + ".lore");
      lore.replaceAll(line -> new MessageBuilder(line).build());

      ParticleItem particleItem = new ParticleItem();
      particleItem.setItemStack(new ItemBuilder(XMaterial.matchXMaterial(config
          .getString(particle + ".material-name", "bedrock").toUpperCase(Locale.ENGLISH)).orElse(XMaterial.PAPER).parseItem())
          .name(new MessageBuilder(config.getString(particle + ".displayname")).build())
          .lore(lore)
          .build());
      particleItem.setPermission(config.getString(particle + ".permission", ""));
      particleItem.setEffect(particle);
      registeredParticles.add(particleItem);
      i++;
    }

    plugin.getDebugger().debug("Registered in total " + i + " particles!");
  }

  private void updateParticlesFile() {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "particles");
    for(String particle : VersionUtils.PARTICLE_VALUES) {
      if(!config.isSet(particle)) {
        config.set(particle + ".displayname", "&6" + particle);
        config.set(particle + ".lore", Arrays.asList("&7Click to activate", "&7on your location"));
        config.set(particle + ".material-name", Material.PAPER.name());
        config.set(particle + ".permission", "buildbattle.particles");
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
    PaginatedFastInv gui = new PaginatedFastInv(54, new MessageBuilder("MENU_OPTION_CONTENT_PARTICLE_INVENTORY").asKey().build());

    new InventoryScheme()
        .mask(" 1111111 ")
        .mask(" 1111111 ")
        .mask(" 1111111 ")
        .mask(" 1111111 ")
        .bindPagination('1').apply(gui);


    gui.previousPageItem(45, p -> new ItemBuilder(XMaterial.ARROW.parseItem()).name("<- " + p + "/" + gui.lastPage()).build());
    gui.nextPageItem(53, p -> new ItemBuilder(XMaterial.ARROW.parseItem()).name(p + "/" + gui.lastPage() + " ->").build());

    gui.setItem(52, new ItemBuilder(XMaterial.BARRIER.parseItem()).name("X").build(),
        e -> e.getWhoClicked().closeInventory());

    gui.setItem(49, new ItemBuilder(new ItemStack(Material.REDSTONE_BLOCK))
        .name(new MessageBuilder("MENU_OPTION_CONTENT_PARTICLE_ITEM_REMOVE_NAME").asKey().build())
        .lore(Collections.singletonList(new MessageBuilder("MENU_OPTION_CONTENT_PARTICLE_ITEM_REMOVE_LORE").asKey().build()))
        .build(), event -> {
      HumanEntity humanEntity = event.getWhoClicked();

      if(!(humanEntity instanceof Player))
        return;

      Player player = (Player) humanEntity;
      BaseArena arena = plugin.getArenaRegistry().getArena(player);
      if(arena == null) {
        return;
      }
      event.setCancelled(false);
      player.closeInventory();
      ParticleRemoveMenu.openMenu(player, arena.getPlotManager().getPlot(player));
    });

    plugin.getOptionsRegistry().addGoBackItem(gui, 46);

    addParticles(gui);

    setParticles(gui);
  }

  private void addParticles(PaginatedFastInv gui) {
    for(ParticleItem item : registeredParticles) {
      gui.addContent(item.getItemStack(), event -> {
        HumanEntity humanEntity = event.getWhoClicked();

        if(!(humanEntity instanceof Player))
          return;

        Player player = (Player) humanEntity;
        BaseArena arena = plugin.getArenaRegistry().getArena(player);
        if(arena == null) {
          return;
        }
        if(!player.hasPermission(item.getPermission())) {
          new MessageBuilder("IN_GAME_MESSAGES_PLOT_PERMISSION_PARTICLE").asKey().player(player).sendPlayer();
          return;
        }
        Plot plot = arena.getPlotManager().getPlot(player);
        if(plot == null) {
          return;
        }
        if(plot.getParticles().size() >= plugin.getConfig().getInt("Particle.Max-Amount", 25)) {
          new MessageBuilder("IN_GAME_MESSAGES_PLOT_LIMIT_PARTICLES").asKey().player(player).sendPlayer();
          return;
        }
        plot.getParticles().put(player.getLocation(), item.getEffect());
        plugin.getUserManager().getUser(player).adjustStatistic("PARTICLES_USED", 1);
        new MessageBuilder("MENU_OPTION_CONTENT_PARTICLE_ADDED").asKey().player(player).sendPlayer();
      });
    }
  }

  public PaginatedFastInv getParticles() {
    return particles;
  }

  public void setParticles(PaginatedFastInv particles) {
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
