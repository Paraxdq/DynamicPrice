package de.paradox.confighandler;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.paradox.DynamicPrice;

public class ConfigAccessor {
	private DynamicPrice plugin;
	private String fileName;
	private File config;
	public FileConfiguration fileConfig;
	
	public ConfigAccessor(DynamicPrice plugin, String fileName){
		this.plugin = plugin;
		this.fileName = fileName;
		
		this.config = new File(plugin.getDataFolder(), fileName);
		
		saveDefaultConfig();
		loadConfig();
	}

	public void saveDefaultConfig(){
		if (!this.config.exists()) {
			this.plugin.saveResource(this.fileName, false);
		}
	}
	
	private void loadConfig(){
		if(config == null) {
			this.config = new File(plugin.getDataFolder(), fileName);
		}

		this.fileConfig = YamlConfiguration.loadConfiguration(config);
	}
	
	public void reloadConfig() {
		this.fileConfig = YamlConfiguration.loadConfiguration(config);
	}
	
	public void saveConfig(){
		try {
			this.fileConfig.save(config);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
