package gebo;

import gebo.player.PlayerProfile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Autosave {

    public static final Autosave get() {
        return new Autosave();
    }

    private Autosave() {}

    public void init() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Arena.getInstance().getSQL().purgeMaps();
                Arena.getInstance().getSQL().saveMaps();
                PlayerProfile.saveAll();
            }
        }.runTaskTimerAsynchronously(Arena.getInstance(), 0, 3600);
    }
}
