package com.mcconquest.conquest;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Conquest extends JavaPlugin {
    public static FileConfiguration conf;
	public static Conquest plugin;
	public static final Logger log = Logger.getLogger("Minecraft");
	static HashMap<String, Long> combat = new HashMap<String, Long>();
    private Settings settings;
    private CommandHandler cH;
    private ConquestHandler cqH;
    private PlayerListener pL;
    private Database database;



	
	public void onEnable() {
		plugin = this;
		
		if (!new File(getDataFolder(), "config.yml").exists()) {
    		saveDefaultConfig();
    	}
			Conquest.plugin = this;
	        registerClasses();
	        connectToDatabase();
	        try {
				Database.createDatabase();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        loadSettings();
	        cqH.loadAllPoints(null);
	        cqH.rewardsHandler();
	        log.info(getName() + " has been enabled.");
		
	}
	
    private void registerClasses()
    {
        PluginManager pluginManager = Bukkit.getPluginManager();
        this.cH = new CommandHandler(this);
        this.cqH = new ConquestHandler(this);
        this.pL = new PlayerListener(this);
        pluginManager.registerEvents(this.pL, plugin);
        getCommand("cq").setExecutor(this.cH);


    }
    private void connectToDatabase()
    {
        this.database = new Database(this);
    }

	private void loadSettings(){
        this.settings = new Settings(this);

	}
	public void onDisable() {
		
		log.info(getName() + " has been disabled.");
	}
	
	public CommandHandler getCommandHandler(){
		return this.cH;
	}
	public ConquestHandler getConquestHandler(){
		return this.cqH;
	}
	public PlayerListener getPlayerListener(){
		return this.pL;
		
	}
	public Settings getSettings(){
		return this.settings;
	}
	public Database getConquestDatabase(){
		return this.database;
	}
    public void Messaging(Player reciever, String message){
    	reciever.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "Conquest" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE + message);
    }
    public static Conquest getInstance()
    {
        return plugin;
    }
    
	
	
}