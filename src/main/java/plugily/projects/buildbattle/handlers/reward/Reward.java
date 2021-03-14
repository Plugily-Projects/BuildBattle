

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

import java.util.logging.Level;

/**
 * @author Plajer
 * <p>
 * Created at 23.11.2018
 */
public class Reward {

  private final RewardType type;
  private final RewardExecutor executor;
  private String executableCode;
  private final double chance;
  private int place = -1;

  public Reward(RewardType type, String rawCode, int place) {
    this(type, rawCode);
    this.place = place;
  }

  public Reward(RewardType type, String rawCode) {
    this.type = type;
    String processedCode = rawCode;

    //set reward executor based on provided code
    if(rawCode.contains("p:")) {
      this.executor = RewardExecutor.PLAYER;
      processedCode = StringUtils.replace(processedCode, "p:", "");
    } else if(rawCode.contains("script:")) {
      this.executor = RewardExecutor.SCRIPT;
      processedCode = StringUtils.replace(processedCode, "script:", "");
    } else {
      this.executor = RewardExecutor.CONSOLE;
    }

    //search for chance modifier
    if(processedCode.contains("chance(")) {
      int loc = processedCode.indexOf(')');
      //modifier is invalid
      if(loc == -1) {
        Bukkit.getLogger().log(Level.WARNING, "[BuildBattle] rewards.yml configuration is broken! Make sure you did not forget using ) character in chance condition! Command: {0}", rawCode);
        //invalid code, 0% chance to execute
        this.chance = 0.0;
        return;
      }
      String chanceStr = processedCode;
      chanceStr = chanceStr.substring(0, loc).replaceAll("[^0-9]+", "");
      processedCode = StringUtils.replace(processedCode, "chance(" + chanceStr + "):", "");
      this.chance = Double.parseDouble(chanceStr);
    } else {
      this.chance = 100.0;
    }
    this.executableCode = processedCode;
  }

  public RewardExecutor getExecutor() {
    return executor;
  }

  public String getExecutableCode() {
    return executableCode;
  }

  public double getChance() {
    return chance;
  }

  public int getPlace() {
    return place;
  }

  public RewardType getType() {
    return type;
  }

  public enum RewardType {
    END_GAME("endgame"), GTB_GUESS("guess"), GTB_ALL_GUESSED("allguessed"), VOTE("vote"), REPORT("report"), PLACE("place"),
    START_GAME("startgame"), SCOREBOARD_REMOVED("scoreboard_remove");

    private final String path;

    RewardType(String path) {
      this.path = path;
    }

    public String getPath() {
      return path;
    }

  }

  public enum RewardExecutor {
    CONSOLE, PLAYER, SCRIPT
  }

}
