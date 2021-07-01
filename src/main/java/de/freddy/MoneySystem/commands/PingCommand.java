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
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PingCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(Main.PREFIX + "Nur Spieler können diesen Command ausführen");
            return true;
        }
        Player player = (Player) sender;
        if(args.length == 0){
            int ping = player.getPing();
            player.sendMessage(Main.PREFIX + "§r§aDein Ping liegt bei §e" + ping);
            return true;
        }else{
            Player otherplayer = Bukkit.getPlayer(args[0]);
            if(!(Bukkit.getOnlinePlayers().contains(otherplayer))){
                player.sendMessage("Dieser Spieler ist nicht online");
                return true;
            }else{
                assert otherplayer != null;
                player.sendMessage(Main.PREFIX + "§r§aDer Ping von " + otherplayer.getName() + " liegt bei §e" + otherplayer.getPing());
                return true;
            }
        }
    }
}
