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
        player.sendTitle("Willkommen zurück", player.getName(),1,10,1);
        event.setJoinMessage("§a§l+§4§r " + player.getDisplayName() + " " + Main.config().getString("Join Message"));
        if (Message.hasMessages(player.getUniqueId().toString())){
            player.sendMessage(Main.PREFIX + "Du hast Nachrichten! Gebe /message get ein, um diese zu lesen!");
        }
    }
    @EventHandler
    public  void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage("§a§l " + player.getDisplayName() + " " + Main.config().getString("quit Message"));
    }

}
