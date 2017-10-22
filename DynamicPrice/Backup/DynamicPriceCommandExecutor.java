package de.paradox;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.utils.WorldManager;

import net.minecraft.server.v1_7_R4.MinecraftServer;

/*
 * PERMISSIONS:
 * dp.dim = Shows dimension id of the current world -- Moderator ++
 * 
 * dp.price = Show the price value of a specified item -- User ++
 * 
 */

public class DynamicPriceCommandExecutor implements CommandExecutor{
	private DynamicPrice plugin;
	
	
	public DynamicPriceCommandExecutor(DynamicPrice plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender cs, Command c, String label, String[] args){
		Player p = (Player) cs;
		
		if (c.getName().equalsIgnoreCase("price")){
			//if (sender.hasPermission("dynamicprices.showprice")){
		        if (args.length == 1){
		        	
		        	p.sendMessage(ChatColor.GREEN + "Show price für " + args[0]);
		        	p.sendMessage(this.plugin.pricesAccessor.getPrice(args[0]) + "");
		        } //else if(args.length < 1){
		        	//p.sendMessage(ChatColor.RED + "Need more arguments");
		        else {
		        	p.sendMessage("Show the price for the specified item.\n/price <item>");
		        }
			//}
		    //else {
	        //	sender.sendMessage(ChatColor.RED + "You don't have the permission to see the price list.");	
		    //}
		} else if(c.getName().equalsIgnoreCase("test")){
			if (args.length == 0){
				//p.sendMessage(this.plugin.getServer().getWorld(args[0]).toString());
				
				p.sendMessage(p.getWorld().getName());
				p.sendMessage(p.getWorld().getEnvironment().getId() + "");
				
				/*World world = this.plugin.getServer().getWorlds().get(args[0]);
                
                for (World iterator : getServer().getWorlds()) {
                    if (iterator.getUID().hashCode() == result.getInt("worldId"))
                        world = iterator;
                }*/
				
	        } else if(args.length < 1){
	        	p.sendMessage(ChatColor.RED + "Need more arguments");
	        } else {
	        	p.sendMessage(ChatColor.RED + "Too much arguments");
	        }
		} else if(c.getName().equalsIgnoreCase("dp")){
			if (args.length > 0){
				if(args[0].equalsIgnoreCase("dim")) {
					if(p.hasPermission("dp.dim")) {
						if(args.length >= 2) {
							/*try {
								MultiverseCore mv = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
						    	WorldManager wm = (WorldManager) mv.getCore().getMVWorldManager();
						    	
						    	
								p.sendMessage(Bukkit.getWorld(args[1].toString()).getEnvironment().getId() + "");
								e(Bukkit.getWorld(args[1].toString()).getEnvironment().getId() + "");
								p.sendMessage(wm.getMVWorld(args[1]).getEnvironment().getId() + "");
								e(wm.getMVWorld(args[1]).getEnvironment().getId() + "");
								p.sendMessage(MinecraftServer.getServer().getWorld().getWorld().getEnvironment().getId()+"");
								e(MinecraftServer.getServer().getWorld().getWorld().getEnvironment().getId()+"");
								p.sendMessage("Is world: " + wm.isMVWorld(args[1]));
								e("Is world: " + wm.isMVWorld(args[1]));
								p.sendMessage("Alias: " + wm.getMVWorld(args[1]).getAlias());
								p.sendMessage("Generator: " + wm.getMVWorld(args[1]).getGenerator());
								p.sendMessage("Name: " + wm.getMVWorld(args[1]).getName());
								p.sendMessage("ToStr: " + wm.getMVWorld(args[1]).toString());
								p.sendMessage("CW Str: " + wm.getMVWorld(args[1]).getColoredWorldString());
								p.sendMessage("PName: " + wm.getMVWorld(args[1]).getPermissibleName());
								p.sendMessage("Scaling: " + wm.getMVWorld(args[1]).getScaling());
								p.sendMessage("CB UID: " + wm.getMVWorld(args[1]).getCBWorld().getUID());
								p.sendMessage("CB Env. ID: " + wm.getMVWorld(args[1]).getCBWorld().getEnvironment().getId());
								
							//} catch (NullPointerException e) {
								//p.sendMessage(ChatColor.RED + "Error: The world does not exist.");
							//}*/
						} else {
							
						}
					} else {
						p.sendMessage(ChatColor.RED + "You don't have the permission for this command.");
					}
				} else {
					System.out.println("Command not exists");
				}
	        } else {
	        	//DISPLAY DP COMMANDS
				return false;
	        }		
		}
		else {
			return false;
		}
		return false;
	}

	private void e(String string) {
		System.out.println(string);		
	}
}
