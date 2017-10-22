package de.paradox;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import de.paradox.confighandler.DatabaseAccessor;

public class Signs {
	
	private static HashMap<String, Location> smap = new HashMap<String, Location>();
	private DynamicPrice plugin;
	private Prices prices;
	private DatabaseAccessor dba;
	
	public Signs(DynamicPrice plugin) {
		this.plugin = plugin;
		this.prices = plugin.prices;
		this.dba = plugin.dba;
		
		smap = dba.loadSigns();
	}
	
	public void updateAllSigns() {
		Iterator<Location> it = smap.values().iterator();
		
		while(it.hasNext()) {
			Location loc = it.next();
			
			if(loc == null) {
				if(plugin.debug)
					System.out.println("loc is null");
				
				continue;
			}
			if(loc.getWorld() == null) {
				if(plugin.debug) {
					System.out.println(loc.toString());
					System.out.println("Loc World is null");	
				}		
				continue;
			}
			
			Sign s = null;
			
			try {
				s = (Sign) loc.getBlock().getState();
				
				if(!s.getLocation().getChunk().isLoaded()) {
					if(s.getLocation().getChunk().load()) {
						if(plugin.debug)
							System.out.println("Chunk loaded at location " + loc.toString());
					}
				}
			} catch (Exception ex) {
				if(plugin.debug) {
					ex.printStackTrace();
					System.out.println("[DynamicPrice] Missing Sign at Location: " + loc.toString() + " going to remove...");
				}
				
				for(Entry<String, Location> entry : smap.entrySet()) {
				    String hash = entry.getKey();
				    Location delLoc = entry.getValue();
				    
				    if(delLoc == null) {
				    	continue;
				    }
				    
				    if(getUniqueHash(delLoc).equals(getUniqueHash(loc))) {
				    	//System.out.println("HashCode " + delLoc.toString() + " equal to " + loc.toString());
				    	this.removeSign(hash);
				    }
				}
				continue;
			}
			
			final int[] id = Helper.splitID(this.getSignItemID(s.getLine(1)));
			int amount = 0;
			
			try {
				amount = this.getSignItemAmount(s.getLine(1));
			} catch (Exception ex) {
				ex.printStackTrace();
				continue;
			}
			final double price = prices.getPrice(id[0], id[1], false) * amount;
			
			if(plugin.debug) {
				System.out.println("[DynamicPrice] Updated Sign with ID: " + Helper.combineID(id[0], id[1], true) + " at Location: " + s.getBlock().getLocation().toString());
			}
			
			this.setSignSellPrice(s, price);
			this.setSignBuyPrice(s, price * DynamicPrice.buyMultiplicator);
		}
	}
	
	/*public void saveSigns() {
		String abc = "";
		
		for (Entry<String, Location> entry : smap.entrySet()){
		    System.out.println(entry.getKey() + "/" + entry.getValue());
		    
		    abc = entry.getValue().getWorld().getName() + "," + entry.getValue().getBlockX() + "," + entry.getValue().getBlockY() + "," + entry.getValue().getBlockZ();
		}
		
		System.out.println(abc);
		String[] sabc = abc.split(",");
		
		Location loc = new Location(plugin.getServer().getWorld(sabc[0]), Double.valueOf(sabc[1]), Double.valueOf(sabc[2]), Double.valueOf(sabc[3]));
		
		System.out.println(loc);
	}
	
	public void loadSigns() {
		
	}*/
	
	public void addSign(String i, Location loc) {
		smap.put(i, loc);
		
		/*for(Entry<Integer, Object[]> entry : map.entrySet()) {
		    int key = entry.getKey();
		    Object[] ob = entry.getValue();
		    
		    System.out.println(key);
		    System.out.println(ob[0]);
		    System.out.println(ob[1]);
		    
		    //Location location = entry.getValue();

		    // do what you have to do here
		    // In your case, an other loop.
		}*/
	}
	
	public static HashMap<String, Location> getHashMap(){
		return smap;
	}
	
	public void removeSign(String s) {
		if(smap.containsKey(s)) {
			smap.put(s, null);
		} else {
			if(plugin.debug) {
				System.out.println("[DynamicPrice] Debug: Sign was not in HashMap.");
			}
		}
	}
	
	public String getSignItemID(String string) {
		String[] Sprice = string.split(" ");
		return Sprice[0];
	}
	
	public int getSignItemAmount(String string) throws ArrayIndexOutOfBoundsException {
		String[] Sprice = string.split(" ");
		return Integer.parseInt(Sprice[1]);
	}
	
	public void setSignSellPrice(Sign sign, double worth) {
		//UPDATE SELL PRICE ON SPECIFIED SIGN
		if(!getSignSellState(sign)) {
			sign.setLine(2, "S: " + Helper.formatPrice(worth));
			sign.update();
		}
	}
	
	public void setSignBuyPrice(Sign sign, double cost) {
		//UPDATE BUY PRICE ON SPECIFIED SIGN
		
		//double m = DynamicPrice.buyMultiplicator;
		
		//System.out.println(m);
		if(!getSignBuyState(sign)) {
			sign.setLine(3, "B: " + Helper.formatPrice(cost));
			sign.update();
		}
	}

	public boolean getSignSellState(Sign sign) {
		return sign.getLine(2).isEmpty();
	}

	public boolean getSignBuyState(Sign sign) {
		return sign.getLine(3).isEmpty();
	}
	
	public int getSignCount() {
		return smap.size();
	}
	
	public String getUniqueHash(Location l) {
		return l.getWorld().getName() + "" + l.getX() + "" + l.getY() + "" + l.getZ();
	}
	
	public static boolean isSignBlock(Block b) {
		if(b != null) {
			if(b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST)
				return true;
		}
		return false;
	}
	
	public static boolean isValidSign(Sign sign) {
		return sign.getLine(0).matches("§a\\[.*\\]§f");
	}
}
