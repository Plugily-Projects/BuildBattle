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

package plugily.projects.buildbattle.handlers.setup;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import pl.plajerlair.commonsbox.minecraft.misc.stuff.ComplementAccessor;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;
import plugily.projects.buildbattle.ConfigPreferences;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.impl.BaseArena;

import java.util.Random;

/**
 * Created by Tom on 15/06/2015.
 */
public class SetupInventory {

  public static final String VIDEO_LINK = "https://tutorial.plugily.xyz";
  private static final Main plugin = JavaPlugin.getPlugin(Main.class);
  private final Inventory inventory;

  public SetupInventory(BaseArena arena) {
    this.inventory = ComplementAccessor.getComplement().createInventory(null, 9 * 2, "BB Arena: " + arena.getID());

    inventory.setItem(ClickPosition.SET_ENDING.getPosition(), new ItemBuilder(Material.REDSTONE_BLOCK)
        .name(ChatColor.GOLD + "► Set" + ChatColor.RED + " ending " + ChatColor.GOLD + "location")
        .lore(ChatColor.GRAY + "Click to set the ending location")
        .lore(ChatColor.GRAY + "on the place where you are standing.")
        .lore(ChatColor.DARK_GRAY + "(location where players will be teleported")
        .lore(ChatColor.DARK_GRAY + "after the game)")
        .lore(isOptionDoneBool("instances." + arena.getID() + ".Endlocation"))
        .build());
    inventory.setItem(ClickPosition.SET_LOBBY.getPosition(), new ItemBuilder(Material.LAPIS_BLOCK)
        .name(ChatColor.GOLD + "► Set" + ChatColor.WHITE + " lobby " + ChatColor.GOLD + "location")
        .lore(ChatColor.GRAY + "Click to set the lobby location")
        .lore(ChatColor.GRAY + "on the place where you are standing")
        .lore(isOptionDoneBool("instances." + arena.getID() + ".lobbylocation"))
        .build());

    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    int min = config.getInt("instances." + arena.getID() + ".minimumplayers");
    if(min == 0) {
      min = 1;
    }
    inventory.setItem(ClickPosition.SET_MINIMUM_PLAYERS.getPosition(), new ItemBuilder(Material.COAL).amount(min)
        .name(ChatColor.GOLD + "► Set" + ChatColor.DARK_GREEN + " minimum players " + ChatColor.GOLD + "size")
        .lore(ChatColor.GRAY + "LEFT click to decrease")
        .lore(ChatColor.GRAY + "RIGHT click to increase")
        .lore(ChatColor.DARK_GRAY + "(how many players are needed")
        .lore(ChatColor.DARK_GRAY + "for game to start lobby countdown)")
        .lore(ChatColor.RED + "Set it minimum 3 when using TEAM game type!!!")
        .lore(isOptionDone("instances." + arena.getID() + ".minimumplayers"))
        .build());
    int max = config.getInt("instances." + arena.getID() + ".maximumplayers");
    if(max == 0) {
      max = 1;
    }
    inventory.setItem(ClickPosition.SET_MAXIMUM_PLAYERS.getPosition(), new ItemBuilder(Material.REDSTONE).amount(max)
        .name(ChatColor.GOLD + "► Set" + ChatColor.GREEN + " maximum players " + ChatColor.GOLD + "size")
        .lore(ChatColor.GRAY + "LEFT click to decrease")
        .lore(ChatColor.GRAY + "RIGHT click to increase")
        .lore(ChatColor.DARK_GRAY + "(how many players arena can hold)")
        .lore(isOptionDone("instances." + arena.getID() + ".maximumplayers"))
        .build());

    if(!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
      inventory.setItem(ClickPosition.ADD_SIGN.getPosition(), new ItemBuilder(XMaterial.OAK_SIGN.parseMaterial())
          .name(ChatColor.GOLD + "► Add game" + ChatColor.AQUA + " sign")
          .lore(ChatColor.GRAY + "Target a sign and click this.")
          .lore(ChatColor.DARK_GRAY + "(this will set target sign as game sign)")
          .build());
    }

    inventory.setItem(ClickPosition.SET_GAME_TYPE.getPosition(), new ItemBuilder(XMaterial.CLOCK.parseItem())
        .name(ChatColor.GOLD + "► Set game type")
        .lore(ChatColor.GRAY + "Set game mode of build battle arena.")
        .lore(ChatColor.GRAY + "Valid types: SOLO, TEAM")
        .lore(ChatColor.GRAY + "SOLO - 1 player per plot")
        .lore(ChatColor.GRAY + "TEAM - 2 players per plot")
        .lore(ChatColor.GRAY + "Guess The Build - Guess the build of one player")
        .lore(isOptionDone("instances." + arena.getID() + ".gametype"))
        .build());
    inventory.setItem(ClickPosition.SET_MAP_NAME.getPosition(), new ItemBuilder(Material.NAME_TAG)
        .name(ChatColor.GOLD + "► Set" + ChatColor.RED + " map name " + ChatColor.GOLD + "(currently: " + arena.getMapName() + ")")
        .lore(ChatColor.GRAY + "Replace this name tag with named name tag.")
        .lore(ChatColor.GRAY + "It will be set as arena name.")
        .lore(ChatColor.RED + "" + ChatColor.BOLD + "Drop name tag here don't move")
        .lore(ChatColor.RED + "" + ChatColor.BOLD + "it and replace with new!!!")
        .build());
    inventory.setItem(ClickPosition.ADD_GAME_PLOT.getPosition(), new ItemBuilder(Material.BARRIER)
        .name(ChatColor.GOLD + "► Add game plot")
        .lore(ChatColor.GRAY + "Select your plot with our built-in")
        .lore(ChatColor.GRAY + "selector (select minimum and maximum")
        .lore(ChatColor.GRAY + "plot opposite selections with built-in wand)")
        .lore(ChatColor.GRAY + "And click this.")
        .lore(ChatColor.GRAY + "Command for wand is: " + ChatColor.YELLOW + "/bba plotwand")
        .lore(ChatColor.GREEN + "PLEASE SELECT FLOOR TOO!")
        .lore(isOptionDoneList("instances." + arena.getID() + ".plots"))
        .build());
    inventory.setItem(ClickPosition.ADD_FLOOR_CHANGER_NPC.getPosition(), new ItemBuilder(Material.GRASS)
        .name(ChatColor.GOLD + "► Add floor changer NPC")
        .lore(ChatColor.GRAY + "Add floor changer NPC to your plot.")
        .lore(ChatColor.RED + "Requires Citizens plugin!")
        .build());
    inventory.setItem(ClickPosition.REGISTER_ARENA.getPosition(), new ItemBuilder(XMaterial.FIREWORK_ROCKET.parseItem())
        .name(ChatColor.GOLD + "► " + ChatColor.GREEN + "Register arena")
        .lore(ChatColor.GRAY + "Click this when you're done with configuration.")
        .lore(ChatColor.GRAY + "It will validate and register arena.")
        .build());

    inventory.setItem(16, new ItemBuilder(XMaterial.GOLD_INGOT.parseItem())
        .name(ChatColor.GOLD + "► Extras Addon (AD) ◄")
        .lore(ChatColor.GRAY + "Add extras to Build Battle gameplay with paid addon!")
        .lore(ChatColor.GOLD + "Features of this addon:")
        .lore(ChatColor.GOLD + "Achievements, Chat Ranks, Replay Ability")
        .lore(ChatColor.GRAY + "Click to get link for patron program!")
        .build());
    inventory.setItem(17, new ItemBuilder(XMaterial.FILLED_MAP.parseItem())
        .name(ChatColor.GOLD + "► View setup video")
        .lore(ChatColor.GRAY + "Having problems with setup or wanna")
        .lore(ChatColor.GRAY + "know some useful tips? Click to get video link!")
        .build());
  }

