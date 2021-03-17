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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.commands.arguments.ArgumentsRegistry;
import plugily.projects.buildbattle.commands.arguments.data.CommandArgument;
import plugily.projects.buildbattle.commands.arguments.data.LabelData;
import plugily.projects.buildbattle.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.buildbattle.utils.CuboidSelector;

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
        Player player = (Player) sender;
        if(args.length == 1) {
          //todo translatable
          player.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorRawMessage("&cPlease type arena name!"));
          return;
        }
        BaseArena arena = ArenaRegistry.getArena(args[1]);
        if(arena == null) {
          player.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("Commands.No-Arena-Like-That"));
          return;
        }
        CuboidSelector.Selection selection = registry.getPlugin().getCuboidSelector().getSelection(player);
        if(selection == null || selection.getFirstPos() == null || selection.getSecondPos() == null) {
          player.sendMessage(registry.getPlugin().getChatManager().colorRawMessage(registry.getPlugin().getChatManager().getPrefix() + "&cPlease select both corners before adding a plot!"));
          return;
        }
        FileConfiguration config = ConfigUtils.getConfig(registry.getPlugin(), "arenas");
        int id = 0;
        if(config.getConfigurationSection("instances." + arena.getID() + ".plots") != null) {
          id = config.getConfigurationSection("instances." + arena.getID() + ".plots").getKeys(false).size() + 1;
        }
        LocationSerializer.saveLoc(registry.getPlugin(), config, "arenas", "instances." + arena.getID() + ".plots." + id + ".minpoint", selection.getFirstPos());
        LocationSerializer.saveLoc(registry.getPlugin(), config, "arenas", "instances." + arena.getID() + ".plots." + id + ".maxpoint", selection.getSecondPos());
        player.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorRawMessage("&aPlot with ID &e" + id + "&a added to arena instance &e" + arena.getID()));
        registry.getPlugin().getCuboidSelector().removeSelection(player);
      }
    });
  }

}
