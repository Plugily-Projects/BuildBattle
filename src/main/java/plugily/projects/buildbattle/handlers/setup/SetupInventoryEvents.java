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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion.Version;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.dimensional.Cuboid;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import pl.plajerlair.commonsbox.minecraft.item.ItemUtils;
import pl.plajerlair.commonsbox.minecraft.misc.stuff.ComplementAccessor;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.arena.impl.GuessTheBuildArena;
import plugily.projects.buildbattle.arena.impl.SoloArena;
import plugily.projects.buildbattle.arena.impl.TeamArena;
import plugily.projects.buildbattle.arena.managers.plots.Plot;
import plugily.projects.buildbattle.handlers.PermissionManager;
import plugily.projects.buildbattle.handlers.sign.ArenaSign;

import java.util.ArrayList;
import java.util.List;

import static plugily.projects.buildbattle.handlers.setup.SetupInventory.isOptionDone;

/**
 * Created by Tom on 15/06/2015.
 */
public class SetupInventoryEvents implements Listener {

  private final Main plugin;

  public SetupInventoryEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onGameTypeSetClick(InventoryClickEvent e) {
    if(!(e.getWhoClicked() instanceof Player || e.getWhoClicked().hasPermission(PermissionManager.getEditGames()))) {
      return;
    }
    if(!ComplementAccessor.getComplement().getTitle(e.getView()).contains("Game type:") || !ItemUtils.isItemStackNamed(e.getCurrentItem())) {
      return;
    }
    BaseArena arena = ArenaRegistry.getArena(ComplementAccessor.getComplement().getTitle(e.getView()).replace("Game type: ", ""));
    if(arena == null) {
      return;
    }

    String name = ChatColor.stripColor(ComplementAccessor.getComplement().getDisplayName(e.getCurrentItem().getItemMeta()));
    Player player = (Player) e.getWhoClicked();

    e.setCancelled(true);
    player.closeInventory();

    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    if(name.contains("Solo")) {
      arena.setArenaType(BaseArena.ArenaType.SOLO);
      config.set("instances." + arena.getID() + ".gametype", "SOLO");
    } else if(name.contains("Team")) {
      arena.setArenaType(BaseArena.ArenaType.TEAM);
      config.set("instances." + arena.getID() + ".gametype", "TEAM");
    } else if(name.contains("Guess The Build")) {
      arena.setArenaType(BaseArena.ArenaType.GUESS_THE_BUILD);
      config.set("instances." + arena.getID() + ".gametype", "GUESS_THE_BUILD");
    }
    player.sendMessage(ChatColor.GREEN + "Game type of arena set to " + ChatColor.GRAY + name);
    ConfigUtils.saveConfig(plugin, config, "arenas");
  }

