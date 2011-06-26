package net.peoplero.arg;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import com.iConomy.iConomy;

public class RegionHandler {
	
	public static Logger log = Logger.getLogger("Minecraft");
    ARG plugin;
    
	//Create hashmap for players with autoclaim enabled
	public static Set<String> AutoClaimers = new HashSet<String>();
	//Create hashmap for players' current chunk
    private final static Map<String, Chunk> ExistingChunk = new HashMap<String, Chunk>();
    //Create hashmap for owned regions
    private static Map<String, Set<String>> OwnedRegions = new HashMap<String, Set<String>>(); 
    //Create hashmap for tracking the last time a player was online
    private static Map<String, Date> LastOnline = new HashMap<String, Date>(); 
    //Create hashmap for counting changes in players' current chunk
    private final static Map<String, Integer> CurrentChunkc = new HashMap<String, Integer>();
    
    public iConomy iConomy = null;
    
    public static boolean autoclaimdefault;
	public static int maxchunks;
	public static int timetoexpire;
	public static int claimthreshold;
	public static float plotcost;
	
	
	RegionHandler(ARG plugin) {
		this.plugin = plugin;
	}
    
    public void BlockCounter(Player player, Chunk newchunk){
    	String playername = player.getName().toLowerCase();
    	if (AutoClaimers.contains(playername)){
    		//determine chunk block was placed in
    		if (ExistingChunk.get(playername) == null) ExistingChunk.put(playername, null);
    		if (CurrentChunkc.get(playername) == null) CurrentChunkc.put(playername, 0);
    		if (newchunk == ExistingChunk.get(playername)){
    			CurrentChunkc.put(playername, CurrentChunkc.get(playername)+1);
    			if (CurrentChunkc.get(playername) >= claimthreshold){
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
    
    
    public void toggleAutoClaim(Player player){
    	String playername = player.getName().toLowerCase();
    	if (AutoClaimers.contains(playername)){
    		AutoClaimers.remove(playername);
    		player.sendMessage(ChatColor.YELLOW + "AutoClaim is now off.");
    	}else{
    		AutoClaimers.add(playername);
    		player.sendMessage(ChatColor.YELLOW + "AutoClaim is now on.");
    	}
    }

	void claimChunk(Player player, Chunk targetchunk) {
		if (plugin.pt.canuser(player)){
			if (ClaimCheck(targetchunk) != "") {
				player.sendMessage(ChatColor.RED + "Chunk is already claimed!");
				return;
			}
			if(iConomy!=null && !iConomy.getAccount(player.getName()).getHoldings().hasEnough(plotcost))
			{
				player.sendMessage(ChatColor.RED + "You need " + (plotcost - iConomy.getAccount(player.getName()).getHoldings().balance()) + " more coins to claim this.");
				return;
			}
			String strchunk = getstrchunk(targetchunk);
			String playername = player.getName().toLowerCase();
			if (OwnedRegions.containsKey(playername) == false){
				Set<String> list = new HashSet<String>();
				OwnedRegions.put(playername, list);
			}
			Set<String> list = OwnedRegions.get(playername);
			if (list.size() < maxchunks || plugin.pt.canbypass(player)){
				list.add(strchunk);
				OwnedRegions.put(playername, list);
				iConomy.getAccount(player.getName()).getHoldings().subtract(plotcost);
				player.sendMessage(ChatColor.YELLOW + "You have claimed chunk " + strchunk);
				player.sendMessage(ChatColor.YELLOW + "You now own " + list.size() + " chunks.");
			}else{
				player.sendMessage(ChatColor.RED + "Cannot claim! Chunk Cap of " + maxchunks + " reached!");
			}
		}
	}
	
	public void giveChunk(Player player, String receiver, String world, String chunkx, String chunkz) {
		String strchunk = world + " " + chunkx + " " + chunkz;
		String playername = player.getName().toLowerCase();
		String claimcheck = ClaimCheck(strchunk);
		if (claimcheck.compareToIgnoreCase(playername) != 0 && plugin.pt.canbypass(player) == false){
			player.sendMessage(ChatColor.RED + "You don't own chunk " + strchunk);
			return;
		}
		if (OwnedRegions.containsKey(receiver) == false){
			Set<String> list = new HashSet<String>();
			OwnedRegions.put(receiver, list);
		}
		Set<String> list = OwnedRegions.get(receiver);
		if (list.size() < maxchunks || plugin.pt.canbypass(player)){
			if (claimcheck != ""){
				unClaimChunk(player, world, chunkx, chunkz);
			}
			list.add(strchunk);
			OwnedRegions.put(receiver, list);
			player.sendMessage(ChatColor.YELLOW + "You have given '" + strchunk + "' to " + receiver);
			Player owner = ARG.Server.getPlayer(receiver);
			if (owner != null){
				owner.sendMessage(ChatColor.YELLOW + player.getName() + " has given you chunk: " + strchunk);
			}
		}else{
			player.sendMessage(ChatColor.RED + "Cannot give! Chunk Cap of " + maxchunks + " reached!");
		}
	}
	
	public void giveChunk(Player player, String receiver) {
		Chunk targetchunk = player.getLocation().getBlock().getChunk();
		String strchunk = getstrchunk(targetchunk);
		String playername = player.getName().toLowerCase();
		String claimcheck = ClaimCheck(strchunk);
		if (claimcheck.compareToIgnoreCase(playername) != 0 && plugin.pt.canbypass(player) == false){
			player.sendMessage(ChatColor.RED + "You don't own chunk " + strchunk);
			return;
		}
		if (OwnedRegions.containsKey(receiver) == false){
			Set<String> list = new HashSet<String>();
			OwnedRegions.put(receiver, list);
		}
		Set<String> list = OwnedRegions.get(receiver);
		if (list.size() < maxchunks || plugin.pt.canbypass(player)){
			if (claimcheck != ""){
				unClaimChunk(player, targetchunk);
			}
			list.add(strchunk);
			OwnedRegions.put(receiver, list);
			player.sendMessage(ChatColor.YELLOW + "You have given '" + strchunk + "' to " + receiver);
			Player owner = ARG.Server.getPlayer(receiver);
			if (owner != null){
				owner.sendMessage(ChatColor.YELLOW + player.getName() + " has given you chunk: " + strchunk);
			}
		}else{
			player.sendMessage(ChatColor.RED + "Cannot give! Chunk Cap of " + maxchunks + " reached!");
		}
	}
	
	public void unClaimChunk(Player player, Chunk targetchunk) {
		String strowner = ClaimCheck(targetchunk);
		String strplayer = player.getName().toLowerCase();
		String strchunk = getstrchunk(targetchunk);
		if (strowner == ""){
			player.sendMessage(ChatColor.RED + "Chunk is not claimed!");
		}else{
			if (plugin.pt.canbypass(player) || strowner.equalsIgnoreCase(strplayer)){
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
	
	
	public void unClaimChunk(Player player, String world, String chunkx, String chunkz) {
		String strchunk = world+" "+chunkx+" "+chunkz;
		String strowner = ClaimCheck(strchunk);
		String strplayer = player.getName().toLowerCase();
		if (strowner == ""){
			player.sendMessage(ChatColor.RED + "Chunk is not claimed!");
		}else{
			if (plugin.pt.canbypass(player) || strowner.equalsIgnoreCase(strplayer)){
				if (OwnedRegions.get(strowner).remove(strchunk)) {
					player.sendMessage(ChatColor.YELLOW + strowner + " no longer owns " + strchunk + ".");
					if (strplayer.compareToIgnoreCase(strowner) != 0){
						Player owner = ARG.Server.getPlayer(strowner);
						if (owner != null){
							owner.sendMessage(ChatColor.YELLOW + player.getName() + " has unclaimed your chunk: " + strchunk);
						}
					}
				}else{
					player.sendMessage(ChatColor.RED + strowner + " doesn't own " + strchunk + ".");
				}
			}else{
				player.sendMessage(ChatColor.RED + "You Can't do that!");
			}
		}
	}

	public String ClaimCheck(String strchunk) {
		//System.out.println("starting claimcheck");
		for (String ownername : OwnedRegions.keySet()){
			for (String claimedchunk : OwnedRegions.get(ownername)){
				if (claimedchunk.equalsIgnoreCase(strchunk)) return ownername;
			}
		}
		return "";
	}
	
	public String ClaimCheck(Chunk targetchunk) {
		//System.out.println("starting claimcheck");
		String strchunk = getstrchunk(targetchunk);
		for (String ownername : OwnedRegions.keySet()){
			for (String claimedchunk : OwnedRegions.get(ownername)){
				if (claimedchunk.equalsIgnoreCase(strchunk)) return ownername;
			}
		}
		return "";
	}
	
	public String listclaims(Player player){
		String retval = "";
		String playername = player.getName().toLowerCase();
		if (OwnedRegions.containsKey(playername)){
			for (String chunk : OwnedRegions.get(playername)){
				retval = retval + "  " + chunk;
			}
			retval = retval.trim().replace("  ", ", ");
		}
		return retval;
	}
	
	public String listclaims(String playername){
		String retval = "";
		if (OwnedRegions.containsKey(playername)){
			for (String chunk : OwnedRegions.get(playername)){
				retval = retval + "  " + chunk;
			}
			retval = retval.trim().replace("  ", ", ");
		}
		return retval;
	}
	
	public int countclaims(String playername){
		int retval = 0;
		if (OwnedRegions.containsKey(playername)){
			retval = OwnedRegions.get(playername).size();
		}
		return retval;
	}
	
	public int countclaims(Player player){
		String playername = player.getName().toLowerCase();
		int retval = 0;
		if (OwnedRegions.containsKey(playername)){
			retval = OwnedRegions.get(playername).size();
		}
		return retval;
	}

	public boolean CanBuildHere(Player player, Chunk targetchunk) {
		if (plugin.pt.canbypass(player)) return true;
		String playername = player.getName().toLowerCase();
		if (OwnedRegions.containsKey(playername)){
			Set<String> list = OwnedRegions.get(playername);
			String strchunk = getstrchunk(targetchunk);
			for (String string : list){
				if (string.equalsIgnoreCase(strchunk)) return true;
			}
		}
		String owner = ClaimCheck(targetchunk);
		if (plugin.FriendHandler.isafriend(player, owner)) return true;
		return false;
	}


	public void saveRegions() {
		FileHandler.saveMultiMap(OwnedRegions, "Regions.txt");
	}

	public void loadRegions() {
		OwnedRegions = FileHandler.loadMultiMap("Regions.txt");
	}

	public String getstrchunk(Chunk targetchunk) {
		String strchunk = "";
		if (targetchunk != null){
			strchunk = targetchunk.getWorld().getName().toLowerCase() + " " + targetchunk.getX() + " " + targetchunk.getZ();
		}
		return strchunk;
	}

	public void sendchunkinfo(Player player, Chunk targetchunk) {
		String strclaimed = ClaimCheck(targetchunk);
		if (strclaimed == ""){
			player.sendMessage(ChatColor.YELLOW + "Chunk '" + getstrchunk(targetchunk) + "' is not claimed.");
		}else{
			player.sendMessage(ChatColor.YELLOW + "Chunk '" + getstrchunk(targetchunk) + "' is claimed by " + strclaimed + ".");
		}
	}
	
	
	public void sendchunkinfo(Player player, String world, String chunkx, String chunkz) {
		String strchunk = world + " " + chunkx + " " + chunkz;
		String strclaimed = ClaimCheck(strchunk);
		if (strclaimed == ""){
			player.sendMessage(ChatColor.YELLOW + "Chunk '" + strchunk + "' is not claimed.");
		}else{
			player.sendMessage(ChatColor.YELLOW + "Chunk '" + strchunk + "' is claimed by " + strclaimed + ".");
		}
	}


	public boolean removePlayer(String playername) {
		boolean retvalue = false;
		if (OwnedRegions.containsKey(playername)){
			OwnedRegions.remove(playername);
			retvalue = true;
		}
		if (LastOnline.containsKey(playername)){
			LastOnline.remove(playername);
			retvalue = true;
		}
		if (FriendHandler.FriendsList.containsKey(playername)){
			FriendHandler.FriendsList.remove(playername);
			retvalue = true;
		}
		return retvalue;
	}


	public void saveLastOnline() {
		FileHandler.saveHashMap(LastOnline, "LastOnline.txt");
	}


	public void loadLastOnline() {
		LastOnline = FileHandler.loadHashMap("LastOnline.txt");
		Date now = new Date();
		for (String playername : OwnedRegions.keySet()){
			if (LastOnline.containsKey(playername) == false){
				LastOnline.put(playername, now);
			}
		}
		for (String playername : FriendHandler.FriendsList.keySet()){
			if (LastOnline.containsKey(playername) == false){
				LastOnline.put(playername, now);
			}
		}
	}


	public void updateLastOnline(Player player) {
		Date now = new Date();
		String playername = player.getName().toLowerCase();
		LastOnline.put(playername, now);
	}
	
	public void checkLastOnline(){
		int counter = 0;
		long now = new Date().getTime();
		Set<String> list = new HashSet<String>();
		for (String playername : LastOnline.keySet()){
			long lo = LastOnline.get(playername).getTime();
			if (lo < now-timetoexpire*86400000L){
				//long temp = lo-now;
				//System.out.println(playername + " comparison: " + temp);
				//removePlayer(playername);
				list.add(playername);
				//counter = counter +1;
				//log.info("[ARG] "+playername+" has expired.");
			}
		}
		for (String playername : list){
			if (removePlayer(playername)){
				counter = counter + 1;
				log.info("[ARG] "+playername+" has expired.");
			}
		}
		log.info("[ARG] "+counter+" players expired.");
	}


	public void removeClaimer(Player player) {
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
