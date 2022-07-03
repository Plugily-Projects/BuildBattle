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

package plugily.projects.buildbattle.commands.arguments.admin.arena;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import plugily.projects.buildbattle.commands.arguments.ArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;

/**
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 */
public class AddNpcArgument {

  public AddNpcArgument(ArgumentsRegistry registry) {
    registry.mapArgument("buildbattleadmin", new LabeledCommandArgument("addnpc", "buildbattle.admin.addnpc", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/bba addnpc", "/bba addnpc",
            "&7Deletes specified arena\n&6Permission: &7buildbattle.admin.addnpc")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(registry.getPlugin().getServer().getPluginManager().isPluginEnabled("Citizens")) {
          NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, new MessageBuilder("IN_GAME_MESSAGES_PLOT_NPC_NAME").asKey().build());
          npc.spawn(((Player) sender).getLocation());
          npc.setProtected(true);
          npc.setName(new MessageBuilder("IN_GAME_MESSAGES_PLOT_NPC_NAME").asKey().build());
          new MessageBuilder("IN_GAME_MESSAGES_PLOT_NPC_CREATED").asKey().send(sender);
        } else {
          new MessageBuilder("IN_GAME_MESSAGES_PLOT_NPC_CITIZENS").asKey().send(sender);
        }
      }
    });
  }

}
