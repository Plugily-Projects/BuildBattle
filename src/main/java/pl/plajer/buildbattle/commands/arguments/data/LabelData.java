/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Tigerpanzer_02, Plajer and contributors
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

package pl.plajer.buildbattle.commands.arguments.data;

import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle.Main;

/**
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 */
public class LabelData {

  private Main plugin = JavaPlugin.getPlugin(Main.class);
  private String text;
  private String command;
  private String description;

  public LabelData(String text, String command, String description) {
    this.text = plugin.getChatManager().colorRawMessage(text);
    this.command = command;
    this.description = plugin.getChatManager().colorRawMessage(description);
  }

  public String getText() {
    return text;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

}
