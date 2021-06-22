package de.freddy.MoneySystem.utils;

public class CreateConfig {
    public static CreateConfig INSTANCE;
    public CreateConfig(){
        INSTANCE = this;
    }
    public static void makeConfig(){

        FileConfig config = new FileConfig("MoneyInfo", "config.yml");
        config.set("PREFIX","§l§sFreddyPlugin§r§7§o ");
        config.set("Plugin geladen","Das Plugin von Freddy880 wurde geladen");
        config.set("Plugin entladen", "Das Plugin von Freddy880 wurde entladen");
        config.set("Join Message", " ist beigetreten");
        config.set("quit Message"," ist abgehauen");
        config.saveConfig();
    }
}
