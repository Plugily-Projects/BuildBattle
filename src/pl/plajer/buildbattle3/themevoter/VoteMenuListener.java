/*
 * BuildBattle 3 - Ultimate building competition minigame
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
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

package pl.plajer.buildbattle3.themevoter;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.arena.Arena;
import pl.plajer.buildbattle3.arena.ArenaRegistry;
import pl.plajer.buildbattle3.handlers.ChatManager;

/**
 * @author Plajer
 * <p>
 * Created at 07.07.2018
 */
public class VoteMenuListener implements Listener {

    public VoteMenuListener(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getInventory() == null || e.getInventory().getName() == null) return;
        if(e.getCurrentItem() == null) return;
        if(e.getInventory().getName().equals(ChatManager.colorMessage("Menus.Theme-Voting.Inventory-Name"))) {
            e.setCancelled(true);
            Arena arena = ArenaRegistry.getArena((Player) e.getWhoClicked());
            if(arena == null || e.getCurrentItem().getType() != Material.SIGN) {
                return;
            }
            String displayName = e.getCurrentItem().getItemMeta().getDisplayName();
            displayName = ChatColor.stripColor(displayName);
            boolean success = arena.getVotePoll().addVote((Player) e.getWhoClicked(), displayName);
            if(!success) {
                e.getWhoClicked().sendMessage(ChatManager.colorMessage("Menus.Theme-Voting.Already-Voted"));
            } else {
                e.getWhoClicked().sendMessage(ChatManager.colorMessage("Menus.Theme-Voting.Voted-Successfully"));
            }
        }
    }

}
