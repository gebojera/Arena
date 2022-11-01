package gebo.player;

import gebo.Arena;
import gebo.SQLManager;
import gebo.display.ArenaScoreboard;
import gebo.game.GameInstance;
import gebo.util.StoredValue;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PlayerProfile {

    private static ArrayList<PlayerProfile> cached_profs = new ArrayList<PlayerProfile>();

    private UUID id;
    private ArrayList<SQLManager.Stat> stats;
    private Player p;
    private ArenaScoreboard current_board;

    public PlayerProfile(Player p) {
        this.p = p;
        id = p.getUniqueId();

        Arena.getInstance().getSQL().setupForPlayer(id);
        stats = Arena.getInstance().getSQL().loadStats(id);

        cached_profs.add(this);
    }

    public <T> T getStatValue(String stat_name) {
        for(SQLManager.Stat stat : stats) {
            if(stat.getType().getName().equals(stat_name)) {
                return (T) stat.getValue();
            }
        }
        return null;
    }

    public void setStat(String stat_name, Object value) {
        for(SQLManager.Stat stat : stats) {
            if(stat.getType().getName().equals(stat_name)) {
                stat.setValue(value);
            }
        }
    }

    public ArrayList<SQLManager.Stat> getStats() {
        return stats;
    }

    public Player getPlayer() {
        return p;
    }

    public UUID getId() {
        return id;
    }

    public void saveStats() {
        Arena.getInstance().getSQL().saveStats(this);
    }


    public static ArrayList<PlayerProfile> getCachedProfiles() {
        return cached_profs;
    }

    public static void saveFor(Player p) {
        for(PlayerProfile prof : cached_profs) {
            if(prof.getId().equals(p.getUniqueId())) {
                Arena.getInstance().getSQL().saveStats(prof);
            }
        }
    }

    public static void saveAll() {
        for(PlayerProfile prof : cached_profs) {
            Arena.getInstance().getSQL().saveStats(prof);
        }
    }

    public static void loadAll(List<Player> players) {
        for(Player pl : players) {
            new PlayerProfile(pl);
        }
    }

    public static PlayerProfile addNew(Player p) {
        for(PlayerProfile prof : cached_profs) {
            if(prof.getId().equals(p.getUniqueId())) return prof; //Profile already exists in this instance
        }
        return new PlayerProfile(p);
    }

    public static PlayerProfile getByPlayer(Player get) {
        for(PlayerProfile prof : cached_profs) {
            if(prof.getId().equals(get.getUniqueId())) {
                return prof;
            }
        }
        return null;
    }

    public void updateScoreboard() {
        switch(GameInstance.get().getState()) {
            case WAITING: current_board = new ArenaScoreboard(ChatColor.GOLD + "Arena", p)
                    .addLine("Line 1").addLine("Line 2").addLine(" ")
                    .addScoreLine("Gold: ", new StoredValue(getStatValue("gold"))); break;
            default: current_board = new ArenaScoreboard("Test", p); break;
        }
        current_board.display();
    }

}
