package de.freddy.MoneySystem.commands;

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

import java.util.*;

public class KontoSystem implements CommandExecutor, TabCompleter {

    public static final String PREFIX = "§4§lKonto §r§7§o";
    ArrayList<String> zugriff = new ArrayList<>();
    String path = "konten.";

    /**
     * Mit dem Command kann man das Konto steuern
     *
     * @param sender  Sender
     * @param command -
     * @param label   -
     * @param args    -
     * @return hat der Command funktioniert?
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        FileConfig konten = new FileConfig("MoneyInfo", "money.yml");
        //Wenn Kein Spieler, der den Command ausübt
        if (!(sender instanceof Player)) {
            sender.sendMessage("Du bist kein Spieler");
            return true;
        }
        Player player = (Player) sender;
        switch (args[0]) {
            case "add":  //Argument zum hinzufügen eines Kontos-----------------------------------------------
                String kontoname = args[1];
                //Existiert das Konto? Wenn ja nicht verändern
                if (konten.contains(path + kontoname)) {
                    player.sendMessage("Das Konto konnte nicht erstellt werden, da dieses bereits existiert!");
                    return true;
                }
                //erstellung der Grundlagen des Kontos
                konten.set(path + kontoname + ".besitzer", player.getUniqueId().toString()); //Besitzer

                zugriff.add(player.getUniqueId().toString());   //Zugriff

                konten.set(path + kontoname + ".zugriff", zugriff);
                konten.set(path + kontoname + ".kontostand", 0);    //Kontostand

                konten.saveConfig();
                player.sendMessage(PREFIX + "Das Konto wurde erfolgreich erstellt");    //Massege

                break;
            case "abheben": {    //ABHEBEN vom Konto-----------------------------------------------------
                String konto = args[1];
                //exestiert Konto?
                if (!konten.contains(path + konto)) {
                    player.sendMessage(PREFIX + "Das Konto existiert nicht. Wenn es exestieren müsste bitte einen Admin " +
                            "kontaktieren");
                    return true;
                }
                int menge = Integer.parseInt(args[2]);
                int kontostand = kontoGetMoney(konto);
                List<String> berrechtigung = konten.getStringList(path + konto + ".zugriff");
                //Kontostand hoch genug zum abheben?
                if (menge > kontostand) {
                    player.sendMessage(PREFIX + "Der Kontostand beträgt nur" + kontostand + "$FP. Abheben nicht möglich!");
                    return true;
                } else if (!berrechtigung.contains(player.getUniqueId().toString())) {
                    player.sendMessage(PREFIX + "Du hast keine Berechtigung für das Konto namens:" + konto);
                    return true;
                }
                kontoRemoveMoney(konto, menge);
                MoneySystem.addMoney(player.getUniqueId().toString(), menge);
                player.sendMessage(PREFIX + "Das abheben von " + menge + "$FP war erfolgreich! Das konto hat noch " +
                        kontoGetMoney(konto) + "$FP.");
                //Notification
                if (!player.getUniqueId().toString().equals(konten.getString(path + konto + ".besitzer"))) {
                    Message.sendNotification("Konto System", konten.getString(path + konto + ".besitzer"), player.getName() + " hat " + menge + ("$FP" +
                            " vom Konto " + konto + " abgehoben!"));
                }
                return true;
            }
            case "withdraw": {  //Aufladen des Kontos --------------------------------------------------
                String konto = args[1];
                int ammount = Integer.parseInt(args[2]);
                if (!konten.contains(path + konto)) { //Wenn das Konto nicht exestiert
                    player.sendMessage(PREFIX + "Das Konto existiert nicht.");
                    return true;
                } else if (MoneySystem.getMoney(player.getUniqueId().toString()) < ammount) {  //Wenn der Spieler nicht genug Geld hat
                    player.sendMessage(PREFIX + "Du hast nicht genug Geld um das Konto aufzuladen. Dein Kontostand beträgt nur " +
                            MoneySystem.getMoney(player.getUniqueId().toString()) + "$FP");
                    return true;
                } else
                    //Wenn der Spieler keine Berrechtigung hat
                    if (!konten.getStringList(path + konto + ".zugriff").contains(player.getUniqueId().toString())) {
                        player.sendMessage(PREFIX + "Du hast keine Berechtigung das Konto aufzuladen. Bitte nutze \"transfer\"");
                        return true;
                    } else {
                        kontoAddMoney(konto, ammount);
                        MoneySystem.removeMoney(player.getUniqueId().toString(), ammount);
                        player.sendMessage(PREFIX + "Das Aufladen des Kontos " + konto + " in Höhe von " + ammount + "$FP" +
                                " war erfolgreich!");
                    }
                //Notifivation
                if (!player.getUniqueId().toString().equals(konten.getString(path + konto + ".besitzer"))) {
                    Message.sendNotification("Konto System", konten.getString(path + konto + ".besitzer"), player.getName() + " hat " + ammount + ("$FP" +
                            " dem " + konto + " hinzugefügt!"));
                }
                return true;
            }
            case "get": {   //Kontostand des Kontos bekommen--------------------------------------------
                String konto = args[1];
                if (!konten.contains(path + konto)) {
                    player.sendMessage(PREFIX + "Das Konto existiert nicht!");
                    return true;
                } else if (!konten.getStringList(path + konto + ".zugriff").contains(player.getUniqueId().toString())) {   //Wenn der Spieler keinen Zugriff hat
                    player.sendMessage(PREFIX + "Du hast keine Berechtigung für das Konto");
                    return true;
                } else {
                    player.sendMessage(PREFIX + "Der Kontostand beträgt " + kontoGetMoney(konto) + "$FP");
                    return true;
                }

            }
            case "transfer": {     //Überweisen an Konto als nicht Besitzer
                //TODO Nachricht an Kontoinhaber, wenn er Online kommt
                // evtl. auch Log wer wann was gemacht hat

                String konto = args[1];
                int amount = Integer.parseInt(args[2]);
                if (MoneySystem.getMoney(player.getUniqueId().toString()) < amount) {
                    player.sendMessage(PREFIX + "Dein Geld reicht für die Überweisung von " + amount + "$FP " +
                            "nicht aus");
                    return true;
                } else if (!konten.contains(path + konto)) {
                    player.sendMessage(PREFIX + "Das Konto existiert nicht!");
                    return true;
                } else {
                    MoneySystem.removeMoney(player.getUniqueId().toString(), amount);
                    kontoAddMoney(konto, amount);
                    player.sendMessage(PREFIX + "Die Überweisung von " + amount + "$FP " +
                            "war erfolgreich!");
                    //Notification
                    if (!player.getUniqueId().toString().equals(konten.getString(path + konto + ".besitzer"))) {
                        Message.sendNotification("Konto System", konten.getString(path + konto + ".besitzer"), player.getName() + " hat " + amount + ("$FP" +
                                " ans Konto " + konto + " überwiesen!"));
                    }
                    return true;
                }
            }
            case "allow": {       //Hinzufügen oder Entfernen von erlaubnissen
                String person = args[1];
                String konto = args[2];
                List<String> zugriffe;
                zugriffe = konten.getStringList(path + konto + ".zugriff");
                //Wenn ausführer nicht der Besitzer ist
                if (!konten.contains(path + konto)) {    //Wenn das Konto nicht existiert
                    player.sendMessage(PREFIX + "Das Konto existiert nicht!");
                    return true;
                } else if (!(Bukkit.getPlayer(UUID.fromString(Objects.requireNonNull(konten.getString(path + konto + ".besitzer")))) == player)) {
                    player.sendMessage(PREFIX + "Nur der Besitzer kann die Berechtigungen ändern!");
                    return true;
                } else if (Bukkit.getPlayer(person) == null) {    //Wenn der Spieler nicht existiert
                    player.sendMessage(PREFIX + "Der Spieler existiert nicht oder ist offline.");
                    return true;
                } else if (zugriffe.contains(Objects.requireNonNull(Bukkit.getPlayer(person)).getUniqueId().toString())) {
                    player.sendMessage(PREFIX + "Es hat sich nichts geändert, da der Spieler schon die Erlaubnis hat.");
                    return true;
                } else {
                    zugriffe.add(Objects.requireNonNull(Bukkit.getPlayer(person)).getUniqueId().toString());
                    konten.set(path + konto + ".zugriff", zugriffe);
                    konten.saveConfig();
                    player.sendMessage(PREFIX + "Dem Spieler " + person + "wurde der Zugriff auf das Konto erteilt");
                    //Notification
                    Message.sendNotification("Konto System", Objects.requireNonNull(Bukkit.getPlayer(person)).getUniqueId().toString(), player.getName() + " hat dir den Zugriff" +
                            " auf das Konto " + konto + " erteilt!");
                }
                return true;
            }
            case "ban": {
                String person = args[1];
                String konto = args[2];
                if (!(Bukkit.getPlayer(UUID.fromString(Objects.requireNonNull(konten.getString(path + konto + ".besitzer")))) == player)) {
                    player.sendMessage(PREFIX + "Nur der Besitzer kann die Berechtigungen ändern!");
                    return true;
                } else if (!konten.contains(path + konto)) {    //Wenn das Konto nicht existiert
                    player.sendMessage(PREFIX + "Das Konto existiert nicht!");
                    return true;
                } else if (Bukkit.getPlayer(person) == null) {    //Wenn der Spieler nicht existiert
                    player.sendMessage(PREFIX + "Der Spieler existiert nicht oder ist offline!");
                    return true;
                } else {
                    List<String> zugriffe;
                    zugriffe = konten.getStringList(path + konto + ".zugriff");
                    zugriffe.remove(Objects.requireNonNull(Bukkit.getPlayer(person)).getUniqueId().toString());
                    konten.set(path + konto + ".zugriff", zugriffe);
                    konten.saveConfig();
                    player.sendMessage(PREFIX + "Dem Spieler " + person + "wurde der Zugriff auf das Konto genommen");
                    //notification
                    Message.sendNotification("Konto System", Objects.requireNonNull(Bukkit.getPlayer(person)).getUniqueId().toString(), player.getName() + " hat dir den Zugriff" +
                            " auf das Konto " + konto + " genommen!");
                }
                return true;
            }
            case "sendfrom": {
                if (args[2].equalsIgnoreCase("tokonto")) {
                    String konto = args[1];
                    String konto1 = args[3];
                    //existiert Konto?
                    if (!konten.contains(path + konto) || !konten.contains(path + konto1)) {
                        player.sendMessage(PREFIX + "Eins der Konten existiert nicht. Wenn du denkst dies ist ein Fehler," +
                                "kontaktiere bitte einen Admin");
                        return true;
                    }
                    int menge = Integer.parseInt(args[4]);
                    int kontostand = kontoGetMoney(konto);
                    List<String> berechtigung = konten.getStringList(path + konto + ".zugriff");
                    //Kontostand hoch genug zum abheben?
                    if (menge > kontostand) {
                        player.sendMessage(PREFIX + "Der Kontostand beträgt nur " + kontostand + "$FP. Versenden nicht möglich!");
                        return true;
                    } else if (!berechtigung.contains(player.getUniqueId().toString())) {
                        player.sendMessage(PREFIX + "Du hast keine Berechtigung für das Konto namens:" + konto);
                        return true;
                    }
                    //Durchführung
                    kontoRemoveMoney(konto, menge);
                    kontoAddMoney(konto1, menge);
                    player.sendMessage(PREFIX + "Das versenden von " + menge + "$FP war erfolgreich! Das Konto hat noch " +
                            kontoGetMoney(konto) + "$FP.");
                    //Notifications
                    Message.sendNotification("Konto System", konten.getString(path + konto1 + ".besitzer"),
                            "Dir wurde " + menge + "$FP vom Konto " + konto + " an dein Konto " + konto1 + " überwiesen!");
                    if (!player.getUniqueId().toString().equals(konten.getString(path + konto + ".besitzer"))) {
                        Message.sendNotification("Konto System", konten.getString(path + konto + ".besitzer"), player.getName() + " hat " + menge + ("$FP" +
                                " von Konto " + konto + " ans Konto " + konto1 + " überwiesen!"));
                    }

                    return true;

                } else if (args[2].equalsIgnoreCase("toplayer")) {
                    String konto = args[1];
                    List<String> berechtigung = konten.getStringList(path + konto + ".zugriff");
                    String spieler = args[3];
                    int menge = Integer.parseInt(args[4]);
                    Player spieler1 = Bukkit.getPlayer(spieler);
                    if (spieler1 == null) {   //Exestiert Spieler
                        player.sendMessage(PREFIX + "Der Spieler existiert nicht oder ist offline!");
                        return true;
                    } else if (!konten.contains(path + konto)) {    //wenn konto nicht existiert
                        player.sendMessage(PREFIX + "Das Konto existiert nicht!");
                        return true;
                    } else if (konten.getInt(path + konto + ".kontostand") < menge) {  //Wenn der Kontostand zu gering ist
                        player.sendMessage(PREFIX + "Das Konto hat nicht genug Geld.");
                        return true;
                    } else if (!berechtigung.contains(player.getUniqueId().toString())) {   //wenn keine Berechtigung
                        player.sendMessage(PREFIX + "Du hast keine Berechtigung für das Konto namens:" + konto);
                        return true;
                    } else {
                        MoneySystem.addMoney(spieler1.getUniqueId().toString(), menge);
                        kontoRemoveMoney(konto, menge);
                        player.sendMessage(PREFIX + "Das versenden von " + menge + "$FP war erfolgreich! Das Konto hat noch " +
                                kontoGetMoney(konto) + "$FP.");
                        //Notifications
                        Message.sendNotification("Konto System", spieler1.getUniqueId().toString(), "Dir " +
                                "wurden " + menge + "$FP vom Konto " + konto + "gegeben.");
                        if (!player.getUniqueId().toString().equals(konten.getString(path + konto + ".besitzer"))) {
                            Message.sendNotification("Konto System", konten.getString(path + konto + ".besitzer"), player.getName() + " hat " + menge + ("$FP" +
                                    " von Konto " + konto + " an den Spieler " + spieler1.getName() + " überwiesen!"));
                        }
                        return true;
                    }
                }
            }
            case "remove": {
                String konto = args[1];
                if (konten.contains(path + konto)) {
                    player.sendMessage("Das Konto existiert nicht!");
                    return true;
                } else if (konten.getString(path + konto + ".besitzer").equals(player.getUniqueId().toString())) {
                    player.sendMessage("Nur der Besitzer kann das Konto auflösen!");
                    return true;
                } else if (KontoSystem.kontoGetMoney(konto) != 0) {
                    player.sendMessage(PREFIX + "Das Konto hat noch Guthaben. Löschen nicht möglich!");
                    return true;
                } else {
                    konten.set(path + konto, null);
                    player.sendMessage(PREFIX + "Das Löschen des Kontos war erfolgreich.");
                }
                return true;
            }
            default: {   //FEHLER------------------------------------------------------------------------------------------------
                player.sendMessage(PREFIX + "Command falsch genutzt. Gebe /MoneyPlugin help ein!");
                break;
            }
        }

        konten.saveConfig();
        return true;
    }

    /**
     * Setzt den Kontostand eines Kontos auf einen bestimmten wert
     *
     * @param kontoName der Name des Kontos
     * @param wert      Der neue Kontostand
     * @return boolean hats funktioniert?
     */
    public boolean kontoSetMoney(String kontoName, int wert) {
        FileConfig konten = new FileConfig("MoneyInfo", "money.yml");
        int kontostand;
        if (!konten.contains(path + kontoName)) {
            System.out.println(PREFIX + "[ERROR] kontoSetMoney: Das Konto existiert nicht");
            return false;
        } else {
            kontostand = wert;
            konten.set(path + kontoName + ".kontostand", kontostand);
            konten.saveConfig();
            return true;
        }
    }

