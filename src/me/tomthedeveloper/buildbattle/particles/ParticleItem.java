package me.tomthedeveloper.buildbattle.particles;

import me.tomthedeveloper.buildbattle.handlers.ChatManager;
import me.tomthedeveloper.buildbattle.utils.ParticleEffect;
import me.tomthedeveloper.buildbattle.utils.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by Tom on 23/08/2015.
 */
public class ParticleItem {

    private Material material;
    private Byte data = null;
    private String[] lore;
    private String displayName;
    private ParticleEffect effect;
    private String permission;
    private boolean enabled = true;
    private Location location;
    private int slot;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setData(Byte data) {
        this.data = data;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    private byte getData() {
        return data;
    }

    public void setData(Integer data) {
        this.data = data.byteValue();
    }

    public String[] getLore() {
        return lore;
    }

    public void setLore(String[] lore) {
        this.lore = lore;
    }

    public void setLore(List<String> lore) {

        this.lore = lore.toArray(new String[lore.size()]);
    }

    public String getDisplayName() {
        return ChatManager.formatMessage(displayName);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ParticleEffect getEffect() {
        return effect;
    }

    public void setEffect(ParticleEffect effect) {
        this.effect = effect;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public ItemStack getItemStack() {
        ItemStack itemStack;
        if(data != null) {
            itemStack = new ItemStack(getMaterial(), 1, getData());
        } else {
            itemStack = new ItemStack(getMaterial());

        }
        Util.setItemNameAndLore(itemStack, ChatManager.formatMessage(this.getDisplayName()), lore);
        return itemStack;
    }
}
