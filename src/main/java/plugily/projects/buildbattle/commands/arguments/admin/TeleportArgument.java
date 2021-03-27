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

package plugily.projects.buildbattle.commands.arguments.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.commands.arguments.ArgumentsRegistry;
import plugily.projects.buildbattle.commands.arguments.data.CommandArgument;
import plugily.projects.buildbattle.commands.arguments.data.LabelData;
import plugily.projects.buildbattle.commands.arguments.data.LabeledCommandArgument;

import java.util.Arrays;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 14.03.2021
 */
public class TeleportArgument {

  public TeleportArgument(ArgumentsRegistry registry) {
    registry.mapArgument("buildbattleadmin", new LabeledCommandArgument("tp", "buildbattle.admin.teleport", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/bba tp &6<arena> <location type>", "/bba tp <arena> <location>",
            "&7Teleport you to provided arena location\n&7Valid locations:\n&7• LOBBY - lobby location\n&7• START - starting location\n"
                + "&7• END - ending location\n&6Permission: &7buildbattle.admin.teleport")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("Commands.Type-Arena-Name"));
          return;
        }
        if(args.length == 2) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + ChatColor.RED + "Please type location type: " + Arrays.toString(LocationType.values()).replace("[", "").replace("]", ""));
          return;
        }
        Player player = (Player) sender;
        LocationType type;
        try {
          type = LocationType.valueOf(args[2].toUpperCase());
        } catch(IllegalArgumentException e) {
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + ChatColor.RED + "Please type location type: " + Arrays.toString(LocationType.values()).replace("[", "").replace("]", ""));
          return;
        }
        for(BaseArena arena : ArenaRegistry.getArenas()) {
          if(arena.getID().equalsIgnoreCase(args[1])) {
            teleport(player, arena, type);
            break;
          }
        }
      }
    });
  }

  private void teleport(Player player, BaseArena arena, LocationType locationType) {
    switch(locationType) {
      case LOBBY:
        org.bukkit.Location lobby = arena.getLobbyLocation();
        if(lobby == null) {
          player.sendMessage(ChatColor.RED + "Lobby location isn't set for this arena!");
          return;
        }
        player.teleport(lobby);
        player.sendMessage(ChatColor.GRAY + "Teleported to LOBBY location from arena " + arena.getID());
        break;
      case START:
        if(arena.getLobbyLocation() == null) {
          player.sendMessage(ChatColor.RED + "Start location isn't set for this arena!");
          return;
        }
        player.teleport(arena.getPlotManager().getPlots().get(0).getTeleportLocation());
        player.sendMessage(ChatColor.GRAY + "Teleported to START location from arena " + arena.getID());
        break;
      case END:
        if(arena.getLobbyLocation() == null) {
          player.sendMessage(ChatColor.RED + "End location isn't set for this arena!");
          return;
        }
        arena.teleportToEndLocation(player);
        player.sendMessage(ChatColor.GRAY + "Teleported to END location from arena " + arena.getID());
        break;
      default:
        break; //o.o
    }
  }

  public enum LocationType {
    LOBBY, END, START //todo PLOT
  }

}
