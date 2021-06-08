package de.freddy.money_system.commands;

import de.freddy.money_system.Main;
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

public class moneyforCommandBlock implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player nearPlayer = null;
        if(sender instanceof Player){
            sender.sendMessage(MoneySystem.PREFIX + "Sorry, aber du hast keine Berechtigung für disen Command . Frage "+
                    "einen Admin dir den entsprechenden Command Block zu plazieren.");
            return false;
        }
        if (label.equals("sendmoney")){
            BlockCommandSender block = (BlockCommandSender) sender;
            int preis = Integer.parseInt(args[0]);
            Collection<Entity> near = block.getBlock().getLocation().getNearbyEntities(0,3 ,0);
            for(Entity entity : near) {
                if(entity instanceof Player) {
                    nearPlayer = (Player) entity;
                    nearPlayer.sendMessage("Moin");
                }
            }
            if(near.size() == 0){
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
            }
        }
                return true;
    }

}
