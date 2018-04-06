package me.tomthedeveloper.buildbattle.utils;

/**
 * Created by Tom on 9/04/2015.
 */

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
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
public class ItemBuilder implements Listener {


    private static final HashMap<String, PotionEffect> effects = new HashMap<String, PotionEffect>();
    private static boolean listener = false;
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
     * Changes the amount of the {@link org.bukkit.inventory.ItemStack}
     *
     * @param amount the new amount to set
     * @return this builder for chaining
     * @since 1.0
     */
    public ItemBuilder amount(final int amount) {
        is.setAmount(amount);
        return this;
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
     * Changes the durability of the {@link org.bukkit.inventory.ItemStack}
     *
     * @param durability the new durability to set
     * @return this builder for chaining
     * @since 1.0
     */
    public ItemBuilder durability(final int durability) {
        is.setDurability((short) durability);
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
     * Adds an {@link org.bukkit.enchantments.Enchantment} with the given level to the {@link org.bukkit.inventory.ItemStack}
     *
     * @param enchantment the enchantment to add
     * @param level       the level of the enchantment
     * @return this builder for chaining
     * @since 1.0
     */
    public ItemBuilder enchantment(final Enchantment enchantment, final int level) {
        is.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    /**
     * Adds an {@link org.bukkit.enchantments.Enchantment} with the level 1 to the {@link org.bukkit.inventory.ItemStack}
     *
     * @param enchantment the enchantment to add
     * @return this builder for chaining
     * @since 1.0
     */
    public ItemBuilder enchantment(final Enchantment enchantment) {
        is.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    /**
     * Changes the {@link org.bukkit.Material} of the {@link org.bukkit.inventory.ItemStack}
     * <p>
     * the new material to set
     *
     * @return this builder for chaining
     * @since 1.0
     */
    public ItemBuilder type(final Material material) {
        is.setType(material);
        return this;
    }

    /**
     * Clears the lore of the {@link org.bukkit.inventory.ItemStack}
     *
     * @return this builder for chaining
     * @since 1.0
     */
    public ItemBuilder clearLore() {
        final ItemMeta meta = is.getItemMeta();
        meta.setLore(new ArrayList<String>());
        is.setItemMeta(meta);
        return this;
    }

    /**
     * Clears the list of {@link org.bukkit.enchantments.Enchantment}s of the {@link org.bukkit.inventory.ItemStack}
     *
     * @return this builder for chaining
     * @since 1.0
     */
    public ItemBuilder clearEnchantments() {
        for(final Enchantment e : is.getEnchantments().keySet()) {
            is.removeEnchantment(e);
        }
        return this;
    }

    /**
     * Sets the {@link org.bukkit.Color} of a part of leather armor
     *
     * @param color the {@link org.bukkit.Color} to use
     * @return this builder for chaining
     * @since 1.1
     */
    public ItemBuilder color(Color color) {
        if(is.getType() == Material.LEATHER_BOOTS || is.getType() == Material.LEATHER_CHESTPLATE || is.getType() == Material.LEATHER_HELMET || is.getType() == Material.LEATHER_LEGGINGS) {
            LeatherArmorMeta meta = (LeatherArmorMeta) is.getItemMeta();
            meta.setColor(color);
            is.setItemMeta(meta);
            return this;
        } else {
            throw new IllegalArgumentException("color() only applicable for leather armor!");
        }
    }

    /**
     * Adds a effects to the item. The effects gets applied to player when
     * <s>wearing the item</s> (later) or consuming it
     *
     * @param type
     *            the {@link org.bukkit.potion.PotionEffectType} to apply
     * @param duration
     *            the duration in ticks (-1 for endless)
     * @param amplifier
     *         ient status
     * @return this builder for chaining
     * @since 1.2


    /**
     * Adds a effects to the item. The effects gets applied to player when
     * <s>wearing the item</s> (later) or consuming it
     *
     * @param effect
     *            the effect to apply
     * @return this builder for chaining
     * @since 1.2
     */


    /**
     * Adds a effects to the item. The effects gets applied to player when
     * <s>wearing the item</s> (later) or consuming it
     *
     * @param type
     *            the {@link org.bukkit.potion.PotionEffectType} to apply
     * @param duration
     *            the duration in ticks (-1 for endless)
     * @param amplifier
     *            the amplifier of the effect
     * @return this builder for chaining
     * @since 1.2
     */


    /**
     * Adds a effects to the item. The effects gets applied to player when
     * <s>wearing the item</s> (later) or consuming it
     *
     * @param type
     *            the {@link org.bukkit.potion.PotionEffectType} to apply
     * @param duration
     *            the duration (-1 for endless)
     * @return this builder for chaining
     * @since 1.2
     */


    /**
     * Builds the {@link org.bukkit.inventory.ItemStack}
     *
     * @return the created {@link org.bukkit.inventory.ItemStack}
     * @since 1.0
     */
    public ItemStack build() {
        return is;
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent e) {
        if(e.getItem().hasItemMeta()) {
            @SuppressWarnings("unchecked") HashMap<String, PotionEffect> copy = (HashMap<String, PotionEffect>) effects.clone();
            String name = e.getItem().getItemMeta().getDisplayName();
            while(copy.containsKey(name)) {
                e.getPlayer().addPotionEffect(copy.get(name), true);
                copy.remove(name);
                name += "#";
            }
        }
    }

    @EventHandler
    public void onItemApply(InventoryClickEvent e) {
        // TODO add effects when item is applied
    }

}