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

public class ARGPlayerListener extends PlayerListener {
	
	
    @SuppressWarnings("unused")
    private final ARG plugin;

    public ARGPlayerListener(final ARG plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Chunk newchunk = event.getBlockClicked().getChunk();
    	final Player player = event.getPlayer();
    	String claimcheck = RegionHandler.ClaimCheck(newchunk);
        if (claimcheck == ""){
        	//RegionHandler.BlockCounter(player, newchunk);
        }else{
        	if (RegionHandler.CanBuildHere(player, newchunk) == false){
        		event.setCancelled(true);
        		player.sendMessage(ChatColor.RED + "Chunk owned by " + claimcheck + ". You can't bucket here.");
        	}
        }
    }
    
    @Override
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Chunk newchunk = event.getBlockClicked().getChunk();
    	final Player player = event.getPlayer();
    	String claimcheck = RegionHandler.ClaimCheck(newchunk);
        if (claimcheck == ""){
        	//RegionHandler.BlockCounter(player, newchunk);
        }else{
        	if (RegionHandler.CanBuildHere(player, newchunk) == false){
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
    			Chunk newchunk = clickedblock.getChunk();
    			String claimcheck = RegionHandler.ClaimCheck(newchunk);
    			if (claimcheck == ""){
    				//RegionHandler.BlockCounter(player, newchunk);
    			}else{
    				if (RegionHandler.CanBuildHere(player, newchunk) == false){
    					event.setCancelled(true);
    					player.sendMessage(ChatColor.RED + "Chunk owned by " + claimcheck + ". You can't steal here.");
    				}
    			}
    		}
    		if (ARG.canuser(player)){
    			if (event.getPlayer().getItemInHand().getType() == Material.STRING){
    				if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
    					Chunk targetchunk = event.getClickedBlock().getChunk();
    					RegionHandler.sendchunkinfo(player, targetchunk);
    				}
    			}
    		}
    		if (ARG.canuser(player)){
    			if (event.getPlayer().getItemInHand().getType() == Material.FEATHER){
    				if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
    					Chunk targetchunk = event.getClickedBlock().getChunk();
    					RegionHandler.unClaimChunk(player, targetchunk);
    				}
    			}
    		}
    	}
    }
    
}
