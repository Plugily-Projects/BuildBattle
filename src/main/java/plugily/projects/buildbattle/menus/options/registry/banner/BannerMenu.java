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

package plugily.projects.buildbattle.menus.options.registry.banner;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion;
import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion.Version;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.utils.Utils;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author Plajer
 * <p>
 * Created at 16.07.2019
 */
public class BannerMenu {

  private static Main plugin;
  private final Map<PatternStage, Gui> guiStages = new EnumMap<>(PatternStage.class);
  private final Banner banner;
  private final Player player;

  public BannerMenu(Player player) {
    this(player, new Banner());
  }

  public BannerMenu(Player player, Banner banner) {
    this.player = player;
    this.banner = banner;
    prepareBaseStageGui();
    prepareLayerStageGui();
    prepareLayerColorStageGui();
  }

  public static void init(Main plugin) {
    BannerMenu.plugin = plugin;
  }

  @SuppressWarnings("deprecation")
  private void prepareBaseStageGui() {
    Gui gui = new Gui(plugin, 6, plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Banner-Creator.Inventories.Color-Choose"));
    OutlinePane pane = new OutlinePane(1, 1, 7, 3);
    for(DyeColor color : DyeColor.values()) {
      ItemStack item;
      if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_12_R1)) {
        item = XMaterial.WHITE_BANNER.parseItem();
        BannerMeta meta = (BannerMeta) item.getItemMeta();
        if(Version.isCurrentEqualOrLower(Version.v1_12_R1)) {
          meta.setBaseColor(color);
        } else {
          ((org.bukkit.block.Banner) item).setBaseColor(color);
        }
        item.setItemMeta(meta);
      } else {
        String banner = color.toString().toUpperCase() + "_BANNER";
        item = XMaterial.matchXMaterial(banner).get().parseItem();
      }
      pane.addItem(new GuiItem(item, e -> {
        e.setCancelled(true);
        banner.setBaseColor(color);
        new BannerMenu(player, banner).openInventory(PatternStage.LAYER);
      }));
    }
    gui.addPane(pane);
    addCreatorItem(gui);
    addGoBackItem(gui);
    guiStages.put(PatternStage.BASE, gui);
  }

  private void prepareLayerStageGui() {
    Gui gui = new Gui(plugin, 6, plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Banner-Creator.Inventories.Add-Layer"));
    OutlinePane pane = new OutlinePane(0, 0, 9, 5);
    gui.addPane(pane);
    for(PatternType pattern : PatternType.values()) {
      ItemStack item = banner.buildBanner();
      BannerMeta meta = (BannerMeta) item.getItemMeta();
      DyeColor color = banner.getColor() == DyeColor.BLACK ? DyeColor.WHITE : DyeColor.BLACK;
      meta.addPattern(new Pattern(color, pattern));
      item.setItemMeta(meta);
      pane.addItem(new GuiItem(item, e -> {
        e.setCancelled(true);
        banner.addPattern(new BannerPattern(color, pattern));
        new BannerMenu(player, banner).openInventory(PatternStage.LAYER_COLOR);
      }));
    }
    addCreatorItem(gui);
    addGoBackItem(gui);
    guiStages.put(PatternStage.LAYER, gui);
  }

  private void prepareLayerColorStageGui() {
    Gui gui = new Gui(plugin, 6, plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Banner-Creator.Inventories.Add-Layer-Color"));
    OutlinePane pane = new OutlinePane(1, 1, 7, 3);
    gui.addPane(pane);
    for(DyeColor color : DyeColor.values()) {
      ItemStack item = banner.buildBanner();
      BannerMeta meta = (BannerMeta) item.getItemMeta();
      Pattern pattern = new Pattern(color, banner.getLastPattern().getPatternType());
      meta.addPattern(pattern);
      item.setItemMeta(meta);
      pane.addItem(new GuiItem(item, e -> {
        e.setCancelled(true);
        banner.replaceLastPattern(new BannerPattern(color, banner.getLastPattern().getPatternType()));
        new BannerMenu(player, banner).openInventory(PatternStage.LAYER);
      }));
    }
    addCreatorItem(gui);
    addGoBackItem(gui);
    guiStages.put(PatternStage.LAYER_COLOR, gui);
  }

  private void addCreatorItem(Gui gui) {
    StaticPane bannerPane = new StaticPane(4, 5, 2, 1);
    gui.addPane(bannerPane);

    bannerPane.addItem(new GuiItem(new ItemBuilder(banner.buildBanner())
        .name(plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Banner-Creator.Create-Banner-Item.Name"))
        .lore(plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Banner-Creator.Create-Banner-Item.Lore"))
        .build(), e -> {
      e.setCancelled(true);
      e.getWhoClicked().closeInventory();
      player.getInventory().addItem(banner.buildBanner());
    }), 0, 0);
  }

  private void addGoBackItem(Gui gui) {
    StaticPane bannerPane = new StaticPane(2, 5, 2, 1);
    gui.addPane(bannerPane);

    bannerPane.addItem(new GuiItem(Utils.getGoBackItem(), e -> {
      e.setCancelled(true);
      e.getWhoClicked().closeInventory();
      player.openInventory(plugin.getOptionsRegistry().formatInventory());
    }), 0, 0);
  }

  public void openInventory(PatternStage stage) {
    guiStages.get(stage).show(player);
  }

  public enum PatternStage {
    BASE, LAYER, LAYER_COLOR
  }

}
