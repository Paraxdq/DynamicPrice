package de.paradox;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import de.paradox.confighandler.Config;
import de.paradox.confighandler.ConfigAccessor;
import de.paradox.confighandler.DatabaseAccessor;
import de.paradox.listener.PlayerListener;
import de.paradox.listener.SignListener;

public class DynamicPrice extends JavaPlugin {

	public ConfigAccessor configAccessor, langAccessor;
	public DatabaseAccessor dba;
    //public static Economy econ = null;
    public Economy econ = null;
    public Config config;
	public Prices prices;
	public Language lang;
	public PlayerListener playerListener;
	public SignListener signListener;
    public static Permission perms = null;
    public static Chat chat = null;
	public boolean debug;
    public static double minPercent, maxPercent;
    public static double percentDecrease, percentIncrease, buyMultiplicator;
    public static long signUpdateInterval, databaseSaveInterval, increasePriceInterval;
	public Signs signs;
	public BackgroundTasks bgt;
	
	/*
	 * PREISE SOLLEN NACH INTERVAL SINKEN / STEIGEN UND NICHT NUR STEIGEN WIE ES AKTUELL IST
	 * HELPER CLASS MIT ZAHLEN CHECK AUSSTATTEN FÜR ID UND VALUE, ID MIT "1555:42+" ERZEUGT INTERNAL ERROR
	 * DPRICE MIT ITEM ANZAHL AUSRÜSTEN /DP PRICE 4 25 ZEIGT PREIS FÜR 25 ITEMS AN
	 * PREISE NACH INTERVAL AUS DER MAP AUSLESEN UND ERHÖHEN, NICHT VON SCHILDER LOCATIONS ABHÄNGIG MACHEN!
	 * 
	 * 
	 */
	@Override
	public void onEnable(){
		if (!setupEconomy() ) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		
		try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();          
            System.out.println("Couldn't load the sqlite-JDBC driver.");
            return;
        }
		
		/*try {
			MultiverseCore mv = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("MultiverseCore");
	    	WorldManager wm = (WorldManager) mv.getCore().getMVWorldManager();
		} catch (Exception e2) {
			e2.printStackTrace();
		}*/
		
		/*if(this.getServer().getWorld("mining") != null) {
			System.out.println(this.getServer().getWorld("mining"));
			System.out.println(Bukkit.getWorld("mining"));
		} else {
			System.out.println("NULL");
		}*/
		
		/*int count = 50;
		double pc = 0.01;
		int price = 5;
		double summe = 0.0;
		
		for(int i = 0; i < count; i++) {
			System.out.println(price - (price * (i * pc)));
			summe = summe + (price - (price * (i * pc)));
			//System.out.println(summe);
		}
		
		System.out.println(summe + " von " + price * count);*/
		
		this.dba = new DatabaseAccessor(this);
		
		this.configAccessor = new ConfigAccessor(this, "config.yml");
		this.config = new Config(this);
		
		signUpdateInterval = this.config.getSignUpdateInterval() * 60L * 20L;
		databaseSaveInterval = this.config.getDatabaseSaveInterval() * 60L * 20L;
		increasePriceInterval = this.config.getIncreasePriceInterval() * 60L * 20L;
		
		minPercent = Double.parseDouble(this.config.getMinPercent()) / 100;
		maxPercent = Double.parseDouble(this.config.getMaxPercent()) / 100;
		percentDecrease = Double.parseDouble(this.config.getPercentDecrease()) / 100;
		percentIncrease = Double.parseDouble(this.config.getPercentIncrease()) / 100;
		
		buyMultiplicator = Double.parseDouble(this.config.getBuyMultiplicator())  / 100;
		
		//this.pricesAccessor = new ConfigAccessor(this, "prices.yml");
		this.langAccessor = new ConfigAccessor(this, this.config.getLang() + ".yml"); // FEHLERPRÜFUNG
		this.debug = this.config.getDebug();

		this.prices = new Prices(this);
		this.signs = new Signs(this);
		this.lang = new Language(this);
		
		this.playerListener = new PlayerListener(this);
		this.signListener = new SignListener(this);
		
		getServer().getPluginManager().registerEvents(this.playerListener, this);
		getServer().getPluginManager().registerEvents(this.signListener, this);
		
		getCommand("dp").setExecutor(new DynamicPriceCommandExecutor(this));
		
		//getServer().getPluginManager().registerEvents(new SignListener(), this); // register new Events
		
		//test2();

		this.bgt = new BackgroundTasks(this);
	}

	@Override
	public void onDisable(){ 
		try {
			dba.savePrices();
			dba.saveSigns();
			System.out.println("[DynamicPrices] Saved prices to database!");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("[DynamicPrices] Could not save prices to database!");
		}
	}
	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
	
	private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static final int N_ITERATIONS = 10;

    public static String testFinal() {
        final String a = "a";
        final String b = "b";
        return a + b;
    }

    public static String testNonFinal() {
        String a = "a";
        String b = "b";
        return a + b;
    }

    public void test() {
        long tStart, tElapsed;

        tStart = System.currentTimeMillis();
        for (int i = 0; i < N_ITERATIONS; i++)
            testFinal();
        tElapsed = System.currentTimeMillis() - tStart;
        System.out.println("Method with finals took " + tElapsed + " ms");

        tStart = System.currentTimeMillis();
        for (int i = 0; i < N_ITERATIONS; i++)
            testNonFinal();
        tElapsed = System.currentTimeMillis() - tStart;
        System.out.println("Method without finals took " + tElapsed + " ms");

    }
    
    /*public void test2() {
        long tStart, tElapsed;

        tStart = System.currentTimeMillis();
        for (int i = 0; i < N_ITERATIONS; i++)
            prices.getPrice(1, 0, true);
        tElapsed = System.currentTimeMillis() - tStart;
        System.out.println("Method with finals took " + tElapsed + " ms");

        tStart = System.currentTimeMillis();
        for (int i = 0; i < N_ITERATIONS; i++)
            prices.getPrice(1, 0, false);
        tElapsed = System.currentTimeMillis() - tStart;
        System.out.println("Method without finals took " + tElapsed + " ms");

    }*/
}

/*

start = System.currentTimeMillis();
    for (int i = 0; i < limit; i++) {
        int integer = Integer.parseInt(sint);
    }
    end = System.currentTimeMillis();

    System.out.println("parseInt took: " + (end - start));

*/
