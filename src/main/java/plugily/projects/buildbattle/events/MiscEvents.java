package plugily.projects.buildbattle.events;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredListener;

import plugily.projects.buildbattle.Main;
import plugily.projects.minigamesbox.classic.api.event.game.PlugilyGameJoinAttemptEvent;
import plugily.projects.minigamesbox.classic.api.event.game.PlugilyGameStopEvent;
import plugily.projects.minigamesbox.classic.arena.ArenaState;

final class MiscEvents implements Listener {

	private final Main bb;

	public MiscEvents(Main bb) {
		this.bb = bb;
		bb.getServer().getPluginManager().registerEvents(this, bb);
	}

	private boolean findThisPlugin(Event event) {
		for (RegisteredListener registeredListener : event.getHandlers().getRegisteredListeners()) {
			if (registeredListener.getPlugin() == bb) {
				return true;
			}
		}

		return false;
	}

	@EventHandler(ignoreCancelled = true)
	public void onGameJoin(PlugilyGameJoinAttemptEvent event) {
		if (event.getArena().getArenaState() != ArenaState.WAITING_FOR_PLAYERS) {
			return;
		}

		if (!findThisPlugin(event)) {
			return; // It may conflict with other Plugily Projects
		}

		for (ListenerHandler.PluginListener pluginListener : ListenerHandler.registeredListeners()) {
			if (pluginListener.requiredForGameStart && !pluginListener.isRegistered) {
				bb.getServer().getPluginManager().registerEvents(pluginListener.listener, bb);
				pluginListener.isRegistered = true;
			}
		}
	}

	@EventHandler
	public void onGameEnd(PlugilyGameStopEvent event) {
		if (!findThisPlugin(event)) {
			return; // It may conflict with other Plugily Projects
		}

		for (ListenerHandler.PluginListener pluginListener : ListenerHandler.registeredListeners()) {
			if (pluginListener.requiredForGameStart && pluginListener.isRegistered) {
				HandlerList.unregisterAll(pluginListener.listener);
				pluginListener.isRegistered = false;
			}
		}
	}
}
