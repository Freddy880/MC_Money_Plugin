package de.freddy.MoneySystem.commands;

import de.freddy.MoneySystem.Main;
import de.freddy.MoneySystem.utils.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Message implements CommandExecutor {
    FileConfig messages = new FileConfig("MoneyInfo", "messages.yml");
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        OfflinePlayer empfaenger = Bukkit.getOfflinePlayer(args[1]);
        switch (args[0]){
            case "send":
                if(args.length < 3){   //Genug Argumente
                    player.sendMessage(Main.PREFIX + "Zu Wenige oder zu viele Argumente!");
                    return true;
                }else if (  empfaenger == null){    //Exestiert emphänger
                    player.sendMessage(Main.PREFIX + "Der Spieler existiert nicht!");
                    return true;
                }else if(!empfaenger.hasPlayedBefore() && !Bukkit.getOnlinePlayers().contains(empfaenger)){ //War er schon auf dem Server
                    player.sendMessage(Main.PREFIX + "Der Spieler war noch nie auf dem Server!");
                    return true;
                }
                StringBuilder mess = new StringBuilder();   //Erstellt den String aus den Argumenten
                for (int i = 2; i < args.length; i++) {
                    mess.append(args[i]);
                    mess.append(" ");
                }
                sendMessage(player.getName(), empfaenger.getUniqueId().toString(), mess.toString());
                player.sendMessage(Main.PREFIX + "Das versenden war erfolgreich!");
                return true;

            default:
                player.sendMessage(Main.PREFIX + "Falscher nutzen des Commands gebe help ein!");
                return true;

        }
    }

    /**
     * Senden einer nachricht an einem Spieler
     * @param absender  Absender String
     * @param uuidOfReceiver    UUID des Empfängers String
     * @param message   String message
     */
    public void sendMessage (String absender, String uuidOfReceiver, String message) {
        List<String> infos = messages.getStringList(uuidOfReceiver);
        infos.add(absender +":" + message);
        messages.set(uuidOfReceiver, infos);
        messages.saveConfig();
    }

    /**
     * Bekommen der Nachrichten
     * @param uuidOfPlayer UUID des Spielers der abfragt String
     */
    public void getMessages (String uuidOfPlayer) {
        List<String> infos = messages.getStringList(uuidOfPlayer);
        Player player = Bukkit.getPlayer(uuidOfPlayer);
        if (player == null){
            Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "[ERROR] Methode getMessage Message.java \n" +
                    "Player == null");
            return;
        }
        for (String info : infos) {
            String[] m = info.split(":");
            player.sendMessage("Nachrricht von: " + m[0]);
            for (int b = 1; b < m.length; b++) {
                player.sendMessage(m[b]);
            }
        }
    }
}
