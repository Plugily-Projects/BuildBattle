
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

package plugily.projects.buildbattle.events.spectator;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import pl.plajerlair.commonsbox.minecraft.compat.VersionUtils;
import pl.plajerlair.commonsbox.minecraft.compat.events.api.CBPlayerInteractEvent;
import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import pl.plajerlair.commonsbox.minecraft.item.ItemUtils;
import pl.plajerlair.commonsbox.minecraft.misc.stuff.ComplementAccessor;
import pl.plajerlair.commonsbox.number.NumberUtils;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.ArenaManager;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.handlers.ChatManager;
import plugily.projects.buildbattle.handlers.items.SpecialItemsManager;
import plugily.projects.buildbattle.utils.Utils;

import java.util.Collections;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 30.05.2021
 */
public class SpectatorItemEvents implements Listener {

  private final Main plugin;
  private final ChatManager chatManager;
  private final SpectatorSettingsMenu spectatorSettingsMenu;

  public SpectatorItemEvents(Main plugin) {
    this.plugin = plugin;
    chatManager = plugin.getChatManager();
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    spectatorSettingsMenu = new SpectatorSettingsMenu(plugin, chatManager.colorMessage("In-Game.Spectator.Settings-Menu.Inventory-Name"),
        chatManager.colorMessage("In-Game.Spectator.Settings-Menu.Speed-Name"));
  }

  @EventHandler
  public void onSpectatorItemClick(CBPlayerInteractEvent e) {
    if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.PHYSICAL) {
      return;
    }
    BaseArena arena = ArenaRegistry.getArena(e.getPlayer());
    ItemStack stack = VersionUtils.getItemInHand(e.getPlayer());
    if(arena == null || !ItemUtils.isItemStackNamed(stack)) {
      return;
    }
    if(plugin.getSpecialItemsManager().getRelatedSpecialItem(stack).getName().equals(SpecialItemsManager.SpecialItems.PLAYERS_LIST.getName())) {
      e.setCancelled(true);
      openSpectatorMenu(arena, e.getPlayer());
    } else if(plugin.getSpecialItemsManager().getRelatedSpecialItem(stack).getName().equals(SpecialItemsManager.SpecialItems.SPECTATOR_OPTIONS.getName())) {
      e.setCancelled(true);
      spectatorSettingsMenu.openSpectatorSettingsMenu(e.getPlayer());
    } else if(plugin.getSpecialItemsManager().getRelatedSpecialItem(stack).getName().equals(SpecialItemsManager.SpecialItems.SPECTATOR_LEAVE_ITEM.getName())) {
      e.setCancelled(true);
      ArenaManager.leaveAttempt(e.getPlayer(), arena);
    }
  }

  private void openSpectatorMenu(BaseArena arena, Player player) {
    int rows = Utils.serializeInt(arena.getPlayers().size()) / 9;
    ChestGui gui = new ChestGui(rows, plugin.getChatManager().colorMessage("In-Game.Spectator.Spectator-Menu-Name"));
    OutlinePane pane = new OutlinePane(9, rows);
    ItemStack skull = XMaterial.PLAYER_HEAD.parseItem();

    for(Player arenaPlayer : arena.getPlayers()) {
      if(plugin.getUserManager().getUser(arenaPlayer).isSpectator()) {
        continue;
      }
      ItemStack cloneSkull = skull.clone();
      SkullMeta meta = VersionUtils.setPlayerHead(arenaPlayer, (SkullMeta) cloneSkull.getItemMeta());
      ComplementAccessor.getComplement().setDisplayName(meta, arenaPlayer.getName());
      cloneSkull.setItemMeta(meta);
      pane.addItem(new GuiItem(cloneSkull, e -> {
        e.setCancelled(true);
        e.getWhoClicked().sendMessage(plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage("Commands.Admin-Commands.Teleported-To-Player"), arenaPlayer));
        e.getWhoClicked().closeInventory();
        e.getWhoClicked().teleport(arenaPlayer);
      }));
    }
    gui.addPane(pane);
    gui.update();
    gui.show(player);
  }

}