  @EventHandler
  public void onClick(InventoryClickEvent e) {
    if(e.getCurrentItem() == null || e.getWhoClicked().getType() != EntityType.PLAYER) {
      return;
    }
    Player player = (Player) e.getWhoClicked();
    if(!(player.hasPermission("buildbattle.admin.create") && ComplementAccessor.getComplement().getTitle(e.getView()).contains("BB Arena:") && ItemUtils.isItemStackNamed(e.getCurrentItem()))) {
      return;
    }

    SetupInventory.ClickPosition slot = SetupInventory.ClickPosition.getByPosition(e.getSlot());

    //do not close inventory nor cancel event when setting arena name via name tag
    if(e.getCurrentItem().getType() != Material.NAME_TAG) {
      if(!(slot == SetupInventory.ClickPosition.SET_MINIMUM_PLAYERS || slot == SetupInventory.ClickPosition.SET_MAXIMUM_PLAYERS)) {
        player.closeInventory();
      }
      e.setCancelled(true);
    }

    BaseArena arena = ArenaRegistry.getArena(ComplementAccessor.getComplement().getTitle(e.getView()).replace("BB Arena: ", ""));
    if(arena == null) {
      return;
    }
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    if(config == null) {
      return;
    }

    ClickType clickType = e.getClick();
    String locationString = player.getLocation().getWorld().getName() + "," + player.getLocation().getX() + "," + player.getLocation().getY() + "," +
        player.getLocation().getZ() + "," + player.getLocation().getYaw() + ",0.0";
    ItemStack currentItem = e.getCurrentItem();
    switch(slot) {
      case SET_ENDING:
        config.set("instances." + arena.getID() + ".Endlocation", locationString);
        player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aEnding location for arena " + arena.getID() + " set at your location!"));
        break;
      case SET_LOBBY:
        config.set("instances." + arena.getID() + ".lobbylocation", locationString);
        player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aLobby location for arena " + arena.getID() + " set at your location!"));
        break;
      case SET_MINIMUM_PLAYERS:
        if(currentItem == null) {
          break; // somehow getCurrentItem is still null even its already checked
        }

        if(clickType.isRightClick()) {
          currentItem.setAmount(currentItem.getAmount() + 1);
        }
        if(clickType.isLeftClick()) {
          currentItem.setAmount(currentItem.getAmount() - 1);
        }
        config.set("instances." + arena.getID() + ".minimumplayers", currentItem.getAmount());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "LEFT click to decrease");
        lore.add(ChatColor.GRAY + "RIGHT click to increase");
        lore.add(ChatColor.DARK_GRAY + "(how many players are needed");
        lore.add(ChatColor.DARK_GRAY + "for game to start lobby countdown)");
        lore.add(ChatColor.RED + "Set it minimum 3 when using TEAM game type!!!");
        lore.add(isOptionDone("instances." + arena.getID() + ".minimumplayers"));
        ItemStack stack = player.getInventory().getItem(SetupInventory.ClickPosition.SET_MINIMUM_PLAYERS.getPosition());
        if(stack != null) {
          ItemMeta meta = stack.getItemMeta();
          ComplementAccessor.getComplement().setLore(meta, lore);
          stack.setItemMeta(meta);
        }
        player.updateInventory();
        break;
      case SET_MAXIMUM_PLAYERS:
        if(currentItem == null) {
          break; // somehow getCurrentItem is still null even its already checked
        }

        if(clickType.isRightClick()) {
          currentItem.setAmount(currentItem.getAmount() + 1);
        }
        if(clickType.isLeftClick()) {
          currentItem.setAmount(currentItem.getAmount() - 1);
        }
        config.set("instances." + arena.getID() + ".maximumplayers", currentItem.getAmount());
        List<String> maxlore = new ArrayList<>();
        maxlore.add(ChatColor.GRAY + "LEFT click to decrease");
        maxlore.add(ChatColor.GRAY + "RIGHT click to increase");
        maxlore.add(ChatColor.DARK_GRAY + "(how many players arena can hold)");
        maxlore.add(isOptionDone("instances." + arena.getID() + ".maximumplayers"));
        ItemStack itemStack = player.getInventory().getItem(SetupInventory.ClickPosition.SET_MAXIMUM_PLAYERS.getPosition());
        if(itemStack != null) {
          ItemMeta meta = itemStack.getItemMeta();
          ComplementAccessor.getComplement().setLore(meta, maxlore);
          itemStack.setItemMeta(meta);
        }
        player.updateInventory();
        break;
      case ADD_SIGN:
        Location location = player.getTargetBlock(null, 10).getLocation();
        if(!(location.getBlock().getState() instanceof Sign)) {
          player.sendMessage(plugin.getChatManager().colorRawMessage("&cPlease look at sign to add it!"));
          break;
        }
        plugin.getSignManager().getArenaSigns().add(new ArenaSign((Sign) location.getBlock().getState(), arena));
        plugin.getSignManager().updateSigns();
        player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Signs.Sign-Created"));
        String loc = location.getBlock().getWorld().getName() + "," + location.getBlock().getX() + "," + location.getBlock().getY() + "," + location.getBlock().getZ() + ",0.0,0.0";
        List<String> locs = config.getStringList("instances." + arena.getID() + ".signs");
        locs.add(loc);
        config.set("instances." + arena.getID() + ".signs", locs);
        break;
      case SET_GAME_TYPE:
        //todo inventory framework
        Inventory inv = ComplementAccessor.getComplement().createInventory(null, 9, "Game type: " + arena.getID());
        inv.addItem(new ItemBuilder(Material.NAME_TAG)
            .name(ChatColor.GREEN + "Solo game mode")
            .lore(ChatColor.GRAY + "1 player per plot")
            .build());
        inv.addItem(new ItemBuilder(Material.NAME_TAG)
            .name(ChatColor.GREEN + "Team game mode")
            .lore(ChatColor.GRAY + "2 players per plot")
            .build());
        inv.addItem(new ItemBuilder(Material.NAME_TAG)
            .name(ChatColor.GREEN + "Guess The Build game mode")
            .lore(ChatColor.GRAY + "1 player builds and others try to guess it")
            .build());
        player.openInventory(inv);
        break;
      case SET_MAP_NAME:
        if(currentItem.getType() == Material.NAME_TAG && e.getCursor().getType() == Material.NAME_TAG) {
          if(!ItemUtils.isItemStackNamed(e.getCursor())) {
            player.sendMessage(ChatColor.RED + "This item doesn't has a name!");
            return;
          }
          String newName = ComplementAccessor.getComplement().getDisplayName(e.getCursor().getItemMeta());
          config.set("instances." + arena.getID() + ".mapname", newName);
          player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aName of arena " + arena.getID() + " set to " + newName));
          ComplementAccessor.getComplement().setDisplayName(currentItem.getItemMeta(), ChatColor.GOLD + "Set a mapname (currently: " + newName);
        }
        break;
      case ADD_GAME_PLOT:
        player.performCommand("bba addplot " + arena.getID());
        return;
      case ADD_FLOOR_CHANGER_NPC:
        player.performCommand("bba addnpc");
        break;
      case REGISTER_ARENA:
        if(arena.isReady()) {
          e.getWhoClicked().sendMessage(ChatColor.GREEN + "This arena was already validated and is ready to use!");
          return;
        }
        for(String s : new String[] {"lobbylocation", "Endlocation"}) {
          if(!config.isSet("instances." + arena.getID() + "." + s) || config.getString("instances." + arena.getID() + "." + s)
              .equals(LocationSerializer.locationToString(Bukkit.getWorlds().get(0).getSpawnLocation()))) {
            e.getWhoClicked().sendMessage(ChatColor.RED + "Arena validation failed! Please configure following spawn properly: " + s + " (cannot be world spawn location)");
            return;
          }
        }
        ConfigurationSection plotsSection = config.getConfigurationSection("instances." + arena.getID() + ".plots");
        if(plotsSection == null) {
          e.getWhoClicked().sendMessage(ChatColor.RED + "Arena validation failed! Please configure plots properly");
          return;
        }
        if(arena.getArenaType() == BaseArena.ArenaType.SOLO && plotsSection.getKeys(false).size() < config.getInt("instances." + arena.getID() + ".minimumplayers")) {
          e.getWhoClicked().sendMessage(ChatColor.RED + "Arena validation failed! You need same value of plots as minimumplayers");
        } else if(arena.getArenaType() == BaseArena.ArenaType.TEAM && (plotsSection.getKeys(false).size() / 2) < config.getInt("instances." + arena.getID() + ".minimumplayers")) {
          e.getWhoClicked().sendMessage(ChatColor.RED + "Arena validation failed! You need half value of plots as minimumplayers");
        }
        for(String plotName : plotsSection.getKeys(false)) {
          if(!config.isSet("instances." + arena.getID() + ".plots." + plotName + ".maxpoint") ||
              !config.isSet("instances." + arena.getID() + ".plots." + plotName + ".minpoint")) {
            e.getWhoClicked().sendMessage(ChatColor.RED + "Arena validation failed! Plots are not configured properly! (missing selection values)");
            return;
          }
          Location minPoint = LocationSerializer.getLocation(config.getString("instances." + arena.getID() + ".plots." + plotName + ".minpoint"));
          Biome biome = Version.isCurrentHigher(Version.v1_15_R1) ?
              minPoint.getWorld().getBiome(minPoint.getBlockX(), minPoint.getBlockY(), minPoint.getBlockZ())
              : minPoint.getWorld().getBiome(minPoint.getBlockX(), minPoint.getBlockZ());
          Plot buildPlot = new Plot(arena, biome);
          buildPlot.setCuboid(new Cuboid(minPoint, LocationSerializer.getLocation(config.getString("instances." + arena.getID() + ".plots." + plotName + ".maxpoint"))));
          buildPlot.fullyResetPlot();
          arena.getPlotManager().addBuildPlot(buildPlot);
        }
        e.getWhoClicked().sendMessage(ChatColor.GREEN + "Validation succeeded! Registering new arena instance: " + arena.getID());
        config.set("instances." + arena.getID() + ".isdone", true);
        ConfigUtils.saveConfig(plugin, config, "arenas");
        List<Sign> signsToUpdate = new ArrayList<>();
        ArenaRegistry.unregisterArena(arena);

        for(ArenaSign arenaSign : plugin.getSignManager().getArenaSigns()) {
          if(arenaSign.getArena().equals(arena)) {
            signsToUpdate.add(arenaSign.getSign());
          }
        }
        switch(BaseArena.ArenaType.valueOf(config.getString("instances." + arena.getID() + ".gametype", "solo").toUpperCase())) {
          case TEAM:
            arena = new TeamArena(arena.getID(), plugin);
            break;
          case GUESS_THE_BUILD:
            arena = new GuessTheBuildArena(arena.getID(), plugin);
            break;
          case SOLO:
          default:
            arena = new SoloArena(arena.getID(), plugin);
            break;
        }
        arena.setReady(true);
        arena.setMinimumPlayers(config.getInt("instances." + arena.getID() + ".minimumplayers"));
        arena.setMaximumPlayers(config.getInt("instances." + arena.getID() + ".maximumplayers"));
        arena.setMapName(config.getString("instances." + arena.getID() + ".mapname"));
        arena.setLobbyLocation(LocationSerializer.getLocation(config.getString("instances." + arena.getID() + ".lobbylocation")));
        arena.setEndLocation(LocationSerializer.getLocation(config.getString("instances." + arena.getID() + ".Endlocation")));
        arena.setArenaType(BaseArena.ArenaType.valueOf(config.getString("instances." + arena.getID() + ".gametype").toUpperCase()));

        for(String plotName : config.getConfigurationSection("instances." + arena.getID() + ".plots").getKeys(false)) {
          Location minPoint = LocationSerializer.getLocation(config.getString("instances." + arena.getID() + ".plots." + plotName + ".minpoint"));
          Biome biome = Version.isCurrentHigher(Version.v1_15_R1) ?
              minPoint.getWorld().getBiome(minPoint.getBlockX(), minPoint.getBlockY(), minPoint.getBlockZ())
              : minPoint.getWorld().getBiome(minPoint.getBlockX(), minPoint.getBlockZ());
          Plot buildPlot = new Plot(arena, biome);
          buildPlot.setCuboid(new Cuboid(minPoint, LocationSerializer.getLocation(config.getString("instances." + arena.getID() + ".plots." + plotName + ".maxpoint"))));
          buildPlot.fullyResetPlot();
          arena.getPlotManager().addBuildPlot(buildPlot);
        }
        if(arena instanceof SoloArena) {
          ((SoloArena) arena).initPoll();
        }
        ArenaRegistry.registerArena(arena);
        arena.start();
        plugin.getSignManager().getArenaSigns().clear();
        for(Sign s : signsToUpdate) {
          plugin.getSignManager().getArenaSigns().add(new ArenaSign(s, arena));
        }
        plugin.getSignManager().updateSigns();
        break;
      case EXTRAS_AD:
        player.sendMessage(plugin.getChatManager().getPrefix()
            + plugin.getChatManager().colorRawMessage("&6Check patron program here: https://patreon.plugily.xyz/"));
        break;
      case VIEW_SETUP_VIDEO:
        player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorRawMessage("&6Check out this video: " + SetupInventory.VIDEO_LINK));
        break;
    }
    ConfigUtils.saveConfig(plugin, config, "arenas");
  }

}
