/*
 * BuildBattle 3 - Ultimate building competition minigame
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
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

package pl.plajer.buildbattle3.plajerlair.core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Class for registering new services
 */
public class ServiceRegistry {

  private static List<JavaPlugin> registeredPlugins = new ArrayList<>();
  private static Map<JavaPlugin, Long> serviceCooldown = new HashMap<>();
  private static LocaleService localeService;

  public static boolean registerService(JavaPlugin plugin) {
    if (registeredPlugins.contains(plugin)) {
      return false;
    }
    registeredPlugins.add(plugin);
    plugin.getLogger().log(Level.INFO, "Hooked with ServiceRegistry! Initialized services properly!");
    new MetricsService(plugin);
    localeService = new LocaleService(plugin);
    return true;
  }

  public static List<JavaPlugin> getRegisteredPlugins() {
    return registeredPlugins;
  }

  public static Map<JavaPlugin, Long> getServiceCooldown() {
    return serviceCooldown;
  }

  public static LocaleService getLocaleService(JavaPlugin plugin) {
    if (!registeredPlugins.contains(plugin)) {
      return null;
    }
    return localeService;
  }
}
