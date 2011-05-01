package net.peoplero.arg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class RegionHandler {
    
	//Create hashmap for players' current chunk
    private final static Map<String, Chunk> ExistingChunk = new HashMap<String, Chunk>();
    //Create hashmap for owned regions
    private static Map<String, ArrayList<String>> OwnedRegions = new HashMap<String, ArrayList<String>>(); 
    //Create hashmap for counting changes in players' current chunk
    private final static Map<String, Integer> CurrentChunkc = new HashMap<String, Integer>();
    
    public static void BlockCounter(Player player, Chunk newchunk){
    	String playername = player.getDisplayName().toLowerCase();
    	//determine chunk block was placed in
    
    	if (ExistingChunk.get(playername) == null) ExistingChunk.put(playername, null);
    	if (CurrentChunkc.get(playername) == null) CurrentChunkc.put(playername, 0);
    	if (newchunk == ExistingChunk.get(playername)){
    		CurrentChunkc.put(playername, CurrentChunkc.get(playername)+1);
    	if (CurrentChunkc.get(playername) > 15){
    		ClaimRegion(player, newchunk);
    		CurrentChunkc.put(playername, 1);
    	}
    	}else{
    		CurrentChunkc.put(playername, 1);
    	}

    	ExistingChunk.put(playername, newchunk);
    	//player.sendMessage("Chunk Count at " + CurrentChunkc.get(playername) + " for chunk: " + ExistingChunk.get(playername));
    }

	static void ClaimRegion(Player player, Chunk chunk) {
		if (ClaimCheck(chunk) != "") {
			player.sendMessage(ChatColor.RED + "Chunk is already claimed!");
			return;
		}
		String strchunk = chunk.getWorld().getName().toLowerCase() + " " + chunk.getX() + " " + chunk.getZ();
		String playername = player.getDisplayName().toLowerCase();
		if (OwnedRegions.containsKey(playername) == false){
			ArrayList<String> list = new ArrayList<String>();
			OwnedRegions.put(playername, list);
		}
		ArrayList<String> list = OwnedRegions.get(playername);
		list.add(strchunk);
		OwnedRegions.put(playername, list);
		player.sendMessage(ChatColor.YELLOW + "You have claimed chunk " + strchunk);
		player.sendMessage(ChatColor.YELLOW + "You now own " + list.size() + " regions.");
//		for (String string : list){
//			player.sendMessage(ChatColor.YELLOW + string);
//		}
	}

	
	public static String ClaimCheck(Chunk targetchunk) {
		//System.out.println("starting claimcheck");
		String strchunk = targetchunk.getWorld().getName().toLowerCase() + " " + targetchunk.getX() + " " + targetchunk.getZ();
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
			String strchunk = targetchunk.getWorld().getName().toLowerCase() + " " + targetchunk.getX() + " " + targetchunk.getZ();
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

}
