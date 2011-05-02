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

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

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
    public static String version = "0.2";
    public static String codename = "Cheeseburger";
    public static PermissionHandler permissionHandler;
    private static boolean UsePermissions;
    
    //Create the hashmap debugees
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    //Create a list of godmode users
	public final static ArrayList<String> godusers = new ArrayList<String>();
	
	//Links the ARGBlockListener
    private final ARGBlockListener blockListener = new ARGBlockListener(this);
    private final ARGEntityListener entityListener = new ARGEntityListener(this);
    private final ARGPlayerListener playerListener = new ARGPlayerListener(this);
    

    public ARG() {
        new File("plugins" + File.separator + "ARG" + File.separator).mkdirs();
    }
    
    public void onLoad(){
    	
    }
    
    public void onDisable() {
    	log.info("ARG is saving Regions...");
    	saveAll();
    	log.info("Done!");
        log.info("ARG Disabled");
    }

    public void onEnable() {
    	Server = this.getServer();
    	description = this.getDescription();
    	directory = getDataFolder();
		//Create the pluginmanager pm.
		PluginManager pm = getServer().getPluginManager();
	    //Create BlockPlaced listener
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.High, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.High, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, playerListener, Event.Priority.High, this);
		pm.registerEvent(Event.Type.PLAYER_BUCKET_FILL, playerListener, Event.Priority.High, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.High, this);

		//Get the information from the yml file.
		PluginDescriptionFile pdfFile = this.getDescription();
		//setup permissions
		setupPermissions();
		
		//load pre-existing values from files
		log.info("ARG is checking/loading files...");
		loadAll();
		
		//Print that the plugin has been enabled!
		log.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );

    }

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        Player player = null;
        String commandName = command.getName().toLowerCase();
        if (sender instanceof Player) {
        	player = (Player)sender;
        	if (commandName.compareToIgnoreCase("arg") == 0) {
        		if (args.length == 0){
        				player.sendMessage(ChatColor.RED + "Usage: /arg [toggle, info, addfriend, removefriend, friends, unclaim, claim, save, load]");
        			return true;
        		}
        		if (args[0].compareToIgnoreCase("claim") == 0) {
        			if (canbypass(player)) {
        				//claim region player is standing in
        				RegionHandler.claimChunk(player, player.getLocation().getBlock().getChunk());
        			} else {
        				player.sendMessage(ChatColor.RED + "You don't have permisson to use that command");
        			}
        			return true;
        		}
        		if (args[0].compareToIgnoreCase("toggle") == 0) {
        			if (canuser(player)) {
        				//claim region player is standing in
        				RegionHandler.toggleAutoClaim(player);
        			} else {
        				player.sendMessage(ChatColor.RED + "You don't have permisson to use that command");
        			}
        			return true;
        		}
        		if (args[0].compareToIgnoreCase("unclaim") == 0) {
        			if (canbypass(player)) {
        				//claim region player is standing in
        				RegionHandler.unClaimChunk(player, player.getLocation().getBlock().getChunk());
        			} else {
        				player.sendMessage(ChatColor.RED + "You don't have permisson to use that command");
        			}
        			return true;
        		}
        		if (args[0].compareToIgnoreCase("info") == 0) {
        			if (canuser(player)) {
        				//claim region player is standing in
        				RegionHandler.sendchunkinfo(player, player.getLocation().getBlock().getChunk());
        			} else {
        				player.sendMessage(ChatColor.RED + "You don't have permisson to use that command");
        			}
        			return true;
        		}
        		if (args[0].compareToIgnoreCase("save") == 0){
        			if (cansaveload(player)){
        				saveAll();
        			} else {
        				player.sendMessage(ChatColor.RED + "You don't have permisson to use that command");
        			}
        			return true;
        		}
        		if (args[0].compareToIgnoreCase("load") == 0){
        			if (cansaveload(player)){
        				loadAll();
        			} else {
        				player.sendMessage(ChatColor.RED + "You don't have permisson to use that command");
        			}
        			return true;
        		}
        		if (args[0].compareToIgnoreCase("addfriend") == 0){
        			if (canuser(player)){
        				if (args[1] != null){
        					FriendHandler.addfriend(player, args[1]);
        				} else return false;
        			} else player.sendMessage(ChatColor.RED + "You don't have permisson to use that command");
        			return true;
        		}
        		if (args[0].compareToIgnoreCase("removefriend") == 0){
        			if (canuser(player)){
        				if (args[1] != null){
        					FriendHandler.removefriend(player, args[1]);
        				} else return false;
        			} else player.sendMessage(ChatColor.RED + "You don't have permisson to use that command");
        			return true;
        		}
        		if (args[0].compareToIgnoreCase("friends") == 0){
        			if (canuser(player)){
        				player.sendMessage(ChatColor.YELLOW + "Your Friends are:");
        				player.sendMessage(ChatColor.YELLOW + FriendHandler.listFriends(player));
        			} else player.sendMessage(ChatColor.RED + "You don't have permisson to use that command");
        			return true;
        		}
        	}
            if (commandName.compareToIgnoreCase("god") == 0) {
            	if (cangodmode(player)) {
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
	
	  private void setupPermissions() {
	      Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

	      if (ARG.permissionHandler == null) {
	          if (permissionsPlugin != null) {
	              ARG.permissionHandler = ((Permissions) permissionsPlugin).getHandler();
	              log.info("ARG has detected Permissions");
	          } else {
	              log.info("Permission system not detected, defaulting to OP");
	          }
	      }
	  }
	
		public static boolean canbypass(Player player) {
		    if (UsePermissions) {
		        return ARG.permissionHandler.has(player, "arg.bypass");
		    }
		    return player.isOp();
		}
		public static boolean cangodmode(Player player) {
		    if (UsePermissions) {
		        return ARG.permissionHandler.has(player, "arg.god");
		    }
		    return player.isOp();
		}
		public static boolean cansaveload(Player player){
		    if (UsePermissions) {
		        return ARG.permissionHandler.has(player, "arg.saveload");
		    }
		    return player.isOp();
		}
		public static boolean canuser(Player player){
		    if (UsePermissions) {
		        return ARG.permissionHandler.has(player, "arg.user");
		    }
		    return true;
		}
	
    //The method toggleVision which if the player is on the hashmap will remove the player else it will add the player.
    //Also sends user a message to notify them.
    public void toggleGod(Player player) {
    	String playername = player.getDisplayName().toLowerCase();
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
    			receiver.sendMessage(player.getDisplayName() + " has disabled godmode on you");
    		} else {
    			ARG.godusers.add(playername);
    			player.sendMessage(ChatColor.YELLOW + "Godmode enabled on " + playername + ".");
    			receiver.sendMessage(player.getDisplayName() + " has enabled godmode on you");
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
	}
	
	private void loadAll() {
		// TODO Auto-generated method stub
		RegionHandler.loadRegions();
		FriendHandler.loadFriends();
	}
}











