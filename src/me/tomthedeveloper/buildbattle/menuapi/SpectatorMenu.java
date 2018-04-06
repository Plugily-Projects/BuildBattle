package me.tomthedeveloper.buildbattle.menuapi;

import me.tomthedeveloper.buildbattle.game.GameInstance;
import me.tomthedeveloper.buildbattle.handlers.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Tom on 2/08/2014.
 */
public abstract class SpectatorMenu {

    private static String specatorMenuName;
    private IconMenu iconMenu;
    private GameInstance GameInstance;

    public SpectatorMenu(GameInstance GameInstance) {

        this.GameInstance = GameInstance;
        FileConfiguration configuration = ConfigurationManager.getConfig("language");
        if(!configuration.contains("SpectatorMenuName")) {
            configuration.set("SpectatorMenuName", "Spectator Menu");
            try {
                configuration.save(ConfigurationManager.getFile("language"));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        specatorMenuName = configuration.getString("SpectatorMenuName");
    }

    private void createMenu() {


        iconMenu = new IconMenu(specatorMenuName, GameInstance.getPlayers().size());
        for(Player player : GameInstance.getPlayersLeft()) {
            ItemStack itemStack = new ItemStack(Material.SKULL_ITEM);
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            boolean b = skullMeta.setOwner(player.getName());


            skullMeta.setDisplayName(player.getName());
            skullMeta.setLore(Arrays.asList(getDescription(player)));
            itemStack.setItemMeta(skullMeta);
            iconMenu.addOption(itemStack);
        }

    }

    public Inventory getInventory() {
        return iconMenu.getIventory();
    }

    public void open(Player player) {
        createMenu();
        iconMenu.open(player);
    }

    public abstract String[] getDescription(Player player);


}
