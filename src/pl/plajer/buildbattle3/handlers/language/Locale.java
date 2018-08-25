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

package pl.plajer.buildbattle3.handlers.language;

import java.util.Arrays;
import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 11.07.2018
 */
public enum Locale {

  CHINESE_SIMPLIFIED("简体中文", "zh_Hans", "POEditor contributors (Haoting)", Arrays.asList("简体中文", "中文", "chinese", "zh")),
  ENGLISH("English", "en_GB", "Plajer", Arrays.asList("default", "english", "en")),
  ESTONIAN("Eesti", "et_EE", "POEditor contributors (kaimokene)", Arrays.asList("estonian", "eesti", "et")),
  FRENCH("Français", "fr_FR", "POEditor contributors", Arrays.asList("french", "francais", "français", "fr")),
  GERMAN("Deutsch", "de_DE", "Tigerkatze and POEditor contributors", Arrays.asList("deutsch", "german", "de")),
  HUNGARIAN("Magyar", "hu_HU", "POEditor contributors (montlikadani)", Arrays.asList("hungarian", "magyar", "hu")),
  INDONESIAN("Indonesia", "id_ID", "POEditor contributors", Arrays.asList("indonesian", "indonesia", "id")),
  KOREAN("한국의", "ko_KR", "POEditor contributors", Arrays.asList("korean", "한국의", "kr")),
  POLISH("Polski", "pl_PL", "Plajer", Arrays.asList("polish", "polski", "pl")),
  RUSSIAN("Pусский", "ru_RU", "POEditor contributors (Mrake)", Arrays.asList("russian", "russkiy", "pусский", "ru")),
  SPANISH("Español", "es_ES", "POEditor contributors", Arrays.asList("spanish", "espanol", "español", "es")),
  TURKISH("Türk", "tr_TR", "POEditor contributors", Arrays.asList("turkish", "turk", "türk", "tr")),
  VIETNAMESE("Việt", "vn_VN", "POEditor contributors (HStreamGamer)", Arrays.asList("vietnamese", "viet", "việt", "vn"));

  String formattedName;
  String prefix;
  String author;
  List<String> aliases;

  Locale(String formattedName, String prefix, String author, List<String> aliases) {
    this.prefix = prefix;
    this.formattedName = formattedName;
    this.author = author;
    this.aliases = aliases;
  }

  public String getFormattedName() {
    return formattedName;
  }

  public String getAuthor() {
    return author;
  }

  public String getPrefix() {
    return prefix;
  }

  public List<String> getAliases() {
    return aliases;
  }
}
