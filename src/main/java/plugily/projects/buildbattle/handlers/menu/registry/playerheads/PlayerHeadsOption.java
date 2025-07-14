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

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import plugily.projects.buildbattle.handlers.menu.MenuOption;
import plugily.projects.buildbattle.handlers.menu.OptionsRegistry;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.conversation.SimpleConversationBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.inventory.common.item.SimpleClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;
import plugily.projects.minigamesbox.inventory.utils.fastinv.InventoryScheme;
import plugily.projects.minigamesbox.inventory.utils.fastinv.PaginatedFastInv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Plajer
 * <p>
 * Created at 23.12.2018
 */
public class PlayerHeadsOption {

  public PlayerHeadsOption(OptionsRegistry registry) {
    registry.registerOption(new MenuOption(10, "PLAYER_HEADS", new ItemBuilder(XMaterial.PLAYER_HEAD.parseItem())
        .name(new MessageBuilder("MENU_OPTION_CONTENT_HEADS_ITEM_NAME").asKey().build())
        .lore(new MessageBuilder("MENU_OPTION_CONTENT_HEADS_ITEM_LORE").asKey().build())
        .build(), new MessageBuilder("MENU_OPTION_CONTENT_HEADS_INVENTORY").asKey().build()) {

      @Override
      public void onClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        humanEntity.closeInventory();
        if(!(humanEntity instanceof Player)) {
          return;
        }
        Player player = (Player) humanEntity;
        if(registry.getPlugin().getConfigPreferences().getOption("HEAD_MENU_CUSTOM")) {
          player.performCommand(registry.getPlugin().getConfig().getString("Head-Menu.Command", "heads"));
          return;
        }
        NormalFastInv gui = new NormalFastInv(registry.getPlugin().getBukkitHelper().serializeInt(registry.getPlayerHeadsRegistry().getCategories().size() + 1), new MessageBuilder("MENU_OPTION_CONTENT_HEADS_INVENTORY").asKey().build());

        for(HeadsCategory headsCategory : registry.getPlayerHeadsRegistry().getCategories().keySet()) {
          if(headsCategory.isSearch()) {
            gui.setItem(gui.getInventory().getSize() - 2, headsCategory.getItemStack(), clickEvent -> {
              player.closeInventory();
              if(!player.hasPermission(headsCategory.getPermission())) {
                new MessageBuilder("IN_GAME_MESSAGES_PLOT_PERMISSION_HEAD").asKey().player(player).sendPlayer();
                return;
              }
              createChatEvent(event, player, registry);
            });
            continue;
          }

          gui.addItem(headsCategory.getItemStack(), clickEvent -> {
            player.closeInventory();
            if(!player.hasPermission(headsCategory.getPermission())) {
              new MessageBuilder("IN_GAME_MESSAGES_PLOT_PERMISSION_HEAD").asKey().player(player).sendPlayer();
              return;
            }
            headsCategory.getGui().open(player);
          });
        }

        registry.getPlugin().getOptionsRegistry().addGoBackItem(gui, gui.getInventory().getSize() - 1);
        gui.open(player);
      }
    });
  }

  private static void createChatEvent(InventoryClickEvent event, Player player, OptionsRegistry registry) {
    new SimpleConversationBuilder(registry.getPlugin()).withPrompt(new StringPrompt() {
      @Override
      public @NotNull String getPromptText(ConversationContext context) {
        return new MessageBuilder("&ePlease type in chat the name of the head!").prefix().build();
      }

      @Override
      public Prompt acceptInput(ConversationContext context, String input) {
        String name = new MessageBuilder(input, false).build();
        context.getForWhom().sendRawMessage(new MessageBuilder("&e✔ Completed | Got " + name).prefix().build());

        PaginatedFastInv resultGui = new PaginatedFastInv(54, new MessageBuilder("Searching %value%").value(name).build());
        new InventoryScheme()
            .mask("111111111")
            .mask("111111111")
            .mask("111111111")
            .mask("111111111")
            .mask("111111111")
            .bindPagination('1').apply(resultGui);


        resultGui.previousPageItem(45, p -> new ItemBuilder(XMaterial.ARROW.parseItem()).name("&7<- &6" + p + "&7/&6" + resultGui.lastPage()).colorizeItem().build());
        resultGui.addPageChangeHandler(openedPage -> {
          resultGui.setItem(49, new ItemBuilder(XMaterial.BARRIER.parseItem()).name("&7X &6" + openedPage + " &7X").colorizeItem().build(), e -> e.getWhoClicked().closeInventory());
        });
        resultGui.nextPageItem(53, p -> new ItemBuilder(XMaterial.ARROW.parseItem()).name("&6 " + p + "&7/&6" + resultGui.lastPage() + " &7->").colorizeItem().build());

        registry.getPlugin().getOptionsRegistry().addGoBackItem(resultGui, 46);

        List<ItemStack> filteredHeads = registry.getPlayerHeadsRegistry().getHeadsDatabase().values().stream()
            .flatMap(innerMap -> innerMap.entrySet().stream())
            .filter(entry -> entry.getKey().toLowerCase().contains(input.toLowerCase()))
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());

        for(ItemStack playerHead : filteredHeads) {
          resultGui.addContent(playerHead, clickEvent -> clickEvent.getWhoClicked().getInventory().addItem(playerHead.clone()));
        }
        resultGui.open(player);
        return Prompt.END_OF_CONVERSATION;
      }
    }).buildFor((Player) event.getWhoClicked());
  }

}
