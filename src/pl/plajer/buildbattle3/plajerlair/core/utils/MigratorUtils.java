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

package pl.plajer.buildbattle3.plajerlair.core.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

/**
 * Manage file strings without YamlConfiguration
 * and without losing comments
 */
public class MigratorUtils {

  /**
   * Remove specified line from file
   *
   * @param file         file to use
   * @param lineToRemove line to remove
   */
  public static void removeLineFromFile(File file, String lineToRemove) {
    try {
      List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
      List<String> updatedLines = lines.stream().filter(s -> !s.contains(lineToRemove)).collect(Collectors.toList());
      FileUtils.writeLines(file, updatedLines, false);
    } catch (IOException e) {
      e.printStackTrace();
      Bukkit.getLogger().warning("[PLCore] Something went horribly wrong with migration! Please contact author!");
    }
  }

  /**
   * Insert text after specified string
   *
   * @param file   file to use
   * @param search string to check
   * @param text   text to insert after search string
   */
  public static void insertAfterLine(File file, String search, String text) {
    try {
      int i = 1;
      List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
      for (String line : lines) {
        if (line.contains(search)) {
          lines.add(i, text);
          Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
          break;
        }
        i++;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Adds new lines to file
   *
   * @param file     file to use
   * @param newLines new lines to add
   */
  public static void addNewLines(File file, String newLines) {
    try {
      FileWriter fw = new FileWriter(file.getPath(), true);
      fw.write(newLines);
      fw.close();
    } catch (IOException e) {
      e.printStackTrace();
      Bukkit.getLogger().warning("[PLCore] Something went horribly wrong with migration! Please contact author!");
    }
  }

}
