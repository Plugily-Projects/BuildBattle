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

package plugily.projects.buildbattle.handlers.menu.registry.playerheads;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.handlers.menu.OptionsRegistry;
import plugily.projects.buildbattle.handlers.misc.HeadDatabaseManager;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemUtils;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XEnchantment;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.inventory.utils.fastinv.InventoryScheme;
import plugily.projects.minigamesbox.inventory.utils.fastinv.PaginatedFastInv;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class PlayerHeadsRegistry {

  private final Main plugin;
  private final Map<HeadsCategory, PaginatedFastInv> categories = new HashMap<>();

  public PlayerHeadsRegistry(OptionsRegistry registry) {
    this.plugin = registry.getPlugin();
    registerCategories();
  }

  private void registerCategories() {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "heads/mainmenu");
    for(String str : config.getKeys(false)) {
      if(!config.getBoolean(str + ".enabled", true)) {
        continue;
      }
      if(config.getBoolean(str + ".database", false)) {
        String categoryName = config.getString(str + ".config", "fail");
        if(categoryName.equalsIgnoreCase("search")) {
          HeadsCategory category = new HeadsCategory(str);
          category.setItemStack(new ItemBuilder(ItemUtils.getSkull(config.getString(str + ".texture")))
              .name(new MessageBuilder(config.getString(str + ".displayname")).value(categoryName.toUpperCase()).build())
              .lore(config.getStringList(str + ".lore").stream()
                  .map(lore -> new MessageBuilder(lore).value(categoryName.toUpperCase()).build()).collect(Collectors.toList()))
              .glowEffect().build());
          category.setPermission(config.getString(str + ".permission"));
          category.setSearch(true);
          categories.put(category, null);
        } else {
          CompletableFuture.supplyAsync(() -> plugin.getHeadDatabaseManager().getDatabase(categoryName)).thenAccept(download -> {
            if(download != HeadDatabaseManager.DownloadStatus.FAIL) {
              useDatabaseHeads(config, str);
            }
          });
        }
        continue;
      }
      HeadsCategory category = new HeadsCategory(str);

      category.setItemStack(new ItemBuilder(ItemUtils.getSkull(config.getString(str + ".texture")))
          .name(new MessageBuilder(config.getString(str + ".displayname")).build())
          .lore(config.getStringList(str + ".lore").stream()
              .map(lore -> new MessageBuilder(lore).build()).collect(Collectors.toList()))
          .build());
      category.setPermission(config.getString(str + ".permission"));

      Set<ItemStack> playerHeads = new HashSet<>();
      FileConfiguration categoryConfig = ConfigUtils.getConfig(plugin, "heads/menus/" +
          config.getString(str + ".config"));
      for(String path : categoryConfig.getKeys(false)) {
        if(!categoryConfig.getBoolean(path + ".enabled", true)) {
          continue;
        }

        ItemStack stack = ItemUtils.getSkull(categoryConfig.getString(path + ".texture"));
        ItemMeta im = stack.getItemMeta();

        ComplementAccessor.getComplement().setDisplayName(im, new MessageBuilder(categoryConfig.getString(path + ".displayname")).build());
        ComplementAccessor.getComplement().setLore(im, categoryConfig.getStringList(path + ".lore").stream()
            .map(lore -> new MessageBuilder(lore).build()).collect(Collectors.toList()));
        stack.setItemMeta(im);
        playerHeads.add(stack);
      }
      createPaginatedInventory(str, config, playerHeads, category);
    }
  }

  private void createPaginatedInventory(String str, FileConfiguration config, Collection<ItemStack> playerHeads, HeadsCategory category) {
    PaginatedFastInv gui = new PaginatedFastInv(54, new MessageBuilder(config.getString(str + ".menuname")).value(config.getString(str + ".config")).build());
    new InventoryScheme()
        .mask("111111111")
        .mask("111111111")
        .mask("111111111")
        .mask("111111111")
        .mask("111111111")
        .bindPagination('1').apply(gui);


    gui.previousPageItem(45, p -> new ItemBuilder(XMaterial.ARROW.parseItem()).name("&7<- &6" + p + "&7/&6" + gui.lastPage()).colorizeItem().build());
    gui.addPageChangeHandler(openedPage -> {
      gui.setItem(49, new ItemBuilder(XMaterial.BARRIER.parseItem()).name("&7X &6" + openedPage + " &7X").colorizeItem().build(), e -> e.getWhoClicked().closeInventory());
    });
    gui.nextPageItem(53, p -> new ItemBuilder(XMaterial.ARROW.parseItem()).name("&6 " + p + "&7/&6" + gui.lastPage() + " &7->").colorizeItem().build());

    plugin.getOptionsRegistry().addGoBackItem(gui, 46);

    if(playerHeads.size() > 200) {
      List<ItemStack> heads = new ArrayList<>(playerHeads);
      Collections.shuffle(heads);
      int start = plugin.getRandom().nextInt(heads.size() - 225);
      playerHeads = heads.subList(start, start + 225);
    }

    for(ItemStack playerHead : playerHeads) {
      gui.addContent(playerHead, clickEvent -> clickEvent.getWhoClicked().getInventory().addItem(playerHead.clone()));
    }

    plugin.getOptionsRegistry().addGoBackItem(gui, gui.getInventory().getSize() - 1);
    category.setGui(gui);
    categories.put(category, gui);
  }

  public void useDatabaseHeads(FileConfiguration config, String str) {
    HeadsCategory category = new HeadsCategory(str);
    String categoryName = config.getString(str + ".config", "fail");

    category.setItemStack(new ItemBuilder(ItemUtils.getSkull(config.getString(str + ".texture")))
        .name(new MessageBuilder(config.getString(str + ".displayname")).value(categoryName.toUpperCase()).build())
        .lore(config.getStringList(str + ".lore").stream()
            .map(lore -> new MessageBuilder(lore).value(categoryName.toUpperCase()).build()).collect(Collectors.toList()))
        .build());
    category.setPermission(config.getString(str + ".permission"));


    CompletableFuture.supplyAsync(() -> loadHeadsFromYML(categoryName)).thenAccept(playerHeads -> createPaginatedInventory(str, config, playerHeads.values(), category));
  }

  private final Map<String, Map<String, ItemStack>> headsDatabase = new HashMap<>();

  public Map<String, ItemStack> loadHeadsFromYML(String name) {
    // Should do this in async thread to do not cause dead for the main thread
    long start = System.currentTimeMillis();
    FileConfiguration categoryConfig = ConfigUtils.getConfig(plugin, "heads/database/" + name);
    Map<String, String> heads = new HashMap<>();
    for(String path : categoryConfig.getKeys(false)) {
      heads.put(path, categoryConfig.getString(path));
    }
    plugin.getDebugger().debug("[System] [Plugin] Head file loading " + name + " finished took ms" + (System.currentTimeMillis() - start));

    long secondStart = System.currentTimeMillis();
    Map<String, ItemStack> playerHeads = new HashMap<>();
    for(Map.Entry<String, String> entry : heads.entrySet()) {
      if(entry.getKey().toLowerCase().contains("(dup)")) {
        continue;
      }
      ItemStack stack = ItemUtils.getSkull(entry.getValue());
      ItemMeta im = stack.getItemMeta();

      ComplementAccessor.getComplement().setDisplayName(im, new MessageBuilder(entry.getKey()).build());
      ComplementAccessor.getComplement().setLore(im, Collections.singletonList(new MessageBuilder("MENU_OPTION_CONTENT_HEADS_DATABASE_LORE").asKey().value(entry.getKey()).build()));
      stack.setItemMeta(im);
      playerHeads.put(entry.getKey(), stack);
    }
    headsDatabase.put(name, playerHeads);
    plugin.getDebugger().debug("[System] [Plugin] Head textures loading " + name + " finished took ms" + (System.currentTimeMillis() - secondStart));
    return playerHeads;
  }


  public Map<HeadsCategory, PaginatedFastInv> getCategories() {
    return categories;
  }

  public Map<String, Map<String, ItemStack>> getHeadsDatabase() {
    return headsDatabase;
  }

}
