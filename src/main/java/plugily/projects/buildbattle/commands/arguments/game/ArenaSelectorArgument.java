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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import plugily.projects.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.commonsbox.minecraft.misc.stuff.ComplementAccessor;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ArenaSelectorArgument {

  // Temporary solution until there is no better inventory creator
  private final Map<UUID, Map<Integer, BaseArena>> arenas = new HashMap<>();

  public ArenaSelectorArgument(ArgumentsRegistry registry) {
    registry.getPlugin().getServer().getPluginManager().registerEvents(new Listener() {
     @EventHandler
     public void onArenaSelectorMenuClick(InventoryClickEvent e) {
       if(!ComplementAccessor.getComplement().getTitle(e.getView()).equals(registry.getPlugin().getChatManager().colorMessage("Arena-Selector.Inv-Title"))) {
         return;
       }

       ItemStack currentItem = e.getCurrentItem();
       if(currentItem == null || !currentItem.hasItemMeta()) {
         return;
       }

       Player player = (Player) e.getWhoClicked();
       player.closeInventory();

       Map<Integer, BaseArena> map = arenas.remove(player.getUniqueId());
       if(map != null) {
         BaseArena arena = map.get(e.getRawSlot());

         if (arena != null) {
           ArenaManager.joinAttempt(player, arena);
           return;
         }
       }

       player.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("Commands.No-Arena-Like-That"));
     }

     @EventHandler
     public void onSelectorCloseEvent(InventoryCloseEvent event) {
       arenas.remove(event.getPlayer().getUniqueId());
     }
    }, registry.getPlugin());

    registry.mapArgument("buildbattle", new LabeledCommandArgument("arenas", "buildbattle.arenas", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/bb arenas", "/bb arenas", "&7Select an arena\n&6Permission: &7buildbattle.arenas")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        int arenaSize = ArenaRegistry.getArenas().size();

        if(arenaSize == 0) {
          sender.sendMessage(registry.getPlugin().getChatManager().colorMessage("Commands.No-Arena-Like-That"));
          return;
        }

        Player player = (Player) sender;
        Inventory inventory = ComplementAccessor.getComplement().createInventory(player, Utils.serializeInt(arenaSize), registry.getPlugin().getChatManager().colorMessage("Arena-Selector.Inv-Title"));

        int slot = 0;

        for(BaseArena arena : ArenaRegistry.getArenas()) {
          ItemStack itemStack = XMaterial.matchXMaterial(registry.getPlugin().getConfig().getString("Arena-Selector.State-Item." + arena.getArenaState().getFormattedName(), "YELLOW_WOOL").toUpperCase()).orElse(XMaterial.YELLOW_WOOL).parseItem();

          if(itemStack == null)
            continue;

          Map<Integer, BaseArena> map = new HashMap<>();
          map.put(slot, arena);

          arenas.put(player.getUniqueId(), map);

          ItemMeta itemMeta = itemStack.getItemMeta();
          if(itemMeta != null) {
            ComplementAccessor.getComplement().setDisplayName(itemMeta, formatItem(LanguageManager.getLanguageMessage("Arena-Selector.Item.Name"), arena, registry.getPlugin()));

            List<String> lore = LanguageManager.getLanguageList("Arena-Selector.Item.Lore");
            for(int b = 0; b < lore.size(); b++) {
              lore.set(b, formatItem(lore.get(b), arena, registry.getPlugin()));
            }

            ComplementAccessor.getComplement().setLore(itemMeta, lore);
            itemStack.setItemMeta(itemMeta);
          }

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

    int maxPlayers = arena.getMaximumPlayers();
    int players = arena.getPlayers().size();

    if(players >= maxPlayers) {
      formatted = StringUtils.replace(formatted, "%state%", plugin.getChatManager().colorMessage("Signs.Game-States.Full-Game"));
    } else {
      formatted = StringUtils.replace(formatted, "%state%", arena.getArenaState().getPlaceholder());
    }

    formatted = StringUtils.replace(formatted, "%type%", arena.getArenaType().getPrefix());
    formatted = StringUtils.replace(formatted, "%playersize%", Integer.toString(players));
    formatted = StringUtils.replace(formatted, "%maxplayers%", Integer.toString(maxPlayers));
    formatted = plugin.getChatManager().colorRawMessage(formatted);
    return formatted;
  }
}
