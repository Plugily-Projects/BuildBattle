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

package plugily.projects.buildbattle.menus.options.registry.banner;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import plugily.projects.buildbattle.menus.options.MenuOption;
import plugily.projects.buildbattle.menus.options.OptionsRegistry;

/**
 * @author Plajer
 * <p>
 * Created at 16.07.2019
 */
public class BannerCreatorOption {

  public BannerCreatorOption(OptionsRegistry registry) {
    registry.registerOption(new MenuOption(16, "BANNER_CREATOR", new ItemBuilder(XMaterial.WHITE_BANNER.parseItem())
        .name(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Banner-Creator.Item-Name"))
        .lore(registry.getPlugin().getChatManager().colorMessage("Menus.Option-Menu.Items.Banner-Creator.Item-Lore"))
        .build()) {
      @Override
      public void onClick(InventoryClickEvent e) {
        e.getWhoClicked().closeInventory();
        new BannerMenu((Player) e.getWhoClicked()).openInventory(BannerMenu.PatternStage.BASE);
      }
    });
  }

}
