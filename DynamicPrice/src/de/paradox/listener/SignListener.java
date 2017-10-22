package de.paradox.listener;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

import de.paradox.DynamicPrice;
import de.paradox.Helper;
import de.paradox.Prices;
import de.paradox.Signs;

public class SignListener implements Listener{
	
	private DynamicPrice plugin;
	private Prices prices;
	private Signs signs;

	public SignListener(DynamicPrice plugin){
		this.plugin = plugin;
		this.prices = plugin.prices;
		this.signs = plugin.signs;
	}
	
	@EventHandler
	public void onSignCreate(SignChangeEvent e) {
	    if(e.getLine(0).equalsIgnoreCase("[DP Shop]")){
	    	if(e.getPlayer().hasPermission("dp.sign")) {
	    		int[] id = {};
	    		int amount = 0;
	    		
	    		try {
		    		 id = Helper.splitID(signs.getSignItemID(e.getLine(1)));
		    		 amount = signs.getSignItemAmount(e.getLine(1));
	    		} catch (Exception ex) {
	    			e.getPlayer().sendMessage("Error: False input for <ID> <Amount>.");
	    			e.setCancelled(true);
	    			return;
	    		}
	    		final String sb = e.getLine(2);
	    		boolean sell = false, buy = false;
	    		
	    		if(sb.equalsIgnoreCase("S")) {
	    			sell = true;
	    		} else if(sb.equalsIgnoreCase("B")) {
	    			buy = true;
	    		} else {
	    			sell = true;
	    			buy = true;
	    		}
	    		
	    		int c = 0;
	    		
	    		try {
		    		c = new Integer(amount);
				} catch (NumberFormatException ex) {
					ex.getStackTrace();
				}	    		
	    		
	    		final double price = (prices.getPrice(id[0], id[1], false) * c);
	    		
	    		e.setLine(0, "§a[DP Shop]§f");
	    		e.setLine(1, Helper.combineID(id[0], id[1], true) + " " + amount);
	    		
	    		if(sell) {
		    		e.setLine(2, "S: " + Helper.formatPrice(price));
	    		} else {
	    			e.setLine(2, "");
	    		}
	    		
	    		if(buy) {
		    		e.setLine(3, "B: " + Helper.formatPrice(price * DynamicPrice.buyMultiplicator));
	    		} else {
	    			e.setLine(3, "");
	    		}
	    		
	    		signs.addSign(signs.getUniqueHash(e.getBlock().getLocation()), e.getBlock().getLocation());
	    		
	    		if(plugin.debug) {
	    			System.out.println("[DynamicPrice] Sign created at location: " + e.getBlock().getLocation());
	    		}
	    	}
		}
	}	
	
	@EventHandler
	public void onSignDestroy(BlockBreakEvent e) {		
		if(Signs.isSignBlock(e.getBlock()) && Signs.isValidSign((Sign)e.getBlock().getState())){
			e.getPlayer().sendMessage(ChatColor.GREEN + "[DynamicPrice]" + ChatColor.WHITE + " Shop Sign destroyed.");
			
			signs.removeSign(signs.getUniqueHash(e.getBlock().getLocation()));
		}
	}
	
	/*public void signUpdater(Location bLocation) {
        World w = bLocation.getWorld();
        Block b = w.getBlockAt(bLocation);
        if(b.getTypeId() == Material.SIGN_POST.getId() || b.getTypeId() == Material.WALL_SIGN.getId()) {
            Sign sign = (Sign) b.getState();
            sign.setLine(1, "isoccerplayer");
            sign.update();
        }
    }
	
	public static void updatePlayersInRange(Sign s) {
	    for (Player p : s.getWorld().getPlayers()) {
	        if (p.getLocation().distanceSquared(s.getLocation()) <= 65) {
	            p.sendSignChange(s.getLocation(), s.getLines());
	        }
	    }
	}*/
}