  public static String isOptionDone(String path) {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    if(!config.isSet(path)) {
      return ChatColor.GOLD + "" + ChatColor.BOLD + "Done: " + ChatColor.RED + "No";
    }
    return ChatColor.GOLD + "" + ChatColor.BOLD + "Done: " + ChatColor.GREEN + "Yes " + ChatColor.GRAY + "(value: " + config.getString(path) + ")";

  }

  public static void sendProTip(Player p) {
    switch(new Random().nextInt(5 + 1)) {
      case 0:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7We are open source! You can always help us by contributing! Check https://github.com/Plugily-Projects/BuildBattle"));
        break;
      case 1:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Help us translating plugin to your language here: https://translate.plugily.xyz"));
        break;
      case 2:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Download some free maps! Get them here: https://wiki.plugily.xyz/minecraft/buildbattle/free_maps.php"));
        break;
      case 3:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7You can use PlaceholderAPI placeholders from our plugin! Check: https://wiki.plugily.xyz/minecraft/buildbattle/papi_placeholders.php"));
        break;
      case 4:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Suggest new ideas for the plugin or vote on current ones! https://app.feedbacky.net/b/BuildBattle"));
        break;
      case 5:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Console can execute /bba votes <add/set> [amount] (player) command! Add super votes via console!"));
        break;
      default:
        break;
    }
  }

  private String isOptionDoneList(String path) {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    if(!config.isSet(path)) {
      return ChatColor.GOLD + "" + ChatColor.BOLD + "Done: " + ChatColor.RED + "No";
    }
    return ChatColor.GOLD + "" + ChatColor.BOLD + "Done: " + ChatColor.GREEN + "Yes " + ChatColor.GRAY + "(value: " +
        config.getConfigurationSection(path).getKeys(false).size() + ")";
  }

  private String isOptionDoneBool(String path) {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    if(!config.isSet(path)) {
      return ChatColor.GOLD + "" + ChatColor.BOLD + "Done: " + ChatColor.RED + "No";
    }
    if(Bukkit.getServer().getWorlds().get(0).getSpawnLocation().equals(LocationSerializer.getLocation(config.getString(path)))) {
      return ChatColor.GOLD + "" + ChatColor.BOLD + "Done: " + ChatColor.RED + "No";
    }
    return ChatColor.GOLD + "" + ChatColor.BOLD + "Done: " + ChatColor.GREEN + "Yes";
  }

  public void addItem(ItemStack itemStack) {
    inventory.addItem(itemStack);
  }

  public Inventory getInventory() {
    return inventory;
  }

  public void openInventory(Player player) {
    player.openInventory(inventory);
  }

  public enum ClickPosition {
    SET_ENDING(0), SET_LOBBY(1), SET_MINIMUM_PLAYERS(2), SET_MAXIMUM_PLAYERS(3), ADD_SIGN(4), SET_GAME_TYPE(5), SET_MAP_NAME(6),
    ADD_GAME_PLOT(7), ADD_FLOOR_CHANGER_NPC(8), REGISTER_ARENA(9), EXTRAS_AD(16), VIEW_SETUP_VIDEO(17);

    private final int position;

    ClickPosition(int position) {
      this.position = position;
    }

    public static ClickPosition getByPosition(int pos) {
      for(ClickPosition position : ClickPosition.values()) {
        if(position.getPosition() == pos) {
          return position;
        }
      }
      //couldn't find position, return tutorial
      return ClickPosition.VIEW_SETUP_VIDEO;
    }

    /**
     * @return gets position of item in inventory
     */
    public int getPosition() {
      return position;
    }
  }

}
