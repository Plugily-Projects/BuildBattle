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

package plugily.projects.buildbattle.handlers.menu.registry.banner;

import org.bukkit.DyeColor;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.buildbattle.Main;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XPatternType;
import plugily.projects.minigamesbox.inventory.common.item.SimpleClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author Plajer
 * <p>
 * Created at 16.07.2019
 */
public class BannerMenu {

  private final Main plugin = JavaPlugin.getPlugin(Main.class);
  private final Map<PatternStage, NormalFastInv> guiStages = new EnumMap<>(PatternStage.class);
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

  @SuppressWarnings("deprecation")
  private void prepareBaseStageGui() {
    NormalFastInv gui = new NormalFastInv(54, new MessageBuilder("MENU_OPTION_CONTENT_BANNER_INVENTORY_COLOR").asKey().build());

    for(DyeColor color : DyeColor.values()) {
      ItemStack item;

      if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_12)) {
        item = XMaterial.WHITE_BANNER.parseItem();
        BannerMeta meta = (BannerMeta) item.getItemMeta();

        BlockStateMeta bsm = (BlockStateMeta) meta;
        BlockState state = ((BlockStateMeta) meta).getBlockState();
        if (state instanceof org.bukkit.block.Banner) {
          ((org.bukkit.block.Banner) state).setBaseColor(color);
          state.update(true);
        }
        bsm.setBlockState(state);
        item.setItemMeta(meta);
      } else {
        item = XMaterial.matchXMaterial(color.toString() + "_BANNER").orElse(XMaterial.WHITE_BANNER).parseItem();
      }

      gui.addItem(new SimpleClickableItem(item, event -> {
        event.setCancelled(true);
        banner.setBaseColor(color);
        banner.addPattern(new BannerPattern(color, PatternType.BASE));
        new BannerMenu(player, banner).openInventory(PatternStage.LAYER);
      }));
    }

    addCreatorItem(gui);
    plugin.getOptionsRegistry().addGoBackItem(gui, 45);
    guiStages.put(PatternStage.BASE, gui);
    gui.refresh();
  }

  private void prepareLayerStageGui() {
    NormalFastInv gui = new NormalFastInv(54, new MessageBuilder("MENU_OPTION_CONTENT_BANNER_INVENTORY_LAYER").asKey().build());

    for(XPatternType pattern : XPatternType.getValues()) {
      ItemStack item = banner.buildBanner();
      BannerMeta meta = (BannerMeta) item.getItemMeta();
      DyeColor color = banner.getColor() == DyeColor.BLACK ? DyeColor.WHITE : DyeColor.BLACK;

      meta.addPattern(new Pattern(color, pattern.get()));
      item.setItemMeta(meta);

      gui.addItem(new SimpleClickableItem(item, event -> {
        event.setCancelled(true);
        banner.addPattern(new BannerPattern(color, pattern.get()));
        new BannerMenu(player, banner).openInventory(PatternStage.LAYER_COLOR);
      }));
    }

    addCreatorItem(gui);
    plugin.getOptionsRegistry().addGoBackItem(gui, 45);
    guiStages.put(PatternStage.LAYER, gui);
    gui.refresh();
  }

  private void prepareLayerColorStageGui() {
    NormalFastInv gui = new NormalFastInv(54, new MessageBuilder("MENU_OPTION_CONTENT_BANNER_INVENTORY_LAYER_COLOR").asKey().build());

    for(DyeColor color : DyeColor.values()) {
      ItemStack item = banner.buildBanner();
      BannerMeta meta = (BannerMeta) item.getItemMeta();

      meta.addPattern(new Pattern(color, banner.getLastPattern().getPatternType()));
      item.setItemMeta(meta);

      gui.addItem(new SimpleClickableItem(item, event -> {
        event.setCancelled(true);
        banner.replaceLastPattern(new BannerPattern(color, banner.getLastPattern().getPatternType()));
        new BannerMenu(player, banner).openInventory(PatternStage.LAYER);
      }));
    }

    addCreatorItem(gui);
    plugin.getOptionsRegistry().addGoBackItem(gui, 45);
    guiStages.put(PatternStage.LAYER_COLOR, gui);
    gui.refresh();
  }

  private void addCreatorItem(NormalFastInv gui) {
    gui.setItem(49, new SimpleClickableItem(new ItemBuilder(banner.buildBanner())
        .name(new MessageBuilder("MENU_OPTION_CONTENT_BANNER_ITEM_CREATE_NAME").asKey().build())
        .lore(new MessageBuilder("MENU_OPTION_CONTENT_BANNER_ITEM_CREATE_LORE").asKey().build())
        .build(), event -> {
      event.setCancelled(true);
      event.getWhoClicked().closeInventory();
      player.getInventory().addItem(banner.buildBanner());
    }));
  }

  public void openInventory(PatternStage stage) {
    guiStages.get(stage).open(player);
  }

  public enum PatternStage {
    BASE, LAYER, LAYER_COLOR
  }

}