    /**
     * Kontostand erhöhen.
     *
     * @param kontoName name des Kontos
     * @param wert      int Erhöhen des wertes um
     */
    public static void kontoAddMoney(String kontoName, int wert) {
        String path = "konten.";
        FileConfig konten = new FileConfig("MoneyInfo", "money.yml");
        int kontostand;
        if (!konten.contains(path + kontoName)) {
            System.out.println(PREFIX + "[ERROR] kontoAddMoney: Das Konto existiert nicht");
        } else {
            kontostand = konten.getInt(path + kontoName + ".kontostand");
            kontostand += wert;
            konten.set(path + kontoName + ".kontostand", kontostand);
            konten.saveConfig();
        }
    }

    /**
     * Getter für den Kontostand eines Kontos
     *
     * @param kontoName Namen des Kontos
     * @return -wenn -1 Error
     * /sonst geld in int
     */
    public static int kontoGetMoney(String kontoName) {
        String path = "konten.";
        FileConfig konten = new FileConfig("MoneyInfo", "money.yml");
        int wert;
        if (!konten.contains(path + kontoName)) {
            System.out.println(PREFIX + "[ERROR] kontoAddMoney: Das Konto existiert nicht");
            return -1;
        } else {
            wert = konten.getInt(path + kontoName + ".kontostand");
            return wert;
        }
    }

