package de.freddy.tutorial;

import de.freddy.tutorial.commands.*;
import de.freddy.tutorial.listener.JoinQuitListener;
import de.freddy.tutorial.singevent.SingEvents;
import de.freddy.tutorial.utils.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Tutorial extends JavaPlugin {

    public static String PREFIX = config().getString("PREFIX");
    public static Tutorial INSTANCE;

    public Tutorial(){
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
    }
    public static FileConfig config() {
        return new FileConfig("MoneyInfo", "config.yml");
    }

}
