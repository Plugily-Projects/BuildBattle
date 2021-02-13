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

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.ArenaManager;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.commands.arguments.ArgumentsRegistry;
import plugily.projects.buildbattle.commands.arguments.data.CommandArgument;
import plugily.projects.buildbattle.commands.arguments.data.LabelData;
import plugily.projects.buildbattle.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.buildbattle.handlers.language.LanguageManager;
import plugily.projects.buildbattle.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * @author 2Wild4You
 * <p>
 * Created at 09.08.2020
 */
public class ArenaSelectorArgument implements Listener {

  private final Map<Integer, BaseArena> arenas = new HashMap<>();

  public ArenaSelectorArgument(ArgumentsRegistry registry) {
    registry.getPlugin().getServer().getPluginManager().registerEvents(this, registry.getPlugin());
    registry.mapArgument("buildbattle", new LabeledCommandArgument("arenas", "buildbattle.arenas", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/bb arenas", "/bb arenas", "&7Select an arena\n&6Permission: &7buildbattle.arenas")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if(ArenaRegistry.getArenas().size() == 0) {
          player.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.No-Arena-Like-That"));
          return;
        }
        Inventory inventory = Bukkit.createInventory(player, Utils.serializeInt(ArenaRegistry.getArenas().size()), registry.getPlugin().getChatManager().colorMessage("Arena-Selector.Inv-Title"));

        int slot = 0;
        arenas.clear();

        for(BaseArena arena : ArenaRegistry.getArenas()) {
          arenas.put(slot, arena);
          ItemStack itemStack;
          switch(arena.getArenaState()) {
            case WAITING_FOR_PLAYERS:
              itemStack = XMaterial.LIME_CONCRETE.parseItem();
              break;
            case STARTING:
              itemStack = XMaterial.YELLOW_CONCRETE.parseItem();
              break;
            default:
              itemStack = XMaterial.RED_CONCRETE.parseItem();
              break;
          }
          ItemMeta itemMeta = itemStack.getItemMeta();
          itemMeta.setDisplayName(formatItem(LanguageManager.getLanguageMessage("Arena-Selector.Item.Name"), arena, registry.getPlugin()));

          ArrayList<String> lore = new ArrayList<>();
          for(String string : LanguageManager.getLanguageList("Arena-Selector.Item.Lore")) {
            lore.add(formatItem(string, arena, registry.getPlugin()));
          }

          itemMeta.setLore(lore);
          itemStack.setItemMeta(itemMeta);
          inventory.addItem(itemStack);
          slot++;
        }
        player.openInventory(inventory);
      }
    });

  }

  private String formatItem(String string, BaseArena arena, Main plugin) {
    String formatted = string;
    formatted = StringUtils.replace(formatted, "%mapname%", arena.getMapName());
    if(arena.getPlayers().size() >= arena.getMaximumPlayers()) {
      formatted = StringUtils.replace(formatted, "%state%", plugin.getChatManager().colorMessage("Signs.Game-States.Full-Game"));
    } else {
      formatted = StringUtils.replace(formatted, "%state%", plugin.getSignManager().getGameStateToString().get(arena.getArenaState()));
    }
    formatted = StringUtils.replace(formatted, "%type%", String.valueOf(arena.getArenaType()));
    formatted = StringUtils.replace(formatted, "%playersize%", String.valueOf(arena.getPlayers().size()));
    formatted = StringUtils.replace(formatted, "%maxplayers%", String.valueOf(arena.getMaximumPlayers()));
    formatted = plugin.getChatManager().colorRawMessage(formatted);
    return formatted;
  }

  private static final Main plugin = JavaPlugin.getPlugin(Main.class);

  @EventHandler
  public void onArenaSelectorMenuClick(InventoryClickEvent e) {
    if(!e.getView().getTitle().equals(plugin.getChatManager().colorMessage("Arena-Selector.Inv-Title"))) {
      return;
    }
    if(e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()) {
      return;
    }
    Player player = (Player) e.getWhoClicked();
    player.closeInventory();


    BaseArena arena = arenas.get(e.getRawSlot());
    if(arena != null) {
      ArenaManager.joinAttempt(player, arena);
    } else {
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Commands.No-Arena-Like-That"));
    }
  }

}
