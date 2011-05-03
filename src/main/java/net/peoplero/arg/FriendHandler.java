package net.peoplero.arg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FriendHandler {
	
	private static Map<String, ArrayList<String>> FriendsList = new HashMap<String, ArrayList<String>>(); 

	public static void addfriend(Player player, String friend) {
		String playername = player.getName().toLowerCase();
		if (FriendsList.containsKey(playername) == false){
			ArrayList<String> blanklist = new ArrayList<String>();
			FriendsList.put(playername, blanklist);
		}
		ArrayList<String> list = FriendsList.get(playername);
		list.add(friend.toLowerCase());
		FriendsList.put(playername, list);
		player.sendMessage(ChatColor.YELLOW + "Added friend: " + friend);
	}

	public static void removefriend(Player player, String friend) {
		String playername = player.getName().toLowerCase();
		if (FriendsList.containsKey(playername) == false){
			player.sendMessage(ChatColor.RED + "You don't have any friends!");
			return;
		}
		if (FriendsList.get(playername).remove(friend.toLowerCase())){
			player.sendMessage(ChatColor.YELLOW + "Removed friend " + friend + ".");
		} else player.sendMessage(ChatColor.RED + "You don't have a friend named " + friend + ".");
	}
	
	public static boolean isafriend(Player player, String owner){
		String playername = player.getName().toLowerCase();
		if (FriendsList.containsKey(owner.toLowerCase())){
			if (FriendsList.get(owner.toLowerCase()).contains(playername)) return true;
		}
		
		return false;
	}

	public static void saveFriends() {
		FileHandler.saveMultiMap(FriendsList, "FriendsList.txt");
		
	}

	public static void loadFriends() {
		FriendsList = FileHandler.loadMultiMap("FriendsList.txt");
		
	}

	public static String listFriends(Player player) {
		String friends = "";
		for (String friend : FriendsList.get(player.getName().toLowerCase())){
			friends = friends + " " + friend;
		}
		friends = friends.trim().replace(" ", ", ");
		return friends;
	}

}
