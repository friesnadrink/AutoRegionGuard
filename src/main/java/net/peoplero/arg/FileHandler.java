package net.peoplero.arg;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class FileHandler {
	
public static Logger log = Logger.getLogger("Minecraft");
	private static String strpath = "plugins" + File.separator + "ARG" + File.separator;
	static File regionsfile = new File(strpath + "Regions.txt");
	
	static DataOutputStream dos;

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
		log.info("Saved "+counter+ " items from " + file);
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



			//		if (regionsfile.exists()){
			//			Properties prop = new Properties();
			//			int counter = 0;
			//			try{
			//				FileInputStream in = new FileInputStream(regionsfile);
			//				prop.load(in);
			//				for (Object key : prop.keySet()){
			//					String[] values = prop.getProperty(key.toString()).split(";");
			//					String playername = values[0];
			//					String strchunk = values[1];
			//					if (OwnedRegions.containsKey(playername) == false){
			//						ArrayList<String> list = new ArrayList<String>();
			//						OwnedRegions.put(playername, list);
			//					}
			//					ArrayList<String> list = OwnedRegions.get(playername);
			//					list.add(strchunk);
			//					OwnedRegions.put(playername, list);
			//					counter = counter + 1;
			//				}
			//				log.info("Loaded "+counter+" regions.");
			//			} catch (Exception ex){
			//			}
			//		}
		}
		log.info("Loaded "+counter+" items from " + file);
		return multiMap;
	}
	
}
