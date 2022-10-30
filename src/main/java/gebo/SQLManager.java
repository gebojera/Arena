package gebo;

import com.sun.tools.javac.file.Locations;
import gebo.game.Map;
import gebo.player.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class SQLManager {

    private static final SQLManager manager = new SQLManager();
    private ArrayList<StatType> stat_types;
    private Connection connection;
    private final String host, database, username, password;
    private int port;

    public static final SQLManager get() {
        return manager;
    }

    private SQLManager() {
        statTypes();

        host = "localhost";
        port = 3306;
        database = "arena";
        username = "root";
        password = "root";

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    openConnection();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(Arena.getInstance());

        int rand = new Random().nextInt(6);

        loadMaps();
    }

    private void statTypes() {
        stat_types = new ArrayList<StatType>();
        stat_types.add(new StatType(DATA_TYPE.INT, "gold", 0));
        stat_types.add(new StatType(DATA_TYPE.INT, "xp", 0));
        stat_types.add(new StatType(DATA_TYPE.INT, "kills", 0));
    }

    public void saveMaps() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Map map : Map.getMaps()) {
                    setMap(map.getId(), map.getName(), map.getSpawns());
                }
            }
        }.runTaskAsynchronously(Arena.getInstance());
    }

    public void loadMaps() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    ResultSet all_tables = connection.createStatement().executeQuery("show tables;");
                    while(all_tables.next()) {
                        String s = all_tables.getString(1);
                        if(s.contains("map_")) {
                            int id = Integer.parseInt(s.substring(4));
                            ResultSet set = connection.createStatement().executeQuery("SELECT name FROM " + s + ";"); set.next();
                            String name = set.getString("name");
                            Map map = Map.getNew(id, name);
                            for(int i = 0; i < 12; i++) {
                                String x = "x" + i;
                                String y = "y" + i;
                                String z = "z" + i;
                                ResultSet results_x = connection.createStatement().executeQuery("SELECT " + x + " FROM " + s + ";"); results_x.next();
                                ResultSet results_y = connection.createStatement().executeQuery("SELECT " + y + " FROM " + s + ";"); results_y.next();
                                ResultSet results_z = connection.createStatement().executeQuery("SELECT " + z + " FROM " + s + ";"); results_z.next();
                                map.setLoc(i, results_x.getInt(x), results_y.getInt(y), results_z.getInt(z));
                            }
                        }
                    }
                    if(!Map.getMaps().isEmpty()) {
                        Bukkit.getLogger().info(ChatColor.GREEN + "All maps loaded!");
                    }else{
                        Bukkit.getLogger().info(ChatColor.YELLOW + "No maps for Arena found");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskLaterAsynchronously(Arena.getInstance(), 20);
    }

    public void purgeMaps() {
        for(int i = 0; i < Map.getMaps().size(); i++) {
            if(Map.getMaps().get(i).shouldDelete()) {
                deleteMap(Map.getMaps().get(i).getId());
                Map.getMaps().remove(i);
            }
        }
    }

    public void deleteMap(int id) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    String mapName = "map_" + id;
                    ResultSet tables = connection.getMetaData().getTables(null, null, mapName, null);
                    if(tables.next()) {
                        connection.createStatement().executeUpdate("drop table " + mapName + ";");
                    }else{
                        Bukkit.getLogger().warning("Attempted to delete a Map with the ID " + id + ", but it was not found!");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    Bukkit.getLogger().warning("Attempted to delete a Map with the ID " + id + ", but it was not found!");
                }
            }
        }.runTaskAsynchronously(Arena.getInstance());
    }

    public void setMap(int id, String name, List<Location> spawns) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    String mapName = "map_" + id;
                    ResultSet tables = connection.getMetaData().getTables(null, null, mapName, null);
                    if(!tables.next()) { //If doesn't exist, set new table and set values
                        connection.createStatement().execute("CREATE TABLE " + mapName + " (name VARCHAR(20), x0 INT, y0 INT, z0 INT, " +
                                "x1 INT, y1 INT, z1 INT, " +
                                "x2 INT, y2 INT, z2 INT, " +
                                "x3 INT, y3 INT, z3 INT, " +
                                "x4 INT, y4 INT, z4 INT, " +
                                "x5 INT, y5 INT, z5 INT, " +
                                "x6 INT, y6 INT, z6 INT, " +
                                "x7 INT, y7 INT, z7 INT, " +
                                "x8 INT, y8 INT, z8 INT, " +
                                "x9 INT, y9 INT, z9 INT, " +
                                "x10 INT, y10 INT, z10 INT, " +
                                "x11 INT, y11 INT, z11 INT);");
                        connection.createStatement().executeUpdate("INSERT INTO " + mapName + " (name) " + "VALUES ('" + name + "');");
                        for(int i = 0; i < spawns.size()-1; i++) {
                            String x = "x" + i;
                            String y = "y" + i;
                            String z = "z" + i;
                            connection.createStatement().executeUpdate("INSERT INTO " + mapName + "(" + x + ", " + y + ", " + z + ") VALUES (" + spawns.get(i).getX() + ", " + spawns.get(i).getY() + ", " + spawns.get(i).getZ() + ");");
                        }
                    }else{ //If it does, set values
                        for(int i = 0; i < spawns.size()-1; i++) {
                            String x = "x" + i;
                            String y = "y" + i;
                            String z = "z" + i;
                            connection.createStatement().executeUpdate("UPDATE " + mapName + " SET " + x + " = " + spawns.get(i).getX() + ";");
                            connection.createStatement().executeUpdate("UPDATE " + mapName + " SET " + y + " = " + spawns.get(i).getY() + ";");
                            connection.createStatement().executeUpdate("UPDATE " + mapName + " SET " + z + " = " + spawns.get(i).getZ() + ";");
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }.runTaskAsynchronously(Arena.getInstance());
    }

    public void setupForPlayer(UUID id) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    ResultSet tables = connection.getMetaData().getTables(null, null, getTableId(id), null);
                    if(!tables.next()) { //If they don't have data
                        connection.createStatement().execute("CREATE TABLE " + getTableId(id) + " (empty INT);");
                        connection.createStatement().executeUpdate("INSERT INTO " + getTableId(id) + " (empty) " + "VALUES (0);");
                        loadStats(id);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }.runTaskAsynchronously(Arena.getInstance());
    }

    private void makeStat(StatType stat, UUID id) {
        String vartype = "";
        switch(stat.getType()) {
            case INT: vartype = "INT"; break;
            case DOUBLE: vartype = "DOUBLE"; break;
            case BOOL: vartype = "BOOL"; break;
            case STRING: vartype = "VARCHAR(20)"; break;
        }
        try {
            connection.createStatement().executeUpdate("ALTER TABLE " + getTableId(id) + " ADD COLUMN " + stat.getName() + " " + vartype + ";");
            connection.createStatement().executeUpdate("UPDATE " + getTableId(id) + " SET " + stat.getName() + " = " + stat.getDefaultValue() + ";");
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void saveStat(StatType stat, UUID id, Object value) {
        try {
            String v = value.toString();
            if(value instanceof String) {
                v = "'" + value.toString() + "'";
            }
            connection.createStatement().executeUpdate("UPDATE " + getTableId(id) + " SET " + stat.getName() + " = " + v + ";");
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList<Stat> loadStats(UUID id) {
        String uuid = id.toString();
        ArrayList<Stat> p_stats = new ArrayList<Stat>();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (StatType s : stat_types) {
                    try {
                        ResultSet results = connection.createStatement().executeQuery("SELECT " + s.getName() + " FROM " + getTableId(id) + ";");
                        results.next();
                        switch(s.getType()) {
                            case INT: p_stats.add(new Stat(s, results.getInt(s.getName()))); break;
                            case BOOL: p_stats.add(new Stat(s, results.getBoolean(s.getName()))); break;
                            case DOUBLE: p_stats.add(new Stat(s, results.getDouble(s.getName()))); break;
                            case STRING: p_stats.add(new Stat(s, results.getString(s.getName()))); break;
                        }
                    } catch(SQLException ex) { //Doesn't exist in preexisting player data -- newly-added stat
                        makeStat(s, id);
                        p_stats.add(new Stat(s, s.getDefaultValue()));
                    }
                }
            }
        }.runTaskLaterAsynchronously(Arena.getInstance(), 1);

        return p_stats;
    }

    public void saveStats(PlayerProfile prof) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Stat s : prof.getStats()) {
                    saveStat(s.getType(), prof.getId(), s.getValue());
                }
            }
        }.runTaskAsynchronously(Arena.getInstance());
    }

    private void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            try {
                connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.database, this.username, this.password);
            } catch(SQLException ex) {
                Bukkit.getLogger().info("[ARENA] Arena DB not found");
                try {
                    connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port, this.username, this.password);
                    connection.createStatement().execute("CREATE DATABASE arena;");
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getTableId(UUID id) {
        return "player" + id.toString().replaceAll("-", "_");
    }


    private enum DATA_TYPE {
        INT, BOOL, DOUBLE, STRING
    }

    public class StatType<T> {

        private DATA_TYPE type;
        private String name;
        private T default_value;

        public StatType(DATA_TYPE type, String name, T default_value) {
            this.type = type;
            this.name = name;
            this.default_value = default_value;
        }

        public DATA_TYPE getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public T getDefaultValue() {
            return default_value;
        }

    }

    public class Stat<T> {

        private T value;
        private StatType type;

        public Stat(StatType type, T value) {
            this.type = type;
            this.value = value;
        }

        public StatType getType() {
            return type;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

    }

}
