package me.tomthedeveloper.buildbattle.handlers;

import me.tomthedeveloper.buildbattle.game.GameInstance;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by Tom on 21/07/2015.
 */
public class MessageHandler {

    private GameInstance gameInstance;

    private MessageHandler(GameInstance gameInstance) {
        this.gameInstance = gameInstance;
    }

    public static void sendTitleMessage(Player player, String message) {
        PlayerConnection titleConnection = ((CraftPlayer) player).getHandle().playerConnection;
        IChatBaseComponent titleMain = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleMain);
        titleConnection.sendPacket(packetPlayOutTitle);
    }
}
