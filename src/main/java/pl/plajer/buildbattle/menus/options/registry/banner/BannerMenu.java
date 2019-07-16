/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and contributors
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

package pl.plajer.buildbattle.menus.options.registry.banner;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import pl.plajer.buildbattle.Main;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;

/**
 * @author Plajer
 * <p>
 * Created at 16.07.2019
 */
public class BannerMenu {

  private static Main plugin;
  private Map<PatternStage, Gui> guiStages = new EnumMap<>(PatternStage.class);
  private Banner banner;
  private Player player;

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

  private void prepareBaseStageGui() {
    Gui gui = new Gui(plugin, 6, plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Banner-Creator.Inventories.Color-Choose"));
    OutlinePane pane = new OutlinePane(1, 1, 7, 3);
    for (DyeColor color : DyeColor.values()) {
      ItemStack item = XMaterial.WHITE_BANNER.parseItem();
      BannerMeta meta = (BannerMeta) item.getItemMeta();
      meta.setBaseColor(color);
      item.setItemMeta(meta);
      pane.addItem(new GuiItem(item, e -> {
        e.setCancelled(true);
        banner.setBaseColor(color);
        new BannerMenu(player, banner).openInventory(PatternStage.LAYER);
      }));
    }
    gui.addPane(pane);
    addCreatorItem(gui);
    guiStages.put(PatternStage.BASE, gui);
  }

  private void prepareLayerStageGui() {
    Gui gui = new Gui(plugin, 6, plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Banner-Creator.Inventories.Add-Layer"));
    OutlinePane pane = new OutlinePane(0, 0, 9, 5);
    gui.addPane(pane);
    for (PatternType pattern : PatternType.values()) {
      ItemStack item = banner.buildBanner();
      BannerMeta meta = (BannerMeta) item.getItemMeta();
      DyeColor color;
      if (banner.getColor() == DyeColor.BLACK) {
        color = DyeColor.WHITE;
      } else {
        color = DyeColor.BLACK;
      }
      meta.addPattern(new Pattern(color, pattern));
      item.setItemMeta(meta);
      pane.addItem(new GuiItem(item, e -> {
        e.setCancelled(true);
        banner.addPattern(new BannerPattern(color, pattern));
        new BannerMenu(player, banner).openInventory(PatternStage.LAYER_COLOR);
      }));
    }
    addCreatorItem(gui);
    guiStages.put(PatternStage.LAYER, gui);
  }

  private void prepareLayerColorStageGui() {
    Gui gui = new Gui(plugin, 6, plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Banner-Creator.Inventories.Add-Layer-Color"));
    OutlinePane pane = new OutlinePane(1, 1, 7, 3);
    gui.addPane(pane);
    for (DyeColor color : DyeColor.values()) {
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
    guiStages.put(PatternStage.LAYER_COLOR, gui);
  }

  private void addCreatorItem(Gui gui) {
    StaticPane bannerPane = new StaticPane(4, 5, 2, 1);
    gui.addPane(bannerPane);

    bannerPane.addItem(new GuiItem(new ItemBuilder(banner.buildBanner())
        .name(plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Banner-Creator.Item-Name"))
        .lore(plugin.getChatManager().colorMessage("Menus.Option-Menu.Items.Banner-Creator.Item-Lore"))
        .build(), e -> {
      e.setCancelled(true);
      e.getWhoClicked().closeInventory();
      player.getInventory().addItem(banner.buildBanner());
    }), 0, 0);
  }

  public void openInventory(PatternStage stage) {
    guiStages.get(stage).show(player);
  }

  public enum PatternStage {
    BASE, LAYER, LAYER_COLOR
  }

}
