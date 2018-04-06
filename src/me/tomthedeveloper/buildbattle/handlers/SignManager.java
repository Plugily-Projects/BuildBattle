package me.tomthedeveloper.buildbattle.handlers;

import me.tomthedeveloper.buildbattle.GameAPI;
import me.tomthedeveloper.buildbattle.game.GameInstance;
import me.tomthedeveloper.buildbattle.game.GameState;
import me.tomthedeveloper.buildbattle.permissions.PermStrings;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * User: Ivan
 * Date: 24/08/13
 * Time: 0:05
 * IDE: IntelliJ IDEA
 * Look me up on bukkit forums!
 * [url]http://forums.bukkit.org/members/ivan.5352/[/url]
 */
public class SignManager extends BukkitRunnable implements Listener {

    public static String[] signlines = new String[]{"--------", "Waiting", "", "--------"};
    public GameAPI plugin;
    HashMap<Sign, GameInstance> signpool = new HashMap();
    Queue<GameInstance> gamequeue = new LinkedList<GameInstance>();


    /*
    The constructor fills our signpool up with sig schedules a
    new bukkit task and registers the associated listener for us.
     */
    public SignManager(GameAPI gameAPI) {
        plugin = gameAPI;
        plugin.getPlugin().getServer().getPluginManager().registerEvents(this, plugin.getPlugin());

        this.start();
    }


