package me.tomthedeveloper.buildbattle.menuapi;

/**
 * Created by Tom on 27/07/2014.
 */
public class SubIconMenu extends IconMenu {

    public SubIconMenu(String name, int size) {
        super(name, size);
        getIventory().setItem(serializeInt(size), MenuItems.getGoBackItem());


    }


}
