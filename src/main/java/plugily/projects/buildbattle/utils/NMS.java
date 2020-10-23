package plugily.projects.buildbattle.utils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion.Version;
import plugily.projects.buildbattle.Main;

@SuppressWarnings("deprecation")
public abstract class NMS {

	private static final Main PLUGIN = JavaPlugin.getPlugin(Main.class);

	public static void hidePlayer(Player to, Player p) {
		if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
			to.hidePlayer(PLUGIN, p);
		} else {
			to.hidePlayer(p);
		}
	}

	public static void showPlayer(Player to, Player p) {
		if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
			to.showPlayer(PLUGIN, p);
		} else {
			to.showPlayer(p);
		}
	}
}
