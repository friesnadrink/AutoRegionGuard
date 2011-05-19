package net.peoplero.arg;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.peoplero.arg.listener.block.ARGBlockListener;
import net.peoplero.arg.listener.entity.ARGEntityListener;
import net.peoplero.arg.listener.player.ARGPlayerListener;

public class ARG extends JavaPlugin {
	
    public static Logger log = Logger.getLogger("Minecraft");
    public static PluginDescriptionFile description;
    public static Plugin instance;
    public static Server Server = null;
    public File directory;
    public static String name = "ARG";
    public static String version = "0.4";
    
    //Create the hashmap debugees
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    //Create a list of godmode users
	public final static ArrayList<String> godusers = new ArrayList<String>();
	
	//Links the ARGBlockListener
    private final ARGBlockListener blockListener = new ARGBlockListener(this);
    private final ARGEntityListener entityListener = new ARGEntityListener(this);
    private final ARGPlayerListener playerListener = new ARGPlayerListener(this);
    public boolean UsePermissions;
    public final PermissionThing pt = new PermissionThing(this);
    public final RegionHandler RegionHandler = new RegionHandler(this);
    public final FileHandler FileHandler = new FileHandler(this);
    public final FriendHandler FriendHandler = new FriendHandler(this);
        

    public ARG() {
        new File("plugins" + File.separator + "ARG" + File.separator).mkdirs();
    }
    
    public void onLoad(){
    	
    }
    
    public void onDisable() {
    	log.info("[ARG] Saving Regions...");
    	saveAll();
    	log.info("[ARG] Unscheduling tasks.");
    	FileHandler.unScheduleTasks();
        log.info("ARG Disabled");
    }

