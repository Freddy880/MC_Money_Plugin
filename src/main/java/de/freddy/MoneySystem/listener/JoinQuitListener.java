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


package de.freddy.MoneySystem.listener;

import de.freddy.MoneySystem.Main;
import de.freddy.MoneySystem.commands.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        player.sendTitle("Willkommen Zurück", player.getName(),1,10,1);
        event.setJoinMessage("§a§l+§4§r " + player.getDisplayName() + " " + Main.config().getString("Join Message"));
        if (Message.hasMessages(player.getUniqueId().toString())){
            player.sendMessage(Main.PREFIX + "Du hast Nachrichten! gebe /message get ein, um diese zu lesen!");
        }
    }
    @EventHandler
    public  void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage("§a§l " + player.getDisplayName() + " " + Main.config().getString("quit Message"));
    }

}
