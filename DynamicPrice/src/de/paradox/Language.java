package de.paradox;

import org.bukkit.ChatColor;

import de.paradox.confighandler.ConfigAccessor;

public class Language {

	private DynamicPrice plugin;
	private ConfigAccessor lang;

	public Language(DynamicPrice plugin) {
		this.plugin = plugin;
		this.lang = plugin.langAccessor;
	}
	
	public String getLang(String l) {
		String a = this.lang.fileConfig.getString(l);
		if(a == null) {
			return "Error in language file next to: " + l;
		} else {
			return replaceColorCodes(a);
		}
	}
	
	private String replaceColorCodes(String l) {
		l = l.replaceAll("&0", ChatColor.BLACK + "");
        l = l.replaceAll("&1", ChatColor.DARK_BLUE + "");
        l = l.replaceAll("&2", ChatColor.DARK_GREEN + "");
        l = l.replaceAll("&3", ChatColor.DARK_AQUA + "");
        l = l.replaceAll("&4", ChatColor.DARK_RED + "");
        l = l.replaceAll("&5", ChatColor.DARK_PURPLE + "");
        l = l.replaceAll("&6", ChatColor.GOLD + "");
        l = l.replaceAll("&7", ChatColor.GRAY + "");
        l = l.replaceAll("&8", ChatColor.DARK_GRAY+ "");
        l = l.replaceAll("&9", ChatColor.BLUE + "");
        l = l.replaceAll("&a", ChatColor.GREEN + "");
        l = l.replaceAll("&b", ChatColor.AQUA + "");
        l = l.replaceAll("&c", ChatColor.RED + "");
        l = l.replaceAll("&d", ChatColor.LIGHT_PURPLE + "");
        l = l.replaceAll("&e", ChatColor.YELLOW + "");
        l = l.replaceAll("&f", ChatColor.WHITE + "");
        l = l.replaceAll("&g", ChatColor.MAGIC + "");
        
		return l;
	}
}
