/*
 * Copyright (c) Element 5.
 *
 * Date: 2022-05-19
 */
package com.inputData;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Converting the JSON Object to JAVA data types
 * 
 * @author	dineshkumar.dinakaran@ideas2it.com
 * @version	1.0
 * @since   2022-05-19
 * 
 */
public class TestDatas {
	
	private static TestDatas testDatas; 
	private Reader reader;
	private JsonElement jsonElement;
	
	private TestDatas() {
		
	}
	
	private TestDatas(String path) throws IOException {
		reader = Files.newBufferedReader(Paths
	    		.get(System.getProperty("user.dir")
				+ path));
		jsonElement = JsonParser.parseReader(reader);
	}
	
	private TestDatas(JsonElement jsonElement) throws IOException {
		this.jsonElement = jsonElement;
	}
	
	/**
	 * Getting the instances of the class
	 * 
	 * @return Class Object
	 */
	public static TestDatas getTestDatasInstance(String path) {
		try {
				testDatas = new TestDatas(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return testDatas;
	}
	
	/**
	 * Getting the instances of the class
	 * 
	 * @return Class Object
	 */
	public static TestDatas getTestDatasInstance(JsonElement jsonElement) {
		try {
			testDatas = new TestDatas(jsonElement);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return testDatas;
	}
	
	/**
	 * Getting the Element From JSON with
	 * value as String
	 * 
	 * @param key 
	 * @return value of the key
	 */
	public String getIfValueAsString(String key) {
		try {
		    JsonObject rootObject = jsonElement.getAsJsonObject();
		    return rootObject.get(key).getAsString();
		} catch (NullPointerException e) {
			System.out.println("ERROR - '" + key + "'- Not a Valid JSON key");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getAsJsonObject(List<String> keys) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		JsonArray jsonArray = null;
		String element = "";
		
		try {
			for (int i = 0; i < keys.size(); i++) {
				element = keys.get(i);
				
				if ((jsonObject.has(element))) {
					
					if ((jsonObject.get(element).isJsonArray()) && (null == jsonArray)) {

						jsonArray = jsonObject.getAsJsonArray(element);				
					} else if ((jsonObject.get(keys.get(i)).isJsonArray()) && (null != jsonArray)) {
						
						if (jsonArray.get(0).isJsonObject()) {
							jsonObject = (JsonObject) jsonObject.getAsJsonObject().get(element);
						}
					} else {

						if (jsonObject.get(element).isJsonObject()) {
							jsonObject = jsonObject.getAsJsonObject(element);
						}
					}
				} else if ((null != jsonArray)) {

					if (jsonArray.get(0).isJsonObject()) {
						
						if (jsonArray.get(0).getAsJsonObject().has(element)) {
							jsonObject = jsonArray.get(0).getAsJsonObject();
						}
					}
				}
				else {
					System.out.println("Error - Not a Valid '" + element + "' key");
					//jsonObject = null;
					break;
				}
			}
			
			return jsonObject.has(element) ? jsonObject.get(element).getAsString() 
					                       : jsonObject.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * getting JSON as object 
	 * 
	 * @return value of the key
	 */
	public JsonObject getAsJsonObject() {
		try {
		    JsonObject rootObject = jsonElement.getAsJsonObject();
		    return rootObject;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getAsJsonObject(String keys) {
		return getAsJsonObject(new ArrayList<>(Arrays.asList(keys.split("\\s*,\\s*"))));
	}
}
