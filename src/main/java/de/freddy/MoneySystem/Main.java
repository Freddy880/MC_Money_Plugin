package de.freddy.MoneySystem;

import de.freddy.MoneySystem.commands.*;
import de.freddy.MoneySystem.listener.JoinQuitListener;
import de.freddy.MoneySystem.singevent.SingEvents;
import de.freddy.MoneySystem.utils.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public static String PREFIX = config().getString("PREFIX");
    public static Main INSTANCE;

    public Main(){
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.register();
        log(config().getString("Plugin geladen"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        log("Plugin entladen");
    }

    public void log(String text) {
        Bukkit.getConsoleSender().sendMessage(PREFIX + text);
    }

    private void register() {
        //Eventlistener
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new JoinQuitListener(), this);
        pluginManager.registerEvents(new SingEvents(), this);

        //Commands
        Bukkit.getPluginCommand("heal").setExecutor(new HealCommand());
        Bukkit.getPluginCommand("Spawn").setExecutor(new SpawnCommand());
        Bukkit.getPluginCommand("money").setExecutor(new MoneySystem());
        Bukkit.getPluginCommand("sendmoney").setExecutor(new moneyforCommandBlock());
        Bukkit.getPluginCommand("konto").setExecutor(new KontoSystem());
        Bukkit.getPluginCommand("ping").setExecutor(new PingCommand());
        Bukkit.getPluginCommand("message").setExecutor(new Message());
    }
    public static FileConfig config() {
        return new FileConfig("MoneyInfo", "config.yml");
    }

}