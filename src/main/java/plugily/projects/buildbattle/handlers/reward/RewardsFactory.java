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

package plugily.projects.buildbattle.handlers.reward;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import plugily.projects.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.commonsbox.minecraft.engine.ScriptEngine;
import plugily.projects.buildbattle.ConfigPreferences;
import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.ArenaRegistry;
import plugily.projects.buildbattle.arena.impl.BaseArena;
import plugily.projects.buildbattle.utils.Debugger;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Tom on 30/01/2016.
 */
public class RewardsFactory {

  private final Set<Reward> rewards = new HashSet<>();
  private final FileConfiguration config;
  private boolean enabled;

  public RewardsFactory(Main plugin) {
    enabled = plugin.getConfigPreferences().getOption(ConfigPreferences.Option.REWARDS);
    config = ConfigUtils.getConfig(plugin, "rewards");
    registerRewards();
  }

  public void performReward(Player player, BaseArena arena, Reward.RewardType type, int place, boolean wasPerformed) {
    if(!enabled) {
      return;
    }

    if (arena != null && !wasPerformed) {
      performRewardForConsole(arena, type, place);
    }

    if (player != null) {
      performReward(player, type, place);
    } else if (arena != null) {
      for(Player p : arena.getPlayers()) {
        pr(p, arena, type, place);
      }
    }
  }

  private void performRewardForConsole(BaseArena arena, Reward.RewardType type, int place) {
    if(!enabled) {
      return;
    }

    for(Reward reward : rewards) {
      if(reward.getType() != type || reward.getExecutor() != Reward.RewardExecutor.CONSOLE) {
        continue;
      }

      if(reward.getPlace() != -1 && reward.getPlace() != place) {
        continue;
      }

      //cannot execute if chance wasn't met
      if(reward.getChance() != -1 && ThreadLocalRandom.current().nextInt(0, 100) > reward.getChance()) {
        continue;
      }

      String command = reward.getExecutableCode();
      command = formatCommandPlaceholders(command, arena, place);

      Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
    }
  }

  public void performReward(Player player, Reward.RewardType type, int place) {
    if (enabled) {
      BaseArena arena = ArenaRegistry.getArena(player);

      if (arena != null) {
        performRewardForConsole(arena, type, place);
        pr(player, arena, type, place);
      }
    }
  }

  private void pr(Player player, BaseArena arena, Reward.RewardType type, int place) {
    if(!enabled || arena == null) {
      return;
    }

    for(Reward reward : rewards) {
      if(reward.getType() != type) {
        continue;
      }

      if(reward.getPlace() != -1 && reward.getPlace() != place) {
        continue;
      }

      //cannot execute if chance wasn't met
      if(reward.getChance() != -1 && ThreadLocalRandom.current().nextInt(0, 100) > reward.getChance()) {
        continue;
      }

      String command = reward.getExecutableCode();
      command = StringUtils.replace(command, "%PLAYER%", player.getName());
      command = formatCommandPlaceholders(command, arena, place);

      switch(reward.getExecutor()) {
        case PLAYER:
          player.performCommand(command);
          break;
        case SCRIPT:
          ScriptEngine engine = new ScriptEngine();
          engine.setValue("player", player);
          engine.setValue("server", Bukkit.getServer());
          engine.setValue("arena", arena);
          engine.execute(command);
          break;
        default:
          break;
      }
    }
  }

  private String formatCommandPlaceholders(String command, BaseArena arena, int place) {
    String formatted = command;
    formatted = StringUtils.replace(formatted, "%ARENA-ID%", arena.getID());
    formatted = StringUtils.replace(formatted, "%MAPNAME%", arena.getMapName());
    formatted = StringUtils.replace(formatted, "%PLACE%", Integer.toString(place));
    formatted = StringUtils.replace(formatted, "%PLAYERAMOUNT%", Integer.toString(arena.getPlayers().size()));
    return formatted;
  }

  private void registerRewards() {
    if(!enabled) {
      return;
    }
    Debugger.debug("[RewardsFactory] Starting rewards registration");
    long start = System.currentTimeMillis();

    Map<Reward.RewardType, Integer> registeredRewards = new EnumMap<>(Reward.RewardType.class);
    for(Reward.RewardType rewardType : Reward.RewardType.values()) {
      if(rewardType == Reward.RewardType.PLACE) {
        ConfigurationSection section = config.getConfigurationSection("rewards." + rewardType.getPath());
        if(section == null) {
          Debugger.debug(Debugger.Level.WARN, "Rewards section " + rewardType.getPath() + " is missing! Was it manually removed?");
          continue;
        }
        for(String key : section.getKeys(false)) {
          for(String reward : section.getStringList(key)) {
            try {
              rewards.add(new Reward(rewardType, reward, Integer.parseInt(key)));
            } catch (NumberFormatException e) {
              continue;
            }

            registeredRewards.put(rewardType, registeredRewards.getOrDefault(rewardType, 0) + 1);
          }
        }
        continue;
      }
      for(String reward : config.getStringList("rewards." + rewardType.getPath())) {
        rewards.add(new Reward(rewardType, reward));
        registeredRewards.put(rewardType, registeredRewards.getOrDefault(rewardType, 0) + 1);
      }
    }

    if (registeredRewards.isEmpty()) {
      Debugger.debug(Debugger.Level.WARN, "[RewardsFactory] No rewards was loaded. Maybe the section was removed?");
      enabled = false;
      return;
    }

    for(Map.Entry<Reward.RewardType, Integer> entry : registeredRewards.entrySet()) {
      Debugger.debug("[RewardsFactory] Registered " + entry.getValue() + " " + entry.getKey().name() + " rewards!");
    }
    Debugger.debug("[RewardsFactory] Registered all rewards took " + (System.currentTimeMillis() - start) + "ms");
  }

}
