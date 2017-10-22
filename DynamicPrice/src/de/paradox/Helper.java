package de.paradox;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.util.Locale;

import org.bukkit.ChatColor;

public class Helper {
	
	static Locale locale;
	
	public static String formatPrice(double p) {
		DecimalFormatSymbols seperator = new DecimalFormatSymbols(Locale.getDefault());
		DecimalFormat df = new DecimalFormat("$0.00", seperator);
		
		if(p == 0.0) {
			df = new DecimalFormat("$0.0", seperator);
		} else if(p < 0.000001) {
			df = new DecimalFormat("$0.00000000", seperator);
		} else if(p < 0.00001) {
			df = new DecimalFormat("$0.0000000", seperator);
		} else if(p < 0.0001) {
			df = new DecimalFormat("$0.000000", seperator);
		} else if(p < 0.001) {
			df = new DecimalFormat("$0.00000", seperator);
		} else if(p < 0.01) {
			df = new DecimalFormat("$0.0000", seperator);
		} else if(p < 0.1) {
			df = new DecimalFormat("$0.000", seperator);
		}
		
		return df.format(p);
	}
	
	public static int[] splitID(String id) {
		int[] sID = {0, 0};
		
		if(id.contains(":")) {
			String[] t = id.split(":");
			sID[0] = Integer.parseInt(t[0]);
			sID[1] = Integer.parseInt(t[1]);
		} else {
			try {
				sID[0] = Integer.parseInt(id);
			} catch (Exception e){
			}
		}
		
		return sID;
	}
	
	public static String combineID(int i, int j, boolean hideZero) {
		if(hideZero) {
			if(j == 0) {
				return i + "";
			}
		}
		
		return i + ":" + j;
	}

	public static String formatPercent(double p) {
		double max = DynamicPrice.maxPercent;
		double min = DynamicPrice.minPercent;
		
		String pc = MessageFormat.format("{0,number,#.##%}", p);
		
		double vmax = (max - 1) / 4;
		double vmin = (1 - min) / 4;
		
		if(p > (1.0 + vmax)) {
			return ChatColor.DARK_GREEN + pc;
		} else if(p >= 1.0) {
			return ChatColor.GREEN + pc;
		} else if(p < (1.0 - vmin)) {
			return ChatColor.RED + pc;
		} else {
			return ChatColor.GOLD + pc;
		}
	}
}
