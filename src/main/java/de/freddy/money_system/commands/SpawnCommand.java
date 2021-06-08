package de.freddy.money_system.commands;

import de.freddy.money_system.Main;
import de.freddy.money_system.utils.FileConfig;
import de.freddy.money_system.utils.LocationUtilies;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpawnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(Main.PREFIX + "Du bist kein Spieler");
            return true;
        }
        Player player = (Player) sender;
        FileConfig spawns = new FileConfig("locations.yml");
        if(label.equalsIgnoreCase("setspawn")) {
            if(player.hasPermission("de.freddy.tutorial.setspawn")){
                spawns.set("spawn", LocationUtilies.loc2String(player.getLocation()));
                spawns.saveConfig();
                player.sendMessage(Main.PREFIX + "Spawn gesetzt");
            }else{
                player.sendMessage(Main.PREFIX + "Dir fehlt die Berrechtigung");
            }
            return true;
        }
        if (spawns.contains("spawn")) {
            LocationUtilies.teleport(player, LocationUtilies.str2loc(spawns.getString("spawn")));
        }else{
            player.sendMessage(Main.PREFIX + "Es wurde kein Spawnpunkt gesetzt.");
        }
        return true;
    }
}
