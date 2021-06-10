package de.freddy.MoneySystem.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.FileNotFoundException;
import java.io.IOException;

public class FileConfig extends YamlConfiguration {

    private String path;

    public FileConfig(String folder, String filename) {
        this.path = "plugins/" + folder + "/" + filename;

        try {
            load(this.path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfig(String filename){
        this("LobbySystem", filename);
    }
    public void saveConfig() {
        try {
            save(this.path);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR beim SPEICHERN DER DATEI");
        }
    }
}
