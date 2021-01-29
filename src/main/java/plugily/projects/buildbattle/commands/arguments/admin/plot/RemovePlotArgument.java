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
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.commands.arguments.ArgumentsRegistry;
import plugily.projects.buildbattle.commands.arguments.data.CommandArgument;
import plugily.projects.buildbattle.commands.arguments.data.LabelData;
import plugily.projects.buildbattle.commands.arguments.data.LabeledCommandArgument;

/**
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 * @deprecated should remove arena directly from game arena too
 */
@Deprecated
public class RemovePlotArgument {

  public RemovePlotArgument(ArgumentsRegistry registry) {
    registry.mapArgument("buildbattleadmin", new LabeledCommandArgument("removeplot", "buildbattle.admin.removeplot", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/bba removeplot &6<arena> <plot ID>", "/bba removeplot <arena> <plot ID>",
            "&7Removes game plot of the arena (only in arenas.yml!)\n&6Permission: &7buildbattle.admin.removeplot")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if(args.length < 2) {
          //todo translatable
          player.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorRawMessage("&cPlease type arena name and plot ID!"));
          return;
        }
        BaseArena arena = ArenaRegistry.getArena(args[1]);
        if(arena == null) {
          player.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("Commands.No-Arena-Like-That"));
          return;
        }
        FileConfiguration config = ConfigUtils.getConfig(registry.getPlugin(), "arenas");
        if(config.contains("instances." + arena.getID() + ".plots." + args[2])) {
          config.set("instances." + arena.getID() + ".plots." + args[2], null);
          ConfigUtils.saveConfig(registry.getPlugin(), config, "arenas");
          player.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorRawMessage("&aPlot with ID &e" + args[2] + "&a removed from arena &e" + arena.getID()));
        } else {
          player.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorRawMessage("&cPlot with that ID doesn't exist!"));
        }
      }
    });
  }

}
