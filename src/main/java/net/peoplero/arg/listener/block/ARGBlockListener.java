package net.peoplero.arg.listener.block;

import net.peoplero.arg.ARG;
import net.peoplero.arg.RegionHandler;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class ARGBlockListener extends BlockListener 
{
    @SuppressWarnings("unused")
    private final ARG plugin;
    
    //public RegionHandler RegionHandlerO = new RegionHandler();
    
    public ARGBlockListener(final ARG plugin)
    {
        this.plugin = plugin;
    }
    
	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		//gather info on where the block was placed and who placed it
        Chunk newchunk = event.getBlock().getChunk();
    	final Player player = event.getPlayer();
    	String claimcheck = RegionHandler.ClaimCheck(newchunk);
        if (claimcheck == ""){
        	RegionHandler.BlockCounter(player, newchunk);
        }else{
        	if (RegionHandler.CanBuildHere(player, newchunk) == false){
        		event.setBuild(false);
        		player.sendMessage(ChatColor.RED + "Chunk owned by " + claimcheck + ". You can't build here.");
        	}
        }
	}
    
	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		//gather info on where the block was broken and who broke it
        Chunk newchunk = event.getBlock().getChunk();
        final Player player = event.getPlayer();
    	String claimcheck = RegionHandler.ClaimCheck(newchunk);
        if (claimcheck == ""){
        	//RegionHandlerO.BlockCounter(player, newchunk);
        }else{
        	if (RegionHandler.CanBuildHere(player, newchunk)){
        	}else{
        		event.setCancelled(true);
        		player.sendMessage(ChatColor.RED + "Chunk owned by " + claimcheck + ". You can't mine here.");
        	}
        }
	}
}
