package net.peoplero.arg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class RegionHandler {
    
	//Create hashmap for players with autoclaim enabled
	private static ArrayList<String> AutoClaimers = new ArrayList<String>();
	//Create hashmap for players' current chunk
    private final static Map<String, Chunk> ExistingChunk = new HashMap<String, Chunk>();
    //Create hashmap for owned regions
    private static Map<String, ArrayList<String>> OwnedRegions = new HashMap<String, ArrayList<String>>(); 
    //Create hashmap for counting changes in players' current chunk
    private final static Map<String, Integer> CurrentChunkc = new HashMap<String, Integer>();
    
    public static void BlockCounter(Player player, Chunk newchunk){
    	String playername = player.getDisplayName().toLowerCase();
    	if (AutoClaimers.contains(playername)){
    		//determine chunk block was placed in
    		if (ExistingChunk.get(playername) == null) ExistingChunk.put(playername, null);
    		if (CurrentChunkc.get(playername) == null) CurrentChunkc.put(playername, 0);
    		if (newchunk == ExistingChunk.get(playername)){
    			CurrentChunkc.put(playername, CurrentChunkc.get(playername)+1);
    			if (CurrentChunkc.get(playername) > 15){
    				claimChunk(player, newchunk);
    				CurrentChunkc.put(playername, 1);
    			}
    		}else{
    			CurrentChunkc.put(playername, 1);
    		}

    		ExistingChunk.put(playername, newchunk);
    		//player.sendMessage("Chunk Count at " + CurrentChunkc.get(playername) + " for chunk: " + ExistingChunk.get(playername));
    	}
    }
    
    
    public static void toggleAutoClaim(Player player){
    	String playername = player.getDisplayName().toLowerCase();
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
		String playername = player.getDisplayName().toLowerCase();
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
		String strplayer = player.getDisplayName().toLowerCase();
		String strchunk = getstrchunk(targetchunk);
		if (strowner == ""){
			player.sendMessage(ChatColor.RED + "Chunk is not claimed!");
		}else{
			if (ARG.canbypass(player) || strowner == strplayer){
				if (OwnedRegions.get(strowner).remove(strchunk)) {
					player.sendMessage(ChatColor.YELLOW + strowner + " no longer owns " + strchunk + ".");
					if (strplayer.compareToIgnoreCase(strowner) != 0){
						Player owner = ARG.Server.getPlayer(strowner);
						if (owner != null){
							owner.sendMessage(ChatColor.YELLOW + player.getDisplayName() + " has unclaimed your chunk: " + strchunk);
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
		String playername = player.getDisplayName().toLowerCase();
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
		String strclaimed = RegionHandler.ClaimCheck(targetchunk);
		if (strclaimed == ""){
			player.sendMessage(ChatColor.YELLOW + "Chunk '" + RegionHandler.getstrchunk(targetchunk) + "' is not claimed.");
		}else{
			player.sendMessage(ChatColor.YELLOW + "Chunk '" + RegionHandler.getstrchunk(targetchunk) + "' is claimed by " + strclaimed + ".");
		}
	}

}
