/*
    Copyright (C) 2021  Florian Marks

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

    Contact: marks.florian123@gmail.com
 */

package de.freddy.MoneySystem.commands;

import de.freddy.MoneySystem.Main;
import de.freddy.MoneySystem.utils.FileConfig;
import de.freddy.MoneySystem.utils.LocationUtilies;
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
            if(player.hasPermission("de.freddy.MoneySystem.setspawn")){
                spawns.set("spawn", LocationUtilies.loc2String(player.getLocation()));
                spawns.saveConfig();
                player.sendMessage(Main.PREFIX + "Spawn gesetzt");
            }else{
                player.sendMessage(Main.PREFIX + "Dir fehlt die Berechtigung");
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
