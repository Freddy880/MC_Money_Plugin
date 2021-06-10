package de.freddy.money_system.listener;

import de.freddy.money_system.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class JoinQuitListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        player.performCommand("spawn");
        player.getInventory().setHelmet(new ItemStack(Material.ACACIA_BOAT));
                event.setJoinMessage("§a§l+§4§r " + player.getDisplayName() + " " + Main.INSTANCE.config().getString("Join Message"));

        player.sendMessage("Zurzeit online: ");

        if(!player.hasPlayedBefore()) {
            player.sendMessage();
        }


    }
    @EventHandler
    public  void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage("§a§l " + player.getDisplayName() + " " + Main.INSTANCE.config().getString("quit Message"));
    }

}
