package de.freddy.MoneySystem.commands;

import de.freddy.MoneySystem.Main;
import de.freddy.MoneySystem.utils.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;

public class moneyforCommandBlock implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        FileConfig konten = new FileConfig("MoneyInfo", "money.yml");
        Player nearPlayer = null;
        if (sender instanceof Player) {
            sender.sendMessage(MoneySystem.PREFIX + "Sorry, aber du hast keine Berechtigung für diesen Command . Frage " +
                    "einen Admin dir den entsprechenden Commandblock zu platzieren.");
            return false;
        }
        BlockCommandSender block = (BlockCommandSender) sender;
        int preis = Integer.parseInt(args[0]);
        String konto = args[1];
        int x = Integer.parseInt(args[2]);
        int y = Integer.parseInt(args[3]);
        int z = Integer.parseInt(args[4]);
        Collection<Entity> near = block.getBlock().getLocation().getNearbyEntities(x, y, z);
        for (Entity entity : near) {
            if (entity instanceof Player) {
                nearPlayer = (Player) entity;
                if (near.size() == 0 || MoneySystem.getMoney(Objects.requireNonNull(nearPlayer).getUniqueId().toString()) < preis || !konten.contains("konten." + konto)) {
                    nearPlayer.sendMessage(MoneySystem.PREFIX + "Du hast nicht genug Geld um die Dienstleistung in Anspruch" +
                            "zu nehmen oder das Konto existiert nicht");
                    final Block commandBlock = ((BlockCommandSender) sender).getBlock();
                    final int bx = commandBlock.getX();
                    final int by = commandBlock.getY();
                    final int bz = commandBlock.getZ();
                    new BukkitRunnable() {
                        public void run() {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "data merge block " + bx + " " + by + " " + bz
                                    + " {SuccessCount:0b}");
                        }
                    }.runTask(Main.getPlugin(Main.class));
                    return true;
                }
                MoneySystem.removeMoney(nearPlayer.getUniqueId().toString(), preis);
                KontoSystem.kontoAddMoney(konto, preis);
                nearPlayer.sendMessage(MoneySystem.PREFIX + "Du hast eine Dienstleistung in Anspruch genommen! Der Betrag" +
                        "von " + preis + "$FP wurde an das Konto " + konto + " überwiesen. Bei Fehlern wende dich an" +
                        "einen Admin");
                return true;
            }
        }
        final Block commandBlock = ((BlockCommandSender) sender).getBlock();
        final int bx = commandBlock.getX();
        final int by = commandBlock.getY();
        final int bz = commandBlock.getZ();
        new BukkitRunnable() {
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "data merge block " + bx + " " + by + " " + bz
                        + " {SuccessCount:0b}");
            }
        }.runTask(Main.getPlugin(Main.class));
        return true;
    }
}
