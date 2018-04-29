package pl.plajer.buildbattle.events;

import pl.plajer.buildbattle.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Tom on 15/06/2015.
 */
public class SetupInventoryClickEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Arena arena;
    private ItemStack itemStack;
    private ClickType clickType;
    private boolean cancel = false;


    public SetupInventoryClickEvent(Arena arena, ItemStack itemStack, Player player, ClickType clickType) {
        this.player = player;
        this.arena = arena;
        this.itemStack = itemStack;
        this.clickType = clickType;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public Arena getArena() {
        return arena;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public ClickType getClickType() {
        return clickType;
    }

    public boolean isCancelled() {
        return this.cancel;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancel = cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
