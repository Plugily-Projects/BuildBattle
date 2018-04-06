package me.tomthedeveloper.buildbattle.menu;

import me.tomthedeveloper.buildbattle.BuildPlot;
import me.tomthedeveloper.buildbattle.utils.Util;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Tom on 18/08/2015.
 */
class MenuOption {


    private Material material;
    private String displayname;
    private String[] lore;
    private BuildPlot buildPlot;
    private byte data;


    public MenuOption(BuildPlot buildPlot) {
        this.buildPlot = buildPlot;
    }

    public byte getData() {
        return data;
    }

    public void setData(byte data) {
        this.data = data;
    }

    public BuildPlot getRelatedBuildPlot() {
        return buildPlot;
    }


    private Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    private String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public List<String> getLore() {
        return Arrays.asList(lore);
    }

    public void setLore(List<String> lore) {

        this.lore = lore.toArray(new String[lore.size()]);
    }

    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(getMaterial());
        Util.setItemNameAndLore(itemStack, this.getDisplayname(), lore);
        return itemStack;
    }
}
