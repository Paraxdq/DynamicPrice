package de.paradox;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import de.paradox.confighandler.DatabaseAccessor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class Prices {

	private DynamicPrice plugin;
	//private ConfigAccessor priceAccessor;
	private Economy eco;
	private DatabaseAccessor dba;
	
	private static HashMap<String, double[]> pmap = new HashMap<String, double[]>();

	public Prices(DynamicPrice plugin) {
		this.plugin = plugin;
		this.dba = plugin.dba;
		//this.priceAccessor = plugin.pricesAccessor;
		this.eco = plugin.econ;
		
		pmap = dba.loadPrices();
		
		//test2();
	}
	
	/*public static final int N_ITERATIONS = 10;
	
	public void test2() {
        long tStart, tElapsed;

        tStart = System.currentTimeMillis();
        for (int i = 0; i < N_ITERATIONS; i++) {
    		pmap.put(Format.combineID(i, i+1), new Double[] {getPrice(i,i+1, true), 100.0});
        }
        tElapsed = System.currentTimeMillis() - tStart;
        System.out.println("Method with finals took " + tElapsed + " ms");

        tStart = System.currentTimeMillis();
        for (int i = 0; i < N_ITERATIONS; i++) {
    		pmap.put(Format.combineID(i, i+1), new Double[] {getPrice(i,i+1, true), 100.0});
        }
        tElapsed = System.currentTimeMillis() - tStart;
        System.out.println("Method without finals took " + tElapsed + " ms");

    }*/
	
	public double getPrice(int id, int subID, boolean standardPrice) {		
		try {
			int Iid = id;
			int IsubID = subID;
			
			if(standardPrice) {
				return pmap.get(Helper.combineID(Iid, IsubID, false))[0];
			} else {
				//System.out.println(pmap.get(Format.combineID(Iid, IsubID))[0] * (pmap.get(Format.combineID(Iid, IsubID))[1] / 100));
				return pmap.get(Helper.combineID(Iid, IsubID, false))[0] * (pmap.get(Helper.combineID(Iid, IsubID, false))[1]);
			}
			//return dba.getPrice(id, subID, standardPrice);
		} catch (Exception e) {
			return 0.0;
		}		
	}
	
	public void setPrice(int id, int subID, double price) {
		DatabaseAccessor.priceUpdate = true;
		
		try {
			int Iid = id;
			int IsubID = subID;
			double Dprice = price;
			
			if(pmap.containsKey(Helper.combineID(Iid, IsubID, false))) {
				pmap.put(Helper.combineID(Iid, IsubID, false), new double[] {Dprice, getPercent(Iid, IsubID)});
			} else {
				pmap.put(Helper.combineID(Iid, IsubID, false), new double[] {Dprice, 1.0});
			}
			
			//dba.setPrice(id, subID, price);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean giveMoney(Player p, double money) {
		EconomyResponse er = this.eco.depositPlayer(this.plugin.getServer().getPlayer(p.getUniqueId()), money);
		
		return er.transactionSuccess();
			
	}
	
	public double getPercent(int i, int j) throws NullPointerException {
		return pmap.get(Helper.combineID(i, j, false))[1];
		//return dba.getPercent(i, j);
	}
	
	public boolean setPercent(int i, int j, double np) {
		DatabaseAccessor.priceUpdate = true;
		if(pmap.containsKey(Helper.combineID(i, j, false))) {
			pmap.put(Helper.combineID(i, j, false), new double[] {getPrice(i,j, true), np});
			return true;
		}
		
		return false;
		//dba.setPercent(i, j, newPercent);
	}

	/*public void increasePercent(int i, int j) {
		double percent = pmap.get(Format.combineID(i, j))[1] + DynamicPrice.maxPercentIncrease;
		if(percent > DynamicPrice.maxPercent) {
			percent = DynamicPrice.maxPercent;
		}
		setPercent(i, j, percent);
		//double percent = dba.getPercent(i, j) + plugin.maxPercentIncrease;
	}*/
	
	public static HashMap<String, double[]> getHashMap(){
		return pmap;
	}

	public void increasePercent() {
		DatabaseAccessor.priceUpdate = true;
		
		double increase = DynamicPrice.percentIncrease;
		
		for(Entry<String, double[]> entry : pmap.entrySet()) {
		    String id = entry.getKey();
		    double[] pp = entry.getValue(); //PRICE PERCENT
		    
		    if(plugin.debug)
		    	System.out.println("[DynamicPrice] Increase Price: " + id + ", Price: " + pp[0] + ", Percent: " + pp[1]);
		    
		    /*System.out.println("KONSTANTE:" + increase);
		    System.out.println("ID: " + id);
		    System.out.println("PRICE: " + pp[0]);
		    System.out.println("PERCENT: " + pp[1]);
		    System.out.println("____________");*/
		    if(pp[1] < (2.0 - increase)) {
			    pmap.put(id, new double[] {pp[0], pp[1] + increase});
		    } else if(pp[1] == 2.0) {
		    	continue;
			} else {
			    pmap.put(id, new double[] {pp[0], 2.0});
		    }
		}
	}
	
	public void increasePercent(int id) {
		
	}
	
	public void decreasePercent(int id) {
		
	}
}
