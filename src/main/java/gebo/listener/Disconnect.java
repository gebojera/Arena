package gebo.listener;

import gebo.Arena;
import gebo.game.GameInstance;
import gebo.game.GameMessage;
import gebo.player.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class Disconnect implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        PlayerProfile.getByPlayer(event.getPlayer()).saveStats();

        if(Bukkit.getOnlinePlayers().size()-1 < Arena.getInstance().min_players) {
            if(GameInstance.get().getState() == Arena.GameState.STARTING) {
                GameInstance.get().resumeWaiting();
            }
        }
    }
}
