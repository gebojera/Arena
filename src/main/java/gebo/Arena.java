package gebo;

import gebo.command.MapCommand;
import gebo.game.GameInstance;
import gebo.listener.Disconnect;
import gebo.listener.Join;
import gebo.listener.Login;
import gebo.listener.Move;
import gebo.player.PlayerProfile;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class Arena extends JavaPlugin {

    private static Arena instance;
    private SQLManager sql;
    public int min_players = 3;

    @Override
    public void onEnable() {
        instance = this;

        sql = SQLManager.get();

        cmds();
        listeners();

        //Handles if plugin enables when players are already present
        PlayerProfile.loadAll(new ArrayList(Bukkit.getOnlinePlayers()));
        GameInstance.get().init();
        Autosave.get().init();
    }

    @Override
    public void onDisable() {
    }

    public static Arena getInstance() {
        return instance;
    }

    private void cmds() {
        getCommand("map").setExecutor(new MapCommand());
    }

    private void listeners() {
        Bukkit.getPluginManager().registerEvents(new Disconnect(), this);
        Bukkit.getPluginManager().registerEvents(new Login(), this);
        Bukkit.getPluginManager().registerEvents(new Join(), this);
        Bukkit.getPluginManager().registerEvents(new Move(), this);
    }

    public SQLManager getSQL() {
        return sql;
    }

    public World getWorld() {
        return Bukkit.getWorlds().get(0);
    }

    public WorldServer getNMSWorld() {
        WorldServer w = ((CraftWorld)getWorld()).getHandle();
        return w;
    }

    public enum GameState {
        WAITING, STARTING, PLAYING, ENDING
    }
}
