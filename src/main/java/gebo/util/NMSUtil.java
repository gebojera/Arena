package gebo.util;

import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class NMSUtil {

    public static void sendPacket(Player p, Packet pack) {
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(pack);
    }

    public static void sendPacket(Packet pack) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(pack);
        }
    }

    public static void sendPacketExempt(Player exempt, Packet pack) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(!p.getUniqueId().equals(exempt.getUniqueId())) {
                ((CraftPlayer)p).getHandle().playerConnection.sendPacket(pack);
            }
        }
    }

    public static Object getPrivateField(String name, @SuppressWarnings("rawtypes") Class clazz, Object obj) {
        Field field;
        Object o = null;

        try {
            field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            o = field.get(obj);

        } catch(NoSuchFieldException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return o;
    }

}
