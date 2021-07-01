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


package de.freddy.MoneySystem.singevent;

import de.freddy.MoneySystem.commands.MoneySystem;
import de.freddy.MoneySystem.utils.FileConfig;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class SingEvents implements Listener {
    FileConfig prices = new FileConfig("MoneyInfo","prices.yml");

    @EventHandler
    public void onSingChange(SignChangeEvent e) {
        //Überprüfung, ob jemand die rechte hat
        if ((e.getLine(0).equalsIgnoreCase("[Buy]") || e.getLine(0).equalsIgnoreCase("[Sell]")) && !(e.getPlayer().hasPermission("de.freddysMoney.buysing"))) {
            e.getPlayer().sendMessage(MoneySystem.PREFIX + "Du hast keine Berechtigung ein Kaufschild zu machen.");
            return;
        }
        //Kontrolle der Schilder
        if(e.getLine(0).equalsIgnoreCase("[Buy]")) {
            e.setLine(0,"§a§l[Buy]");
            String path = "Preise." + e.getLine(1);
            if(!prices.contains(path)) {
                //Wen der Pfad nicht vorhanden: vorberreitung der Config
                prices.set(path + ".preis",Integer.valueOf(e.getLine(2)));
                prices.set(path + ".itemid", "ITEMID");
                prices.set(path + ".menge", 1);
                prices.saveConfig();
                e.getPlayer().sendMessage(MoneySystem.PREFIX + "In der Config \"prices.yml\" wurde ein neuer Pfad Hinzugefügt. Hier bitte die Konfiguration übernehmen.");
            }
            e.setLine(1,"Item: " + e.getLine(1));
            e.setLine(2,"Preis: " + prices.getString(path + ".preis") + "$");
        }
        if(e.getLine(0).equalsIgnoreCase("[Sell]")) {         //Verkaufsschild
            //Setzen der Texte
            e.setLine(0,"§a§l[Sell]");
            String path = "verkauf." + e.getLine(1);
            if(!prices.contains(path)) {
                //Wen der Pfad nicht vorhanden: vorberreitung der Config
                prices.set(path + ".preis",Integer.valueOf(e.getLine(2)));
                prices.set(path + ".itemid", "ITEMID");
                prices.set(path + ".menge", 1);
                prices.saveConfig();
                e.getPlayer().sendMessage(MoneySystem.PREFIX + "In der Config \"prices.yml\" wurde ein neuer Pfad Hinzugefügt. Hier bitte die Konfiguration übernehmen.");
            }
            //Setzen der Texte
            e.setLine(1,"Item: " + e.getLine(1));
            e.setLine(2,"Preis: " + prices.getString(path + ".preis") + "$");
        }
    }
    @EventHandler
    //Wenn Spieler mit schilder Interagiert
    public void onPlayerInteractWithSing(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if (e.getClickedBlock().getState() instanceof Sign) {
                Sign sign = (Sign) e.getClickedBlock().getState();
                //KAUFEN------------------------------------------------------------------------------------------------
                if (sign.getLine(0).equalsIgnoreCase("§a§l[Buy]")) {
                    String[] itemText = sign.getLine(1).split(" "); //Item für pfad
                    String item = itemText[1];
                    String path = "Preise." + item; //Pfad zum lesen der Preise
                    String itemID = prices.getString(path + ".itemid"); //ItemID
                    int preis = prices.getInt(path + ".preis"); //Preis Abfrage
                    int amount = prices.getInt(path + ".menge");
                    Material material = Material.getMaterial(itemID); //Item ID zu Material.item
                    if (material == null) {
                        //Falls kein Item gefunden
                        p.sendMessage(MoneySystem.PREFIX + "Ups... Anscheinend ist etwas falsch gelaufen. Bitte gebe " +
                                "folgenden Error an die Admins: material == null -> zugewiesene ID in Config falsch ");
                        return;
                    }
                    //Item den Spieler geben
                    if (amount + p.getInventory().getItemInHand().getAmount()> 64) { //Test ob genug platz in Hand
                        p.sendMessage(MoneySystem.PREFIX + "Deine Hand hat keinen Platz, für alle Items, die gekauft werden sollen.");
                        return;
                    }
                    if (!(p.getInventory().getItemInHand().getType() == material || p.getInventory().getItemInHand().getType().isAir())) {  //Test ob die Hand frei ist bzw. gleiche Item
                        p.sendMessage(MoneySystem.PREFIX + "Deine Hand muss leer sein oder das zu kaufende Item halten.");
                        return;
                    }
                    //Test ob geld reicht
                    if (MoneySystem.getMoney(p.getUniqueId().toString()) >= preis) {
                        MoneySystem.removeMoney(p.getUniqueId().toString(), preis);
                        p.getInventory().addItem(new ItemStack(material, amount));
                        p.sendMessage(MoneySystem.PREFIX + "Der Kauf von \"" + item + "\"in höhe von "
                                + preis + "$ war erfolgreich.");
                    } else {
                        p.sendMessage(MoneySystem.PREFIX + "Du hast nicht genug geld für den Kauf. Dein Kontostand " +
                                "beträgt nur " + MoneySystem.getMoney(p.getUniqueId().toString()) + "$");
                    }
                }else if (sign.getLine(0).equalsIgnoreCase("§a§l[Sell]")) {
                    //TODO: Make this Controll if item in inventory price usw.
                    //Item bekommen + Anzahl + Pfad in der yml. Datei
                    String[] line1 = sign.getLine(1).split(" ");
                    String path = "verkauf." + line1[1];
                    Material item = Material.getMaterial(Objects.requireNonNull(prices.getString(path + ".itemid")));
                    int amount = prices.getInt(path +".menge");
                    int preis = prices.getInt(path + ".preis");
                    //Testet, ob das Item in der Hand des spielers ist
                    if(p.getItemInHand().getType() != item) {
                        p.sendMessage(MoneySystem.PREFIX + "Du hast das Entsprechende Item nicht in der Hand!");
                    }else if(!(p.getItemInHand().getAmount() >= amount)){       //Testet, ob der Spieler genug von dem Item hat.
                        p.sendMessage(MoneySystem.PREFIX + "Du hast die Entsprechende Menge des Items in der Hand!");
                    }else if(item == null){
                        p.sendMessage(MoneySystem.PREFIX + "Ups... Anscheinend ist etwas falsch gelaufen. Bitte gebe " +
                                "folgenden Error an die Admins: material == null -> zugewiesene ID in Config falsch ");
                    }else{
                        p.getInventory().removeItem(new ItemStack(item , amount));
                        MoneySystem.addMoney(p.getUniqueId().toString(),preis);
                        p.sendMessage(MoneySystem.PREFIX + "Der Verkauf von dem Item in Höhe von " + preis + "$ war erfolgreich!");
                    }
                }
            }
        }
    }
}