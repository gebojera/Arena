package gebo.game;

import gebo.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;

public class Map {

    private String name;
    private int id;
    private ArrayList<Location> spawns;
    private static ArrayList<Map> maps = new ArrayList<Map>();
    private boolean delete;

    public static Map getNew(int id, String name) {
        return new Map(id, name);
    }

    public static Map createNew(String name) {
        int next_id = 0;
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for(int i = 0; i < maps.size(); i++) {
            ids.add(maps.get(i).getId());
        }
        while(ids.contains(next_id)) {
            next_id++;
        }
        return getNew(next_id, name);
    }

    public static ArrayList<Map> getMaps() {
        return maps;
    }

    public void saveMap() {
        Arena.getInstance().getSQL().setMap(id, name, spawns);
    }

    public static Map getMapByName(String name) {
        for(Map map : maps) {
            if(map.getName().equalsIgnoreCase(name)) {
                return map;
            }
        }
        return null;
    }

    public static void deleteMap(String name) {
        for(Map map : maps) {
            if(map.getName().equalsIgnoreCase(name)) {
                map.setToDelete();
            }
        }
    }

    private Map(int id, String name) {
        this.id = id;
        this.name = name;

        spawns = new ArrayList<Location>();
        maps.add(this);
    }

    public void addLoc(int x, int y, int z) {
        spawns.add(new Location(Arena.getInstance().getWorld(), x, y, z));
    }

    public void setLoc(int index, int x, int y, int z) {
        try {
            if(x == 0 && y == 0 && z == 0) { //If spawn is blank, don't create loc that is 0, 0, 0
                spawns.set(index, Arena.getInstance().getWorld().getSpawnLocation());
            }
            spawns.set(index, new Location(Arena.getInstance().getWorld(), x, y, z));
        } catch (IndexOutOfBoundsException ex) {
            if(x == 0 && y == 0 && z == 0) {
                spawns.add(Arena.getInstance().getWorld().getSpawnLocation());
            }
            spawns.add(new Location(Arena.getInstance().getWorld(), x, y, z));
        }
    }

    public ArrayList<Location> getSpawns() {
        return spawns;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return  id;
    }

    public Location getSpawnAt(int index) {
        return spawns.get(index);
    }

    public void setToDelete() {
        delete = true;
    }

    public boolean shouldDelete() {
        return delete;
    }


}
