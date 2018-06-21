package com.garthi.timebanmod.utilities;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

/**
 * @author Martin "Garth" Zander <garth@new-crusader.de>
 * @package BanMod
 */
public class ConfigHelper
{
    private static ConfigHelper instance;
    
    private static String CONFIG_CATEGORY_BANNED = "banned";
    private static String CONFIG_CATEGORY_CONFIG = "config";
    private static String BANNED_MINUTES_DESCRIPTION = "Banned for %d minutes";
    
    private Configuration config;
    
    private String player;
    
    private ConfigHelper(Configuration config)
    {
        this.config = config;
        
        configureConfig();
    }

    public static ConfigHelper init(File configFile)
    {
        Configuration configuration = new Configuration(configFile);
        instance = new ConfigHelper(configuration);
        
        return instance;
    }
    
    public static ConfigHelper player(String player) throws NotLoadedException
    {
        if (instance == null) {
            throw new NotLoadedException("Config Helper have no instance");
        }
        
        instance.player = player;
        
        return instance;
    }

    public boolean isDeathBanTimeActive()
    {
        Configuration configuration = getConfig();

        if (!configuration.hasKey(CONFIG_CATEGORY_CONFIG, "enable")) {
            return false;
        }

        Property config = configuration.get(CONFIG_CATEGORY_CONFIG, "enable", false);

        return config.getBoolean();
    }

    public int getDeathBanTime()
    {
        Configuration configuration = getConfig();
        
        if (!configuration.hasKey(CONFIG_CATEGORY_CONFIG, "bantime")) {
            return 0;
        }

        Property config = configuration.get(CONFIG_CATEGORY_CONFIG, "bantime", 0);
        
        return config.getInt(); 
    }
    
    public Boolean isBanned()
    {
        Configuration configuration = getConfig();
        
        // exist entity
        if (!configuration.hasKey(CONFIG_CATEGORY_BANNED, this.player)) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        Property playerConfig = configuration.get(CONFIG_CATEGORY_BANNED, this.player, "0");
        
        if (!playerConfig.getString().equals("0") && Long.parseLong(playerConfig.getString()) > currentTime) {
            // is current banned
            return true;
        }

        // remove the entity from config file
        this.remove();
        
        return false;
    }
    
    public void add(int minutes)
    {
        Configuration configuration = getConfig();

        long bannedEndTime = System.currentTimeMillis() + (minutes * 60 * 1000); 
        
        // add the entity
        configuration.get(
                CONFIG_CATEGORY_BANNED,
                this.player,
                (bannedEndTime + ""),
                String.format(BANNED_MINUTES_DESCRIPTION, minutes)
        );

        configuration.save();
    }
    
    public String reason()
    {
        Configuration configuration = getConfig();
        Property playerConfig = configuration.get(CONFIG_CATEGORY_BANNED, this.player, 0);
        
        return playerConfig.getComment();
    }
    
    private void remove()
    {
        String REMOVE_CATEGORY = "remove";
        
        Configuration configuration = getConfig();

        // exist entity
        if (!configuration.hasKey(CONFIG_CATEGORY_BANNED, this.player)) {
            return;
        }
        
        // remove entity
        configuration.addCustomCategoryComment(REMOVE_CATEGORY, "remove from banned list");
        configuration.moveProperty(CONFIG_CATEGORY_BANNED, this.player, REMOVE_CATEGORY);
        configuration.removeCategory(configuration.getCategory(REMOVE_CATEGORY));
        
        configuration.save();
    }
    
    private Configuration getConfig()
    {
        this.config.load();
        return this.config;
    }
    
    private void configureConfig()
    {
        Configuration configuration = getConfig();

        // add config category
        configuration.addCustomCategoryComment(CONFIG_CATEGORY_CONFIG, "Death ban configuration");
        configuration.get(CONFIG_CATEGORY_CONFIG, "bantime", "5", "time in minutes for death ban");
        configuration.get(CONFIG_CATEGORY_CONFIG, "enable", false, "death ban active = true ot not active = false");

        configuration.addCustomCategoryComment(CONFIG_CATEGORY_BANNED, "list of time banned players");
        
        configuration.save();

        // add a sample entity
        this.player = "SamplePlayer";
        this.add(5);
    }
}
