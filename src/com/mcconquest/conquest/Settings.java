package com.mcconquest.conquest;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Settings {

    private Conquest plugin;

    public Settings(Conquest instance)
    {
        this.plugin = instance;
        System.out.println("Loading settings for Conquest");
        
        
    }

    public ConfigurationSection getSection()
    {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection("Settings");
    	return section;
    }


	/** returns conquest time */
	public int getConquestTime()
	{
		try
		{
			return getSection().getInt("conquest-time-in-seconds");
		}
		catch (Exception e)
		{
			Conquest.log.severe("There was an error loading your conquest time in config!");
			e.printStackTrace();
			return 60; // default 5s
		}
	}
	/** returns rewards period */
	public int getRewardsTimer()
	{
		try
		{
			return getSection().getInt("rewards-period-in-seconds");
		}
		catch (Exception e)
		{
			Conquest.log.severe("There was an error loading your rewards timer in config!");
			e.printStackTrace();
			return 60; // default 5s
		}
	}
	/** returns min players until cPoint is lost */
	public int getMinOnlinePLayers()
	{
		try
		{
			return getSection().getInt("minimum-online-players");
		}
		catch (Exception e)
		{
			Conquest.log.severe("There was an error loading your minOnlinePlayers in config!");
			e.printStackTrace();
			return 1; // default 5s
		}
	}

}
