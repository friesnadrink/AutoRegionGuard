package net.peoplero.arg;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class FileHandler {

	public static Logger log = Logger.getLogger("Minecraft");
	ARG plugin;
	static String strpath = "plugins" + File.separator + "ARG" + File.separator;
	static File regionsfile = new File(strpath + "Regions.txt");
	static int RecurringSave = 0;
	static int RecurringExpire = 0;

	static DataOutputStream dos;
	
	FileHandler(ARG plugin) {
		this.plugin = plugin;
	}

	/*
	 * Utility method to write a given text to a file
	 */
	public static boolean writeToFile(String fileName, String dataLine,
			boolean isAppendMode, boolean isNewLine) {
		if (isNewLine) {
			dataLine = dataLine + "\n";
		}

		try {
			File outFile = new File(fileName);
			if (isAppendMode) {
				dos = new DataOutputStream(new FileOutputStream(fileName, true));
			} else {
				dos = new DataOutputStream(new FileOutputStream(outFile));
			}

			dos.writeBytes(dataLine);
			dos.close();
		} catch (FileNotFoundException ex) {
			return (false);
		} catch (IOException ex) {
			return (false);
		}
		return (true);

	}
	
	/*
	 * Reads data from a given file
	 */
	public static ArrayList<String> readFromFile(String fileName) {
		ArrayList<String> lines = new ArrayList<String>();
		try {
			File inFile = new File(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(inFile)));
			String line = null;
			while ((line = br.readLine()) != null){
				lines.add(line);	
			}
			br.close();
		} catch (FileNotFoundException ex) {
			return (null);
		} catch (IOException ex) {
			return (null);
		}
		return (lines);

	}

	public static boolean isFileExists(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}

	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		return file.delete();
	}

	public static void saveMultiMap(Map<String, ArrayList<String>> multiMap, String file){
		String filepath = strpath + file;
		if (isFileExists(filepath)) deleteFile(filepath);
		int counter = 0;
		for (String playername : multiMap.keySet()){
			for (String strvalue : multiMap.get(playername)){
				writeToFile(filepath, playername+";"+strvalue, true, true);
				//System.out.println("saved "+playername+";"+strchunk+".");
				counter = counter + 1;
			}
		}
		log.info("[ARG] Saved "+counter+ " items in " + file);
	}
	
	public static Map<String, ArrayList<String>> loadMultiMap(String file){
		Map<String, ArrayList<String>> multiMap = new HashMap<String, ArrayList<String>>();
		String filepath = strpath + file;
		int counter = 0;
		if (isFileExists(filepath)){
			ArrayList<String> lines = readFromFile(filepath);
			for (String line : lines){
				String[] something = line.split(";");
				if (something.length == 2){
					String playername=something[0].toLowerCase();
					String strvalue=something[1].toLowerCase();
					if (multiMap.containsKey(playername) == false){
						ArrayList<String> list = new ArrayList<String>();
						multiMap.put(playername, list);
					}
					ArrayList<String> list = multiMap.get(playername);
					list.add(strvalue);
					multiMap.put(playername, list);
					//System.out.println("loaded "+playername+";"+strchunk+".");
					counter = counter + 1;
				}
			}
		}
		log.info("[ARG] Loaded "+counter+" items from " + file);
		return multiMap;
	}
	
	public static void saveHashMap(Map<String, Date> hashMap, String file){
		String filepath = strpath + file;
		if (isFileExists(filepath)) deleteFile(filepath);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH");
		int counter = 0;
		for (String playername : hashMap.keySet()){
			String strvalue = sdf.format(hashMap.get(playername));
			writeToFile(filepath, playername + ";" + strvalue, true, true);
			//System.out.println("Saved " + playername + ";" + strvalue);
			counter = counter + 1;
		}
		log.info("[ARG] Saved "+counter+ " items in " + file);
	}
	
	public static Map<String, Date> loadHashMap(String file){
		Map<String, Date> hashMap = new HashMap<String, Date>();
		String filepath = strpath + file;
		int counter = 0;
		if (isFileExists(filepath)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH");
			ArrayList<String> lines = readFromFile(filepath);
			for (String line : lines){
				String[] something = line.split(";");
				if (something.length == 2){
					String playername=something[0].toLowerCase();
					Date d;
					try {
						d = sdf.parse(something[1]);
						hashMap.put(playername, d);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					counter = counter + 1;
				}
			}
		}
		log.info("[ARG] Loaded "+counter+" items from " + file);
		return hashMap;
	}

	public void scheduleTasks() {
		RecurringSave = ARG.Server.getScheduler().scheduleSyncRepeatingTask(ARG.instance, new Runnable() {
            public void run() {
            	plugin.saveAll();
            }
        }, 1800 * 20L, 1800 * 20L);
		RecurringExpire = ARG.Server.getScheduler().scheduleSyncRepeatingTask(ARG.instance, new Runnable() {
			public void run() {
				plugin.RegionHandler.checkLastOnline();
			}
		}, 1800 * 20L, 1800 * 20L);
	}
	
	public void unScheduleTasks() {
		if (RecurringSave != 0){
			ARG.instance.getServer().getScheduler().cancelTask(RecurringSave);
			RecurringSave = 0;
		}
		if (RecurringExpire != 0){
			ARG.instance.getServer().getScheduler().cancelTask(RecurringExpire);
			RecurringExpire = 0;
		}
	}
}
