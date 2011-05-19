package net.peoplero.arg;

import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class PermissionThing {

    public PermissionHandler permissionHandler = null;
    public static Logger log = Logger.getLogger("Minecraft");
    ARG plugin;
    
	PermissionThing(ARG plugin) {
		this.plugin = plugin;
	}
    
	  public void setupPermissions() {
	      Plugin permissionsPlugin = ARG.Server.getPluginManager().getPlugin("Permissions");

	      if (permissionHandler == null) {
	          if (permissionsPlugin != null) {
	              permissionHandler = ((Permissions) permissionsPlugin).getHandler();
	              plugin.UsePermissions = true;
	              log.info("ARG has detected Permissions");
	          } else {
	              log.info("Permission system not detected, defaulting to OP");
	          }
	      }
	  }
	  
		public boolean canbypass(Player player) {
		    if (plugin.UsePermissions) {
		        return permissionHandler.has(player, "arg.bypass");
		    }
		    return player.isOp();
		}
		public boolean canbypassclaim(Player player) {
		    if (plugin.UsePermissions) {
		        return permissionHandler.has(player, "arg.claim");
		    }
		    return player.isOp();
		}
		public boolean cangodmode(Player player) {
		    if (plugin.UsePermissions) {
		        return permissionHandler.has(player, "arg.god");
		    }
		    return player.isOp();
		}
		public boolean cansaveload(Player player){
		    if (plugin.UsePermissions) {
		        return permissionHandler.has(player, "arg.saveload");
		    }
		    return player.isOp();
		}
		public boolean canuser(Player player){
		    if (plugin.UsePermissions) {
		        return permissionHandler.has(player, "arg.user");
		    }
		    return true;
		}
    
	
}
