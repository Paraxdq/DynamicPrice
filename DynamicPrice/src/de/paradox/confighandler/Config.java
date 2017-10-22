package de.paradox.confighandler;

import de.paradox.DynamicPrice;

public class Config {

	private DynamicPrice plugin;
	private ConfigAccessor config;

	public Config(DynamicPrice plugin) {
		this.plugin = plugin;
		this.config = plugin.configAccessor;
	}
	
	// TODO DATEN MÜSSEN BEIM RELOADEN NEU EINGELESEN WERDEN.
	
	public String getLang() {
		return this.config.fileConfig.getString("language");
	}
	
	public boolean getDebug() {
		return Boolean.valueOf(this.config.fileConfig.getString("debug"));
	}
	
	public long getSignUpdateInterval() {
		return this.config.fileConfig.getLong("sign-update-interval");
	}
	
	public long getDatabaseSaveInterval() {
		return this.config.fileConfig.getLong("database-save-interval");
	}

	public long getIncreasePriceInterval() {
		return this.config.fileConfig.getLong("increase-price-over-time-interval");
	}
	
	public String getMinPercent() {
		return this.config.fileConfig.getString("min-percent");
	}
	
	public String getMaxPercent() {
		return this.config.fileConfig.getString("max-percent");
	}
	
	public String getPercentDecrease() {
		return this.config.fileConfig.getString("percent-decrease-on-sell");
	}
	
	public String getPercentIncrease() {
		return this.config.fileConfig.getString("percent-increase-on-buy");
	}
	
	public String getPercentIncreaseOverTime() {
		return this.config.fileConfig.getString("percent-increase-over-time");
	}
	
	public String getBuyMultiplicator() {
		return this.config.fileConfig.getString("buy-multiplicator");
	}
}
