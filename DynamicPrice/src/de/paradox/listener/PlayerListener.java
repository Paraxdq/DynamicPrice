package de.paradox.listener;

import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import de.paradox.DynamicPrice;
import de.paradox.Helper;
import de.paradox.Language;
import de.paradox.Prices;
import de.paradox.Signs;
import net.milkbowl.vault.economy.Economy;

public class PlayerListener implements Listener {
	private DynamicPrice plugin;
	private Prices prices;
	private Language lang;
	private double minPercent;
	private double maxPercent;
	private double percentDecrease;
	private double percentIncrease;
	private Signs signs;
	private Economy econ;
	private static HashMap<String, Long> cmap = new HashMap<String, Long>();

	public PlayerListener(DynamicPrice plugin) {
		this.plugin = plugin;
		this.prices = plugin.prices;
		this.lang = plugin.lang;
		this.signs = plugin.signs;
		this.econ = plugin.econ;
		this.minPercent = DynamicPrice.minPercent;
		this.maxPercent = DynamicPrice.maxPercent;
		this.percentDecrease = DynamicPrice.percentDecrease;
		this.percentIncrease = DynamicPrice.percentIncrease;
	}

	// @EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {

	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		
		if (Signs.isSignBlock(e.getClickedBlock())
				|| (e.getItem() != null && Signs.isSignBlock(e.getPlayer().getTargetBlock(null, 3)))) {
			
			Player p = e.getPlayer();
			Block target = p.getTargetBlock(null, 3);

			if (target.getState() instanceof Sign) {
				Sign sign = (Sign) target.getState();
				if (sign.getLine(0).equalsIgnoreCase("§a[DP SHOP]§f") && Signs.isValidSign(sign)) {
					
					/* Schild ist erkannt worden, da in der ersten Zeile §a[DP SHOP]§f steht... */

				} else if (sign.getLine(0).equalsIgnoreCase("[DP Shop]")) {
					System.out.println("[DynamicPrice] " + p.getName() + " tried cheating.");
				}
			} else {
				if (this.plugin.debug) {
					p.sendMessage("Target is no Sign");
					p.sendMessage(target.toString() + ":" + target.getType());
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		
		if (Signs.isSignBlock(e.getClickedBlock())
				|| (e.getItem() != null && Signs.isSignBlock(e.getPlayer().getTargetBlock(null, 3)))) {
			
			Player p = e.getPlayer();
			final boolean sneak = p.isSneaking();
			Block target = p.getTargetBlock(null, 3);

			if (target.getState() instanceof Sign) {
				Sign sign = (Sign) target.getState();
				if (sign.getLine(0).equalsIgnoreCase("§a[DP SHOP]§f") && Signs.isValidSign(sign)) {
					
					if(cmap.containsKey(p.getName())){
						Long time = cmap.get(p.getName());
						
						//System.out.println("Vergangene Zeit in ms: " + (System.currentTimeMillis() - time));
						
						if((System.currentTimeMillis() - time) < 150) {
							//System.out.println(cmap.size());
							//p.sendMessage(ChatColor.RED + "Du klickst zu schnell!");
							return;
						} else {
							cmap.put(p.getName(), System.currentTimeMillis());
						}
					} else {
						cmap.put(p.getName(), System.currentTimeMillis());
					}
					
					
					final int[] id = Helper.splitID(signs.getSignItemID(sign.getLine(1)));

					final int amount = signs.getSignItemAmount(sign.getLine(1));
					final Inventory inv = p.getInventory();

					if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
						//SELL ITEMS
						
						e.setCancelled(true);
						
						if(signs.getSignSellState(sign)) {
							p.sendMessage(this.lang.getLang("dp-sign-sell-failed"));
							return;
						}
						
						final ItemStack[] item = { new ItemStack(id[0], amount, (short) id[1]) };

						int itemsFound = 0;
						int count = 0;
						double worth = 0.0;
						boolean tr;

						// if(p.getInventory().contains(Material.getMaterial(sID[0]))){//
						// && p.getInventory()) {
						if (p.getInventory().contains(id[0])) {// &&
																// p.getInventory())
																// {-
							for (int i = 0; i < inv.getSize(); i++) {
								if (inv.getItem(i) == null) {
									continue;
								}

								if (inv.getItem(i).isSimilar(item[0])) {
									itemsFound = itemsFound + inv.getItem(i).getAmount();
								}
							}

							if (sneak)
								count = itemsFound / amount;
							else {
								if (itemsFound >= amount)
									count = 1;
							}

							if (count == 0) {
								p.sendMessage(String.format(this.lang.getLang("dp-sign-sell-error"), amount, Helper.combineID(id[0], id[1], true)));
								return;
							} else if ((itemsFound / amount >= 2) && !sneak) {
								p.sendMessage(this.lang.getLang("dp-sign-sell-info"));
							}

							p.sendMessage("Found Items: " + itemsFound + ". Remove " + count + " times.");

							for (int j = 0; j < count; j++) {
								inv.removeItem(item);
							}

							final double price = prices.getPrice(id[0], id[1], true);
							double percent = prices.getPercent(id[0], id[1]);

							for (int i = 0; i < count; i++) {
								worth = worth + ((price * percent) * amount);
								
								if (percent >= (this.minPercent + this.percentDecrease)) {
									percent = percent - this.percentDecrease;
									p.sendMessage(this.lang.getLang("dp-sign-percent-info") + " " + Helper.formatPercent(percent) + "");
								} else {
									percent = this.minPercent;
								}
							}

							signs.setSignSellPrice(sign, (price * percent) * amount);

							prices.setPercent(id[0], id[1], percent);

							tr = this.prices.giveMoney(p, worth);
							p.updateInventory();
							
							signs.updateAllSigns();

							if (tr) {
								p.sendMessage(
										String.format(this.lang.getLang("dp-sign-sell-success"), Helper.formatPrice(worth)));
							} else {
								p.sendMessage(ChatColor.RED + "Something went wrong.");
							}

						} else {
							p.sendMessage(String.format(this.lang.getLang("dp-sign-sell-error"), amount, Helper.combineID(id[0], id[1], true)));
						}
					} else if(e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {

						//BUY ITEMS
						
						if(signs.getSignBuyState(sign)) {
							e.setCancelled(true);
							p.sendMessage(this.lang.getLang("dp-sign-buy-failed"));
							return;
						}
						
						if(!p.isOp() || this.plugin.debug) {
							e.setCancelled(true);
							final double cost = prices.getPrice(id[0], id[1], true) * DynamicPrice.buyMultiplicator; // STANDARD PREIS
							double percent = prices.getPercent(id[0], id[1]);
							
							OfflinePlayer op = this.plugin.getServer().getPlayer(p.getUniqueId());
							
							int amount_ = amount;
							
							if(econ.getBalance(op) > cost && cost > 0.0) {
								// USER HAT GENUG GELD
								HashMap<Integer, ItemStack> n = inv.addItem(new ItemStack(id[0], amount, (short)id[1]));
								
								double add = this.percentIncrease;
								
								if(n.size() > 0) {		
									// INVENTAR VOLL
									p.sendMessage(ChatColor.RED + "Error: Inventory full!");
									//p.sendMessage("Could not send " + n.values().toString());
									
									amount_ = amount - n.get(0).getAmount();
									
									add = this.percentIncrease / (Double.valueOf(amount) / Double.valueOf(amount_));									
								}
								
								double withdraw = (cost * percent) * amount_;
								
								if (percent <= (this.maxPercent - add)) {
									percent = percent + add;
									p.sendMessage(this.lang.getLang("dp-sign-percent-info") + " " + Helper.formatPercent(percent) + "");
								} else {
									percent = this.maxPercent;
								}
								
								//System.out.println("Gave player money " + ((cost * percent) * amount_));
								econ.withdrawPlayer(op, withdraw);

								//System.out.println("Updated sign buy price to " + ((cost * percent) * amount));
								//System.out.println("Buy function playerlistener: " + withdraw);
								signs.setSignBuyPrice(sign, withdraw);
								
								//System.out.println("Set percent to " + percent);
								prices.setPercent(id[0], id[1], percent);
								signs.updateAllSigns();
								
								p.sendMessage(String.format(this.lang.getLang("dp-sign-buy-success"), Helper.formatPrice(withdraw)));
								return;
							} else {
								p.sendMessage(String.format(this.lang.getLang("dp-sign-buy-error"), Helper.formatPrice((cost * percent) * amount)));
								return;
							}
						}
					}

				} else if (sign.getLine(0).equalsIgnoreCase("[DP Shop]")) {
					System.out.println("[DynamicPrice] " + p.getName() + " tried cheating.");
				}
			} else {
				if (this.plugin.debug) {
					p.sendMessage("Target is no Sign");
					p.sendMessage(target.toString() + ":" + target.getType());
				}
			}
		}
	}
}
