package me.tomthedeveloper.buildbattle.kitapi;

import me.tomthedeveloper.buildbattle.User;
import me.tomthedeveloper.buildbattle.GameAPI;
import me.tomthedeveloper.buildbattle.events.PlayerChooseKitEvent;
import me.tomthedeveloper.buildbattle.handlers.ChatManager;
import me.tomthedeveloper.buildbattle.handlers.UserManager;
import me.tomthedeveloper.buildbattle.kitapi.basekits.Kit;
import me.tomthedeveloper.buildbattle.menuapi.IconMenu;
import me.tomthedeveloper.buildbattle.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * Created by Tom on 26/07/2014.
 */
public class KitMenuHandler implements Listener {


    private GameAPI GameAPI;
    private IconMenu iconMenu;
    private String itemname = null;
    private Material material;
    private String[] description;
    private String menuName;

    private String UNLOCKED;
    private String LOCKED;


    public KitMenuHandler(GameAPI GameAPI) {
        itemname = ChatManager.getFromLanguageConfig("Kit-Menu-Item-Name", "Kit-Menu");
        this.GameAPI = GameAPI;
        UNLOCKED = ChatManager.getFromLanguageConfig("KitUnlockedLoreInKitMenu", ChatColor.GREEN + "UNLOCKED!");
        LOCKED = ChatManager.getFromLanguageConfig("KitLockedLoreInKitMenu", ChatColor.RED + "LOCKED!");


    }

    public String getItemName() {
        return itemname;
    }

    public void setItemName(String name) {
        this.itemname = name;
    }

    private void createKitMenu(Player player) {
        iconMenu = new IconMenu(getMenuName(), GameAPI.getKitHandler().getKits().size());
        for(Kit kit : GameAPI.getKitHandler().getKits()) {
            ItemStack itemStack = kit.getItemStack();
            if(kit.isUnlockedByPlayer(player)) Util.addLore(itemStack, UNLOCKED);
            else Util.addLore(itemStack, LOCKED);

            iconMenu.addOption(itemStack);
        }
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String[] getDescription() {
        return description;
    }

    public void setDescription(String[] description) {
        this.description = description;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public void openKitMenu(Player player) {
        createKitMenu(player);
        iconMenu.open(player);
    }

    public void giveKitMenuItem(Player player) {
        ItemStack itemStack = new ItemStack(getMaterial());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(getItemName());
        itemMeta.setLore(Arrays.asList(getDescription()));
        itemStack.setItemMeta(itemMeta);
        player.getInventory().addItem(itemStack);
    }


    @EventHandler
    private void onKitMenuItemClick(PlayerInteractEvent event) {
        if(!GameAPI.areKitsEnabled()) return;
        if(!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
        if(event.getPlayer().getItemInHand().getType() != getMaterial()) return;
        if(!event.getPlayer().getItemInHand().hasItemMeta()) return;
        if(!event.getPlayer().getItemInHand().getItemMeta().hasLore()) return;
        if(!event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(getItemName())) ;
        openKitMenu(event.getPlayer());
    }

    @EventHandler
    public void onKitChoose(InventoryClickEvent event) {
        if(!event.getInventory().getName().equalsIgnoreCase(getMenuName())) return;
        if(!GameAPI.areKitsEnabled()) return;
        if(!(event.getWhoClicked() instanceof Player)) ;
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        if(event.getCurrentItem() == null) return;
        if(!(event.isLeftClick() || event.isRightClick())) return;
        if(!event.getCurrentItem().hasItemMeta()) return;
        PlayerChooseKitEvent playerChooseKitEvent = new PlayerChooseKitEvent(player, GameAPI.getKitHandler().getKit(event.getCurrentItem()));
        Bukkit.getPluginManager().callEvent(playerChooseKitEvent);


    }

    @EventHandler
    public void checkifisUnlocked(PlayerChooseKitEvent event) {
        if(!GameAPI.areKitsEnabled()) return;
        if(event.getKit().isUnlockedByPlayer(event.getPlayer())) {
            User user = UserManager.getUser(event.getPlayer().getUniqueId());
            user.setKit(event.getKit());
            String chosenkitmessage = ChatManager.getFromLanguageConfig("KitChosenMessage", ChatColor.GREEN + "You have chosen: " + ChatColor.AQUA + "%KIT%" + ChatColor.GREEN + " !");
            chosenkitmessage = ChatManager.formatMessage(chosenkitmessage, event.getKit());
            event.getPlayer().sendMessage(chosenkitmessage);
        } else {
            String chosenKitMessageButNotUnlocked = ChatManager.getFromLanguageConfig("KitChosenButNotUnlockedMessage", ChatColor.RED + "You haven't unlocked " + ChatColor.AQUA + "%KIT%" + ChatColor.RED + " yet!");

            event.getPlayer().sendMessage(ChatManager.formatMessage(chosenKitMessageButNotUnlocked, event.getKit()));
        }

    }
}
