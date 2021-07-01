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
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Message implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        FileConfig messages = new FileConfig("MoneyInfo", "messages.yml");
        Player player = (Player) sender;
        switch (args[0]){
            case "send": {
                if (args.length < 3) {   //Genug Argumente
                    player.sendMessage(Main.PREFIX + "Zu Wenige oder zu viele Argumente!");
                    return true;
                }
                OfflinePlayer empfaenger = Bukkit.getOfflinePlayer(args[1]);
                if (empfaenger == null) {    //Exestiert emphänger
                    player.sendMessage(Main.PREFIX + "Der Spieler existiert nicht!");
                    return true;
                } else if (!empfaenger.hasPlayedBefore() && !Bukkit.getOnlinePlayers().contains(empfaenger)) { //War er schon auf dem Server
                    player.sendMessage(Main.PREFIX + "Der Spieler war noch nie auf dem Server!");
                    return true;
                }
                StringBuilder mess = new StringBuilder();   //Erstellt den String aus den Argumenten
                for (int i = 2; i < args.length; i++) {
                    mess.append(args[i]);
                    mess.append(" ");
                }
                sendNotification(player.getName(), empfaenger.getUniqueId().toString(), mess.toString());
                player.sendMessage(Main.PREFIX + "Das versenden war erfolgreich!");
                return true;
            }

            case "get" : {
                if (messages.getStringList(player.getUniqueId().toString()).size() < 1) {
                    player.sendMessage(Main.PREFIX + "Du hast keine Benachrichtigungen.");
                }
            }
                getMessages(player.getUniqueId().toString());
                return true;

            case "delete" : {
                if (args.length != 2) {
                    player.sendMessage(Main.PREFIX + "Du hast zu wenige bzw zu viele Argumente! Müssen genau 2 sein!");
                    return true;
                }
                if (args[1].equalsIgnoreCase("all")) {
                    messages.set(player.getUniqueId().toString(), null);
                    player.sendMessage(Main.PREFIX + "Das Löschen aller Nachrichten war erfolgreich");
                    messages.saveConfig();
                    return true;
                } else {
                    int index = Integer.parseInt(args[1]);
                    List<String> m = messages.getStringList(player.getUniqueId().toString());
                    m.remove(index);
                    messages.set(player.getUniqueId().toString(), m);
                    messages.saveConfig();
                    player.sendMessage(Main.PREFIX + "Das Löschen der Nachricht war erfolgreich");
                    return true;
                }
            }

            default: {
                player.sendMessage(Main.PREFIX + "Falscher nutzen des Commands gebe help ein!");
                return true;
            }
        }
    }

    /**
     * Senden einer nachricht an einem Spieler
     * @param absender  Absender String
     * @param uuidOfReceiver    UUID des Empfängers String
     * @param message   String message
     */
    public static void sendNotification(String absender, String uuidOfReceiver, String message) {
        FileConfig messages = new FileConfig("MoneyInfo", "messages.yml");
        List<String> infos = messages.getStringList(uuidOfReceiver);
        if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(UUID.fromString(uuidOfReceiver)))){
            Player player = Bukkit.getPlayer(UUID.fromString(uuidOfReceiver));
            assert player != null;
            player.sendMessage(Main.PREFIX + "Du hast soeben eine Nachricht erhalten!");
        }
        infos.add(absender +":" + message);
        messages.set(uuidOfReceiver, infos);
        messages.saveConfig();
    }

    /**
     * Bekommen der Nachrichten
     * @param uuidOfPlayer UUID des Spielers der abfragt String
     */
    public void getMessages (String uuidOfPlayer) {
        FileConfig messages = new FileConfig("MoneyInfo", "messages.yml");
        List<String> infos = messages.getStringList(uuidOfPlayer);
        Player player = Bukkit.getPlayer(UUID.fromString(uuidOfPlayer));
        if (player == null){
            Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "[ERROR] Methode getMessage Message.java \n" +
                    "Player == null");
            return;
        }
        for (int i = 0; i < infos.size(); i++) {
            String[] m = infos.get(i).split(":");
            player.sendMessage("§b§o" + i +".§r§a§l Nachricht von: §6" + m[0] + ":");
            for (int b = 1; b < m.length; b++) {
                player.sendMessage( "§7§o"+m[b]);
            }
        }
    }

    /**
     * Sends a apecial masssage to the player
     *
     * @param uuidOfPlayer  uuid of player who got the message
     * @param index number of message
     */
    public void getMessage (String uuidOfPlayer, int index) {
        FileConfig messages = new FileConfig("MoneyInfo", "messages.yml");
        List<String> info = messages.getStringList(uuidOfPlayer);
        String infos = info.get(index);
        Player player = Bukkit.getPlayer(UUID.fromString(uuidOfPlayer));
        if (player == null){
            Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "[ERROR] Methode getMessage Message.java \n" +
                    "Player == null");
            return;
        }
        String[] m = infos.split(":");
        player.sendMessage("Nachrricht von: " + m[0]);
        for (int b = 1; b < m.length; b++) {
            player.sendMessage(m[b]);
        }
    }
    /**
     * Kontrolliet ob ein Spieler nachrichten hat
     * @param uuidOfPlayer UUID des Speilers in String
     * @return boolean
     */
    public static boolean hasMessages(String uuidOfPlayer){
        FileConfig messages = new FileConfig("MoneyInfo", "messages.yml");
        return messages.getStringList(uuidOfPlayer).size() >= 1;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> b = new ArrayList<>();
        if(args.length == 1) {
            b.add("send");
            b.add("get");
            b.add("delete");
        }
        if (args.length == 2) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "send":
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        b.add(p.getName());
                    }
                    break;
                case "delete":
                    Player p = (Player) sender;
                    FileConfig messages = new FileConfig("MoneyInfo", "messages.yml");
                    List<String> n = messages.getStringList(p.getUniqueId().toString());
                    for (int i = 0; i < n.size(); i++) {
                        b.add(String.valueOf(i));
                    }
                    b.add("all");
                    break;
            }
        }
        if (args.length == 3 && args[0].toLowerCase(Locale.ROOT).equals("send")){
            b.add("<Nachricht>");
        }
        if (args.length == 1){
            StringUtil.copyPartialMatches(args[0], b, completions);
        }else if (args.length == 2){
            StringUtil.copyPartialMatches(args[1], b, completions);
        }else if (args.length == 3) {
            StringUtil.copyPartialMatches(args[2], b, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
