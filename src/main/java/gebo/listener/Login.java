package gebo.listener;

import gebo.Arena;
import gebo.player.PlayerProfile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Login implements Listener {

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        PlayerProfile.addNew(event.getPlayer());
    }
}
