package de.freddy.tutorial.commands;

import de.freddy.tutorial.Tutorial;
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
            sender.sendMessage(Tutorial.PREFIX + "Nur Spieler können diesen Command ausführen");
            return true;
        }
        Player player = (Player) sender;
        if(args.length == 0){
        player.sendMessage(Tutorial.PREFIX + "§r§aDein Ping liegt bei §e" + player.getPing());
        return true;
        }else{
            Player otherplayer = Bukkit.getPlayer(args[0]);
            if(!(Bukkit.getOnlinePlayers().contains(otherplayer))){
                player.sendMessage("Dieser Spieler ist nicht online");
                return true;
            }else{
                assert otherplayer != null;
                player.sendMessage(Tutorial.PREFIX + "§r§aDer Ping von " + otherplayer.getName() + " liegt bei §e" + otherplayer.getPing());
                return true;
            }
        }
    }
}
