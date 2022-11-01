package gebo.display;

import gebo.Arena;
import gebo.SQLManager;
import gebo.util.NMSUtil;
import gebo.util.StoredValue;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArenaScoreboard {

    private String title;
    private Player p;
    private Map<Integer, String> lines;
    private List<ScoreLine> score_lines;
    private int next_index;
    private BukkitTask update;

    public ArenaScoreboard(String title, Player p) {
        this.title = title;
        lines = new HashMap<Integer, String>();
        score_lines = new ArrayList<ScoreLine>();
        this.p = p;

        next_index = 0;
    }

    public ArenaScoreboard addLine(String line) {
        lines.put(next_index, line);
        next_index++;
        return this;
    }

    public ArenaScoreboard addScoreLine(String line, StoredValue value) {
        score_lines.add(new ScoreLine(next_index, line, value));
        next_index++;
        return this;
    }

    public void clear() {
        update = null;
    }

    public void init() {
        update = new BukkitRunnable() {
            @Override
            public void run() {
                display();
            }
        }.runTaskTimerAsynchronously(Arena.getInstance(), 0, 5);
    }

    public void display() {
        Scoreboard board = new Scoreboard();
        ScoreboardObjective obj = board.registerObjective("board", IScoreboardCriteria.b);
        obj.setDisplayName(title);

        PacketPlayOutScoreboardObjective createPacket = new PacketPlayOutScoreboardObjective(obj, 0);
        PacketPlayOutScoreboardDisplayObjective packet = new PacketPlayOutScoreboardDisplayObjective(1, obj);
        ArrayList<ScoreboardScore> scores = new ArrayList<ScoreboardScore>();
        for(int i = 0; i < next_index; i++) {
            if(lines.keySet().contains(i)) { //Gets if this is a regular line
                ScoreboardScore sc = new ScoreboardScore(board, obj, lines.get(i));
                sc.setScore((lines.size() + score_lines.size())-i);
                scores.add(sc);
            }else{ //Otherwise it's a 'score' line (a line with a mutable value)
                for(ScoreLine s : score_lines) {
                    if(s.line_index == i) {
                        ScoreboardScore sc = new ScoreboardScore(board, obj, s.build());
                        sc.setScore((lines.size() + score_lines.size())-i);
                        scores.add(sc);
                    }
                }
            }
        }

        PacketPlayOutScoreboardObjective removePacket = new PacketPlayOutScoreboardObjective(obj, 1);

        NMSUtil.sendPacket(p, removePacket);
        NMSUtil.sendPacket(p, createPacket);
        NMSUtil.sendPacket(p, packet);
        for(ScoreboardScore sc : scores) {
            NMSUtil.sendPacket(p, new PacketPlayOutScoreboardScore(sc));
        }

        if(update == null) init();
    }

    private class ScoreLine {

        private int line_index;
        private String line;
        private StoredValue value;

        public ScoreLine(int line_index, String line, StoredValue value) {
            this.line_index = line_index;
            this.line = line;
            this.value = value;
        }

        public String build() {
            return line + value.getValue();
        }
    }
}
