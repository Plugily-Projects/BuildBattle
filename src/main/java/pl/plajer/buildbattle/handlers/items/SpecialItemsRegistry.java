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

package pl.plajer.buildbattle.handlers.items;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import pl.plajer.buildbattle.Main;
import pl.plajerlair.core.debug.Debugger;
import pl.plajerlair.core.debug.LogLevel;
import pl.plajerlair.core.utils.ConfigUtils;
import pl.plajerlair.core.utils.ItemBuilder;
import pl.plajerlair.core.utils.XMaterial;

/**
 * Created by Tom on 5/02/2016.
 */
public class SpecialItemsRegistry {

  private static Set<SpecialItem> specialItems = new HashSet<>();
  private Main plugin;

  public SpecialItemsRegistry(Main plugin) {
    this.plugin = plugin;
    updateSpecialItemsConfig();
    registerItems();
  }

  public void addItem(SpecialItem item) {
    specialItems.add(item);
  }

  @Nullable
  public SpecialItem getSpecialItem(String name) {
    for (SpecialItem item : specialItems) {
      if (item.getName().equals(name)) {
        return item;
      }
    }
    return null;
  }

  @Nullable
  public SpecialItem getRelatedSpecialItem(ItemStack itemStack) {
    for (SpecialItem item : specialItems) {
      if (item.getItemStack().isSimilar(itemStack)) {
        return item;
      }
    }
    return null;
  }

  private void updateSpecialItemsConfig() {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "lobbyitems");
    for (String key : config.getKeys(false)) {
      if (config.isSet(key + ".material-name")) {
        continue;
      }
      config.set(key + ".material-name", Material.PAPER.toString());
      Debugger.debug(LogLevel.WARN, "Found outdated item in lobbyitems.yml! We've converted it to the newest version!");
    }
    ConfigUtils.saveConfig(plugin, config, "lobbyitems");
  }

  private void registerItems() {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "lobbyitems");
    for (String key : config.getKeys(false)) {
      addItem(new SpecialItem(key, new ItemBuilder(XMaterial
          .fromString(config.getString(key + ".material-name", "BEDROCK").toUpperCase()).parseItem())
          .name(plugin.getChatManager().colorRawMessage(config.getString(key + ".displayname")))
          .lore(config.getStringList(key + ".lore").stream().map((lore) -> lore = plugin.getChatManager().colorRawMessage(lore))
              .collect(Collectors.toList()))
          .build(), config.getInt(key + ".slot")));
    }
  }
}
