package me.tomthedeveloper.buildbattle;

import me.tomthedeveloper.buildbattle.permissions.PermStrings;
import me.tomthedeveloper.buildbattle.game.GameInstance;
import me.tomthedeveloper.buildbattle.kitapi.basekits.Kit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Tom on 27/07/2014.
 */
public class User {

    public static GameAPI plugin;
    private static long COOLDOWNCOUNTER = 0;
    private ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
    private Scoreboard scoreboard;
    private UUID uuid;
    private UUID lasthitted;
    private int power;
    private int exp;
    private boolean fakedead = false;
    private boolean spectator = false;
    private boolean doublejump = false;
    private boolean hasdoublejumped = false;
    private Kit kit;
    private HashMap<String, Integer> ints = new HashMap<String, Integer>();
    private HashMap<String, Long> cooldowns = new HashMap<String, Long>();
    private HashMap<String, Object> objects = new HashMap<String, Object>();


    public User(UUID uuid) {
        scoreboard = scoreboardManager.getNewScoreboard();
        this.uuid = uuid;

        kit = plugin.getKitHandler().getDefaultKit();
    }

    public static void handleCooldowns() {
        COOLDOWNCOUNTER++;
    }

    public Kit getKit() {
        if(kit == null) {
            throw new NullPointerException("User has no kit!");
        } else return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public Object getObject(String s) {
        if(objects.containsKey(s)) return objects.get(s);
        return null;
    }

    public void setObject(Object object, String s) {
        objects.put(s, object);
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(Scoreboard scoreboard) {
        Bukkit.getPlayer(uuid).setScoreboard(scoreboard);
    }

    public boolean isInInstance() {
        if(plugin.getGameInstanceManager().isInGameInstance(Bukkit.getPlayer(uuid))) return true;
        else return false;
    }

    public GameInstance getGameInstance() {
        return plugin.getGameInstanceManager().getGameInstance(Bukkit.getPlayer(uuid));
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public void addPower() {
        setPower(getPower() + 1);
    }

    public void addPower(int i) {
        setPower(getPower() + i);
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public void addExp(int exp) {
        setExp(getExp() + exp);
    }

    public void addExp() {
        setExp(getExp() + 1);
    }

    public boolean isFakeDead() {
        return fakedead;
    }

    public void setFakeDead(boolean b) {
        fakedead = b;
    }

    public Player toPlayer() {
        return Bukkit.getServer().getPlayer(uuid);
    }

    public void removePower(int i) {
        setPower(getPower() - i);
    }

    public boolean isSpectator() {
        return spectator;
    }

    public void setSpectator(boolean b) {
        spectator = b;
    }

    public void allowDoubleJump() {
        doublejump = true;
    }

    public boolean getAllowDoubleJump() {
        if((this.toPlayer().hasPermission(PermStrings.getDoubleJump()) || this.toPlayer().isOp() || isVIP()) && doublejump) return true;
        else return false;
    }

    public void setAllowDoubleJump(boolean b) {
        doublejump = b;
    }

    public boolean hasDoubleJumped() {
        return hasdoublejumped;
    }

    public void reNewDoubleJump() {
        hasdoublejumped = false;
    }

    public void doubleJumped() {
        hasdoublejumped = true;
    }

    public int getInt(String s) {
        if(!ints.containsKey(s)) {
            ints.put(s, 0);
            return 0;
        } else if(ints.get(s) == null) {
            return 0;
        }

        return ints.get(s);


    }

    public void removeScoreboard() {
        this.toPlayer().setScoreboard(scoreboardManager.getNewScoreboard());

    }

    public void setInt(String s, int i) {
        ints.put(s, i);

    }

    public boolean isPremium() {
        if(this.toPlayer().hasPermission(PermStrings.getVIP()) || this.toPlayer().hasPermission(PermStrings.getMVP()) || this.toPlayer().hasPermission(PermStrings.getELITE())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isVIP() {
        if(this.toPlayer().hasPermission(PermStrings.getVIP()) || this.toPlayer().hasPermission(PermStrings.getMVP()) || this.toPlayer().hasPermission(PermStrings.getELITE())) {
            return true;
        } else {

            return false;
        }
    }

    public boolean isMVP() {
        if(this.toPlayer().hasPermission(PermStrings.getMVP()) || this.toPlayer().hasPermission(PermStrings.getELITE())) return true;
        return false;
    }

    public boolean isELITE() {
        if(this.toPlayer().hasPermission(PermStrings.getELITE())) return true;
        return false;
    }

    public void addInt(String s, int i) {
        ints.put(s, getInt(s) + i);
    }

    public void setCooldown(String s, int seconds) {
        cooldowns.put(s, seconds + COOLDOWNCOUNTER);
    }

    public long getCooldown(String s) {
        if(!cooldowns.containsKey(s)) return 0;
        if(cooldowns.get(s) <= COOLDOWNCOUNTER) return 0;
        return cooldowns.get(s) - COOLDOWNCOUNTER;
    }

    public void removeInt(String string, int i) {
        if(ints.containsKey(string)) {
            ints.put(string, ints.get(string) - i);
        }
    }

    public Player getLastHitted() {
        if(lasthitted == null) return null;
        return Bukkit.getPlayer(lasthitted);
    }

    public void setLastHitted(Player player) {
        if(player == null) {
            lasthitted = null;
            return;
        }
        lasthitted = player.getUniqueId();
    }

    private enum Rank {
        VIP, MVP, ELITE;
    }


}
