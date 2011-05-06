package net.peoplero.arg;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class RegionHandler {
	
	public static Logger log = Logger.getLogger("Minecraft");
    
	//Create hashmap for players with autoclaim enabled
	private static ArrayList<String> AutoClaimers = new ArrayList<String>();
	//Create hashmap for players' current chunk
    private final static Map<String, Chunk> ExistingChunk = new HashMap<String, Chunk>();
    //Create hashmap for owned regions
    private static Map<String, ArrayList<String>> OwnedRegions = new HashMap<String, ArrayList<String>>(); 
    //Create hashmap for tracking the last time a player was online
    private static Map<String, Date> LastOnline = new HashMap<String, Date>(); 
    //Create hashmap for counting changes in players' current chunk
    private final static Map<String, Integer> CurrentChunkc = new HashMap<String, Integer>();
    
    public static void BlockCounter(Player player, Chunk newchunk){
    	String playername = player.getName().toLowerCase();
    	if (AutoClaimers.contains(playername)){
    		//determine chunk block was placed in
    		if (ExistingChunk.get(playername) == null) ExistingChunk.put(playername, null);
    		if (CurrentChunkc.get(playername) == null) CurrentChunkc.put(playername, 0);
    		if (newchunk == ExistingChunk.get(playername)){
    			CurrentChunkc.put(playername, CurrentChunkc.get(playername)+1);
    			if (CurrentChunkc.get(playername) > 15){
    				claimChunk(player, newchunk);
    				CurrentChunkc.put(playername, 0);
    			}
    		}else{
    			CurrentChunkc.put(playername, 1);
    		}

    		ExistingChunk.put(playername, newchunk);
    		//player.sendMessage("Chunk Count at " + CurrentChunkc.get(playername) + " for chunk: " + ExistingChunk.get(playername));
    	}
    }
    
    
    public static void toggleAutoClaim(Player player){
    	String playername = player.getName().toLowerCase();
    	if (AutoClaimers.contains(playername)){
    		AutoClaimers.remove(playername);
    		player.sendMessage(ChatColor.YELLOW + "AutoClaim is now off.");
    	}else{
    		AutoClaimers.add(playername);
    		player.sendMessage(ChatColor.YELLOW + "AutoClaim is now on.");
    	}
    }

	static void claimChunk(Player player, Chunk targetchunk) {
		if (ClaimCheck(targetchunk) != "") {
			player.sendMessage(ChatColor.RED + "Chunk is already claimed!");
			return;
		}
		String strchunk = getstrchunk(targetchunk);
		String playername = player.getName().toLowerCase();
		if (OwnedRegions.containsKey(playername) == false){
			ArrayList<String> list = new ArrayList<String>();
			OwnedRegions.put(playername, list);
		}
		ArrayList<String> list = OwnedRegions.get(playername);
		list.add(strchunk);
		OwnedRegions.put(playername, list);
		player.sendMessage(ChatColor.YELLOW + "You have claimed chunk " + strchunk);
		player.sendMessage(ChatColor.YELLOW + "You now own " + list.size() + " chunks.");
	}
	
	public static void unClaimChunk(Player player, Chunk targetchunk) {
		String strowner = ClaimCheck(targetchunk);
		String strplayer = player.getName().toLowerCase();
		String strchunk = getstrchunk(targetchunk);
		if (strowner == ""){
			player.sendMessage(ChatColor.RED + "Chunk is not claimed!");
		}else{
			if (ARG.canbypass(player) || strowner.equalsIgnoreCase(strplayer)){
				if (OwnedRegions.get(strowner).remove(strchunk)) {
					player.sendMessage(ChatColor.YELLOW + strowner + " no longer owns " + strchunk + ".");
					if (strplayer.compareToIgnoreCase(strowner) != 0){
						Player owner = ARG.Server.getPlayer(strowner);
						if (owner != null){
							owner.sendMessage(ChatColor.YELLOW + player.getName() + " has unclaimed your chunk: " + strchunk);
						}
					}
				}else{
					player.sendMessage(ChatColor.RED + strowner + " doesn't own " + getstrchunk(targetchunk) + ".");
				}
			}else{
				player.sendMessage(ChatColor.RED + "You Can't do that!");
			}
		}
	}

	
	public static String ClaimCheck(Chunk targetchunk) {
		//System.out.println("starting claimcheck");
		String strchunk = getstrchunk(targetchunk);
		for (String ownername : OwnedRegions.keySet()){
			for (String claimedchunk : OwnedRegions.get(ownername)){
				if (claimedchunk.equalsIgnoreCase(strchunk)) return ownername;
			}
		}
//		for (ArrayList<String> list : OwnedRegions.values()){
//			for (String string : list){
//				if (string.equalsIgnoreCase(strchunk)) return true;
//			}
//		}
		return "";
	}

	public static boolean CanBuildHere(Player player, Chunk targetchunk) {
		if (ARG.canbypass(player)) return true;
		String playername = player.getName().toLowerCase();
		if (OwnedRegions.containsKey(playername)){
			ArrayList<String> list = OwnedRegions.get(playername);
			String strchunk = getstrchunk(targetchunk);
			for (String string : list){
				if (string.equalsIgnoreCase(strchunk)) return true;
			}
		}
		String owner = ClaimCheck(targetchunk);
		if (FriendHandler.isafriend(player, owner)) return true;
		return false;
	}


	public static void saveRegions() {
		FileHandler.saveMultiMap(OwnedRegions, "Regions.txt");
	}

	public static void loadRegions() {
		OwnedRegions = FileHandler.loadMultiMap("Regions.txt");
	}

	public static String getstrchunk(Chunk targetchunk) {
		String strchunk = "";
		if (targetchunk != null){
			strchunk = targetchunk.getWorld().getName().toLowerCase() + " " + targetchunk.getX() + " " + targetchunk.getZ();
		}
		return strchunk;
	}

	public static void sendchunkinfo(Player player, Chunk targetchunk) {
		String strclaimed = ClaimCheck(targetchunk);
		if (strclaimed == ""){
			player.sendMessage(ChatColor.YELLOW + "Chunk '" + getstrchunk(targetchunk) + "' is not claimed.");
		}else{
			player.sendMessage(ChatColor.YELLOW + "Chunk '" + getstrchunk(targetchunk) + "' is claimed by " + strclaimed + ".");
		}
	}


	public static boolean removePlayer(String playername) {
		boolean retvalue = false;
		if (OwnedRegions.containsKey(playername)){
			OwnedRegions.remove(playername);
			retvalue = true;
		}
		if (LastOnline.containsKey(playername)){
			LastOnline.remove(playername);
			retvalue = true;
		}
		return retvalue;
	}


	public static void saveLastOnline() {
		FileHandler.saveHashMap(LastOnline, "LastOnline.txt");
	}


	public static void loadLastOnline() {
		LastOnline = FileHandler.loadHashMap("LastOnline.txt");
	}


	public static void updateLastOnline(Player player) {
		Date now = new Date();
		String playername = player.getName().toLowerCase();
		LastOnline.put(playername, now);
	}
	
	public static void checkLastOnline(){
		int counter = 0;
		long now = new Date().getTime();
		for (String playername : LastOnline.keySet()){
			long lo = LastOnline.get(playername).getTime();
			if (lo < now-30*86400000L){
				long temp = lo-now;
				System.out.println(playername + " comparison: " + temp);
				removePlayer(playername);
				counter = counter +1;
				log.info("[ARG] "+playername+" has expired.");
			}
		}
		log.info("[ARG] "+counter+" players expired.");
	}


	public static void removeClaimer(Player player) {
		String playername = player.getName().toLowerCase();
		if (AutoClaimers.contains(playername)){
			AutoClaimers.remove(playername);
		}
		if (ExistingChunk.containsKey(playername)){
			ExistingChunk.remove(playername);
		}
		if (CurrentChunkc.containsKey(playername)){
			CurrentChunkc.remove(playername);
		}
	}

}
