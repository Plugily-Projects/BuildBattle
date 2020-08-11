/*
 * MurderMystery - Find the murderer, kill him and survive!
 * Copyright (C) 2020  Plugily Projects - maintained by Tigerpanzer_02, 2Wild4You and contributors
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
 */

package plugily.projects.buildbattle.events.spectator;

import org.bukkit.*;
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
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.handlers.ChatManager;
import plugily.projects.buildbattle.user.UserManager;
import plugily.projects.buildbattle.utils.Utils;

import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 05.08.2018
 */
public class SpectatorItemEvents implements Listener {

  private final Main plugin;
  private final boolean usesPaperSpigot = Bukkit.getServer().getVersion().contains("Paper");

  public SpectatorItemEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onSpectatorItemClick(PlayerInteractEvent e) {
    if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() != Action.PHYSICAL) {
      BaseArena arena = ArenaRegistry.getArena(e.getPlayer());
      if (arena == null) {
        return;
      }
      ItemStack stack = e.getPlayer().getInventory().getItemInMainHand();
      if (!stack.hasItemMeta() || !stack.getItemMeta().hasDisplayName()) {
        return;
      }
      if (stack.getItemMeta().getDisplayName().equalsIgnoreCase(plugin.getChatManager().colorMessage("In-Game.Spectator.Spectator-Item-Name"))) {
        e.setCancelled(true);
        openSpectatorMenu(e.getPlayer().getWorld(), e.getPlayer(), arena);
      }
    }
  }

  private void openSpectatorMenu(World world, Player p, BaseArena arena) {
    Inventory inventory = plugin.getServer().createInventory(null, Utils.serializeInt(arena.getPlayers().size()),
            plugin.getChatManager().colorMessage("In-Game.Spectator.Spectator-Menu-Name"));
    List<Player> players = arena.getPlayers();

    UserManager userManager = plugin.getUserManager();
    for (Player player : world.getPlayers()) {
      if (!players.contains(player) || userManager.getUser(player).isSpectator()) continue;

      ItemStack skull;
      if (plugin.is1_12_R1()) {
        skull = new ItemStack(Material.getMaterial("SKULL_ITEM"), 1, (short) 3);
        skull.setDurability((short) SkullType.PLAYER.ordinal());
      } else {
        skull = XMaterial.PLAYER_HEAD.parseItem();
      }
      SkullMeta meta = (SkullMeta) skull.getItemMeta();
      if (usesPaperSpigot && player.getPlayerProfile().hasTextures()) {
        meta.setPlayerProfile(player.getPlayerProfile());
      } else {
        meta.setOwningPlayer(player);
      }
      meta.setDisplayName(player.getName());
      skull.setItemMeta(meta);
      inventory.addItem(skull);
    }
    p.openInventory(inventory);
  }

  @EventHandler
  public void onSpectatorInventoryClick(InventoryClickEvent e) {
    Player p = (Player) e.getWhoClicked();
    BaseArena arena = ArenaRegistry.getArena(p);
    if (arena == null) {
      return;
    }
    if (e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()
            || !e.getCurrentItem().getItemMeta().hasDisplayName() || !e.getCurrentItem().getItemMeta().hasLore()) {
      return;
    }
    ChatManager chatManager = plugin.getChatManager();
    if (!e.getView().getTitle().equalsIgnoreCase(chatManager.colorMessage("In-Game.Spectator.Spectator-Menu-Name"))) {
      return;
    }
    e.setCancelled(true);
    ItemMeta meta = e.getCurrentItem().getItemMeta();
    for (Player player : arena.getPlayers()) {
      if (player.getName().equalsIgnoreCase(meta.getDisplayName()) || ChatColor.stripColor(meta.getDisplayName()).contains(player.getName())) {
        p.sendMessage(chatManager.formatMessage(arena, chatManager.colorMessage("Commands.Admin-Commands.Teleported-To-Player"), player));
        p.teleport(player);
        p.closeInventory();
        return;
      }
    }
    p.sendMessage(chatManager.colorMessage("Commands.Admin-Commands.Player-Not-Found"));
  }

}
