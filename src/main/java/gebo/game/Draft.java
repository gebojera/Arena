package gebo.game;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.Map;

public class Draft {

    public enum DraftType {
        WEAPON, ARMOR, ABILITY
    }

    private DraftType type;
    private int weight = -1;
    private Map<Player, Inventory> drafts = new HashMap<Player, Inventory>();

    public static Draft getNew(DraftType type) {
        return new Draft(type);
    }

    private Draft(DraftType type) {
        this.type = type;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    private void fillInventories() {
        if(weight == -1) weight = 2; //If weight never got set, default it to 2
        int max = 11;
        for(int i = 0; i < 12; i++) {
            Inventory inv = Bukkit.createInventory(null, 24, "Draft an Item");
            Player[] players = (Player[]) Bukkit.getOnlinePlayers().toArray();
            FillerRandom filler = new FillerRandom(inv, type, weight, max);
            inv = filler.fill().build();
            drafts.put(players[i], inv);
        }
    }

    public void open() {
        for(Player p : drafts.keySet()) {
            p.openInventory(drafts.get(p));
        }
    }

    public void rotate() {

    }

    public class FillerRandom {

        private int weight;
        private DraftType type;
        private Inventory inv;
        private int max;

        /**
         * Weight can at most be 5
         * @param inv
         * @param type
         * @param weight
         */
        public FillerRandom(Inventory inv, DraftType type, int weight, int max) {
            this.weight = weight;
            this.type = type;
            this.inv = inv;
            this.max = max;

            if(weight > 5) weight = 5;
            if(weight < 0) weight = 0;
        }

        public FillerRandom fill() {
            int empty = inv.getSize()-max;
            int toAdd = max;

            int orig_index = inv.getSize()/empty;
            int index = orig_index;
            index += new Random().nextInt(3); //Add initial random indent

            for(int i = 0; i < inv.getSize(); i++) {
                if(toAdd == 0) break;

                if(index == 0) {
                    //Calculate semi-randomized quantity
                    int quantity = new Random().nextInt(3);
                    //quantity *= item.getMultiplicative(); -- For when method exists

                    index = orig_index;
                    //If there's room, randomize the index a little so inventories don't look the same
                    if(i < inv.getSize()-2) index += new Random().nextInt(3);
                    inv.setItem(i, new ItemStack(Material.STONE, quantity));
                }
                toAdd--;
                index--;
            }
            return this;
        }

        public Inventory build() {
            return inv;
        }

    }


}
