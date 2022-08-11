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
import plugily.projects.buildbattle.arena.BaseArena;
import plugily.projects.buildbattle.commands.arguments.ArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;

/**
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 */
public class RemovePlotArgument {

  public RemovePlotArgument(ArgumentsRegistry registry) {
    registry.mapArgument("buildbattleadmin", new LabeledCommandArgument("removeplot", "buildbattle.admin.removeplot", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/bba removeplot &6<arena> <plot ID>", "/bba removeplot <arena> <plot ID>",
            "&7Removes game plot of the arena (only in arenas.yml!)\n&6Permission: &7buildbattle.admin.removeplot")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length < 3) {
          new MessageBuilder("COMMANDS_TYPE_ARENA_NAME").asKey().send(sender);
          new MessageBuilder("COMMANDS_WRONG_USAGE").asKey().value("/bba removeplot <arena> <plot ID>").send(sender);
          return;
        }

        BaseArena arena = (BaseArena) registry.getPlugin().getArenaRegistry().getArena(args[1]);
        if(arena == null) {
          new MessageBuilder("COMMANDS_NO_ARENA_LIKE_THAT").asKey().send(sender);
          return;
        }

        String plot = args[2];
        FileConfiguration config = ConfigUtils.getConfig(registry.getPlugin(), "arenas");

        if(config.contains("instances." + arena.getId() + ".plots." + plot)) {
          config.set("instances." + arena.getId() + ".plots." + plot, null);
          ConfigUtils.saveConfig(registry.getPlugin(), config, "arenas");
          new MessageBuilder("&aPlot with ID &e" + plot + "&a removed from arena &e" + arena.getId()).send(sender);
        } else {
          new MessageBuilder("&cPlot with that ID doesn't exist!").send(sender);
        }
      }
    });
  }

}
