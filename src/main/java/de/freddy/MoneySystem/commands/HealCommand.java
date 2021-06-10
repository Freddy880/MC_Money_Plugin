package de.freddy.MoneySystem.commands;

import de.freddy.MoneySystem.Main;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HealCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            Main.INSTANCE.log("Du bist kein Spieler");
            return true;
        }

        Player player = (Player) sender;

        if(player.hasPermission("de.freddy.tutorial.heal")){
            player.setHealth(20d);
            player.setFoodLevel(20);
            player.sendMessage(Main.PREFIX + "Du wurdest geheilt!");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.3f, 1.2f);
        }else {
            player.sendMessage(Main.PREFIX + "Du hast nicht die richtige Permission. Bitte frage einen Admin, dir diese zu geben.");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.3f, 1.2f);
        }
        return true;
    }
}
