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

package plugily.projects.buildbattle.commands.arguments.admin.plot;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.commands.arguments.ArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.dimensional.CuboidSelector;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;

/**
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 */
public class AddPlotArgument {

  public AddPlotArgument(ArgumentsRegistry registry) {
    registry.mapArgument("buildbattleadmin", new LabeledCommandArgument("addplot", "buildbattle.admin.addplot", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/bba addplot &6<arena>", "/bba addplot <arena>",
            "&7Add new game plot to the arena\n&6Permission: &7buildbattle.admin.addplot")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
          new MessageBuilder("COMMANDS_TYPE_ARENA_NAME").asKey().send(sender);
          return;
        }
        BaseArena arena = (BaseArena) registry.getPlugin().getArenaRegistry().getArena(args[1]);
        if(arena == null) {
          new MessageBuilder("COMMANDS_NO_ARENA_LIKE_THAT").asKey().send(sender);
          return;
        }
        Player player = (Player) sender;
        CuboidSelector.Selection selection = registry.getPlugin().getCuboidSelector().getSelection(player);
        if(selection == null || selection.getFirstPos() == null || selection.getSecondPos() == null) {
          new MessageBuilder("&cPlease select both corners before adding a plot!").send(sender);
          return;
        }
        FileConfiguration config = ConfigUtils.getConfig(registry.getPlugin(), "arenas");
        int id = 0;
        ConfigurationSection section = config.getConfigurationSection("instances." + arena.getId() + ".plots");
        if(section != null) {
          id = section.getKeys(false).size() + 1;
        }
        LocationSerializer.saveLoc(registry.getPlugin(), config, "arenas", "instances." + arena.getId() + ".plots." + id + ".minpoint", selection.getFirstPos());
        LocationSerializer.saveLoc(registry.getPlugin(), config, "arenas", "instances." + arena.getId() + ".plots." + id + ".maxpoint", selection.getSecondPos());
        new MessageBuilder("&aPlot with ID &e" + id + "&a added to arena instance &e" + arena.getId()).send(sender);
        registry.getPlugin().getCuboidSelector().removeSelection(player);
      }
    });
  }

}
