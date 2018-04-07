package me.tomthedeveloper.buildbattle.utils;

/**
 * Created by Tom on 9/04/2015.
 */

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a chainable builder for {@link org.bukkit.inventory.ItemStack}s in {@link org.bukkit.Bukkit}
 * <br>
 * Example Usage:<br>
 * {@code ItemStack is = new ItemBuilder(Material.LEATHER_HELMET).amount(2).data(4).durability(4).enchantment(Enchantment.ARROW_INFINITE).enchantment(Enchantment.LUCK, 2).name(ChatColor.RED + "the name").lore(ChatColor.GREEN + "line 1").lore(ChatColor.BLUE + "line 2").color(Color.MAROON).build();
 *
 * @author MiniDigger
 * @version 1.2
 */
public class ItemBuilder {

    private final ItemStack is;

    /**
     * Inits the builder with the given {@link org.bukkit.Material}
     *
     * @param mat the {@link org.bukkit.Material} to start the builder from
     * @since 1.0
     */
    public ItemBuilder(final Material mat) {
        is = new ItemStack(mat);
    }

    /**
     * Inits the builder with the given {@link org.bukkit.inventory.ItemStack}
     *
     * @param is the {@link org.bukkit.inventory.ItemStack} to start the builder from
     * @since 1.0
     */
    public ItemBuilder(final ItemStack is) {
        this.is = is;
    }

    /**
     * Changes the display name of the {@link org.bukkit.inventory.ItemStack}
     *
     * @param name the new display name to set
     * @return this builder for chaining
     * @since 1.0
     */
    public ItemBuilder name(final String name) {
        final ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(name);
        is.setItemMeta(meta);
        return this;
    }

    /**
     * Adds a new line to the lore of the {@link org.bukkit.inventory.ItemStack}
     * <p>
     * <p>
     * the new line to add
     *
     * @return this builder for chaining
     * @since 1.0
     */
    public ItemBuilder lore(final String name) {
        final ItemMeta meta = is.getItemMeta();
        List<String> lore = meta.getLore();
        if(lore == null) {
            lore = new ArrayList<String>();
        }
        lore.add(name);
        meta.setLore(lore);
        is.setItemMeta(meta);
        return this;
    }

    /**
     * Changes the data of the {@link org.bukkit.inventory.ItemStack}
     *
     * @param data the new data to set
     * @return this builder for chaining
     * @since 1.0
     */
    @SuppressWarnings("deprecation")
    public ItemBuilder data(final int data) {
        is.setData(new MaterialData(is.getType(), (byte) data));
        return this;
    }

    /**
     * Builds the {@link org.bukkit.inventory.ItemStack}
     *
     * @return the created {@link org.bukkit.inventory.ItemStack}
     * @since 1.0
     */
    public ItemStack build() {
        return is;
    }

}