    public void onEnable() {
    	Server = this.getServer();
    	description = this.getDescription();
    	directory = getDataFolder();
    	instance = this;
		//Create the pluginmanager pm.
		PluginManager pm = Server.getPluginManager();
	    //Create BlockPlaced listener
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.High, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.High, this);
		pm.registerEvent(Event.Type.BLOCK_BURN, blockListener, Event.Priority.High, this);
		pm.registerEvent(Event.Type.BLOCK_IGNITE, blockListener, Event.Priority.High, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, playerListener, Event.Priority.High, this);
		pm.registerEvent(Event.Type.PLAYER_BUCKET_FILL, playerListener, Event.Priority.High, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.High, this);
		pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Event.Priority.High, this);

		//Get the information from the yml file.
		PluginDescriptionFile pdfFile = this.getDescription();
		//setup permissions
		pt.setupPermissions();
		
		//load pre-existing values from files
		log.info("[ARG] Checking/loading files...");
		loadAll();
		
		log.info("[ARG] Scheduling tasks...");
		FileHandler.scheduleTasks();
		
		//Print that the plugin has been enabled!
		log.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );

    }

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        Player player = null;
        if (sender instanceof Player) player = (Player)sender;
        String commandName = command.getName().toLowerCase();
        if (commandName.compareToIgnoreCase("arg") == 0) {
        	if (args.length == 0){
        		if (player != null){
        			player.sendMessage(ChatColor.RED + "Usage: /arg [toggle, info, addfriend, removefriend, friends, unclaim, claim, removeplayer, save, load]");
        		}else{
        			System.out.println("Usage: /arg [removeplayer, save, load, checklastonline]");
        		}
        		return true;
        	}
        	if (args[0].compareToIgnoreCase("checklastonline") == 0){
        		if (player == null){
        			RegionHandler.checkLastOnline();
        		}else{
            		if (pt.cansaveload(player)){
            			RegionHandler.checkLastOnline();
            		} else {
            			player.sendMessage(ChatColor.RED + "You don't have permisson to use that command");
            		}
        		}
        		return true;
        	}
        	if (args[0].compareToIgnoreCase("save") == 0){
        		if (player == null){
        			saveAll();
        		}else{
        			if (pt.cansaveload(player)){
        				saveAll();
        			} else {
        				player.sendMessage(ChatColor.RED + "You don't have permisson to use that command");
        			}
        		}
        		return true;
        	}
        	if (args[0].compareToIgnoreCase("load") == 0){
        		if (player == null){
        			loadAll();
        		}else{
        			if (pt.cansaveload(player)){
        				loadAll();
        			} else {
        				player.sendMessage(ChatColor.RED + "You don't have permisson to use that command");
        			}
        		}
        		return true;
        	}
        	if (args[0].compareToIgnoreCase("removeplayer") == 0){
        		if (player == null){
        			if (args.length == 2){
    					if (RegionHandler.removePlayer(args[1])){
    							System.out.println(args[1] + " no longer owns any regions.");
    					}else{
    							System.out.println(args[1] + " doesn't own any regions.");
    					}
    				}else{
    					System.out.println("Usage: /arg removeplayer [playername]");
    				}
        		}else{
        			if (pt.canbypass(player)){
        				if (args.length == 2){
        					if (RegionHandler.removePlayer(args[1])){
        							player.sendMessage(ChatColor.YELLOW + args[1] + " no longer owns any regions.");
        					}else{
        							player.sendMessage(ChatColor.RED + args[1] + " doesn't own any regions.");
        					}
        				}else{
        						player.sendMessage(ChatColor.RED + "Usage: /arg removeplayer [playername]");
        				}
        			} else {
        					player.sendMessage(ChatColor.RED + "You don't have permisson to use that command");
        			}
        		}
        		return true;
        	}
        }
        if (player != null) {
        	if (args.length >= 1) {
        		if (args[0].compareToIgnoreCase("claim") == 0) {
        			if (pt.canbypassclaim(player)) {
        				//claim region player is standing in
        				RegionHandler.claimChunk(player, player.getLocation().getBlock().getChunk());
        			} else {
        				player.sendMessage(ChatColor.RED + "You don't have arg.claim permisson to use that command");
        			}
        			return true;
        		}
        		if (args[0].compareToIgnoreCase("toggle") == 0) {
        			if (pt.canuser(player)) {
        				//claim region player is standing in
        				RegionHandler.toggleAutoClaim(player);
        			} else {
        				player.sendMessage(ChatColor.RED + "You don't have permisson to use that command");
        			}
        			return true;
        		}
        		if (args[0].compareToIgnoreCase("unclaim") == 0) {
        			if (pt.canuser(player)) {
        				if (args.length >= 4){
        					RegionHandler.unClaimChunk(player, args[1], args[2], args[3]);
        				}else{
        					//claim region player is standing in
        					RegionHandler.unClaimChunk(player, player.getLocation().getBlock().getChunk());
        				} 
        			}else {
        				player.sendMessage(ChatColor.RED + "You don't have permisson to use that command");
        			}
        			return true;
        		}
        		if (args[0].compareToIgnoreCase("list") == 0) {
        			if (args.length == 2){
        				if (pt.canbypass(player)){
        					player.sendMessage(ChatColor.YELLOW + args[1] + " owns: " + RegionHandler.listclaims(args[1]));
        				}else{
        					player.sendMessage(ChatColor.RED + "You don't have permisson to use that command");
        				}
        				return true;
        			}
        			if (pt.canuser(player)) {
        				player.sendMessage(ChatColor.YELLOW + "You own: " + RegionHandler.listclaims(player));
        			} else {
        				player.sendMessage(ChatColor.RED + "You don't have permisson to use that command");
        			}
        			return true;
        		}
        		if (args[0].compareToIgnoreCase("info") == 0) {
        			if (pt.canuser(player)) {
        				if (args.length >= 4){
        					RegionHandler.sendchunkinfo(player, args[1], args[2], args[3]);
        				}else{
        					//info for region player is standing in
        					RegionHandler.sendchunkinfo(player, player.getLocation().getBlock().getChunk());
        				}
        			} else {
        				player.sendMessage(ChatColor.RED + "You don't have permisson to use that command");
        			}
        			return true;
        		}
        		if (args[0].compareToIgnoreCase("addfriend") == 0){
        			if (pt.canuser(player)){
        				if (args[1] != null){
        					FriendHandler.addfriend(player, args[1]);
        				} else return false;
        			} else player.sendMessage(ChatColor.RED + "You don't have permisson to use that command");
        			return true;
        		}
        		if (args[0].compareToIgnoreCase("removefriend") == 0){
        			if (pt.canuser(player)){
        				if (args[1] != null){
        					FriendHandler.removefriend(player, args[1]);
        				} else return false;
        			} else player.sendMessage(ChatColor.RED + "You don't have permisson to use that command");
        			return true;
        		}
        		if (args[0].compareToIgnoreCase("friends") == 0){
        			if (pt.canuser(player)){
        				player.sendMessage(ChatColor.YELLOW + "Your Friends are:");
        				player.sendMessage(ChatColor.YELLOW + FriendHandler.listFriends(player));
        			} else player.sendMessage(ChatColor.RED + "You don't have permisson to use that command");
        			return true;
        		}
        	}
            if (commandName.compareToIgnoreCase("god") == 0) {
            	if (pt.cangodmode(player)) {
            		if (args.length < 1) toggleGod(player);
            		if (args.length == 1) toggleGod(player, args[0]);
            		if (args.length > 1) player.sendMessage(ChatColor.RED + "Too many Arguments");
            	} else {
            	    player.sendMessage(ChatColor.RED + "You don't have permisson to use that command");
            	}
            	return true;
            }
            
        }
		return false;
	}
	

	

	
    //The method toggleGod which if the player is on the hashmap will remove the player else it will add the player.
    //Also sends user a message to notify them.
    public void toggleGod(Player player) {
    	String playername = player.getName().toLowerCase();
		if (ARG.godusers.contains(playername)) {
			ARG.godusers.remove(playername);
			player.sendMessage(ChatColor.YELLOW + "Godmode disabled.");
		} else {
			ARG.godusers.add(playername);
			player.sendMessage(ChatColor.YELLOW + "Godmode enabled.");
		}
	}
    
    
    public void toggleGod(Player player, String playername) {
    	Player receiver = Server.getPlayer(playername);
    	if (receiver != null){
    		if (ARG.godusers.contains(playername)) {
    			ARG.godusers.remove(playername);
    			player.sendMessage(ChatColor.YELLOW + "Godmode disabled on " + playername + ".");
    			receiver.sendMessage(ChatColor.YELLOW + player.getName() + " has disabled godmode on you");
    		} else {
    			ARG.godusers.add(playername);
    			player.sendMessage(ChatColor.YELLOW + "Godmode enabled on " + playername + ".");
    			receiver.sendMessage(ChatColor.YELLOW + player.getName() + " has enabled godmode on you");
    		}
    	}
    }
    
    
    public static boolean isGod(String playername){
    	if (godusers.contains(playername)) return true;
    	return false;
    }
    
	//Used when debugging
	public boolean isDebugging(final Player player) {
		if (debugees.containsKey(player)) {
			return debugees.get(player);
		} else {
			return false;
		}
	}
	public void setDebugging(final Player player, final boolean value) {
    	debugees.put(player, value);
	}
	
	public void saveAll(){
		RegionHandler.saveRegions();
		FriendHandler.saveFriends();
		RegionHandler.saveLastOnline();
	}
	
	private void loadAll() {
		RegionHandler.loadRegions();
		FriendHandler.loadFriends();
		RegionHandler.loadLastOnline();
		PropHandler.loadProperties();
	}
}











