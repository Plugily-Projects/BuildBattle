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

package plugily.projects.buildbattle.commands.arguments.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.arena.impl.SoloArena;
import plugily.projects.buildbattle.commands.arguments.ArgumentsRegistry;
import plugily.projects.buildbattle.commands.arguments.data.CommandArgument;
import plugily.projects.buildbattle.commands.arguments.data.LabelData;
import plugily.projects.buildbattle.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.buildbattle.handlers.setup.SetupInventory;

import java.util.ArrayList;

/**
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 */
public class CreateArgument {

  private final ArgumentsRegistry registry;

  public CreateArgument(ArgumentsRegistry registry) {
    this.registry = registry;
    registry.mapArgument("buildbattle", new LabeledCommandArgument("create", "buildbattle.admin.create", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/bb create &6<arena>", "/bb create <arena>",
            "&7Create new arena\n&6Permission: &7buildbattle.admin.create")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
          sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.Type-Arena-Name"));
          return;
        }
        Player player = (Player) sender;
        for(BaseArena arena : ArenaRegistry.getArenas()) {
          if(arena.getID().equalsIgnoreCase(args[1])) {
            player.sendMessage(ChatColor.DARK_RED + "Arena with that ID already exists!");
            player.sendMessage(ChatColor.DARK_RED + "Usage: /bb create <ID>");
            return;
          }
        }
        if(ConfigUtils.getConfig(registry.getPlugin(), "arenas").contains("instances." + args[1])) {
          player.sendMessage(ChatColor.DARK_RED + "Instance/Arena already exists! Use another ID or delete it first!");
        } else {
          createInstanceInConfig(args[1]);
          player.sendMessage(ChatColor.BOLD + "------------------------------------------");
          player.sendMessage(ChatColor.YELLOW + "      Instance " + args[1] + " created!");
          player.sendMessage("");
          player.sendMessage(ChatColor.GREEN + "Edit this arena via " + ChatColor.GOLD + "/bb " + args[1] + " edit" + ChatColor.GREEN + "!");
          player.sendMessage(ChatColor.GOLD + "Don't know where to start? Check out tutorial video:");
          player.sendMessage(ChatColor.GOLD + SetupInventory.VIDEO_LINK);
          player.sendMessage(ChatColor.BOLD + "------------------------------------------- ");
          SetupInventory.sendProTip(player);
        }
      }
    });
  }

  private void createInstanceInConfig(String id) {
    String path = "instances." + id + ".";
    FileConfiguration config = ConfigUtils.getConfig(registry.getPlugin(), "arenas");
    LocationSerializer.saveLoc(registry.getPlugin(), config, "arenas", path + "lobbylocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
    LocationSerializer.saveLoc(registry.getPlugin(), config, "arenas", path + "Endlocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
    config.set(path + "minimumplayers", 2);
    config.set(path + "maximumplayers", 4);
    config.set(path + "mapname", id);
    config.set(path + "signs", new ArrayList<>());
    config.createSection(path + "plots");
    config.set(path + "gametype", "SOLO");
    config.set(path + "isdone", false);
    ConfigUtils.saveConfig(registry.getPlugin(), config, "arenas");

    SoloArena arena = new SoloArena(id, registry.getPlugin());

    arena.setMinimumPlayers(config.getInt(path + "minimumplayers"));
    arena.setMaximumPlayers(config.getInt(path + "maximumplayers"));
    arena.setMapName(config.getString(path + "mapname"));
    arena.setLobbyLocation(LocationSerializer.getLocation(config.getString(path + "lobbylocation")));
    arena.setEndLocation(LocationSerializer.getLocation(config.getString(path + "Endlocation")));
    arena.setArenaType(BaseArena.ArenaType.SOLO);
    arena.setReady(false);
    arena.initPoll();
    ArenaRegistry.registerArena(arena);

    ArenaRegistry.registerArenas();
  }

}
