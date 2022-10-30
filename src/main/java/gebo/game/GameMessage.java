package gebo.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class GameMessage {

    private String message;
    private MessageType type;
    private ChatColor msgColor;
    private Player to;
    private boolean sound;
    private Sound customSound;

    public static GameMessage newMessage(String message, MessageType type) {
        return new GameMessage(message, type);
    }

    private GameMessage(String message, MessageType type) {
        this.message = message;
        this.type = type;

        msgColor = ChatColor.WHITE;
    }

    public GameMessage sound() {
        sound = true;
        return this;
    }

    public GameMessage addColor(ChatColor color) {
        msgColor = color;
        return this;
    }

    public GameMessage addCustomSound(Sound sound){
        customSound = sound;
        return this;
    }

    public String get() {
        return message;
    }

    public void send() {
        String announce_prefix = ChatColor.YELLOW + "[" + ChatColor.RED + "Arena" + ChatColor.YELLOW + "] " + ChatColor.RESET;
        String notif_prefix = ChatColor.AQUA + ">" + ChatColor.GOLD + " Info: "+ ChatColor.RESET;

        if(msgColor != null) message = msgColor + message;

        switch(type) {
            case ANNOUNCEMENT: message = announce_prefix + message; break;
            case NOTIFICATION: message = notif_prefix + message; break;
            default: break;
        }

        if(to != null) {
            to.sendMessage(message);
            if(sound) to.playSound(to.getLocation(), Sound.ORB_PICKUP, 1, 1);
            if(customSound != null) to.playSound(to.getLocation(), customSound, 1, 1);
        }else{
            for(Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(message);
                if(sound) p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 1);
                if(customSound != null) p.playSound(p.getLocation(), customSound, 1, 1);
            }
        }
    }

    public enum MessageType {
        ANNOUNCEMENT, NOTIFICATION, REWARD
    }

}
