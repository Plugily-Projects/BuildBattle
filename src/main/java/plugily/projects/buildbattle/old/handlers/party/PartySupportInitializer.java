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

package plugily.projects.buildbattle.old.handlers.party;

import org.bukkit.entity.Player;
import plugily.projects.buildbattle.old.ConfigPreferences;
import plugily.projects.buildbattle.old.Main;

/**
 * @author Plajer
 * <p>
 * Created at 09.02.2020
 */
public class PartySupportInitializer {

  public PartyHandler initialize(Main plugin) {
    if(!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DISABLE_PARTIES)) {
      org.bukkit.plugin.PluginManager pm = plugin.getServer().getPluginManager();

      if(pm.isPluginEnabled("Parties")) {
        Debugger.debug(Debugger.Level.INFO, "[Party] Enabled support for Parties");
        return new PartiesPartyHandlerImpl();
      }

      if(pm.isPluginEnabled("Spigot-Party-API-PAF")) {
        Debugger.debug(Debugger.Level.INFO, "[Party] Enabled support for Spigot-Party-API-PAF (Bungeecord)");
        return new PAFBPartyHandlerImpl();
      }

      if(pm.isPluginEnabled("PartyAndFriends")) {
        Debugger.debug(Debugger.Level.INFO, "[Party] Enabled support for PartyAndFriends (Spigot)");
        return new PAFSPartyHandlerImpl();
      }
    }

    return new PartyHandler() {
      @Override
      public boolean isPlayerInParty(Player player) {
        return false;
      }

      @Override
      public GameParty getParty(Player player) {
        return null;
      }

      @Override
      public boolean partiesSupported() {
        return false;
      }

      @Override
      public PartyPluginType getPartyPluginType() {
        return PartyPluginType.NONE;
      }
    };
  }

}
