package de.freddy.MoneySystem.commands;

import de.freddy.MoneySystem.utils.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class KontoSystem implements CommandExecutor {

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
        //TODO: Konto evt. Command für genehmigung,
        // konto zu konto überweisung
        // Kontostnd abfrage

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
                            MoneySystem.getMoney(player.getUniqueId().toString() + "$FP"));
                    return true;
                } else
                    //Wenn der Spieler keine Berrechtigung hat
                    if (!konten.getStringList(path + konto + ".zugriff").contains(player.getUniqueId().toString())) {
                        player.sendMessage(PREFIX + "Du hast keine Berechtigung das Konto aufzuladen. Bitte nutze \"überweisen\"");
                        return true;
                    } else {
                        kontoAddMoney(konto, ammount);
                        MoneySystem.removeMoney(player.getUniqueId().toString(), ammount);
                        player.sendMessage(PREFIX + "Das Aufladen des Kontos " + konto + " in höhe von " + ammount + "$FP" +
                                " war erfolgreich!");
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
                    return true;
                }
            }
            case "allow": {       //Hinzufügen oder Entfernen von erlaubnissen
                String person = args[1];
                String konto = args[2];
                List<String> zugriffe;
                zugriffe = konten.getStringList(path + konto + ".zugriff");
                //Wenn ausführer nicht der Besitzer ist
                if (!(Bukkit.getPlayer(UUID.fromString(Objects.requireNonNull(konten.getString(path + konto + ".besitzer")))) == player)) {
                    player.sendMessage(PREFIX + "Nur der Besitzer kann die Berechtigungen ändern!");
                    return true;
                } else if (!konten.contains(path + konto)) {    //Wenn das Konto nicht existiert
                    player.sendMessage(PREFIX + "Das Konto existiert nicht!");
                    return true;
                } else if (Bukkit.getPlayer(person) == null) {    //Wenn der Spieler nicht existiert
                    player.sendMessage(PREFIX + "Der Spieler existiert nicht oder ist Offline.");
                    return true;
                } else if (zugriffe.contains(Objects.requireNonNull(Bukkit.getPlayer(person)).getUniqueId().toString())) {
                    player.sendMessage(PREFIX + "Es hat sich nichts geändert, da der Spieler schon die Erlaubnis hat.");
                    return true;
                } else {
                    zugriffe.add(Objects.requireNonNull(Bukkit.getPlayer(person)).getUniqueId().toString());
                    konten.set(path + konto + ".zugriff", zugriffe);
                    konten.saveConfig();
                    player.sendMessage(PREFIX + "Dem Spieler " + person + "wurde der Zugriff auf das Konto erteilt");
                    return true;
                }
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
                    player.sendMessage(PREFIX + "Der Spieler existiert nicht oder ist Offline!");
                    return true;
                } else {
                    List<String> zugriffe;
                    zugriffe = konten.getStringList(path + konto + ".zugriff");
                    zugriffe.remove(Objects.requireNonNull(Bukkit.getPlayer(person)).getUniqueId().toString());
                    konten.set(path + konto + ".zugriff", zugriffe);
                    konten.saveConfig();
                    player.sendMessage(PREFIX + "Dem Spieler " + person + "wurde der Zugriff auf das Konto genommen");
                    return true;
                }
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
                        player.sendMessage(PREFIX + "Der Kontostand beträgt nur" + kontostand + "$FP. Versenden nicht möglich!");
                        return true;
                    } else if (!berechtigung.contains(player.getUniqueId().toString())) {
                        player.sendMessage(PREFIX + "Du hast keine Berechtigung für das Konto namens:" + konto);
                        return true;
                    }
                    //Durchführung
                    kontoRemoveMoney(konto, menge);
                    kontoAddMoney(konto1, menge);
                    player.sendMessage(PREFIX + "Das versenden von " + menge + "$FP war erfolgreich! Das konto hat noch " +
                            kontoGetMoney(konto) + "$FP.");
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
                        player.sendMessage(PREFIX + "Das konto hat nicht genug Geld.");
                        return true;
                    } else if (!berechtigung.contains(player.getUniqueId().toString())) {   //wenn keine Berechtigung
                        player.sendMessage(PREFIX + "Du hast keine Berechtigung für das Konto namens:" + konto);
                        return true;
                    } else {
                        MoneySystem.removeMoney(spieler1.getUniqueId().toString(), menge);
                        kontoRemoveMoney(konto, menge);
                        player.sendMessage(PREFIX + "Das versenden von " + menge + "$FP war erfolgreich! Das konto hat noch " +
                                kontoGetMoney(konto) + "$FP.");
                        return true;
                    }
                }
            }
            default:   //FEHLER------------------------------------------------------------------------------------------------
                player.sendMessage(PREFIX + "Command falsch genutzt gebe /MoneyPlugin help ein!");
                break;
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
    public void kontoAddMoney(String kontoName, int wert) {
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
    public int kontoGetMoney(String kontoName) {
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
}