package plugily.projects.buildbattle.utils;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;

@SuppressWarnings("serial")
public class MaterialUtil {

	private static Set<Material> WALL_SIGNS = new HashSet<Material>() {
		{
			add(getMat("WALL_SIGN"));
			add(getMat("ACACIA_WALL_SIGN"));
			add(getMat("BIRCH_WALL_SIGN"));
			add(getMat("DARK_OAK_WALL_SIGN"));
			add(getMat("JUNGLE_WALL_SIGN"));
			add(getMat("OAK_WALL_SIGN"));
			add(getMat("SPRUCE_WALL_SIGN"));
			add(getMat("WARPED_WALL_SIGN"));
			add(getMat("CRIMSON_WALL_SIGN"));
		}
	};

	public static boolean isWallSign(Material mat) {
		return WALL_SIGNS.contains(mat);
	}

	private static Material getMat(String name) {
		return Material.getMaterial(name.toUpperCase());
	}
}
