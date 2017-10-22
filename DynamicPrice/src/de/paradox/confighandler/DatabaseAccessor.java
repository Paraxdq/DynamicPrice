package de.paradox.confighandler;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import de.paradox.DynamicPrice;
import de.paradox.Helper;
import de.paradox.Prices;
import de.paradox.Signs;

public class DatabaseAccessor {
	
	private DynamicPrice plugin;
	public Connection db;
	public static boolean priceUpdate;

	public DatabaseAccessor(DynamicPrice plugin) {
		this.plugin = plugin;
		priceUpdate = false;
		
		File f = new File(this.plugin.getDataFolder(), "DynamicPrice.db");
		
		if(!f.exists()) { // TODO !exists
			createDatabase();
		}
		
		if(!getConnection()) {
            System.out.println("[DynamicPrice] Failed to get the connection with the DynamicPrice.db file.");
			Bukkit.getPluginManager().disablePlugin(this.plugin);
		}
	}
	
	private boolean getConnection() {    
        Connection connection = null;      
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + this.plugin.getDataFolder() + "/DynamicPrice.db");
        } catch (SQLException e1) {
            e1.printStackTrace();
            return false;
        }      
        
        db = connection;
        
        try {
			db.setAutoCommit(false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return true;
	}
	
	private void createDatabase() {
		
		plugin.saveResource("DynamicPrice.db", true); // TODO false
		
		/*Statement stmt = null;
		try {
			stmt = this.db.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("[DynamicPrice] Failed to create Statement for database creation.");
		}
		
		String sql = "CREATE TABLE PRICES " +
				"(ID int," +
				"SUBID int," +
				"NAME VARCHAR(25)," +
				"PRICE DOUBLE)";
		
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sql = "CREATE UNIQUE INDEX IPRICES " +
				"on PRICES(ID, SUBID)";
		
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	public double getPrice(int id, int subID, boolean b) {
		String sql = "SELECT price, percent FROM PRICES WHERE ID = " + id + " AND SUBID = " + subID;
		
		try {
			Statement s = db.createStatement();
			ResultSet rs = s.executeQuery(sql);
			double price = 0.0;
			
			if(!rs.next()) {
				return 0.0;
			}
			
			if(b) { // RETURN STANDARD PRICE
				price = rs.getDouble("price");
			} else {
				price = rs.getDouble("price") * (rs.getDouble("percent") / 100);
			}
			
			return price;
			
			
		} catch (SQLException e) {
			System.out.println("[DynamicPrice] Error in db.getPrice function.");
			e.printStackTrace();
		}
		
		return 0.0;
	}
	
	public void setPrice(int id, int subID, double price) {
		String sql = "UPDATE PRICES SET price = " + price + " WHERE ID = " + id + " AND SUBID = " + subID;
		
		try {
			Statement s = db.createStatement();
			s.execute(sql);
			if(s.getUpdateCount() == 0) {
				sql = "INSERT INTO PRICES (ID, SUBID, PRICE, PERCENT) VALUES (" + id + "," + subID + "," + price + ", 100)";
				s.execute(sql);
			}
		} catch (SQLException e) {
			System.out.println("[DynamicPrice] Error in db.setPrice function.");
			e.printStackTrace();
		}
	}
	
	public double getPercent(int i, int j) {
		String sql = "SELECT percent FROM PRICES WHERE ID = " + i + " AND SUBID = " + j;

		try {
			Statement s = db.createStatement();
			ResultSet rs = s.executeQuery(sql);
			
			return rs.getDouble("percent");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0.0;
	}
	
	public void setPercent(int i, int j, double p) {
		String sql = "UPDATE PRICES SET percent = " + p + " WHERE ID = " + i + " AND SUBID = " + j;
		
		executeStatement(sql, true);
	}
	
	private ResultSet executeStatement(String sql, boolean update) {
		Statement s;
		ResultSet rs;
		
		try {
			if(!update) {
				//select
				s = db.createStatement();
				rs = s.executeQuery(sql);

				return rs;
			} else {
				//update
				s = db.createStatement();
				s.execute(sql);
				
				return null;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public HashMap<String, double[]> loadPrices() {
		HashMap<String, double[]> t = new HashMap<String, double[]>();
		String sql = "SELECT * FROM PRICES";
		ResultSet rs;
		try {
			rs = this.db.createStatement().executeQuery(sql);
			
			//ResultSetMetaData md = rs.getMetaData();
			
			while(rs.next()) {
				t.put(rs.getString("ID") + ":" + rs.getString("SUBID"), new double[] {rs.getDouble("PRICE"), rs.getDouble("PERCENT")});
			}
			
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("[DynamicPrice] Could not load item prices from database.");
		}
		
		return t;
	}
	
	public void savePrices() throws SQLException {
		HashMap<String, double[]> t = Prices.getHashMap();
		
		if(!priceUpdate) {
			return;
		} else {
			priceUpdate = false;
		}
		
		for(Entry<String, double[]> entry : t.entrySet()) {
		    String id = entry.getKey();
		    double[] pp = entry.getValue();
		    
		    int[] sid = Helper.splitID(id);
		    
			db.createStatement().execute("INSERT OR REPLACE INTO PRICES VALUES (" + sid[0] + ", " + sid[1] + ", " + pp[0] + ", " + pp[1] + ");");

		}
		
		db.commit();
	}
	
	public HashMap<String, Location> loadSigns(){
		HashMap<String, Location> t = new HashMap<String, Location>();
		String sql = "SELECT HASH, LOCATION FROM SIGNS";
		ResultSet rs;
		try {
			rs = this.db.createStatement().executeQuery(sql);
			
			while(rs.next()) {
				String[] Sloc = rs.getString("LOCATION").split(",");
				
				
				//System.out.println("Plugin World: " + plugin.getServer().getWorld(Sloc[0]));
				//System.out.println("Bukkit World: " + Bukkit.getWorld(Sloc[0]));
				
				Location loc = new Location(plugin.getServer().getWorld(Sloc[0]), Double.valueOf(Sloc[1]), Double.valueOf(Sloc[2]), Double.valueOf(Sloc[3]));
				
				//System.out.println(rs.getString("HASH"));
				//System.out.println(loc);
				
				t.put(rs.getString("HASH"), loc);
			}
			
			System.out.println("[DynamicPrice] Successfully loaded " + t.values().size() + " Signs.");
			
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("[DynamicPrice] Could not load sign locations from database.");
		}
		
		return t;
	}
	
	public void saveSigns() throws SQLException {
		HashMap<String, Location> t = Signs.getHashMap();

		for(Entry<String, Location> entry : t.entrySet()) {
		    String hash = entry.getKey();
		    Location loc = entry.getValue();
		    
		    if(loc == null || entry.getValue().getWorld() == null) {
		    	//System.out.println("Location was null, deleting!");
		    	//System.out.println("DELETE FROM SIGNS WHERE HASH ='" + hash + "';");
		    	db.createStatement().execute("DELETE FROM SIGNS WHERE HASH ='" + hash + "';");
		    } else {
			    //System.out.println("Location not null, saving sign");-
		    	String Sloc = entry.getValue().getWorld().getName() + "," + entry.getValue().getBlockX() + "," + entry.getValue().getBlockY() + "," + entry.getValue().getBlockZ();
		    	//System.out.println("INSERT OR REPLACE INTO SIGNS(HASH, LOCATION) VALUES ('" + hash + "', '" + Sloc + "');");
				db.createStatement().execute("INSERT OR REPLACE INTO SIGNS(HASH, LOCATION) VALUES ('" + hash + "', '" + Sloc + "');");// +  + ", " + pp[1] + ");");
		    }

		}
		
		db.commit();
	}
}
