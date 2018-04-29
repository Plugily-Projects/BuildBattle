package me.tomthedeveloper.buildbattle.handlers;

import me.tomthedeveloper.buildbattle.User;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Tom on 27/07/2014.
 */
public class UserManager {

    private static HashMap<UUID, User> users = new HashMap<>();

    public static User getUser(UUID uuid) {
        if(users.containsKey(uuid)) {
            return users.get(uuid);
        } else {
            users.put(uuid, new User(uuid));
            return users.get(uuid);
        }
    }

    public static void removeUser(UUID uuid) {
        users.remove(uuid);
    }
}
