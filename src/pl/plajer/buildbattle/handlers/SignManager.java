package pl.plajer.buildbattle.handlers;

import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.arena.Arena;
import pl.plajer.buildbattle.arena.ArenaRegistry;
import pl.plajer.buildbattle.arena.ArenaState;
import org.bukkit.Bukkit;
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
import pl.plajer.buildbattle.utils.Util;

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
    public Main plugin;
    HashMap<Sign, Arena> signpool = new HashMap();
    Queue<Arena> gamequeue = new LinkedList<>();


    /*
    The constructor fills our signpool up with sig schedules a
    new bukkit task and registers the associated listener for us.
     */
    public SignManager(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        loadSigns();

        this.start();
    }

    private void loadSigns() {
        if(!plugin.getConfig().contains("signs")) {
            Util.saveLoc("signs.example", Bukkit.getWorlds().get(0).getSpawnLocation());
        }
        for(String path : plugin.getConfig().getConfigurationSection("signs").getKeys(false)) {
            if(path.contains("example")) continue;
            path = "signs." + path;

            Location loc = Util.getLocation(path);
            if(loc == null) System.out.print("LOCATION IS NNNNUUUUULLLL!!");
            if(loc.getBlock().getState() instanceof Sign) {
                registerSign((Sign) loc.getBlock().getState());
            } else {
                System.out.println("Block at given location " + path + " isn't a sign!");
            }
        }
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
        this.runTaskTimer(plugin, 20L, 20L);

    }

    public void removeSign(Arena arena) {
        if(signpool.containsValue(arena)) {
            for(Sign sign : signpool.keySet()) {
                if(signpool.get(sign) != null) {
                    if(signpool.get(sign).equals(arena)) {
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
    public Arena getBySign(Sign sign) {
        return signpool.getOrDefault(sign, null);
    }

    /*
    This method will be called by your gameinstance
    or by some kind of handler that decides when
    a game should show up here on the signwall
    */
    public void addToQueue(Arena arena) {
        if(!(gamequeue.contains(arena) || signpool.containsValue(arena) || signpool.values().contains(arena))) {
            gamequeue.add(arena);
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
            Arena arena = getBySign(sign);
            arena.updateSign(sign);

           /* If it no longer needs players,
           remove it from the board*/
            if(!arena.needsPlayers()) {
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

            Arena instance = gamequeue.poll();
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

            Arena arenaSign = getBySign((Sign) event.getClickedBlock().getState());

            if(arenaSign == null) {
                Location location = event.getClickedBlock().getLocation();
                for(Arena arena : ArenaRegistry.getArenas()) {
                    if(arena.getSigns().contains(location)) {
                        arenaSign = arena;
                        break;
                    }
                }
            }
            /* If there is a Start associated, tell the Startinstance
            that someone is trying to join. It will return us a
            string with the message that the player will receive.
            If there is still a spot left for him, the instance
            can teleport him using the joinAttempt() method.*/

            if(arenaSign != null) {
                for(Arena arena : ArenaRegistry.getArenas()) {
                    if(arena.getPlayers().contains(event.getPlayer())) {
                        event.getPlayer().sendMessage(ChatManager.getFromLanguageConfig("YouAreAlreadyIngame", ChatColor.RED + "You are already qeued for a game! You can leave a game with /leave."));
                        return;
                    }
                }

                if(arenaSign.getMAX_PLAYERS() <= arenaSign.getPlayers().size()) {

                    if((event.getPlayer().hasPermission(PermissionManager.getVIP()) || event.getPlayer().hasPermission(PermissionManager.getJoinFullGames()))) {

                        boolean b = false;
                        for(Player player : arenaSign.getPlayers()) {
                            if(player.hasPermission(PermissionManager.getVIP()) || player.hasPermission(PermissionManager.getJoinFullGames())) {

                            } else {
                                if((arenaSign.getGameState() == ArenaState.STARTING || arenaSign.getGameState() == ArenaState.WAITING_FOR_PLAYERS)) {
                                    arenaSign.leaveAttempt(player);
                                    player.sendMessage(ArenaRegistry.getArenas().get(0).getChatManager().getMessage("YouGotKickedToMakePlaceForAPremiumPlayer", ChatColor.RED + "You got kicked out of the game to make place for a premium player!"));
                                    arenaSign.getChatManager().broadcastMessage(ArenaRegistry.getArenas().get(0).getChatManager().getMessage("KickedToMakePlaceForPremiumPlayer", "%PLAYER% got removed from the game to make place for a premium players!", player));
                                    arenaSign.joinAttempt(event.getPlayer());
                                    b = true;
                                    return;
                                } else {
                                    arenaSign.joinAttempt(event.getPlayer());
                                    b = true;
                                    return;
                                }
                            }

                        }
                        if(!b) {
                            event.getPlayer().sendMessage(ArenaRegistry.getArenas().get(0).getChatManager().getMessage("FullGameAlreadyFullWithPermiumPlayers", ChatColor.RED + "This game is already full with premium players! Sorry"));
                        }

                    } else {
                        event.getPlayer().sendMessage(ArenaRegistry.getArenas().get(0).getChatManager().getMessage("NoPermissionToJoinFullGames", "You don't have the permission to join full games!"));
                    }
                    // instance.joinAttempt(event.getPlayer());

                } else {
                    arenaSign.joinAttempt(event.getPlayer());
                }
            }
        }
    }
}
