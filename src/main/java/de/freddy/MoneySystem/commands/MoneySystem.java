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

import de.freddy.MoneySystem.UUIDFetcher;
import de.freddy.MoneySystem.utils.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MoneySystem implements CommandExecutor, TabCompleter {
    public static final String PREFIX = "§4§lMoney §r§7§o";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(PREFIX + "Du bist kein Spieler");
            return true;
        }
            //Command
        Player player = (Player) sender;
        if (label.equalsIgnoreCase("pay")) {
            String UUIDsender = player.getUniqueId().toString();
            String target = args[0];
            String uuidTarget = UUIDFetcher.getUUID(target).toString();
            int money = Integer.parseInt(args[1]);
            if (money > getMoney(UUIDsender)) {
                player.sendMessage(PREFIX + "Dein Geld reicht nicht aus!");
                return true;
            }else if(Bukkit.getServer().getPlayer(target)== null) {
                player.sendMessage(PREFIX + "Der Command kann nicht durchgeführt werden, da der Spieler offline ist.");
                return true;
            }
            removeMoney(UUIDsender, money);
            addMoney(uuidTarget, money);
            Bukkit.getPlayer(target).sendMessage(PREFIX + "Dir wurden " + money + "$ von " + player.getName() + " überwiesen.");
            player.sendMessage(PREFIX + "Du hast " + money + "$ an " + target + " überwiesen.");



        }else if(args.length == 0){ //Wenn keine Argumente
            String user = player.getUniqueId().toString();
            player.sendMessage(PREFIX + "Dein Kontostand beträgt " + getMoney(user));
            return true;

        }else if(args[0].equalsIgnoreCase("set")) {     //Setzen von geld
            if (!sender.hasPermission("de.freddy.moneyAdmin")) {    //Permission
                sender.sendMessage(PREFIX + "Sorry, du hast keine Erlaubnis diesen command durchzuführen");
                return true;

            }
            //Kontrolle ob spieler online
            if(Bukkit.getServer().getPlayer(args[1]) == null) {
                sender.sendMessage(PREFIX + "Der Spieler ist nicht online. Der Command kann nicht druchgeführt werden.");
                return true;

            }
            //Command
            String target = UUIDFetcher.getUUID(args[1]).toString();    //Spieler, der geld bekommen soll
            int money = Integer.parseInt(args[2]);

            if (target == null) {           //Kontolle ob spieler exestiert
                sender.sendMessage(PREFIX + "Der spieler existiert nicht.");
                return true;
            }
            setMoney(target, money);
            player.sendMessage(PREFIX + "Das Geld von " + (Bukkit.getServer().getPlayer(args[1])).getName() + " wurde auf " + money + " gesetzt.");

        }else if(args[0].equalsIgnoreCase("remove")) { //Entfernen von geld
            if (!sender.hasPermission("de.freddy.moneyAdmin")) { //berrechtigung
                sender.sendMessage(PREFIX + "Sorry, du hast keine Erlaubnis diesen Command durchzuführen");
                return true;
            }
            //Kontrolle ob spieler online
            if(Bukkit.getServer().getPlayer(args[1]) == null) {
                sender.sendMessage(PREFIX + "Der Spieler ist nicht online. Der Command kann nicht druchgeführt werden.");
                return true;

            }
            //Command
            String target = UUIDFetcher.getUUID(args[1]).toString();    //Spieler, der geld bekommen soll
            int money = Integer.parseInt(args[2]);

            if (target == null) {           //Kontolle ob spieler exestiert
                sender.sendMessage(PREFIX + "Der spieler existiert nicht.");
                return true;
            }
            removeMoney(target, money);
            player.sendMessage(PREFIX + "Das Geld von " + (Bukkit.getServer().getPlayer(args[1])).getName() + " wurde auf " + getMoney(target) + " gesetzt.");

        }else if(args[0].equalsIgnoreCase("add")) {
            if (!sender.hasPermission("de.freddy.moneyAdmin")) {
                sender.sendMessage(PREFIX + "Sorry, du hast keine Erlaubnis diesen Command durchzuführen");
                return true;
            }
            //Kontrolle ob spieler online
            if(Bukkit.getServer().getPlayer(args[1]) == null) {
                sender.sendMessage(PREFIX + "Der Spieler ist nicht online. Der Command kann nicht druchgeführt werden.");
                return true;

            }
            //Command
            String target = UUIDFetcher.getUUID(args[1]).toString();    //Spieler, der geld bekommen soll
            int money = Integer.parseInt(args[2]);

            if (target == null) {           //Kontolle ob spieler exestiert
                sender.sendMessage(PREFIX + "Der spieler existiert nicht.");
                return true;
            }
            addMoney(target, money);
            player.sendMessage(PREFIX + "Das Geld von " + (Bukkit.getServer().getPlayer(args[1])).getName() + " wurde auf " + getMoney(target) + " gesetzt.");

        }else{
            sender.sendMessage(PREFIX + "Command falsch eingegeben");
            return true;
        }

        return true;
    }

    public static int getMoney (String UUID) {
        FileConfig file = new FileConfig("MoneyInfo", "money.yml");
        return file.getInt(UUID);

    }

    public static void setMoney (String UUID, int amount) {
        FileConfig file = new FileConfig("MoneyInfo", "money.yml");
        file.set(UUID, amount);
        file.saveConfig();

    }

    public static void removeMoney (String UUID, int amount) {
        FileConfig file = new FileConfig("MoneyInfo", "money.yml");
        int money = file.getInt(UUID);
        money -= amount;
        file.set(UUID, money);
        file.saveConfig();
    }

    public static void addMoney (String UUID, int amount) {
        FileConfig file = new FileConfig("MoneyInfo", "money.yml");
        int money = file.getInt(UUID);
        money += amount;
        file.set(UUID, money);
        file.saveConfig();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> b = new ArrayList<>();
        if (alias.equals("pay")){
            if(args.length == 1){
                for (Player p : Bukkit.getOnlinePlayers()) {
                    b.add(p.getName());
                }
            }else if (args.length == 2){
                b.add("<Menge>");
            }
        }
        if (args.length == 1){
            StringUtil.copyPartialMatches(args[0], b, completions);
        }else if (args.length == 2){
            StringUtil.copyPartialMatches(args[1], b, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