    /**
     * Zum removen von Geld vom Konto
     *
     * @param kontoName Name des Kontos
     * @param wert      wert der runter soll
     */
    public void kontoRemoveMoney(String kontoName, int wert) {
        FileConfig konten = new FileConfig("MoneyInfo", "money.yml");
        int kontostand;
        if (!konten.contains(path + kontoName)) {
            System.out.println(PREFIX + "[ERROR] kontoAddMoney: Das Konto existiert nicht");
        } else {
            kontostand = konten.getInt(path + kontoName + ".kontostand");
            kontostand -= wert;
            konten.set(path + kontoName + ".kontostand", kontostand);
            konten.saveConfig();
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        //create new array
        List<String> completions = new ArrayList<>();
        List<String> b = new ArrayList<>();
        if (args.length == 1) {
            b.add("add");
            b.add("abheben");
            b.add("withdraw");
            b.add("get");
            b.add("allow");
            b.add("ban");
            b.add("remove");
            b.add("sendfrom");
            b.add("transfer");
        }
        if (args.length == 2) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "add":
                    b.add("<Kontoname>");
                    break;
                case "abheben":
                case "withdraw":
                case "transfer":
                case "sendfrom":
                case "remove":
                case "get":
                    b.add("<Konto>");
                    break;
                case "allow":
                case "ban":
                    for (Player on : Bukkit.getOnlinePlayers()) {
                        b.add(on.getName());
                    }
                    break;

            }
        }
        if (args.length == 3){
            switch (args[0].toLowerCase(Locale.ROOT)){
                case "abheben":
                case "withdraw":
                case "transfer":
                    b.add("<Menge>");
                    break;
                case "sendfrom":
                    b.add("toplayer");
                    b.add("tokonto");
                    break;
                case "allow":
                case "ban":
                    b.add("<Konto>");
                    break;
            }
        }
        if (args.length ==4){
            if(args[0].equalsIgnoreCase("sendfrom") && args[2].equalsIgnoreCase("toplayer")){
                for (Player on : Bukkit.getOnlinePlayers()) {
                    b.add(on.getName());
                }
            }
            else if(args[0].equalsIgnoreCase("sendfrom") && args[2].equalsIgnoreCase("tokonto")){
                b.add("<Konto>");
            }
        }
        if (args.length ==5 && args[0].equalsIgnoreCase("sendfrom")){
            b.add("<Menge>");
        }
        //Sortiert die Ausgabe
        StringUtil.copyPartialMatches(args[0], b, completions);
        if (args.length == 1){
            StringUtil.copyPartialMatches(args[0], b, completions);
        }else if (args.length == 2){
            StringUtil.copyPartialMatches(args[1], b, completions);
        }else if (args.length == 3) {
            StringUtil.copyPartialMatches(args[2], b, completions);
        }else if (args.length == 4){
            StringUtil.copyPartialMatches(args[3], b, completions);
        }
        //kopiert sachen die matchen
        Collections.sort(completions);
        return completions;
    }
}