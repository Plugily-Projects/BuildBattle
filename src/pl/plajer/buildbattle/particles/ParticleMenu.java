package pl.plajer.buildbattle.particles;

import pl.plajer.buildbattle.BuildPlot;
import pl.plajer.buildbattle.ConfigPreferences;
import pl.plajer.buildbattle.handlers.ChatManager;
import pl.plajer.buildbattle.handlers.ConfigurationManager;
import pl.plajer.buildbattle.handlers.UserManager;
import pl.plajer.buildbattle.utils.ParticleEffect;
import pl.plajer.buildbattle.utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tom on 23/08/2015.
 */
public class ParticleMenu {

    private static List<ParticleItem> particleItems = new ArrayList<>();

    public static void openMenu(Player player, BuildPlot buildPlot) {
        Inventory inventory = player.getServer().createInventory(player, 6 * 9, ChatManager.getSingleMessage("Particle-Menu-Name", "Particle Menu"));
        for(ParticleItem particleItem : particleItems) {
            if(particleItem.isEnabled()) inventory.setItem(particleItem.getSlot(), particleItem.getItemStack());
        }
        ItemStack itemStack = new ItemStack(Material.REDSTONE_BLOCK);
        Util.setItemNameAndLore(itemStack, ChatManager.getSingleMessage("Remove-Particle-Item-Name", ChatColor.RED + "Remove Particles"), new String[]{ChatManager.getSingleMessage("Remove-Particle-Item-Lore", "Right click to open menu!")});
        inventory.setItem(53, itemStack);
        player.openInventory(inventory);
    }


    public static void loadFromConfig() {
        FileConfiguration config = ConfigurationManager.getConfig("particles");
        int slotcounter = 0;
        for(ParticleEffect particleEffect : ParticleEffect.values()) {
            if(particleEffect == ParticleEffect.BLOCK_CRACK || particleEffect == ParticleEffect.ITEM_CRACK || particleEffect == ParticleEffect.ITEM_TAKE || particleEffect == ParticleEffect.BLOCK_DUST || particleEffect == ParticleEffect.MOB_APPEARANCE)
                continue;
            if(!config.contains(particleEffect.toString())) {
                config.set(particleEffect.toString() + ".data", 0);
                config.set(particleEffect.toString() + ".displayname", "&6" + particleEffect.toString());
                config.set(particleEffect.toString() + ".lore", Arrays.asList("Click to activate", "on your location"));
                config.set(particleEffect.toString() + ".material", org.bukkit.Material.PAPER.getId());
                config.set(particleEffect.toString() + ".enabled", true);
                config.set(particleEffect.toString() + ".permission", "particles.VIP");
                config.set(particleEffect.toString() + ".slot", slotcounter);
                slotcounter++;
            }
            try {
                config.save(ConfigurationManager.getFile("particles"));
            } catch(IOException e) {
                e.printStackTrace();
            }
            ParticleItem particleItem = new ParticleItem();
            particleItem.setData(config.getInt(particleEffect.toString() + ".data"));
            particleItem.setEnabled(config.getBoolean(particleEffect.toString() + ".enabled"));
            particleItem.setMaterial(org.bukkit.Material.getMaterial(config.getInt(particleEffect.toString() + ".material")));
            particleItem.setLore(config.getStringList(particleEffect.toString() + ".lore"));
            particleItem.setDisplayName(config.getString(particleEffect.toString() + ".displayname"));
            particleItem.setPermission(config.getString(particleEffect.toString() + ".permission"));
            particleItem.setEffect(particleEffect);
            particleItem.setSlot(config.getInt(particleEffect.toString() + ".slot"));
            particleItems.add(particleItem);
        }


    }


    public static void onClick(Player player, ItemStack itemStack, BuildPlot buildPlot) {
        for(ParticleItem particleItem : particleItems) {
            if(particleItem.getDisplayName().equalsIgnoreCase(itemStack.getItemMeta().getDisplayName()) && particleItem.getMaterial() == itemStack.getType()) {
                if(!player.hasPermission(particleItem.getPermission())) {
                    player.sendMessage(ChatManager.getSingleMessage("No-Permission-For-This-Particle", ChatColor.RED + " No permission for this particle!"));

                } else {
                    if(buildPlot.getParticles().size() >= ConfigPreferences.getMaxParticles()) {
                        player.sendMessage(ChatManager.getSingleMessage("Reached-Max-Amount-Of-Particles", ChatColor.RED + "Reached max amount of particles!"));
                    } else {
                        buildPlot.addParticle(player.getLocation(), particleItem.getEffect());
                        UserManager.getUser(player.getUniqueId()).addInt("particles", 1);
                        player.sendMessage(ChatManager.getSingleMessage("Particle-Succesfully-Added", ChatColor.GREEN + "Particle succesfully added!"));
                    }
                }
            }
        }
    }


    public static ParticleItem getParticleItem(ParticleEffect effect) {
        for(ParticleItem particleItem : particleItems) {
            if(effect == particleItem.getEffect()) return particleItem;
        }
        return null;
    }

    public static List<ParticleItem> getParticleItems() {
        return particleItems;
    }
}
