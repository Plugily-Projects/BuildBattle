/*
 *
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2022 Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

package plugily.projects.buildbattle.handlers.menu.registry.playerheads;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import plugily.projects.buildbattle.handlers.menu.MenuOption;
import plugily.projects.buildbattle.handlers.menu.OptionsRegistry;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemUtils;
import plugily.projects.minigamesbox.inventory.common.item.SimpleClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class PlayerHeadsOption {

    public PlayerHeadsOption(OptionsRegistry registry) {
        registry.registerOption(new MenuOption(10, "PLAYER_HEADS", new ItemBuilder(ItemUtils.PLAYER_HEAD_ITEM.clone())
                .name(new MessageBuilder("MENU_OPTION_CONTENT_HEADS_ITEM_NAME").asKey().build())
                .lore(new MessageBuilder("MENU_OPTION_CONTENT_HEADS_ITEM_LORE").asKey().build())
                .build(), new MessageBuilder("MENU_OPTION_CONTENT_HEADS_INVENTORY").asKey().build()) {

            @Override
            public void onClick(InventoryClickEvent event) {
                HumanEntity humanEntity = event.getWhoClicked();
                humanEntity.closeInventory();
                if (!(humanEntity instanceof Player)) {
                    return;
                }
                Player player = (Player) humanEntity;
                if (registry.getPlugin().getConfigPreferences().getOption("HEAD_MENU_CUSTOM")) {
                    player.performCommand(registry.getPlugin().getConfig().getString("Head-Menu.Command", "heads"));
                    return;
                }
                NormalFastInv gui = new NormalFastInv(registry.getPlugin().getBukkitHelper().serializeInt(registry.getPlayerHeadsRegistry().getCategories().size() + 1), new MessageBuilder("MENU_OPTION_CONTENT_HEADS_INVENTORY").asKey().build());

                for (HeadsCategory headsCategory : registry.getPlayerHeadsRegistry().getCategories().keySet()) {
                    gui.addItem(new SimpleClickableItem(headsCategory.getItemStack(), clickEvent -> {
                        player.closeInventory();
                        if (!player.hasPermission(headsCategory.getPermission())) {
                            new MessageBuilder("IN_GAME_MESSAGES_PLOT_PERMISSION_HEAD").asKey().player(player).sendPlayer();
                            return;
                        }
                        headsCategory.getGui().open(player);
                    }));
                }

                registry.getPlugin().getOptionsRegistry().addGoBackItem(gui, gui.getInventory().getSize() - 1);
                gui.open(humanEntity);
            }
        });
    }

}
