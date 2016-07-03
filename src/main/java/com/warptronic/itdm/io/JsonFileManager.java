package com.warptronic.itdm.io;

import java.io.FileWriter;
import java.io.IOException;

import javax.json.JsonObject;

/**
 * A class that only handles serialization of Json Objects and I/O
 * @author Sebastian Luca
 *
 */
public class JsonFileManager {
	
	private JsonFileManager() {
		//empty constructor as class only has static methods
	}

	/**
	 * Writes a JSON to file
	 * @param jsonObject {@link JsonObject} to be serialized and written to file
	 * @param filename {@link String} containing the File name (without extension)
	 */
	public static void writeJsonToFile(JsonObject jsonObject, String filename) {
		
		try (FileWriter file = new FileWriter(filename.concat(".json"))) {
			file.write(jsonObject.toString());
			System.out.println("Successfully Copied JSON Object to File...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
