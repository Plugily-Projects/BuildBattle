package me.tomthedeveloper.buildbattle.handlers;

import me.tomthedeveloper.buildbattle.game.GameInstance;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
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


    // public String announceWinners(Player first, Player second, Player third){
    //   gameInstance.getChatManager().broadcastMessage();
    //}

    public static void sendSubTitleMessage(Player player, String message) {
        PlayerConnection titleConnection = ((CraftPlayer) player).getHandle().playerConnection;
        IChatBaseComponent titleMain = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, titleMain);
        titleConnection.sendPacket(packetPlayOutTitle);
    }

    public static void sendTitleMessage(Player player, String message) {

        PlayerConnection titleConnection = ((CraftPlayer) player).getHandle().playerConnection;
        IChatBaseComponent titleMain = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleMain);
        titleConnection.sendPacket(packetPlayOutTitle);
    }

    public static void sendActionBarMessage(Player player, String message) {
        PlayerConnection titleConnection = ((CraftPlayer) player).getHandle().playerConnection;
        IChatBaseComponent titleMain = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat bar = new PacketPlayOutChat(titleMain, (byte) 2);
        titleConnection.sendPacket(bar);
    }
}
