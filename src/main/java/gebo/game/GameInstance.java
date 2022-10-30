package gebo.game;

import gebo.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class GameInstance {

    private static final GameInstance instance = new GameInstance();
    private BukkitTask timer;
    private int counter;
    private Arena.GameState state;

    public static GameInstance get() {
        return instance;
    }

    private GameInstance() {
        state = Arena.GameState.WAITING;
    }

    public void init() {
        timer = new BukkitRunnable() {
            @Override
            public void run() {
                if(state == Arena.GameState.WAITING) {
                    if(Bukkit.getOnlinePlayers().size() >= Arena.getInstance().min_players) {
                        state = Arena.GameState.STARTING;
                        counter = 0;
                        GameMessage.newMessage("The game is starting!", GameMessage.MessageType.ANNOUNCEMENT)
                                .addCustomSound(Sound.NOTE_PIANO).send();
                    }else{
                        if(counter == 30) {
                            GameMessage.newMessage("Waiting for players (" + ChatColor.RED + Bukkit.getOnlinePlayers().size() + "/" + ChatColor.RESET + Arena.getInstance().min_players + ")", GameMessage.MessageType.ANNOUNCEMENT)
                                    .send();
                            counter = 0;
                        }
                    }
                }
                if(state == Arena.GameState.STARTING) {
                    if(counter == 30) {
                        startGame();
                        counter = 0;
                    }else if(counter >= 25 || counter == 20 || counter == 15 || counter == 10 || counter == 0) {
                        int sec = 30 - counter;
                        GameMessage.newMessage("The game is starting in " + ChatColor.GOLD + sec + ChatColor.WHITE + " seconds", GameMessage.MessageType.ANNOUNCEMENT)
                                .sound().send();
                    }
                }
                counter++;
            }
        }.runTaskTimer(Arena.getInstance(), 0, 20);
    }

    public int getCounter() {
        return counter;
    }

    private void startGame() { //TP players and such
        state = Arena.GameState.PLAYING;

        GameMessage.newMessage("The game has started!", GameMessage.MessageType.ANNOUNCEMENT)
                        .addCustomSound(Sound.LEVEL_UP).addColor(ChatColor.AQUA).send();
    }

    private void beginEnd() {
        state = Arena.GameState.ENDING;
    }

    private void endGame() {
        state = Arena.GameState.WAITING;
    }

    public void terminateGame() {
        timer.cancel();
    }

    public Arena.GameState getState() {
        return state;
    }

    public void resumeWaiting() {
        state = Arena.GameState.WAITING;
        GameMessage.newMessage("A player left! Waiting for players " + ChatColor.RED + "(" + Bukkit.getOnlinePlayers().size() + ChatColor.GREEN + "/" + ChatColor.GOLD + Arena.getInstance().min_players + ChatColor.RESET + ")", GameMessage.MessageType.ANNOUNCEMENT)
                .send();
    }

}
