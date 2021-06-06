package de.freddy.tutorial.commands;

import de.freddy.tutorial.utils.FileConfig;
import jdk.nashorn.internal.runtime.regexp.joni.ast.StringNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class KontoSystem implements CommandExecutor {

    public static final String PREFIX = "§4§lKonto §r§7§o";
    ArrayList<String> zugriff = new ArrayList<String>();
    String path = "konten.";

    /**
     * Mit dem Command kann man das Konto steuern
     *
     * @param sender Sender
     * @param command -
     * @param label -
     * @param args  -
     * @return  hat der Command funktioniert?
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        FileConfig konten = new FileConfig("MoneyInfo", "money.yml");
        //Wenn Kein Spieler, der den Command ausübt
        if (!(sender instanceof Player)){
            sender.sendMessage("Du bist kein Spieler");
            return true;
        }
        Player player = (Player) sender;
        //TODO: Konto evt. Command für genehmigung,
        // Überweisen
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
                System.out.println(menge + " " + player.getUniqueId().toString());
                kontoRemoveMoney(konto, menge);
                MoneySystem.addMoney(player.getUniqueId().toString(), menge);
                player.sendMessage(PREFIX + "Das abheben von " + menge + "$FP war erfolgreich! Das konto hat noch " +
                        kontoGetMoney(konto) + "$FP.");
                return true;
            }
            case "aufladen": {  //Aufladen des Kontos --------------------------------------------------
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
            case "überweisen" :{
                //TODO überweise vorgang von Konto zu Konto
            }
            default:   //FEHLER------------------------------------------------------------------------------------------------
                player.sendMessage("ERROR");
                break;
        }

        konten.saveConfig();
        return true;
    }
    /**
     * Setzt den Kontostand eines Kontos auf einen bestimmten wert
     * @param kontoName der Name des Kontos
     * @param wert Der neue Kontostand
     * @return boolean hats funktioniert?
     */
    public boolean kontoSetMoney(String kontoName, int wert){
        FileConfig konten = new FileConfig("MoneyInfo", "money.yml");
        int kontostand;
        if(!konten.contains(path + kontoName)){
            System.out.println(PREFIX + "[ERROR] kontoSetMoney: Das Konto existiert nicht");
            return false;
        }else {
            kontostand = wert;
            konten.set(path + kontoName + ".kontostand", kontostand);
            konten.saveConfig();
            return true;
        }
    }

    /**
     * Kontostand erhöhen.
     * @param kontoName name des Kontos
     * @param wert int Erhöhen des wertes um
     * @return funktioniert?
     */
    public boolean kontoAddMoney(String kontoName, int wert){
        FileConfig konten = new FileConfig("MoneyInfo", "money.yml");
        int kontostand;
        if(!konten.contains(path + kontoName)){
            System.out.println(PREFIX + "[ERROR] kontoAddMoney: Das Konto existiert nicht");
            return false;
        }else {
            kontostand = konten.getInt(path + kontoName + ".kontostand");
            kontostand += wert;
            konten.set(path + kontoName + ".kontostand", kontostand);
            konten.saveConfig();
            return true;
        }
    }

    /**
     * Getter für den Kontostand eines Kontos
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
     * @param kontoName Name des Kontos
     * @param wert  wert der runter soll
     */
    public void kontoRemoveMoney(String kontoName, int wert) {
        FileConfig konten = new FileConfig("MoneyInfo", "money.yml");
        int kontostand;
        if(!konten.contains(path + kontoName)){
            System.out.println(PREFIX + "[ERROR] kontoAddMoney: Das Konto existiert nicht");
        }else {
            kontostand = konten.getInt(path + kontoName + ".kontostand");
            kontostand -= wert;
            konten.set(path + kontoName + ".kontostand", kontostand);
            konten.saveConfig();
        }
    }
}
