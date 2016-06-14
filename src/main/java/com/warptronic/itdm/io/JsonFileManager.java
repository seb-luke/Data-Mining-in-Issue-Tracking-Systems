package com.warptronic.itdm.io;

import java.io.FileWriter;
import java.io.IOException;

import javax.json.JsonObject;

public class JsonFileManager {

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
