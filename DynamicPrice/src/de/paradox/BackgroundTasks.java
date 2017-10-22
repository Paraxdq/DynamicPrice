package de.paradox;

import java.sql.SQLException;

import org.bukkit.ChatColor;

import de.paradox.confighandler.DatabaseAccessor;

public class BackgroundTasks {
	
	private Signs signs;
	private DatabaseAccessor dba;
	private Prices prices;
	private DynamicPrice plugin;
	private long increasePriceInterval, signUpdateInterval, databaseSaveInterval;

	public BackgroundTasks(final DynamicPrice plugin) {
		this.plugin = plugin;
		this.signs = plugin.signs;
		this.dba = plugin.dba;
		this.prices = plugin.prices;
		
		this.increasePriceInterval = DynamicPrice.increasePriceInterval;
		this.signUpdateInterval = DynamicPrice.signUpdateInterval;
		this.databaseSaveInterval = DynamicPrice.databaseSaveInterval;
		
		startIncreasePriceTask();
		startSignUpdateTask();
		startDatabaseSaveTask();
	}
	
	private void startIncreasePriceTask() {
		if(this.increasePriceInterval != 0L) {
			this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				public void run() {increasePrices();if(plugin.debug)System.out.println("[DynamicPrice] Increased prices.");}}, 
					this.increasePriceInterval, this.increasePriceInterval);
		} else {
			System.out.println("[DynamicPrice] Price-increase-interval deactivated.");
		}
		
		if(plugin.debug) {
			System.out.println("[DynamicPrice] IncreasePriceTask started. Interval: " + this.increasePriceInterval);
		}
	}
	
	private void startSignUpdateTask() {
		if(this.signUpdateInterval != 0L) {
			plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				public void run() {updateSigns();if(plugin.debug)System.out.println("[DynamicPrice] Updated signs.");}}, 
					this.signUpdateInterval + 40L, this.signUpdateInterval);
		} else {
			System.out.println("[DynamicPrice] Sign-update-interval deactivated.");
		}
		
		if(plugin.debug) {
			System.out.println("[DynamicPrice] SignUpdateTask started. Interval: " + this.signUpdateInterval);
		}
	}
	
	private void startDatabaseSaveTask() {
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {saveToDatabase();if(plugin.debug)System.out.println("[DynamicPrice] Saved to database.");}}, 
				this.databaseSaveInterval != 0L ? this.databaseSaveInterval + 40L : 2440L, this.databaseSaveInterval != 0L ? this.databaseSaveInterval: 1200L);
		
		if(plugin.debug) {
			System.out.println("[DynamicPrice] DatabaseSaveTask started. Interval: " + this.databaseSaveInterval);
		}
	}
	
	private void updateSigns() {
		signs.updateAllSigns();
	}
	
	private void saveToDatabase() {
		try {
			dba.savePrices();
			dba.saveSigns();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("[DynamicPrices] Could not save prices to database!");
		}
	}
	
	private void increasePrices() {
		prices.increasePercent();
	}
	
	public void restartTasks() {
		this.plugin.getServer().getScheduler().cancelAllTasks();
		
		this.increasePriceInterval = DynamicPrice.increasePriceInterval;
		this.signUpdateInterval = DynamicPrice.signUpdateInterval;
		this.databaseSaveInterval = DynamicPrice.databaseSaveInterval;
		
		System.out.println("PriceInterval: " + this.increasePriceInterval);
		System.out.println("SignInterval: " + this.signUpdateInterval);
		System.out.println("DatabaseInterval: " + this.databaseSaveInterval);
		
		startIncreasePriceTask();
		startSignUpdateTask();
		startDatabaseSaveTask();
	}
}
