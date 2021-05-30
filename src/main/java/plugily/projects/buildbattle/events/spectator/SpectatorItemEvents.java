
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

import org.bukkit.ChatColor;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pl.plajerlair.commonsbox.minecraft.compat.VersionUtils;
import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import pl.plajerlair.commonsbox.minecraft.misc.stuff.ComplementAccessor;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.handlers.ChatManager;
import plugily.projects.buildbattle.handlers.items.SpecialItemsManager;
import plugily.projects.buildbattle.utils.Utils;

import java.util.List;

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
  public void onSpectatorItemClick(PlayerInteractEvent e) {
    if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() != Action.PHYSICAL) {
      if(ArenaRegistry.getArena(e.getPlayer()) == null) {
        return;
      }
      ItemStack stack = VersionUtils.getItemInHand(e.getPlayer());
      if(!stack.hasItemMeta() || !stack.getItemMeta().hasDisplayName()) {
        return;
      }
      String key = plugin.getSpecialItemsRegistry().getRelatedSpecialItem(stack).getName();
      if(key == null) {
        return;
      }
      if(key.equals(SpecialItemsManager.SpecialItems.SPECTATOR_OPTIONS.getName())) {
        e.setCancelled(true);
        spectatorSettingsMenu.openSpectatorSettingsMenu(e.getPlayer());
      } else if(key.equals(SpecialItemsManager.SpecialItems.PLAYERS_LIST.getName())) {
        e.setCancelled(true);
        openSpectatorMenu(e.getPlayer().getWorld(), e.getPlayer());
      }
    }
  }

  private void openSpectatorMenu(World world, Player p) {
    Inventory inventory = plugin.getServer().createInventory(null, Utils.serializeInt(ArenaRegistry.getArena(p).getPlayers().size()),
      chatManager.colorMessage("In-Game.Spectator.Spectator-Menu-Name"));
    List<Player> players = ArenaRegistry.getArena(p).getPlayers();

    for(Player player : world.getPlayers()) {
      if(players.contains(player) && !plugin.getUserManager().getUser(player).isSpectator()) {
        ItemStack skull = XMaterial.PLAYER_HEAD.parseItem();
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta = VersionUtils.setPlayerHead(player, meta);
        ComplementAccessor.getComplement().setDisplayName(meta, player.getName());
        VersionUtils.setDurability(skull, (short) SkullType.PLAYER.ordinal());
        skull.setItemMeta(meta);
        inventory.addItem(skull);
      }
    }
    p.openInventory(inventory);
  }

  @EventHandler
  public void onSpectatorInventoryClick(InventoryClickEvent e) {
    Player p = (Player) e.getWhoClicked();
    BaseArena arena = ArenaRegistry.getArena(p);
    if(arena == null || e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()
      || !e.getCurrentItem().getItemMeta().hasDisplayName() || !e.getCurrentItem().getItemMeta().hasLore()) {
      return;
    }
    if(!e.getView().getTitle().equalsIgnoreCase(chatManager.colorMessage("In-Game.Spectator.Spectator-Menu-Name"))) {
      return;
    }
    e.setCancelled(true);
    ItemMeta meta = e.getCurrentItem().getItemMeta();
    for(Player player : arena.getPlayers()) {
      if(player.getName().equalsIgnoreCase(meta.getDisplayName()) || ChatColor.stripColor(meta.getDisplayName()).contains(player.getName())) {
        p.sendMessage(chatManager.formatMessage(arena, chatManager.colorMessage("Commands.Admin-Commands.Teleported-To-Player"), player));
        p.teleport(player);
        p.closeInventory();
        return;
      }
    }
    p.sendMessage(chatManager.colorMessage("Commands.Player-Not-Found"));
  }

}
