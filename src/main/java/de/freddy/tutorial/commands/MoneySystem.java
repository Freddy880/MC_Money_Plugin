package de.freddy.tutorial.commands;

import de.freddy.tutorial.UUIDFetcher;
import de.freddy.tutorial.utils.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class MoneySystem implements CommandExecutor {
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

}
