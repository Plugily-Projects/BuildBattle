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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import pl.plajerlair.commonsbox.minecraft.compat.VersionUtils;
import pl.plajerlair.commonsbox.minecraft.compat.events.api.CBPlayerInteractEvent;
import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import pl.plajerlair.commonsbox.minecraft.item.ItemUtils;
import pl.plajerlair.commonsbox.minecraft.misc.stuff.ComplementAccessor;
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
public class SpectatorEvents implements Listener {

  private final ChatManager chatManager;
  private final Main plugin;
  private final SpectatorSettingsMenu spectatorSettingsMenu;

  public SpectatorEvents(Main plugin) {
    this.plugin = plugin;
    chatManager = plugin.getChatManager();
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    spectatorSettingsMenu = new SpectatorSettingsMenu(plugin, chatManager.colorMessage("In-Game.Spectator.Settings-Menu.Inventory-Name"),
        chatManager.colorMessage("In-Game.Spectator.Settings-Menu.Speed-Name"));
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onDamage(EntityDamageEvent event) {
    if(!(event.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getEntity();
    if(!plugin.getUserManager().getUser(player).isSpectator()) {
      return;
    }

    event.setCancelled(true);

    if(player.getLocation().getY() < 1) {
      BaseArena arena = ArenaRegistry.getArena(player);
      if(arena != null) {
        player.teleport(arena.getPlotManager().getPlots().get(0).getTeleportLocation());
      }
    }
  }

  @EventHandler
  public void onEntityDamage(EntityDamageByEntityEvent event) {
    if(!(event.getDamager() instanceof Player)) return;

    Player player = (Player) event.getDamager();
    if(plugin.getUserManager().getUser(player).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onSpectatorInteract(CBPlayerInteractEvent e) {
    if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() != Action.PHYSICAL) {

      if(!plugin.getUserManager().getUser(e.getPlayer()).isSpectator()) {
        return;
      }

      BaseArena arena = ArenaRegistry.getArena(e.getPlayer());
      if(arena == null) {
        return;
      }

      ItemStack stack = VersionUtils.getItemInHand(e.getPlayer());
      if(!ItemUtils.isItemStackNamed(stack)) {
        return;
      }

      e.setCancelled(true);

      if(ComplementAccessor.getComplement().getDisplayName(stack.getItemMeta()).equalsIgnoreCase(chatManager.colorMessage("In-Game.Spectator.Spectator-Item-Name"))) {
        openSpectatorMenu(e.getPlayer().getWorld(), e.getPlayer(), arena);
      } else if(ComplementAccessor.getComplement().getDisplayName(stack.getItemMeta()).equalsIgnoreCase(chatManager.colorMessage("In-Game.Spectator.Settings-Menu.Item-Name"))) {
        spectatorSettingsMenu.openSpectatorSettingsMenu(e.getPlayer());
      }
    }
  }

  private void openSpectatorMenu(World world, Player p, BaseArena arena) {
    Inventory inventory = ComplementAccessor.getComplement().createInventory(null, Utils.serializeInt(arena.getPlayers().size()),
        chatManager.colorMessage("In-Game.Spectator.Spectator-Menu-Name"));
    List<Player> players = arena.getPlayers();

    UserManager userManager = plugin.getUserManager();
    for(Player player : world.getPlayers()) {
      if(!players.contains(player) || userManager.getUser(player).isSpectator()) continue;

      ItemStack skull = XMaterial.PLAYER_HEAD.parseItem();

      SkullMeta meta = (SkullMeta) skull.getItemMeta();
      meta = VersionUtils.setPlayerHead(player, meta);
      ComplementAccessor.getComplement().setDisplayName(meta, player.getName());
      skull.setItemMeta(meta);
      inventory.addItem(skull);
    }
    p.openInventory(inventory);
  }

  @EventHandler
  public void onSpectatorInventoryClick(InventoryClickEvent e) {
    Player p = (Player) e.getWhoClicked();
    BaseArena arena = ArenaRegistry.getArena(p);
    if(arena == null) {
      return;
    }

    if(!plugin.getUserManager().getUser(p).isSpectator()) {
      return;
    }

    e.setCancelled(true);

    if(!ItemUtils.isItemStackNamed(e.getCurrentItem())
        || !ComplementAccessor.getComplement().getTitle(e.getView()).equalsIgnoreCase(chatManager.colorMessage("In-Game.Spectator.Spectator-Menu-Name"))) {
      return;
    }

    String displayName = ComplementAccessor.getComplement().getDisplayName(e.getCurrentItem().getItemMeta());
    Player target = Bukkit.getPlayer(displayName);
    if(target == null) target = Bukkit.getPlayer(ChatColor.stripColor(displayName));

    if(target == null) {
      p.sendMessage(chatManager.colorMessage("Commands.Player-Not-Found"));
      return;
    }

    p.sendMessage(chatManager.formatMessage(arena, chatManager.colorMessage("Commands.Admin-Commands.Teleported-To-Player"), target));
    p.teleport(target);
    p.closeInventory();
  }
}