    public void start() {
        FileConfiguration config = ConfigurationManager.getConfig("signModification");
        if(!config.contains("signs.format.WaitingForNewGame")) {
            config.set("signs.format.WaitingForNewGame.lines.1", signlines[0]);
            config.set("signs.format.WaitingForNewGame.lines.2", signlines[1]);
            config.set("signs.format.WaitingForNewGame.lines.3", signlines[2]);
            config.set("signs.format.WaitingForNewGame.lines.4", signlines[3]);
            try {
                config.save(ConfigurationManager.getFile("signModification"));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        signlines[0] = config.getString("signs.format.WaitingForNewGame.lines.1");
        signlines[1] = config.getString("signs.format.WaitingForNewGame.lines.2");
        signlines[2] = config.getString("signs.format.WaitingForNewGame.lines.3");
        signlines[3] = config.getString("signs.format.WaitingForNewGame.lines.4");
        for(Sign sign : signpool.keySet()) {
            formatEmptySign(sign);

        }
        this.runTaskTimer(plugin.getPlugin(), 20L, 20L);

    }

    public void removeSign(GameInstance gameInstance) {
        if(signpool.containsValue(gameInstance)) {
            for(Sign sign : signpool.keySet()) {
                if(signpool.get(sign) != null) {
                    if(signpool.get(sign).equals(gameInstance)) {
                        signpool.put(sign, null);
                        return;
                    }
                }
            }
        }
    }

    public boolean registerSign(Sign sign) {
        if(!signpool.containsKey(sign)) {
            signpool.put(sign, null);
            formatEmptySign(sign);
            return true;
        }
        return false;
    }

    /*
    Gets an empty sign out of our signpool
     */
    public Sign getEmptySign() {
        for(Sign sign : signpool.keySet()) {
            if(signpool.get(sign) == null) return sign;
        }

        return null;
    }

    /*
    This will be used by the listener that will check
    if there is a game associated with the clicked sign.
     */
    public GameInstance getBySign(Sign sign) {
        if(signpool.containsKey(sign)) return signpool.get(sign);
        else return null;
    }

    /*
    This method will be called by your gameinstance
    or by some kind of handler that decides when
    a game should show up here on the signwall
    */
    public void addToQueue(GameInstance instance) {
        if(!(gamequeue.contains(instance) || signpool.containsValue(instance) || signpool.values().contains(instance))) {
            gamequeue.add(instance);
        } else {
        }
    }

    /*
    This is the method called by our loop
    that will decide what pops up when
    the sign is empty.
     */
    private void formatEmptySign(Sign sign) {
        sign.setLine(0, signlines[0].replaceAll("(&([a-f0-9]))", "\u00A7$2"));
        sign.setLine(1, signlines[1].replaceAll("(&([a-f0-9]))", "\u00A7$2"));
        sign.setLine(2, signlines[2].replaceAll("(&([a-f0-9]))", "\u00A7$2"));
        sign.setLine(3, signlines[3].replaceAll("(&([a-f0-9]))", "\u00A7$2"));
        sign.update(true);
    }


    /*
    This is our loop. Here is where the magic
    happens :).
     */
    @Override
    public void run() {


    /* This part removes full games from the signwall,
     since they don't need players anymore*/
        for(Sign sign : signpool.keySet()) {
            if(signpool.get(sign) == null) {
                formatEmptySign(sign);
                sign.update(true);
                continue;
            }
            GameInstance instance = getBySign(sign);
            instance.updateSign(sign);

           /* If it no longer needs players,
           remove it from the board*/
            if(!instance.needsPlayers()) {
                signpool.put(sign, null);
                formatEmptySign(sign);
                sign.update(true);
            }
        }


        /* This part checks if more games have to be added to the list
         */
        while(!gamequeue.isEmpty()) {
            Sign emptysign = getEmptySign();

            /* If no signs are availible, break out of the loop
             */
            if(emptysign == null) break;

            GameInstance instance = gamequeue.poll();
            instance.updateSign(emptysign);
            signpool.put(emptysign, instance);
            emptysign.update(true);
        }

    }

    /*
    This is the listener part of our controller.
    It checks if a game is associated with the
    clicked sign.
     */
    @EventHandler
    public void onJoinAttempt(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getState() instanceof Sign) {

            GameInstance instance = getBySign((Sign) event.getClickedBlock().getState());

            if(instance == null) {
                Location location = event.getClickedBlock().getLocation();
                for(GameInstance gameInstance : plugin.getGameInstanceManager().getGameInstances()) {
                    if(gameInstance.getSigns().contains(location)) {
                        instance = gameInstance;
                        break;
                    }
                }
            }
            /* If there is a Start associated, tell the Startinstance
            that someone is trying to join. It will return us a
            string with the message that the player will receive.
            If there is still a spot left for him, the instance
            can teleport him using the joinAttempt() method.*/

            if(instance != null) {
                for(GameInstance gameInstance : plugin.getGameInstanceManager().getGameInstances()) {
                    if(gameInstance.getPlayers().contains(event.getPlayer())) {
                        event.getPlayer().sendMessage(ChatManager.getFromLanguageConfig("YouAreAlreadyIngame", ChatColor.RED + "You are already qeued for a game! You can leave a game with /leave."));
                        return;
                    }
                }

                if(instance.getMAX_PLAYERS() <= instance.getPlayers().size()) {

                    if((event.getPlayer().hasPermission(PermStrings.getVIP()) || event.getPlayer().hasPermission(PermStrings.getJoinFullGames()))) {

                        boolean b = false;
                        for(Player player : instance.getPlayers()) {
                            if(player.hasPermission(PermStrings.getVIP()) || player.hasPermission(PermStrings.getJoinFullGames())) {

                            } else {
                                if((instance.getGameState() == GameState.STARTING || instance.getGameState() == GameState.WAITING_FOR_PLAYERS)) {
                                    instance.leaveAttempt(player);
                                    player.sendMessage(plugin.getGameInstanceManager().getGameInstances().get(0).getChatManager().getMessage("YouGotKickedToMakePlaceForAPremiumPlayer", ChatColor.RED + "You got kicked out of the game to make place for a premium player!"));
                                    instance.getChatManager().broadcastMessage(plugin.getGameInstanceManager().getGameInstances().get(0).getChatManager().getMessage("KickedToMakePlaceForPremiumPlayer", "%PLAYER% got removed from the game to make place for a premium players!", player));
                                    instance.joinAttempt(event.getPlayer());
                                    b = true;
                                    return;
                                } else {
                                    instance.joinAttempt(event.getPlayer());
                                    b = true;
                                    return;
                                }
                            }

                        }
                        if(!b) {
                            event.getPlayer().sendMessage(plugin.getGameInstanceManager().getGameInstances().get(0).getChatManager().getMessage("FullGameAlreadyFullWithPermiumPlayers", ChatColor.RED + "This game is already full with premium players! Sorry"));
                            return;
                        } else {
                            return;
                        }

                    } else {
                        event.getPlayer().sendMessage(plugin.getGameInstanceManager().getGameInstances().get(0).getChatManager().getMessage("NoPermissionToJoinFullGames", "You don't have the permission to join full games!"));
                        return;
                    }
                    // instance.joinAttempt(event.getPlayer());

                } else {
                    instance.joinAttempt(event.getPlayer());
                }
            }
        }
    }
}
