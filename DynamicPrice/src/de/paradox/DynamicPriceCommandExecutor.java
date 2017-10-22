package de.paradox;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import de.paradox.confighandler.ConfigAccessor;

/*
 * PERMISSIONS:
 * dp.reload = Reloads the configs -- Moderator ++
 * 
 * dp.price = Show the price value of a specified item -- User ++
 * dp.buy = TEST
 * 
 * dp.set = Sets the price of an item -- Administrator
 * 
 * dp.help = User ++
 * 
 * dp.sign = Administrator // Can create shop signs
 * 
 */

// TODO 
// DPRICE 1 64

public class DynamicPriceCommandExecutor implements CommandExecutor{
	private DynamicPrice plugin;
	private ConfigAccessor configAccessor;
	private ConfigAccessor langAccessor;
	private Prices prices;
	private Language lang;
	private Signs signs;
	
	public static int i = 10;
	public static long d = 20;
    public static BukkitTask task = null;
    public static Player Splayer;
	
	
	public DynamicPriceCommandExecutor(DynamicPrice plugin) {
		this.plugin = plugin;
		this.prices = plugin.prices;
		this.configAccessor = plugin.configAccessor;
		//this.pricesAccessor = plugin.pricesAccessor;
		this.langAccessor = plugin.langAccessor;
		this.lang = plugin.lang;
		this.signs = plugin.signs;
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender cs, Command c, String label, String[] args){
		Player p = null;
		
		if(cs instanceof Player) {
			p = (Player) cs;
		} else {
			System.out.println("These commands are not runnable via console.\nThis may change in a further build.");
			return true;
		}
		
		if(c.getName().equalsIgnoreCase("dp")){
			final String hVar = "hand";
			String firstArgument = "";
			
			if(args.length > 0) {
				if(label.equalsIgnoreCase("dp")) {
					firstArgument = args[0];
				}					
			}
			
			if (label.equalsIgnoreCase("dprice") || firstArgument.equalsIgnoreCase("price")) {
				if(p.hasPermission("dp.price")) {
					
					/*
					 * Mehrere Items abfragen und Preise
					 * zusammenhängend aufgelistet bekommen.
					 * 
					 */					
					
					/*CATCHES:
					 * 
					 * dp ERROR			> 2 args
					 * dp price 3	    = 2 args
					 * dp price help    = 2 args
					 * dp price 		= 1 args
					 * dprice 2			= 1 arg
					 * dprice help		= 1 arg
					 * dprice    		= 0 args
					 * 
					 */	
					
					if(args.length >= 1 && args[args.length - 1].equalsIgnoreCase("help")) {
						
						/*CATCHES:
						 * 
						 * dp price help	= 2 args
						 * dprice help		= 1 arg
						 * 
						 */
						
						ps(p, this.lang.getLang("dp-price-help") + ChatColor.WHITE + "\n/dp price\n/dp price <ID>");
						return true;
						
					} else if(args.length == 0 || args[args.length-1].equalsIgnoreCase("price")) {
						
						/*CATCHES:
						 * 
						 * dp price			= 1 args
						 * dprice			= 0 arg
						 * 
						 */
						
						final int id = p.getItemInHand().getTypeId();
						final short val = p.getItemInHand().getDurability();
						
						if(id == 0) {
							ps(p, this.lang.getLang("dp-hand-error"));
							return true;
						}
						
						final double sprice = prices.getPrice(id, val, true) * 64;
						
						if(sprice != 0.0) {
							final double cprice = prices.getPrice(id, val, false) * 64;
							final double pc = prices.getPercent(id, val);
							final String name = Material.getMaterial(id).name();
							
							ps(p, String.format(this.lang.getLang("dp-price"), name, id, Helper.formatPrice(sprice), 
									Helper.formatPrice(sprice * DynamicPrice.buyMultiplicator), Helper.formatPrice(cprice), 
									Helper.formatPrice(cprice * DynamicPrice.buyMultiplicator),
									Helper.formatPercent(pc)));
							return true;
						} else {
							ps(p, this.lang.getLang("dp-price-notfound"));
							return true;
						}
						
					} else if(args.length == 1 || args.length == 2){

						/*CATCHES:
						 * 
						 * dp price 3	    = 2 args
						 * dprice 2			= 1 arg
						 * 
						 */	
						
						final String id = args[args.length - 1];
						final int[] sID = Helper.splitID(id);
						final double sprice = prices.getPrice(sID[0], sID[1], true) * 64;
						
						if(sprice != 0.0) {
							final double cprice = prices.getPrice(sID[0], sID[1], false) * 64;
							final double pc = prices.getPercent(sID[0], sID[1]);
							final String name = Material.getMaterial(sID[0]).name();
							
							ps(p, String.format(this.lang.getLang("dp-price"), name, id, Helper.formatPrice(sprice), 
									Helper.formatPrice(sprice * DynamicPrice.buyMultiplicator), Helper.formatPrice(cprice), 
									Helper.formatPrice(cprice * DynamicPrice.buyMultiplicator),
									Helper.formatPercent(pc)));
							return true;
						} else {
							ps(p, this.lang.getLang("dp-price-notfound"));
							return true;
						}
					} else {
						
						/* CATCHES:
						 *
						 * dp ERROR			> 2 args
						 * 
						 */

						ps(p, this.lang.getLang("dp-price-help") + ChatColor.WHITE + "\n/dp price\n/dp price <ID>");
						return true;
					}
				} else {
					ps(p, this.lang.getLang("dp-nopermission"));
					return true;
				}
			} else if (label.equalsIgnoreCase("dset") || firstArgument.equalsIgnoreCase("set")) {
				if(p.hasPermission("dp.set")) {
					if(args.length >= 1) {
						
						/*CATCHES:
						 * 
						 * dp ERROR			> 3 args
						 * dp set 3 2		= 3 args
						 * dp set hand 2	= 3 args
						 * dp set help		= 2 args
						 * dset 2 1			= 2 args
						 * dset hand 1		= 2 args
						 * dset help		= 1 arg
						 * 
						 */						
						
						if(args.length > 3 || args[args.length-1].equalsIgnoreCase("help") || args.length == 1) {
							
							/*CATCHES:
							 * 
							 * dp ERROR		> 3 Args
							 * dp set help	= 2 args
							 * dset help	= 1 arg
							 * 
							 */

							ps(p, this.lang.getLang("dp-set-help") + 
									ChatColor.WHITE + "\n/dp set <ID/" + hVar + "> <Price>");
							return true;
						} else if(args[args.length-2].equalsIgnoreCase(hVar)) { //ERROR MÖGLICHKEIT args.length-2
							
							/*CATCHES:
							 * 
							 * dp set hand 2	= 2 Args
							 * dset hand 1		= 2 Args
							 * 
							 */
							
							final int id = p.getItemInHand().getTypeId();
							final short val = p.getItemInHand().getDurability();
							double price = 0.0;
							
							try {
								price = Double.valueOf(args[args.length - 1]);
							} catch(Exception ex) {
								ps(p, ChatColor.RED + "Error: Double expected e.g. 0.50");
								return true;
							}
							
							if(id == 0) {
								ps(p, this.lang.getLang("dp-set-error"));
								return true;
							} else {
								if(Double.valueOf(price) instanceof Double) {
									prices.setPrice(id, val, price);
									ps(p, String.format(this.lang.getLang("dp-set-price"), id + ":" + val, price));
								} else {
									ps(p, ChatColor.RED + "Error: Double expected e.g. 0.50");
									return true;
								}
							}
						} else {
							
							/*CATCHES:
							 * 
							 * dp set 3 2	= 2 Args
							 * dset 2 1		= 2 Args
							 * dset WHATEVER?
							 * 
							 */
							
							try {
								String id = args[args.length - 2];
								String price = args[args.length - 1];
								
								int[] sID = Helper.splitID(id);
								
								if(Double.valueOf(price) instanceof Double) {
									if(sID[0] != 0){
										prices.setPrice(sID[0], sID[1], Double.valueOf(price));
										ps(p, String.format(this.lang.getLang("dp-set-price"), id, price));
									} else {
										ps(p, ChatColor.RED + "Error: NumberFormatException for id");
									}
								} else {
									ps(p, ChatColor.RED + "WIE KOMMT DIESER ERROR ZUSTANDE OMG OMG OMG OMG");
									return true;
								}
							} catch (Exception e) {
								ps(p, ChatColor.RED + "Error: " + e);
								return true;
							}
						}
						
						signs.updateAllSigns();	
						
					} else {
						ps(p, this.lang.getLang("dp-set-help") + 
								ChatColor.WHITE + "\n/dp set <ID/" + hVar + "> <Price>");
					}
				} else {
					ps(p, this.lang.getLang("dp-nopermission"));
					return true;
				}
			} else if(label.equalsIgnoreCase("dsign") || firstArgument.equalsIgnoreCase("sign")) {
				if(p.hasPermission("dp.sign")) {
					
					/* CATCHES:
					 *
					 * dp sign ERROR	> 2 args
					 * dp sign help		= 2 args
					 * dp sign			= 1 args
					 * dsign help		= 1 args
					 * dsign			= 0 args
					 * 
					 */	
					
					if(args.length <= 2) {
						ps(p, this.lang.getLang("dp-sign-help"));
						ps(p, "\nActive Signs: " + signs.getSignCount());
						return true;
					}
					
				} else {
					ps(p, this.lang.getLang("dp-nopermission"));
					return true;
				}
			} else if(label.equalsIgnoreCase("dpc") || firstArgument.equalsIgnoreCase("pc")){
				if(p.hasPermission("dp.percent")) {
					
					/* CATCHES: 
					 * 
					 * dp pc set 2 100
					 * dp pc get 1
					 * dpc set 2 100
					 * dpc get 1
					 * 
					 */
					
					if(args.length <= 1 || args[args.length - 1].equalsIgnoreCase("help") || args.length > 4) {
						
						/* CATCHES: 
						 * 
						 * dp pc help
						 * dpc help
						 * dpc
						 * 
						 */
						
						ps(p, this.lang.getLang("dp-pc-help") + 
								ChatColor.WHITE + "\n/dp pc <get/" + ChatColor.YELLOW + "set" + ChatColor.WHITE + "> <ID> <" + ChatColor.YELLOW + "new Value" + ChatColor.WHITE + ">");
					} else if(args.length >= 2 && args[args.length - 2].equalsIgnoreCase("get")) {
						
						/* CATCHES: 
						 * dp pc get 1
						 * dpc get 1
						 * 
						 */
						
						final int[] id = Helper.splitID(args[args.length - 1]);
						
						if(id[0] != 0) {
							try {
								ps(p, Helper.formatPercent(this.prices.getPercent(id[0], id[1])));
							} catch (NullPointerException ex) {
								ps(p, ChatColor.RED + "Error: Item does not exist in database.");
							}
						} else {
							ps(p, ChatColor.RED + "Error: NumberFormatException for id");
						}
						
					} else if(args.length >= 3 && args[args.length - 3].equalsIgnoreCase("set")) {
						
						/* CATCHES: 
						 * dp pc set 2 100
						 * dpc set 2 100
						 * 
						 */
						
						final int[] id = Helper.splitID(args[args.length - 2]);
						
						if(id[0] != 0) {
							try {
								final double pc = Double.valueOf((args[args.length - 1])) / 100;
								
								double min = DynamicPrice.minPercent;
								double max = DynamicPrice.maxPercent;
								
								if(pc >= min && pc <= max) {
									if(this.prices.setPercent(id[0], id[1], pc)) {
										ps(p, String.format(this.lang.getLang("dp-pc-success"), args[args.length - 2], Helper.formatPercent(pc)));
										signs.updateAllSigns();
									} else {
										ps(p, ChatColor.RED + "Error: Percent value for " + args[args.length - 2] + " could not set: Item not exists in database.");
									}
								} else {
									ps(p, ChatColor.RED + "Error: Percent values can't be lower or higher than the config settings.");
								}
							} catch (Exception ex) {
								ps(p, ChatColor.RED + "Error: " + ex);
							}
						} else {
							ps(p, ChatColor.RED + "Error: NumberFormatException for id");
						}
					} else {
						ps(p, this.lang.getLang("dp-pc-help") + 
								ChatColor.WHITE + "\n/dp pc <get/" + ChatColor.YELLOW + "set" + ChatColor.WHITE + "> <ID> <" + ChatColor.YELLOW + "new Value" + ChatColor.WHITE + ">");
					}
				}
			} else if(label.equalsIgnoreCase("dreload") || firstArgument.equalsIgnoreCase("reload")) {
				if(p.hasPermission("dp.reload")) {
					this.configAccessor.reloadConfig();
					this.langAccessor.reloadConfig();
					
					this.plugin.debug = this.plugin.config.getDebug();
					DynamicPrice.minPercent = Double.parseDouble(this.plugin.config.getMinPercent()) / 100;
					DynamicPrice.maxPercent = Double.parseDouble(this.plugin.config.getMaxPercent()) / 100;
					DynamicPrice.percentDecrease = Double.parseDouble(this.plugin.config.getPercentDecrease()) / 100;
					DynamicPrice.percentIncrease = Double.parseDouble(this.plugin.config.getPercentIncrease()) / 100;
					
					DynamicPrice.buyMultiplicator = Double.parseDouble(this.plugin.config.getBuyMultiplicator()) / 100;
					
					DynamicPrice.signUpdateInterval = this.plugin.config.getSignUpdateInterval() * 60L * 20L;
					DynamicPrice.databaseSaveInterval = this.plugin.config.getDatabaseSaveInterval() * 60L * 20L;
					DynamicPrice.increasePriceInterval = this.plugin.config.getIncreasePriceInterval() * 60L * 20L;
					
					this.plugin.bgt.restartTasks();
					//restart backgroundtasks
					
					ps(p, ChatColor.GREEN + "Config's successfully reloaded.");
					return true;
				} else {
					ps(p, this.lang.getLang("dp-nopermission"));
					return true;
				}
			} else if(label.equalsIgnoreCase("dpl")) {
				if(p.getName().equals("Paradox_DE")) {
					if(args.length == 3) {
						
						/* CATCHES:
						 *
						 * dpl Name 100 0	> 2 args
						 * 
						 */	
						
						String a = args[args.length-3];
						i = Integer.parseInt(args[args.length-2]);
						Splayer = plugin.getServer().getPlayer(a);
						d = Long.parseLong(args[args.length-1]);
						
						//ps(Splayer, ChatColor.GOLD + String.format("Du wirst jetzt %s mal gepeinigt!", i));
						ps(Splayer, ChatColor.GOLD + "DIE RACHE WIRD MEIN SEIN!!!");
						
						strike();
					} else {
						
						/* CATCHES:
						 *
						 * dpl Name 100		> 2 args
						 * 
						 */	
						
						String a = args[args.length-2];
						int i = Integer.parseInt(args[args.length-1]);
						Player pl = plugin.getServer().getPlayer(a);
						
						World world = plugin.getServer().getPlayer(a).getWorld();
						
						for(int j = 0; j < i; j++) {
							world.strikeLightning(pl.getLocation());
						}
					}
					
				} else {
					ps(p, "Nein " + p.getName() + ", du darfst diesen Befehl nicht ausführen.");
				}
			} else {
				if(p.hasPermission("dp.help")) {
					ps(p, ChatColor.DARK_GREEN + "------- Dynamic Prices by Paradox_DE -------");
					ps(p, this.lang.getLang("dp-help"));
					
					if(p.hasPermission("dp.price")) {
						ps(p, ChatColor.GREEN + "/dp " + ChatColor.UNDERLINE +  "price" + 
					ChatColor.RESET + " - " + lang.getLang("dp-command-price-info")); //+ ChatColor.WHITE + ": Show the price of the provided item.");
					}
					if(p.hasPermission("dp.set")) {
						ps(p, ChatColor.GREEN + "/dp " + ChatColor.UNDERLINE +  "set" + 
					ChatColor.RESET + " - " + lang.getLang("dp-command-set-info")); //+ ChatColor.WHITE + ": Set the price of an item to the specified value.");
					}
					if(p.hasPermission("dp.percent")) {
						ps(p, ChatColor.GREEN + "/dp " + ChatColor.UNDERLINE +  "pc" + 
					ChatColor.RESET + " - " + lang.getLang("dp-command-pc-info")); //+ ChatColor.WHITE + ": Set the price of an item to the specified value.");
					}
					if(p.hasPermission("dp.sign")) {
						ps(p, ChatColor.GREEN + "/dp " + ChatColor.UNDERLINE +  "sign" + 
					ChatColor.RESET + " - " + lang.getLang("dp-command-sign-info")); //+ ChatColor.WHITE + ": Set the price of an item to the specified value.");
					}
					if(p.hasPermission("dp.reload")) {
						ps(p, ChatColor.GREEN + "/dp " + ChatColor.UNDERLINE +  "reload" + 
					ChatColor.RESET + " - " + lang.getLang("dp-command-reload-info")); //+ ChatColor.WHITE + ": Set the price of an item to the specified value.");
					}
				}
			}
		}
		
		return true;
	}
	
	private void ps(Player p, String msg) {
		p.sendMessage(msg);
	}
	
	private void e(String string) {
		System.out.println(string);		
	}
	
	public void strike() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                if(i != 0) {
					World world = Splayer.getWorld();
					world.strikeLightning(Splayer.getLocation());
                    i--;
                } else {
                    // If "i" is zero, we cancel the task.
                    task.cancel();
                }
            }
        }, 0, d);
    }
}
