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

package plugily.projects.buildbattle.events;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import plugily.projects.buildbattle.Main;
import plugily.projects.buildbattle.arena.ArenaEvents;
import plugily.projects.buildbattle.arena.vote.VoteEvents;
import plugily.projects.buildbattle.handlers.menu.OptionsMenuHandler;

public abstract class ListenerHandler {

	private static final Set<PluginListener> listeners = new HashSet<>(5);

	private static final Main BB = JavaPlugin.getPlugin(Main.class);

	public static final Listener ARENA_EVENTS = add(new PluginListener(new ArenaEvents(BB)));
	public static final Listener VOTE_EVENTS = add(new PluginListener(new VoteEvents(BB)));
	public static final Listener OPTION_MENU_EVENTS = add(new PluginListener(new OptionMenuEvents(BB)));
	public static final Listener OPTIONS_MENU_HANDLER = add(new PluginListener(new OptionsMenuHandler(BB)));

	static {
		new MiscEvents(BB); // Useless to cache this
	}

	private static Listener add(PluginListener inst) {
		listeners.add(inst);
		return inst.listener;
	}

	private static Set<PluginListener> copy;

	public static Set<PluginListener> registeredListeners() {
		if (copy == null || copy.size() != listeners.size()) {
			copy = new HashSet<>(listeners);
		}

		return copy;
	}

	public static final class PluginListener {

		public final Listener listener;
		public final boolean requiredForGameStart;

		public boolean isRegistered = false;

		PluginListener(Listener listener) {
			this(listener, true);
		}

		PluginListener(Listener listener, boolean requiredForGameStart) {
			this.listener = listener;
			this.requiredForGameStart = requiredForGameStart;
		}
	}
}
