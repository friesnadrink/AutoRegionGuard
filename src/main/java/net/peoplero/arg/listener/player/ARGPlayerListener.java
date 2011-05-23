package net.peoplero.arg.listener.player;

import net.peoplero.arg.ARG;
import net.peoplero.arg.RegionHandler;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ARGPlayerListener extends PlayerListener {
	

    private final ARG plugin;
    public static int infotool;
    public static int unclaimtool;
    public static boolean protectchests;

    public ARGPlayerListener(final ARG plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Chunk newchunk = event.getBlockClicked().getChunk();
    	final Player player = event.getPlayer();
    	String claimcheck = plugin.RegionHandler.ClaimCheck(newchunk);
        if (claimcheck == ""){
        	//RegionHandler.BlockCounter(player, newchunk);
        }else{
        	if (plugin.RegionHandler.CanBuildHere(player, newchunk) == false){
        		event.setCancelled(true);
        		player.sendMessage(ChatColor.RED + "Chunk owned by " + claimcheck + ". You can't bucket here.");
        	}
        }
    }
    
    @Override
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Chunk newchunk = event.getBlockClicked().getChunk();
    	final Player player = event.getPlayer();
    	String claimcheck = plugin.RegionHandler.ClaimCheck(newchunk);
        if (claimcheck == ""){
        	//RegionHandler.BlockCounter(player, newchunk);
        }else{
        	if (plugin.RegionHandler.CanBuildHere(player, newchunk) == false){
        		event.setCancelled(true);
        		player.sendMessage(ChatColor.RED + "Chunk owned by " + claimcheck + ". You can't bucket here.");
        	}
        }
    }
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
    	Block clickedblock = event.getClickedBlock();
    	if (clickedblock != null){
    		Player player = event.getPlayer();
    		if (clickedblock.getType() == Material.CHEST){
    			if (protectchests){
    				Chunk newchunk = clickedblock.getChunk();
    				String claimcheck = plugin.RegionHandler.ClaimCheck(newchunk);
    				if (claimcheck == ""){
    					//RegionHandler.BlockCounter(player, newchunk);
    				}else{
    					if (plugin.RegionHandler.CanBuildHere(player, newchunk) == false){
    						event.setCancelled(true);
    						player.sendMessage(ChatColor.RED + "Chunk owned by " + claimcheck + ". You can't steal here.");
    					}
    				}
    			}
    		}
    		if (plugin.pt.canuser(player)){
    			if (event.getPlayer().getItemInHand().getType().getId() == infotool){
    				if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
    					Chunk targetchunk = event.getClickedBlock().getChunk();
    					plugin.RegionHandler.sendchunkinfo(player, targetchunk);
    				}
    			}
    		}
    		if (plugin.pt.canuser(player)){
    			if (event.getPlayer().getItemInHand().getType().getId() == unclaimtool){
    				if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
    					Chunk targetchunk = event.getClickedBlock().getChunk();
    					plugin.RegionHandler.unClaimChunk(player, targetchunk);
    				}
    			}
    		}
    	}
    }
    
    @Override
    public void onPlayerLogin(PlayerLoginEvent event) {
    	Player player = event.getPlayer();
    	if (plugin.pt.canuser(player)){
    		plugin.RegionHandler.updateLastOnline(player);
    		if (RegionHandler.autoclaimdefault){
    			String playername = player.getName().toLowerCase();
    			RegionHandler.AutoClaimers.add(playername);
    		}
    	}
    }
    
    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
    	Player player = event.getPlayer();
    	if (plugin.pt.canuser(player)){
    		String playername = player.getName().toLowerCase();
    		plugin.RegionHandler.updateLastOnline(player);
    		plugin.RegionHandler.removeClaimer(player);
    		if (RegionHandler.AutoClaimers.contains(playername)){
    			RegionHandler.AutoClaimers.remove(playername);
    		}
    	}
    }
    
}
