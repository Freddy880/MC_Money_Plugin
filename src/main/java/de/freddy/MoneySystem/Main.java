package de.freddy.MoneySystem;

import de.freddy.MoneySystem.commands.*;
import de.freddy.MoneySystem.listener.JoinQuitListener;
import de.freddy.MoneySystem.singevent.SingEvents;
import de.freddy.MoneySystem.utils.CreateConfig;
import de.freddy.MoneySystem.utils.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

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
        File file = new File("plugins/MoneyInfo/config.yml");
        if (!file.exists()){
            CreateConfig.makeConfig();
        }
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
        Objects.requireNonNull(Bukkit.getPluginCommand("heal")).setExecutor(new HealCommand());
        Objects.requireNonNull(Bukkit.getPluginCommand("Spawn")).setExecutor(new SpawnCommand());
        Objects.requireNonNull(Bukkit.getPluginCommand("money")).setExecutor(new MoneySystem());
        Objects.requireNonNull(Bukkit.getPluginCommand("sendmoney")).setExecutor(new moneyforCommandBlock());
        Objects.requireNonNull(Bukkit.getPluginCommand("konto")).setExecutor(new KontoSystem());
        Objects.requireNonNull(Bukkit.getPluginCommand("ping")).setExecutor(new PingCommand());
    }
    public static FileConfig config() {
        return new FileConfig("MoneyInfo", "config.yml");
    }

}
