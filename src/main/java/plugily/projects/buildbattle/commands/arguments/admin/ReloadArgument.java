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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.plajerlair.commonsbox.minecraft.serialization.InventorySerializer;
import plugily.projects.buildbattle.ConfigPreferences;
import plugily.projects.buildbattle.arena.ArenaManager;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.commands.arguments.ArgumentsRegistry;
import plugily.projects.buildbattle.commands.arguments.data.CommandArgument;
import plugily.projects.buildbattle.commands.arguments.data.LabelData;
import plugily.projects.buildbattle.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.buildbattle.handlers.language.LanguageManager;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 */
public class ReloadArgument {

  private final Set<CommandSender> confirmations = new HashSet<>();

  public ReloadArgument(ArgumentsRegistry registry) {
    registry.mapArgument("buildbattleadmin", new LabeledCommandArgument("reload", "buildbattle.admin.reload", CommandArgument.ExecutorType.BOTH,
        new LabelData("/bba reload", "/bba reload", "&7Reload all game arenas and configuration files\n&7&lThey will be stopped!\n&6Permission: &7buildbattle.admin.reload")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(!confirmations.contains(sender)) {
          confirmations.add(sender);
          Bukkit.getScheduler().runTaskLater(registry.getPlugin(), () -> confirmations.remove(sender), 20 * 10);
          sender.sendMessage(registry.getPlugin().getChatManager().getPrefix()
              + registry.getPlugin().getChatManager().colorRawMessage("&cAre you sure you want to do this action? Type the command again &6within 10 seconds &cto confirm!"));
          return;
        }
        confirmations.remove(sender);

        registry.getPlugin().reloadConfig();
        LanguageManager.reloadConfig();

        for(BaseArena arena : ArenaRegistry.getArenas()) {
          for(Player player : arena.getPlayers()) {
            arena.doBarAction(BaseArena.BarAction.REMOVE, player);
            arena.teleportToEndLocation(player);
            if(registry.getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
              InventorySerializer.loadInventory(registry.getPlugin(), player);
            } else {
              player.getInventory().clear();
              player.getInventory().setArmorContents(null);
              player.getActivePotionEffects().forEach(pe -> player.removePotionEffect(pe.getType()));
            }
          }
          ArenaManager.stopGame(true, arena);
        }
        registry.getPlugin().getConfigPreferences().loadOptions();
        ArenaRegistry.registerArenas();
        sender.sendMessage(ChatColor.GREEN + "Plugin reloaded!");
      }
    });
  }

}
