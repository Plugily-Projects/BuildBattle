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

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.plajer.buildbattle3.ConfigPreferences;
import pl.plajer.buildbattle3.arena.Arena;
import pl.plajer.buildbattle3.handlers.ChatManager;
import pl.plajer.buildbattle3.utils.Glow;
import pl.plajer.buildbattle3.utils.ItemBuilder;
import pl.plajer.buildbattle3.utils.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 07.07.2018
 */
public class VoteMenu {

    private Inventory inventory;
    private VotePoll votePoll;
    private Arena arena;

    public VoteMenu(Arena arena) {
        this.arena = arena;
        this.inventory = Bukkit.createInventory(null, 9 * 5, ChatManager.colorMessage("Menus.Theme-Voting.Inventory-Name"));
    }

    public void setItem(ItemStack itemStack, int pos) {
        inventory.setItem(pos, itemStack);
    }

    public void insertThemes() {
        List<String> themesTotal = ConfigPreferences.getThemes();
        //random themes order
        Collections.shuffle(themesTotal);
        List<String> randomThemes = new ArrayList<>();
        if(themesTotal.size() <= 5) {
            randomThemes.addAll(themesTotal);
        } else {
            Iterator<String> itr = themesTotal.iterator();
            int i = 0;
            while(itr.hasNext()) {
                if(i == 5) break;
                randomThemes.add(itr.next());
                itr.remove();
                i++;
            }
        }
        this.inventory = Bukkit.createInventory(null, 9 * (randomThemes.size() > 5 ? 5 : randomThemes.size()), ChatManager.colorMessage("Menus.Theme-Voting.Inventory-Name"));
        for(int i = 0; i < randomThemes.size(); i++) {
            setItem(new ItemBuilder(new ItemStack(Material.SIGN))
                    .name(ChatManager.colorMessage("Menus.Theme-Voting.Theme-Item-Name").replace("%theme%", randomThemes.get(i)))
                    .lore(ChatManager.colorMessage("Menus.Theme-Voting.Theme-Item-Lore").replace("%theme%", randomThemes.get(i))
                            //todo timer time for theme voting
                            .replace("%percent%", String.valueOf("0.0")).replace("%time-left%", String.valueOf(arena.getTimer())).split(";"))
                    .build(), i * 9);
            setItem(new ItemBuilder(new ItemStack(Material.IRON_FENCE)).build(), (i * 9) + 1);
            //super votes not supported yet
            for(int j = 0; j < 7; j++) {
                setItem(new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14)).build(), (i * 9) + 1 + j + 1);
            }
        }
        votePoll = new VotePoll(arena, randomThemes);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public VotePoll getVotePoll() {
        return votePoll;
    }

    public void updateInventory(Player player) {
        int totalVotes = votePoll.getPlayerVote().size();
        int i = 0;
        for(String theme : votePoll.getVotedThemes().keySet()) {
            double percent;
            if(votePoll.getVotedThemes().get(theme) == Double.NaN || votePoll.getVotedThemes().get(theme) == 0) {
                percent = 0.0;
            } else {
                percent = ((double) votePoll.getVotedThemes().get(theme) / (double) totalVotes) * 100;
            }
            ItemStack stack = new ItemBuilder(new ItemStack(Material.SIGN))
                    .name(ChatManager.colorMessage("Menus.Theme-Voting.Theme-Item-Name").replace("%theme%", theme))
                    .lore(ChatManager.colorMessage("Menus.Theme-Voting.Theme-Item-Lore").replace("%theme%", theme)
                            //todo timer time for theme voting
                            .replace("%percent%", String.valueOf(Util.round(percent, 2))).replace("%time-left%", String.valueOf(arena.getTimer())).split(";"))
                    .build();
            if(votePoll.getPlayerVote().containsKey(player) && votePoll.getPlayerVote().get(player).equals(theme)) {
                ItemMeta meta = stack.getItemMeta();
                meta.addEnchant(new Glow(150), 1, true);
                stack.setItemMeta(meta);
            }
            setItem(stack, i * 9);
            for(int j = 0; j < 7; j++) {
                setItem(new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14)).build(), (i * 9) + 1 + j + 1);
            }
            if(votePoll.getVotedThemes().get(theme) > 0) {
                double vote = 0;
                for(int j = 0; j < 7; j++) {
                    if(vote > percent) {
                        break;
                    }
                    setItem(new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5)).build(), (i * 9) + 1 + j + 1);
                    vote += 14.3;
                }
            }
            i++;
        }
        player.openInventory(inventory);
    }

}
