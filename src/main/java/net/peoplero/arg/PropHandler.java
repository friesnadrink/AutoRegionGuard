package net.peoplero.arg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import net.peoplero.arg.listener.block.ARGBlockListener;
import net.peoplero.arg.listener.player.ARGPlayerListener;

public class PropHandler {

    static File propfile = new File(FileHandler.strpath + "ARG.properties");
    public static Properties prop = new Properties();
    
    static String description = "maxchunks - (default 30)Maximum number of chunks a player can own.\n" +
    "timetoexpire - (default 30)Time in days before a user will expire (deletes players friends and unclaims their chunks)\n" + 
    "autoclaimdefault - (default true)true/false whether autoclaim will be on(true) or off(false) by default\n" + 
    "claimthreshold - (default 16)How many blocks a user needs to place in a chunk before it gets autoclaimed\n" + 
    "fireprotection - (default true)true/false Protects claimed chunks from fire spread(blocks can still be set ablaze by flint and finder/lava)\n" + 
    "infotool - (default 287)ItemID to use as infotool\n" + 
    "unclaimtool - (default 288)ItemID to use as unclaimtool\n" +
    "protectchests - (default true)true/false weather to protect chests in claimed chunks or not";
    
    static void loadProperties(){
    	RegionHandler.maxchunks = 30;
    	RegionHandler.timetoexpire = 30;
    	RegionHandler.autoclaimdefault = true;
    	RegionHandler.claimthreshold = 16;
    	ARGBlockListener.fireprotection = true;
    	ARGPlayerListener.infotool = 287;
    	ARGPlayerListener.unclaimtool = 288;
    	ARGPlayerListener.protectchests = true;
    	
    	if(!propfile.exists()){
    		try { //try catch clause explained below in tutorial
    			propfile.createNewFile(); //creates the file zones.dat
    			storevalue("maxchunks", RegionHandler.maxchunks);
    			storevalue("timetoexpire", RegionHandler.timetoexpire);
    			storevalue("autoclaimdefault", RegionHandler.autoclaimdefault);
    			storevalue("claimthreshold", RegionHandler.claimthreshold);
    			storevalue("infotool", ARGPlayerListener.infotool);
    			storevalue("unclaimtool", ARGPlayerListener.unclaimtool);
    			storevalue("protectchests", ARGPlayerListener.protectchests);
    		} catch (IOException ex) {
    			ex.printStackTrace();
    		}
    	} else {
    		try{
    			FileInputStream in = new FileInputStream(propfile); //Creates the input stream
    			prop.load(in); //loads the file contents of zones ("in" which references to the zones file) from the input stream.
    			if (prop.containsKey("maxchunks")){
    				RegionHandler.maxchunks = Integer.parseInt(prop.getProperty("maxchunks")); //explained below
    			}else{
    				storevalue("maxchunks", RegionHandler.maxchunks);
    			}
    			if (prop.containsKey("timetoexpire")){
    				RegionHandler.timetoexpire = Integer.parseInt(prop.getProperty("timetoexpire")); //explained below
    			}else{
    				storevalue("timetoexpire", RegionHandler.timetoexpire);
    			}
    			if (prop.containsKey("autoclaimdefault")){
    				RegionHandler.autoclaimdefault = Boolean.parseBoolean(prop.getProperty("autoclaimdefault")); //explained below
    			}else{
    				storevalue("autoclaimdefault", RegionHandler.autoclaimdefault);
    			}
    			if (prop.containsKey("claimthreshold")){
    				RegionHandler.claimthreshold = Integer.parseInt(prop.getProperty("claimthreshold")); //explained below
    			}else{
    				storevalue("claimthreshold", RegionHandler.claimthreshold);
    			}
    			if (prop.containsKey("fireprotection")){
    				ARGBlockListener.fireprotection = Boolean.parseBoolean(prop.getProperty("fireprotection"));
    			}else{
    				storevalue("fireprotection", ARGBlockListener.fireprotection);
    			}
    			if (prop.containsKey("infotool")){
    				ARGPlayerListener.infotool = Integer.parseInt(prop.getProperty("infotool"));
    			}else{
    				storevalue("infotool", ARGPlayerListener.infotool);
    			}
    			if (prop.containsKey("unclaimtool")){
    				ARGPlayerListener.unclaimtool = Integer.parseInt(prop.getProperty("unclaimtool"));
    			}else{
    				storevalue("unclaimtool", ARGPlayerListener.unclaimtool);
    			}
    			if (prop.containsKey("protectchests")){
    				ARGPlayerListener.protectchests = Boolean.parseBoolean(prop.getProperty("protectchests"));
    			}else{
    				storevalue("protectchests", ARGPlayerListener.protectchests);
    			}
    			in.close(); //Closes the input stream.
    		}catch (IOException ex){
    			ex.printStackTrace();
    		}
    	}
    }
    
    static void storevalue(String key, Integer value){
    	try {
			FileOutputStream out = new FileOutputStream(propfile);
			prop.put(key, String.valueOf(value));
			prop.store(out, description);
			out.flush();  //Explained below in tutorial
			out.close(); //Closes the output stream as it is not needed anymore.
		} catch (IOException ex) {
			ex.printStackTrace();
		}
    }
    
    static void storevalue(String key, Boolean value){
    	try {
			FileOutputStream out = new FileOutputStream(propfile);
			prop.put(key, String.valueOf(value));
			prop.store(out, description);
			out.flush();  //Explained below in tutorial
			out.close(); //Closes the output stream as it is not needed anymore.
		} catch (IOException ex) {
			ex.printStackTrace();
		}
    }


}
