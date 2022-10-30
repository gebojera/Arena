package gebo.command;

import gebo.Arena;
import gebo.game.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MapCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(args.length < 1) {
                p.sendMessage(ChatColor.RED + "Usage: /map <create:delete:list:set:save> <name>");
            }else{
                if(args[0].equalsIgnoreCase("list")) {
                    if(Map.getMaps().isEmpty()) {
                        p.sendMessage(ChatColor.GOLD + "=============================================");
                        p.sendMessage(ChatColor.RED + "No maps to display!");
                        p.sendMessage(ChatColor.GOLD + "=============================================");
                        return false;
                    }else{
                        p.sendMessage(ChatColor.GOLD + "=============================================");
                        for(Map map : Map.getMaps()) {
                            p.sendMessage(ChatColor.YELLOW + "-Name: " + ChatColor.GREEN + map.getName() + ChatColor.YELLOW + " ID: " + ChatColor.GREEN + map.getId());
                        }
                        p.sendMessage(ChatColor.GOLD + "=============================================");
                        return false;
                    }
                }
                if(args.length < 2) {
                    if(args[0].equalsIgnoreCase("save")) { //Broad save all function
                        Arena.getInstance().getSQL().purgeMaps();
                        Arena.getInstance().getSQL().saveMaps();
                        p.sendMessage(ChatColor.GREEN + "Saved all maps!");
                    }else{
                        p.sendMessage(ChatColor.RED + "Usage: /map <create:delete:list:set:save> <name>");
                    }
                    return false;
                }
                if(args[0].equalsIgnoreCase("create")) {
                    Map.createNew(args[1]);
                    p.sendMessage(ChatColor.GREEN + "Map '" + args[1] + "' created!");
                }else if(args[0].equalsIgnoreCase("set")) {
                    if(args.length < 3) {
                        p.sendMessage(ChatColor.RED + "Usage: /map set <name> <spawn # 1-12>");
                        return false;
                    }
                    if(Map.getMapByName(args[1]) == null) {
                        p.sendMessage(ChatColor.RED + "Map not found");
                        return false;
                    }
                    int spawn_num = 0;
                    try {
                        spawn_num = Integer.parseInt(args[2]);
                    } catch(Exception ex) {
                        p.sendMessage(ChatColor.RED + "Invalid number '" + args[2] + "'!");
                        return false;
                    }
                    if(spawn_num > 12 && spawn_num < 1) {
                        p.sendMessage(ChatColor.RED + "Invalid number '" + args[2] + "'! Must be between 0 and 12.");
                        return false;
                    }
                    Map map = Map.getMapByName(args[1]);
                    map.setLoc(spawn_num-1, p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ());
                    p.sendMessage(ChatColor.GREEN + "Spawn #" + spawn_num + " set!");
                }else if(args[0].equalsIgnoreCase("save")) {
                    if(Map.getMapByName(args[1]) == null) {
                        p.sendMessage(ChatColor.RED + "Map not found");
                        return false;
                    }
                    Map.getMapByName(args[1]).saveMap();
                    p.sendMessage(ChatColor.GREEN + "Map saved!");
                }else if(args[0].equalsIgnoreCase("delete")) {
                    if(Map.getMapByName(args[1]) == null) {
                        p.sendMessage(ChatColor.RED + "Map not found");
                        return false;
                    }
                    Map.deleteMap(args[1]);
                    p.sendMessage(ChatColor.GREEN + "Map deleted!");
                }
            }
        }else{
            sender.sendMessage("Do not use this command from the console!");
        }
        return false;
    }
}
